package com.all.client.view.dialog;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.all.i18n.Messages;

public final class CloseApplicationDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private static final Icon WARNING_LABEL_ICON = UIManager.getDefaults().getIcon("icons.warningBig");

	private static final Rectangle CONTENT_COMPONENT_BOUNDS = new Rectangle(0, 0, 390, 171);

	private static final Rectangle INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(16, 0, 320, 80);

	private static final Rectangle NO_CLOSE_BUTTON_BOUNDS = new Rectangle(100, 140, 80, 22);

	private static final Rectangle PANEL_BACKGROUND_BOUNDS = new Rectangle(13, 47, 363, 76);

	private static final Rectangle PANEL_SEPARATOR_BOUNDS = new Rectangle(13, 133, 363, 2);

	private static final Rectangle QUESTION_LABEL_BOUNDS = new Rectangle(20, 10, 360, 30);

	private static final Rectangle YES_CLOSE_BUTTON_BOUNDS = new Rectangle(200, 140, 80, 22);

	private static final String PANEL_BACKGROUND_NAME = "selectedContactInfoPanel";

	private static final String PANEL_SEPARATOR_NAME = "bottomPanelSeparator";

	private JButton noCloseButton;

	private JButton yesCloseButton;

	private JLabel instructionsLabel;

	private JLabel questionLabel;

	private JPanel contentComponent;

	private JPanel panelBackground;

	private JPanel panelSeparator;

	private boolean closing = false;
	
	public CloseApplicationDialog(Frame frame, Messages messages) {
		super(frame, messages);
		initializeContentPane();
		internationalizeDialog(messages);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("close.app.confirmation.dialog.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentContainer();
	}

	private JPanel getContentContainer() {
		if (contentComponent == null) {
			contentComponent = new JPanel();
			contentComponent.setLayout(null);
			contentComponent.setBounds(CONTENT_COMPONENT_BOUNDS);
			contentComponent.add(getQuestionLabel());
			contentComponent.add(getPanelBackground());
			contentComponent.add(getPanelSeparator());
			contentComponent.add(getNoCloseButton());
			contentComponent.add(getYesCloseButton());
		}
		return contentComponent;
	}

	private JPanel getPanelSeparator() {
		if (panelSeparator == null) {
			panelSeparator = new JPanel();
			panelSeparator.setName(PANEL_SEPARATOR_NAME);
			panelSeparator.setBounds(PANEL_SEPARATOR_BOUNDS);
		}
		return panelSeparator;
	}

	private JLabel getQuestionLabel() {
		if (questionLabel == null) {
			questionLabel = new JLabel();
			questionLabel.setBounds(QUESTION_LABEL_BOUNDS);
			questionLabel.setName("purpleDialogBold12");
		}
		return questionLabel;
	}

	private JPanel getPanelBackground() {
		if (panelBackground == null) {
			panelBackground = new JPanel();
			panelBackground.setLayout(null);
			panelBackground.setName(PANEL_BACKGROUND_NAME);
			panelBackground.setBounds(PANEL_BACKGROUND_BOUNDS);
			panelBackground.add(getInstructionsLabel());
		}
		return panelBackground;
	}

	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			instructionsLabel.setIcon(WARNING_LABEL_ICON);
			instructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
			instructionsLabel.setIconTextGap(16);
		}
		return instructionsLabel;
	}

	private JButton getNoCloseButton() {
		if (noCloseButton == null) {
			noCloseButton = new JButton();
			noCloseButton.setBounds(NO_CLOSE_BUTTON_BOUNDS);
			noCloseButton.setName("buttonNo");
			noCloseButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					closing = false;
					closeDialog();
				}
			});
		}
		return noCloseButton;
	}
	

	public boolean isClosing() {
		return closing;
	}
	
	private JButton getYesCloseButton() {
		if (yesCloseButton == null) {
			yesCloseButton = new JButton();
			yesCloseButton.setBounds(YES_CLOSE_BUTTON_BOUNDS);
			yesCloseButton.setName("buttonYes");
			yesCloseButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					closing = true;
					closeDialog();
				}
			});

		}
		return yesCloseButton;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		questionLabel.setText(messages.getMessage("close.app.confirmation.dialog.question"));
		instructionsLabel.setText(messages.getMessage("close.app.confirmation.dialog.instructions"));
		yesCloseButton.setText(messages.getMessage("close.app.confirmation.dialog.closeButton"));
		noCloseButton.setText(messages.getMessage("close.app.confirmation.dialog.noCloseButton"));
	}
}
