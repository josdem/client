package com.all.client.view.toolbar.downloads;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.all.client.model.Download;
import com.all.client.view.music.ColumnResizeListener;
import com.all.client.view.music.ExtendedTable;
import com.all.client.view.music.LockableRowSorter;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public class DownloadTable extends ExtendedTable<Download, DownloadTableStyle> implements Internationalizable {
	private static final String DESCRIPTION_TABLE_NAME = "descriptionTable";
	private static final long serialVersionUID = 1L;
	private TrackContainer downloadsTrackContainer;
	private TrackContainer playingPlaylist;
	private Track playingTrack;

	public DownloadTable(Messages messages, TrackContainer downloadsTrackContainer) {
		super(new DownloadTableStyle(messages));
		this.downloadsTrackContainer = downloadsTrackContainer;
		this.setName(DESCRIPTION_TABLE_NAME);

		final DownloadTableHeader tableHeader = new DownloadTableHeader(columnModel);
		this.setTableHeader(tableHeader);
		this.setHeaderRowHeight(getRowHeight());

		for (DownloadTableColumns downloadColumn : DownloadTableColumns.values()) {
			TableColumn column = setupColumn(downloadColumn);
			tableColumnModel.addColumn(column);
		}

		LockableRowSorter<TableModel> rowSorter = new LockableRowSorter<TableModel>(model);
		rowSorter.setMaxSortKeys(1);
		for (DownloadTableColumns downloadColumn : DownloadTableColumns.values()) {
			rowSorter.setComparator(downloadColumn.index(), downloadColumn.comparator(style));
		}
		this.setRowSorter(rowSorter);

		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int width = (30 + (((int) Math.log10(getRowCount())) * 10));
				TableColumn indexCol = getColumn(DownloadTableColumns.INDEX);
				indexCol.setMaxWidth(width);
				indexCol.setMinWidth(width);
				indexCol.setWidth(width);
				indexCol.setPreferredWidth(width);
			}
		});

		ColumnResizeListener columnResizeListener = new ColumnResizeListener(this);
		getTableHeader().addMouseListener(columnResizeListener);
		getColumnModel().addColumnModelListener(columnResizeListener);
	}

	public void setModel(Iterable<Download> content) {
		super.setDataModel(content);
		model.fireTableDataChanged();
	}

	public List<Download> getVisibleDownloads() {
		List<Download> rows = new ArrayList<Download>(getRowCount());
		for (int i = 0; i < getRowCount(); i++) {
			rows.add(getValue(i));
		}
		return rows;
	}

	public void setPlayingTrack(Track playingTrack) {
		this.playingTrack = playingTrack;
	}

	public void setPlayingPlaylist(TrackContainer playingPlaylist) {
		this.playingPlaylist = playingPlaylist;
	}

	public TrackContainer getDownloadsTrackContainer() {
		return downloadsTrackContainer;
	}

	public boolean isPlaying(Download download) {
		if (playingPlaylist == null || playingTrack == null) {
			return false;
		}
		return playingPlaylist == downloadsTrackContainer && download.getTrackId().equals(playingTrack.getHashcode());
	}

	@Override
	public void internationalize(Messages messages) {
		if (tableColumnModel.getColumnCount() > 0) {
			for (DownloadTableColumns column : DownloadTableColumns.values()) {
				if (column.name == null) {
					continue;
				}
				String title = messages.getMessage(column.name);
				int columnIndex = tableColumnModel.getColumnIndex(column);
				tableColumnModel.getColumn(columnIndex).setHeaderValue(title);
			}
			repaint();
		}
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}
}
