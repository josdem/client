package com.all.client.view;

import org.springframework.stereotype.Component;

import com.all.i18n.Messages;

@Component
public class AlertProgressBottomPanel extends ProgressBottomPanel{
	private static final long serialVersionUID = 1L;

	
	public AlertProgressBottomPanel() {
		super("alertReceivedIcon");
	}

	@Override
	public void internationalize(Messages messages) {
		getLabel().setText(messages.getMessage("bottomPanel.alertProgress"));
	}
}
