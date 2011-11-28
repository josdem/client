package com.all.client.view;

import java.awt.Rectangle;

import javax.swing.border.EmptyBorder;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public final class DeviceTitleHeaderPanel extends TitleHeaderPanel {

	private static final long serialVersionUID = 1L;

	private static final EmptyBorder LIBRARY_LABEL_BORDER = new EmptyBorder(2, 10, 2, 0);

	private static final Rectangle LIBRARY_LABEL_BOUNDS = new Rectangle(2, 3, 160, 16);

	private final Root root;

	public DeviceTitleHeaderPanel(Root root) {
		this.root = root;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.setLayout(null);
		this.setSize(HEADER_SIZE);
		this.setName(HEADER_BACKGROUND);
		this.setPreferredSize(HEADER_SIZE);
		this.setMaximumSize(HEADER_SIZE);
		this.setMinimumSize(HEADER_SIZE);

		getLibraryLabel().setBounds(LIBRARY_LABEL_BOUNDS);
		getLibraryLabel().setBorder(LIBRARY_LABEL_BORDER);

		this.add(getLibraryLabel(), null);
		this.add(getCloseButton(), null);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	@Override
	public void internationalize(Messages messages) {
		getLibraryLabel().setText(root.getName().toUpperCase());
	}
}
