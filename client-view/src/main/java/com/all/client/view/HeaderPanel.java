package com.all.client.view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class HeaderPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;
	private final MediaPanel mediaPanel;
	private final TitleHeaderPanel titleHeaderPanel;

	public HeaderPanel(MediaPanel mediaPanel, TitleHeaderPanel titleHeaderPanel) {
		this.mediaPanel = mediaPanel;
		this.titleHeaderPanel = titleHeaderPanel;
		
		initialize();
	}

	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(titleHeaderPanel);
		if (mediaPanel != null) {
			this.add(mediaPanel);
			mediaPanel.initialize();
		}
	}
	
	@Override
	public void initialize(ViewEngine viewEngine) {
		titleHeaderPanel.initialize(viewEngine);
		if (mediaPanel != null) {
			mediaPanel.initialize(viewEngine);
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		titleHeaderPanel.destroy(viewEngine);
		if (mediaPanel != null) {
			mediaPanel.destroy(viewEngine);
		}
	}

	@Override
	public void internationalize(Messages messages) {
		getCollapseButton().setToolTipText(messages.getMessage("tooltip.button.collapse"));
		getCloseButton().setToolTipText(messages.getMessage("tooltip.button.close"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		titleHeaderPanel.removeMessages(messages);
		if (mediaPanel != null) {
			mediaPanel.removeMessages(messages);
		}
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		titleHeaderPanel.setMessages(messages);
		if (mediaPanel != null) {
			mediaPanel.setMessages(messages);
		}
	}

	public JButton getCollapseButton() {
		return titleHeaderPanel.getCollapseButton();
	}

	public JButton getCloseButton() {
		return titleHeaderPanel.getCloseButton();
	}

}
