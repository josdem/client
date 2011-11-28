package com.all.client.view.dialog;

import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.all.i18n.Messages;

public class NewAccountPrivacyDialog extends AllDialog {
	private static final String CREATE_NEW_ACCOUNT_PRIVACY_OK_MESSAGE = "createNewAccountPrivacy.ok";

	private static final String CREATE_NEW_ACCOUNT_PRIVACY_MESSAGE = "createNewAccountPrivacy.message";

	private static final String BUTTON_OK_NAME = "buttonOk";

	private static final String BOTTOM_PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private static final String PRIVACY_TEXT_AREA_NAME = "privacyTextArea";

	private static final Rectangle OK_BUTTON_BOUNDS = new Rectangle(122, 200, 80, 22);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 191, 316, 1);

	private static final Rectangle SCROLLPANE_BOUNDS = new Rectangle(12, 16, 302, 170);

	private static final Rectangle CONTENT_BOUNDS = new Rectangle(0, 0, 326, 230);

	private static final long serialVersionUID = -5918257652673988187L;

	private JPanel content = null;
	private JTextArea privacyTextArea = null;
	private JPanel separatorPanel = null;
	private JButton okButton = null;
	private JScrollPane scrollPane = null;

	public NewAccountPrivacyDialog(JFrame frame, Messages messages) {
		super(frame, messages);
		this.initializeContentPane();
		setVisible(true);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("createNewAccountPrivacy.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContent();
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JPanel getContent() {
		if (content == null) {
			content = new JPanel();
			content.setLayout(null);
			content.setBounds(CONTENT_BOUNDS);
			content.add(getScrollPane(), null);
			content.add(getSeparatorPanel(), null);
			content.add(getOkButton(), null);
		}
		return content;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			privacyTextArea = new JTextArea();
			privacyTextArea.setText(getMessages().getMessage(CREATE_NEW_ACCOUNT_PRIVACY_MESSAGE));
			privacyTextArea.setEnabled(false);
			privacyTextArea.setLineWrap(true);
			privacyTextArea.setWrapStyleWord(true);
			privacyTextArea.setOpaque(false);
			privacyTextArea.setName(PRIVACY_TEXT_AREA_NAME);
			scrollPane = new JScrollPane(privacyTextArea);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setBounds(SCROLLPANE_BOUNDS);
			scrollPane.setOpaque(false);
		}
		return scrollPane;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setName(BOTTOM_PANEL_SEPARATOR_NAME);
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
		}
		return separatorPanel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(getMessages().getMessage(CREATE_NEW_ACCOUNT_PRIVACY_OK_MESSAGE));
			okButton.setName(BUTTON_OK_NAME);
			okButton.setBounds(OK_BUTTON_BOUNDS);
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}

}
