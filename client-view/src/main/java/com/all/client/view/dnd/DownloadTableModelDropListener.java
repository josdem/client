package com.all.client.view.dnd;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.view.toolbar.downloads.DownloadTable;
import com.all.core.actions.Actions;
import com.all.core.actions.MoveDownloadsAction;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Track;

public class DownloadTableModelDropListener extends DragOverAdapter implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

	private final DownloadTable downloadTable;
	private final ViewEngine viewEngine;

	public DownloadTableModelDropListener(DownloadTable downloadTable, ViewEngine viewEngine) {
		this.downloadTable = downloadTable;
		this.viewEngine = viewEngine;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		Map<String, Download> allDownloads = viewEngine.get(Model.ALL_DOWNLOADS);
		ModelCollection model = draggedObject.get(ModelCollection.class);
		downloadTable.setDropRowIndex(-1);
		List<Download> downloads = new ArrayList<Download>();
		for (Track track : model.getTracks()) {
			downloads.add(allDownloads.get(track.getHashcode()));
		}
		int row = downloadTable.getRowIndexAtLocation(location);
		viewEngine.send(Actions.Downloads.MOVE, new MoveDownloadsAction(downloads, row));
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		return model.has(ModelTypes.tracks) && model.only(ModelTypes.tracks) && isSortedGood();
	}

	private boolean isSortedGood() {
		try {
			SortKey sortKey = downloadTable.getRowSorter().getSortKeys().get(0);
			return sortKey.getColumn() == 0 && sortKey.getSortOrder() == SortOrder.ASCENDING;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void dragExit(boolean dropped) {
		downloadTable.setDropRowIndex(-1);
	}

	@Override
	public void dropOcurred(boolean success) {
	}

	@Override
	public void updateLocation(Point location) {
		int row = downloadTable.getRowIndexAtLocation(location);
		downloadTable.setDropRowIndex(row);
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

}
