/**
 * 
 */
package com.all.client.view.dnd;

import java.awt.Point;

import javax.swing.JToggleButton;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;

public class DownloadButtonDropListener implements DropListener, DragOverListener {
	private static final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

	private final JToggleButton button;
	private boolean lastEnabled = false;
	private boolean res;

	private final ViewEngine viewEngine;

	// private Log log = LogFactory.getLog(this.getClass());

	public DownloadButtonDropListener(JToggleButton button, ViewEngine viewEngine) {
		this.button = button;
		this.viewEngine = viewEngine;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		viewEngine.sendValueAction(Actions.Downloads.ADD_MODEL_COLLECTION, model);
		button.setEnabled(true);
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		if (model == null) {
			return false;
		}
		if (model.hasAny(ModelTypes.folders, ModelTypes.playlists)) {
			return false;
		}
		return true;
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

	@Override
	public void dragAllowedChanged(boolean newStatus) {
		res = newStatus;
	}

	@Override
	public void dragEnter(DraggedObject dragObject) {
	}

	@Override
	public void dragExit(boolean dropped) {
		button.setEnabled(lastEnabled);
	}

	@Override
	public void dropOcurred(boolean success) {
	}

	@Override
	public void updateLocation(Point location) {
		button.setEnabled(!res);
	}
}