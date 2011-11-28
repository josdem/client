package com.all.client.view.contacts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.ResizeImageType;
import com.all.client.view.EditingComponent;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.ImageDropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.flows.AddContactFlow;
import com.all.client.view.flows.EditProfileFlow;
import com.all.core.actions.Actions;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.User;

@Component
public class ContactListHeaderPanel extends JPanel implements Internationalizable {

	private static final int MAX_QUOTE_LENGTH = 256;

	private static final double ARC_IMAGE = .17;

	private static final long serialVersionUID = 1L;

	private static final Border SEARCH_CONTACT_TEXTFIELD_BORDER = BorderFactory.createLineBorder(Color.gray, 0);

	private static final Dimension ADD_CONTACT_BUTTON_DEFAULT_SIZE = new Dimension(28, 26);

	private static final Dimension EDIT_PROFILE_BUTTON_DEFAULT_SIZE = new Dimension(28, 26);

	private static final Dimension BUTTON_CONTAINER_PANEL_DEFAULT_SIZE = new Dimension(87, 34);

	private static final Dimension BUTTON_DEFAULT_SIZE = new Dimension(20, 20);

	private static final Dimension CLEAR_SEARCH_BUTTON_DEFAULT_SIZE = new Dimension(30, 22);

	private static final Dimension DEFAULT_SIZE = new Dimension(220, 88);

	private static final Dimension MAXIMUM_SIZE = new Dimension(2147483647, 88);

	private static final Dimension MINIMUM_SIZE = new Dimension(0, 36);

	private static final Dimension PROFILE_NAME_PANEL_DEFAULT_SIZE = new Dimension(100, 20);

	private static final Dimension PROFILE_NAME_PANEL_MAXIMUM_SIZE = new Dimension(2147483647, 20);

	private static final Dimension PROFILE_NAME_PANEL_MINIMUM_SIZE = new Dimension(0, 20);

	private static final Dimension PROFILE_PANEL_DEFAULT_SIZE = new Dimension(10, 50);

	private static final Dimension PROFILE_PANEL_MAXIMUM_SIZE = new Dimension(2147483647, 50);

	private static final Dimension PROFILE_PANEL_MINIMUM_SIZE = new Dimension(0, 50);

	private static final Dimension PROFILE_PICTURE_PANEL_DEFAULT_SIZE = new Dimension(42, 42);

	private static final Dimension SEARCH_BUTTON_DEFAULT_SIZE = new Dimension(28, 22);

	private static final Dimension SEARCH_CONTACT_PANEL_DEFAULT_SIZE = new Dimension(165, 22);

	private static final Dimension SEARCH_CONTACT_PANEL_MAXIMUM_SIZE = new Dimension(500, 22);

	private static final Dimension SEARCH_CONTACT_TEXTFIELD_DEFAULT_SIZE = new Dimension(90, 22);

	private static final Dimension SEARCH_CONTACT_TEXTFIELD_MAXIMUM_SIZE = new Dimension(445, 22);

	private static final Dimension TOOLBAR_PANEL_MINIMUM_SIZE = new Dimension(0, 34);

	private static final Dimension TOOLBAR_PANEL_MAXIMUM_SIZE = new Dimension(2147483647, 34);

	private static final Dimension TOOLBAR_PANEL_DEFAULT_SIZE = new Dimension(10, 34);

	private static final Insets ADD_CONTACT_BUTTON_INSETS = new Insets(4, 11, 4, 10);

	private static final Insets EDIT_PROFILE_BUTTON_INSETS = new Insets(4, 0, 4, 10);

	private static final Insets INSETS = new Insets(0, 0, 2, 0);

	private static final Insets MESSAGE_PANEL_INSETS = new Insets(1, 0, 4, 5);

	private static final Insets PROFILE_NAME_LABEL_INSETS = new Insets(0, 5, 0, 5);

