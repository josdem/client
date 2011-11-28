/**
 * 
 */
package com.all.client.view.dnd;

import java.awt.Point;

public interface DropListener extends DragAndDropListener {

	boolean validateDrop(DraggedObject draggedObject, Point location);

	void doDrop(DraggedObject draggedObject, Point location);
}