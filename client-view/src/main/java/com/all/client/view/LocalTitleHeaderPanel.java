package com.all.client.view;

import java.awt.Point;
import java.awt.Rectangle;

import javax.annotation.PostConstruct;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Root;
import com.all.shared.model.Root.ContainerType;
import com.all.shared.model.User;

@Component
public class LocalTitleHeaderPanel extends TitleHeaderPanel {
	private static final long serialVersionUID = 1L;

	private static final EmptyBorder LIBRARY_LABEL_BORDER = new EmptyBorder(2, 10, 2, 0);

	private static final Point COLLAPSE_BUTTON_LOCATION = new Point(171, 3);

	private static final Rectangle LIBRARY_LABEL_BOUNDS = new Rectangle(2, 3, 156, 16);

	private static final String HEADER_BACKGROUND = "headerBackground";

	private Messages messages;

	private String userName = "User";

	@Autowired
	private ViewEngine viewEngine;

	private Root root;

	@PostConstruct
	public void initialize() {
		this.setLayout(null);
		this.setSize(HEADER_SIZE);
		this.setName(HEADER_BACKGROUND);
		this.setPreferredSize(HEADER_SIZE);
		this.setMaximumSize(HEADER_SIZE);
		this.setMinimumSize(HEADER_SIZE);

		getCollapseButton().setLocation(COLLAPSE_BUTTON_LOCATION);
		getLibraryLabel().setBounds(LIBRARY_LABEL_BOUNDS);
		getLibraryLabel().setBorder(LIBRARY_LABEL_BORDER);

		this.add(getCollapseButton(), null);
		this.add(getLibraryLabel(), null);
	}
	
	@EventMethod(Events.UserProfile.USER_PROFILE_UPDATED_ID)
	public void onUserProfileUpdated(ValueEvent<User> event){
		if(root.getType().equals(ContainerType.LOCAL)){
			String message = messages.getMessage("headerPanel.mylibrary.label", event.getValue().getNickName());
			this.getLibraryLabel().setText( message.toUpperCase());
		}
	}
	@Override
	public void initialize(ViewEngine viewEngine) {
		root = viewEngine.get(Model.USER_ROOT);
		internationalize(messages);
	}
	
	@EventMethod(Events.Application.STARTED_ID)
	public void onUserSessionStarted() {
			User user = viewEngine.get(Model.CURRENT_USER);
			userName = user.getNickName() == null ? user.getEmail() : user.getNickName();
			internationalize(messages);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		super.setMessages(messages);
		this.messages = messages;
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onStart() {
		initialize(viewEngine);
	}

	public void internationalize(Messages messages) {
		if (root != null) {
			String text = "";
			switch (root.getType()) {
			case DEVICE:
				text = "Device: " + root.getName();
				break;
			case LOCAL:
				String message = messages.getMessage("headerPanel.mylibrary.label", userName);
				text = message.toUpperCase();
				break;
			case REMOTE:
				text = "MY REMOTE LIBRARY";
				break;
			case CONTACT:
				text = messages.getMessage("headerPanel.mylibrary.label", root.getName());
				break;
			}

			this.getLibraryLabel().setText(text);
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

}
