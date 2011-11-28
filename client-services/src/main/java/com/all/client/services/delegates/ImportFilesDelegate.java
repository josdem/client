package com.all.client.services.delegates;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.Container;
import com.all.client.model.DeviceFullException;
import com.all.client.model.FileContainer;
import com.all.client.model.FileSystemValidator;
import com.all.client.model.InvalidFileException;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.notifiers.Notifier;
import com.all.client.peer.share.ShareService;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.sync.SyncMessages;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.core.events.ImportProgressEvent;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.MediaImportStat.ImportType;

@Service
public class ImportFilesDelegate {
	private static final Log log = LogFactory.getLog(ImportFilesDelegate.class);

	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private LocalModelFactory modelFactory;
	@Autowired
	private LocalModelDao dao;
	@Autowired
	private ShareService shareService;
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ClientReporter reporter;

	private void importFolders(List<Container<List<FileContainer>>> folders, Root root, ImportResult result, ImportType importType)
			throws DeviceFullException {
		if (folders.isEmpty()) {
			return;
		}
		log.debug("-----------------------Import Folders-------------------------");
		for (Container<List<FileContainer>> file : folders) {
			root.add(modelFactory.createFolder(file, result.getInvalidFilesSet(), result, importType == ImportType.EXTERNAL_DEVICES));
		}
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(root));
	}

	private void importPlaylists(List<FileContainer> playlists, Root root, ImportResult result, ImportType importType) throws DeviceFullException {
		if (playlists.isEmpty()) {
			return;
		}
		log.debug("-----------------------Import Playlists-------------------------");
		for (FileContainer file : playlists) {
			Playlist playlist = modelFactory.createPlaylist(file, result.getInvalidFilesSet(), root.getPlaylists(), result,
					importType == ImportType.EXTERNAL_DEVICES);
			root.add(playlist);
			dao.update(playlist);
		}
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(root));
	}

	private void importPlaylists(List<FileContainer> playlists, Folder folder, ImportResult result, ImportType importType, Root root)
			throws DeviceFullException {
		if (playlists.isEmpty()) {
			return;
		}
		log.debug("-----------------------Import Playlists-------------------------");
		for (FileContainer file : playlists) {
			Playlist playlist = modelFactory.createPlaylist(file, result.getInvalidFilesSet(), folder.getPlaylists(), result,
					importType == ImportType.EXTERNAL_DEVICES);
			((LocalFolder) folder).add(playlist);
			dao.update(playlist);
			dao.update(folder);
		}
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(folder));
	}

	private void importTracks(List<File> tracks, Root root, ImportResult result, ImportType importType) throws DeviceFullException {
		if (tracks.isEmpty()) {
			return;
		}
		log.debug("-----------------------Import Tracks in Root-------------------------");
		for (File file : tracks) {
			try {
				modelFactory.createTrack(file, result, true, importType == ImportType.EXTERNAL_DEVICES);
			} catch (InvalidFileException e) {
				result.addInvalid(e.getFile());
			}
			controlEngine.fireEvent(Events.View.STATUS_BAR_MESSAGE, new ValueEvent<String>(file.getName()));
		}
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(root));
	}

	private void importTracks(List<File> tracks, Folder folder, FileSystemValidator validator, Root root, String playlistBaseName, ImportResult result,
			ImportType importType) throws DeviceFullException {
		if (tracks.isEmpty()) {
			return;
		}
		log.debug("-----------------------Import Tracks in Folder-------------------------");
		Playlist untitledPlaylist = LocalPlaylist.createUntitledPlaylist(folder.getPlaylists(), playlistBaseName + " ");
		((LocalFolder) folder).add(untitledPlaylist);
		importTracks(validator.getTracks(), untitledPlaylist, result, importType);
		// TODO: verify if not a update of folder is enough
		dao.update(untitledPlaylist);
		dao.merge(folder);
		// modelNotifier.notifyContainerTracksChanged(folder);
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
	}

	private void importTracks(List<File> tracks, Playlist playlist, ImportResult result, ImportType importType) throws DeviceFullException {
		if (tracks.isEmpty()) {
			return;
		}
		log.debug("-----------------------Import Tracks in Playlist-------------------------");
		for (File file : tracks) {
			controlEngine.fireEvent(Events.View.STATUS_BAR_MESSAGE, new ValueEvent<String>(file.getName()));
			try {
				Track track = modelFactory.createTrack(file, result, true, importType == ImportType.EXTERNAL_DEVICES);
				dao.update(track);
				((LocalPlaylist) playlist).add(track);
			} catch (InvalidFileException e) {
				result.addInvalid(e.getFile());
			}
		}
		dao.saveOrUpdate(playlist);
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(playlist));
	}

	public void doImport(Root root, TrackContainer target, ImportType importType, FileSystemValidatorLight files) {
		log.debug("-----------------------Import - Validation-------------------------");
		if (target instanceof SmartPlaylist && !((SmartPlaylist) target).dropAllowed()) {
			throw new IllegalArgumentException("Illegal target");
		}
		FileSystemValidator validator = new FileSystemValidator(files);

		if (validator.hasError()) {
			throw new IllegalArgumentException("Validator has errors");
		}
		if (!validator.canBeInside(target)) {
			throw new IllegalArgumentException("Validator not valid for target");
		}

		log.debug("-----------------------Import - SETUP-------------------------");
		messEngine.send(new AllMessage<String>(MessEngineConstants.PAUSE_SYNC, SyncMessages.IMPORT_LIBRARY));
		final ImportResult importResult = newImportResult(dao, validator.getTrackCount());
		importResult.setNotificationResponder(new Notifier() {
			int percent = 0;

			@Override
			public void notifyObserver() {
				int percent = importResult.getPercent();
				if (this.percent != percent) {
					controlEngine.fireEvent(Events.Library.IMPORT_PROGRESS, new ImportProgressEvent("Importing...", percent));
					this.percent = percent;
				}
			}
		});

		log.info(new StringBuilder().append("There are ").append(importResult.getTotalTracksBefore()).append(" tracks, ")
				.append(importResult.getTotalPlaylistsBefore()).append(" playlists and ").append(importResult.getTotalFoldersBefore())
				.append(" folders before import from ").append(importType).toString());
		// TODO: add operation to the event to know when importing
		try {
			if (target instanceof Playlist) {
				Playlist playlist = (Playlist) target;
				if (playlist.isSmartPlaylist()) {
					importFolders(validator.getFolders(), root, importResult, importType);
					importPlaylists(validator.getPlaylists(), root, importResult, importType);
					importTracks(validator.getTracks(), root, importResult, importType);
				} else {
					importTracks(validator.getTracks(), playlist, importResult, importType);
					dao.update(playlist);
					controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(playlist));
				}
			} else if (target instanceof Folder) {
				Folder folder = (LocalFolder) target;
				importPlaylists(validator.getPlaylists(), folder, importResult, importType, root);
				String playlistBaseName = "* Untitled Playlist";
				importTracks(validator.getTracks(), folder, validator, root, playlistBaseName, importResult, importType);
			} else if (target instanceof Root || target instanceof SmartPlaylist) {
				importFolders(validator.getFolders(), root, importResult, importType);
				importPlaylists(validator.getPlaylists(), root, importResult, importType);
				importTracks(validator.getTracks(), root, importResult, importType);
				controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(root));
			}
		} catch (DeviceFullException e) {
			log.error(e, e);
			controlEngine.fireEvent(Events.Errors.DEVICE_FULL);
		} catch (Exception e) {
			log.error(e, e);
			controlEngine.fireValueEvent(Events.Errors.EXCEPTION, e);
		} finally {
			AllMessage<String> message = new AllMessage<String>(MessEngineConstants.RESUME_SYNC, SyncMessages.IMPORT_LIBRARY);
			messEngine.send(message);
		}

		log.debug("-----------------------Import - DONE-------------------------");

		dao.flush();

		shareService.run();

		for (File f : importResult.getInvalidFiles()) {
			validator.getErrorMessages().add(f);
		}

		if (!validator.getErrorMessages().isEmpty()) {
			controlEngine.fireValueEvent(Events.Errors.MODEL_IMPORT_INVALID_FILES, validator.getErrorMessages());
		}
		controlEngine.fireEvent(Events.Library.IMPORT_COMPLETED);
		controlEngine.fireEvent(Events.Library.NEW_CONTENT_AVAILABLE);

		setNewTracksToImportResult(importResult, dao);

		log.info(new StringBuilder().append("There were ").append(importResult.getImportedTracks()).append(" tracks, ")
				.append(importResult.getImportedPlaylists()).append(" playlists and ").append(importResult.getImportedFolders())
				.append(" folders imported from ").append(importType.toString().toLowerCase()).toString());

		reporter.logImportEvent(importType, importResult.getImportedTracks(), importResult.getImportedPlaylists(), importResult.getImportedFolders());
		// Suggest Garbage Collection to mitigate a potential memory leak
		// That can be monitored using JConsole after importing
		System.gc();
	}

	private void setNewTracksToImportResult(ImportResult importResult, LocalModelDao dao2) {
		importResult.setAfterImportedTracks(dao.count(LocalTrack.class));
		importResult.setAfterImportedPlaylists(dao.count(LocalPlaylist.class));
		importResult.setAfterImportedFolders(dao.count(LocalFolder.class));
	}

	private ImportResult newImportResult(LocalModelDao dao, long totalCount) {
		long totalTracksBefore = dao.count(LocalTrack.class);
		long totalPlaylistsBefore = dao.count(LocalPlaylist.class);
		long totalFoldersBefore = dao.count(LocalFolder.class);
		return new ImportResult(totalTracksBefore, totalPlaylistsBefore, totalFoldersBefore, totalCount);
	}

}
