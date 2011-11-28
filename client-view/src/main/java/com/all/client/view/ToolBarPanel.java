package com.all.client.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatType;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.AddContactFlow;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.actions.ShareContentAction;
import com.all.core.common.model.ApplicationModel;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

@Component
public class ToolBarPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final int MINIMUM_WIDTH = 850;

	private static final Dimension DEFAULT_BUTTON_SIZE = new Dimension(89, 30);

	private static final Dimension DEFAULT_CORNER_BUTTON_SIZE = new Dimension(98, 30);

	private static final Dimension DEFAULT_SIZE = new Dimension(818, 32);

	private static final Dimension SEPARATOR_PANEL_DEFAULT_SIZE = new Dimension(2, 30);

	private static final Dimension BUTTON_MINIMUM_SIZE = new Dimension(40, 30);

	private static final Dimension CORNER_BUTTON_MINIMUM_SIZE = new Dimension(48, 30);

	private static final Dimension MINIMUM_SIZE = new Dimension(392, 32);

	private static final String TOOLBAR_FIND_BUTTON_NAME = "toolbarFindButton";

	private static final String TOOLBAR_DOWNLOADS_BUTTON_NAME = "toolbarDownloadsButton";

	private static final String TOOLBAR_SOCIAL_BUTTON_NAME = "toolbarSocialButton";

	private static final String TOOLBAR_HOME_BUTTON_NAME = "toolbarHomeButton";

	private static final String TOOLBAR_SEARCH_BUTTON_NAME = "toolbarSearchButton";

	private static final String TOOLBAR_BACKGROUND_PANEL_NAME = "toolbarBackgroundPanel";

	private static final String SEPARATOR_PANEL_NAME = "separatorToolbarPanel";

	private static final String TOOLBAR_INVITE_BUTTON_NAME = "toolbarInviteButton";

	private static final String TOOLBAR_USERS_BUTTON_NAME = "toolbarUsersButton";

	private static final String TOOLBAR_SEND_BUTTON_NAME = "toolbarSendButton";

	private static final String TOOLBAR_HUNDRED_BUTTON_NAME = "toolbarHundredButton";

	private ButtonGroup buttonGroup;

	private JToggleButton homeToggleButton;

	private JToggleButton searchToggleButton;

	private JToggleButton downloadsToggleButton;

	private JToggleButton inviteToggleButton;

	private JToggleButton socialToggleButton;

	private JToggleButton usersToggleButton;

	private JToggleButton sendToggleButton;

	private JToggleButton findToggleButton;

	private JToggleButton hundredToggleButton;

	@Autowired
	private ViewEngine viewEngine;

	@Autowired
	private DialogFactory dialogFactory;

	private JPanel separatorPanel;

	private Messages messages;

	public ToolBarPanel() {
		initialize();
	}

	private void initialize() {
		buttonGroup = new ButtonGroup();
		this.setLayout(new GridBagLayout());
		this.setName(TOOLBAR_BACKGROUND_PANEL_NAME);
		this.setBorder(BorderFactory.createEmptyBorder(0, -2, 2, -2));
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				applyResize();
			}
		});
		GridBagConstraints toggleButtonContraints = new GridBagConstraints();
		toggleButtonContraints.gridx = 0;
		toggleButtonContraints.gridy = 0;
		toggleButtonContraints.fill = 1;
		toggleButtonContraints.weightx = 1;

		this.add(getHomeToggleButton(), toggleButtonContraints);

		GridBagConstraints separatorPanelConstraints = new GridBagConstraints();
		separatorPanelConstraints.gridy = 0;
		separatorPanelConstraints.gridx = 1;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 2;
		this.add(getSearchToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 3;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 4;
		this.add(getDownloadsToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 5;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 6;
		this.add(getSocialToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 7;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 8;
		this.add(getHundredToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 9;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 10;
		this.add(getInviteToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 11;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 12;
		this.add(getUsersToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 13;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 14;
		this.add(getSendToggleButton(), toggleButtonContraints);

		separatorPanelConstraints.gridx = 15;
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		toggleButtonContraints.gridx = 16;
		this.add(getFindToggleButton(), toggleButtonContraints);
	}

	private Views lastView = Views.LOCAL_MUSIC;

	@EventMethod(value = Model.CURRENT_VIEW_ID, eager = true)
	public void onModelViewCurrentViewChanged(ValueEvent<ContainerView> eventArgs) {
		Views value = eventArgs.getValue().getViews();
		switch (value) {
		case HOME:
			buttonGroup.setSelected(homeToggleButton.getModel(), true);
			break;
		case DOWNLOAD:
			buttonGroup.setSelected(downloadsToggleButton.getModel(), true);
			break;
		case LOCAL_MUSIC:
			clearSelectionToolbar();
			break;
		case SEARCH:
			buttonGroup.setSelected(searchToggleButton.getModel(), true);
			break;
		case CRAWLER:
			buttonGroup.setSelected(inviteToggleButton.getModel(), true);
			viewEngine.request(Actions.Social.REQUEST_CONTACTS, ChatType.ALL, new ResponseCallback<List<ContactInfo>>() {
				public void onResponse(java.util.List<ContactInfo> response) {
					dialogFactory.showImportContactsDialog(response);
				};
			});
			break;
		case PROFILE:
			buttonGroup.setSelected(socialToggleButton.getModel(), true);
			break;
		case HUNDRED:
			buttonGroup.setSelected(hundredToggleButton.getModel(), true);
			break;
		case BROWSE_MEMBERS:
			buttonGroup.setSelected(usersToggleButton.getModel(), true);
			break;
		case SEND_MUSIC:
			buttonGroup.setSelected(sendToggleButton.getModel(), true);
			viewEngine.send(Actions.Social.SHOW_SEND_CONTENT_DIALOG, new ShareContentAction());
			break;
		case FIND_FRIENDS:
			buttonGroup.setSelected(findToggleButton.getModel(), true);
			disableButtons();
			new AddContactFlow(viewEngine, dialogFactory).executeAdd("");
			enableButtons();
			viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(lastView)));
			break;
		}
		lastView = value;
	}
	
	private JToggleButton getHomeToggleButton() {
		if (homeToggleButton == null) {
			homeToggleButton = new JToggleButton();
			homeToggleButton.setName(TOOLBAR_HOME_BUTTON_NAME);
			homeToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			homeToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			homeToggleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.HOME)));
				}
			});
			buttonGroup.add(homeToggleButton);
		}
		return homeToggleButton;
	}

	private JToggleButton getSearchToggleButton() {
		if (searchToggleButton == null) {
			searchToggleButton = new JToggleButton();
			searchToggleButton.setName(TOOLBAR_SEARCH_BUTTON_NAME);
			searchToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			searchToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			searchToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.SEARCH)));
					} else {
						dialogFactory.showSearchUnavailableDialog();
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
					}
				}
			});
			buttonGroup.add(searchToggleButton);
		}
		return searchToggleButton;
	}

	private JToggleButton getDownloadsToggleButton() {
		if (downloadsToggleButton == null) {
			downloadsToggleButton = new JToggleButton();
			downloadsToggleButton.setName(TOOLBAR_DOWNLOADS_BUTTON_NAME);
			downloadsToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			downloadsToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			downloadsToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.DOWNLOAD)));
					}
				}
			});
			buttonGroup.add(downloadsToggleButton);
		}
		return downloadsToggleButton;
	}

	private JToggleButton getInviteToggleButton() {
		if (inviteToggleButton == null) {
			inviteToggleButton = new JToggleButton();
			inviteToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			inviteToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			inviteToggleButton.setName(TOOLBAR_INVITE_BUTTON_NAME);
			inviteToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.CRAWLER)));
					}
				}
			});
			buttonGroup.add(inviteToggleButton);
		}
		return inviteToggleButton;
	}

	private JToggleButton getSocialToggleButton() {
		if (socialToggleButton == null) {
			socialToggleButton = new JToggleButton();
			socialToggleButton.setName(TOOLBAR_SOCIAL_BUTTON_NAME);
			socialToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			socialToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			socialToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						ContactInfo contact = new ContactInfo(viewEngine.get(Model.CURRENT_USER));
						viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(contact));
					} else {
						dialogFactory.showProfileUnavailableDialog();
					}
				}
			});
			buttonGroup.add(socialToggleButton);
		}
		return socialToggleButton;
	}

	private JToggleButton getHundredToggleButton() {
		if (hundredToggleButton == null) {
			hundredToggleButton = new JToggleButton();
			hundredToggleButton.setName(TOOLBAR_HUNDRED_BUTTON_NAME);
			hundredToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			hundredToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			hundredToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.HUNDRED)));
					}
				}
			});
			buttonGroup.add(hundredToggleButton);
		}
		return hundredToggleButton;
	}

	private JToggleButton getUsersToggleButton() {
		if (usersToggleButton == null) {
			usersToggleButton = new JToggleButton();
			usersToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			usersToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			usersToggleButton.setName(TOOLBAR_USERS_BUTTON_NAME);
			usersToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.BROWSE_MEMBERS)));
					}
				}
			});
			buttonGroup.add(usersToggleButton);
		}
		return usersToggleButton;
	}

	private JToggleButton getSendToggleButton() {
		if (sendToggleButton == null) {
			sendToggleButton = new JToggleButton();
			sendToggleButton.setName(TOOLBAR_SEND_BUTTON_NAME);
			sendToggleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			sendToggleButton.setMinimumSize(BUTTON_MINIMUM_SIZE);
			sendToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.SEND_MUSIC)));
					}
				}
			});
			buttonGroup.add(sendToggleButton);
		}
		return sendToggleButton;
	}

	private JToggleButton getFindToggleButton() {
		if (findToggleButton == null) {
			findToggleButton = new JToggleButton();
			findToggleButton.setName(TOOLBAR_FIND_BUTTON_NAME);
			findToggleButton.setPreferredSize(DEFAULT_CORNER_BUTTON_SIZE);
			findToggleButton.setMinimumSize(CORNER_BUTTON_MINIMUM_SIZE);
			findToggleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
						viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.FIND_FRIENDS)));
					}
				}
			});
			buttonGroup.add(findToggleButton);
		}
		return findToggleButton;
	}

	public JPanel getSeparatorPanel() {
		separatorPanel = new JPanel();
		separatorPanel.setSize(SEPARATOR_PANEL_DEFAULT_SIZE);
		separatorPanel.setMinimumSize(SEPARATOR_PANEL_DEFAULT_SIZE);
		separatorPanel.setPreferredSize(SEPARATOR_PANEL_DEFAULT_SIZE);
		separatorPanel.setMaximumSize(SEPARATOR_PANEL_DEFAULT_SIZE);
		separatorPanel.setName(SEPARATOR_PANEL_NAME);
		return separatorPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		applyResize();
		homeToggleButton.setToolTipText(messages.getMessage("home.tooltip"));
		searchToggleButton.setToolTipText(messages.getMessage("search.tooltip"));
		downloadsToggleButton.setToolTipText(messages.getMessage("downloads.tooltip"));
		socialToggleButton.setToolTipText(messages.getMessage("profile.tooltip"));
		inviteToggleButton.setToolTipText(messages.getMessage("invite.tooltip"));
		usersToggleButton.setToolTipText(messages.getMessage("browseMembers.tooltip"));
		sendToggleButton.setToolTipText(messages.getMessage("send.tooltip"));
		findToggleButton.setToolTipText(messages.getMessage("find.tooltip"));
		hundredToggleButton.setToolTipText(messages.getMessage("top100.tooltip"));
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
		this.messages = messages;
		internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	private void removeButtonsText() {
		homeToggleButton.setText("");
		searchToggleButton.setText("");
		downloadsToggleButton.setText("");
		socialToggleButton.setText("");
		inviteToggleButton.setText("");
		usersToggleButton.setText("");
		sendToggleButton.setText("");
		findToggleButton.setText("");
		hundredToggleButton.setText("");
	}

	public void clearSelectionToolbar() {
		buttonGroup.clearSelection();
	}

	private void disableButtons() {
		Enumeration<AbstractButton> elements = buttonGroup.getElements();
		while (elements.hasMoreElements()) {
			AbstractButton abstractButton = (AbstractButton) elements.nextElement();
			abstractButton.setEnabled(false);
		}
	}

	private void enableButtons() {
		Enumeration<AbstractButton> elements = buttonGroup.getElements();
		while (elements.hasMoreElements()) {
			AbstractButton abstractButton = (AbstractButton) elements.nextElement();
			abstractButton.setEnabled(true);
		}
	}

	private void applyResize() {
		if (getWidth() <= MINIMUM_WIDTH) {
			removeButtonsText();
		} else {
			homeToggleButton.setText(messages.getMessage("toolbar.home"));
			searchToggleButton.setText(messages.getMessage("toolbar.search"));
			downloadsToggleButton.setText(messages.getMessage("toolbar.downloads"));
			inviteToggleButton.setText(messages.getMessage("toolbar.invite"));
			socialToggleButton.setText(messages.getMessage("toolbar.social"));
			usersToggleButton.setText(messages.getMessage("toolbar.users"));
			sendToggleButton.setText(messages.getMessage("toolbar.send"));
			findToggleButton.setText(messages.getMessage("toolbar.find"));
			hundredToggleButton.setText(messages.getMessage("toolbar.hundred"));
		}
	}

}
