package com.all.client.view.listeners;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import com.all.core.common.view.transparency.TransparencyManagerFactory;

public class MainFrameResizerMouseListener extends MouseAdapter {

	public static final short NO_RESIZE = 0;
	public static final short RESIZE_E = 1;
	public static final short RESIZE_W = 2;
	public static final short RESIZE_N = 4;
	public static final short RESIZE_S = 8;

	private JFrame frame;

	private final Component component;

	private short direction;
	private final short defaultDirection;
	private Rectangle originalBounds;
	private Rectangle newBounds;
	private Point originalMouse;

	private JWindow transparentWindow;

	private boolean isDragging;
	private boolean exited;

	public MainFrameResizerMouseListener(JFrame frame) {
		this(frame, NO_RESIZE);
	}

	@SuppressWarnings("serial")
	public MainFrameResizerMouseListener(Component component, short defaultDirection) {
		this.component = component;
		this.defaultDirection = defaultDirection;
		transparentWindow = new JWindow() {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setColor(Color.GRAY);
				g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}

			@Override
			public void update(Graphics g) {
			}

		};
	}

	public void init() {
		if (frame == null) {
			if (component instanceof JFrame) {
				this.frame = (JFrame) component;
			} else {
				this.frame = (JFrame) SwingUtilities.getWindowAncestor(component);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		exited = false;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		init();
		if (frame == null) {
			return;
		}
		initDirection(e);
		originalBounds = frame.getBounds(new Rectangle());
		originalMouse = e.getLocationOnScreen();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		init();
		if (frame != null && (!(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH))) {
			int cursorType = getCursorType(e);
			frame.setCursor(Cursor.getPredefinedCursor(cursorType));
			transparentWindow.setCursor(Cursor.getPredefinedCursor(cursorType));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isDragging = false;
		init();
		if (frame == null) {
			return;
		}
		direction = 0;
		transparentWindow.setAlwaysOnTop(false);
		transparentWindow.setVisible(false);
		if (newBounds != null) {
			frame.setBounds(newBounds);
		}
		frame.repaint();
		newBounds = null;
		if (exited) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		init();
		if (frame == null) {
			return;
		}
		if (!isDragging) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		exited = true;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!(frame.getExtendedState() == JFrame.MAXIMIZED_BOTH)) {
			isDragging = true;
			init();
			if (frame == null || direction == NO_RESIZE) {
				return;
			}
			Point currentMouse = e.getLocationOnScreen();
			Dimension diferential = new Dimension(currentMouse.x - originalMouse.x, currentMouse.y - originalMouse.y);
			Rectangle bounds = frame.getBounds(new Rectangle());
			newBounds = bounds;

			if ((direction & RESIZE_E) != 0) {
				bounds.width = originalBounds.width + diferential.width;
			} else if ((direction & RESIZE_W) != 0) {
				bounds.width = originalBounds.width - diferential.width;
				bounds.x = currentMouse.x;
			}
			newBounds.width = (bounds.getSize().width >= frame.getMinimumSize().width) ? bounds.getSize().width : frame
					.getMinimumSize().width;

			if ((direction & RESIZE_S) != 0) {
				bounds.height = originalBounds.height + diferential.height;
			} else if ((direction & RESIZE_N) != 0) {
				bounds.height = originalBounds.height - diferential.height;
				bounds.y = currentMouse.y;
			}
			newBounds.height = (bounds.getSize().height >= frame.getMinimumSize().height) ? bounds.getSize().height : frame
					.getMinimumSize().height;

			TransparencyManagerFactory.getManager().setWindowOpacity(transparentWindow, 0.25f);

			transparentWindow.setBounds(newBounds);
			transparentWindow.setAlwaysOnTop(true);
			transparentWindow.setVisible(true);
		}

	}

	private int getCursorType(MouseEvent evt) {
		initDirection(evt);
		switch (direction) {
		case RESIZE_S:
			return Cursor.S_RESIZE_CURSOR;
		case RESIZE_E:
			return Cursor.E_RESIZE_CURSOR;
		case RESIZE_N:
			return Cursor.N_RESIZE_CURSOR;
		case RESIZE_W:
			return Cursor.W_RESIZE_CURSOR;
		case RESIZE_S | RESIZE_E:
			return Cursor.SE_RESIZE_CURSOR;
		case RESIZE_N | RESIZE_W:
			return Cursor.NW_RESIZE_CURSOR;
		case RESIZE_N | RESIZE_E:
			return Cursor.NE_RESIZE_CURSOR;
		case RESIZE_S | RESIZE_W:
			return Cursor.SW_RESIZE_CURSOR;
		default:
			return Cursor.DEFAULT_CURSOR;
		}
	}

	private void initDirection(MouseEvent evt) {
		if (defaultDirection != NO_RESIZE) {
			direction = defaultDirection;

		} else {
			direction = NO_RESIZE;
			Insets insets = new Insets(15, 15, 15, 15);
			Point p = SwingUtilities.convertPoint(component, evt.getPoint(), frame);
			if (p.x < insets.left) {
				direction |= RESIZE_W;
			} else if (p.x > frame.getWidth() - insets.right) {
				direction |= RESIZE_E;
			}
			if (p.y < insets.top) {
				direction |= RESIZE_N;
			} else if (p.y > frame.getHeight() - insets.bottom) {
				direction |= RESIZE_S;
			}
		}
	}

}
