package com.all.client.view.chat;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.view.components.ImagePanel;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public class NodeContactSelectedPanel extends JPanel {

	private static final String PORTRAIT_PANEL_NAME = "portraitWhitePanel";
	private static final long serialVersionUID = 4801984014448221860L;
	private static final Color TRANSPARENT = new Color(0, 0, 0);
	private static final Color DEFAULT_FOREGROUND = new Color(77, 77, 77);
	private static final String LIBRARY_BUTTON_NAME = "musicSelectedContactButton";
	private static final String CHAT_BUTTON_NAME = "chatSelectedContactButton";
	private static final String OFFLINE_NAME = "selectedContactPanelBackgroundOffline";
	private static final String ONLINE_NAME = "selectedContactPanelBackground";
	private static final Dimension BUTTON_BOUNDS = new Dimension(28, 28);
	private static final Dimension IMAGE_PANEL_DIMENSION = new Dimension(30, 30);
	private static final Dimension PORTRAIT_PANEL_DIMENSION = new Dimension(32, 32);
	private static final Dimension BUTTONS_PANEL_DIMENSION_ALL = new Dimension(98, 30);
	private static final Dimension BUTTONS_PANEL_DIMENSION = new Dimension(34, 30);
	private static final Insets IMAGE_PANEL_INSETS = new Insets(0, 3, 0, 4);
	private static final Insets BUTTONS_PANEL_INSETS = new Insets(3, 0, 0, 0);
	private static int WIDTH;
	private static final int HEIGHT = 44;

	private ImagePanel imagePanel;
	private JPanel portraitPanel;
	private JLabel nameLabel;
	private JLabel quoteLabel;
	private JButton libraryButton;
	private JButton chatButton;
	private JLabel stateLabel;
	private JPanel buttonsPanel;
	private JPanel infoPanel;
	private final ContactInfo contact;
	private final Icon icon;
	private final int PAD_RIGHT = 22;
	private final Messages messages;

	public NodeContactSelectedPanel(int width, ContactInfo contact, Icon icon, Messages messages) {
		WIDTH = width;
		this.contact = contact;
		this.icon = icon;
		this.messages = messages;

		init();
		addHierarchyBoundsListener(new HierarchyBoundsListener() {

			@Override
			public void ancestorResized(HierarchyEvent e) {
				Container changedParent = e.getChangedParent();
				if (changedParent instanceof JViewport) {
					int preferredWidth = changedParent.getWidth() - PAD_RIGHT;
					setPreferredSize(new Dimension(preferredWidth, getHeight()));
					setSize(new Dimension(preferredWidth, getHeight()));
					setMaximumSize(new Dimension(preferredWidth, getHeight()));
					setMinimumSize(new Dimension(preferredWidth, getHeight()));
				}
			}

			@Override
			public void ancestorMoved(HierarchyEvent e) {

			}
		});

	}

	public void onContactUpdated(ContactInfo  contact) {
		if (contact.equals(NodeContactSelectedPanel.this.contact)) {
			updateCurrentContact(contact);
		}
	}

	private void init() {
		if (contact.getChatStatus().equals(ChatStatus.OFFLINE)) {
			setName(OFFLINE_NAME);
		} else {
			setName(ONLINE_NAME);
		}
		setLayout(new GridBagLayout());
		setPreferredSize(new Dimension(WIDTH - PAD_RIGHT, HEIGHT));
		setMinimumSize(new Dimension(WIDTH - PAD_RIGHT, HEIGHT));
		setSize(new Dimension(WIDTH - PAD_RIGHT, HEIGHT));
		setMaximumSize(new Dimension(WIDTH - PAD_RIGHT, HEIGHT));

		GridBagConstraints statusIconLabelConstraints = new GridBagConstraints();
		statusIconLabelConstraints.gridx = 0;
		add(getStateLabel(), statusIconLabelConstraints);

		GridBagConstraints imagePanelConstraints = new GridBagConstraints();
		imagePanelConstraints.gridx = 1;
		imagePanelConstraints.insets = IMAGE_PANEL_INSETS;
		add(getPortraitPanel(), imagePanelConstraints);

		GridBagConstraints infoPanelConstraints = new GridBagConstraints();
		infoPanelConstraints.gridx = 2;
		infoPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		infoPanelConstraints.weightx = 1.0;

		add(getInfoPanel(), infoPanelConstraints);

		GridBagConstraints buttonsPanelConstraints = new GridBagConstraints();
		buttonsPanelConstraints.gridx = 3;
		buttonsPanelConstraints.insets = BUTTONS_PANEL_INSETS;
		add(getButtonsPanel(), buttonsPanelConstraints);
	}

	private ImagePanel getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new ImagePanel();
			imagePanel.setPreferredSize(IMAGE_PANEL_DIMENSION);
			imagePanel.setMaximumSize(IMAGE_PANEL_DIMENSION);
			imagePanel.setMinimumSize(IMAGE_PANEL_DIMENSION);
			imagePanel.setSize(IMAGE_PANEL_DIMENSION);

		}
		return imagePanel;
	}

	private JPanel getPortraitPanel() {
		if (portraitPanel == null) {
			portraitPanel = new JPanel();
			portraitPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
			portraitPanel.setPreferredSize(PORTRAIT_PANEL_DIMENSION);
			portraitPanel.setMinimumSize(PORTRAIT_PANEL_DIMENSION);
			portraitPanel.setMaximumSize(PORTRAIT_PANEL_DIMENSION);
			portraitPanel.setSize(PORTRAIT_PANEL_DIMENSION);
			portraitPanel.setName(PORTRAIT_PANEL_NAME);
			portraitPanel.add(getImagePanel(), null);
		}
		return portraitPanel;
	}

	public void setImage(Image avatar) {
		getImagePanel().setImage(avatar, 0, 0);
	}

	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel(contact.getChatName());
			nameLabel.setAlignmentX(SwingConstants.LEFT);
			nameLabel.setBackground(TRANSPARENT);
			nameLabel.setForeground(DEFAULT_FOREGROUND);
			nameLabel.setName(SynthFonts.BOLD_FONT12_GRAY50_50_50);
		}
		return nameLabel;
	}

	private JLabel getStateLabel() {
		if (stateLabel == null) {
			stateLabel = new JLabel();
			stateLabel.setIcon(icon);
		}
		return stateLabel;
	}

	private JLabel getQuoteLabel() {
		if (quoteLabel == null) {
			String quote = contact.getChatType().equals(ChatType.FACEBOOK) && (contact.isOnline() || contact.isAway()) ? contact.getChatStatus()
					.toString().toLowerCase() : contact.getQuote();

			quoteLabel = new JLabel(quote);
			quoteLabel.setAlignmentX(SwingConstants.LEFT);
			quoteLabel.setBackground(TRANSPARENT);
			quoteLabel.setForeground(DEFAULT_FOREGROUND);
			quoteLabel.setName(SynthFonts.ITALIC_FONT10_GRAY100_100_100);
		}
		return quoteLabel;
	}

	public JButton getChatButton() {
		if (chatButton == null) {
			chatButton = new JButton();
			chatButton.setPreferredSize(BUTTON_BOUNDS);
			chatButton.setName(CHAT_BUTTON_NAME);
			chatButton.setToolTipText(messages.getMessage("tooltip.chat"));
			chatButton.setEnabled(contact.isOnline() || contact.isAway());
		}
		return chatButton;
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			if (contact.getChatType().equals(ChatType.ALL)) {
				buttonsPanel.setPreferredSize(BUTTONS_PANEL_DIMENSION_ALL);
				buttonsPanel.setMinimumSize(BUTTONS_PANEL_DIMENSION_ALL);
				buttonsPanel.setMaximumSize(BUTTONS_PANEL_DIMENSION_ALL);
				buttonsPanel.setSize(BUTTONS_PANEL_DIMENSION_ALL);
			} else {
				buttonsPanel.setPreferredSize(BUTTONS_PANEL_DIMENSION);
				buttonsPanel.setMinimumSize(BUTTONS_PANEL_DIMENSION);
				buttonsPanel.setMaximumSize(BUTTONS_PANEL_DIMENSION);
				buttonsPanel.setSize(BUTTONS_PANEL_DIMENSION);
			}
			FlowLayout flow = new FlowLayout(FlowLayout.CENTER, 4, 0);
			buttonsPanel.setLayout(flow);
			buttonsPanel.add(getLibraryButton(), null);
			buttonsPanel.add(getChatButton(), null);
		}
		return buttonsPanel;
	}

	public JButton getLibraryButton() {
		if (libraryButton == null) {
			libraryButton = new JButton();
			libraryButton.setPreferredSize(BUTTON_BOUNDS);
			libraryButton.setName(LIBRARY_BUTTON_NAME);
			libraryButton.setToolTipText(messages.getMessage("tooltip.contactLibrary"));
			libraryButton.setVisible(contact.getChatType().equals(ChatType.ALL));
		}
		return libraryButton;
	}

	public JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setLayout(new GridBagLayout());
			GridBagConstraints nameLabelConstraint = new GridBagConstraints();
			nameLabelConstraint.gridy = 0;
			nameLabelConstraint.fill = GridBagConstraints.HORIZONTAL;
			nameLabelConstraint.weightx = 1.0;
			infoPanel.add(getNameLabel(), nameLabelConstraint);
			GridBagConstraints quoteLabelConstraint = new GridBagConstraints();
			quoteLabelConstraint.gridy = 1;
			quoteLabelConstraint.fill = GridBagConstraints.HORIZONTAL;
			quoteLabelConstraint.weightx = 1.0;
			infoPanel.add(getQuoteLabel(), quoteLabelConstraint);
		}
		return infoPanel;
	}

	private void updateCurrentContact(ContactInfo contact) {
		ContactInfo currentContact = contact;
		if (currentContact != null && currentContact.getChatStatus() != ChatStatus.PENDING) {
			getNameLabel().setText(currentContact.getChatName());

			String quote = contact.getChatType().equals(ChatType.FACEBOOK) && (contact.isOnline() || contact.isAway()) ? contact.getChatStatus()
					.toString().toLowerCase() : contact.getQuote();
					
			getQuoteLabel().setText(quote);

			Image image = ImageUtil.getImage(currentContact.getAvatar());
			setImage(image);
		} else {
			setVisible(false);
		}

	}

}
