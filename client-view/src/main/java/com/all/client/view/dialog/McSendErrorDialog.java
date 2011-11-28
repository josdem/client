package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class McSendErrorDialog extends AllDialog {

	private static final long serialVersionUID = 8817173405309674745L;
	private static final String DEFAULT_TEXT_BUTTON_KEY = "sendContent.error.ok.button";
	private static final String DEFAULT_TITLE_KEY = "sendContent.error.title";
	private static final String ICONS_WARNING_BIG_RED = "icons.warningBig";
	private static final String WARNING_PANEL_NAME = "grayRoundedBorderPanel";
	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final Dimension DEFAULT_SIZE = new Dimension(390, 171);
	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(
			390, 171);
	private JPanel contentComponent;
	private JPanel warningPanel;
	private JLabel warningIconLabel;
	private JLabel errorLabel;
	private JPanel panelSeparator;
	private JLabel messageLabel;
	private JButton okButton;
	private final String error;
	private final String errorDetail;
	private final String title;
	private final String textButton;

	public McSendErrorDialog(JFrame frame, Messages messages, String error,
			String errorDetail) {
		this(frame, messages, error, errorDetail, messages
				.getMessage(DEFAULT_TITLE_KEY), messages
				.getMessage(DEFAULT_TEXT_BUTTON_KEY));
	}

	public McSendErrorDialog(JFrame frame, Messages messages, String errorKey,
			String errorDetailKey, String titleKey, String textButton) {
		super(frame, messages);
		this.error = errorKey;
		this.errorDetail = errorDetailKey;
		this.title = titleKey;
		this.textButton = textButton;
		this.setModal(true);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.initializeContentPane();
	}

	@Override
	JComponent getContentComponent() {
		if (contentComponent == null) {
			contentComponent = new JPanel();
			contentComponent.setSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setPreferredSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setMinimumSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setMaximumSize(CONTENT_COMPONENT_DEFAULT_SIZE);
			contentComponent.setLayout(null);
			contentComponent.add(getErrorLabel());
			contentComponent.add(getWarningPanel());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getOkButton());
		}
		return contentComponent;
	}

	private JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel(error);
			errorLabel.setName(SynthFonts.BOLD_ITALIC_FONT14_PURPLE49_19_49);
			errorLabel.setBounds(12, 0, 364, 47);
			errorLabel.setHorizontalAlignment(JLabel.CENTER);
			errorLabel.setHorizontalTextPosition(JLabel.CENTER);
		}
		return errorLabel;
	}

	private JPanel getWarningPanel() {
		if (warningPanel == null) {
			warningPanel = new JPanel();
			warningPanel.setLayout(null);
			warningPanel.setName(WARNING_PANEL_NAME);
			warningPanel.setBounds(12, 47, 364, 76);
			warningPanel.add(getMessageLabel());
			warningPanel.add(getWarningIcon());
		}
		return warningPanel;
	}

	private JLabel getWarningIcon() {
		if (warningIconLabel == null) {
			warningIconLabel = new JLabel();
			warningIconLabel.setBounds(16, 20, 40, 35);
			Icon icon = UIManager.getDefaults().getIcon(ICONS_WARNING_BIG_RED);
			warningIconLabel.setIcon(icon);
		}
		return warningIconLabel;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel(errorDetail);
			messageLabel.setBounds(75, 4, 283, 68);
		}
		return messageLabel;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setBounds(new Rectangle(4, 131, 380, 2));
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
		}
		return panelSeparator;
	}

	public JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setName("buttonCancel");
			okButton.setText(textButton);
			okButton.setBounds(154, 140, 80, 22);
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}

	@Override
	String dialogTitle(Messages messages) {
		return title;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

}
