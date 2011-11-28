/**
 * 
 */
package com.all.client.view.dnd;

import java.awt.Point;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.UIManager;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.DownloadCollection;
import com.all.core.actions.Actions;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.actions.ModelImportAction;
import com.all.core.actions.ModelMoveAction;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.MediaImportStat.ImportType;

public class MyMusicDropListener implements DropListener, DragOverListener {
	private static final Class<?>[] classes = new Class<?>[] { FileSystemValidatorLight.class, ModelCollection.class, DownloadCollection.class };

	private final JLabel arrowIcon;

	private final ViewEngine viewEngine;

	public MyMusicDropListener(ViewEngine viewEngine, JLabel arrowIcon) {
		super();
		this.viewEngine = viewEngine;
		this.arrowIcon = arrowIcon;
	}

	private boolean canBeInside;

	public void doDrop(FileSystemValidatorLight validator, Point location) {
		ImportType importType = validator.isFromExternalDevicesPanel() ? ImportType.EXTERNAL_DEVICES : ImportType.SYSTEM_DRAG;
		Root target = viewEngine.get(Model.USER_ROOT);
		viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction(target, importType, validator));
	}

	public boolean validateDrop(FileSystemValidatorLight validator) {
		canBeInside = validator.canBeInside(viewEngine.get(Model.USER_ROOT));
		return canBeInside;
	}

	@Override
	public void updateLocation(Point location) {
	}

	public void doDrop(ModelCollection model, Point location) {
		TrackContainer root = viewEngine.get(Model.USER_ROOT);
		viewEngine.send(Actions.Library.MODEL_MOVE, new ModelMoveAction(model, root));
	}

	boolean validateDrop(ModelCollection model) {
		List<Playlist> playlists = model.getPlaylists();
		for (Playlist playlist : playlists) {
			if (playlist.getParentFolder() == null && !(model.isRemote())) {
				return false;
			}
		}
		if (model.isRemote() || (model.only(ModelTypes.playlists) && playlists.size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public void dragExit(boolean dropped) {
		arrowIcon.setIcon(UIManager.getDefaults().getIcon("icons.myMusicDndArrow"));
	}

	@Override
	public void dragEnter(DraggedObject draggedObject) {
		if (draggedObject == null) {
			highlightMyLabel();
		} else {
			if (isInternalDropable(draggedObject)) {
				highlightMyLabel();
			}
			if (isExternalDropable(draggedObject)) {
				highlightMyLabel();
			}
		}
	}

	private boolean isInternalDropable(DraggedObject draggedObject) {
		return draggedObject.is(ModelCollection.class, DownloadCollection.class) && validateDrop(draggedObject, null);
	}

	private boolean isExternalDropable(DraggedObject draggedObject) {
		return draggedObject.is(FileSystemValidatorLight.class) && validateDrop(draggedObject, null);
	}

	private void highlightMyLabel() {
		arrowIcon.setIcon(UIManager.getDefaults().getIcon("icons.myMusicDndArrowWhite"));
	}

	@Override
	public void dragAllowedChanged(boolean newStatus) {
	}

	public boolean isDropable(DraggedObject draggedObject) {
		if (draggedObject == null || draggedObject.get() == null) {
			return true;
		}
		if (isInternalDropable(draggedObject)) {
			return true;
		}
		if (isExternalDropable(draggedObject)) {
			return true;
		}
		return false;
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		if(draggedObject.is(DownloadCollection.class)) {
			return true;
		}
		if (draggedObject.is(ModelCollection.class)) {
			return validateDrop(draggedObject.get(ModelCollection.class));
		}
		if (draggedObject.is(FileSystemValidatorLight.class)) {
			return validateDrop(draggedObject.get(FileSystemValidatorLight.class));
		}
		return false;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		if (draggedObject.is(ModelCollection.class)) {
			doDrop(draggedObject.get(ModelCollection.class), location);
		}
		if (draggedObject.is(FileSystemValidatorLight.class)) {
			doDrop(draggedObject.get(FileSystemValidatorLight.class), location);
		}
	}

	@Override
	public void dropOcurred(boolean success) {
	}
}