package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;

import com.all.client.model.DownloadCollection;
import com.all.client.model.DownloadTransferable;

public class DraggedObjectDownloadGenerator extends SimpleDraggedObjectGenerator {

	@Override
	public DraggedObject get(Transferable transferable) {
		try {
			Object draggedObject = (DownloadCollection) transferable.getTransferData(DownloadTransferable.DOWNLOADS_FLAVOR);
			return new SimpleDraggedObject(draggedObject);
		} catch (Exception e) {
			return new SimpleDraggedObject(null);
		}
	}

}
