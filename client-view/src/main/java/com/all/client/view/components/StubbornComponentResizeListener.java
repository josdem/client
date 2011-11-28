package com.all.client.view.components;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

final class StubbornComponentResizeListener extends ComponentAdapter {
	private Log log = LogFactory.getLog(getClass());
	private int width;
	private int height;
	private final String name;

	public StubbornComponentResizeListener(int width, int height, String name) {
		this.width = width;
		this.height = height;
		this.name = name;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if (e.getComponent().getWidth() != width || e.getComponent().getHeight() != height) {
			log.debug(name + " stubborn from: " + e.getComponent().getSize());
			e.getComponent().setSize(width, height);
		}
		log.debug(name + " resized to: " + e.getComponent().getSize());
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}