package com.all.client.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalHeaderPanel extends HeaderPanel {
	
	private static final long serialVersionUID = 6089233547128777947L;

	@Autowired
	public LocalHeaderPanel(MediaPanel mediaPanel, LocalTitleHeaderPanel titleHeaderPanel) {
		super(mediaPanel, titleHeaderPanel);
	}
	
}
