package com.all.client.view.music;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;

public class DescriptionTableHeader extends JTableHeader {
	private static final long serialVersionUID = 1L;

	public DescriptionTableHeader(TableColumnModel columnModel) {
		super(columnModel);
		this.setReorderingAllowed(true);
		this.setDefaultRenderer(new DescriptionTableHeaderCellRenderer());
	}

	@Override
	public void updateUI() {
		setUI(new DescriptionTableHeaderUI());
	}
}

class DescriptionTableHeaderUI extends BasicTableHeaderUI {
	private boolean is1and2Swapped = true;
	private int offset = 0;

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler((DescriptionTableHeader) header);
	}

	class MouseInputHandler extends BasicTableHeaderUI.MouseInputHandler {
		protected DescriptionTableHeader header;

		public MouseInputHandler(DescriptionTableHeader header) {
			this.header = header;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			Enumeration<TableColumn> columns = header.getColumnModel().getColumns();
			int totalX = 0;
			while (columns.hasMoreElements()) {
				TableColumn column = columns.nextElement();
				if (e.getX() > totalX && e.getX() < totalX + column.getWidth()) {
					offset = e.getX() - totalX;
					return;
				}
				totalX += column.getWidth();
			}
		}

		public void mouseDragged(MouseEvent e) {
			TableColumn draggedColumn = ((DescriptionTableHeader) e.getSource()).getDraggedColumn();
			if (draggedColumn == null) {
				super.mouseDragged(e);
				return;
			}
			int draggedColumnIndex = draggedColumn.getModelIndex();
			int destination = header.getColumnModel().getColumnIndexAtX(e.getPoint().x - offset);
			if (draggedColumnIndex > 1 && destination > 1) {
				super.mouseDragged(e);
				is1and2Swapped = true;
			} else {
				is1and2Swapped = false;
			}
		}

		public void mouseReleased(MouseEvent e) {
			TableColumn draggedColumn = ((DescriptionTableHeader) e.getSource()).getDraggedColumn();
			if (draggedColumn == null) {
				super.mouseReleased(e);
				return;
			}
			int draggedColumnIndex = draggedColumn.getModelIndex();
			Point p = e.getPoint();
			TableColumnModel columnModel = header.getColumnModel();
			if (p.x < 0) {
				p.x = 0;
			}
			int index = columnModel.getColumnIndexAtX(p.x - offset);
			super.mouseReleased(e);
			if ((index == 0 || index == 1) && is1and2Swapped) {
				header.getColumnModel().moveColumn(index, draggedColumnIndex);
			}
		}
	}

}

class DescriptionTableHeaderCellRenderer implements TableCellRenderer {
	private static final String CONTAINER_NAME = "tableHeader";
	private static final String CONTAINER_SORT_NAME = "tableSortedHeader";
	private static final Insets ICON_INSETS = new Insets(0, 0, 0, 3);
	private static final Insets LABEL_INSETS = new Insets(0, 4, 0, 3);
	/**
	 * 
	 */
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();
	private JLabel iconLabel = new JLabel();

	public DescriptionTableHeaderCellRenderer() {
		label.setName(SynthFonts.BOLD_FONT11_BLACK);
		GridBagConstraints iconConstraints = new GridBagConstraints();
		GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridx = 0;
		labelConstraints.gridy = 0;
		labelConstraints.fill = GridBagConstraints.HORIZONTAL;
		labelConstraints.weightx = 1.0;
		labelConstraints.insets = LABEL_INSETS;
		iconConstraints.gridx = 1;
		iconConstraints.gridy = 0;
		iconConstraints.insets = ICON_INSETS;
		panel.setLayout(new GridBagLayout());
		panel.add(label, labelConstraints);
		panel.add(iconLabel, iconConstraints);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Icon sortIcon = getIcon(table, column);
		iconLabel.setIcon(sortIcon);
		label.setText(value.toString());
		label.setName(SynthFonts.BOLD_FONT11_BLACK);

		setLabelAlignment(value.toString(), label);

		if (sortIcon != null) {
			label.setName(SynthFonts.BOLD_FONT11_WHITE);
			panel.setName(CONTAINER_SORT_NAME);
			iconLabel.setVisible(true);
		} else {
			panel.setName(CONTAINER_NAME);
			iconLabel.setVisible(false);
		}
		return panel;
	}

	private void setLabelAlignment(String columnName, JLabel label) {
		label.setHorizontalAlignment(JLabel.LEFT);
		if (columnName.equals("Plays") || columnName.equals("Time") || columnName.equals("Bitrate")
				|| columnName.equals("Size") || columnName.equals("Skips")) {
			label.setHorizontalAlignment(JLabel.RIGHT);
		}
		if (columnName.equals("Rating")) {
			label.setHorizontalAlignment(JLabel.CENTER);
		}
	}

	private Icon getIcon(JTable table, int column) {
		// DescriptionTable dTable = (DescriptionTable) table;
		if (table == null || table.getRowSorter() == null) {
			return UIManager.getIcon("Table.naturalSortIcon");
		}
		Icon sortIcon = null;

		List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
		if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() == table.convertColumnIndexToModel(column)) {
			switch (sortKeys.get(0).getSortOrder()) {
			case ASCENDING:
				sortIcon = SynthIcons.SORT_ASCENDING_ICON;
				break;
			case DESCENDING:
				sortIcon = SynthIcons.SORT_DESCENDING_ICON;
				break;
			case UNSORTED:
				sortIcon = SynthIcons.SORT_NATURAL_ICON;
				break;
			default:
				throw new AssertionError("Cannot happen");
			}
		}

		return sortIcon;
	}
}