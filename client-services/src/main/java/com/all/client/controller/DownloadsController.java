package com.all.client.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.DecoratedSearchData;
import com.all.client.model.Download;
import com.all.client.model.LocalModelDao;
import com.all.client.peer.share.ShareService;
import com.all.client.services.ModelService;
import com.all.client.services.MusicEntityService;
import com.all.client.services.delegates.MoveDelegate;
import com.all.client.services.reporting.ClientReporter;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.MoveDownloadsAction;
import com.all.core.common.spring.InitializeService;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.downloader.bean.DownloadStatus;
import com.all.downloader.download.DownloadCompleteEvent;
import com.all.downloader.download.DownloadException;
import com.all.downloader.download.DownloadUpdateEvent;
import com.all.downloader.download.Downloader;
import com.all.downloader.download.DownloaderListener;
import com.all.event.ValueEvent;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.usage.UserActions;

@Controller
public class DownloadsController implements DownloaderListener {

	private final static Log log = LogFactory.getLog(DownloadsController.class);

	public static final int MAX_CONCURRENT_DOWNLOADS = 2;

	@Autowired
	private LocalModelDao dao;
	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired(required = false)
	@Qualifier("downloaderManager")
	private Downloader downloader;
	@Autowired
	private ShareService shareService;
	@Autowired
	private MoveDelegate moveDelegate;
	@Autowired
	private ClientReporter reporter;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private ModelService modelService;

	private final Map<String, Download> downloads = new HashMap<String, Download>();

	private final List<String> downloadPriorities = new CopyOnWriteArrayList<String>();

	private final BlockingQueue<Download> resumedDownloads = new LinkedBlockingQueue<Download>();

	private final DownloadFactory downloadFactory = new DownloadFactory();

	private final List<String> currentDownloads = new CopyOnWriteArrayList<String>();

	private final AtomicBoolean started = new AtomicBoolean(false);

	@InitializeService
	public void start() {
		if (started.get()) {
			throw new IllegalStateException("DownloadsController was already started.");
		}
		downloader.addDownloaderListener(this);
		resetDownloadsInProgress();

		controlEngine.set(Model.DOWNLOADS_SORTED_BY_PRIORITY, getAllDownloadsSortedByPriority(), null);
		controlEngine.set(Model.ALL_DOWNLOADS, downloads, null);
		controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);

		started.set(true);
		log.info("DownloadsController has started succesfully.");
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void stop() {
		if (started.compareAndSet(true, false)) {
			saveDownloadsInDB();
			downloader.removeDownloaderListener(this);
			downloads.clear();
		}
	}

	@RequestMethod(Actions.Downloads.REQUEST_ADD_TRACK_ID)
	public Download add(Track track) {
		return add(moveDelegate.relocate(track), true);
	}

	@MessageMethod(MessEngineConstants.ADD_DOWNLOAD_TRACK)
	public void add(AllMessage<Track> allMessage) {
		add(allMessage.getBody(), true);
	}

	private void add(TrackContainer container) {
		for (Track track : container.getTracks()) {
			add(track);
		}
	}

	@ActionMethod(Actions.Downloads.ADD_MODEL_COLLECTION_ID)
	public void add(ModelCollection downloadTracks) {
		if (downloadTracks.isRemote()) {
			downloadTracks = moveDelegate.doMove(downloadTracks, controlEngine.get(Model.USER_ROOT));
		}
		addTrackList(downloadTracks.getTracks());
		for (Playlist playlist : downloadTracks.getPlaylists()) {
			add(playlist);
		}
		for (Folder folder : downloadTracks.getFolders()) {
			add(folder);
		}
	}

	@ActionMethod(Actions.Downloads.ADD_SEARCH_DATA_ID)
	public Download addSearchData(ValueAction<DecoratedSearchData> valueAction) {
		DecoratedSearchData searchData = valueAction.getValue();
		if (searchData == null) {
			log.warn("search data is null");
			return null;
		}
		reporter.logUserAction(UserActions.Downloads.DOWNLOAD_SEARCH_RESULT);
		return add(moveDelegate.relocate(searchData.toTrack()));
	}

