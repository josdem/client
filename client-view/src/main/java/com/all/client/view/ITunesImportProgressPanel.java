package com.all.client.view;

import org.springframework.stereotype.Component;

import com.all.i18n.Messages;

@Component
public class ITunesImportProgressPanel extends ProgressBottomPanel {
	private static final long serialVersionUID = 1L;

	public ITunesImportProgressPanel() {
		super("importITunesIcon");
	}

	@Override
	public void internationalize(Messages messages) {
		getLabel().setText(messages.getMessage("bottomPanel.importITunesProgress"));
	}
}
