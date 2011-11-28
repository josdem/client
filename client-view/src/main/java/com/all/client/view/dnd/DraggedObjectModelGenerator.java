package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;

import com.all.client.model.ModelTransfereable;
import com.all.shared.model.ModelCollection;

public class DraggedObjectModelGenerator extends SimpleDraggedObjectGenerator {

	@Override
	public DraggedObject get(Transferable transferable) {
		try {
			Object draggedObject = (ModelCollection) transferable.getTransferData(ModelTransfereable.MODEL_FLAVOR);
			return new SimpleDraggedObject(draggedObject);
		} catch (Exception e) {
			return DraggedObject.NULL;
		}
	}
}
