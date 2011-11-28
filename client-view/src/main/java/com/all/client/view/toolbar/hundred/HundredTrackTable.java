package com.all.client.view.toolbar.hundred;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.view.components.Table;
import com.all.client.view.music.ColumnResizeListener;
import com.all.client.view.music.DescriptionTableColumns;
import com.all.client.view.music.DescriptionTableHeader;
import com.all.client.view.music.DescriptionTableStyle;
import com.all.client.view.music.DescriptionTableTransferHandler;
import com.all.client.view.music.LockableRowSorter;
import com.all.core.model.Model;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ModelSourceProvider;
import com.all.shared.model.Track;

public final class HundredTrackTable extends Table<Track, DescriptionTableStyle> implements Internationalizable {

	private static final long serialVersionUID = 1L;
	private List<Track> displayedPlaylist;
	private final ViewEngine viewEngine;

	public HundredTrackTable(List<DescriptionTableColumns> columns, ViewEngine viewEngine,
			ModelSourceProvider sourceProvider) {
		super(new HundredTableStyle());
		this.viewEngine = viewEngine;

		this.setName("descriptionTable");

		final DescriptionTableHeader tableHeader = new DescriptionTableHeader(columnModel);
		this.setTableHeader(tableHeader);
		this.setHeaderRowHeight(getRowHeight());

		for (DescriptionTableColumns col : columns) {
			tableColumnModel.addColumn(setupColumn(col));
		}

		LockableRowSorter<TableModel> rowSorter = new LockableRowSorter<TableModel>(model);
		rowSorter.setMaxSortKeys(1);

		int i = 0;
		for (DescriptionTableColumns column : columns) {
			rowSorter.setComparator(i, column.comparator(style));
			i++;
		}
		this.setRowSorter(rowSorter);

		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int width = (30 + (((int) Math.log10(getRowCount())) * 10));
				TableColumn indexCol = getColumn(DescriptionTableColumns.INDEX);
				indexCol.setMaxWidth(width);
				indexCol.setMinWidth(width);
				indexCol.setWidth(width);
				indexCol.setPreferredWidth(width);
			}
		});

		ColumnResizeListener columnResizeListener = new ColumnResizeListener(this);
		getTableHeader().addMouseListener(columnResizeListener);
		getColumnModel().addColumnModelListener(columnResizeListener);

		this.setDragEnabled(true);
		this.setTransferHandler(new DescriptionTableTransferHandler(viewEngine, sourceProvider));
		this.setDropTarget(null);

	}

	public void setModel(List<Track> tracks) {
		this.displayedPlaylist = tracks;
		super.setDataModel(tracks);
		model.fireTableDataChanged();
	}

	@Override
	public void internationalize(Messages messages) {
		if (tableColumnModel.getColumnCount() > 0) {
			Enumeration<TableColumn> columns = getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tableColumn = (TableColumn) columns.nextElement();
				DescriptionTableColumns column = (DescriptionTableColumns) tableColumn.getIdentifier();
				if (column != null && column.getName() != null) {
					String title = messages.getMessage(column.getName());
					int columnIndex = tableColumnModel.getColumnIndex(column);
					tableColumnModel.getColumn(columnIndex).setHeaderValue(title);
				}
			}
			repaint();
		}
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	public Download getDownload(String hashcode) {
		if (viewEngine == null) {
			return null;
		}
		Map<String, Download> downloads = viewEngine.get(Model.ALL_DOWNLOADS);
		return downloads == null ? null : downloads.get(hashcode);
	}

	public void tryUpdateDownloadComplete(Download value) {
		boolean hasTrack = false;
		for (Track track : model) {
			if (value.getTrackId().equals(track.getHashcode()) || value.getTrackId().equals(track.getDownloadString())) {
				hasTrack = true;
				break;
			}
		}
		if (hasTrack) {
			setModel(getDisplayedPlaylist());
		} else {
			updateAllTable();
		}
	}

	private List<Track> getDisplayedPlaylist() {
		return displayedPlaylist;
	}

	public void updateRow(String trackId) {
		for (Track track : model) {
			if (track.getHashcode().equals(trackId)) {
				updateRow(track);
			}
		}
	}

	public void repaintIndex() {
		model.fireTableColumnChanged(0);
		model.fireTableStructureChanged();
	}

}
