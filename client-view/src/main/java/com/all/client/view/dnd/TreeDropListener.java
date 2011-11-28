package com.all.client.view.dnd;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.model.DownloadCollection;
import com.all.client.util.ModelValidation;
import com.all.client.util.TimeUtil;
import com.all.client.view.MyMusicHoverAnimation;
import com.all.client.view.components.PreviewTree;
import com.all.core.actions.Actions;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.actions.ModelImportAction;
import com.all.core.actions.ModelMoveAction;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.MediaImportStat.ImportType;

public class TreeDropListener implements DropListener, DragOverListener {
	private static final Class<?>[] classes = new Class<?>[] { FileSystemValidatorLight.class, ModelCollection.class,
			DownloadCollection.class };

	private long lastTime = 0;
	private Object lastValue = null;

	private Object lastDraggedContent;

	private final MyMusicHoverAnimation animation;
	private final ViewEngine viewEngine;
	private final PreviewTree previewTree;

	public TreeDropListener(MyMusicHoverAnimation animation, ViewEngine viewEngine, PreviewTree previewTree) {
		this.animation = animation;
		this.viewEngine = viewEngine;
		this.previewTree = previewTree;
	}

	public void doDrop(ModelCollection model, Point location) {
		currentExpandThread = null;
		previewTree.setDragOverObject(null);
		TrackContainer treeValue;
		if (location == null) {
			treeValue = viewEngine.get(Model.SELECTED_CONTAINER);
		} else {
			treeValue = getValueFromPreviewTree(location);
		}
		animation.stop();
		viewEngine.send(Actions.Library.MODEL_MOVE, new ModelMoveAction(model, treeValue));
	}

	private void doDrop(DownloadCollection downloadCollection, Point location) {
		currentExpandThread = null;
		previewTree.setDragOverObject(null);
		TrackContainer treeValue;
		if (location == null) {
			treeValue = viewEngine.get(Model.SELECTED_CONTAINER);
		} else {
			treeValue = getValueFromPreviewTree(location);
		}
		animation.stop();
		List<String> hashcodes = new ArrayList<String>();
		for (Download download : downloadCollection.getDownloads()) {
			hashcodes.add(download.getTrackId());
		}
		final TrackContainer target = treeValue;
		viewEngine.request(Actions.Library.FIND_TRACKS_BY_HASHCODES, hashcodes, new ResponseCallback<List<Track>>() {
			@Override
			public void onResponse(List<Track> t) {
				ModelCollection model = new ModelCollection(t);
				viewEngine.send(Actions.Library.MODEL_MOVE, new ModelMoveAction(model, target));
			}
		});
	}

	public boolean validateDrop(ModelCollection model, Point location) {
		TrackContainer treeValue;
		if (location == null) {
			treeValue = viewEngine.get(Model.SELECTED_CONTAINER);
		} else {
			treeValue = getValueFromPreviewTree(location);
		}
		boolean ableToPaste = ModelValidation.isAbleToPaste(model, treeValue);
		if (!ableToPaste) {
			previewTree.setDragOverObject(null);
		} else {
			previewTree.setDragOverObject(location.y);
		}
		animateOnDemand(treeValue);
		previewTree.repaint();
		return ableToPaste;
	}

	public void doDrop(FileSystemValidatorLight fileSystemValidator, Point location) {
		currentExpandThread = null;
		previewTree.setDragOverObject(null);
		Object target = previewTree.getTreeHelper().getTreeNodeContentFromCoordinates(location.x, location.y);
		if (!(target instanceof TrackContainer)) {
			target = null;
		}
		if (target == null) {
			target = viewEngine.get(Model.USER_ROOT);
		}
		animation.stop();
		previewTree.repaint();
		ImportType importType = fileSystemValidator.isFromExternalDevicesPanel() ? ImportType.EXTERNAL_DEVICES
				: ImportType.SYSTEM_DRAG;
		viewEngine.send(Actions.Library.MODEL_IMPORT, new ModelImportAction((TrackContainer) target, importType,
				fileSystemValidator));
	}

