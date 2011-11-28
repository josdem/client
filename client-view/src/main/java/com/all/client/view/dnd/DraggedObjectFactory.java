package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

public class DraggedObjectFactory {
	private final List<DraggedObjectGenerator> innerGenerators = new ArrayList<DraggedObjectGenerator>();

	public DraggedObjectFactory() {
		innerGenerators.add(new DraggedObjectModelGenerator());
		innerGenerators.add(new DraggedObjectDownloadGenerator());
		innerGenerators.add(new DraggedObjectContactGenerator());
		innerGenerators.add(new DraggedObjectPictureGenerator());
		innerGenerators.add(new DraggedObjectFileSystemGenerator());
	}

	public DraggedObject getPreview(Transferable transferable) {
		for (DraggedObjectGenerator generator : innerGenerators) {
			DraggedObject draggedObject = generator.getPreview(transferable);
			if (draggedObject != null && draggedObject.get() != null) {
				return draggedObject;
			}
		}
		return DraggedObject.NULL;
	}

	public DraggedObject getContent(Transferable transferable) {
		for (DraggedObjectGenerator generator : innerGenerators) {
			DraggedObject draggedObject = generator.getContent(transferable);
			if (draggedObject != null && draggedObject.get() != null) {
				return draggedObject;
			}
		}
		return DraggedObject.NULL;
	}

}
