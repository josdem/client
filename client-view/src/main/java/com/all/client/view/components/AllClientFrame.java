package com.all.client.view.components;

import java.awt.Rectangle;
import java.awt.dnd.DropTarget;

import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.listeners.MainFrameResizerMouseListener;
import com.all.core.common.view.AllBaseFrame;
import com.all.i18n.Messages;

public abstract class AllClientFrame extends AllBaseFrame {
	private static final Rectangle BOUNDS = new Rectangle(10, 10);
	private static final long serialVersionUID = 1L;

	public AllClientFrame(Messages messages, boolean isMainFrame) {
		super(messages, isMainFrame);
		initialize();
	}

	private void initialize() {
		MainFrameResizerMouseListener topLeftListener = new MainFrameResizerMouseListener(getTopLeftResizer(),
				MainFrameResizerMouseListener.RESIZE_W);
		getTopLeftResizer().addMouseListener(topLeftListener);
		getTopLeftResizer().addMouseMotionListener(topLeftListener);

		MainFrameResizerMouseListener windowResizerMouseListener = new MainFrameResizerMouseListener(this);
		getTopResizer().addMouseListener(windowResizerMouseListener);
		getTopResizer().addMouseMotionListener(windowResizerMouseListener);

		short direction = MainFrameResizerMouseListener.RESIZE_S;
		direction |= MainFrameResizerMouseListener.RESIZE_E;
		MainFrameResizerMouseListener windowResizerMouseListener2 = new MainFrameResizerMouseListener(getResizePanel(),
				direction);
		getResizePanel().addMouseListener(windowResizerMouseListener2);
		getResizePanel().addMouseMotionListener(windowResizerMouseListener2);

	}

	public void setDragAndDrop(MultiLayerDropTargetListener dndListener, Messages messages) {
		dndListener.addDragListener(this, new MainFrameDragOverListener(this, messages));
		this.setDropTarget(new DropTarget(this, dndListener));
	}

	/**
	 * forces a resize so that the contact frame shows the new content - necessary on MAC
	 */
	public void fixPaintBugNotWindows() {
		Rectangle rect = this.getBounds();
		setBounds(BOUNDS);
		setBounds(rect);
	}

}
