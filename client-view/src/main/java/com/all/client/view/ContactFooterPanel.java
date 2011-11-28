package com.all.client.view;

import java.awt.Dimension;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Messages;

public class ContactFooterPanel extends FooterPanel {

	private static final long serialVersionUID = 1L;

	private static final Dimension DEFAULT_SIZE = new Dimension(0, 1);
	
	private static final String NAME = "previewTreeBackground";

	public ContactFooterPanel() {
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setSize(DEFAULT_SIZE);
		this.setName(NAME);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void removeMessages(Messages messages) {
	}

	@Override
	public void setMessages(Messages messages) {
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
	}

}
