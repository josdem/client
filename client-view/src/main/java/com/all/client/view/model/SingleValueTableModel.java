package com.all.client.view.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class SingleValueTableModel<T> extends AbstractTableModel implements Iterable<T> {
	private static final long serialVersionUID = 1L;
	private int columnCount;
	private List<T> data;

	public SingleValueTableModel(int columnCount) {
		this.columnCount = columnCount;
		this.data = new LinkedList<T>();
	}

	@Override
	public int getColumnCount() {
		return columnCount;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex);
	}

	@SuppressWarnings("unchecked")
	public T getValue(int rowIndex, int columnIndex) {
		return (T) getValueAt(rowIndex, 0);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		setValueAt((T) value, rowIndex);
	}

	public void setValueAt(T value, int rowIndex) {
		data.set(rowIndex, value);
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public synchronized void setModelData(Iterable<T> items) {
		data.clear();
		if (items != null) {
			for (T t : items) {
				data.add(t);
			}
		}
		super.fireTableDataChanged();
	}

	public void removeRow(int i) {
		data.remove(i);
		fireTableRowsDeleted(i, i);
	}

	public void addRow(T row) {
		data.add(row);
		fireTableRowsInserted(data.size() - 1, data.size() - 1);
	}

	public void fireTableCellUpdated(int row, int column) {
		super.fireTableCellUpdated(row, column);
	}

	public void fireTableRowUpdated(int row) {
		super.fireTableRowsUpdated(row, row);
	}

	public void fireTableColumnChanged(int column) {
		for (int i = 0; i < data.size(); i++) {
			super.fireTableCellUpdated(i, column);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return data.iterator();
	}
}
