package com.all.client.view.dnd;

import java.awt.Component;
import java.awt.Point;

import com.all.observ.ObservValue;
import com.all.observ.ObserverCollection;

public interface DragAndDropAction {
	
	DragAndDropAction EMPTY_ACTION = new DragAndDropActionEmpty();

	boolean validate(Point location);

	void dragExit();

	boolean drop(Point location);

	void setLocation(Point location);

	void setDragObject(DraggedObject draggedObject);

	void setDropListeners(DnDListenerEntries<DropListener> dropListeners);

	void setDragListeners(DnDListenerEntries<DragOverListener> dragListeners);

	ObserverCollection<ObservValue<Component>> onComponentChangedListener();

	Class<?> getContentClass();

	boolean isDragObjectSet();

	boolean isDone();

}
