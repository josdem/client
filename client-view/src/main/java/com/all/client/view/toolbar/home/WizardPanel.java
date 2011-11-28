package com.all.client.view.toolbar.home;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.wizard.WizardDialog;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.stats.usage.UserActions;

public final class WizardPanel extends JScrollPane implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Dimension MINIMUM_SIZE = new Dimension(397, 138);

	private static final Dimension MAXIMUM_SIZE = new Dimension(497, 230);

	private static final Rectangle WIZARD_BUTTON_BOUNDS = new Rectangle(188, 36, 210, 68);

	private static final Rectangle WIZARD_TITLE_BOUNDS = new Rectangle(158, 0, 262, 36);

	private static final Rectangle WIZARD_INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(158, 104, 262, 46);

	private static final String MAIN_PANEL_NAME = "wizardCrawlerHomeBackgroundPanel";

	private static final String WIZARD_BUTTON_NAME = "wizardHomeButton";

	private JPanel mainPanel;

	private JButton wizardButton;

	private JLabel wizardTitleLabel;

	private JTextPane wizardInstructionsLabel;

	private final DialogFactory dialogFactory;

	private final ViewEngine viewEngine;

	public WizardPanel(DialogFactory dialogFactory, ViewEngine viewEngine) {
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setMinimumSize(MINIMUM_SIZE);
		this.setPreferredSize(MINIMUM_SIZE);
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setViewportView(getMainPanel());
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(null);
			mainPanel.setPreferredSize(MAXIMUM_SIZE);
			mainPanel.setName(MAIN_PANEL_NAME);
			mainPanel.add(getWizardTitleLabel());
			mainPanel.add(getWizardButton());
			mainPanel.add(getItunesInstructionsLabel());
		}
		return mainPanel;
	}

	private JButton getWizardButton() {
		if (wizardButton == null) {
			wizardButton = new JButton();
			wizardButton.setBounds(WIZARD_BUTTON_BOUNDS);
			wizardButton.setName(WIZARD_BUTTON_NAME);
			wizardButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Wizard.REOPEN_WIZARD);
					WizardDialog wizardDialog = dialogFactory.getWizardDialog();
					wizardDialog.setVisible(true);
				}
			});
		}
		return wizardButton;
	}

	private JLabel getWizardTitleLabel() {
		if (wizardTitleLabel == null) {
			wizardTitleLabel = new JLabel();
			wizardTitleLabel.setBounds(WIZARD_TITLE_BOUNDS);
			wizardTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			wizardTitleLabel.setName(SynthFonts.BOLD_FONT16_PURPLE_70_40_90);
		}
		return wizardTitleLabel;
	}

	private JTextPane getItunesInstructionsLabel() {
		if (wizardInstructionsLabel == null) {
			wizardInstructionsLabel = new JTextPane();
			wizardInstructionsLabel.setBounds(WIZARD_INSTRUCTIONS_LABEL_BOUNDS);
			wizardInstructionsLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE70_40_90);
			wizardInstructionsLabel.setEditable(false);
			wizardInstructionsLabel.setEnabled(false);
			StyledDocument doc = wizardInstructionsLabel.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}
		return wizardInstructionsLabel;
	}

	@Override
	public void internationalize(Messages messages) {
		wizardTitleLabel.setText(messages.getMessage("home.wizard.title"));
		wizardInstructionsLabel.setText(messages.getMessage("home.wizard.instructions"));
		wizardButton.setText(messages.getMessage("home.wizard.button"));
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}
}
