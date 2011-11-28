package com.all.client.view.music;

import java.awt.Point;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.core.actions.Actions;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.actions.ModelImportAction;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.MediaImportStat.ImportType;

public final class DescriptionTableFileSystemDropListener implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { FileSystemValidatorLight.class };

	private final DescriptionTable descriptionTable;

	private final ViewEngine viewEngine;

	public DescriptionTableFileSystemDropListener(DescriptionTable descriptionTable, ViewEngine viewEngine) {
		super();
		this.descriptionTable = descriptionTable;
		this.viewEngine = viewEngine;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		FileSystemValidatorLight fileSystemValidator = draggedObject.get(FileSystemValidatorLight.class);
		descriptionTable.setDropRowIndex(-1);
		TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
		ImportType importType = fileSystemValidator.isFromExternalDevicesPanel() ? ImportType.EXTERNAL_DEVICES : ImportType.SYSTEM_DRAG;
		if (target instanceof Playlist || target == null || target instanceof Root || target instanceof Folder) {
			viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction(target, importType, fileSystemValidator));
		} else if (target instanceof SmartPlaylist) {
			SmartPlaylist smartPlaylist = (SmartPlaylist) target;
			if (smartPlaylist.dropAllowed()) {
				Root root = viewEngine.get(Model.USER_ROOT);
				viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction(root, importType, fileSystemValidator));
			}
		}
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		FileSystemValidatorLight fileSystemValidator = draggedObject.get(FileSystemValidatorLight.class);
		TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
		if (target instanceof SmartPlaylist) {
			SmartPlaylist smartPlaylist = (SmartPlaylist) target;
			if (smartPlaylist.dropAllowed()) {
				return true;
			}
			return false;
		}
		return (!(fileSystemValidator.hasError() || fileSystemValidator.hasFolders() || fileSystemValidator.hasPlaylists()));
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

}