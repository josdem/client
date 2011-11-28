package com.all.client.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;

@Component
public class LocalContentPanel extends ContentPanel {

	private static final long serialVersionUID = 1L;

	@Autowired
	public LocalContentPanel(LocalContentTitlePanel contentTitlePanel, LocalPreviewPanel previewPanel) {
		super(contentTitlePanel, previewPanel);
	}

	@Autowired
	private ViewEngine viewEngine;

	@PostConstruct
	public void initialize() {
		super.initialize(viewEngine);
	}

}
