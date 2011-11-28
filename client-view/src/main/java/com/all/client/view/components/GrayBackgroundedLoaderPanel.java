package com.all.client.view.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GrayBackgroundedLoaderPanel extends TransparentLoaderPanel {
	private static final long serialVersionUID = 1L;

	private static final Color BACKGROUND_GLASS_PANEL = new Color(77, 77, 77, 100);

	public GrayBackgroundedLoaderPanel() {
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		// Paint general background
		g2.setColor(BACKGROUND_GLASS_PANEL);
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}
