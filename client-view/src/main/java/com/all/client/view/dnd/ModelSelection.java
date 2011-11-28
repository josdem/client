package com.all.client.view.dnd;

import java.awt.Point;

import com.all.shared.model.ModelCollection;



public interface ModelSelection {
	ModelCollection selectedObjects(Point point);

	boolean isFromRemoteLibrary(Point point);
}
