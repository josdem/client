package com.all.core.common.observables;

import java.awt.image.BufferedImage;

import com.all.observ.ObserveObject;

public class PortraitEvent extends ObserveObject {
	private static final long serialVersionUID = 1L;
	private final BufferedImage avatar;
	private final String email;

	public PortraitEvent(String email, BufferedImage avatar) {
		this.email = email;
		this.avatar = avatar;
	}

	public BufferedImage getAvatar() {
		return avatar;
	}

	public String getEmail() {
		return email;
	}

}
