package com.all.client.view.dnd;

import java.awt.Point;
import java.util.Arrays;
import java.util.Date;

import javax.swing.SwingUtilities;

import com.all.appControl.control.ViewEngine;
import com.all.client.util.TimeUtil;
import com.all.client.view.contacts.ContactTree;
import com.all.core.actions.Actions;
import com.all.core.actions.ShareContentAction;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;

public class TracksToContactListener implements DropListener, DragOverListener {
	private static final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

	private final ContactTree tree;

	private final ViewEngine viewEngine;

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

	public TracksToContactListener(ContactTree tree, ViewEngine viewEngine) {
		this.tree = tree;
		this.viewEngine = viewEngine;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		setSelectedPoint(null);
		ContactInfo contact = (ContactInfo) tree.getTreeHelper().getTreeNodeContentFromCoordinates(location);
		viewEngine.send(Actions.Social.SHOW_SEND_CONTENT_DIALOG, new ShareContentAction(model, Arrays.asList(contact)));

		tree.invalidate();
		SwingUtilities.getWindowAncestor(tree).validate();
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		Object userObject = tree.getTreeHelper().getTreeNodeContentFromCoordinates(location);
		ContactInfo contact = null;
		if (userObject instanceof ContactInfo) {
			setSelectedPoint(location.y);

			contact = (ContactInfo) userObject;
			contact.setIsDropping(true);

			return !contact.isPending();
		}
		setSelectedPoint(null);
		return false;
	}

	private void setSelectedPoint(Integer y) {
		tree.setDragOverObject(y);
		tree.repaint();
	}

	@Override
	public void dragEnter(DraggedObject dragObject) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (currentExpandThread == Thread.currentThread()) {
					moveOnDemand();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}

		});
		currentExpandThread = thread;
		thread.setDaemon(true);
		thread.setName("ScrollThread");
		thread.start();
	}

	private Thread currentExpandThread = null;
	private Point lastLocation = new Point();
	private long lastTime = 0;
	private Object lastValue = null;

	private void moveOnDemand() {
		Point location = this.lastLocation;
		Object dragOverObject = tree.getTreeHelper().getTreeNodeContentFromCoordinates(location.x, location.y);
		if (tree.getMousePosition() != null && (lastValue != dragOverObject)
				&& (tree.getPreferredScrollableViewportSize().height <= tree.getMousePosition().y)) {
			lastTime = new Date().getTime();
			lastValue = dragOverObject;
			tree.repaint();
		}
		if (TimeUtil.hasPassedOneSecond(new Date().getTime(), lastTime)) {
			tree.getTreeHelper().expandNodeFromCoordinates(location.x, location.y);
			lastTime = new Date().getTime();
		}
	}

	@Override
	public void dragAllowedChanged(boolean newStatus) {
	}

	@Override
	public void dragExit(boolean dropped) {
		setSelectedPoint(null);
	}

	@Override
	public void updateLocation(Point location) {
		this.lastLocation = location;
	}

	@Override
	public void dropOcurred(boolean success) {
	}
}
