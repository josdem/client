package com.all.client.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class VerticalLabelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private int stringWidth;
	private Image bufferedLabel;
	private String label;
	private int stringHeight;
	private int baseLineDistance;

	public void setLabel(String label) {
		this.label = label;
		bufferedLabel = null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (label == null) {
			return;
		}
		if (this.height != getHeight() || this.width != getWidth()) {
			bufferedLabel = null;
		}

		if (bufferedLabel == null) {
			Font font = Font.decode(Font.DIALOG).deriveFont(Font.BOLD, 14);
			FontMetrics fontMetrics = g.getFontMetrics(font);
			stringWidth = fontMetrics.stringWidth(label);
			stringHeight = fontMetrics.getHeight();
			// add a 20% to allow letters that goes below baseline to draw correctly
			baseLineDistance = Math.max((int) ((double) stringHeight * 0.2), 2);

			Image img = new BufferedImage(stringHeight + baseLineDistance, stringWidth, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D) img.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.rotate(Math.toRadians(-90));
			g2.setFont(font);
			g2.setColor(new Color(100, 100, 100));
			g2.drawString(label, -1 * stringWidth, stringHeight - baseLineDistance);
			this.bufferedLabel = img;
			this.height = getHeight();
			this.width = getWidth();
		}
		g.drawImage(bufferedLabel, ((getWidth() - stringHeight) / 2), (getHeight() / 2)
				- (stringWidth / 2), null);
	}

}
