package com.all.client.view.components;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class EmptyTableRenderer implements TableCellRenderer {

	private final JPanel panel;

	public EmptyTableRenderer() {
		panel = new JPanel();
		panel.setOpaque(false);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		return panel;
	}

}
