package com.all.client.view.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class FileDragSource implements DragGestureListener, DragSourceListener {

	private static final Log LOG = LogFactory.getLog(FileDragSource.class);
	
	private DragSource dragSource;
	private FileSelection fileSelection;

	public static void addDragSource(Component c, FileSelection modelSelection) {
		DragSource dragSource = new DragSource();
		FileDragSource dgl = new FileDragSource(dragSource, modelSelection);
		// Do not change ACTION_COPY or else only pain and misery will follow you until the end of time. JAVA DnDrops sux.
		// Big Time
		dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_COPY, dgl);
	}

	private FileDragSource(DragSource dragSource, FileSelection fileSelection) {
		this.dragSource = dragSource;
		this.fileSelection = fileSelection;
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Point dragOrigin = dge.getDragOrigin();
		List<File> draggedItems = fileSelection.selectedObjects(dragOrigin);
		if (draggedItems != null && !draggedItems.isEmpty()) {
			Transferable t = new FileTransferable(fileSelection.isFromExternalDevices(dragOrigin), draggedItems);
			try {
				dragSource.startDrag(dge, DragSource.DefaultCopyDrop, t, this);
			} catch (InvalidDnDOperationException e) {
				LOG.error(e, e);
			}
		}
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
	}
}
