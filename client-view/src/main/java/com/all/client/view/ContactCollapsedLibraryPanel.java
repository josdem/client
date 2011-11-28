package com.all.client.view;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public class ContactCollapsedLibraryPanel extends CollapsedLibraryPanel {
	private static final long serialVersionUID = 1L;
	private final Root root;

	public ContactCollapsedLibraryPanel(Root root, Messages messages) {
		super(messages);
		this.root = root;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		initialize(viewEngine, root);
	}
}
