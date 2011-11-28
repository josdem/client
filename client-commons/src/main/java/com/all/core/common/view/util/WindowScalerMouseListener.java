/**
 * 
 */
package com.all.core.common.view.util;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public final class WindowScalerMouseListener implements MouseMotionListener, MouseListener {
	boolean mouseDown = false;
	Point startMousePoint;
	Dimension startWindowSize;
	Window window;

	public static void setWindowScaler(Window window) {
		WindowScalerMouseListener scaler = new WindowScalerMouseListener(window);
		window.addMouseMotionListener(scaler);
		window.addMouseListener(scaler);
	}

	private WindowScalerMouseListener(Window window) {
		this.window = window;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		move(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		move(e);
	}

	private void move(MouseEvent e) {
		Point windowPosition = window.getLocation();
		Dimension windowSize = window.getSize();
		int newCursor = Cursor.DEFAULT_CURSOR;
		if (getDragPoint(e.getLocationOnScreen(), windowPosition, windowSize)) {
			newCursor = Cursor.SE_RESIZE_CURSOR;
		}
		if (window.getCursor().getType() != newCursor) {
			window.setCursor(new Cursor(newCursor));
		}
		if (mouseDown) {
			int width = startWindowSize.width;
			int height = startWindowSize.height;
			width += (e.getXOnScreen() - startMousePoint.x);
			height += (e.getYOnScreen() - startMousePoint.y);
			window.setSize(width, height);
		}
	}

	private boolean getDragPoint(Point mousePosition, Point windowPosition, Dimension windowSize) {
		if (mousePosition.x > windowPosition.x + windowSize.width - 10
				&& mousePosition.x < windowPosition.x + windowSize.width
				&& mousePosition.y > windowPosition.y + windowSize.height - 10
				&& mousePosition.y < windowPosition.y + windowSize.height) {
			return true;
		}
		return false;
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
	public void mousePressed(MouseEvent e) {
		startMousePoint = e.getLocationOnScreen();
		startWindowSize = window.getSize();
		if (getDragPoint(e.getLocationOnScreen(), window.getLocation(), window.getSize())) {
			mouseDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseDown = false;
	}
}