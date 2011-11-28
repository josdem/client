package com.all.core.common.view.transparency;

import java.awt.GraphicsConfiguration;
import java.awt.Shape;
import java.awt.Window;

public interface TransparencyManager {

	public boolean isTranslucencySupported(Object kind);

	public boolean isTranslucencyCapable(GraphicsConfiguration gc);

	public void setWindowShape(Window window, Shape shape);

	public void setWindowOpacity(Window window, float opacity);

	public void setWindowOpaque(Window window, boolean opaque);

	public GraphicsConfiguration getTranslucencyCapableGC();
}