	private void addTrackList(List<Track> tracks) {
		List<Track> tracksToDownload = new ArrayList<Track>(tracks);
		Iterator<Track> iterator = tracksToDownload.iterator();
		while (iterator.hasNext()) {
			Track track = iterator.next();
			if (musicEntityService.isFileAvailable(track)) {
				iterator.remove();
			}
		}
		for (Track track : tracksToDownload) {
			add(track, false);
		}
		controlEngine.fireEvent(Events.Downloads.ALL_MODIFIED);
	}

	@ActionMethod(Actions.Downloads.PAUSE_ID)
	public void pause(ValueAction<Collection<Download>> downloadsValue) {
		Collection<Download> downloads = downloadsValue.getValue();
		for (Download download : downloads) {
			if (download.getStatus() == DownloadState.Downloading) {
				try {
					downloader.pause(download.getDownloadId());
					currentDownloads.remove(download.getDownloadId());
					controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
				} catch (Exception e) {
					log.error(e, e);
				}
			}
		}
		startNextDownload();
		log.debug("finish pause downloads");
	}

	@ActionMethod(Actions.Downloads.RESUME_ID)
	public void resume(ValueAction<Collection<Download>> downloadsValue) {
		Collection<Download> downloads = downloadsValue.getValue();
		for (Download download : downloads) {
			DownloadState status = download.getStatus();
			if (status == DownloadState.Paused) {
				try {
					if (currentDownloads.size() < MAX_CONCURRENT_DOWNLOADS) {
						downloader.resume(download.getDownloadId());
						currentDownloads.add(download.getDownloadId());
						controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
					} else {
						resumedDownloads.offer(download);
						download.setStatus(DownloadState.Queued);
						updateDownload(download);
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			} else if (status == DownloadState.Error || status == DownloadState.MoreSourcesNeeded) {
				download.setStatus(DownloadState.Queued);
				updateDownload(download);
				startNextDownload();
			}
		}
		log.debug("finish resume downloads");

	}

	@MessageMethod(MessEngineConstants.DELETE_DOWNLOAD_COLLECTION)
	public void deleteDownloadCollection(AllMessage<List<Download>> allMessage) {
		doDeleteDownloadCollection(allMessage.getBody());
	}

	@ActionMethod(Actions.Downloads.DELETE_DOWNLOAD_COLLECTION_ID)
	public void deleteDownloadCollection(ValueAction<List<Download>> valueAction) {
		doDeleteDownloadCollection(valueAction.getValue());
	}

	@MessageMethod(MessEngineConstants.DELETE_DOWNLOAD_COLLECTION)
	public void deleteDownloadByTrackReference(AllMessage<Track> allMessage) {
		Track track = allMessage.getBody();
		Download download = downloads.get(track.getHashcode());
		if (download != null) {
			removeDownload(download, true);
		}
	}

	@MessageMethod(MessEngineConstants.DELETE_DOWNLOAD_TRACK)
	public void deleteDownload(AllMessage<List<String>> allMessage) {
		List<String> downloadIdList = allMessage.getBody();
		Collection<Download> downloadsTodelete = new ArrayList<Download>(downloadIdList.size());
		for (String downloadId : downloadIdList) {
			Download download = downloads.get(downloadId);
			if (download != null) {
				downloadsTodelete.add(download);
			}
		}
		doDeleteDownloadCollection(downloadsTodelete);
	}

	@ActionMethod(Actions.Downloads.CLEAN_UP_ID)
	public void cleanUp() {
		for (Download download : new ArrayList<Download>(downloads.values())) {
			if (download.getStatus() == DownloadState.Complete) {
				removeDownload(download, false);
			}
		}
		notifyAndRearangeEverything();
	}

	@ActionMethod(Actions.Downloads.MOVE_ID)
	public void moveDownloads(MoveDownloadsAction moveDownloadsAction) {
		List<Download> movedDownloads = moveDownloadsAction.getMovedDownloads();
		int row = moveDownloadsAction.getRow();

		List<String> newPriorities = new ArrayList<String>();
		List<Download> previousOrder = getAllDownloadsSortedByPriority();
		previousOrder.removeAll(movedDownloads);
		int i = 0;
		for (Download downloadA : previousOrder) {
			if (i == row) {
				for (Download downloadB : movedDownloads) {
					newPriorities.add(downloadB.getDownloadId());
				}
			}
			newPriorities.add(downloadA.getDownloadId());
			i++;
		}
		if (newPriorities.size() < downloadPriorities.size()) {
			for (Download downloadB : movedDownloads) {
				newPriorities.add(downloadB.getDownloadId());
			}
		}
		downloadPriorities.clear();
		downloadPriorities.addAll(newPriorities);
		controlEngine.set(Model.DOWNLOADS_SORTED_BY_PRIORITY, getAllDownloadsSortedByPriority(), null);
		reassignDownloadPriorities();
		controlEngine.fireEvent(Events.Downloads.ALL_MODIFIED);
	}

	@Override
	public void onDownloadCompleted(DownloadCompleteEvent downloadCompleteEvent) {
		String downloadId = downloadCompleteEvent.getDownloadId();
		File file = downloadCompleteEvent.getDestinationFile();

		Download currentDownload = downloads.get(downloadId);
		if (currentDownload != null) {
			currentDownload.complete();
			currentDownloads.remove(downloadId);
			controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
			startNextDownload();
		}
		Track track = modelService.updateDownloadedTrack(downloadId, file);
		if (currentDownload != null) {
			currentDownload.updateTrackInfo(track);
		}
		shareService.run();
		completeDownload(currentDownload);
		Sound.DOWNLOAD_FINISHED.play();
	}

	@Override
	public void onDownloadUpdated(DownloadUpdateEvent downloadUpdateEvent) {
		String id = downloadUpdateEvent.getDownloadId();
		DownloadStatus status = downloadUpdateEvent.getDownloadStatus();
		Download currentDownload = downloads.get(id);
		if (currentDownload == null) {
			return;
		}
		if (status.getState() != currentDownload.getStatus()
				&& (!(status.getState() == DownloadState.Paused && currentDownload.getStatus() == DownloadState.Queued))) {
			log.info("Download " + id + " changed from " + currentDownload.getStatus() + " to  " + status.getState());
			currentDownload.setStatus(status.getState());
		}
		currentDownload.setProgress(status.getProgress());
		currentDownload.setRate(status.getDownloadRate());
		currentDownload.setRemainingSeconds(status.getRemainingSeconds());
		currentDownload.setFreeNodes(status.getFreeNodes());
		currentDownload.setBusyNodes(status.getBusyNodes());
		currentDownload.checkResourcesAvailable();

		updateDownload(currentDownload);

		switch (status.getState()) {
		case Error:
		case MoreSourcesNeeded:
			log.info("removeCurrentDownload: " + currentDownload.getDownloadId() + " : " + status.getState());
			currentDownloads.remove(currentDownload.getDownloadId());
			controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
			startNextDownload();
		}
	}

	private void doDeleteDownloadCollection(Collection<Download> downloads) {
		for (Download download : downloads) {
			removeDownload(download, false);
		}
		notifyAndRearangeEverything();
	}

	private Download add(Track track, boolean notify) {
		if (!musicEntityService.isFileAvailable(track)) {
			log.debug("Track Name: " + track.getName());
			log.debug("Track MagnetLink is: " + track.getDownloadString());
			Download download = downloadFactory.createDownload(track);
			if (downloads.containsKey(download.getDownloadId())) {
				return download;
			}
		    return add(download, notify);
		}
		else{
			controlEngine.fireEvent(Events.Downloads.TRACK_ALREADY_AVAILABLE);
			return null;
		}
	}

	private List<Download> getAllDownloadsSortedByPriority() {
		List<Download> allDownloads = new ArrayList<Download>();
		for (String downloadId : downloadPriorities) {
			allDownloads.add(downloads.get(downloadId));
		}
		return allDownloads;
	}

	private void notifyAndRearangeEverything() {
		reassignDownloadPriorities();
		controlEngine.fireEvent(Events.Downloads.ALL_MODIFIED);
		startNextDownload();
	}

	private void completeDownload(Download download) {
		if (downloads.containsKey(download.getDownloadId())) {
			dao.update(download);
			controlEngine.fireEvent(Events.Downloads.COMPLETED, new ValueEvent<Download>(download));
		}
	}

	private void updateDownload(Download download) {
		if (downloads.containsKey(download.getDownloadId())) {
			dao.saveOrUpdate(download);
			controlEngine.fireEvent(Events.Downloads.UPDATED, new ValueEvent<Download>(download));
		}
	}

	private Download add(Download download, boolean notify) {
		download.setPriority(downloads.size());
		downloads.put(download.getDownloadId(), download);
		controlEngine.set(Model.ALL_DOWNLOADS, downloads, null);
		downloadPriorities.add(download.getDownloadId());
		controlEngine.set(Model.DOWNLOADS_SORTED_BY_PRIORITY, getAllDownloadsSortedByPriority(), null);
		startNextDownload();
		dao.saveOrUpdate(download);
		if (notify) {
			controlEngine.fireEvent(Events.Downloads.ADDED, new ValueEvent<Download>(download));
		}
		return download;
	}

	private void startNextDownload() {
		while (currentDownloads.size() < MAX_CONCURRENT_DOWNLOADS && !resumedDownloads.isEmpty()) {
			Download resumedDownload = resumedDownloads.poll();
			try {
				downloader.resume(resumedDownload.getDownloadId());
				currentDownloads.add(resumedDownload.getDownloadId());
				controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
			} catch (DownloadException e) {
				log.error(e, e);
			}
		}
		for (String downloadId : new ArrayList<String>(downloadPriorities)) {
			if (currentDownloads.size() < MAX_CONCURRENT_DOWNLOADS) {
				Download nextDownload = downloads.get(downloadId);
				DownloadState currentState = nextDownload.getStatus();
				if (currentState == DownloadState.Queued && !nextDownload.isStarted()) {
					try {
						downloader.download(downloadId);
						nextDownload.setStarted(true);
						currentDownloads.add(downloadId);
						controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
					} catch (DownloadException e) {
						log.error(e, e);
					}
				}
			} else {
				break;
			}
		}
	}

	private Download removeDownload(Download download, boolean notify) {
		download = downloads.remove(download.getDownloadId());
		if (download != null) {
			downloadPriorities.remove(download.getDownloadId());
			if (notify) {
				reassignDownloadPriorities();
			}
			controlEngine.set(Model.DOWNLOADS_SORTED_BY_PRIORITY, getAllDownloadsSortedByPriority(), null);
			cancelDownload(download, notify);
			dao.delete(download);
			deleteFile(download.getDownloadFile());
			if (notify) {
				controlEngine.fireEvent(Events.Downloads.REMOVED, new ValueEvent<Download>(download));
			}
		}
		return download;
	}

	private void cancelDownload(Download downloadInfo, boolean notify) {
		if (currentDownloads.contains(downloadInfo.getDownloadId()) || resumedDownloads.contains(downloadInfo)
				|| downloadInfo.getStatus() == DownloadState.Paused) {
			try {
				downloader.delete(downloadInfo.getDownloadId());
				downloadInfo.setStatus(DownloadState.Canceled);
			} catch (DownloadException e) {
				log.error(e, e);
			}
		}
		currentDownloads.remove(downloadInfo.getDownloadId());
		controlEngine.set(Model.CURRENT_DOWNLOAD_IDS, currentDownloads, null);
		if (notify) {
			startNextDownload();
		}
	}

	private void reassignDownloadPriorities() {
		int i = 0;
		for (String downloadId : downloadPriorities) {
			Download download = downloads.get(downloadId);
			if (download != null) {
				download.setPriority(i);
				i++;
			}
		}
	}

	private void deleteFile(File file) {
		if (file != null && file.exists()) {
			file.delete();
		}
	}

	private void resetDownloadsInProgress() {
		List<Download> downloadsList = dao.findAll(Download.class);

		for (Download download : downloadsList) {
			if (download.getStatus() == DownloadState.Downloading || download.getStatus() == DownloadState.MoreSourcesNeeded) {
				log.debug(download.getDisplayName() + " status: " + download.getStatus());
				download.setStatus(DownloadState.Queued);
			}
			this.add(download, false);
		}
		controlEngine.fireEvent(Events.Downloads.ALL_MODIFIED);
	}

	private void saveDownloadsInDB() {
		for (Download currentDownload : new ArrayList<Download>(downloads.values())) {
			dao.saveOrUpdate(currentDownload);
		}
	}

	class DownloadFactory {
		public Download createDownload(Track track) {
			return new Download(track);
		}
	}

}
