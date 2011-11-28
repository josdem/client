package com.all.client.view;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.event.EventMethod;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;

@Component
public class PokeballPanel extends JPanel implements Internationalizable {
	private static final long serialVersionUID = 1L;

	private JButton shortcutButton;
	private PokeballItems currentItem;

	private Messages messages;

	private static final Rectangle BOUNDS = new Rectangle(0, 0, 82, 63);

	private static final Rectangle SHORTCUT_BUTTON_BOUNDS = new Rectangle(28, 11, 40, 40);

	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private ViewEngine viewEngine;

	private Observable<ObserveObject> contactListEvent = new Observable<ObserveObject>();

	public ObserverCollection<ObserveObject> onDisplayContactFrame() {
		return contactListEvent;
	}

	public enum PokeballItems {
		CONTACT_LIST("shortcutButton_ContactList", true, "contactList.tooltip");

		private final String synthName;
		private boolean isEnabled = false;
		private String toolTip;

		private PokeballItems(String synthName, boolean isEnabled, String toolTip) {
			this.synthName = synthName;
			this.isEnabled = isEnabled;
			this.toolTip = toolTip;
		}

		public String getSynthName() {
			return synthName;
		}

		public String getToolTip() {
			return toolTip;
		}

		public boolean isEnabled() {
			return isEnabled;
		}

		public void setEnabled(boolean enabled) {
			this.isEnabled = enabled;
		}

	}


	@PostConstruct
	public void init() {
		initialize();
		setItem(PokeballItems.CONTACT_LIST);

		getShortcutButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Sound.LIBRARY_EXPAND.play();
				manageEvent();
			}
		});
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onAppStarted() {
		Boolean isOnline = viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION);
		PokeballItems.CONTACT_LIST.setEnabled(isOnline);
	}

	private void manageEvent() {
		if (currentItem.isEnabled) {
			manageByCurrentItem(viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION));
		}
	}

	private void manageByCurrentItem(boolean connected) {
		switch (currentItem) {
		case CONTACT_LIST:
			if (connected) {
				contactListEvent.fire(ObserveObject.EMPTY);
			} else {
				dialogFactory.showContactListUnavailableDialog();
			}
			break;
		}
	}

	public void setItem(PokeballItems item) {
		currentItem = item;
		shortcutButton.setName(currentItem.getSynthName());
		shortcutButton.setEnabled(currentItem.isEnabled());
		shortcutButton.setToolTipText(messages.getMessage(currentItem.getToolTip()));
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setBounds(BOUNDS);
		this.add(getShortcutButton());
	}

	/**
	 * This method initializes muteButton
	 */
	private JButton getShortcutButton() {
		if (shortcutButton == null) {
			shortcutButton = new JButton();
			shortcutButton.setBounds(SHORTCUT_BUTTON_BOUNDS);
		}
		return shortcutButton;
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

	@Override
	public void internationalize(Messages messages) {
		if (currentItem != null) {
			shortcutButton.setToolTipText(messages.getMessage(currentItem.getToolTip()));
		}
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

}