	public boolean validateDrop(FileSystemValidatorLight validator, Point location) {
		Object value = previewTree.getTreeHelper().getTreeNodeContentFromCoordinates(location.x, location.y);
		if (value == null) {
			value = viewEngine.get(Model.USER_ROOT);
		}
		boolean canBeInside = validator.canBeInside(value);
		if (!canBeInside) {
			previewTree.setDragOverObject(null);
		} else {
			previewTree.setDragOverObject(location.y);
		}
		animateOnDemand(value);
		previewTree.repaint();
		return canBeInside;
	}

	private boolean validateDrop(DownloadCollection downloadCollection, Point location) {
		Object value = previewTree.getTreeHelper().getTreeNodeContentFromCoordinates(location.x, location.y);
		boolean canBeInside = value != null && !downloadCollection.getDownloads().isEmpty();
		if (!canBeInside) {
			previewTree.setDragOverObject(null);
		} else {
			previewTree.setDragOverObject(location.y);
		}
		animateOnDemand(value);
		previewTree.repaint();
		return canBeInside;
	}

	@Override
	public void dragExit(boolean dropped) {
		previewTree.repaint();
		previewTree.setDragOverObject(null);
		currentExpandThread = null;
		animation.stop();
	}

	@Override
	public void dropOcurred(boolean success) {
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

	private Thread currentExpandThread = null;

	@Override
	public void dragEnter(DraggedObject dragObject) {
		lastDraggedContent = dragObject;
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

	private void moveOnDemand() {
		Point location = this.lastLocation;
		Object dragOverObject = previewTree.getTreeHelper().getTreeNodeContentFromCoordinates(location.x, location.y);
		if (previewTree.getMousePosition() != null && (lastValue != dragOverObject)
				&& (previewTree.getPreferredScrollableViewportSize().height <= previewTree.getMousePosition().y)) {
			lastTime = System.currentTimeMillis();
			lastValue = dragOverObject;
			previewTree.repaint();
		}
		if (TimeUtil.hasPassedOneSecond(System.currentTimeMillis(), lastTime)) {
			previewTree.getTreeHelper().expandNodeFromCoordinates(location.x, location.y);
			lastTime = System.currentTimeMillis();
		}
	}

	private Point lastLocation = new Point();

	@Override
	public void updateLocation(Point location) {
		if (lastDraggedContent == null) {
			previewTree.setDragOverObject(location.y);
			previewTree.repaint();
		}
		this.lastLocation = location;
	}

	private TrackContainer getValueFromPreviewTree(Point location) {
		adjustLocationIfPointLeftOfTree(location);
		return (TrackContainer) previewTree.getTreeHelper().getDefaultMutableTreeNodeFromCoordinates(location)
				.getUserObject();
	}

	private void adjustLocationIfPointLeftOfTree(Point location) {
		final int minX = 20;
		if (location.x < minX) {
			location.x = minX;
		}
	}

	@Override
	public void dragAllowedChanged(boolean newStatus) {
	}

	private void animateOnDemand(Object treeValue) {
		if (!(treeValue instanceof Playlist) || !(treeValue instanceof Folder)) {
			animation.stop();
		}
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		if (draggedObject.is(ModelCollection.class)) {
			return validateDrop(draggedObject.get(ModelCollection.class), location);
		}
		if (draggedObject.is(FileSystemValidatorLight.class)) {
			return validateDrop(draggedObject.get(FileSystemValidatorLight.class), location);
		}
		if (draggedObject.is(DownloadCollection.class)) {
			return validateDrop(draggedObject.get(DownloadCollection.class), location);
		}
		return false;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		if (draggedObject.is(ModelCollection.class)) {
			doDrop(draggedObject.get(ModelCollection.class), location);
		}
		if (draggedObject.is(FileSystemValidatorLight.class)) {
			doDrop(draggedObject.get(FileSystemValidatorLight.class), location);
		}
		if (draggedObject.is(DownloadCollection.class)) {
			doDrop(draggedObject.get(DownloadCollection.class), location);
		}
	}

}
