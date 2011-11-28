/**
 * 
 */
package com.all.client.view.music;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;

final class SpanerColumnTableListener extends ComponentAdapter implements TableColumnModelListener {

	private final TableColumn spannerColumn;
	private final JTable table;

	public SpanerColumnTableListener(TableColumn spannerColumn, JTable table) {
		this.spannerColumn = spannerColumn;
		this.table = table;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		recalculate();
	}

	private void recalculate() {
		// Force the table to fill the viewport's width
		Component parent = table.getParent();
		int width = 0;
		if (parent != null) {
			width = (parent.getWidth() - table.getWidth()) + spannerColumn.getWidth();
			if (width < 0) {
				width = 0;
			}
		}
		spannerColumn.setWidth(width);
		spannerColumn.setMinWidth(width);
		spannerColumn.setMaxWidth(width);
	}

	@Override
	public void columnAdded(TableColumnModelEvent e) {
		recalculate();
	}

	@Override
	public void columnMarginChanged(ChangeEvent e) {
		recalculate();
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		recalculate();
	}

	@Override
	public void columnRemoved(TableColumnModelEvent e) {
		recalculate();
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
		recalculate();
	}
}