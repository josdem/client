/**
 * 
 */
package com.all.client.view.toolbar.downloads;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.all.client.model.Download;
import com.all.client.model.DownloadTransferable;

public class DownloadTableTransferHandler extends TransferHandler {

	public DownloadTableTransferHandler() {
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected Transferable createTransferable(JComponent c) {
		return createTransferableFromTable((DownloadTable) c);
	}

	public Transferable createTransferableFromTable(DownloadTable table) {
		int[] selectedRows = table.getSelectedRows();
		List<Download> downloads = new ArrayList<Download>(selectedRows.length);
		for (int i = 0; i < selectedRows.length; i++) {
			Download download = (Download) table.getValueAt(selectedRows[i], 0);
			downloads.add(download);
		}
		return new DownloadTransferable(downloads);
	}

	@Override
	public boolean importData(TransferSupport support) {
		return true;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return false;
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

}