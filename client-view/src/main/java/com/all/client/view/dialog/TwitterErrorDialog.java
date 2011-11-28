package com.all.client.view.dialog;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.i18n.Messages;

public class TwitterErrorDialog extends AllDialog {

	private static final int NOT_FOUND = 404;
	private static final int FORBIDDEN = 403;
	private static final int UNAUTHORIZED = 401;

	private static final long serialVersionUID = 1L;

	private static final Rectangle CONTENT_PANEL_BOUNDS = new Rectangle(0, 0, 390, 214);

	private static final Rectangle ERROR_PANEL_BOUNDS = new Rectangle(1, 1, 387, 170);

	private static final String ERROR_PANEL_NAME = "twitterErrorBackgroundPanel";

	private static final Rectangle ERROR_ICON_LABEL_BOUNDS = new Rectangle(30, 105, 39, 35);

	private static final String ERROR_ICON_LABEL_NAME = "icons.warningBig";

	private static final Rectangle MESSAGE_LABEL_BOUNDS = new Rectangle(85, 70, 300, 100);

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(155, 184, 80, 22);

	private static final String BUTTON_NAME = "buttonOk";

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 176, 379, 2);

	private static final String SEPARATOR_PANEL_NAME = "bottomPanelSeparator";

	private JPanel contentPanel;

	private JPanel errorPanel;

	private JLabel errorIconLabel;

	private JLabel messageLabel;

	private JButton closeButton;

	private JPanel separatorPanel;

	private final int errorCode;

	public TwitterErrorDialog(Frame frame, Messages messages, int errorCode) {
		super(frame, messages);
		this.errorCode = errorCode;
		initializeContentPane();
		internationalizeDialog(messages);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("twitter.dialog.error.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(CONTENT_PANEL_BOUNDS);
			contentPanel.add(getErrorPanel());
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCloseButton());
		}
		return contentPanel;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setBounds(CANCEL_BUTTON_BOUNDS);
			closeButton.setName(BUTTON_NAME);
			closeButton.addActionListener(new CloseListener());
		}
		return closeButton;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
		}
		return separatorPanel;
	}

	private JPanel getErrorPanel() {
		if (errorPanel == null) {
			errorPanel = new JPanel();
			errorPanel.setLayout(null);
			errorPanel.setBounds(ERROR_PANEL_BOUNDS);
			errorPanel.setName(ERROR_PANEL_NAME);
			errorPanel.add(getErrorIconLabel());
			errorPanel.add(getMessageLabel());
		}
		return errorPanel;
	}

	private JLabel getErrorIconLabel() {
		if (errorIconLabel == null) {
			errorIconLabel = new JLabel();
			errorIconLabel.setBounds(ERROR_ICON_LABEL_BOUNDS);
			Icon icon = UIManager.getDefaults().getIcon(ERROR_ICON_LABEL_NAME);
			errorIconLabel.setIcon(icon);
		}
		return errorIconLabel;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setBounds(MESSAGE_LABEL_BOUNDS);
		}
		return messageLabel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		String errorMessage = null;
		switch (errorCode) {
		case NOT_FOUND:
			errorMessage = messages.getMessage("twitter.dialog.error.notfound");
			break;
		case FORBIDDEN:
			errorMessage = messages.getMessage("twitter.dialog.error.forbidden");
			break;
		case UNAUTHORIZED:
			errorMessage = messages.getMessage("twitter.dialog.error.unauthorized");
			break;
		default:
			errorMessage = messages.getMessage("twitter.dialog.error.default");
		}
		getMessageLabel().setText(errorMessage);
		getCloseButton().setText(messages.getMessage("ok"));
	}

}
