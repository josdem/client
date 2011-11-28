package com.all.client.view.dnd;

import java.awt.Point;

public interface DragOverListener extends DragAndDropListener {
	void updateLocation(Point location);

	void dragEnter(DraggedObject dragObject);

	void dragExit(boolean dropped);

	void dropOcurred(boolean success);

	void dragAllowedChanged(boolean newStatus);
}
