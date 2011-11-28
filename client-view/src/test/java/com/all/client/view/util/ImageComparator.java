package com.all.client.view.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class ImageComparator {
	private final BufferedImage image1;
	private final BufferedImage image2;
	private Area shape = null;
	private int tolerance = 0;
	private Point offset = null;

	public ImageComparator(BufferedImage image, BufferedImage image2) {
		super();
		this.image1 = image;
		this.image2 = image2;
	}

	public ImageComparator tolerance(int tolerance) {
		this.tolerance = tolerance;
		return this;
	}

	public ImageComparator shape(Shape area) {
		if(this.shape!=null){
			this.shape.add(new Area(area));
		}else{
			this.shape = new Area(area);
		}
		return this;
	}

	public ImageComparator offset(Point offset) {
		this.offset = offset;
		return this;
	}

	public void compare() throws PixelNotEqualException {
		int offsetX = offset == null ? 0 : offset.x;
		int offsetY = offset == null ? 0 : offset.y;
		int startX = 0;
		int startY = 0;
		int endX = Math.min(image1.getWidth(), image2.getWidth() - offsetX);
		int endY = Math.min(image1.getHeight(), image2.getHeight() - offsetY);
		if (shape != null) {
			Rectangle area = shape.getBounds();
			endX = Math.min(area.width + area.x, endX);
			endY = Math.min(area.height + area.y, endY);
			startX = Math.max(area.x, startX);
			startY = Math.max(area.y, startY);
		}
		for (int x = startX; x < endX; x++) {
			for (int y = startY; y < endY; y++) {
				if (shape == null || shape.contains(x, y)) {
					verifyPixel(x, y, x + offsetX, y + offsetY);
				}
			}
		}
	}

	private void verifyPixel(int x1, int y1, int x2, int y2) throws PixelNotEqualException {
		int[] pixel1 = image1.getData().getPixel(x1, y1, (int[]) null);
		int[] pixel2 = image2.getData().getPixel(x2, y2, (int[]) null);
		if (!PixelUtil.colorEqual(pixel1, pixel2, tolerance)) {
			throw new PixelNotEqualException(x1, y1, pixel1, x2, y2, pixel2);
		}
	}

}
