package com.all.client.view.dialog;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.i18n.Messages;

public class InfoDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private static final Rectangle OK_BUTTON_BOUNDS = new Rectangle(123, 81, 80, 22);
	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 72, 316, 2);
	private static final Rectangle CONTENT_PANEL_BOUNDS = new Rectangle(0, 0, 326, 111);
	private static final Rectangle INFO_LABEL_BOUNDS = new Rectangle(5, 5, 316, 62);
	
	private JPanel contentPanel = null;
	private JLabel infoLabel = null;
	private JPanel separatorPanel = null;
	private JButton okButton = null;

	private String messageKey;
	private String titleKey;

	public InfoDialog(Frame frame, Messages messages, String messageKey, String titleKey) {
		super(frame, messages);
		this.messageKey = messageKey;
		this.titleKey = titleKey;
		initializeContentPane();
	}
	public InfoDialog(Dialog dialog, Messages messages, String messageKey, String titleKey) {
		super(dialog, messages);
		this.messageKey = messageKey;
		this.titleKey = titleKey;
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		if (titleKey == null) {
			return messages.getMessage("infodialog.title");
		}
		return messages.getMessage(titleKey);
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			infoLabel = new JLabel();
			infoLabel.setText(getMessages().getMessage(messageKey));
			infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
			infoLabel.setBounds(INFO_LABEL_BOUNDS);
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(CONTENT_PANEL_BOUNDS);
			contentPanel.add(infoLabel, null);
			contentPanel.add(getSeparatorPanel(), null);
			contentPanel.add(getOkButton(), null);
		}
		return contentPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setBounds(OK_BUTTON_BOUNDS);
			okButton.setText(getMessages().getMessage("infodialog.okButton"));
			okButton.setName("buttonOk");
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}

}
