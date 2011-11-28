package com.all.client.view.dnd;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

import com.all.observ.NullObservable;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;

public class DragAndDropActionImpl implements DragAndDropAction {
	private DnDListenerEntries<DropListener> dropListeners = DnDListenerEntries.empty();
	private DnDListenerEntries<DragOverListener> dragListeners = DnDListenerEntries.empty();
	private Observable<ObservValue<Component>> currentComponentChanged = new Observable<ObservValue<Component>>();

	private DraggedObject draggedObject = null;
	private boolean validationResult = false;

	private final Container currentFrame;
	private Component currentComponent;
	private Point location = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	private boolean done = false;

	public DragAndDropActionImpl(Container currentTriggerFrame) {
		this.currentFrame = currentTriggerFrame;
	}

	@Override
	public boolean validate(final Point point) {
		if (isDone()) {
			return false;
		}
		Boolean result = false;
		if (isDragObjectSet()) {
			final DraggedObject draggedObject = this.draggedObject;
			final DnDListenerEntries<DropListener> dropListeners = this.dropListeners;
			result = dropListeners.forEach(DragAndDropIterators.validateDrop(draggedObject, point, currentFrame));
			result = DragAndDropIterators.createBoolean(result);
		} else {
			result = true;
		}
		if (this.validationResult != result.booleanValue()) {
			final DnDListenerEntries<DragOverListener> dragListeners = this.dragListeners;
			this.validationResult = result.booleanValue();
			dragListeners.forEach(DragAndDropIterators.dragAllowedChanged(result.booleanValue()));
		}
		return validationResult;
	}

	@Override
	public void dragExit() {
		stop(false);
	}

	@Override
	public boolean drop(final Point point) {
		final DraggedObject draggedObject = this.draggedObject;
		final DnDListenerEntries<DropListener> dropListeners = this.dropListeners;
		final DnDListenerEntries<DragOverListener> dragListeners = this.dragListeners;
		final Container currentFrame = this.currentFrame;
		final boolean isDone = isDone();
		final boolean isDragObjectSet = isDragObjectSet();
		stop(true);
		boolean success = false;
		if (!(isDone || !isDragObjectSet)) {
			Boolean result = dropListeners.forEach(DragAndDropIterators.doDrop(point, draggedObject, currentFrame));
			success = DragAndDropIterators.createBoolean(result);
		}
		dragListeners.forEach(DragAndDropIterators.dropOcurred(success));
		return success;
	}

	public void setLocation(Point location) {
		if (isDone()) {
			return;
		}
		if (!this.location.equals(location)) {
			this.location = location;
			Component newComponent = currentFrame.findComponentAt(location);
			if (newComponent != null && !newComponent.equals(currentComponent)) {
				currentComponent = newComponent;
				currentComponentChanged.fire(new ObservValue<Component>(newComponent));
			}
		}
	}

	@Override
	public void setDragObject(DraggedObject draggedObject) {
		if (this.draggedObject != draggedObject) {
			this.draggedObject = draggedObject;
			currentComponentChanged.fire(new ObservValue<Component>(currentComponent));
		}
	}

	public void setDropListeners(DnDListenerEntries<DropListener> dropListeners) {
		if (dropListeners == null || isDone()) {
			dropListeners = DnDListenerEntries.empty();
		}
		this.dropListeners = dropListeners;
		validate(location);
	}

	public void setDragListeners(DnDListenerEntries<DragOverListener> dragListeners) {
		setDragListeners(dragListeners, false);
	}

	private void setDragListeners(DnDListenerEntries<DragOverListener> dragListeners, boolean isForDrop) {
		if (dragListeners == null || isDone()) {
			dragListeners = DnDListenerEntries.empty();
		}
		Point location = this.location;
		DnDListenerEntries<DragOverListener> lastListeners = this.dragListeners;
		lastListeners.forEachExclude(DragAndDropIterators.dragExit(isForDrop), dragListeners);
		DraggedObject draggedObj = draggedObject;
		boolean valRes = validationResult;
		Container frame = currentFrame;
		dragListeners.forEachExclude(DragAndDropIterators.dragEnter(location, draggedObj, valRes, frame), lastListeners);
		this.dragListeners = dragListeners;
	}

	@Override
	public ObserverCollection<ObservValue<Component>> onComponentChangedListener() {
		return this.currentComponentChanged;
	}

	@Override
	public Class<?> getContentClass() {
		if (isDragObjectSet()) {
			return draggedObject.getContentClass();
		} else {
			return null;
		}
	}

	@Override
	public boolean isDragObjectSet() {
		if (isDone()) {
			return true;
		}
		return draggedObject != null && draggedObject.get() != null;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	protected void stop(boolean causeOfDrop) {
		if (!isDone()) {
			done = true;
			currentComponentChanged.clean();
			currentComponentChanged = NullObservable.get();
			setDragListeners(null, causeOfDrop);
			setDropListeners(null);
		}
	}

	protected void updateDragOver() {
		if (isDone()) {
			return;
		}
		final Point point = location;
		final DnDListenerEntries<DragOverListener> dragListeners = this.dragListeners;
		dragListeners.forEach(DragAndDropIterators.updateLocation(point, currentFrame));
	}

}
