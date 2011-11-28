package com.all.client.view.chat;

import java.awt.Point;
import java.awt.dnd.DropTarget;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.actions.SendContentAction;
import com.all.core.model.ContactCollection;
import com.all.i18n.Messages;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;

public class ChatAllFrame extends ChatFrame {

	private static final long serialVersionUID = -3160261286654302226L;
	private final ContactInfo contact;
	private final Messages messages;
	private ChatFriendInfoPanel friendInfoPanel;

	public ChatAllFrame(ContactInfo contact, Messages messages, ViewEngine viewEngine) {
		super(contact, messages, viewEngine);
		this.contact = contact;
		this.messages = messages;
	}

	public void setUp(ViewEngine viewEngine) {
		getChatMainPanel().setup(contact);
		getFriendInfoPanel().setup(contact, viewEngine);
		MultiLayerDropTargetListener dndListener = new MultiLayerDropTargetListener();
		this.setDropTarget(new DropTarget(this, dndListener));
		dndListener.addDragListener(this, new MainFrameDragOverListener(this, messages));
		DropListener listener = new ChatModelDropListener(contact, this, viewEngine);
		dndListener.addDropListener(this, listener);
		getChatMainPanel().setDragAndDrops(dndListener, listener);
		getChatMainPanel().add(getFriendInfoPanel(), getFriendsInfoPanelConstraints());
	}

	@Override
	public void notifyContactUpdated(ContactInfo contact) {
		getFriendInfoPanel().setup(contact);
		getFriendInfoPanel().notifyPresence(contact.getChatStatus());
	}

	ChatFriendInfoPanel getFriendInfoPanel() {
		if (friendInfoPanel == null) {
			friendInfoPanel = new ChatFriendInfoPanel();
			friendInfoPanel.internationalize(messages);
		}
		return friendInfoPanel;
	}

}

class ChatModelDropListener implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { ModelCollection.class };
	private final ContactInfo contact;
	private final ChatFrame chatFrame;
	private final ViewEngine viewEngine;

	public ChatModelDropListener(ContactInfo contact, ChatFrame chatFrame, ViewEngine viewEngine) {
		this.contact = contact;
		this.viewEngine = viewEngine;
		this.chatFrame = chatFrame;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		final ModelCollection model = draggedObject.get(ModelCollection.class);
		viewEngine.request(Actions.Chat.SENT_CONTENT, contact, new ResponseCallback<ChatMessage>() {

			@Override
			public void onResponse(ChatMessage response) {
				chatFrame.addMessage(response);
			}
		});
		viewEngine.send(Actions.Social.SEND_CONTENT_ALERT, new SendContentAction(model, new ContactCollection(contact),
				"Sent from chat."));
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		ModelCollection model = draggedObject.get(ModelCollection.class);
		return model != null && !model.isEmpty() && !model.has(ModelTypes.others);
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

}
