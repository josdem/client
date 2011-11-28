package com.all.client.view.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.lang.reflect.Field;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SquareTransparencyCropArea extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(SquareTransparencyCropArea.class);

	private static final int STROKE = 1;
	private static final int CORNER_SIZE = 8;

	private int squareCropSize;
	private int width;
	private int height;

	private JPanel nw;
	private JPanel ne;
	private JPanel sw;
	private JPanel se;

	public Component component = this;
	private RenderingHints hints;
	private Rectangle intersection;
	private Rectangle complementTop;
	private Rectangle complementBottom;
	private Rectangle complementLeft;
	private Rectangle complementRight;

	public SquareTransparencyCropArea(Rectangle dimension) {
		createRenderingHints();
		width = dimension.width;
		height = dimension.height;
		initializeSquareInside();
		updatePanelsInCornersOfIntersection();
		MouseCornerListener mouseListener = new MouseCornerListener();
		MouseMoveCropAreaListener mouseMoveCropArea = new MouseMoveCropAreaListener();
		addMouseListener(mouseMoveCropArea);
		addMouseMotionListener(mouseMoveCropArea);
		setLayout(null);
		add(nw);
		setComponentZOrder(nw, 0);
		nw.addMouseMotionListener(mouseListener);
		add(ne);
		setComponentZOrder(ne, 0);
		ne.addMouseMotionListener(mouseListener);
		add(sw);
		setComponentZOrder(sw, 0);
		sw.addMouseMotionListener(mouseListener);
		add(se);
		setComponentZOrder(se, 0);
		se.addMouseMotionListener(mouseListener);
	}

	private void initializeSquareInside() {
		intersection = new Rectangle();
		if (isHorizontal()) {
			squareCropSize = height - STROKE;
			intersection.x = (width - squareCropSize) / 2;
			intersection.y = 0;
		} else {
			squareCropSize = width - STROKE;
			intersection.x = 0;
			intersection.y = (height - squareCropSize) / 2;
		}
		intersection.width = squareCropSize;
		intersection.height = squareCropSize;
		updateComplementIntersection();
	}

	private boolean isHorizontal() {
		return width > height;
	}

	private void updatePanelsInCornersOfIntersection() {
		if (nw == null) {
			nw = new JPanel();
			nw.setPreferredSize(new Dimension(CORNER_SIZE, CORNER_SIZE));
			nw.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			nw.setName("nw");
		}
		nw.setBounds(intersection.x - 4, intersection.y - 4, CORNER_SIZE, CORNER_SIZE);
		if (ne == null) {
			ne = new JPanel();
			ne.setPreferredSize(new Dimension(CORNER_SIZE, CORNER_SIZE));
			ne.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			ne.setName("ne");
		}
		ne.setBounds(intersection.x + intersection.width - 4, intersection.y - 4, CORNER_SIZE, CORNER_SIZE);
		if (sw == null) {
			sw = new JPanel();
			sw.setPreferredSize(new Dimension(CORNER_SIZE, CORNER_SIZE));
			sw.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			sw.setName("sw");
		}
		sw.setBounds(intersection.x - 4, intersection.y + intersection.height - 4, CORNER_SIZE, CORNER_SIZE);
		if (se == null) {
			se = new JPanel();
			se.setPreferredSize(new Dimension(CORNER_SIZE, CORNER_SIZE));
			se.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			se.setName("se");
		}
		se.setBounds(intersection.x + intersection.width - 4, intersection.y + intersection.height - 4, CORNER_SIZE,
				CORNER_SIZE);
	}

	private void updateComplementIntersection() {
		int rightLimitOnX = intersection.x + intersection.width;
		int bottomHeight = height - intersection.y - intersection.height;
		int bottomY = intersection.y + intersection.height;
		int rightWidth = width - (rightLimitOnX);

		if (isHorizontal()) {
			complementTop = new Rectangle(intersection.x, 0, intersection.width, intersection.y);
			complementBottom = new Rectangle(intersection.x, bottomY, intersection.width, bottomHeight);
			complementLeft = new Rectangle(0, 0, intersection.x, height);
			complementRight = new Rectangle(rightLimitOnX, 0, rightWidth, height);

		} else {
			complementTop = new Rectangle(0, 0, width, intersection.y);
			complementBottom = new Rectangle(0, bottomY, width, bottomHeight);
			complementLeft = new Rectangle(0, intersection.y, intersection.x, intersection.height);
			complementRight = new Rectangle(rightLimitOnX, intersection.y, rightWidth, intersection.height);
		}
	}

	private void createRenderingHints() {
		hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		Object value = RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		try {
			Field declaredField = RenderingHints.class.getDeclaredField("VALUE_TEXT_ANTIALIAS_LCD_HRGB");
			value = declaredField.get(null);
		} catch (Exception e) {
			log.error(e, e);
		}
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, value);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHints(hints);
		/* complement */
		AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.40f);
		g2.setComposite(alpha);
		g2.setColor(Color.white);
		g2.fillRect(complementTop.x, complementTop.y, complementTop.width, complementTop.height);
		g2.fillRect(complementBottom.x, complementBottom.y, complementBottom.width, complementBottom.height);
		g2.fillRect(complementLeft.x, complementLeft.y, complementLeft.width, complementLeft.height);
		g2.fillRect(complementRight.x, complementRight.y, complementRight.width, complementRight.height);
		/* intersection */
		alpha = AlphaComposite.SrcOver.derive(1.f);
		g2.setComposite(alpha);
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(STROKE));
		g2.drawRect(intersection.x, intersection.y, intersection.width, intersection.height);
		/* corners */
		g2.setColor(Color.white);
		g2.fillRect(ne.getBounds().x, ne.getBounds().y, ne.getBounds().width, ne.getBounds().height);
		g2.fillRect(nw.getBounds().x, nw.getBounds().y, nw.getBounds().width, nw.getBounds().height);
		g2.fillRect(se.getBounds().x, se.getBounds().y, se.getBounds().width, se.getBounds().height);
		g2.fillRect(sw.getBounds().x, sw.getBounds().y, sw.getBounds().width, sw.getBounds().height);
		g2.setColor(Color.black);
		g2.drawRect(ne.getBounds().x, ne.getBounds().y, ne.getBounds().width, ne.getBounds().height);
		g2.drawRect(nw.getBounds().x, nw.getBounds().y, nw.getBounds().width, nw.getBounds().height);
		g2.drawRect(se.getBounds().x, se.getBounds().y, se.getBounds().width, se.getBounds().height);
		g2.drawRect(sw.getBounds().x, sw.getBounds().y, sw.getBounds().width, sw.getBounds().height);
		g2.dispose();
	}

	private void updateAndRepaint() {
		updatePanelsInCornersOfIntersection();
		updateComplementIntersection();
		repaint();
	}

	public Rectangle getCropArea() {
		return intersection;
	}

	private final class MouseMoveCropAreaListener extends MouseAdapter {
		private Point previousPoint;
		private boolean isDragging = false;

		@Override
		public void mousePressed(MouseEvent e) {
			previousPoint = e.getPoint();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			int cursor = isInsideIntersection(e) ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR;
			component.setCursor(Cursor.getPredefinedCursor(cursor));
		}

		private boolean isInsideIntersection(MouseEvent e) {
			return intersection.contains(e.getPoint());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isInsideIntersection(e) || isDragging) {
				isDragging = true;
				moveIntersection(e);
				updateAndRepaint();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isDragging = false;
		}

		private void moveIntersection(MouseEvent e) {
			int dx = previousPoint.x - e.getPoint().x;
			int dy = previousPoint.y - e.getPoint().y;
			previousPoint.x -= dx;
			previousPoint.y -= dy;

			Rectangle tmpIntersection = new Rectangle(intersection);
			tmpIntersection.translate(-dx, -dy);
			intersection = validateMovedIntersection(tmpIntersection);
		}

		private Rectangle validateMovedIntersection(Rectangle newIntersection) {
			Rectangle validatedIntersection = new Rectangle(newIntersection);
			if (validatedIntersection.x < 0) {
				validatedIntersection.x = 0;
			}
			if (validatedIntersection.y < 0) {
				validatedIntersection.y = 0;
			}
			if (validatedIntersection.x + validatedIntersection.width > width) {
				validatedIntersection.y = intersection.y;
				validatedIntersection.x = intersection.x;
				validatedIntersection.height = validatedIntersection.width = intersection.width;
			}
			if (validatedIntersection.y + validatedIntersection.height > height) {
				validatedIntersection.x = intersection.x;
				validatedIntersection.y = intersection.y;
				validatedIntersection.height = validatedIntersection.width = intersection.height;
			}
			return validatedIntersection;
		}

	}

	class MouseCornerListener extends MouseMotionAdapter {
		private static final int MIN_SIZE = 42;

		@Override
		public void mouseDragged(MouseEvent e) {
			resizeDependingOnCase(e);
			updateAndRepaint();
		}

		private void resizeDependingOnCase(MouseEvent e) {
			String name = ((JPanel) e.getSource()).getName();
			int delta = e.getPoint().y;
			int squareSize = 0;
			if (name.equals("nw")) {
				delta = validateNWCorner(delta);
				intersection.x = intersection.x + delta;
				intersection.y = intersection.y + delta;
				squareSize = intersection.width - delta;
			} else if (name.equals("ne")) {
				delta = validateNECorner(delta);
				intersection.y = intersection.y + delta;
				squareSize = intersection.width - delta;
			} else if (name.equals("sw")) {
				delta = validateSWCorner(delta);
				intersection.x = intersection.x - delta;
				squareSize = intersection.width + delta;
			} else if (name.equals("se")) {
				delta = validateSECorner(delta);
				squareSize = intersection.width + delta;
			}
			intersection.width = squareSize;
			intersection.height = squareSize;
		}

		private int validateSECorner(int delta) {
			int d = delta;
			if (intersection.x + intersection.width - STROKE + d > width
					|| intersection.y + intersection.height - STROKE + d > height) {
				// outside area, grow to maximum size possible
				d = Math.min(width - STROKE - intersection.x - intersection.width, height - STROKE - intersection.y
						- intersection.height);
			}
			if (intersection.width + d < MIN_SIZE) {
				// the minimum area, avoid further shrinking
				d = MIN_SIZE - intersection.width;
			}
			return d;
		}

		private int validateSWCorner(int delta) {
			int d = delta;
			if (intersection.x - d < 0 || intersection.y + intersection.height - STROKE + d > height) {
				// outside area, grow to maximum size possible
				d = Math.min(intersection.x, height - STROKE - intersection.y - intersection.height);
			}
			if (intersection.width + d < MIN_SIZE) {
				// the minimum area, avoid further shrinking
				d = MIN_SIZE - intersection.width;
			}
			return d;
		}

		private int validateNECorner(int delta) {
			int d = delta;
			if (intersection.y + d < 0 || intersection.x + intersection.width - STROKE - d > width) {
				// outside area, grow to maximum size possible
				d = Math.min(intersection.y, width - STROKE - intersection.x - intersection.width) * -1;
			}
			if (intersection.width - d < MIN_SIZE) {
				// the minimum area, avoid further shrinking
				d = (MIN_SIZE - intersection.width) * -1;
			}
			return d;
		}

		private int validateNWCorner(int delta) {
			int d = delta;
			if (intersection.x + d < 0 || intersection.y + d < 0) {
				// outside area, grow to maximum size possible
				d = Math.min(intersection.x, intersection.y) * -1;
			}
			if (intersection.width - d < MIN_SIZE) {
				// the minimum area, avoid further shrinking
				d = intersection.width - MIN_SIZE;
			}
			return d;
		}
	}

}
