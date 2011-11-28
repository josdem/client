package com.all.client.services.delegates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.Trash;
import com.all.client.services.MusicEntityService;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.ModelDeleteAction.DeleteMode;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Component
public class DeleteDelegate {
	private static final Log log = LogFactory.getLog(DeleteDelegate.class);

	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private MessEngine messEngine;

	protected void deleteFolders(DeleteMode mode, ModelCollection model, Trash trash, Set<Track> tracks) {
		if (model.empty(ModelTypes.folders)) {
			return;
		}
		switch (mode) {
		case ONLY_REFERENCES:
			trash.addFolders(model.getFolders());
			break;
		case REF_AND_FILES:
			for (Folder folder : model.getFolders()) {
				for (Track track : folder.getTracks()) {
					tracks.add(track);
				}
				trash.addFolderWithReferences(folder);
			}
			break;
		default:
			break;
		}
	}

	protected Folder deletePlayLists(DeleteMode mode, Folder nodeToBeSelected, ModelCollection model, Trash trash,
			Set<Track> tracks) {
		Folder folderToBeSelected = nodeToBeSelected;
		if (model.empty(ModelTypes.playlists)) {
			return folderToBeSelected;
		}
		switch (mode) {
		case ONLY_REFERENCES:
			if (model.getPlaylists().size() > 0) {
				folderToBeSelected = model.getPlaylists().get(0).getParentFolder();
			}
			trash.addPlayLists(model.getPlaylists());
			break;
		case REF_AND_FILES:
			for (Playlist playlist : model.getPlaylists()) {
				folderToBeSelected = playlist.getParentFolder();
				tracks.addAll(playlist.getTracks());
				trash.addPlayLists(model.getPlaylists());
			}
			break;
		default:
			break;
		}
		return folderToBeSelected;
	}
	
	protected void deleteTracks(DeleteMode mode, Collection<Track> tracks, TrackContainer trackContainer, Trash trash, boolean selectAllMusic) {
		if (tracks.isEmpty()) {
			return;
		}
		switch (mode) {
		case ONLY_REFERENCES:
			if (trackContainer instanceof LocalPlaylist) {
				LocalPlaylist playlist = (LocalPlaylist) trackContainer;
				for (Track track : tracks) {
					musicEntityService.removeFrom(track, playlist);
				}
			}
			break;
		case REF_AND_FILES:
			for (Track track : tracks) {
				trash.addTrack(track);
				if (trackContainer instanceof LocalPlaylist && !selectAllMusic) {
					musicEntityService.removeFrom(track, (LocalPlaylist) trackContainer);
				}
				musicEntityService.deleteFile(track);
			}
			break;
		default:
			break;
		}
	}

	public void doDelete(Trash trash, Root root, TrackContainer trackContainer, ModelCollection model, DeleteMode mode) {
		Folder nodeToBeSelected = null;
		boolean isDeleted = false;
		boolean selectAllMusic = false;
		try {
			Sound.LIBRARY_DELETE_NODE.play();
			Set<Track> tracksToDelete = new HashSet<Track>();

			if (model.getPlaylists().size() > 0) {
				nodeToBeSelected = deletePlayLists(mode, nodeToBeSelected, model, trash, tracksToDelete);
				selectAllMusic = nodeToBeSelected == null;
			}
			
			if (model.getFolders().size() > 0) {
				nodeToBeSelected = null;
				selectAllMusic = true;
			}
			
			deleteFolders(mode, model, trash, tracksToDelete);
			tracksToDelete.addAll(model.getTracks());
			deleteTracks(mode, tracksToDelete, trackContainer, trash, selectAllMusic);
			
			if (mode == DeleteMode.REF_AND_FILES && !tracksToDelete.isEmpty()) {
				Sound.TRACK_DELETE.play();
				sendDeleteFilesMessage(tracksToDelete);
			}
			isDeleted = true;
		} catch (Exception e) {
			log.error(e, e);
		}
		if (isDeleted) {
			controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(root));
			controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));
		}
		if (nodeToBeSelected != null || selectAllMusic) {
			Object nodeValue = isDeleted ? nodeToBeSelected : trackContainer;
			controlEngine.fireEvent(Events.Library.RESTORE_SELECTION, new ValueEvent<Object>(nodeValue));
		}
	}

	private void sendDeleteFilesMessage(Set<Track> tracksToDelete) {
		List<String> hashcodes = new ArrayList<String>();
		for (Track track : tracksToDelete) {
			hashcodes.add(track.getHashcode());
		}
		messEngine.send(new AllMessage<List<String>>(MessEngineConstants.VALIDATE_STOP_PLAYER, hashcodes));
		messEngine.send(new AllMessage<List<String>>(MessEngineConstants.DELETE_DOWNLOAD_TRACK, hashcodes));
	}
}