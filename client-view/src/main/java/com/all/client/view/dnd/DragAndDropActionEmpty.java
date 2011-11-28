package com.all.client.view.dnd;

import java.awt.Component;
import java.awt.Point;

import com.all.observ.NullObservable;
import com.all.observ.ObservValue;
import com.all.observ.ObserverCollection;

public class DragAndDropActionEmpty implements DragAndDropAction {

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public void dragExit() {
	}

	@Override
	public void setLocation(Point location) {
	}

	@Override
	public ObserverCollection<ObservValue<Component>> onComponentChangedListener() {
		return NullObservable.get();
	}

	@Override
	public void setDropListeners(DnDListenerEntries<DropListener> dropListeners) {

	}

	@Override
	public void setDragListeners(DnDListenerEntries<DragOverListener> dragListeners) {

	}

	@Override
	public boolean validate(Point point) {
		return false;
	}

	@Override
	public boolean isDragObjectSet() {
		return true;
	}

	@Override
	public void setDragObject(DraggedObject draggedObject) {
	}

	@Override
	public boolean drop(Point location) {
		return false;
	}

	@Override
	public Class<?> getContentClass() {
		return null;
	}

}
