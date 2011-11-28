package com.all.client.view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.all.core.common.view.SynthFonts;
import com.all.core.model.ContactCollection;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class SendContentConfirmationPanel extends JPanel implements Internationalizable{
	
	private static final long serialVersionUID = 1L;
	private static final Dimension SIZE = new Dimension(440, 453);
	private JPanel grayPanel;
	private JLabel okLabel;
	private JLabel confirmationMessage;
	private JPanel separatorPanel;
	private JButton okButton;
	private JPanel contactsPanel;
	private Icon contactIcon = UIManager.getDefaults().getIcon("contactTree.leafOnlineIcon");
	private JLabel contactLabel;

	public SendContentConfirmationPanel(ContactCollection contacts) {
		initialize();
		updateContacts(contacts);
	}
	
	private void initialize() {
		setLayout(null);
		setSize(SIZE);
		add(getSuccessIcon());
		add(getConfirmationMessageLabel());
		add(getGrayPanel());
		add(getSeparator());
		add(getOkButton());
	}


	private JPanel getSeparator() {
		if(separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setBounds(5, SIZE.height-38, SIZE.width - 10, 2);
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setName("buttonOk");
			okButton.setText("Ok");
			okButton.setBounds((SIZE.width-80)/2, SIZE.height - 29, 80,22);
		}
		return okButton;
	}

	private JPanel getGrayPanel() {
		if(grayPanel == null) {
			grayPanel = new JPanel(null);
			grayPanel.setName("sendContentBackgroundPanel");
			grayPanel.setBounds((SIZE.width - 172)/2, 139, 172, 247);
			
			contactLabel = new JLabel();
			contactLabel.setText("Contacts");
			contactLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contactLabel.setName("myContactsLabel");
			contactLabel.setBounds(6, 6, 160, 18);
			
			grayPanel.add(contactLabel);
			grayPanel.add(getContactsPanel());
		}
		return grayPanel;
	}

	private Component getContactsPanel() {
		if(contactsPanel ==null) {
			contactsPanel = new JPanel();
			contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
			contactsPanel.setBounds(16, 25, 142, 200);
		}
		return contactsPanel;
	}

	private JLabel createContactLabel(String name) {
		JLabel contactLabel = new JLabel(name, contactIcon, JLabel.LEFT);
		Dimension size = new Dimension(160,20);
		contactLabel.setSize(size);
		contactLabel.setPreferredSize(size);
		return contactLabel;
	}

	private JLabel getConfirmationMessageLabel() {
		if (confirmationMessage == null ){
			confirmationMessage = new JLabel();
			confirmationMessage.setName(SynthFonts.BOLD_FONT14_GRAY77_77_77);
			confirmationMessage.setText("<html><center>Your content was succesfully sent<br> to these contacts.</center></html>");
			confirmationMessage.setHorizontalAlignment(SwingConstants.CENTER);
			confirmationMessage.setBounds((SIZE.width - 262)/2, 79, 262, 35);
		}
		return confirmationMessage;
	}

	private JLabel getSuccessIcon() {
		if (okLabel == null) {
			okLabel = new JLabel();
			okLabel.setName("sendContentSuccessLabel");
			okLabel.setBounds((SIZE.width-36)/2, 25, 36, 36);			
		}
		return okLabel;
	}

	public void updateContacts(ContactCollection contacts) {
		contactsPanel.removeAll();
		for(ContactInfo contact: contacts.getContacts()) {
			contactsPanel.add(createContactLabel(contact.getNickName()));
		}		
	}

	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void internationalize(Messages messages) {
		getOkButton().setText(messages.getMessage("ok"));
		contactLabel.setText(messages.getMessage("sendContent.contactList"));
		getConfirmationMessageLabel().setText(messages.getMessage("sendContent.confirmation"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	public void addActionListenerToOkButton(ActionListener actionListener) {
		getOkButton().addActionListener(actionListener);
	}

}
