package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.ResizeImageType;
import com.all.client.util.Formatters;
import com.all.client.view.View;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.ImageDropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.flows.EditProfileFlow;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.core.model.Profile;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

public final class ProfileInfoPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final int IMAGE_ARC = 0;

	private static final Dimension DEFAULT_SIZE = new Dimension(170, 466);

	private static final Dimension NAME_BUTTON_PANEL_DEFAULT_SIZE = new Dimension(170, 30);

	private static final Dimension PROFILE_PHOTO_PANEL_DEFAULT_SIZE = new Dimension(150, 150);

	private static final Dimension QUOTE_NAME_PANEL_DEFAULT_SIZE = new Dimension(150, 57);

	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(170, 2);

	private static final Insets BOTTOM_SEPARATOR_INSETS = new Insets(0, 0, 30, 0);

	private static final Insets QUOTE_PANEL_CONSTRAINTS = new Insets(10, 0, 10, 0);

	private static final Rectangle NAME_LABEL_BOUNDS = new Rectangle(10, 0, 130, 30);

	private static final Rectangle NAME_QUOTE_LABEL_BOUNDS = new Rectangle(0, 0, 150, 20);

	private static final Rectangle PROFILE_CONTROL_BUTTON_BOUNDS = new Rectangle(142, 2, 28, 28);

	private static final String NAME = "profileInfoBackgroundPanel";

	private static final String VIEW_LIBRARY_BUTTON_NAME = "showRemoteLibraryProfileButton";

	private static final String PROFILE_PORTRAIT_MASK_NAME = "profilePortraitMask";

	private static final String EDIT_PROFILE_BUTTON_NAME = "editProfileInfoButton";

	private final DialogFactory dialogFactory;

	private ImagePanel profilePhotoPanel;

	private JButton editProfileButton;

	private JButton viewLibraryButton;

	private JLabel nickInfoLabel;

	private JPanel portraitMask;

	private QuotePanel quotePanel;

	private JPanel namePanel;

	private JSeparator bottomSeparator;

	private JSeparator topSeparator;

	private final MultiLayerDropTargetListener dropListener;

	private ContactInfo contact;

	private final Profile profile;

	private PersonalInfoPanel infoPanel;

	private final Messages messages;

	private ViewEngine viewEngine;

	private EventListener<ValueEvent<User>> profileUpdatedListener;

	private JPanel nameButtonPanel;

	private JLabel nameLabel;

	public ProfileInfoPanel(Profile profile, Messages messages, MultiLayerDropTargetListener dropListener,
			DialogFactory dialogFactory) {
		this.profile = profile;
		this.messages = messages;
		this.contact = profile.getContact();
		this.dropListener = dropListener;
		this.dialogFactory = dialogFactory;
		this.profileUpdatedListener = new ProfileUpdatedListener();
	}

	private void initialize(final Messages messages) {
		this.setName(NAME);
		this.setLayout(new GridBagLayout());
		this.setMinimumSize(DEFAULT_SIZE);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);

		GridBagConstraints nameButtonPanelConstraints = new GridBagConstraints();

		GridBagConstraints profilephotoPanelConstraints = new GridBagConstraints();
		profilephotoPanelConstraints.gridy = 1;

		GridBagConstraints topSeparatorConstraints = new GridBagConstraints();
		topSeparatorConstraints.gridy = 3;

		GridBagConstraints quotePanelConstraints = new GridBagConstraints();
		quotePanelConstraints.gridy = 2;
		quotePanelConstraints.insets = QUOTE_PANEL_CONSTRAINTS;

		GridBagConstraints infoPanelConstraints = new GridBagConstraints();
		infoPanelConstraints.gridy = 4;
		infoPanelConstraints.fill = GridBagConstraints.VERTICAL;
		infoPanelConstraints.weighty = 1.0;

		GridBagConstraints bottomSeparatorConstraints = new GridBagConstraints();
		bottomSeparatorConstraints.gridy = 5;
		bottomSeparatorConstraints.insets = BOTTOM_SEPARATOR_INSETS;

		this.add(getNameButtonPanel(), nameButtonPanelConstraints);
		this.add(getProfilePhotoPanel(), profilephotoPanelConstraints);
		this.add(getTopSeparator(), topSeparatorConstraints);
		this.add(getNamePanel(), quotePanelConstraints);
		this.add(getInfoPanel(), infoPanelConstraints);
		this.add(getBottomSeparator(), bottomSeparatorConstraints);
		setup();
	}

	private void setup() {
		setDragAndDrop(dropListener, dialogFactory);
	}

	private JPanel getNameButtonPanel() {
		if (nameButtonPanel == null) {
			nameButtonPanel = new JPanel();
			nameButtonPanel.setLayout(null);
			nameButtonPanel.setPreferredSize(NAME_BUTTON_PANEL_DEFAULT_SIZE);
			nameButtonPanel.setMinimumSize(NAME_BUTTON_PANEL_DEFAULT_SIZE);
			nameButtonPanel.add(getNickInfoLabel());
			if (profile.isLocal()) {
				nameButtonPanel.add(getEditProfileButton());
			} else {
				nameButtonPanel.add(getViewLibraryButton());
			}
		}
		return nameButtonPanel;
	}

	private PersonalInfoPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new PersonalInfoPanel(contact);
		}
		return infoPanel;
	}

	private JSeparator getBottomSeparator() {
		if (bottomSeparator == null) {
			bottomSeparator = new JSeparator();
			bottomSeparator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
			bottomSeparator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		}
		return bottomSeparator;
	}

	private JSeparator getTopSeparator() {
		if (topSeparator == null) {
			topSeparator = new JSeparator();
			topSeparator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
			topSeparator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		}
		return topSeparator;
	}

	private JButton getViewLibraryButton() {
		if (viewLibraryButton == null) {
			viewLibraryButton = new JButton();
			viewLibraryButton.setName(VIEW_LIBRARY_BUTTON_NAME);
			viewLibraryButton.setBounds(PROFILE_CONTROL_BUTTON_BOUNDS);
			viewLibraryButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String mail = contact.getEmail();
					viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(mail));
				}
			});
		}
		return viewLibraryButton;
	}

	private JButton getEditProfileButton() {
		if (editProfileButton == null) {
			editProfileButton = new JButton();
			editProfileButton.setName(EDIT_PROFILE_BUTTON_NAME);
			editProfileButton.setBounds(PROFILE_CONTROL_BUTTON_BOUNDS);
			editProfileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new EditProfileFlow(viewEngine, dialogFactory).execute(true);
				}
			});
		}
		return editProfileButton;
	}

	private JPanel getNamePanel() {
		if (namePanel == null) {
			namePanel = new JPanel();
			namePanel.setLayout(null);
			namePanel.setPreferredSize(QUOTE_NAME_PANEL_DEFAULT_SIZE);
			namePanel.setMinimumSize(QUOTE_NAME_PANEL_DEFAULT_SIZE);
			namePanel.add(getNameLabel());
			namePanel.add(getQuotePanel());
		}
		return namePanel;
	}

	private QuotePanel getQuotePanel() {
		if (quotePanel == null) {
			quotePanel = new QuotePanel(profile, viewEngine);
		}
		return quotePanel;
	}

	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel();
			nameLabel.setBounds(NAME_QUOTE_LABEL_BOUNDS);
			nameLabel.setName(SynthFonts.BOLD_FONT14_87_63_106);
		}
		return nameLabel;
	}

	private ImagePanel getProfilePhotoPanel() {
		if (profilePhotoPanel == null) {
			profilePhotoPanel = new ImagePanel();
			profilePhotoPanel.setLayout(new BorderLayout());
			profilePhotoPanel.setPreferredSize(PROFILE_PHOTO_PANEL_DEFAULT_SIZE);
			profilePhotoPanel.setMinimumSize(PROFILE_PHOTO_PANEL_DEFAULT_SIZE);
			profilePhotoPanel.setImage(ImageUtil.getImage(contact.getAvatar()), IMAGE_ARC, IMAGE_ARC);
			profilePhotoPanel.add(getPortraitMask(), BorderLayout.CENTER);
		}
		return profilePhotoPanel;
	}

	private JPanel getPortraitMask() {
		if (portraitMask == null) {
			portraitMask = new JPanel();
			portraitMask.setLayout(null);
			portraitMask.setName(PROFILE_PORTRAIT_MASK_NAME);
		}
		return portraitMask;
	}

	private JLabel getNickInfoLabel() {
		if (nickInfoLabel == null) {
			nickInfoLabel = new JLabel();
			nickInfoLabel.setBounds(NAME_LABEL_BOUNDS);
			nickInfoLabel.setName(SynthFonts.BOLD_FONT14_87_63_106);
		}
		return nickInfoLabel;
	}

	@Override
	public void internationalize(final Messages messages) {
		if (profile.isLocal()) {
			editProfileButton.setToolTipText(messages.getMessage("profile.edit.button.tooltip"));
		} else {
			viewLibraryButton.setToolTipText(messages.getMessage("profile.view.button.tooltip"));
		}
		nickInfoLabel.setText(messages.getMessage("profile.info", contact.getNickName()));
		nameLabel.setText(contact.getFirstName() + " " + contact.getLastName());
		getInfoPanel().internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		infoPanel.removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		infoPanel.setMessages(messages);
	}

	private void setDragAndDrop(MultiLayerDropTargetListener dropListener, DialogFactory dialogFactory) {
		ImageDropListener listener = new ImageDropListener(getProfilePhotoPanel(), dialogFactory,
				ResizeImageType.editPhotoDialog);
		dropListener.removeListeners(getProfilePhotoPanel());
		
		if(viewEngine.get(Model.CURRENT_USER).getEmail().equals(contact.getEmail())){
			dropListener.addDropListener(getProfilePhotoPanel(), listener);
		}
		
		listener.onDropped().add(new Observer<ObservValue<ImagePanel>>() {
			@Override
			public void observe(ObservValue<ImagePanel> eventArgs) {
				changeAvatar(eventArgs.getValue());
			}
		});
	}

	private void changeAvatar(ImagePanel imagePanel) {
		Image avatar = imagePanel.getImage();
		viewEngine.sendValueAction(Actions.UserProfile.UPDATE_AVATAR, avatar);
	}

	public void onContactUpdated(ContactInfo contactInfo) {
		if (contact.equals(contactInfo)) {
			contact = contactInfo;
			updateContactInfo(contact);
		}
	}

	void enableControls() {
		if (profile.isLocal()) {
			editProfileButton.setEnabled(true);
		} else {
			viewLibraryButton.setEnabled(true);
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.UserProfile.USER_PROFILE_UPDATED, profileUpdatedListener);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize(messages);
		internationalize(messages);
		viewEngine.addListener(Events.UserProfile.USER_PROFILE_UPDATED, profileUpdatedListener);
	}

	private final class ProfileUpdatedListener extends EventListener<ValueEvent<User>> {
		@Override
		public void handleEvent(ValueEvent<User> eventArgs) {
			User user = eventArgs.getValue();
			if (viewEngine.get(Model.CURRENT_PROFILE).getContact().getEmail().equals(user.getEmail())) {
				contact = new ContactInfo(user);
				updateContactInfo(contact);
			}
		}
	}

	private String formatDate(ContactInfo contactInfo) {
		String date = Formatters.formatDate(contactInfo.getBirthday(), messages.getMessage("dateFormat"));
		return date;
	}

	private void updateContactInfo(ContactInfo contactInfo) {
		getProfilePhotoPanel().setImage(ImageUtil.getImage(contactInfo.getAvatar()), IMAGE_ARC, IMAGE_ARC);
		getQuotePanel().setTextToQuoteLabel(contactInfo.getMessage());
		infoPanel.getLocationLabel().setText(contactInfo.getCity().getCityName());
		infoPanel.getSexLabel().setText(contactInfo.getGender().getLabel());
		infoPanel.getBirthdayLabel().setText(formatDate(contactInfo));
		nickInfoLabel.setText(messages.getMessage("profile.info", contactInfo.getNickName()));
		formatNameContact(contactInfo);
	}

	private void formatNameContact(ContactInfo contactInfo) {
		if (contactInfo.getFirstName() == null || (contactInfo.getFirstName().equals(contactInfo.getNickName()) && contactInfo.getLastName().equals(contactInfo.getNickName()))) {
			nameLabel.setText(contactInfo.getNickName());
		} else {
			nameLabel.setText(contactInfo.getFirstName() + " " + contactInfo.getLastName());
		}
	}
}
