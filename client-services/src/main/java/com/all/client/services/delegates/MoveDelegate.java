package com.all.client.services.delegates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;
import com.all.client.model.LocalPlaylist;
import com.all.client.services.MusicEntityService;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.ErrorMessageEvent;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Service
public class MoveDelegate {
	private static final String PLAYLIST_DEFAULT_NEW_NAME = "playlist.defaultNewName";

	@Autowired
	private LocalModelDao modelDao;
	@Autowired
	private LocalModelFactory modelFactory;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private MusicEntityService musicEntityService;
	@Autowired
	private Messages messages;

	public ModelCollection doMove(ModelCollection content, TrackContainer destination) {
		if (!(destination instanceof Root) && !(destination instanceof Playlist) && !(destination instanceof Folder)
				&& (destination instanceof SmartPlaylist && !((SmartPlaylist) destination).dropAllowed())) {
			throw new RuntimeException("Invalid destination " + destination.getClass().getName());
		}
		String playlistBasename = messages.getMessage(PLAYLIST_DEFAULT_NEW_NAME);
		Root destinationRoot = controlEngine.get(Model.USER_ROOT);
		if (content.isRemote()) {
			content = modelFactory.doRelocate(content);
		}
		if (destination instanceof Folder) {
			if (content.only(ModelTypes.playlists, ModelTypes.tracks)) {
				for (final Playlist playlist : content.getPlaylists()) {
					Folder parentFolder = (Folder) destination;
					if (parentFolder.getPlaylists().size() >= Folder.MAX_PLAYLIST_ALLOWED) {
						controlEngine.fireEvent(Events.Errors.ERROR_MESSAGE, new ErrorMessageEvent("playlist.maxAllowed", "a Folder", ""
								+ Folder.MAX_PLAYLIST_ALLOWED));
						controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(destinationRoot));
						return content;
					}
					((LocalFolder) parentFolder).add(playlist);
				}
				if (content.has(ModelTypes.tracks)) {

					Playlist playlist = LocalPlaylist.createUntitledPlaylist(((LocalFolder) destination).getPlaylists(), playlistBasename);
					((LocalFolder) destination).add(playlist);
					for (Track track : content.getTracks()) {
						((LocalPlaylist) playlist).add(track);
					}
					modelDao.update(playlist);
				}
				modelDao.update(((Folder) destination));
				controlEngine.fireEvent(Events.Library.FOLDER_UPDATED, new ValueEvent<Folder>((Folder) destination));
			}
		} else if (destination instanceof Playlist) {
			if (content.only(ModelTypes.tracks)) {
				for (Track track : content.getTracks()) {
					((LocalPlaylist) destination).add(track);
				}
				modelDao.update(((Playlist) destination));
			}
		} else {
			if (content.only(ModelTypes.playlists)) {
				for (Playlist playlist : content.getPlaylists()) {
					musicEntityService.moveTo(playlist, null);
				}
			}
		}
		controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(destinationRoot));
		// controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(null));
		controlEngine.fireEvent(Events.Library.CONTAINER_MODIFIED, new ContainerModifiedEvent(destination));
		return content;
	}

	public Track relocate(Track track) {
		return modelFactory.relocateTrack(track);
	}
}
