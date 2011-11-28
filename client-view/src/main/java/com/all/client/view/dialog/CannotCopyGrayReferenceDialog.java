package com.all.client.view.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.i18n.Messages;

public class CannotCopyGrayReferenceDialog extends AllDialog {
	private static final long serialVersionUID = 1L;
	private static final Dimension DEFAULT_SIZE = new Dimension(324, 145);

	private static final Dimension CONTENT_COMPONENT_DEFAULT_SIZE = new Dimension(324, 145);

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(5, 103, 313, 2);

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private static final String WARNING_PANEL_NAME = "grayRoundedBorderPanel";

	private static final Rectangle CHECK_BOX_BOUNDS = new Rectangle(30, 79, 280, 16);

	private JPanel contentComponent = null;
	private JPanel panelSeparator = null;
	private JLabel messageLabel = null;
	private JButton okButton = null;
	private JLabel warningLabel = null;
	private JPanel warningPanel = null;
	private JCheckBox checkBox = null;
	private final ViewEngine viewEngine;

	public CannotCopyGrayReferenceDialog(Dialog parent, Messages messages, ViewEngine viewEngine) {
		super(parent, messages);
		this.viewEngine = viewEngine;
		this.setModal(true);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.initializeContentPane();
		getOkButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CannotCopyGrayReferenceDialog that = CannotCopyGrayReferenceDialog.this;
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
			contentComponent.add(getWarningPanel());
			contentComponent.add(getCheckBox());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getOkButton());
		}
		return contentComponent;
	}

	private JPanel getWarningPanel() {
		if (warningPanel == null) {
			warningPanel = new JPanel();
			warningPanel.setLayout(null);
			warningPanel.setName(WARNING_PANEL_NAME);
			warningPanel.setBounds(12, 12, 300, 55);
			warningPanel.add(getWarningIcon());
			warningPanel.add(getMessageLabel());
		}

		return warningPanel;
	}

	public JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton("OK");
			okButton.setBounds(122, 112, 80, 22);
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
			messageLabel = new JLabel(getMessages().getMessage("devices.warning.grayReference"));
			messageLabel.setBounds(60, 5, 225, 45);
		}
		return messageLabel;
	}

	private JLabel getWarningIcon() {
		if (warningLabel == null) {
			warningLabel = new JLabel();
			warningLabel.setBounds(10, 10, 40, 35);
			Icon icon = UIManager.getDefaults().getIcon("icons.warningBig");
			warningLabel.setIcon(icon);
		}
		return warningLabel;
	}

	private JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox();
			checkBox.setBounds(CHECK_BOX_BOUNDS);
			checkBox.setText(getMessages().getMessage("devices.warning.grayReferencePref"));
			checkBox.setIconTextGap(8);

			final boolean grayReferencesWarningSkip = viewEngine.get(Model.UserPreference.SKIP_COPY_REFERENCES_TO_USB_WARN);
			checkBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.sendValueAction(Actions.UserPreference.SET_SKIP_COPY_REFERENCES_TO_USB_WARN, Boolean
							.valueOf(!grayReferencesWarningSkip));
				}
			});
		}
		return checkBox;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

}
