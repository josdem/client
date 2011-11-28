package com.all.client.view.dnd;

import java.awt.Point;

public abstract class DragAndDropAdapter extends DragOverAdapter implements DropListener {

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		return false;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
	}

}
