package com.all.client.view.components;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public abstract class FilteredRenderer<T> implements TableCellRenderer, CellFilter<T> {
	private final TableCellRenderer renderer;

	public FilteredRenderer(TableCellRenderer renderer) {
		this.renderer = renderer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		return renderer.getTableCellRendererComponent(table, filter((T) value, row, column), isSelected, hasFocus, row, column);
	}
}