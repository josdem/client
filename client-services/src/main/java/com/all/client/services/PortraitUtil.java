package com.all.client.services;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.core.common.services.ApplicationConfig;
import com.all.core.common.util.ImageUtil;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.model.User;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

@SuppressWarnings("restriction")
@Service
public class PortraitUtil {
	
	private static final String AVATARS = "avatars";
	
	private static final String AVATAR_EXTENSION = ".jpg";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private File avatarFolder;
	
	private Map<String, Gender> genderCache = new HashMap<String, Gender>();
	
	@Autowired
	public void setAppConfig(ApplicationConfig appConfig) {
		this.avatarFolder = appConfig.getAppFolder(AVATARS);
	}

	// TODO test this
	public Image getAvatar(ContactInfo contact) {
		return getAvatar(contact.getEmail(), contact.getGender());
	}

	// TODO test this
	public Image getAvatar(User user) {
		return getAvatar(user.getEmail(), user.getGender());
	}

	public boolean hasAvatar(String email) {
		return getAvatarFileName(email).exists();
	}

	public Image getAvatar(String mail, Gender sex) {
		try {
			return ImageIO.read(getAvatarFileName(mail));
		} catch (Exception e) {
			if (sex == Gender.UNKNOWN) {
				Gender gender = genderCache.get(mail);
				return ImageUtil.getImage(gender == null ? Gender.MALE : gender);
			} else {
				genderCache.put(mail, sex);
				return ImageUtil.getImage(sex == null ? Gender.MALE : sex);
			}
		}
	}

	public File getAvatarFileName(String email) {
		if (avatarFolder == null) {
			throw new IllegalStateException("Avatar path must be defined before using");
		}
		return new File(avatarFolder, email + AVATAR_EXTENSION);
	}

	public void saveAvatarInDefaultLocation(String email, byte[] bytes) {
		if (bytes != null && bytes.length > 0 && email != null) {
			BufferedImage avatar = null;
			try {
				avatar = ImageIO.read(new ByteArrayInputStream(bytes));
				// Save as JPEG
				File file = getAvatarFileName(email);
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(avatar);
				param.setQuality((float) 1.0f, true);
				encoder.setJPEGEncodeParam(param);
				encoder.encode(avatar);
				out.close();
			} catch (IOException e) {
				log.debug(e, e);
			}
		}
	}

	public void saveAvatarInDefaultLocation(String email, Image image) {
		byte[] avatarData = ImageUtil.extractAvatarData(image);
		saveAvatarInDefaultLocation(email, avatarData);
	}

	public byte[] getAvatarData(ContactInfo contact) {
		return ImageUtil.extractAvatarData(getAvatar(contact));
	}

}
