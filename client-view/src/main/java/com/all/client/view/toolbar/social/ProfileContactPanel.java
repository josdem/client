package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.ShowProfileFlow;
import com.all.core.common.util.ImageUtil;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;
import com.all.shared.model.ContactInfo;

public final class ProfileContactPanel extends JPanel {

	private static final Dimension PHOTO_PANEL_DEFAULT_SIZE = new Dimension(72, 72);
	private static final Insets PHOTO_PANEL_INSETS = new Insets(6, 12, 3, 12);
	private static final Insets LAST_NAME_LABEL_INSETS = new Insets(0, 0, 6, 0);
	private static final Dimension DEFAULT_SIZE = new Dimension(96, 118);
	private static final long serialVersionUID = 1L;
	private static final String PROFILE_PORTRAIT_MASK_NAME = "profilePortrait72DarkerMask";
	private static final String PROFILE_PORTRAIT_MASK_HIGHTLIGHT_NAME = "profilePortraitHightLightMask";
	private JLabel firstNameLabel;
	private ImagePanel photoPanel;
	private ContactInfo contactInfo;
	private JPanel portraitMask;
	private final ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	private final Observable<ObserveObject> onSelectProfileEvent = new Observable<ObserveObject>();

	public ProfileContactPanel(ContactInfo contactInfo, ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.contactInfo = contactInfo;
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setPreferredSize(DEFAULT_SIZE);

		GridBagConstraints firstNameLabelConstratints = new GridBagConstraints();
		firstNameLabelConstratints.gridx = 0;
		firstNameLabelConstratints.gridy = 1;

		GridBagConstraints lastNameLabelConstraints = new GridBagConstraints();
		lastNameLabelConstraints.gridx = 0;
		lastNameLabelConstraints.gridy = 2;
		lastNameLabelConstraints.insets = LAST_NAME_LABEL_INSETS;

		GridBagConstraints photoPanelConstraints = new GridBagConstraints();
		photoPanelConstraints.gridx = 0;
		photoPanelConstraints.gridy = 0;
		photoPanelConstraints.insets = PHOTO_PANEL_INSETS;

		this.add(getImagePanel(), photoPanelConstraints);
		this.add(getFirstNameLabel(), firstNameLabelConstratints);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					manageOneClick();
				}
			}
		});
	}

	private ImagePanel getImagePanel() {
		if (photoPanel == null) {
			photoPanel = new ImagePanel();
			photoPanel.setLayout(new BorderLayout());
			photoPanel.setSize(PHOTO_PANEL_DEFAULT_SIZE);
			photoPanel.setPreferredSize(PHOTO_PANEL_DEFAULT_SIZE);
			photoPanel.setMinimumSize(PHOTO_PANEL_DEFAULT_SIZE);
			photoPanel.setMaximumSize(PHOTO_PANEL_DEFAULT_SIZE);
			final Image portrait = ImageUtil.getImage(contactInfo.getAvatar());
			photoPanel.setImage(portrait, 0, 0);
			photoPanel.add(getPortraitMask(), BorderLayout.CENTER);
			photoPanel.setToolTipText(contactInfo.getMessage());
			photoPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						manageOneClick();
					} else if (e.getClickCount() == 2) {
						manageDoubleClick(e);
					}
				}
			});
		}
		return photoPanel;
	}

	private void manageDoubleClick(MouseEvent e) {
		new ShowProfileFlow(viewEngine, dialogFactory).execute(contactInfo, this);
	}
	
	public ContactInfo getContact() {
		return contactInfo;
	}

	private JPanel getPortraitMask() {
		if (portraitMask == null) {
			portraitMask = new JPanel();
			portraitMask.setLayout(null);
			portraitMask.setName(PROFILE_PORTRAIT_MASK_NAME);
		}
		return portraitMask;
	}

	private JLabel getFirstNameLabel() {
		if (firstNameLabel == null) {
			firstNameLabel = new JLabel();
			firstNameLabel.setText(contactInfo.getNickName());
		}
		return firstNameLabel;
	}

	public void setHightLightPhotoPanel(boolean isHightLight) {
		if (isHightLight) {
			portraitMask.setName(PROFILE_PORTRAIT_MASK_HIGHTLIGHT_NAME);
		} else {
			portraitMask.setName(PROFILE_PORTRAIT_MASK_NAME);
		}

	}

	private void manageOneClick() {
		requestFocusInWindow();
		onSelectProfileEvent.fire(ObserveObject.EMPTY);
	}

	public ObserverCollection<ObserveObject> onSelectProfile() {
		return onSelectProfileEvent;
	}

	public void refresh(ContactInfo contact) {
		contactInfo = contact;
		photoPanel.setToolTipText(contactInfo.getMessage());
		final Image portrait = ImageUtil.getImage(contactInfo.getAvatar());
		photoPanel.setImage(portrait, 0, 0);
		firstNameLabel.setText(contactInfo.getNickName());
	}

}
