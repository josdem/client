package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;
import com.all.shared.model.ContactInfo;

public class ContactsTreeCellRenderer extends JPanel implements TreeCellRenderer {

	private static final Dimension AVATAR_PANEL_SIZE = new Dimension(30, 30);

	private static final long serialVersionUID = -4430385733622846351L;

	private static final Insets CONTACT_INSETS = new Insets(0, 4, 0, 4);
	private static final Color droppingColor = new Color(50, 15, 50);
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	private JLabel contactNameLabel;
	private JLabel statusIconLabel;

	private ImagePanel avatarPanel;
	private JPanel portraitMask;

	private boolean isRoot;

	private JPanel contactDetailPanel;

	private JLabel contactQuoteLabel;

	public ContactsTreeCellRenderer() {
		super();
		setBackground(TRANSPARENT);
		setLayout(new GridBagLayout());

		GridBagConstraints statusIconLabelConstraints = new GridBagConstraints();
		statusIconLabelConstraints.gridx = 0;

		GridBagConstraints avatarPanelConstraints = new GridBagConstraints();
		avatarPanelConstraints.gridx = 1;
		avatarPanelConstraints.insets = new Insets(0, 3, 0, 0);

		add(getPortraitMask(), avatarPanelConstraints);

		add(getStatusIconLabel(), statusIconLabelConstraints);
		
		GridBagConstraints contactConstraints = new GridBagConstraints();
		contactConstraints.gridx = 2;
		contactConstraints.fill = GridBagConstraints.HORIZONTAL;
		contactConstraints.weightx = 1.0;
		contactConstraints.insets = CONTACT_INSETS;
		
		add(getContactDetailPanel(), contactConstraints);
	}
	

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		// defaults?
		Icon icon = null;
		String contactName = "";
		String message = "";
		Image avatar = null;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		ContactInfo contact = null;

		if (node.getUserObject() instanceof ContactInfo) {
			contact = (ContactInfo) node.getUserObject();
			String name = (contact.isOnline() || contact.isAway()) ? SynthFonts.BOLD_FONT12_GRAY77_77_77 : SynthFonts.BOLD_FONT12_GRAY150_150_150; 
			
			getContactNameLabel().setName(name);
			contactName = contact.getChatName();
			message = contact.getQuote();
			avatar = ImageUtil.getImage(contact.getAvatar());
			
			validateDrop(contact);
			
			getStatusIconLabel().setVisible(true);
			getPortraitMask().setVisible(true);
			
			getContactNameLabel().setPreferredSize(new Dimension(142, 15));
			getContactQuoteLabel().setVisible(true);
			
			isRoot = false;
			if(contact.getChatStatus().equals(ChatStatus.PENDING)){
				getPortraitMask().setVisible(false);
			}
		} else {
			getContactNameLabel().setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			contactName = node.getUserObject().toString();
			getStatusIconLabel().setVisible(false);
			getPortraitMask().setVisible(false);
			getContactNameLabel().setPreferredSize(new Dimension(142, 30));
			getContactQuoteLabel().setVisible(false);
			isRoot = true;
		}

