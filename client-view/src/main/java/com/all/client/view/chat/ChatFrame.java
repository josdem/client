package com.all.client.view.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.AllClientFrame;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;

public abstract class ChatFrame extends AllClientFrame implements Internationalizable {

	private static final Rectangle TITLE_LABEL_BOUNDS = new Rectangle(8, 1, 200, 20);

	private static final long serialVersionUID = 1L;

	public static final Dimension DEFAULT_SIZE = new Dimension(400, 630);

	private static final Dimension MINIMUM_SIZE = new Dimension(330, 450);

	private ChatMainPanel chatMainPanel;

	protected final ContactInfo contact;

	private JPanel contactNamePanel;

	private JLabel titleLabel;
		
	private GridBagConstraints friendInfoPanelConstraints;
	private static final Insets FRIEND_INFO_PANEL_INSETS = new Insets(0, 2, 2, 2);

	private final ViewEngine viewEngine;

	public ChatFrame(final ContactInfo contact, Messages messages, ViewEngine viewEngine) {
		super(messages, false);
		this.contact = contact;
		this.viewEngine = viewEngine;
		this.getBottomLeftPanel().add(getContactNamePanel(), BorderLayout.CENTER);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setTitle(contact.getChatName());
		getContentPanel().add(getChatMainPanel(), BorderLayout.CENTER);
		this.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				getChatMainPanel().requestFocusOnTextArea();
			}

			@Override
			public void focusLost(FocusEvent e) {
			}
		});
		JLabel titleLabel = getTitleLabel();
		getLayeredPane().add(titleLabel);
		internationalize(messages);
		super.setBounds(getBounds());
		super.doResize();
		this.setState(JFrame.ICONIFIED);
	}

	public ChatMainPanel getChatMainPanel() {
		if (chatMainPanel == null) {
			chatMainPanel = new ChatMainPanel(this, viewEngine);
		}
		return chatMainPanel;
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setBounds(TITLE_LABEL_BOUNDS);
		}
		return titleLabel;
	}

	private JPanel getContactNamePanel() {
		if (contactNamePanel == null) {
			contactNamePanel = new JPanel();
			JLabel contactNameLabel = new JLabel(contact.getChatName());
			contactNameLabel.setName(SynthFonts.PLAIN_FONT12_WHITE);
			contactNamePanel.add(contactNameLabel);
		}
		return contactNamePanel;
	}

	public void addMessage(ChatMessage message) {
		getChatMainPanel().addMessage(message);
	}

	@Override
	public final void internationalize(Messages messages) {
		getTitleLabel().setText(messages.getMessage("contact.frame.title"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public abstract void notifyContactUpdated(ContactInfo contact);

	public ContactInfo getContact() {
		return contact;
	}
	
	protected GridBagConstraints getFriendsInfoPanelConstraints(){
		if(friendInfoPanelConstraints == null){
			friendInfoPanelConstraints= new GridBagConstraints();
			friendInfoPanelConstraints.gridx = 0;
			friendInfoPanelConstraints.gridy = 0;
			friendInfoPanelConstraints.weightx = 1.0;
			friendInfoPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			friendInfoPanelConstraints.insets = FRIEND_INFO_PANEL_INSETS;
		}
		return friendInfoPanelConstraints;
	}
}
