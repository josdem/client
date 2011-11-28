package com.all.client.view.music;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.core.model.Model;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.EmptyTrackContainer;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public class DescriptionTable extends ExtendedTable<Track, DescriptionTableStyle> implements Internationalizable {
	private static final long serialVersionUID = 1L;
	private boolean indexVisible;
	private Track playingTrack;
	private TrackContainer playingPlaylist;
	private TrackContainer displayedPlaylist;
	private int indexWidth;
	private final ViewEngine viewEngine;

	public DescriptionTable(ViewEngine viewEngine) {
		super(new DescriptionTableStyle());
		this.viewEngine = viewEngine;
		this.setName("descriptionTable");

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.setTableHeader(new DescriptionTableHeader(tableColumnModel));
		this.setHeaderRowHeight(getRowHeight());

		this.setDragEnabled(true);
		this.setTransferHandler(new DescriptionTableTransferHandler(viewEngine));
		this.setDropTarget(null);

		for (DescriptionTableColumns col : DescriptionTableColumns.values()) {
			tableColumnModel.addColumn(setupColumn(col));
		}
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				setModelToStyle(displayedPlaylist.getTracks());
				setIndexWidth(30 + (((int) Math.log10(getRowCount())) * 10));
			}
		});
		onVisibleRowsChanged().add(new Observer<ObservValue<List<Track>>>() {
			@Override
			public void observe(ObservValue<List<Track>> t) {
				setIndexWidth(30 + (((int) Math.log10(getRowCount())) * 10));
			}
		});
		this.setIndexVisible(false);
		LockableRowSorter<TableModel> rowSorter = new LockableRowSorter<TableModel>(model);
		rowSorter.setMaxSortKeys(1);
		for (int i = 0; i < model.getColumnCount(); i++) {
			DescriptionTableColumns column = (DescriptionTableColumns) tableColumnModel.getColumn(i).getIdentifier();
			rowSorter.setComparator(i, column.comparator(style));
		}
		this.setRowSorter(rowSorter);

		ColumnResizeListener columnResizeListener = new ColumnResizeListener(this);
		getTableHeader().addMouseListener(columnResizeListener);
		getColumnModel().addColumnModelListener(columnResizeListener);

	}

	private void autoScrollToPlayingTrack() {
		Container parent = getParent();
		if (parent instanceof JViewport) {
			JViewport viewport = (JViewport) parent;
			int row = 0;
			for (int i = 0; i < getRowCount(); i++) {
				if (getValue(i).equals(playingTrack)) {
					row = i;
					break;
				}
			}
			Rectangle rowRect = getCellRect(row, 0, true);
			Rectangle viewRect = viewport.getViewRect();
			int x = rowRect.x;
			int y = rowRect.y;
			y = y > 0 ? y : 0;
			Rectangle newViewRect = new Rectangle(x, y, viewRect.width, viewRect.height);
			if (!newViewRect.equals(viewRect)) {
				viewport.scrollRectToVisible(newViewRect);
			}
		}
	}

	protected void doPostNotitifyVisibleRowsChanged() {
		autoScrollToPlayingTrack();
	}

	public void setModel(TrackContainer trackContainer) {
		setIndexVisible(trackContainer instanceof Playlist);
		this.displayedPlaylist = trackContainer;
		Iterable<Track> tracks = trackContainer.getTracks();
		super.setDataModel(tracks);
		setModelToStyle(tracks);
	}

	private void setModelToStyle(Iterable<Track> tracks) {
		if (!visibleRowChangedIsEnabled()) {
			return;
		}
		style.setTracks(tracks);
	}

	public boolean isVisible(DescriptionTableColumns col) {
		TableColumn column = getColumn(col);
		return !(column.getWidth() < 0 || column.getMaxWidth() == 0);
	}

	public void setVisible(DescriptionTableColumns col, boolean visible) {
		if (DescriptionTableColumns.INDEX == col || DescriptionTableColumns.NAME == col || col == null) {
			return;
		}
		if (!visible && col == getSortColumn()) {
			setSortRow(DescriptionTableColumns.NAME, SortOrder.ASCENDING);
		}
		TableColumn column = getColumn(col);
		if (visible) {
			column.setResizable(col.resizable());
			column.setMaxWidth(col.maxWidth());
			column.setMinWidth(col.minWidth());
			column.setWidth(col.defaultWidth());
		} else {
			column.setWidth(0);
			column.setMinWidth(0);
			column.setMaxWidth(0);
			column.setResizable(false);
		}
	}

	public void setIndexWidth(int indexWidth) {
		this.indexWidth = indexWidth;
		TableColumn indexCol = getColumn(DescriptionTableColumns.INDEX);
		int width = indexVisible ? indexWidth : 20;
		indexCol.setMaxWidth(width);
		indexCol.setMinWidth(width);
		indexCol.setWidth(width);
		indexCol.setPreferredWidth(width);
	}

	private final void setIndexVisible(boolean indexVisible) {
		this.indexVisible = indexVisible;
		setIndexWidth(indexWidth);
	}

	public boolean isIndexVisible() {
		return indexVisible;
	}

	public void setPlayingTrack(Track playingTrack) {
		updateRow(this.playingTrack);
		updateRow(playingTrack);
		this.playingTrack = playingTrack;
		updateTable();
		repaint();
	}

	public Track getPlayingTrack() {
		return playingTrack;
	}

	public TrackContainer getPlayingPlaylist() {
		return playingPlaylist;
	}

	public void setPlayingPlaylist(TrackContainer playingPlaylist) {
		this.playingPlaylist = playingPlaylist;
	}

	public TrackContainer getDisplayedPlaylist() {
		if (displayedPlaylist == null) {
			return EmptyTrackContainer.INSTANCE;
		}
		return displayedPlaylist;
	}

	private DescriptionTableColumns getSortColumn() {
		try {
			int sortColumn = getRowSorter().getSortKeys().get(0).getColumn();
			return (DescriptionTableColumns) getColumnModel().getColumn(sortColumn).getIdentifier();
		} catch (Exception e) {
			return null;
		}
	}

	public Download getDownload(String hashcode) {
		if(viewEngine == null) {
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

	@Override
	public void internationalize(Messages messages) {
		if (tableColumnModel.getColumnCount() > 0) {
			for (DescriptionTableColumns column : DescriptionTableColumns.values()) {
				if (column.getName() == null) {
					continue;
				}
				String title = messages.getMessage(column.getName());
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
