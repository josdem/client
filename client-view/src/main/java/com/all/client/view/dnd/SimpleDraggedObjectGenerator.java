package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;

public abstract class SimpleDraggedObjectGenerator implements DraggedObjectGenerator {

	@Override
	public final DraggedObject getPreview(Transferable transferable) {
		return get(transferable);
	}

	@Override
	public DraggedObject getContent(Transferable transferable) {
		return get(transferable);
	}

	abstract DraggedObject get(Transferable transferable);

}
