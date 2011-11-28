/**
 * 
 */
package com.all.client.view.dnd;

import java.awt.Point;

import javax.swing.JToggleButton;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.DeleteFlow;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;

public class TrashDnDListener implements DropListener, DragOverListener {
	private static final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

	private JToggleButton trashButton;

	private final DialogFactory dialogFactory;

	private final ViewEngine viewEngine;

	public TrashDnDListener(JToggleButton trashButton, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super();
		this.trashButton = trashButton;
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		if (model == null || model.isEmpty()) {
			return;
		}
		new DeleteFlow(viewEngine, dialogFactory).delete(model);
		trashButton.setName("trashButton");
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		if (model == null || model.isEmpty()) {
			return false;
		}
		if (!model.only(ModelTypes.playlists, ModelTypes.folders, ModelTypes.tracks)) {
			return false;
		}
		trashButton.setName("trashButtonOver");
		return true;
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

	@Override
	public void updateLocation(Point location) {
	}

	@Override
	public void dragExit(boolean dropped) {
		trashButton.setName("trashButton");
	}

	@Override
	public void dropOcurred(boolean success) {
	}

	@Override
	public void dragEnter(DraggedObject dragObject) {
	}

	@Override
	public void dragAllowedChanged(boolean newStatus) {
	}
}