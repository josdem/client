package com.all.client.view.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.i18n.Messages;

public class NotEnoughSpaceDialog extends AllDialog {

	private static final long serialVersionUID = -7034360161222928916L;

	private static final Dimension DEFAULT_SIZE = new Dimension(324, 90);

	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(324, 90);

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(5, 51, 313, 2);

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private JPanel contentComponent = null;

	private JPanel panelSeparator = null;

	private JLabel messageLabel = null;

	private JButton okButton = null;

	private JLabel warningLabel = null;

	public NotEnoughSpaceDialog(Dialog parent, Messages messages) {
		super(parent, messages);
		this.setModal(true);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.initializeContentPane();
		getOkButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NotEnoughSpaceDialog that = NotEnoughSpaceDialog.this;
				that.dispatchEvent(new WindowEvent(that, WindowEvent.WINDOW_CLOSING));
			}
		});
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("devices.warning.title");
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
			contentComponent.add(getWarningIcon());
			contentComponent.add(getMessageLabel());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getOkButton());
		}
		return contentComponent;
	}

	public JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.setBounds(122, 60, 80, 22);
			okButton.setName("buttonCancel");
		}
		return okButton;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setBounds(PANEL_SEPARATOR_BOUNDS);
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
		}
		return panelSeparator;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel(getMessages().getMessage("devices.warning.notEnoughSpace"));
			messageLabel.setBounds(80, 8, 234, 35);
		}
		return messageLabel;
	}

	private JLabel getWarningIcon() {
		if (warningLabel == null) {
			warningLabel = new JLabel();
			warningLabel.setBounds(20, 8, 40, 35);
			Icon icon = UIManager.getDefaults().getIcon("icons.warningBig");
			warningLabel.setIcon(icon);
		}
		return warningLabel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

}
