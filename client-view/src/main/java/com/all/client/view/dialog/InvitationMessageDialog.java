package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;
import com.all.shared.messages.FriendshipRequestStatus;

public class InvitationMessageDialog extends AllDialog {

	private static final Dimension MESSAGE_PANEL_SIZE = new Dimension(380, 107);

	private static final Dimension CONTENT_PANEL_PREFERRED_SIZE = new Dimension(385, 150);

	private static final long serialVersionUID = 1L;

	// private static Log log = LogFactory.getLog(InvitationMessageDialog.class);

	private JPanel contentPanel;

	private JPanel messagePanel;

	private final FriendshipRequestStatus addFriendResult;

	public InvitationMessageDialog(Frame frame, Messages messages, FriendshipRequestStatus addFriendResult) {
		super(frame, messages);
		this.addFriendResult = addFriendResult;
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("sendInvitation.title");
	}

	@Override
	JComponent getContentComponent() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(new BorderLayout());
			contentPanel.add(getMessagePanel(), BorderLayout.CENTER);
			contentPanel.add(getButtonPanel(), BorderLayout.SOUTH);
			contentPanel.setPreferredSize(CONTENT_PANEL_PREFERRED_SIZE);
		}
		return contentPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel();
			messagePanel.setLayout(null);
			messagePanel.setSize(MESSAGE_PANEL_SIZE);
			String message = getMessages().getMessage(addFriendResult.getMainMessage());
			// String message = "Friend request was sent!";
			JLabel mainMessageLabel = new JLabel(message);
			mainMessageLabel.setBounds(0, 22, messagePanel.getWidth(), 25);
			mainMessageLabel.setHorizontalAlignment(JLabel.CENTER);
			mainMessageLabel.setName(SynthFonts.BOLD_FONT16_PURPLE50_15_50);
			JLabel descriptionLabel = new JLabel(getMessages().getMessage(addFriendResult.getDescription()));
			descriptionLabel.setBounds(0, 55, messagePanel.getWidth(), 40);
			descriptionLabel.setName(SynthFonts.PLAIN_FONT12_GRAY80_80_80);
			descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
			messagePanel.add(mainMessageLabel);
			messagePanel.add(descriptionLabel);
		}

		return messagePanel;
	}

}
