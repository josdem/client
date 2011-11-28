package com.all.client.view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.TransferHandler;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.view.model.SingleValueTableModel;

public class Table<T, S extends TableStyle> extends JTable {
	private static final Log LOG = LogFactory.getLog(Table.class);
	public final class DragAndDropMouseListenerHelper extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
				Point p = e.getPoint();
				int row = rowAtPoint(p);
				if (!isRowSelected(row)) {
					selectedRow(row);
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			e.consume();
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
		}
	}

	private static final long serialVersionUID = 1L;
	private boolean init = false;
	protected final SingleValueTableModel<T> model;
	protected final S style;
	protected final TableColumnModel tableColumnModel;
	private Set<T> modifiedRows = new HashSet<T>();
	private static final int ROW_HEIGHT = 20;
	private Integer headerRowHeight = null;
	private Thread monitor = null;
	private int dropRowIndex = -1;
	private Set<T> selectedNodeValues = new HashSet<T>(1);

	public Table(S style) {
		this.style = style;
		this.setShowHorizontalLines(false);
		this.setShowVerticalLines(false);
		this.setRowHeight(ROW_HEIGHT);
		this.tableColumnModel = new DefaultTableColumnModel();
		this.model = new SingleValueTableModel<T>(1);

		super.setModel(model);
		this.setColumnModel(tableColumnModel);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				resetTableHeader();
			}
		});

		tableColumnModel.addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnAdded(TableColumnModelEvent e) {
				model.setColumnCount(tableColumnModel.getColumnCount());
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				resetTableHeader();
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
				model.setColumnCount(tableColumnModel.getColumnCount());
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
				model.setColumnCount(tableColumnModel.getColumnCount());
			}

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
			}
		});
		MouseListener[] mouseListeners = getMouseListeners();
		for (MouseListener l : mouseListeners) {
			this.removeMouseListener(l);
		}
		this.addMouseListener(new DragAndDropMouseListenerHelper());
		for (MouseListener l : mouseListeners) {
			addMouseListener(l);
		}

		init = true;
	}

	@Override
	public void createDefaultColumnsFromModel() {
	}

	public void startMonitor() {
		monitor = new Thread(new Runnable() {
			@Override
			public void run() {
				Thread currentThread = Thread.currentThread();
				while (monitor == currentThread) {
					try {
						updateTable();
						Thread.sleep(500);
					} catch (InterruptedException e) {
						break;
					} catch (Exception e) {
						// The exception was somewhere else
						LOG.error(e, e);
					}
				}
			}

		});
		monitor.setDaemon(true);
		monitor.setName(this.getClass().getName() + "Updater");
		monitor.start();
	}

	public void stopMonitor() {
		monitor = null;
	}

	public S getStyle() {
		return style;
	}

	@Override
	public void setModel(TableModel dataModel) {
		if (init) {
			throw new IllegalArgumentException();
		} else {
			super.setModel(dataModel);
		}
	}

	@Override
	public final void setColumnModel(TableColumnModel columnModel) {
		if (init) {
			throw new IllegalArgumentException();
		} else {
			super.setColumnModel(columnModel);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		int i = 0;
		while (i * getRowHeight() < this.getHeight()) {
			if (isRowSelected(i)) {
				g.setColor(style.getSelectedSeparatorColor());
				g.fillRect(0, (i * getRowHeight()) + getRowHeight() - 1, getWidth(), 1);
				g.setColor(style.getSelectedRowColor());
				g.fillRect(0, i * getRowHeight(), getWidth(), getRowHeight() - 1);
			} else {
				g.setColor(i % 2 == 0 ? style.getOddRowColor() : style.getEvenRowColor());
				g.fillRect(0, i * getRowHeight(), getWidth(), getRowHeight());
			}
			i++;
		}
		TableColumnModel columnModel = getColumnModel();
		Rectangle clip = g.getClipBounds();
		g.setColor(style.getGridColor());
		int x = 0;
		for (i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn column = columnModel.getColumn(i);
			if (column != null) {
				x += column.getWidth();
				if (x >= clip.x && x <= clip.x + clip.width) {
					g.drawLine(x - 1, clip.y, x - 1, clip.y + clip.height);
				}
			}
		}
		setOpaque(false);
		if (dropRowIndex >= 0) {
			g.setColor(Color.BLACK);
			g.drawLine(0, dropRowIndex * getRowHeight(), getWidth(), dropRowIndex * getRowHeight());
			g.drawLine(0, dropRowIndex * getRowHeight() + 1, getWidth(), dropRowIndex * getRowHeight() + 1);
		}
		super.paintComponent(g);
		setOpaque(true);
	}

	public int getDropRowIndex() {
		return dropRowIndex;
	}

	public void setDropRowIndex(int dropRowIndex) {
		if (dropRowIndex > getRowCount()) {
			dropRowIndex = getRowCount();
		}
		if (dropRowIndex != this.dropRowIndex) {
			this.dropRowIndex = dropRowIndex;
			revalidate();
			repaint();
		}
	}

	protected void setDataModel(Iterable<T> content) {
		saveState();
		model.setModelData(content);
		restoreState();
	}

	public void clear() {
		model.setModelData(null);
	}

	public void addRow(T t) {
		model.addRow(t);
	}

	public void removeRow(T t) {
		for (int i = 0; i < getRowCount(); i++) {
			if (model.getValueAt(i, 0) == t) {
				model.removeRow(i);
				break;
			}
		}
	}

	public void updateRow(T modelData) {
		synchronized (this) {
			if (modelData != null) {
				modifiedRows.add(modelData);
			}
		}
	}

	public Set<T> getUpdatedRows() {
		synchronized (this) {
			if (!modifiedRows.isEmpty()) {
				Set<T> modifiedRowSet = this.modifiedRows;
				this.modifiedRows = new HashSet<T>();
				return modifiedRowSet;
			}
			return null;
		}
	}

	public void updateTable() {
		Set<T> modifiedRowSet = getUpdatedRows();
		if (modifiedRowSet != null) {
			for (int row = 0; row < model.getRowCount(); row++) {
				T valueAt = model.getValue(row, 0);
				if (modifiedRowSet.contains(valueAt)) {
					model.fireTableRowUpdated(row);
				}
			}
			revalidate();
		}
	}

	public TableColumn setupColumn(TableColumnStyle<S> downloadColumn) {
		TableColumn column = new TableColumn();
		column.setHeaderValue(downloadColumn.label());
		column.setIdentifier(downloadColumn);
		column.setWidth(downloadColumn.defaultWidth());
		column.setMinWidth(downloadColumn.minWidth());
		column.setMaxWidth(downloadColumn.maxWidth());
		column.setResizable(downloadColumn.resizable());
		column.setCellRenderer(downloadColumn.getRenderer(style));
		column.setModelIndex(downloadColumn.index());
		return column;
	}

	public void setHeaderRowHeight(Integer headerRowHeight) {
		this.headerRowHeight = headerRowHeight;
	}

	public void resetTableHeader() {
		if (headerRowHeight != null) {
			getTableHeader().setPreferredSize(new Dimension(tableColumnModel.getTotalColumnWidth(), headerRowHeight));
		}
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// Force the table to fill the viewport's height
		final Component parent = getParent();
		if (!(parent instanceof JViewport)) {
			return false;
		}
		return ((JViewport) parent).getHeight() > getPreferredSize().height;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// Force the table to fill the viewport's width
		final Component parent = getParent();
		if (!(parent instanceof JViewport)) {
			return false;
		}
		return ((JViewport) parent).getWidth() > getPreferredSize().width;
	}

	@SuppressWarnings("unchecked")
	public T getValue(int row) {
		return (T) getValueAt(row, 0);
	}

	public TableCellInfo<T> getCellData(Point point) {
		int col = columnAtPoint(point);
		int row = rowAtPoint(point);
		int y = point.y - (row * getRowHeight());
		int x = 0;
		for (int i = 0; i < col; i++) {
			x += columnModel.getColumn(i).getWidth();
		}
		x = (point.x - x);
		if (row < 0 || col < 0) {
			return null;
		}
		TableColumn column = columnModel.getColumn(col);
		return new TableCellInfo<T>(row, col, x, y, column.getWidth(), getRowHeight(), getValue(row), column);
	}

	public List<T> getSelectedValues() {
		List<T> list = new ArrayList<T>(getSelectedRowCount());
		if (getSelectedRowCount() > 0) {
			for (int i : getSelectedRows()) {
				list.add(getValue(i));
			}
		}
		return list;
	}

	private void saveState() {
		selectedNodeValues = new HashSet<T>();
		int[] selection = getSelectedRows();
		if (selection != null) {
			for (int x : selection) {
				selectedNodeValues.add(getValue(x));
			}
		}
	}

	private void restoreState() {
		clearSelection();
		for (int x = 0; x < getRowCount(); x++) {
			T nodeValue = getValue(x);
			if (selectedNodeValues.contains(nodeValue)) {
				addRowSelectionInterval(x, x);
			}
		}
	}

	public int getRowIndexAtLocation(Point location) {
		int row = rowAtPoint(location);
		if (row == -1) {
			if (location.y > 0) {
				row = getRowCount();
			} else {
				row = 0;
			}
		}
		return row;
	}

	public void selectedRow(int row) {
		for (int i = 0; i < getSelectedRows().length; i++) {
			if (row == getSelectedRows()[i]) {
				return;
			}
		}
		ListSelectionModel selectionModel = this.getSelectionModel();
		selectionModel.setSelectionInterval(row, row);
	}

	public void updateColumn(int column) {
		model.fireTableColumnChanged(column);
	}

	public void updateAllTable() {
		model.fireTableDataChanged();
	}

	protected void setSortRow(TableColumnStyle<?> column, SortOrder sortOrder) {
		try {
			int columnIndex = 0;
			for (; columnIndex < getColumnCount(); columnIndex++) {
				if (getColumnModel().getColumn(columnIndex).getIdentifier() == column) {
					break;
				}
			}
			List<SortKey> sortkeys = new ArrayList<SortKey>();
			sortkeys.add(new SortKey(columnIndex, sortOrder));
			getRowSorter().setSortKeys(sortkeys);
		} catch (Exception e) {
		}
	}
}
