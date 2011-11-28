package com.all.client.view.toolbar.search;

import java.util.Map;

import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.DecoratedSearchData;
import com.all.client.model.Download;
import com.all.client.util.TrackRepository;
import com.all.client.view.components.Table;
import com.all.client.view.music.LockableRowSorter;
import com.all.core.model.Model;

public class P2PSearchTable extends Table<DecoratedSearchData, P2PSearchTableStyle> {
	private static final long serialVersionUID = 1L;
	private ViewEngine viewEngine;

	public P2PSearchTable() {
		super(new P2PSearchTableStyle());
		this.setName("descriptionTable");

		final P2PSearchTableHeader tableHeader = new P2PSearchTableHeader(columnModel);
		this.setTableHeader(tableHeader);
		this.setHeaderRowHeight(getRowHeight());

		for (P2PSearchTableColumns p2pSearchColumn : P2PSearchTableColumns.values()) {
			TableColumn column = setupColumn(p2pSearchColumn);
			tableColumnModel.addColumn(column);
		}

		LockableRowSorter<TableModel> rowSorter = new LockableRowSorter<TableModel>(model);
		rowSorter.setMaxSortKeys(1);
		rowSorter.setSortsOnUpdates(true);
		for (P2PSearchTableColumns p2pSearchColumn : P2PSearchTableColumns.values()) {
			rowSorter.setComparator(p2pSearchColumn.index(), p2pSearchColumn.comparator(style));
		}
		this.setRowSorter(rowSorter);

		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int width = 20;// (15 + (((int) Math.log10(getRowCount())) * 10));
				TableColumn indexCol = getColumn(P2PSearchTableColumns.INDEX);
				indexCol.setMaxWidth(width);
				indexCol.setMinWidth(width);
				indexCol.setWidth(width);
				indexCol.setPreferredWidth(width);
			}
		});
		setSortRow(P2PSearchTableColumns.PEERS, SortOrder.DESCENDING);
	}

	public void setModel(Iterable<DecoratedSearchData> content) {
		super.setDataModel(content);
		model.fireTableDataChanged();
	}

	public void repaintIndex() {
		model.fireTableColumnChanged(0);
	}

	public boolean isTrackAvailable(DecoratedSearchData track) {
		if (viewEngine != null) {
			TrackRepository trackRepository = viewEngine.get(Model.TRACK_REPOSITORY);
			return trackRepository == null ? false : trackRepository.isLocallyAvailable(track.getFileHash());
		}
		return false;
	}

	public Download getDownload(String downloadId) {
		Map<String, Download> downloads = viewEngine.get(Model.ALL_DOWNLOADS);
		return downloads != null ? downloads.get(downloadId) : null;
	}

	public void setViewEngine(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

}
