package com.all.core.common.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.shared.model.Gender;
import com.all.shared.util.Imageable;

public final class ImageUtil {

	private static final Log LOG = LogFactory.getLog(ImageUtil.class);
	
	private static Map<Imageable, Image> images = new HashMap<Imageable, Image>();
	
	public enum Images implements Imageable {
		STAMP_BG("StampBackground.png"), MAIL_ICON("sobre.png");

		private final String name;

		private Images(String name) {
			this.name = name;
		}

		@Override
		public String getImageName() {
			return name;
		}
	}

	private ImageUtil() {
	}

	public static Image getImage(Imageable imageable) {
		Image image = images.get(imageable);
		try {
			InputStream resourceAsStream = ImageUtil.class.getResourceAsStream("/images/" + imageable.getImageName());
			image = ImageIO.read(resourceAsStream);
			images.put(imageable, image);
		} catch (IOException e) {
			LOG.error(e, e);
		}
		return image;
	}

	public static Image getImage(URL url) {
		try {
			return ImageIO.read(url);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		return null;
	}

	public static Image getImage(byte[] imageBytes) {
		try {
			return ImageIO.read(new ByteArrayInputStream(imageBytes));
		} catch (Exception e) {
			LOG.error(e, e);
		}
		return null;
	}

	
	public static byte[] extractAvatarData(Image image) {
		if (image instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) image;

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				ImageIO.write(bi, "jpeg", out);
			} catch (IOException e) {
				LOG.error(e, e);
				return null;
			}
			return out.toByteArray();
		}
		return null;
	}
	
	public static byte[] getDefaultAvatar() {
		return ImageUtil.extractAvatarData(ImageUtil.getImage(Gender.MALE));
	}

}
