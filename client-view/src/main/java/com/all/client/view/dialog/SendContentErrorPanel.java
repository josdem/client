package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class SendContentErrorPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = -4058683391587099534L;
	private static final Rectangle ERROR_LABEL_BOUNDS = new Rectangle(72, 150, 296, 187);
	private static final Dimension SIZE = new Dimension(440, 453);
	private static final Rectangle OK_BUTTON_BOUNDS = new Rectangle((SIZE.width - 80) / 2, SIZE.height - 29, 80, 22);
	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, SIZE.height - 38, SIZE.width - 10, 2);
	private JPanel separatorPanel;
	private JButton okButton;
	private JLabel errorLabel;

	public SendContentErrorPanel() {
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setSize(SIZE);
		this.add(getErrorLabel());
		this.add(getSeparator());
		this.add(getOkButton());
	}

	private JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setBounds(ERROR_LABEL_BOUNDS);
			errorLabel.setHorizontalAlignment(JLabel.CENTER);
			errorLabel.setVerticalAlignment(JLabel.TOP);
			errorLabel.setText("ERROR");
		}
		return errorLabel;
	}

	private JPanel getSeparator() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setName("buttonOk");
			okButton.setText("Ok");
			okButton.setBounds(OK_BUTTON_BOUNDS);
		}
		return okButton;
	}

	@Override
	public void internationalize(Messages messages) {
		getErrorLabel().setText(messages.getMessage("sendContent.sendContentError"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}
