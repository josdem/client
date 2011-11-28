package com.all.client.view.components;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.view.chat.ChatViewManager;
import com.all.client.view.chat.NodeContactSelectedPanel;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthIcons;
import com.all.core.events.Events;
import com.all.event.EventMethod;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

@org.springframework.stereotype.Component
public class ContactsTreeCellEditor implements TreeCellEditor {

	private ChatViewManager chatViewManager;

	private Log log = LogFactory.getLog(this.getClass());
	private Messages messages;

	@Autowired
	private ViewEngine viewEngine;
	private NodeContactSelectedPanel selectedPanel;

	public ContactsTreeCellEditor() {
	}
	
	
	@EventMethod(Events.Social.CONTACT_UPDATED_ID)
	public void onContactUpdated(ContactInfo contact){
		if(selectedPanel != null){
			selectedPanel.onContactUpdated(contact);
		}
	}

	@Override
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Image avatar = null;
		Object userObject = node.getUserObject();
		if (userObject instanceof ContactInfo) {
			final ContactInfo contactInfo = (ContactInfo) node.getUserObject();
			avatar = ImageUtil.getImage(contactInfo.getAvatar());
			Icon icon = null;
			if (leaf && value instanceof DefaultMutableTreeNode && contactInfo != null) {
				switch (contactInfo.getChatStatus()) {
				case OFFLINE:
					icon = contactInfo.getChatType().equals(ChatType.ALL) ? SynthIcons.OFFLINE_ICON : SynthIcons.FACEBOOK_OFFLINE_ICON;
					break;
				case ONLINE:
					icon = contactInfo.getChatType().equals(ChatType.ALL) ? SynthIcons.ONLLINE_ICON : SynthIcons.FACEBOOK_ONLINE_ICON;
					break;
				case PENDING:
					icon = SynthIcons.PENDING_ICON;
					break;
				case AWAY:
					icon = SynthIcons.FACEBOOK_AWAY_ICON;
					break;
				}
			}
			selectedPanel = new NodeContactSelectedPanel(tree.getWidth(), contactInfo, icon, messages);
			selectedPanel.getLibraryButton().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(contactInfo.getEmail()));
				}
			});

			selectedPanel.getChatButton().addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (contactInfo.getChatStatus() != ChatStatus.OFFLINE) {
							chatViewManager.showChat(contactInfo);
						}
					} catch (Exception ex) {
						log.debug(ex, ex);
					}

				}
			});
			selectedPanel.setImage(avatar);
			selectedPanel.validate();
		}
		return selectedPanel;
	}


	@Autowired
	public void setChatViewManager(ChatViewManager chatViewManager) {
		this.chatViewManager = chatViewManager;
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return false;
	}


	@Autowired
	public void setMessages(Messages messages) {
		this.messages = messages;
	}


}
