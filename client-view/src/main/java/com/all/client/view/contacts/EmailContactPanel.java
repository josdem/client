package com.all.client.view.contacts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.view.components.ImagePanel;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.shared.model.ContactInfo;

public final class EmailContactPanel extends JPanel {

	private static final long serialVersionUID = 7935894979679132883L;

	private static final double ARC_IMAGE = .17;

	private static final Color NAME_LABEL_COLOR_FOREGROUND = new Color(50, 15, 50);

	private static final Log log = LogFactory.getLog(EmailContactPanel.class);

	private static final Dimension AVATAR_DEFAULT_SIZE = new Dimension(70, 70);

	private static final Dimension PANEL_SIZE = new Dimension(324, 80);

	private static final Rectangle AVATAR_PANEL_BOUNDS = new Rectangle(24, 1, 70, 70);

	private static final Rectangle BOUNDS = new Rectangle(3, 4, 318, 72);

	private static final Rectangle CHECKBOX_BOUNDS = new Rectangle(0, 24, 20, 17);

	private static final Rectangle CITY_LABEL_BOUNDS = new Rectangle(97, 48, 240, 14);

	private static final Rectangle GENDER_LABEL_BOUNDS = new Rectangle(97, 34, 240, 14);

	private static final Rectangle MESSAGE_LABEL_BOUNDS = new Rectangle(97, 20, 222, 14);

	private static final Rectangle NAME_LABEL_BOUNDS = new Rectangle(97, 6, 240, 14);

	private static final String BACKGROUND_PANEL_NAME = "selectedContactInfoPanel";

	private static final String NAME_LABEL_NAME = "contactEmailName";

	private boolean checked = false;

	private boolean isCheckBox = false;

	private ContactInfo contact;

	private ImagePanel avatarPanel = new ImagePanel();

	private JCheckBox checkBox;

	private JPanel backgroundPanel;

	private JLabel cityLabel = new JLabel();

	private JLabel genderLabel = new JLabel();

	private JLabel messageLabel = new JLabel();

	private JLabel nameLabel = new JLabel();

	public EmailContactPanel(ContactInfo contact, boolean isCheck) {
		this.isCheckBox = isCheck;
		fillData(contact);
		initializePanel();
	}

	private void initializePanel() {
		this.setLayout(null);
		this.setSize(PANEL_SIZE);
		this.setPreferredSize(PANEL_SIZE);
		this.setMinimumSize(PANEL_SIZE);
		this.setMaximumSize(PANEL_SIZE);
		this.getBackgroundPanel().setBounds(BOUNDS);
		this.setBorder(null);

		avatarPanel.setPreferredSize(AVATAR_DEFAULT_SIZE);
		avatarPanel.setOpaque(false);
		avatarPanel.setBounds(AVATAR_PANEL_BOUNDS);
		nameLabel.setOpaque(false);

		nameLabel.setName(NAME_LABEL_NAME);
		nameLabel.setBounds(NAME_LABEL_BOUNDS);
		nameLabel.setForeground(NAME_LABEL_COLOR_FOREGROUND);

		messageLabel.setBounds(MESSAGE_LABEL_BOUNDS);
		messageLabel.setOpaque(false);
		messageLabel.setName(SynthFonts.BOLD_ITALIC_FONT11_GRAY77_77_77);

		genderLabel.setBounds(GENDER_LABEL_BOUNDS);
		genderLabel.setOpaque(false);
		genderLabel.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);

		cityLabel.setBounds(CITY_LABEL_BOUNDS);
		cityLabel.setOpaque(false);
		cityLabel.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);

		if (this.isCheckBox) {
			checkBox = new JCheckBox();
			checkBox.setBounds(CHECKBOX_BOUNDS);
			checkBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					checked = !checked;
					if (checked) {
						getBackgroundPanel().setName(BACKGROUND_PANEL_NAME);
					} else {
						getBackgroundPanel().setName("");
					}
					log.debug("EmailContactPanel.checked is: " + checked);
					repaint();
				}
			});

			this.add(checkBox);
			this.getBackgroundPanel().add(checkBox);
		}
		this.backgroundPanel.add(avatarPanel);
		this.backgroundPanel.add(nameLabel);
		this.backgroundPanel.add(messageLabel);
		this.backgroundPanel.add(genderLabel);
		this.backgroundPanel.add(cityLabel);
		this.add(getBackgroundPanel());
	}

	private void fillData(ContactInfo contact) {
		nameLabel.setText(contact.getNickName());
		messageLabel.setText(contact.getMessage());
		genderLabel.setText(contact.getGender().getLabel());
		cityLabel.setText(contact.getCity().getCityName());
		this.contact = contact;
		try {
			avatarPanel.setImage(ImageUtil.getImage(contact.getAvatar()), ARC_IMAGE, ARC_IMAGE);
		} catch (NullPointerException e) {
			log.warn(e, e);
		}
	}

	public void setBackgroundPanel(JPanel backgroundPanel) {
		this.backgroundPanel = backgroundPanel;
	}

	private JPanel getBackgroundPanel() {
		if (backgroundPanel == null) {
			backgroundPanel = new JPanel();
			backgroundPanel.setLayout(null);
		}
		return backgroundPanel;
	}

	public JCheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(JCheckBox checkBox) {
		this.checkBox = checkBox;
	}

	public boolean isChecked() {
		return checked;
	}

	@Override
	public String toString() {
		return nameLabel.getText() + " " + messageLabel.getText() + " " + genderLabel.getText() + " " + cityLabel.getText();
	}

	public ContactInfo getContactInfo() {
		return contact;
	}

	public void setBackgroundPanelSize(Dimension dimension) {
		this.getBackgroundPanel().setSize(dimension);
	}

}
