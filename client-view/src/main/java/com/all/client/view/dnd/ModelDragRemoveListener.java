package com.all.client.view.dnd;

import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;
import com.all.shared.model.RemotePlaylist;
import com.all.shared.model.Track;

public class ModelDragRemoveListener implements DragRemoveListener {
	private final ModelCollection model;

	public ModelDragRemoveListener(ModelCollection model) {
		this.model = model;
	}

	@Override
	public void remove(Object parent, Object child) {
		if (parent != null) {
			if (parent instanceof RemoteFolder) {
				((RemoteFolder) parent).remove((Playlist) child);
			}
			if (parent instanceof RemotePlaylist) {
				((RemotePlaylist) parent).removeTrack((Track) child);
			}
		} else {
			if (child instanceof Track) {
				model.getTracks().remove(child);
			}
			if (child instanceof Playlist) {
				model.getPlaylists().remove(child);
			}
			if (child instanceof Folder) {
				model.getFolders().remove(child);
			}
		}
	}
}
