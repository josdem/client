package com.all.client.view;

import java.awt.Rectangle;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public final class ContactTitleHeaderPanel extends TitleHeaderPanel {
	private static final long serialVersionUID = 1L;

	private static final Rectangle BOUNDS = new Rectangle(4, 3, 150, 16);

	private static final Rectangle DROP_DOWN_PANEL_BOUNDS = new Rectangle(4, 3, 150, 16);

	private ComboBox dropDownPanel;

	private final Root root;

	private final ViewEngine viewEngine;

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	public ContactTitleHeaderPanel(Root root, ViewEngine viewEngine) {
		this.root = root;
		this.viewEngine = viewEngine;
	}

	public void initialize(ViewEngine viewEngine) {
		this.setLayout(null);
		this.setSize(HEADER_SIZE);
		this.setName(HEADER_BACKGROUND);
		this.setPreferredSize(HEADER_SIZE);
		this.setMaximumSize(HEADER_SIZE);
		this.setMinimumSize(HEADER_SIZE);
		this.setBounds(BOUNDS);
		this.add(getCloseButton(), null);
		this.add(getCollapseButton(), null);
		this.add(getDropDownPanel(), null);
	}

	public ComboBox getDropDownPanel() {
		if (dropDownPanel == null) {
			dropDownPanel = new ComboBox(viewEngine);
			dropDownPanel.setBounds(DROP_DOWN_PANEL_BOUNDS);
		}
		return dropDownPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getDropDownPanel().setText(root.getName().toUpperCase());
	}
}
