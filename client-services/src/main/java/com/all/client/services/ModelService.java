package com.all.client.services;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.ContactRoot;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.Trash;
import com.all.client.services.delegates.DeleteDelegate;
import com.all.client.services.delegates.ImportFilesDelegate;
import com.all.client.services.delegates.MoveDelegate;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.Actions;
import com.all.core.actions.ModelDeleteAction;
import com.all.core.actions.ModelImportAction;
import com.all.core.actions.ModelMoveAction;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.ErrorMessageEvent;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.mc.manager.McManager;
import com.all.messengine.MessageMethod;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Service
public class ModelService {
	private static final Log log = LogFactory.getLog(ModelService.class);

	public static final int MAX_FOLDERS_ALLOWED = 5000;
	public static final int MAX_PLAYLIST_ALLOWED_IN_ROOT = 5000;

	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private ClientReporter reporter;
	@Autowired
	private LocalModelFactory localModelFactory;
	@Autowired
	private LocalModelDao localModelDao;
	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired
	private ImportFilesDelegate importFilesDelegate;
	@Autowired
	private DeleteDelegate deleteDelegate;
	@Autowired
	private MoveDelegate moveDelegate;
	@Autowired
	private ContactCacheService contactCacheService;
	@Autowired(required = false)
	private McManager mcManager;

	@ActionMethod(Actions.Library.MODEL_CREATE_PLAYLIST_ID)
	public void createPlaylist() {
		String playlistBasename = "* Untitled Playlist";
		Root root = controlEngine.get(Model.USER_ROOT);
		TrackContainer selectedItem = controlEngine.get(Model.SELECTED_CONTAINER);

		Playlist playlist = null;

		if (selectedItem instanceof Folder) {
			Folder folderSelected = (Folder) selectedItem;
			int playlistCount = folderSelected.getPlaylists().size();
			if (playlistCount < Folder.MAX_PLAYLIST_ALLOWED) {
				playlist = LocalPlaylist.createUntitledPlaylist(folderSelected.getPlaylists(), playlistBasename);
				localModelDao.save(playlist);
				musicEntityService.moveTo(playlist, folderSelected);
				reporter.logNewPlaylist();
			} else {
				showError("playlist.maxAllowed", "a Folder", Folder.MAX_PLAYLIST_ALLOWED);
			}
		} else {
			int playlistCount = root.size(Playlist.class);
			if (playlistCount < MAX_PLAYLIST_ALLOWED_IN_ROOT) {
				playlist = localModelFactory.createUntitledPlaylist(root);
				root.add(playlist);
				reporter.logNewPlaylist();
			} else {
				showError("playlist.maxAllowed", "Music Library", MAX_PLAYLIST_ALLOWED_IN_ROOT);
			}
		}
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
		controlEngine.fireEvent(Events.Library.PLAYLIST_CREATED, new ValueEvent<Playlist>(playlist));
	}

	private void showError(String errorMessage, Object... parameters) {
		controlEngine.fireEvent(Events.Errors.ERROR_MESSAGE, new ErrorMessageEvent(errorMessage, parameters));
	}

	@ActionMethod(Actions.Library.MODEL_CREATE_FOLDER_ID)
	public void createFolder() {
		Root root = controlEngine.get(Model.USER_ROOT);
		if (root.size(Folder.class) < MAX_FOLDERS_ALLOWED) {
			Folder folder = localModelFactory.createUntitledFolder(root);
			root.add(folder);
			reporter.logNewFolder();
			controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
			controlEngine.fireEvent(Events.Library.FOLDER_CREATED, new ValueEvent<Folder>(folder));
		} else {
			showError("folder.maxAllowed", MAX_FOLDERS_ALLOWED);
		}
	}

	@RequestMethod(Actions.Library.MODEL_FIND_TRACKS_ID)
	public List<Track> findTracks(final List<String> hashcodes) {
		return localModelDao.findTracks(hashcodes);
	}

	@ActionMethod(Actions.Library.MODEL_IMPORT_ID)
	public void importFiles(ModelImportAction action) {
		importFilesDelegate.doImport(controlEngine.get(Model.USER_ROOT), action.getTarget(), action.getImportType(),
				action.getFiles());
	}

	public void move(ModelCollection model, Root root, TrackContainer container) {
		if (root != null && root instanceof ContactRoot) {
			ContactInfo contact = contactCacheService.findContactByEmail(((ContactRoot) root).getOwner().getEmail());
			if (!contact.isOnline()) {
				List<String> trackIds = new ArrayList<String>();
				Collection<Track> allTracks = model.rawTracks();
				for (Track track : allTracks) {
					trackIds.add(track.getHashcode());
				}
				List<String> availableTracks = mcManager.getAvailableTracks(trackIds);
				if (allTracks.size() > availableTracks.size()) {
					McRequestAlert alert = new McRequestAlert(new ContactInfo(controlEngine.get(Model.CURRENT_USER)), contact,
							new Date(), model);
					controlEngine.fireValueEvent(Events.Alerts.CONFIRM_REQUEST_ALERT, alert);
					return;
				}
			}
		}
		moveDelegate.doMove(model, container);
	}

	@ActionMethod(Actions.Library.MODEL_DELETE_ID)
	public void delete(ModelDeleteAction action) {
		Trash trash = controlEngine.get(Model.USER_TRASH);
		Root root = controlEngine.get(Model.USER_ROOT);
		deleteDelegate.doDelete(trash, root, action.getContainer(), action.getModel(), action.getMode());
	}

	public void deleteReference(Track track) {
		Trash trash = controlEngine.get(Model.USER_TRASH);
		trash.addTrack(track);
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));
	}

	@MessageMethod(MessEngineConstants.DELETE_TRACKS_REFERENCES)
	public void deleteReferences(AllMessage<List<Track>> message) {
		List<Track> tracks = message.getBody();
		Trash trash = controlEngine.get(Model.USER_TRASH);
		trash.addTracks(tracks);
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));
	}

	@ActionMethod(Actions.Library.MODEL_MOVE_ID)
	public void moveModel(ModelMoveAction action) {
		Root currentRoot = controlEngine.get(Model.SELECTED_ROOT);
		ModelCollection model = action.getModel();
		TrackContainer container = action.getContainer();
		move(model, currentRoot, container);
	}

	@ActionMethod(Actions.Library.MODEL_REQUEST_RENAME_ID)
	public void rename() {
		controlEngine.fireEvent(Events.Library.EDIT_CURRENT_SELECTION);
	}

	@RequestMethod(Actions.Library.FIND_TRACKS_BY_HASHCODES_ID)
	public List<Track> findTracksByHashcodes(List<String> hashcodes) {
		List<Track> tracks = new ArrayList<Track>();
		for (String hashcode : hashcodes) {
			tracks.add(localModelFactory.findByHashCode(hashcode));
		}
		return tracks;
	}

	/**
	 * Updates a track metadata using the file.
	 * 
	 * @param track
	 *          The track to update.
	 * @param downloadedFile
	 *          the file to read.
	 * 
	 */
	public Track updateDownloadedTrack(String downloadId, File downloadedFile) {
		try {
			Track currentTrack = localModelFactory.findByHashCode(downloadId);
			return musicEntityService.updateDownloadedTrack(currentTrack, downloadedFile);
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));
			controlEngine.fireEvent(Events.Library.NEW_CONTENT_AVAILABLE);
		}
		return null;
	}

}
