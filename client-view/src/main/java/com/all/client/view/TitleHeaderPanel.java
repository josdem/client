package com.all.client.view;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.appControl.control.ViewEngine;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public abstract class TitleHeaderPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension CLOSE_BUTTON_SIZE = new Dimension(14, 14);

	private static final Dimension COLLAPSE_BUTTON_SIZE = new Dimension(18, 16);
	
	protected static final Dimension HEADER_SIZE = new Dimension(200, 19);

	private static final Point CLOSE_BUTTON_LOCATION = new Point(178, 3);

	private static final Point COLLAPSE_BUTTON_LOCATION = new Point(158, 3);

	private static final String CLOSE_BUTTON = "closeLibraryButton";
	
	private static final String COLLAPSE_BUTTON_STYLE = "collapseButton";
	
	protected static final String HEADER_BACKGROUND = "headerBackground";

	private JButton closeButton = null;

	private JButton collapseButton = null;

	private JLabel libraryLabel = null;

	public TitleHeaderPanel() {
		super();
	}

	@Override
	public abstract void destroy(ViewEngine viewEngine);

	public abstract void initialize(ViewEngine viewEngine);

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	public JButton getCollapseButton() {
		if (collapseButton == null) {
			collapseButton = new JButton();
			collapseButton.setSize(COLLAPSE_BUTTON_SIZE);
			collapseButton.setLocation(COLLAPSE_BUTTON_LOCATION);
			collapseButton.setName(COLLAPSE_BUTTON_STYLE);
		}
		return collapseButton;
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setSize(CLOSE_BUTTON_SIZE);
			closeButton.setLocation(CLOSE_BUTTON_LOCATION);
			closeButton.setName(CLOSE_BUTTON);
		}
		return closeButton;
	}

	protected JLabel getLibraryLabel() {
		if (libraryLabel == null) {
			libraryLabel = new JLabel();
			libraryLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return libraryLabel;
	}

}
