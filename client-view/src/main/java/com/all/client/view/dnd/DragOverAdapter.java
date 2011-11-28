package com.all.client.view.dnd;

import java.awt.Point;

public abstract class DragOverAdapter implements DragOverListener {
	@Override
	public void dragEnter(DraggedObject o) {
	}

	@Override
	public void dragExit(boolean dropped) {
	}

	@Override
	public void updateLocation(Point location) {
	}

	@Override
	public void dragAllowedChanged(boolean newStatus) {
	}

	@Override
	public void dropOcurred(boolean success) {
	}
}
