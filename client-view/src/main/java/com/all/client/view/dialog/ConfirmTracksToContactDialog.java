package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class ConfirmTracksToContactDialog extends AllDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel = null; // @jve:decl-index=0:visual-constraint="233,133"
	private JLabel messageLabel = null;
	private JLabel contactLabel = null;
	private JLabel questionLabel = null;
	private JCheckBox checkBox = null;
	private JPanel separatorPanel = null;
	private JButton cancelButton = null;
	private JButton continueButton = null;
	private boolean answer = false;
	private final String name;
	private ViewEngine viewEngine;

	public ConfirmTracksToContactDialog(Frame frame, Messages messages, String name, ViewEngine viewEngine) {
		super(frame, messages);
		this.name = name;
		this.viewEngine = viewEngine;

		initializeContentPane();
		setVisible(true);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("sendTrackToContact.title");
	}

	@Override
	public JPanel getContentComponent() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setSize(new Dimension(325, 149));
			contentPanel.setPreferredSize(new Dimension(325, 149));
			contentPanel.add(getMessageLabel(), null);
			contentPanel.add(getContactLabel(), null);
			contentPanel.add(getQuestionLabel(), null);
			contentPanel.add(getCheckBox(), null);
			contentPanel.add(getSeparatorPanel(), null);
			contentPanel.add(getCancelButton(), null);
			contentPanel.add(getContinueButton(), null);
		}
		return contentPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setText("JLabel");
			messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
			messageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			messageLabel.setBounds(new Rectangle(0, 20, 324, 12));
			messageLabel.setText(getMessages().getMessage("sendTrackToContact.message"));
		}
		return messageLabel;
	}

	private JLabel getContactLabel() {
		if (contactLabel == null) {
			contactLabel = new JLabel();
			contactLabel.setBounds(new Rectangle(0, 40, 324, 14));
			contactLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contactLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			contactLabel.setText(this.name);
			contactLabel.setName(SynthFonts.BOLD_ITALIC_FONT12_PURPLE32_0f_32);
		}
		return contactLabel;
	}

	private JLabel getQuestionLabel() {
		if (questionLabel == null) {
			questionLabel = new JLabel();
			questionLabel.setBounds(new Rectangle(0, 58, 324, 12));
			questionLabel.setHorizontalAlignment(SwingConstants.CENTER);
			questionLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			questionLabel.setText(getMessages().getMessage("sendTrackToContact.question"));
		}
		return questionLabel;
	}

	/**
	 * This method initializes checkBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckBox() {
		if (checkBox == null) {
			checkBox = new JCheckBox();
			checkBox.setBounds(new Rectangle(105, 85, 218, 18));
			checkBox.setText(getMessages().getMessage("sendTrackToContact.preference"));

			checkBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.sendValueAction(Actions.UserPreference.SET_SKIP_DRAG_CONTENT_TO_CONTACT_CONFIRMATION, Boolean
							.valueOf(checkBox.isSelected()));
				}
			});
		}
		return checkBox;
	}

	/**
	 * This method initializes separatorPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(new Rectangle(5, 108, 316, 2));
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	/**
	 * This method initializes cancelButton * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(new Rectangle(78, 117, 80, 22));
			cancelButton.setText(getMessages().getMessage("sendTrackToContact.cancelButton"));
			cancelButton.setName("buttonCancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes continueButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getContinueButton() {
		if (continueButton == null) {
			continueButton = new JButton();
			continueButton.setBounds(new Rectangle(168, 117, 80, 22));
			continueButton.setText(getMessages().getMessage("sendTrackToContact.continueButton"));
			continueButton.setName("buttonContinue");
			continueButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					answer = true;
					dispose();
				}
			});

		}
		return continueButton;
	}

	public boolean getAnswer() {
		return answer;
	}
}
