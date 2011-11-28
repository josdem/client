/**
 * 
 */
package com.all.client.view.music;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public final class ColumnResizeListener implements MouseListener, TableColumnModelListener {
	private final JTable table;
	private int[] columnWidths;
	private Integer resizingColumnIndex;
	
	public ColumnResizeListener(JTable descriptionTable) {
		this.table = descriptionTable;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		columnWidths = new int[table.getColumnCount()];
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnWidths[i] = columnModel.getColumn(i).getWidth();
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		resizingColumnIndex = null;
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	@Override
	public void columnAdded(TableColumnModelEvent e) {
	}
	
	@Override
	public void columnMarginChanged(ChangeEvent e) {
		if (table.getScrollableTracksViewportWidth()) {
			TableColumn resizingColumn = table.getTableHeader().getResizingColumn();
			if (resizingColumn == null) {
				return;
			}
			if (columnWidths[getResizingColumnIndex()] > resizingColumn.getWidth()) {
				table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			} else {
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			}
		} else {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
		
	}
	
	public Integer getResizingColumnIndex() {
		if (resizingColumnIndex == null) {
			TableColumn resizingColumn = table.getTableHeader().getResizingColumn();
			TableColumnModel columnModel = table.getColumnModel();
			Integer resizingColumnIndex = 0;
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				resizingColumnIndex = i;
				if (resizingColumn == columnModel.getColumn(i)) {
					break;
				}
			}
			this.resizingColumnIndex = resizingColumnIndex;
		}
		return resizingColumnIndex;
	}
	
	@Override
	public void columnMoved(TableColumnModelEvent e) {
	}
	
	@Override
	public void columnRemoved(TableColumnModelEvent e) {
	}
	
	@Override
	public void columnSelectionChanged(ListSelectionEvent e) {
	}
}