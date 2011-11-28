package com.all.client.view.util;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ComponentPaintValidator {
	private final Component component;
	private BufferedImage image;
	
	private Log log = LogFactory.getLog(this.getClass());

	public ComponentPaintValidator(Component component) {
		this.component = component;
		refresh();
	}

	public void refresh() {
		BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
		component.paint(image.getGraphics());
		this.image = image;
	}

	public void verifyPixels(int tolerance, Pixel... pixels) throws PixelNotEqualException {
		for (Pixel pixel : pixels) {
			verifyPixel(pixel.getX(), pixel.getY(), pixel.getArgb(), tolerance);
		}
	}

	public void verifyPixels(Collection<? extends Pixel> pixels, int tolerance) throws PixelNotEqualException {
		for (Pixel pixel : pixels) {
			verifyPixel(pixel.getX(), pixel.getY(), pixel.getArgb(), tolerance);
		}
	}

	public void verifyPixel(int x, int y, int[] argb, int tolerance) throws PixelNotEqualException {
		int[] pixel = image.getData().getPixel(x, y, (int[]) null);
		if (!PixelUtil.colorEqual(pixel, argb, tolerance)) {
			throw new PixelNotEqualException(x, y, argb, x, y, pixel);
		}
	}

	public ImageComparator compare(String filePath) throws IOException {
		return compare(new File(filePath));
	}

	public ImageComparator compare(File file) throws IOException {
		return compare(file.toURI().toURL());
	}

	public ImageComparator compare(URL url) throws IOException {
		BufferedImage image = ImageIO.read(url);
		if (image == null) {
			throw new IOException();
		}
		return compare(image);
	}

	public ImageComparator compare(BufferedImage image2) {
		return new ImageComparator(image, image2);
	}

	public void saveImage(String filename) {
		if (filename == null) {
			filename = "temp.png";
		}
		try {
			ImageIO.write(image, "PNG", new File(filename));
		} catch (IOException e) {
			log.debug(e,e);
		}
	}

	public void containsColors(BufferedImage image2, int range) throws ColorsException {
		Set<Long> encodedColors = new HashSet<Long>();
		for (int x = 0; x < image2.getWidth(); x++) {
			for (int y = 0; y < image2.getHeight(); y++) {
				int[] pixel = image2.getData().getPixel(x, y, (int[]) null);
				encodedColors.add(PixelUtil.encodeColor(pixel));
			}
		}
		containsColors(encodedColors, range);
	}

	private void containsColors(Set<Long> encodedColors, int range) throws ColorsException {
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (encodedColors.isEmpty()) {
					break;
				}
				for (Iterator<Long> iter = encodedColors.iterator(); iter.hasNext();) {
					int[] color = PixelUtil.decodeColor(iter.next());
					if (PixelUtil.colorEqual(color, image.getData().getPixel(x, y, (int[]) null), range)) {
						iter.remove();
					}
				}
			}
			if (encodedColors.isEmpty()){
				break;
			}	
		}
		if (!encodedColors.isEmpty()) {
			throw new ColorsException();
		}
	}

	public void containsColors(int tolerance, int[]... argb) {
		Set<Long> encodedColors = new HashSet<Long>();
		for (int[] color : argb) {
			encodedColors.add(PixelUtil.encodeColor(color));
		}
		containsColors(encodedColors, tolerance);

	}

}
