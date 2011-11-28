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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.ModelTransfereable;
import com.all.client.util.TrackRepository;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;

public final class ModelDragSource implements DragGestureListener, DragSourceListener {

	private static final Log LOG = LogFactory.getLog(ModelDragSource.class);

	private DragSource dragSource;
	private ModelSelection modelSelection;
	private static TrackRepository trackRepository;

	public static void addDragSource(Component c, ModelSelection modelSelection, TrackRepository trackRepository) {
		ModelDragSource.trackRepository = trackRepository;
		DragSource dragSource = new DragSource();
		ModelDragSource dgl = new ModelDragSource(dragSource, modelSelection);
		// Do not change ACTION_COPY or else only pain and misery will follow you until the end of time. JAVA DnDrops sux.
		// Big Time
		dragSource.createDefaultDragGestureRecognizer(c, DnDConstants.ACTION_COPY, dgl);
	}

	private ModelDragSource(DragSource dragSource, ModelSelection modelSelection) {
		this.dragSource = dragSource;
		this.modelSelection = modelSelection;
	}

	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Point dragOrigin = dge.getDragOrigin();
		ModelCollection draggedItems = modelSelection.selectedObjects(dragOrigin);
		if (draggedItems != null) {
			boolean remote = modelSelection.isFromRemoteLibrary(dragOrigin);
			Transferable t = new ModelTransfereable(remote ? ModelSource.remote() : ModelSource.local(), draggedItems,
					trackRepository);
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
