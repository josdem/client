package com.all.client.view.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image portrait;

	private Log log = LogFactory.getLog(getClass());
	private double arcHeight;
	private double arcWidth;

	public ImagePanel() {
	}

	public ImagePanel(Image portrait, double arcWidth, double arcHeight) {
		setImage(portrait, arcWidth, arcHeight);
	}

	public void setImage(Image portrait, double arcWidth, double arcHeight) {
		this.portrait = portrait;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
		this.repaint();
	}

	public Image getImage() {
		return portrait;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (portrait == null) {
			super.paintComponent(g);
			return;
		}
		try {
			// Create a translucent intermediate image in which we can perform the soft clipping
			GraphicsConfiguration gc = ((Graphics2D) g).getDeviceConfiguration();
			BufferedImage intermediateBufferedImage = gc.createCompatibleImage(getWidth(), getHeight(),
					Transparency.TRANSLUCENT);
			Graphics2D bufferGraphics = intermediateBufferedImage.createGraphics();

			// Clear the image so all pixels have zero alpha
			bufferGraphics.setComposite(AlphaComposite.Clear);
			bufferGraphics.fillRect(0, 0, getWidth(), getHeight());

			// Render our clip shape into the image. Shape on where to paint
			bufferGraphics.setComposite(AlphaComposite.Src);
			bufferGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			bufferGraphics.setColor(Color.WHITE);
			bufferGraphics.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (getWidth() * arcWidth),
					(int) (getHeight() * arcHeight));

			// SrcAtop uses the alpha value as a coverage value for each pixel stored in the
			// destination shape. For the areas outside our clip shape, the destination
			// alpha will be zero, so nothing is rendered in those areas. For
			// the areas inside our clip shape, the destination alpha will be fully
			// opaque.
			bufferGraphics.setComposite(AlphaComposite.SrcAtop);
			bufferGraphics.drawImage(portrait, 0, 0, getWidth(), getHeight(), null);
			bufferGraphics.dispose();

			// Copy our intermediate image to the screen
			g.drawImage(intermediateBufferedImage, 0, 0, null);

		} catch (Exception e) {
			log.warn("Error: Creating Renderings", e);
		}
	}

}
