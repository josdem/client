/**
 * 
 */
package com.all.client.view.dnd;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;

import javax.swing.SwingUtilities;

import com.all.client.model.DownloadCollection;
import com.all.client.model.Picture;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.model.ContactCollection;
import com.all.i18n.Messages;
import com.all.shared.model.ModelCollection;

public class MainFrameDragOverListener implements DragOverListener {
	private final Window frame;
	private DragTooltipDialog dialog;

	private final Messages messages;

	public MainFrameDragOverListener(Window frame, Messages messages) {
		this.frame = frame;
		this.messages = messages;
	}

	@Override
	public void dragEnter(DraggedObject draggedObject) {
		if (frame instanceof Frame) {
			dialog = new DragTooltipDialog((Frame) frame);
		} else {
			dialog = new DragTooltipDialog(null);
		}
		if (draggedObject.is(FileSystemValidatorLight.class)) {
			FileSystemValidatorLight validator = draggedObject.get(FileSystemValidatorLight.class);
			if (!validator.hasError()) {
				dialog.setContent(validator);
			} else {
				dialog.setContent(messages.getMessage("mainFrameDrop.error.1level"));
			}
		} else if (draggedObject.is(ModelCollection.class)) {
			ModelCollection model = draggedObject.get(ModelCollection.class);
			dialog.setContent(model);
			dialog.setVisible(true);
		} else if (draggedObject.is(ContactCollection.class)) {
			ContactCollection contacts = draggedObject.get(ContactCollection.class);
			dialog.setContent(contacts);
			dialog.setVisible(true);
		} else if (draggedObject.is(DownloadCollection.class)) {
			DownloadCollection downloads = draggedObject.get(DownloadCollection.class);
			dialog.setContent(downloads);
			dialog.setVisible(true);
		} else if (draggedObject.is(Picture.class)) {
			Picture picture = draggedObject.get(Picture.class);
			dialog.setContent(picture);
			dialog.setVisible(true);
		} else if (draggedObject.get() == null) {
			dialog.setContent("");
			dialog.setVisible(true);
		} else {
			dialog.setContent("It is impossible to insert what you dragged. Cause its evil");
		}
		dialog.setVisible(true);
	}

	@Override
	public void dragExit(boolean droppe) {
		if (dialog != null) {
			dialog.setVisible(false);
			dialog.dispose();
			dialog = null;
		}
	}

	@Override
	public void dropOcurred(boolean success) {
		if (dialog != null) {
			dialog.setVisible(false);
			dialog.dispose();
			dialog = null;
		}
	}

	@Override
	public void updateLocation(Point location) {
		if (dialog != null) {
			Point p = (Point) location.clone();
			p.x = p.x + 5;
			SwingUtilities.convertPointToScreen(p, frame);
			dialog.setLocation(p);
		}
	}

	@Override
	public void dragAllowedChanged(boolean allowed) {
		if (dialog != null) {
			dialog.setAllowed(allowed);
		}
	}

	@Override
	public Class<?>[] handledTypes() {
		return null;
	}
}