		if (leaf && value instanceof DefaultMutableTreeNode && contact != null) {
			switch (contact.getChatStatus()) {
			case OFFLINE:
				icon = contact.getChatType().equals(ChatType.ALL) ? SynthIcons.OFFLINE_ICON :  SynthIcons.FACEBOOK_OFFLINE_ICON;
				getContactQuoteLabel().setVisible(!contact.getChatType().equals(ChatType.FACEBOOK));
				break;
			case ONLINE:
				icon = contact.getChatType().equals(ChatType.ALL) ? SynthIcons.ONLLINE_ICON : SynthIcons.FACEBOOK_ONLINE_ICON;
				message = contact.getChatType().equals(ChatType.FACEBOOK) ?  contact.getChatStatus().toString().toLowerCase() : message ;
				
				break;
			case PENDING:
				icon = SynthIcons.PENDING_ICON;
				contactName = ((ContactInfo) contact).getEmail();
				message = "";
				break;
			case AWAY:
				icon = SynthIcons.FACEBOOK_AWAY_ICON;
				message = contact.getChatType().equals(ChatType.FACEBOOK) ?  contact.getChatStatus().toString().toLowerCase() : message ;
				break;
			}
		}
		setStyle(avatar, contactName, message, icon);
        int h = 46 ; 
        setMaximumSize(new Dimension(tree.getWidth() - 25, h));
		setPreferredSize(new Dimension(tree.getWidth() - 25, h));
		setMinimumSize(new Dimension(tree.getWidth() - 25, h));
		setSize(new Dimension(tree.getWidth() - 25, h));
		return this;
	}


	private void validateDrop(ContactInfo contact) {
		if (contact.isIsDropping()) {
			getContactNameLabel().setForeground(droppingColor);
			contact.setIsDropping(false);
		}
		else{
			getContactNameLabel().setForeground(null);
		}
	}

	private void setStyle(Image avatar, String contactName, String message, Icon icon) {
		if (isRoot) {
			getContactNameLabel().setIcon(icon);
		} else{
			getStatusIconLabel().setIcon(icon);
			getAvatarPanel().setImage(avatar, 0, 0);
		}
		getContactNameLabel().setText(contactName);
		getContactQuoteLabel().setText(message);
	}

	public DefaultMutableTreeNode getDefaultMutableTreeNodeFromCoordinates(int x, int y, JTree tree) {
		if (tree.getPathForLocation(x, y) == null) {
			return (DefaultMutableTreeNode) tree.getModel().getRoot();
		}
		return (DefaultMutableTreeNode) tree.getPathForLocation(x, y).getLastPathComponent();
	}
	
	private JLabel getContactNameLabel() {
		if(contactNameLabel == null){
			contactNameLabel = new JLabel();
			contactNameLabel.setAlignmentX(SwingConstants.LEFT);
			contactNameLabel.setPreferredSize(new Dimension(142, 15));
		}
		return contactNameLabel;
	}
	
	private JLabel getContactQuoteLabel() {
		if(contactQuoteLabel == null){
			contactQuoteLabel = new JLabel();
			contactQuoteLabel.setName(SynthFonts.ITALIC_FONT10_GRAY150_150_150);
			contactQuoteLabel.setAlignmentX(SwingConstants.LEFT);
			contactQuoteLabel.setPreferredSize(new Dimension(142, 15));
		}
		return contactQuoteLabel;
	}
	
	private JPanel getContactDetailPanel() {
		if(contactDetailPanel == null){
			contactDetailPanel = new JPanel();
			contactDetailPanel.setLayout(new GridBagLayout());GridBagConstraints contactNameLabelConstraint = new GridBagConstraints();
			contactNameLabelConstraint.fill = GridBagConstraints.HORIZONTAL;
			contactNameLabelConstraint.weightx = 1.0;
			
			contactDetailPanel.add(getContactNameLabel(), contactNameLabelConstraint);
			
			GridBagConstraints contactQuoteLabelConstraint = new GridBagConstraints();
			contactQuoteLabelConstraint.fill = GridBagConstraints.HORIZONTAL;
			contactQuoteLabelConstraint.weightx = 1.0;
			contactQuoteLabelConstraint.gridy = 1;

			contactDetailPanel.add(getContactQuoteLabel(), contactQuoteLabelConstraint);
		}
		return contactDetailPanel;
	}
	
	private ImagePanel getAvatarPanel() {
		if(avatarPanel == null){
			this.avatarPanel = new ImagePanel();
			avatarPanel.setLayout(new BorderLayout());
			avatarPanel.setPreferredSize(AVATAR_PANEL_SIZE);
			avatarPanel.setMinimumSize(AVATAR_PANEL_SIZE);
			avatarPanel.setMaximumSize(AVATAR_PANEL_SIZE);
			avatarPanel.setSize(AVATAR_PANEL_SIZE);
		}
		return avatarPanel;
	}
	
	private JPanel getPortraitMask() {
		if(portraitMask == null){
			portraitMask = new JPanel();
			portraitMask.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1 ));
			portraitMask.setPreferredSize(new Dimension(32, 32));
			portraitMask.setMinimumSize(new Dimension(32, 32));
			portraitMask.setMaximumSize(new Dimension(32, 32));
			portraitMask.setSize(new Dimension(32, 32));
			portraitMask.setName("portraitWhitePanel");
			portraitMask.add(getAvatarPanel(), null);
		}
		return portraitMask;
	}
	
	public JLabel getStatusIconLabel() {
		if(statusIconLabel == null){
			statusIconLabel = new JLabel();
			statusIconLabel.setBackground(TRANSPARENT);
		}
		return statusIconLabel;
	}

}
