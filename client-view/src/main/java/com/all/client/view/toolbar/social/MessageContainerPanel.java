/**
 * 
 */
package com.all.client.view.toolbar.social;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public final class MessageContainerPanel extends JPanel implements Scrollable {

	private static final long serialVersionUID = 1L;
	
	private static final Color COLOR_229_228_230 = new Color(229, 228, 230);

	public MessageContainerPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Paint general background
		g2.setColor(COLOR_229_228_230);
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 10;
	}

}