package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.i18n.Messages;

public class DeleteUsbContentDialog extends AllDialog {

	private static final long serialVersionUID = -3598897598031388130L;

	private static final Dimension DEFAULT_SIZE = new Dimension(324, 110);

	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(324, 110);

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(5, 71, 313, 2);

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private static final String WARNING_PANEL_NAME = "grayRoundedBorderPanel";

	private JPanel contentComponent = null;
	private JPanel panelSeparator = null;
	private JLabel messageLabel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private JLabel warningIconLabel = null;
	private JPanel warningPanel = null;

	private boolean deleteFiles = false;

	public DeleteUsbContentDialog(Frame frame, Messages messages) {
		super((JFrame) frame, messages);
		this.setModal(true);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.initializeContentPane();
	}

	public boolean getUserSelection() {
		return deleteFiles;
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
			contentComponent.add(getWarningPanel());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getOkButton());
			contentComponent.add(getCancelButton());
		}
		return contentComponent;
	}

	private JPanel getWarningPanel() {
		if (warningPanel == null) {
			warningPanel = new JPanel();
			warningPanel.setLayout(null);
			warningPanel.setName(WARNING_PANEL_NAME);
			warningPanel.setBounds(12, 8, 300, 55);
			warningPanel.add(getWarningIcon());
			warningPanel.add(getMessageLabel());
		}

		return warningPanel;
	}

	private JLabel getWarningIcon() {
		if (warningIconLabel == null) {
			warningIconLabel = new JLabel();
			warningIconLabel.setBounds(10, 10, 40, 35);
			Icon icon = UIManager.getDefaults().getIcon("icons.warningBig");
			warningIconLabel.setIcon(icon);
		}
		return warningIconLabel;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel(getMessages().getMessage("devices.delete.warning"));
			messageLabel.setBounds(64, 5, 222, 40);
		}
		return messageLabel;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setBounds(PANEL_SEPARATOR_BOUNDS);
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
		}
		return panelSeparator;
	}

	public JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setName("buttonCancel");
			okButton.setText(getMessages().getMessage("devices.delete.button"));
			okButton.setBounds(167, 80, 80, 22);
			okButton.addActionListener(new CloseListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					deleteFiles = true;
					super.actionPerformed(e);
				}
			});
		}
		return okButton;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName("buttonCancel");
			cancelButton.setText(getMessages().getMessage("devices.copy.cancel"));
			cancelButton.setBounds(77, 80, 80, 22);
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("devices.delete.title");
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

}
