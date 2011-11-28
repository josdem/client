package com.all.core.common.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

@Entity
@Component
public class Preference {

	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	private int screenXPosition;
	private int screenYPosition;
	private int screenWidth;
	private int screenHeight;
	private int contactListscreenXPosition;
	private int contactListscreenYPosition;
	private int contactListscreenWidth;
	private int contactListscreenHeight;
	private boolean contactListSnapped = true;

	public int getScreenXPosition() {
		return screenXPosition;
	}

	public void setScreenXPosition(int screenXPosition) {
		this.screenXPosition = screenXPosition;
	}

	public int getScreenYPosition() {
		return screenYPosition;
	}

	public void setScreenYPosition(int screenYPosition) {
		this.screenYPosition = screenYPosition;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int id() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;
		if (o instanceof Preference) {
			Preference up = (Preference) o;
			result = up.id == this.id;
		}
		return result;
	}

	@Override
	public int hashCode() {
		return id == 0 ? super.hashCode() : Integer.valueOf(id).hashCode();
	}

	public int getContactListscreenXPosition() {
		return contactListscreenXPosition;
	}

	public void setContactListscreenXPosition(int contactListscreenXPosition) {
		this.contactListscreenXPosition = contactListscreenXPosition;
	}

	public int getContactListscreenYPosition() {
		return contactListscreenYPosition;
	}

	public void setContactListscreenYPosition(int contactListscreenYPosition) {
		this.contactListscreenYPosition = contactListscreenYPosition;
	}

	public int getContactListscreenWidth() {
		return contactListscreenWidth;
	}

	public void setContactListscreenWidth(int contactListscreenWidth) {
		this.contactListscreenWidth = contactListscreenWidth;
	}

	public int getContactListscreenHeight() {
		return contactListscreenHeight;
	}

	public void setContactListscreenHeight(int contactListscreenHeight) {
		this.contactListscreenHeight = contactListscreenHeight;
	}

	public void setContactListSnapped(boolean contactListSnapped) {
		this.contactListSnapped = contactListSnapped;
	}

	public boolean isContactListSnapped() {
		return contactListSnapped;
	}
}
