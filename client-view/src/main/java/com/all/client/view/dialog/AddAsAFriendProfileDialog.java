package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.client.view.components.ImagePanel;
import com.all.client.view.flows.AddContactFlow;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class AddAsAFriendProfileDialog extends JDialog implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final double ARC_IMAGE = .17;

	private static final Dimension DIALOG_SIZE = new Dimension(186, 266);

	private static final Dimension PHOTO_PANEL_SIZE = new Dimension(98, 98);

	private static final Rectangle CITY_LABEL_BOUNDS = new Rectangle(5, 178, 170, 14);

	private static final Rectangle CLOSE_BUTTON_BOUNDS = new Rectangle(157, 3, 20, 20);

	private static final Rectangle COUNTRY_LABEL_BOUNDS = new Rectangle(5, 199, 170, 14);

	private static final Rectangle INVITATION_BUTTON_BOUNDS = new Rectangle(19, 227, 142, 22);

	private static final Rectangle NAME_LABEL_BOUNDS = new Rectangle(5, 137, 170, 36);

	private static final Rectangle PHOTO_PANEL_BOUNDS = new Rectangle(41, 32, 98, 98);

	private static final Rectangle TITTLE_LABEL_BOUNDS = new Rectangle(10, 4, 140, 16);

	private static final String CLOSE_BUTTON_NAME = "addAsAFriendCloseButtonProfile";

	private static final String CONTAINER_PANEL_NAME = "addAsAFriendPanelProfile";

	private static final String INVITATION_BUTTON_NAME = "addFriendButton";

	private static final String PROFILE_PORTRAIT_MASK_NAME = "profilePortrait98Mask";

	private ContactInfo contactInfo;

	private ImagePanel photoPanel;

	private JButton closeButton;

	private JButton invitationButton;

	private JLabel cityLabel;

	private JLabel countryLabel;

	private JLabel nameLabel;

	private JLabel tittleLabel;

	private JPanel portraitMask;

	private AddContactFlow addContactFlow;

	public AddAsAFriendProfileDialog() {
	}

	public AddAsAFriendProfileDialog(ContactInfo contactInfo, Messages messages) {
		this.contactInfo = contactInfo;
		initialize();
		internationalize(messages);
	}

	public AddAsAFriendProfileDialog(JFrame frame, ContactInfo contactInfo, Messages messages,
			AddContactFlow addContactFlow) {
		super(frame, null, false, TransparencyManagerFactory.getManager().getTranslucencyCapableGC());
		this.contactInfo = contactInfo;
		this.addContactFlow = addContactFlow;
		initialize();
		internationalize(messages);
		TransparencyManagerFactory.getManager().setWindowOpaque(this, false);
	}

	private void initialize() {
		this.setSize(DIALOG_SIZE);
		this.getContentPane().setName(CONTAINER_PANEL_NAME);
		this.setUndecorated(true);
		this.setModal(true);
		this.getContentPane().setLayout(null);
		this.add(getTittleLabel());
		this.add(getCloseButton());
		this.add(getPhotoPanel());
		this.add(getNameLabel());
		this.add(getCityLabel());
		this.add(getCountryLabel());
		this.add(getInvitationButton());
	}

	private JLabel getTittleLabel() {
		if (tittleLabel == null) {
			tittleLabel = new JLabel();
			tittleLabel.setBounds(TITTLE_LABEL_BOUNDS);
			tittleLabel.setName(SynthFonts.BOLD_FONT12_PURPLE90_74_103);
		}
		return tittleLabel;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setBounds(CLOSE_BUTTON_BOUNDS);
			closeButton.setName(CLOSE_BUTTON_NAME);
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();

				}
			});
		}
		return closeButton;
	}

	private ImagePanel getPhotoPanel() {
		if (photoPanel == null) {
			photoPanel = new ImagePanel();
			photoPanel.setLayout(new BorderLayout());
			photoPanel.setSize(PHOTO_PANEL_SIZE);
			photoPanel.setPreferredSize(PHOTO_PANEL_SIZE);
			photoPanel.setBounds(PHOTO_PANEL_BOUNDS);
			photoPanel.setImage(ImageUtil.getImage(contactInfo.getAvatar()), ARC_IMAGE, ARC_IMAGE);
			photoPanel.add(getPortraitMask(), BorderLayout.CENTER);
			photoPanel.setToolTipText(contactInfo.getMessage());
		}
		return photoPanel;
	}

	private JPanel getPortraitMask() {
		if (portraitMask == null) {
			portraitMask = new JPanel();
			portraitMask.setLayout(null);
			portraitMask.setName(PROFILE_PORTRAIT_MASK_NAME);
		}
		return portraitMask;
	}

	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel();
			nameLabel.setBounds(NAME_LABEL_BOUNDS);
			nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			nameLabel.setName(SynthFonts.BOLD_FONT12_PURPLE90_74_103);
			nameLabel.setText("<html><p align='CENTER' style='color: #5A4A67; font-size: 12pt; font-weight: bold'>"
					+ contactInfo.getNickName() + "</p></html>");
		}
		return nameLabel;
	}

	private JLabel getCityLabel() {
		if (cityLabel == null) {
			cityLabel = new JLabel();
			cityLabel.setBounds(CITY_LABEL_BOUNDS);
			cityLabel.setName(SynthFonts.PLAIN_FONT11_PURPLE90_74_103);
			cityLabel.setHorizontalAlignment(SwingConstants.CENTER);
			if (contactInfo.getCity() != null) {
				String cityName = contactInfo.getCity().getCityName() == null ? "" : contactInfo.getCity().getCityName();
				String stateName = contactInfo.getCity().getStateName() == null ? "" : contactInfo.getCity().getStateName();
				cityLabel.setText(cityName + " " + stateName);
			}
		}
		return cityLabel;
	}

	private JButton getInvitationButton() {
		if (invitationButton == null) {
			invitationButton = new JButton();
			invitationButton.setBounds(INVITATION_BUTTON_BOUNDS);
			invitationButton.setName(INVITATION_BUTTON_NAME);
			invitationButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
					addContactFlow.executeRequest(contactInfo, false);
				}
			});
		}
		return invitationButton;
	}

	private JLabel getCountryLabel() {
		if (countryLabel == null) {
			countryLabel = new JLabel();
			countryLabel.setBounds(COUNTRY_LABEL_BOUNDS);
			countryLabel.setName(SynthFonts.PLAIN_FONT11_PURPLE90_74_103);
			countryLabel.setHorizontalAlignment(SwingConstants.CENTER);
			countryLabel.setText(contactInfo.getCity().getCountryName());
		}
		return countryLabel;
	}

	@Override
	public void internationalize(Messages messages) {
		tittleLabel.setText(messages.getMessage("addFriend.dialog.title"));
		invitationButton.setText(messages.getMessage("addFriend.dialog.button"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}
