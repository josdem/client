package com.all.core.common.view.transparency;

import java.awt.GraphicsConfiguration;
import java.awt.Shape;
import java.awt.Window;


public class NullTransparencyManager implements TransparencyManager {

	@Override
	public boolean isTranslucencySupported(Object kind) {
		return false;
	}

	@Override
	public boolean isTranslucencyCapable(GraphicsConfiguration gc) {
		return false;
	}

	@Override
	public void setWindowShape(Window window, Shape shape) {
	}

	@Override
	public void setWindowOpacity(Window window, float opacity) {
	}

	@Override
	public void setWindowOpaque(Window window, boolean opaque) {
	}

	@Override
	public GraphicsConfiguration getTranslucencyCapableGC() {
		return null;
	}

}
