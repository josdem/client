package com.all.client.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.orm.hibernate3.HibernateSystemException;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.client.data.TrackFactory;
import com.all.client.notifiers.Notifier;
import com.all.client.notifiers.NullNotifier;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.util.FileUtil;
import com.all.client.util.FileUtil.FileCopyObserver;
import com.all.core.events.Events;
import com.all.downloader.download.DownloaderConfig;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.stats.MediaImportStat.ImportType;

@Service
// TODO review all creation and retrieve
public class LocalModelFactory implements Internationalizable {

	private String playlistBaseName = "* Untitled Playlist";
	private String folderBaseName = "* Untitled Folder";

	// public for test
	@Autowired
	public ControlEngine controlEngine;
	@Autowired
	private LocalModelDao dao;
	@Autowired
	private TrackFactory trackFactory;
	@Autowired
	private DownloaderConfig downloaderConfig;
	@Autowired
	private ClientReporter reporter;

	private Log log = LogFactory.getLog(this.getClass());

	@Override
	public void internationalize(Messages messages) {
		playlistBaseName = messages.getMessage("playlist.defaultNewName");
		folderBaseName = messages.getMessage("folder.defaultNewName");
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Autowired
	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public Folder createFolder(Container<List<FileContainer>> folderContainer, Set<File> invalidFiles,
			Notifier observable, boolean copy) throws DeviceFullException {
		LocalFolder folder = new LocalFolder(folderContainer.getName());
		for (FileContainer file : folderContainer.getContent()) {
			Playlist playlist = createPlaylist(file, invalidFiles, folder.getPlaylists(), observable, copy);
			folder.add(playlist);
		}
		dao.save(folder);
		return folder;
	}

	public Playlist createPlaylist(FileContainer playlistContainer, Set<File> invalidFiles, Iterable<Playlist> playlists,
			Notifier observable, boolean copy) throws DeviceFullException {
		String name = playlistContainer.getName();
		LocalPlaylist playlist = null;
		if (name == null) {
			@SuppressWarnings("unchecked")
			LocalPlaylist pl = LocalPlaylist.createUntitledPlaylist(playlists == null ? Collections.EMPTY_LIST : playlists,
					playlistBaseName);
			playlist = pl;
		} else {
			playlist = new LocalPlaylist(name);
		}
		boolean empty = true;
		for (File file : playlistContainer) {
			if (!file.isHidden() && !file.getName().startsWith(".") && file.getName().length() > 0) {
				Track track = null;
				try {
					track = createTrack(file, observable, true, copy);
				} catch (InvalidFileException e) {
					log.warn(e, e);
					invalidFiles.add(e.getFile());
				}
				if (track != null) {
					controlEngine.fireEvent(Events.View.STATUS_BAR_MESSAGE, new ValueEvent<String>(track.getName()));
					playlist.add(track);
					empty = false;
				}
			}
		}
		if (empty) {
			return null;
		}
		dao.save(playlist);
		return playlist;
	}

	public Folder createUntitledFolder(Iterable<Folder> folders) {
		Set<String> names = new HashSet<String>();
		for (Folder f : folders) {
			names.add(f.toString());
		}
		int i = 1;
		String name;
		String zero;
		do {
			if (i <= 9) {
				zero = "0";
			} else {
				zero = "";
			}
			name = folderBaseName + zero + i;
			i++;
		} while (names.contains(name));
		Folder folder = new LocalFolder(name);
		return folder;
	}

	public Folder createUntitledFolder(Root root) {
		return createUntitledFolder(root.getFolders());
	}

	public Playlist createUntitledPlaylist(Iterable<Playlist> playlists) {
		return LocalPlaylist.createUntitledPlaylist(playlists, playlistBaseName);
	}

	public Playlist createUntitledPlaylist(Root root) {
		return createUntitledPlaylist(root.getPlaylists());
	}

	private Track createTrack(TrackFile trackFile, Notifier observable, boolean newContent, boolean copy)
			throws InvalidFileException, DeviceFullException {
		File file = trackFile.getFile();
		Throwable error = null;
		if (!file.exists() || file.isDirectory()) {
			log.warn("track file does not exist or is a directory");
			return null;
		}

		String hashcode = trackFile.getHashcode();

		Track track = trackFactory.createTrack(trackFile);

		try {
			if (track != null) {
				if (copy) {
					File localFile = copyToLocal(trackFile);
					trackFile.setFilename(localFile.getAbsolutePath());
				}
				if (track instanceof LocalTrack) {
					LocalTrack localTrack = (LocalTrack) track;
					localTrack.setDateAdded(new Date());
					localTrack.setNewContent(newContent);
				}
				dao.saveOrUpdate(track);
				dao.saveOrUpdate(trackFile);
				observable.notifyObserver();
			}
		} catch (NullPointerException e) {
			log.error(e, e);
			// TODO refactor TestAudioPlayer.java:41 so this catch is avoided
			// this due to spring not loading the context
		} catch (UncategorizedSQLException e) {
			track = findByHashCode(hashcode);
			error = e;
		} catch (HibernateSystemException e) {
			track = findByHashCode(hashcode);
			error = e;
		} catch (DeviceFullException e) {
			throw e;
		} catch (Exception e) {
			track = findByHashCode(hashcode);
			error = e;
		}
		if (track == null) {
			throw new InvalidFileException(trackFile.getFile(), error);
		}
		return track;
	}

	private File copyToLocal(TrackFile trackFile) throws DeviceFullException {
		File file = null;
		try {
			FileCopyToLocalDeviceFull observer = new FileCopyToLocalDeviceFull();
			file = FileUtil.copy(trackFile.getFile(), new File(downloaderConfig.getCompleteDownloadsPath()), observer);
			if (observer.isDeviceFull()) {
				throw new DeviceFullException();
			}
		} catch (InterruptedException e) {
			return trackFile.getFile();
		}
		return file.exists() ? file : trackFile.getFile();
	}

	public Track findByHashCode(String hashcode) {
		Track track = dao.findByHashcode(hashcode);
		if (track != null) {
			track = (Track) dao.merge(track);
		}
		return track;
	}

	public Track createTrack(File file, boolean newContent, boolean copy) throws InvalidFileException,
			DeviceFullException {
		return createTrack(new TrackFile(file), new NullNotifier(), newContent, copy);
	}

	public Track createTrack(File file, Notifier observable, boolean newContent, boolean copy)
			throws InvalidFileException, DeviceFullException {
		return createTrack(new TrackFile(file), observable, newContent, copy);
	}

	public LocalTrack relocateTrack(Track track) {
		LocalTrack result = dao.findById(LocalTrack.class, track.getHashcode());
		if (result == null) {
			result = dao.findByUrnSha1(track.getHashcode());
		}
		if (result == null) {
			result = new LocalTrack(track.getName(), track.getHashcode());
			result.setAlbum(track.getAlbum());
			result.setArtist(track.getArtist());
			result.setBitRate(track.getBitRate());
			result.setDuration(track.getDuration());
			result.setEnabled(track.isEnabled());
			result.setFileFormat(track.getFileFormat());
			result.setGenre(track.getGenre());
			result.setSampleRate(track.getSampleRate());
			result.setTrackNumber(track.getTrackNumber());
			if (track.isVBR()) {
				result.setVbr();
			}
			result.setYear(track.getYear());
			result.setDownloadString(track.getDownloadString());
			result.setSize(track.getSize());
			result.setNewContent(true);
			dao.save(result);
		} else {
			log.debug("track : " + result.getName() + " magnet : " + result.getDownloadString());
			result.setNewContent(true);
			dao.saveOrUpdate(result);
		}
		return result;
	}

	private Playlist convertPlaylist(Playlist remotePlaylist) {
		LocalPlaylist localPlaylist = new LocalPlaylist();
		localPlaylist.setName(remotePlaylist.getName());
		// TODO when synchronized changes on playlist, need to setHashcode and
		// invoke save or update
		// result.setHashcode(remotePlaylist.getHashcode());
		// dao.saveOrUpdate(result);
		dao.save(localPlaylist);
		List<Track> remoteTracks = remotePlaylist.getTracks();
		List<Track> localTracks = new ArrayList<Track>();
		for (Track track : remoteTracks) {
			localTracks.add(relocateTrack(track));
		}
		localPlaylist.add(localTracks);
		return (Playlist) dao.update(localPlaylist);
	}

	private Folder convertFolder(Folder remoteFolder) {
		LocalFolder localFolder = new LocalFolder(remoteFolder.getName());
		dao.save(localFolder);
		for (Playlist remotePlaylist : remoteFolder.getPlaylists()) {
			localFolder.add(convertPlaylist(remotePlaylist));
		}
		return dao.update(localFolder);
	}

	public ModelCollection doRelocate(ModelCollection remoteModel) {
		long totalTracksBefore = dao.count(LocalTrack.class);
		long totalPlaylistsBefore = dao.count(LocalPlaylist.class);
		long totalFoldersBefore = dao.count(LocalFolder.class);
		ModelCollection result = new ModelCollection();
		for (Track remoteTrack : remoteModel.getTracks()) {
			result.getTracks().add(relocateTrack(remoteTrack));
		}
		for (Folder remoteFolder : remoteModel.getFolders()) {
			result.getFolders().add(convertFolder(remoteFolder));
		}
		for (Playlist remotePlaylist : remoteModel.getPlaylists()) {
			result.getPlaylists().add(convertPlaylist(remotePlaylist));
		}
		ModelSource source = remoteModel.source();
		if (source != null) {
			switch (source.getType()) {
			case TOP_HUNDRED:
				if (!remoteModel.getPlaylists().isEmpty()) {
					log.debug("---- Top hundred stat about to send");
					for (Playlist pl : remoteModel.getPlaylists()) {
						log.debug("			Sending Top Hundred Playlist");
						reporter.logTopHundredDownload(source.getHundredCategory(), pl);
					}
				} else {
					log.debug("			Sending Top Hundred Categiry & Playlist");
					reporter.logTopHundredDownload(source.getHundredCategory(), source.getHundredPlaylist());
				}
			case REMOTE:
				int importedTracks = (int) (dao.count(LocalTrack.class) - totalTracksBefore);
				int importedPlaylists = (int) (dao.count(LocalPlaylist.class) - totalPlaylistsBefore);
				int importedFolders = (int) (dao.count(LocalFolder.class) - totalFoldersBefore);

				log.info(new StringBuilder().append("There were ").append(importedTracks).append(" tracks, ")
						.append(importedPlaylists).append(" playlists and ").append(importedFolders)
						.append(" folders imported from Remote Library.").toString());
				reporter.logImportEvent(ImportType.REMOTE_LIBRARY, importedTracks, importedPlaylists, importedFolders);
				break;
			}
		}
		return result;
	}
}

class FileCopyToLocalDeviceFull implements FileCopyObserver {
	private boolean deviceFull;

	@Override
	public void deviceFull() {
		deviceFull = true;
	}

	@Override
	public void copyProgress(long currentFileBytes, long totalFileBytes) {
	}

	@Override
	public void complete(long totalFileBytes) {
	}

	@Override
	public void checkInterrupt() throws InterruptedException {
	}

	public boolean isDeviceFull() {
		return deviceFull;
	}

}