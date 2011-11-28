package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

public class NetworkConnectionMessageDialog extends AllDialog {

	private final String titleKey;

	private final String messageKey;

	private static final Dimension MESSAGE_PANEL_SIZE = new Dimension(387, 132);

	private static final Dimension CONTENT_PANEL_PREFERRED_SIZE = new Dimension(390, 173);

	private static final long serialVersionUID = 1L;

	// private static Log log = LogFactory.getLog(InvitationMessageDialog.class);

	private JPanel contentPanel;

	private JPanel messagePanel;

	private final String subtitleKey;

	public NetworkConnectionMessageDialog(Frame frame, Messages messages, String titleKey, String subtitleKey, String messageKey) {
		super(frame, messages);
		this.subtitleKey = subtitleKey;
		this.messageKey = messageKey;
		this.titleKey = titleKey;
		initializeContentPane();
	}
	
	public NetworkConnectionMessageDialog(Dialog dialog, Messages messages, String titleKey, String subtitleKey, String messageKey) {
		super(dialog, messages);
		this.subtitleKey = subtitleKey;
		this.messageKey = messageKey;
		this.titleKey = titleKey;
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage(titleKey);
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
			String message = getMessages().getMessage(subtitleKey);
			JLabel mainMessageLabel = new JLabel(message);
			mainMessageLabel.setBounds(0, 22, messagePanel.getWidth(), 25);
			mainMessageLabel.setHorizontalAlignment(JLabel.CENTER);
			mainMessageLabel.setName(SynthFonts.BOLD_ITALIC_FONT15_PURPLE73_25_73);

			JLabel descriptionLabel = new JLabel(getMessages().getMessage(messageKey));
			descriptionLabel.setBounds(0, 55, messagePanel.getWidth(), 53);
			descriptionLabel.setName(SynthFonts.PLAIN_FONT12_GRAY80_80_80);
			descriptionLabel.setHorizontalAlignment(JLabel.CENTER);
			messagePanel.add(mainMessageLabel);
			messagePanel.add(descriptionLabel);
		}

		return messagePanel;
	}

}
