package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class CancelUploadDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private static final String ICONS_WARNING_BIG_RED = "icons.warningBig";
	private static final String WARNING_PANEL_NAME = "grayRoundedBorderPanel";
	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";
	private static final Dimension DEFAULT_SIZE = new Dimension(390, 171);
	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(390, 171);
	private JPanel contentComponent;
	private JPanel warningPanel;
	private JLabel warningIconLabel;
	private JLabel errorLabel;
	private JPanel panelSeparator;
	private JLabel messageTextPane;
	private JButton okButton;
	private JButton dontButton;
	private String error;
	private String errorDetail;
	private String title;
	private String textYesButton;
	private String textCancelButton;

	private boolean response;

	//as this dialog is modal, it's safe if we sent the string values directly
	public CancelUploadDialog(JFrame frame, Messages messages, String error, String errorDetail, String title,
			String textYesButton, String textCancelButton) {
		super(frame, messages);
		this.error = error;
		this.errorDetail = errorDetail;
		this.title = title;
		this.textYesButton = textYesButton;
		this.textCancelButton = textCancelButton;
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
			contentComponent.add(getDontButton());
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
		if (messageTextPane == null) {
			messageTextPane = new JLabel();
			messageTextPane.setBounds(75, 4, 283, 68);
			messageTextPane.setText(errorDetail);
		}
		return messageTextPane;
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
			okButton.setText(textYesButton);
			okButton.setBounds(200, 140, 80, 22);
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					response = true;
					closeDialog();
				}
			});
		}
		return okButton;
	}

	public JButton getDontButton() {
		if (dontButton == null) {
			dontButton = new JButton();
			dontButton.setName("buttonCancel");
			dontButton.setText(textCancelButton);
			dontButton.setBounds(100, 140, 80, 22);
			dontButton.addActionListener(new CloseListener());
		}
		return dontButton;
	}

	@Override
	String dialogTitle(Messages messages) {
		return title;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	public boolean getResponse() {
		return response;
	}

}
