package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.all.i18n.Messages;

public class ErrorDialog extends AbstractErrorDialog {

	private static final int VGAP = 7;
	private static final int HGAP = 0;
	private static final Dimension DEFAULT_OK_BUTTON_SIZE = new Dimension(80, 22);
	private static final long serialVersionUID = 1L;

	public ErrorDialog(Frame frame, Messages messages, String errorKey, String... parameters) {
		super(frame, messages, errorKey, parameters);
	}

	private JPanel buttonPanel;

	protected JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, HGAP, VGAP));
			JButton okButton = new JButton();
			okButton.setName("buttonOk");
			okButton.setText(getMessages().getMessage("ok"));
			okButton.setPreferredSize(DEFAULT_OK_BUTTON_SIZE);
			okButton.addActionListener(new CloseListener());
			buttonPanel.add(okButton);
		}
		return buttonPanel;
	}

}