	private static final Insets PROFILE_NAME_PANEL_INSETS = new Insets(4, 0, 1, 5);

	private static final Insets PROFILE_PICTURE_PANEL_INSETS = new Insets(4, 4, 4, 5);

	private static final Insets SEARCH_CONTACT_PANEL_INSETS = new Insets(6, 0, 6, 0);

	private static final String ADD_CONTACT_BUTTON_NAME = "addContactButton";

	private static final String EDIT_HEADER_PROFILE_BUTTON_NAME = "editHeaderProfileButton";

	private static final String CLEAN_SEARCH_BUTTON_VISIBLE_NAME = "clearSearchButtonVisible";

	private static final String CLEAN_SEARCH_BUTTON_INVISIBLE_NAME = "clearSearchButtonInvisible";

	private static final String EDIT_PROFILE_BUTTON_NAME = "editProfileButton";

	private static final String EDIT_QUOTE_BUTTON_NAME = "tuercaProfileButton";

	private static final String NAME = "contactListHeader";

	private static final String PROFILE_MESSAGE_PANEL_NAME = "editProfilePanel";

	private static final String PROFILE_PICTURE_MASK_NAME = "contactListHeaderProfilePictureMask";

	private static final String SEARCH_BUTTON_NAME = "lupaSearchButton";

	private static final String SEARCH_CONTACT_TEXT_FIELD_NAME = "searchTextField";

	private static final String TOOLTIP_ADD_CONTACT = "tooltip.addContact";

	private static final String TOOLTIP_PHOTO = "tooltip.photo";

	private static final String TOOLTIP_SEARCH_CONTACT = "tooltip.searchContact";

	private static final String TOOLTIP_WRITE_QUOTE = "tooltip.writeQuote";

	private static final String TOOLTIP_WRITE_PROFILE = "tooltip.writeProfile";

	private EditingComponent<JLabel, JTextField> edittingComponent;

	private ImagePanel profilePicturePanel = null;

	private JButton addContactButton = null;

	private JButton clearSearchButton = null;

	private JButton editProfileButton = null;

	private JButton editQouteButton;

	private JButton searchButton = null;

	private JLabel quoteLabel = null;

	private JLabel profileNameLabel = null;

	private JPanel buttonContainerPanel = null;

	private JPanel quotePanel = null;

	private JPanel profileNamePanel = null;

	private JPanel profilePanel = null;

	private JPanel profilePictureMask = null;

	private JPanel searchContactPanel = null;

	private JPanel toolbarPanel = null;

	private JTextField profileTextField;

	private JTextField searchContactTextField = null;

	private JButton editButton;

	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private DialogFactory dialogFactory;

	/**
	 * This is the default constructor
	 */
	public ContactListHeaderPanel() {
		super();
		initialize();
	}

