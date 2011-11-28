/**
 * 
 */
package com.all.client.view.music;

import java.awt.Point;

import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.LocalPlaylist;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.core.actions.Actions;
import com.all.core.actions.ModelMoveAction;
import com.all.core.actions.ReorderPlaylistAction;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.TrackContainer;

public final class DescriptionTableModelDropListener implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

	private final DescriptionTable descriptionTable;
	private final ViewEngine viewEngine;

	public DescriptionTableModelDropListener(DescriptionTable descriptionTable, ViewEngine viewEngine) {
		this.descriptionTable = descriptionTable;
		this.viewEngine = viewEngine;
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		boolean result = false;
		if (model.isRemote()) {
			result = model.has(ModelTypes.tracks) && model.only(ModelTypes.tracks);
			if (descriptionTable.getDisplayedPlaylist() instanceof SmartPlaylist) {
				result = result && ((SmartPlaylist) descriptionTable.getDisplayedPlaylist()).dropAllowed();
			}
			return result;
		}
		result = descriptionTable.getDisplayedPlaylist() instanceof Playlist && model.has(ModelTypes.tracks) && model.only(ModelTypes.tracks)
				&& isSortedGood();
		return result;
	}

	private boolean isSortedGood() {
		try {
			SortKey sortKey = descriptionTable.getRowSorter().getSortKeys().get(0);
			return sortKey.getColumn() == 0 && sortKey.getSortOrder() == SortOrder.ASCENDING;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		if (model.isRemote()) {
			// Import
			TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
			viewEngine.send(Actions.Library.MODEL_MOVE, new ModelMoveAction(model, target));
		} else {
			// Reorder rows
			descriptionTable.setDropRowIndex(-1);
			Playlist playlist = (Playlist) descriptionTable.getDisplayedPlaylist();
			int row = descriptionTable.getRowIndexAtLocation(location);
			if (playlist instanceof LocalPlaylist) {
				viewEngine.send(Actions.Library.REORDER_PLAYLIST, new ReorderPlaylistAction((LocalPlaylist) playlist, model.getTracks(), row));
			}
		}
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

}