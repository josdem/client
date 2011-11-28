package com.all.client.view.contacts;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.view.components.ImagePanel;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.shared.model.ContactInfo;

public class ContactInfoPanel extends JPanel {

	private static final long serialVersionUID = 7935894979679132883L;
	
	private static final double ARC_IMAGE = .17;

	private static final Dimension AVATAR_PANEL_PREFERRED_SIZE = new Dimension(70, 70);
	
	private static final Dimension PREFERRED_SIZE = new Dimension(312, 72);
	
	private static final Rectangle AVATAR_PANEL_BOUNDS = new Rectangle(0, 1, 70, 70);

	private static final Rectangle INFO_PANEL_BOUNDS = new Rectangle(78, 1, 234, 70);

	private static final Log log = LogFactory.getLog(ContactInfoPanel.class);

	private ImagePanel avatarPanel = new ImagePanel();

	private JLabel cityLabel;
	
	private JLabel nameLabel;
	
	private JLabel nicknameLabel;

	private JPanel infoPanel;

	public ContactInfoPanel() {
		this.setLayout(null);
		this.setPreferredSize(PREFERRED_SIZE);
		avatarPanel.setPreferredSize(AVATAR_PANEL_PREFERRED_SIZE);
		avatarPanel.setOpaque(false);
		avatarPanel.setBounds(AVATAR_PANEL_BOUNDS);
		
		this.add(avatarPanel);
		this.add(getInfoPanel());
	}

	public void fillData(ContactInfo contact) {
		getNicknameLabel().setText(contact.getNickName());
		getInfoPanel().add(getNicknameLabel());
		if(contact.getNickName() != null && !contact.getNickName().equals(StringUtils.EMPTY)){
			getNameLabel().setText(contact.getName());
			getInfoPanel().add(getNameLabel());
		}
		getCityLabel().setText(contact.getCity().toString());
		getInfoPanel().add(getCityLabel());
		
		try {
			avatarPanel.setImage(ImageUtil.getImage(contact.getAvatar()), ARC_IMAGE, ARC_IMAGE);
		} catch (NullPointerException e) {
			log.warn(e, e);
		}
	}
	
	public JPanel getInfoPanel() {
		if(infoPanel == null){
			infoPanel = new JPanel();
			infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
			infoPanel.setBounds(INFO_PANEL_BOUNDS);
		}
		return infoPanel;
	}
	
	public JLabel getNameLabel() {
		if(nameLabel == null){
			nameLabel = new JLabel();
			nameLabel.setOpaque(false);
			nameLabel.setName(SynthFonts.BOLD_FONT12_PURPLE100_45_145);
		}
		return nameLabel;
	}
	
	public JLabel getCityLabel() {
		if(cityLabel == null){
			cityLabel = new JLabel();
			cityLabel.setOpaque(false);
			cityLabel.setName(SynthFonts.PLAIN_FONT11_GRAY51_51_51);
		}
		return cityLabel;
	}
	
	public JLabel getNicknameLabel() {
		if(nicknameLabel == null){
			nicknameLabel = new JLabel();
			nicknameLabel.setOpaque(false);
			nicknameLabel.setName(SynthFonts.BOLD_FONT13_51_00_51);
		}
		return nicknameLabel;
	}

}