	@PostConstruct
	public void setup() {
		final SearchContactTextFieldListener listener = new SearchContactTextFieldListener();
		searchContactTextField.getDocument().addDocumentListener(listener);
		searchContactTextField.addFocusListener(listener);

		getCleanSearchButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchContactTextField.requestFocus();
				searchContactTextField.setText("");
			}
		});

		addContactButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AddContactFlow(viewEngine, dialogFactory).executeAdd();
			}
		});
		editProfileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EditProfileFlow(viewEngine, dialogFactory).execute(false);
			}
		});

		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new EditProfileFlow(viewEngine, dialogFactory).execute(false);
			}
		});

		profileTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					saveData();
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					edittingComponent.endEdit();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				if (profileTextField.getText().length() >= MAX_QUOTE_LENGTH
						&& (profileTextField.getSelectedText() == null || profileTextField.getSelectedText().length() == 0)) {
					Toolkit.getDefaultToolkit().beep();
					e.consume();
				}
			}
		});
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onUserSessionStarted() {
		updateUserProfile(viewEngine.get(Model.CURRENT_USER));
	}

	@EventMethod(Events.UserProfile.USER_PROFILE_UPDATED_ID)
	public void onUserProfileUpdated(ValueEvent<User> event) {
		updateUserProfile(event.getValue());
	}

	private void searchContacts() {
		String keyword = searchContactTextField.getText();
		viewEngine.sendValueAction(Actions.Social.SEARCH_CONTACTS_LOCALLY, keyword);
		if (StringUtils.isEmpty(keyword)) {
			getCleanSearchButton().setName(CLEAN_SEARCH_BUTTON_INVISIBLE_NAME);
			searchContactTextField.setText("");
		} else {
			getCleanSearchButton().setName(CLEAN_SEARCH_BUTTON_VISIBLE_NAME);
		}

	}

	private void updateUserProfile(User user) {
		user = viewEngine.get(Model.CURRENT_USER);
		profileNameLabel.setText(user.getNickName());
		quoteLabel.setText(user.getQuote());
		profileTextField.setText(user.getQuote());
		profilePicturePanel.setImage(ImageUtil.getImage(user.getAvatar()), ARC_IMAGE, ARC_IMAGE);
	}

	@Autowired
	public void setDragAndDrops(final MultiLayerDropTargetListener multiLayerDropTargetListener) {
		ImageDropListener listener = new ImageDropListener(getProfilePicturePanel(), dialogFactory,
				ResizeImageType.editPhotoDialog);
		multiLayerDropTargetListener.addDropListener(getProfilePicturePanel(), listener);
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

	private void saveData() {
		getEditQouteButton().setVisible(false);
		viewEngine.sendValueAction(Actions.UserProfile.UPDATE_QUOTE, profileTextField.getText());
		edittingComponent.endEdit();
	}

	private void initialize() {
		GridBagConstraints profilePanelConstraints = new GridBagConstraints();
		profilePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		profilePanelConstraints.gridx = 0;
		profilePanelConstraints.gridy = 1;
		profilePanelConstraints.weightx = 1.0;
		profilePanelConstraints.weighty = 1.0;
		profilePanelConstraints.insets = INSETS;
		GridBagConstraints toolbarPanelConstraints = new GridBagConstraints();
		toolbarPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		toolbarPanelConstraints.gridy = -1;
		toolbarPanelConstraints.weightx = 1.0;
		toolbarPanelConstraints.weighty = 1.0;
		toolbarPanelConstraints.insets = INSETS;
		toolbarPanelConstraints.gridx = -1;
		this.setSize(DEFAULT_SIZE);
		this.setLayout(new GridBagLayout());
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.add(getToolbarPanel(), toolbarPanelConstraints);
		this.add(getProfilePanel(), profilePanelConstraints);
		this.setName(NAME);
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getToolbarPanel() {
		if (toolbarPanel == null) {
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			GridBagConstraints searchContactPanelConstraints = new GridBagConstraints();
			searchContactPanelConstraints.gridx = 2;
			searchContactPanelConstraints.weightx = 1.0;
			searchContactPanelConstraints.weighty = 1.0;
			searchContactPanelConstraints.fill = GridBagConstraints.BOTH;
			searchContactPanelConstraints.insets = SEARCH_CONTACT_PANEL_INSETS;
			searchContactPanelConstraints.gridy = 0;
			toolbarPanel = new JPanel();
			toolbarPanel.setLayout(new GridBagLayout());
			toolbarPanel.setMaximumSize(TOOLBAR_PANEL_MAXIMUM_SIZE);
			toolbarPanel.setPreferredSize(TOOLBAR_PANEL_DEFAULT_SIZE);
			toolbarPanel.setMinimumSize(TOOLBAR_PANEL_MINIMUM_SIZE);
			toolbarPanel.add(getButtonContainerPanel(), gridBagConstraints);
			toolbarPanel.add(getSearchContactPanel(), searchContactPanelConstraints);
		}
		return toolbarPanel;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getProfilePanel() {
		if (profilePanel == null) {
			GridBagConstraints messagePanelConstraints = new GridBagConstraints();
			messagePanelConstraints.gridx = 1;
			messagePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			messagePanelConstraints.weightx = 1.0;
			messagePanelConstraints.weighty = 1.0;
			messagePanelConstraints.insets = MESSAGE_PANEL_INSETS;
			messagePanelConstraints.gridy = 1;
			GridBagConstraints profileNamePanelConstraints = new GridBagConstraints();
			profileNamePanelConstraints.gridx = 1;
			profileNamePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			profileNamePanelConstraints.weightx = 1.0;
			profileNamePanelConstraints.weighty = 1.0;
			profileNamePanelConstraints.insets = PROFILE_NAME_PANEL_INSETS;
			profileNamePanelConstraints.gridy = 0;
			GridBagConstraints profilePicturePanelConstraints = new GridBagConstraints();
			profilePicturePanelConstraints.gridx = 0;
			profilePicturePanelConstraints.gridheight = 2;
			profilePicturePanelConstraints.insets = PROFILE_PICTURE_PANEL_INSETS;
			profilePicturePanelConstraints.gridy = 0;
			profilePanel = new JPanel();
			profilePanel.setLayout(new GridBagLayout());
			profilePanel.setMinimumSize(PROFILE_PANEL_MINIMUM_SIZE);
			profilePanel.setPreferredSize(PROFILE_PANEL_DEFAULT_SIZE);
			profilePanel.setMaximumSize(PROFILE_PANEL_MAXIMUM_SIZE);
			profilePanel.add(getProfilePicturePanel(), profilePicturePanelConstraints);
			profilePanel.add(getProfileNamePanel(), profileNamePanelConstraints);
			profilePanel.add(getProfileMessagePanel(), messagePanelConstraints);
		}
		return profilePanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getAddContactButton() {
		if (addContactButton == null) {
			addContactButton = new JButton();
			addContactButton.setMaximumSize(ADD_CONTACT_BUTTON_DEFAULT_SIZE);
			addContactButton.setPreferredSize(ADD_CONTACT_BUTTON_DEFAULT_SIZE);
			addContactButton.setMinimumSize(ADD_CONTACT_BUTTON_DEFAULT_SIZE);
			addContactButton.setName(ADD_CONTACT_BUTTON_NAME);
		}
		return addContactButton;
	}

	private JPanel getSearchContactPanel() {
		if (searchContactPanel == null) {
			GridBagConstraints cleanSearchButtonConstraints = new GridBagConstraints();
			cleanSearchButtonConstraints.gridx = 2;
			cleanSearchButtonConstraints.gridy = 0;
			GridBagConstraints searchContactFieldConstraints = new GridBagConstraints();
			searchContactFieldConstraints.fill = GridBagConstraints.BOTH;
			searchContactFieldConstraints.gridy = 0;
			searchContactFieldConstraints.weightx = 1.0;
			searchContactFieldConstraints.weighty = 1.0;
			searchContactFieldConstraints.gridx = 1;
			GridBagConstraints searchButtonConstraints = new GridBagConstraints();
			searchButtonConstraints.gridx = 0;
			searchButtonConstraints.gridy = 0;
			searchContactPanel = new JPanel();
			searchContactPanel.setLayout(new GridBagLayout());
			searchContactPanel.setMaximumSize(SEARCH_CONTACT_PANEL_MAXIMUM_SIZE);
			searchContactPanel.setPreferredSize(SEARCH_CONTACT_PANEL_DEFAULT_SIZE);
			searchContactPanel.setMinimumSize(SEARCH_CONTACT_PANEL_DEFAULT_SIZE);
			searchContactPanel.add(getSearchButton(), searchButtonConstraints);
			searchContactPanel.add(getSearchContactField(), searchContactFieldConstraints);
			searchContactPanel.add(getCleanSearchButton(), cleanSearchButtonConstraints);
		}
		return searchContactPanel;
	}

	private ImagePanel getProfilePicturePanel() {
		if (profilePicturePanel == null) {
			profilePicturePanel = new ImagePanel();
			profilePicturePanel.setLayout(new BorderLayout());
			profilePicturePanel.setPreferredSize(PROFILE_PICTURE_PANEL_DEFAULT_SIZE);
			profilePicturePanel.setMinimumSize(PROFILE_PICTURE_PANEL_DEFAULT_SIZE);
			profilePicturePanel.setMaximumSize(PROFILE_PICTURE_PANEL_DEFAULT_SIZE);
			profilePicturePanel.add(getProfilePictureMask(), BorderLayout.CENTER);
		}
		return profilePicturePanel;
	}

	private JPanel getProfileNamePanel() {
		if (profileNamePanel == null) {
			GridBagConstraints editProfileButtonConstraints = new GridBagConstraints();
			editProfileButtonConstraints.gridx = 1;
			editProfileButtonConstraints.gridy = 0;
			GridBagConstraints profileNameLabelConstraints = new GridBagConstraints();
			profileNameLabelConstraints.gridx = 0;
			profileNameLabelConstraints.fill = GridBagConstraints.BOTH;
			profileNameLabelConstraints.weightx = 1.0;
			profileNameLabelConstraints.weighty = 1.0;
			profileNameLabelConstraints.insets = PROFILE_NAME_LABEL_INSETS;
			profileNameLabelConstraints.gridy = 0;
			profileNameLabel = new JLabel();
			profileNameLabel.setName(SynthFonts.BOLD_FONT16_GRAY240_240_240);
			profileNamePanel = new JPanel();
			profileNamePanel.setLayout(new GridBagLayout());
			profileNamePanel.setMinimumSize(PROFILE_NAME_PANEL_MINIMUM_SIZE);
			profileNamePanel.setPreferredSize(PROFILE_NAME_PANEL_DEFAULT_SIZE);
			profileNamePanel.setMaximumSize(PROFILE_NAME_PANEL_MAXIMUM_SIZE);
			profileNamePanel.add(profileNameLabel, profileNameLabelConstraints);
			profileNamePanel.add(getEditProfileButton(), editProfileButtonConstraints);
			EditProfileMouseOverListener listener = new EditProfileMouseOverListener(profileNamePanel, getEditProfileButton());
			profileNamePanel.addMouseListener(new NameMouseOverListener(profileNamePanel, getEditProfileButton()));
			getEditProfileButton().addMouseListener(listener);
		}
		return profileNamePanel;
	}

	private JPanel getProfileMessagePanel() {
		if (quotePanel == null) {
			GridBagConstraints profileMessagePanelConstraints = new GridBagConstraints();
			profileMessagePanelConstraints.gridx = 0;
			profileMessagePanelConstraints.fill = GridBagConstraints.BOTH;
			profileMessagePanelConstraints.weightx = 1.0;
			profileMessagePanelConstraints.weighty = 1.0;
			profileMessagePanelConstraints.insets = PROFILE_NAME_LABEL_INSETS;
			profileMessagePanelConstraints.gridy = 0;
			GridBagConstraints editQouteButtonConstraints = new GridBagConstraints();
			editQouteButtonConstraints.gridx = 1;
			editQouteButtonConstraints.gridy = 0;

			quoteLabel = new JLabel();
			quoteLabel.setName(SynthFonts.PLAIN_FONT12_CLEAR_GRAY240_240_240);
			profileTextField = new JTextField();
			edittingComponent = new EditingComponent<JLabel, JTextField>(quoteLabel, profileTextField, null, null);

			quotePanel = new JPanel();
			quotePanel.setLayout(new GridBagLayout());
			quotePanel.setPreferredSize(PROFILE_NAME_PANEL_DEFAULT_SIZE);
			quotePanel.setMinimumSize(PROFILE_NAME_PANEL_MINIMUM_SIZE);
			quotePanel.setMaximumSize(PROFILE_NAME_PANEL_MAXIMUM_SIZE);
			quotePanel.add(edittingComponent, profileMessagePanelConstraints);
			quotePanel.add(getEditQouteButton(), editQouteButtonConstraints);
			getEditQouteButton().addMouseListener(new EditProfileMouseOverListener(quotePanel, getEditQouteButton()));
			quotePanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						edittingComponent.startEdit();
						profileTextField.selectAll();
						profileTextField.requestFocus();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					quotePanel.setName(PROFILE_MESSAGE_PANEL_NAME);
					getEditQouteButton().setVisible(true);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (!edittingComponent.isEditing()) {
						quotePanel.setName(null);
						getEditQouteButton().setVisible(false);
					}
				}
			});
		}
		return quotePanel;
	}

	private JButton getEditProfileButton() {
		if (editProfileButton == null) {
			editProfileButton = new JButton();
			editProfileButton.setName(EDIT_PROFILE_BUTTON_NAME);
			editProfileButton.setPreferredSize(BUTTON_DEFAULT_SIZE);
			editProfileButton.setMinimumSize(BUTTON_DEFAULT_SIZE);
			editProfileButton.setMaximumSize(BUTTON_DEFAULT_SIZE);
			editProfileButton.setVisible(false);
		}
		return editProfileButton;
	}

	private JButton getEditQouteButton() {
		if (editQouteButton == null) {
			editQouteButton = new JButton();
			editQouteButton.setName(EDIT_QUOTE_BUTTON_NAME);
			editQouteButton.setPreferredSize(BUTTON_DEFAULT_SIZE);
			editQouteButton.setMinimumSize(BUTTON_DEFAULT_SIZE);
			editQouteButton.setMaximumSize(BUTTON_DEFAULT_SIZE);
			editQouteButton.setVisible(false);
		}
		return editQouteButton;
	}

	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setMaximumSize(SEARCH_BUTTON_DEFAULT_SIZE);
			searchButton.setPreferredSize(SEARCH_BUTTON_DEFAULT_SIZE);
			searchButton.setMinimumSize(SEARCH_BUTTON_DEFAULT_SIZE);
			searchButton.setName(SEARCH_BUTTON_NAME);
		}
		return searchButton;
	}

	private JTextField getSearchContactField() {
		if (searchContactTextField == null) {
			searchContactTextField = new JTextField();
			searchContactTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			searchContactTextField.setText("");
			searchContactTextField.setName(SEARCH_CONTACT_TEXT_FIELD_NAME);
			searchContactTextField.setMaximumSize(SEARCH_CONTACT_TEXTFIELD_MAXIMUM_SIZE);
			searchContactTextField.setMinimumSize(SEARCH_CONTACT_TEXTFIELD_DEFAULT_SIZE);
			searchContactTextField.setPreferredSize(SEARCH_CONTACT_TEXTFIELD_DEFAULT_SIZE);
			searchContactTextField.setSize(SEARCH_CONTACT_TEXTFIELD_DEFAULT_SIZE);
			searchContactTextField.setBorder(SEARCH_CONTACT_TEXTFIELD_BORDER);
			searchContactTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
		}
		return searchContactTextField;
	}

	private JButton getCleanSearchButton() {
		if (clearSearchButton == null) {
			clearSearchButton = new JButton();
			clearSearchButton.setPreferredSize(CLEAR_SEARCH_BUTTON_DEFAULT_SIZE);
			clearSearchButton.setMinimumSize(CLEAR_SEARCH_BUTTON_DEFAULT_SIZE);
			clearSearchButton.setMaximumSize(CLEAR_SEARCH_BUTTON_DEFAULT_SIZE);
			clearSearchButton.setName(CLEAN_SEARCH_BUTTON_INVISIBLE_NAME);
		}
		return clearSearchButton;
	}

	private JPanel getButtonContainerPanel() {
		if (buttonContainerPanel == null) {
			GridBagConstraints addContactButtonContraints = new GridBagConstraints();
			addContactButtonContraints.insets = ADD_CONTACT_BUTTON_INSETS;
			addContactButtonContraints.gridy = 0;
			addContactButtonContraints.gridx = 0;
			GridBagConstraints editButtonContraints = new GridBagConstraints();
			editButtonContraints.gridy = 0;
			editButtonContraints.gridx = 1;
			editButtonContraints.insets = EDIT_PROFILE_BUTTON_INSETS;
			buttonContainerPanel = new JPanel();
			buttonContainerPanel.setLayout(new GridBagLayout());
			buttonContainerPanel.setMinimumSize(BUTTON_CONTAINER_PANEL_DEFAULT_SIZE);
			buttonContainerPanel.setMaximumSize(BUTTON_CONTAINER_PANEL_DEFAULT_SIZE);
			buttonContainerPanel.setPreferredSize(BUTTON_CONTAINER_PANEL_DEFAULT_SIZE);
			buttonContainerPanel.add(getAddContactButton(), addContactButtonContraints);
			buttonContainerPanel.add(getEditButton(), editButtonContraints);
		}
		return buttonContainerPanel;
	}

	private JButton getEditButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setMaximumSize(EDIT_PROFILE_BUTTON_DEFAULT_SIZE);
			editButton.setPreferredSize(EDIT_PROFILE_BUTTON_DEFAULT_SIZE);
			editButton.setMinimumSize(EDIT_PROFILE_BUTTON_DEFAULT_SIZE);
			editButton.setName(EDIT_HEADER_PROFILE_BUTTON_NAME);
		}
		return editButton;
	}

	private JPanel getProfilePictureMask() {
		if (profilePictureMask == null) {
			profilePictureMask = new JPanel();
			profilePictureMask.setLayout(null);
			profilePictureMask.setName(PROFILE_PICTURE_MASK_NAME);
		}
		return profilePictureMask;
	}

	private final class NameMouseOverListener extends MouseAdapter {

		private final JPanel panel;
		private final JButton button;

		public NameMouseOverListener(JPanel panel, JButton button) {
			this.panel = panel;
			this.button = button;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			panel.setName(PROFILE_MESSAGE_PANEL_NAME);
			button.setVisible(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			panel.setName(null);
			button.setVisible(false);
		}
	}

	private final class EditProfileMouseOverListener extends MouseAdapter {

		private final JPanel panel;
		private final JButton button;

		public EditProfileMouseOverListener(JPanel panel, JButton button) {
			this.panel = panel;
			this.button = button;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1) {
				edittingComponent.startEdit();
				profileTextField.selectAll();
				profileTextField.requestFocus();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			panel.setName(PROFILE_MESSAGE_PANEL_NAME);
			button.setVisible(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			panel.setName(null);
			button.setVisible(false);
		}
	}

	private final class SearchContactTextFieldListener extends FocusAdapter implements DocumentListener {

		private SearchContactTextFieldListener() {
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			searchContacts();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			searchContacts();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			searchContacts();
		}

		@Override
		public void focusGained(FocusEvent e) {
			searchContactTextField.selectAll();
		}

	}

	@Override
	public void internationalize(Messages messages) {
		getAddContactButton().setToolTipText(messages.getMessage(TOOLTIP_ADD_CONTACT));
		getEditProfileButton().setToolTipText(messages.getMessage(TOOLTIP_WRITE_PROFILE));
		getEditQouteButton().setToolTipText(messages.getMessage(TOOLTIP_WRITE_QUOTE));
		getSearchContactField().setToolTipText(messages.getMessage(TOOLTIP_SEARCH_CONTACT));
		getProfilePicturePanel().setToolTipText(messages.getMessage(TOOLTIP_PHOTO));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}
