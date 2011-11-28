package com.all.client.view.contacts;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.chat.ChatType;
import com.all.core.common.view.SynthFonts;
import com.all.observ.ObservedProperty;

class ChatAccountPanel extends JPanel{
	private static final Dimension CHAT_TYPE_LABEL_DIMENSION = new Dimension(71, 15);
	
	private final Dimension CHAT_ICON_PANEL_SIZE = new Dimension(24, 24);

	private static final int PREFERRED_HEIGHT = 30;

	private static final Insets CHAT_TYPE_PANEL_INSETS = new Insets(0, 4, 0, 0);

	private static final Insets ICON_PANEL_INSETS = new Insets(0, 27, 0, 0);

	private static final String ALL_CHAT_ACTIVE_SELECTED_ICON_NAME = "allChatSelectedPanelAccount";

	private static final String ALL_CHAT_ACTIVE_ICON_NAME = "allChatPanelAccount";

	private static final String ALL_FACEBOOK_ICON_NAME = "allFacebookPanelAccount";

	private static final String ALL_FACEBOOK_ACTIVE_SELECTED_ICON_NAME = "allFacebookActiveSelectedPanelAccount";

	private static final String ALL_FACEBOOK_INACTIVE_ICON_NAME = "allFacebookInactivePanelAccount";

	private static final String ALL_FACEBOOK_INACTIVE_SELECTED_ICON_NAME = "allFacebookInactiveSelectedPanelAccount";
	
	private static final long serialVersionUID = 1L;


	private boolean chatTypeEnable = false;
	private JLabel chatTypeLabel;
	private JPanel chatIconPanel;
	private JPanel buttonPanel;
	
	private static final String PANEL_ENABLE_NAME = "panelChatAccount";
	private static final String PANEL_SELECTED_NAME = "panelChatAccountSelected";
	protected final ChatType chatType;
	private boolean isSelected = false ;
	private final ObservedProperty<ChatSelectionPanel, ChatType> selectedChat;

	public ChatAccountPanel(ChatType chatType, boolean facebookEnable, ObservedProperty<ChatSelectionPanel, ChatType> selectedChat) {
		this.chatType = chatType;
		this.chatTypeEnable = facebookEnable;
		this.selectedChat = selectedChat;
		init();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				select();
			}
		});
	}
	
	public void select() {
		if(!isSelected ){
			Component[] components = getParent().getComponents();
			for (int i = 0; i < components.length; i++) {
				if(components[i] instanceof ChatAccountPanel ){
					ChatAccountPanel component = (ChatAccountPanel) components[i];
					component.unselect();
				}
			}
		}
		isSelected = true;
		setName(PANEL_SELECTED_NAME);
		updateIconPanel();
		selectedChat.setValue(chatType);
	}

	public void unselect() {
		this.isSelected = false;
		setName(PANEL_ENABLE_NAME);
		updateIconPanel();
	}
	
	private void updateIconPanel(){
		switch (chatType){
		case FACEBOOK:
			if (!chatTypeEnable) {
				if (isSelected) {
					chatIconPanel.setName(ALL_FACEBOOK_INACTIVE_SELECTED_ICON_NAME);
				} else {
					chatIconPanel.setName(ALL_FACEBOOK_INACTIVE_ICON_NAME);
				}
			} else {
				if (isSelected) {
					chatIconPanel.setName(ALL_FACEBOOK_ACTIVE_SELECTED_ICON_NAME);
				} else {
					chatIconPanel.setName(ALL_FACEBOOK_ICON_NAME);
				}
			}
			break;
		case ALL:
			if (isSelected) {
				chatIconPanel.setName(ALL_CHAT_ACTIVE_SELECTED_ICON_NAME);
			} else {
				chatIconPanel.setName(ALL_CHAT_ACTIVE_ICON_NAME);
			}
			break;
		}
	}

	private void init() {
		this.setName(PANEL_ENABLE_NAME);
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(new Dimension(getWidth(), PREFERRED_HEIGHT));
		
		GridBagConstraints iconPanelConstraints = new GridBagConstraints();
		iconPanelConstraints.gridx = 0;
		iconPanelConstraints.insets = ICON_PANEL_INSETS;
		add(getChatIconPanel(), iconPanelConstraints);
		
		GridBagConstraints chatTypePanelConstraints = new GridBagConstraints();
		chatTypePanelConstraints.gridx = 1;
		chatTypePanelConstraints.insets = CHAT_TYPE_PANEL_INSETS;
		add(getChatTypeLabel(), chatTypePanelConstraints);
		
		GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
		buttonPanelConstraints.gridx = 2; 
		buttonPanelConstraints.fill = GridBagConstraints.BOTH;
		buttonPanelConstraints.weightx=1.0;
		buttonPanelConstraints.weighty=1.0;
		
		add(getButtonPanel(), buttonPanelConstraints);
	}
	
	private JLabel getChatTypeLabel() {
		if(chatTypeLabel == null){
			chatTypeLabel = new JLabel();
			String text = chatType.equals(ChatType.ALL) ? "ALL" : "FACEBOOK" ;
			chatTypeLabel.setText(text);
			chatTypeLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			chatTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
			chatTypeLabel.setPreferredSize(CHAT_TYPE_LABEL_DIMENSION);
		}
		return chatTypeLabel;
	}
	
	public JPanel getChatIconPanel() {
		if(chatIconPanel == null){
			chatIconPanel = new JPanel();
			chatIconPanel.setPreferredSize(CHAT_ICON_PANEL_SIZE);
			String name = chatType.equals(ChatType.ALL) ? ALL_CHAT_ACTIVE_ICON_NAME : ALL_FACEBOOK_INACTIVE_ICON_NAME ;
			chatIconPanel.setName(name);
		}
		return chatIconPanel;
	}
	
	public void activateChatType(boolean active){
		chatTypeEnable = active;
        updateIconPanel();
	}
	
	protected JPanel getButtonPanel() {
		if(buttonPanel == null){
			buttonPanel = new JPanel();
			buttonPanel.setLayout(null);
		}
		return buttonPanel;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public ChatType getChatType() {
		return chatType;
	}
	public boolean isChatTypeEnable() {
		return chatTypeEnable;
	}
}

