package com.all.client.view.toolbar.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;

public class P2PSearchTableHeader extends JTableHeader {
	private static final long serialVersionUID = 1L;

	public P2PSearchTableHeader(TableColumnModel columnModel) {
		super(columnModel);
		this.setReorderingAllowed(false);
		this.setDefaultRenderer(new P2PSearchTableHeaderCellRenderer());
	}
}

class P2PSearchTableHeaderCellRenderer implements TableCellRenderer {
	private static final String CONTAINER_NAME = "tableHeader";
	private static final String CONTAINER_SELECTED_NAME = "tableSearchSelectedHeader";
	private final JLabel title;
	private final JLabel arrow;
	private final JPanel panel;

	public P2PSearchTableHeaderCellRenderer(){
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		title = new JLabel();
		arrow = new JLabel();

		title.setHorizontalAlignment(JLabel.LEFT);
		title.setName(SynthFonts.BOLD_FONT11_BLACK);

		panel.add(title, BorderLayout.CENTER);
		panel.add(arrow, BorderLayout.EAST);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		title.setText("  " + value.toString());
		SortOrder sortOrder = SortOrder.UNSORTED;
		if (table.getRowSorter() != null && table.getRowSorter().getSortKeys() != null) {
			List<? extends SortKey> sortKeys = table.getRowSorter().getSortKeys();
			for (SortKey sortKey : sortKeys) {
				if (sortKey.getColumn() == column) {
					sortOrder = sortKey.getSortOrder();
					break;
				}
			}
		}
		switch (sortOrder) {
		case ASCENDING:
			panel.setName(CONTAINER_SELECTED_NAME);
			arrow.setIcon(SynthIcons.SORT_ASCENDING_ICON);
			title.setName(SynthFonts.BOLD_FONT11_WHITE);
			break;
		case DESCENDING:
			panel.setName(CONTAINER_SELECTED_NAME);
			arrow.setIcon(SynthIcons.SORT_DESCENDING_ICON);
			title.setName(SynthFonts.BOLD_FONT11_WHITE);
			break;
		default:
			panel.setName(CONTAINER_NAME);
			arrow.setIcon(SynthIcons.SORT_NATURAL_ICON);
			title.setName(SynthFonts.BOLD_FONT11_GRAY77_77_77);
			break;
		}
		return panel;

	}
}
