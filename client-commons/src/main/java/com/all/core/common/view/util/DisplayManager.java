package com.all.core.common.view.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

public final class DisplayManager {

	public static final int MAIN_DISPLAY = 0;
	public static final int SECONDARY_DISPLAY = 1;
	
	private DisplayManager() {
	}

	public static GraphicsDevice[] getGraphicDevices() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}

	public static Insets getDeviceInsets(int display) {
		GraphicsDevice[] graphicDevices = getGraphicDevices();
		return Toolkit.getDefaultToolkit().getScreenInsets(graphicDevices[display].getDefaultConfiguration());
	}

	public static Rectangle getMaximumDisplayBounds(int display) {
		GraphicsDevice[] graphicDevices = getGraphicDevices();
		// FIXME check display is within range, and check the returning Rectangle
		// needed to work proper
		// log.info("->graphicDevices.length=" + graphicDevices.length);
		if (display >= graphicDevices.length) {
			return graphicDevices[MAIN_DISPLAY].getDefaultConfiguration().getBounds();
		} else {
			return graphicDevices[display].getDefaultConfiguration().getBounds();
		}
	}

	public static boolean belongsToMainDisplay(int screenXCoordinate) {
		Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().getBounds();
		if (screenXCoordinate <= bounds.width) {
			return true;
		}
		return false;
	}

	public static Rectangle getAvailableDisplayBounds(int display) {

		Rectangle bounds = DisplayManager.getMaximumDisplayBounds(display);
		Insets insets = DisplayManager.getDeviceInsets(display);

		return new Rectangle(bounds.x + insets.left, bounds.y + insets.top, bounds.width - insets.right, bounds.height
				- insets.bottom);

	}

}
