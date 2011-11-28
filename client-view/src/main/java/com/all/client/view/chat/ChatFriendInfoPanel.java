package com.all.client.view.chat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatStatus;
import com.all.client.view.components.ImagePanel;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public class ChatFriendInfoPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final double ARC_IMAGE = .17;

	private static final Dimension CENTER_PANEL_DEFAULT_SIZE = new Dimension(253, 88);

	private static final Dimension CENTER_PANEL_MINIMUM_SIZE = new Dimension(120, 88);

	private static final Dimension CENTER_PANEL_PREFERRED_SIZE = new Dimension(253, 88);

	private static final Dimension DEFAULT_SIZE = new Dimension(396, 86);

	private static final Dimension RIGHT_PANEL_DEFAULT_SIZE = new Dimension(42, 86);

	private static final Dimension LEFT_PANEL_DEFAULT_SIZE = new Dimension(101, 86);

	private static final Dimension MINIMUM_SIZE = new Dimension(330, 86);

	private static final Insets CONTACT_NAME_LABEL_INSETS = new Insets(-32, 0, 0, 0);

	private static final Rectangle CONTACT_QUOTE_LABEL_BOUNDS = new Rectangle(100, 30, 250, 30);

	private static final Rectangle IMAGE_PANEL_BOUNDS = new Rectangle(8, 8, 70, 70);

	private static final Rectangle SHOW_REMOTE_LIBRARY_BUTTON_BOUNDS = new Rectangle(0, 44, 34, 34);

	private static final Rectangle USER_ICON_LABEL_BOUNDS = new Rectangle(85, 8, 9, 18);

	private static final Rectangle USER_NAME_LABEL_BOUNDS = new Rectangle(100, 5, 200, 30);

	private static final String CHAT_PORTRAIT_MASK_NAME = "chatPortraitMask";

	private static final String FRIEND_INFO_PANEL_NAME = "friendInfoPanelChat";

	private static final String USER_ICON_ONLINE_LABEL_NAME = "userIconOnlineLabel";

	private static final String USER_ICON_OFFLINE_LABEL_NAME = "userIconOfflineLabel";

	private static final String SHOW_REMOTE_LIBRARY_BUTTON_NAME = "showRemoteLibraryButton";

	private JPanel leftPanel;

	private JPanel rightPanel;

	private JPanel centerPanel;

	private JLabel userIconLabel;

	private JButton showContactLibraryButton;

	private ImagePanel imagePanel;

	private JLabel contactNameLabel;

	private JLabel contactQuoteLabel;

	private JPanel portraitMask;

	public ChatFriendInfoPanel() {
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setName(FRIEND_INFO_PANEL_NAME);
		this.setLayout(new GridBagLayout());
		GridBagConstraints leftPanelConstraints = new GridBagConstraints();
		leftPanelConstraints.gridx = 0;
		leftPanelConstraints.gridy = 0;
		this.add(leftPanel(), leftPanelConstraints);

		GridBagConstraints centerPanelConstraints = new GridBagConstraints();
		centerPanelConstraints.gridx = 1;
		centerPanelConstraints.gridy = 0;
		centerPanelConstraints.weightx = 1;
		centerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(centerPanel(), centerPanelConstraints);

		GridBagConstraints rightPanelConstraints = new GridBagConstraints();
		rightPanelConstraints.gridx = 2;
		rightPanelConstraints.gridy = 0;
		this.add(rightPanel(), rightPanelConstraints);
	}

	public void setup(final ContactInfo contact, final ViewEngine viewEngine) {
		getShowRemoteLibraryButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(contact.getEmail()));
			}
		});
		setup(contact);
	}

	public void setup(final ContactInfo contact) {
		getImagePanel().setImage(ImageUtil.getImage(contact.getAvatar()), ARC_IMAGE, ARC_IMAGE);
		getContactNameLabel().setText(contact.getChatName());
		getContactQuoteLabel().setText(contact.getQuote());
	}

	private JPanel rightPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setLayout(null);
			rightPanel.setSize(RIGHT_PANEL_DEFAULT_SIZE);
			rightPanel.setPreferredSize(RIGHT_PANEL_DEFAULT_SIZE);
			rightPanel.setMinimumSize(RIGHT_PANEL_DEFAULT_SIZE);
			rightPanel.add(getShowRemoteLibraryButton());
		}
		return rightPanel;
	}

	public JButton getShowRemoteLibraryButton() {
		if (showContactLibraryButton == null) {
			showContactLibraryButton = new JButton();
			showContactLibraryButton.setName(SHOW_REMOTE_LIBRARY_BUTTON_NAME);
			showContactLibraryButton.setBounds(SHOW_REMOTE_LIBRARY_BUTTON_BOUNDS);
		}
		return showContactLibraryButton;
	}

	private JPanel centerPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new GridBagLayout());
			centerPanel.setSize(CENTER_PANEL_DEFAULT_SIZE);
			centerPanel.setPreferredSize(CENTER_PANEL_PREFERRED_SIZE);
			centerPanel.setMinimumSize(CENTER_PANEL_MINIMUM_SIZE);

			GridBagConstraints contactNameLabelConstraints = new GridBagConstraints();
			contactNameLabelConstraints.gridx = 0;
			contactNameLabelConstraints.gridy = 0;
			contactNameLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			contactNameLabelConstraints.weightx = 1.0;
			contactNameLabelConstraints.insets = CONTACT_NAME_LABEL_INSETS;

			centerPanel.add(getContactNameLabel(), contactNameLabelConstraints);

			GridBagConstraints contactQuoteLabelConstraints = new GridBagConstraints();
			contactQuoteLabelConstraints.gridx = 0;
			contactQuoteLabelConstraints.gridy = 1;
			contactQuoteLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			contactQuoteLabelConstraints.weightx = 1.0;
			centerPanel.add(getContactQuoteLabel(), contactQuoteLabelConstraints);
		}
		return centerPanel;
	}

	public JLabel getContactNameLabel() {
		if (contactNameLabel == null) {
			contactNameLabel = new JLabel();
			contactNameLabel.setName(SynthFonts.BOLD_FONT15_WHITE);
			contactNameLabel.setBounds(USER_NAME_LABEL_BOUNDS);
		}
		return contactNameLabel;
	}

	private JPanel leftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			leftPanel.setLayout(null);
			leftPanel.setSize(LEFT_PANEL_DEFAULT_SIZE);
			leftPanel.setPreferredSize(LEFT_PANEL_DEFAULT_SIZE);
			leftPanel.setMaximumSize(LEFT_PANEL_DEFAULT_SIZE);
			leftPanel.setMinimumSize(LEFT_PANEL_DEFAULT_SIZE);
			leftPanel.add(getImagePanel());
			leftPanel.add(getUserIconLabel());
		}
		return leftPanel;
	}

	public JLabel getContactQuoteLabel() {
		if (contactQuoteLabel == null) {
			contactQuoteLabel = new JLabel();
			contactQuoteLabel.setBounds(CONTACT_QUOTE_LABEL_BOUNDS);
			contactQuoteLabel.setName(SynthFonts.BOLD_FONT12_WHITE);
		}
		return contactQuoteLabel;
	}

	public void setNameUserIconLabel(String name) {
		userIconLabel.setName(name);
	}

	public JLabel getUserIconLabel() {
		if (userIconLabel == null) {
			userIconLabel = new JLabel();
			userIconLabel.setName(USER_ICON_ONLINE_LABEL_NAME);
			userIconLabel.setBounds(USER_ICON_LABEL_BOUNDS);
		}
		return userIconLabel;
	}

	public ImagePanel getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new ImagePanel();
			imagePanel.setLayout(new BorderLayout());
			imagePanel.setBounds(IMAGE_PANEL_BOUNDS);
			imagePanel.add(getPortraitMask(), BorderLayout.CENTER);
		}
		return imagePanel;
	}

	private JPanel getPortraitMask() {
		if (portraitMask == null) {
			portraitMask = new JPanel();
			portraitMask.setLayout(null);
			portraitMask.setName(CHAT_PORTRAIT_MASK_NAME);
		}
		return portraitMask;
	}

	@Override
	public void internationalize(Messages messages) {
		showContactLibraryButton.setToolTipText(messages.getMessage("chatFriendInfoPanel.showLibrary.tooltip"));

	}

	@Override
	public void removeMessages(Messages messages) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMessages(Messages messages) {
		internationalize(messages);
	}

	public void notifyPresence(ChatStatus status) {
		if (status == ChatStatus.OFFLINE) {
			setNameUserIconLabel(USER_ICON_OFFLINE_LABEL_NAME);
		}
		if (status == ChatStatus.ONLINE) {
			setNameUserIconLabel(USER_ICON_ONLINE_LABEL_NAME);
		}
	}
}
