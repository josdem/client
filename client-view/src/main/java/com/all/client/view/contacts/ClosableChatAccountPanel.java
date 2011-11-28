package com.all.client.view.contacts;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import com.all.chat.ChatType;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObservedProperty;
import com.all.observ.ObserverCollection;

public class ClosableChatAccountPanel extends ChatAccountPanel implements Internationalizable{

	private static final long serialVersionUID = 1L;
	private JButton closeButton;
	private static final String CLOSE_BUTTON_NAME = "buttonClose";
	private static final Rectangle CLOSE_BUTTON_BOUNDS = new Rectangle(8, 3, 80, 22);

	private final Observable<ObservValue<ChatType>> onCloseEvent = new Observable<ObservValue<ChatType>>();
	private String textSignIn;
	private String textSignOut;

	public ClosableChatAccountPanel(ChatType chatType, boolean facebookEnable,
			ObservedProperty<ChatSelectionPanel, ChatType> selectedChat) {
		super(chatType, facebookEnable, selectedChat);
		addCloseButton();
	}

	private void addCloseButton() {
		getButtonPanel().add(getCloseButton());
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setBounds(CLOSE_BUTTON_BOUNDS);
			closeButton.setName(CLOSE_BUTTON_NAME);
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(isChatTypeEnable()){
						onCloseEvent.fire(new ObservValue<ChatType>(chatType));
					}else{
						select();
					}
				}
			});
		}
		return closeButton;
	}

	@Override
	public void select() {
		super.select();
		setTextButton();
	}

	@Override
	public void unselect() {
		super.unselect();
		setTextButton();
	}

	@Override
	public void activateChatType(boolean active) {
		super.activateChatType(active);
		setTextButton();
	}

	public ObserverCollection<ObservValue<ChatType>> onCloseEvent() {
		return onCloseEvent;
	}

	private void setTextButton() {
		String text = isChatTypeEnable() ? textSignOut : textSignIn;
		closeButton.setText(text);
	}

	@Override
	public void internationalize(Messages messages) {
		textSignIn = messages.getMessage("facebook.dialog.button.login");
		textSignOut = messages.getMessage("facebook.dialog.button.logout");
		setTextButton();
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
