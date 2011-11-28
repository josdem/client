/**
 * 
 */
package com.all.client.view.music;

import java.awt.Point;

import com.all.client.view.dnd.DragOverAdapter;

public final class DescriptionTableDragOverListener extends DragOverAdapter {
	private final DescriptionTable table;

	public DescriptionTableDragOverListener(DescriptionTable table) {
		this.table = table;
	}

	private boolean dragValid;

	@Override
	public void updateLocation(Point location) {
		if (dragValid) {
			table.setDropRowIndex(table.getRowIndexAtLocation(location));
		}
	}

	@Override
	public void dragExit(boolean dropped) {
		table.setDropRowIndex(-1);
	}

	@Override
	public void dragAllowedChanged(boolean dragValid) {
		this.dragValid = dragValid;
	}

	@Override
	public Class<?>[] handledTypes() {
		return null;
	}
}