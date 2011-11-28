package com.all.client.view.music;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.all.core.common.view.SynthFonts;

public class DescriptionTableRenderer implements TableCellRenderer {
	private JLabel label;

	public DescriptionTableRenderer(int horizontalAlignment) {
		label = new JLabel();
		label.setName(SynthFonts.PLAIN_FONT11_BLACK);
		label.setOpaque(false);
		label.setHorizontalAlignment(horizontalAlignment);
		if (horizontalAlignment == JLabel.LEFT) {
			label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		}
		if (horizontalAlignment == JLabel.RIGHT) {
			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		}
		if (horizontalAlignment == JLabel.CENTER) {
			label.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		label.setName(isSelected ? SynthFonts.BOLD_FONT11_BLACK : SynthFonts.PLAIN_FONT11_BLACK);
		label.setText(value.toString());
		return label;
	}

}