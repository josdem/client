package com.all.core.common.view.util;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;

public final class WindowDraggerMouseListener implements MouseMotionListener, MouseListener {

	private static final int TOP_SCREEN_JUNCTURE_INSET = 3;
	private static final int TOP_MENU_HEIGHT = 18;

	private Point startMousePoint;
	private Point startWindowPoint;

	private final Observable<ObservValue<Window>> frameMovedEvent = new Observable<ObservValue<Window>>();

	private Window window;

	// TODO Discuss about supporting more than two displays
	@Override
	public void mouseDragged(MouseEvent e) {
		window = getWindowComponent(e);

		if (window != null && (isDraggeableFrame() || window instanceof JDialog)) {
			Rectangle screenBounds = DisplayManager.getMaximumDisplayBounds(DisplayManager.MAIN_DISPLAY);
			Insets screenInsets = DisplayManager.getDeviceInsets(DisplayManager.MAIN_DISPLAY);

			// Additional display doesn't necessarily starts on zero
			int screenYDelta = 0;

			// detects when dragging over an additional display
			if (!DisplayManager.belongsToMainDisplay(e.getXOnScreen())) {
				screenBounds = DisplayManager.getMaximumDisplayBounds(DisplayManager.SECONDARY_DISPLAY);
				screenInsets = DisplayManager.getDeviceInsets(DisplayManager.SECONDARY_DISPLAY);
				screenYDelta = screenBounds.y;
			}

			int x = startWindowPoint.x;
			int y = startWindowPoint.y;

			x += (e.getXOnScreen() - startMousePoint.x);
			y += (e.getYOnScreen() - startMousePoint.y);

			if (y - screenYDelta < screenInsets.top) {
				y = screenInsets.top - TOP_SCREEN_JUNCTURE_INSET;
			}
			if ((y - screenYDelta + TOP_MENU_HEIGHT) > (screenBounds.height - screenInsets.bottom)) {
				y = screenBounds.height - screenInsets.bottom - TOP_MENU_HEIGHT;
			}

			window.setLocation(x, y);
		}
	}

	private boolean isDraggeableFrame() {
		return (window instanceof JFrame && !(((JFrame) window).getExtendedState() == JFrame.MAXIMIZED_BOTH));
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startMousePoint = e.getLocationOnScreen();
		window = getWindowComponent(e);
		startWindowPoint = window.getLocationOnScreen();
	}

	Window getWindowComponent(MouseEvent e) {
		return (Window) SwingUtilities.windowForComponent((Component) e.getSource());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		frameMovedEvent.fire(new ObservValue<Window>(window));
	}

	public void setup(Component component) {
		if (component != null) {
			component.addMouseMotionListener(this);
			component.addMouseListener(this);
		}
	}

	public ObserverCollection<ObservValue<Window>> frameMoved() {
		return frameMovedEvent;
	}
}