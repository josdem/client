/**
 * 
 */
package com.all.client.view.dnd;

import java.awt.Point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.actions.ModelImportAction;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.MediaImportStat.ImportType;

@Service
public class DescriptionPanelDropListener implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { FileSystemValidatorLight.class };

	@Autowired
	private ViewEngine viewEngine;

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		FileSystemValidatorLight fileSystemValidator = draggedObject.get(FileSystemValidatorLight.class);
		TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
		if (target instanceof Playlist || target == null || target instanceof Root) {
			ImportType importType = fileSystemValidator.isFromExternalDevicesPanel() ? ImportType.EXTERNAL_DEVICES : ImportType.SYSTEM_DRAG;
			viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction(target, importType, fileSystemValidator));
		}
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		FileSystemValidatorLight fileSystemValidator = draggedObject.get(FileSystemValidatorLight.class);
		TrackContainer target = viewEngine.get(Model.SELECTED_CONTAINER);
		boolean isFolder = target instanceof Folder;
		return (!(fileSystemValidator.hasError() || fileSystemValidator.hasFolders() || fileSystemValidator.hasPlaylists() || isFolder));
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}
}