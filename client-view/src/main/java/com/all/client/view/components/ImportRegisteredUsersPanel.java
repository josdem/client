package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class ImportRegisteredUsersPanel extends JPanel implements Internationalizable {
	private static final long serialVersionUID = 1L;

	private static final Dimension MINIMUM_SIZE = new Dimension(324, 178);

	private static final Dimension DEFAULT_SIZE = new Dimension(324, 555);

	private ContactResultsPanel contactResultsPanel;
	private final List<ContactInfo> contactList;

	private final Messages messages;
	
	public ImportRegisteredUsersPanel(List<ContactInfo> contactList, Messages messages) {
		this.contactList = contactList;
		this.messages = messages;
		setLayout(new BorderLayout());
		resetSize();
		add(getImportContactsResultsPanel());
		setVisible(true);
	}
	
	private ContactResultsPanel getImportContactsResultsPanel() {
		if (contactResultsPanel == null) {
			contactResultsPanel = new ContactResultsPanel(contactList, messages);
		}
		return contactResultsPanel;
	}
	
	public JButton getSkipButton(){
		return contactResultsPanel.getSkipButton();
	}
	
	public JButton getInviteButton(){
		return contactResultsPanel.getInviteButton();
	}

	@Override
	public void internationalize(Messages messages) {
		contactResultsPanel.internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public void resetSize() {
		setMaximumSize(DEFAULT_SIZE);
		setMinimumSize(MINIMUM_SIZE);
		setPreferredSize(DEFAULT_SIZE);
		setSize(DEFAULT_SIZE);
	}

	public List<ContactInfo> getSelectedContacts() {
		return contactResultsPanel.getCheckedContacts();		
	}
	

	
}
