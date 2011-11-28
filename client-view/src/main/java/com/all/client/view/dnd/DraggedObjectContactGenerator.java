package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;

import com.all.client.model.ContactTransferable;
import com.all.core.model.ContactCollection;

public class DraggedObjectContactGenerator extends SimpleDraggedObjectGenerator {

	@Override
	public DraggedObject get(Transferable transferable) {
		try {
			Object draggedObject = (ContactCollection) transferable.getTransferData(ContactTransferable.CONTACT_FLAVOR);
			return new SimpleDraggedObject(draggedObject);
		} catch (Exception e) {
			return new SimpleDraggedObject(null);
		}
	}

}
