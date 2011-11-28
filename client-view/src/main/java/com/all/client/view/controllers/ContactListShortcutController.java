package com.all.client.view.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.MainFrame;
import com.all.client.view.contacts.ContactListPanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.AddContactFlow;
import com.all.client.view.flows.EditProfileFlow;
import com.all.commons.Environment;

@Controller
public class ContactListShortcutController {

	private static final String CREATE_CONTACTS = "createContactsGroup";

	private static final String EDIT_PROFILE = "editProfile";

	private static final String OPEN_PENDING_EMAILS = "openPendingEmails";

	private static final String OPEN_SEARCH_CONTACTS = "openSearchContacts";

	@Autowired
	private MainFrame contactFrame;
	@Autowired
	private ContactListPanel contactListPanel;
	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private DialogFactory dialogFactory;

	private KeyStroke createContactsKey;
	private KeyStroke editProfileKey;
	private KeyStroke openPendingEmailsKey;
	private KeyStroke openSearchContactsKey;

	private Action createContactsGroupAction;
	private Action editProfileAction;
	private Action openPendingEmailsAction;
	private Action openSearchContactsAction;

	@SuppressWarnings("serial")
	public ContactListShortcutController() {
		int mask = (Environment.isMac() ? ActionEvent.META_MASK : ActionEvent.CTRL_MASK);
		createContactsKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_G, mask);
		editProfileKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_E, mask);
		openPendingEmailsKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_I, mask);
		openSearchContactsKey = KeyStroke.getKeyStroke((char) KeyEvent.VK_S, mask);

		createContactsGroupAction = new AbstractAction() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// if
				// (hipecotechTopPanel.getPlayButton().getName().equals(HipecotechTopPanel.PLAY_BUTTON_STYLE))
				// {
				// musicPlayerController.play();
				// } else if
				// (hipecotechTopPanel.getPlayButton().getName().equals(HipecotechTopPanel.STOP_BUTTON))
				// {
				// musicPlayerController.stop();
				// } else if
				// (hipecotechTopPanel.getPlayButton().getName().equals(HipecotechTopPanel.PAUSE_BUTTON))
				// {
				// musicPlayerController.pause();
				// hipecotechTopPanel.getPlayButton().setName(HipecotechTopPanel.PLAY_BUTTON_STYLE);
				// }
				// mainFrame.requestFocus();
			}
		};

		editProfileAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// musicPlayerController.forward();
				// mainFrame.requestFocus();
				if (contactListPanel.isShowing()) {
					new EditProfileFlow(viewEngine, dialogFactory).execute(false);
				}
			}
		};

		openPendingEmailsAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (contactListPanel.isShowing()) {
					contactListPanel.sendInvitation();
				}
			}
		};

		openSearchContactsAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (contactListPanel.isShowing()) {
					new AddContactFlow(viewEngine, dialogFactory).executeAdd();
				}
			}
		};
	}

	@PostConstruct
	public void bindShortcuts() {
		bindShortcuts(contactFrame.getRootPane());
	}

	private void bindShortcuts(JComponent component) {

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(createContactsKey, CREATE_CONTACTS);
		component.getActionMap().put(CREATE_CONTACTS, createContactsGroupAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(editProfileKey, EDIT_PROFILE);
		component.getActionMap().put(EDIT_PROFILE, editProfileAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(openPendingEmailsKey, OPEN_PENDING_EMAILS);
		component.getActionMap().put(OPEN_PENDING_EMAILS, openPendingEmailsAction);

		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(openSearchContactsKey, OPEN_SEARCH_CONTACTS);
		component.getActionMap().put(OPEN_SEARCH_CONTACTS, openSearchContactsAction);

	}

}
