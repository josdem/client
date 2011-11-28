package com.all.client.view.toolbar.users;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.all.action.ResponseCallback;
import com.all.appControl.ViewEngineConfigurator;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.MiddleCloseablePanel;
import com.all.client.view.View;
import com.all.client.view.components.GrayBackgroundedLoaderPanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.AddContactFlow;
import com.all.core.actions.Actions;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.common.view.util.SpacerKeyListener;
import com.all.core.events.Events;
import com.all.core.model.ContainerView;
import com.all.core.model.Views;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class BrowseMembersPanel extends MiddleCloseablePanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Border SEARCH_CONTACT_TEXTFIELD_BORDER = BorderFactory.createLineBorder(Color.gray, 0);

	private static final Dimension BLANK_PANEL_DEFAULT_SIZE = new Dimension(100, 24);

	private static final Dimension BLANK_PANEL_MINIMUM_SIZE = new Dimension(1, 24);

	private static final Dimension SEARCH_CONTACT_PANEL_DEFAULT_SIZE = new Dimension(180, 22);

	private static final Dimension SEARCH_CONTACT_TEXTFIELD_DEFAULT_SIZE = new Dimension(100, 22);

	private static final Rectangle SEARCH_CONTACT_TEXFIELD_BOUNDS = new Rectangle(28, 0, 131, 22);

	private static final Rectangle SEARCH_BUTTON_BOUNDS = new Rectangle(0, 0, 28, 22);

	private static final Rectangle CLEAR_SEARCH_BUTTON_BOUNDS = new Rectangle(159, 0, 30, 22);

	private static final String CLEAN_SEARCH_BUTTON_INVISIBLE_INVALID_NAME = "clearSearchButtonInvalidInvisible";

	private static final String CLEAN_SEARCH_BUTTON_INVISIBLE_NAME = "clearSearchButtonInvisible";

	private static final String SEARCH_BUTTON_INVALID_NAME = "lupaSearchInvalidButton";

	private static final String SEARCH_BUTTON_NAME = "lupaSearchButton";

	private static final String SEARCH_CONTACT_TEXT_FIELD_INVALID_NAME = "searchTextFieldInvalid";

	private static final String SEARCH_CONTACT_TEXT_FIELD_NAME = "searchTextField";

	private DialogFactory dialogFactory;

	private FriendsMainPanel friendsMainPanel;

	private JButton clearSearchButton;

	private JButton searchButton;

	private JPanel searchContactPanel;

	private JTextField searchContactTextField;

	private JPanel centerPanel;

	private JPanel loaderPanel;

	private final ViewEngine viewEngine;
	
	private final EventListener<ValueEvent<ContactInfo>> contactUpdatedListener = new EventListener<ValueEvent<ContactInfo>>(){
		@Override
		public void handleEvent(ValueEvent<ContactInfo> eventArgs) {
			getMainPanel().updateContact(eventArgs.getValue());
		}
	};

	private final EventListener<ValueEvent<ContainerView>> onViewChangedListener = new EventListener<ValueEvent<ContainerView>>(){
		@Override
		public void handleEvent(ValueEvent<ContainerView> eventArgs) {
			if (eventArgs.getValue().getViews() == Views.BROWSE_MEMBERS) {
				getMainPanel().fireEvent();
			}
		}
	};

	public BrowseMembersPanel(DialogFactory dialogFactory, ViewEngine viewEngine) {
		super();
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		init();
	}

	private void init() {
		getMiddlePanel().add(getMainPanel(), BorderLayout.CENTER);
		GridBagConstraints centerPanelConstraints = new GridBagConstraints();
		centerPanelConstraints.gridx = 1;
		centerPanelConstraints.gridy = 0;
		centerPanelConstraints.insets = new Insets(0, 0, 0, 5);

		getTopPanel().add(getCenterPanel(), centerPanelConstraints);

		GridBagConstraints loaderPanelConstraints = new GridBagConstraints();
		loaderPanelConstraints.gridx = 0;
		loaderPanelConstraints.gridy = 1;
		loaderPanelConstraints.weightx = 1;
		loaderPanelConstraints.weighty = 1;
		loaderPanelConstraints.fill = GridBagConstraints.BOTH;

		this.add(getLoaderPanel(), loaderPanelConstraints);
		this.setComponentZOrder(getMiddlePanel(), 1);
		this.setComponentZOrder(getLoaderPanel(), 0);
	}

	public void setAppControlConfigurer(ViewEngineConfigurator configurer) {
		// TODO: as we setup we should eventually reset.
		configurer.setupViewEngine(this);
	}


	protected FriendsMainPanel getMainPanel() {
		if (friendsMainPanel == null) {
			friendsMainPanel = new FriendsMainPanel(dialogFactory, viewEngine, getLoaderPanel());
		}
		return friendsMainPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(messages.getMessage("browse.members.title"));
		getSearchContactField().setText(messages.getMessage("browse.members.search.textfield"));
		getMainPanel().internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getMainPanel().removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getMainPanel().setMessages(messages);
	}

	private JPanel getSearchContactPanel() {
		if (searchContactPanel == null) {
			searchContactPanel = new JPanel();
			searchContactPanel.setLayout(null);
			searchContactPanel.setMaximumSize(SEARCH_CONTACT_PANEL_DEFAULT_SIZE);
			searchContactPanel.setPreferredSize(SEARCH_CONTACT_PANEL_DEFAULT_SIZE);
			searchContactPanel.setMinimumSize(SEARCH_CONTACT_PANEL_DEFAULT_SIZE);
			searchContactPanel.add(getSearchButton());
			searchContactPanel.add(getSearchContactField());
			searchContactPanel.add(getCleanSearchButton());
		}
		return searchContactPanel;
	}

	private JButton getCleanSearchButton() {
		if (clearSearchButton == null) {
			clearSearchButton = new JButton();
			clearSearchButton.setBounds(CLEAR_SEARCH_BUTTON_BOUNDS);
			clearSearchButton.setName(CLEAN_SEARCH_BUTTON_INVISIBLE_NAME);
		}
		return clearSearchButton;
	}

	private JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton();
			searchButton.setBounds(SEARCH_BUTTON_BOUNDS);
			searchButton.setName(SEARCH_BUTTON_NAME);
			searchButton.setEnabled(false);
			searchButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new AddContactFlow(viewEngine, dialogFactory).executeAdd(getSearchContactField().getText());
				}
			});
		}
		return searchButton;
	}

	private JTextField getSearchContactField() {
		if (searchContactTextField == null) {
			searchContactTextField = new JTextField();
			searchContactTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			searchContactTextField.setName(SEARCH_CONTACT_TEXT_FIELD_NAME);
			searchContactTextField.setSize(SEARCH_CONTACT_TEXTFIELD_DEFAULT_SIZE);
			searchContactTextField.setBounds(SEARCH_CONTACT_TEXFIELD_BOUNDS);
			searchContactTextField.setBorder(SEARCH_CONTACT_TEXTFIELD_BORDER);
			searchContactTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
			searchContactTextField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					validateSearch(e);
				}
			});
			searchContactTextField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					searchContactTextField.setText("");
				}
			});
			searchContactTextField.addKeyListener(new SpacerKeyListener());
		}
		return searchContactTextField;
	}

	private void validateSearch(KeyEvent e) {
		if (KeyEvent.VK_ENTER == e.getKeyCode() && getSearchButton().isEnabled()) {
			new AddContactFlow(viewEngine, dialogFactory).executeAdd(getSearchContactField().getText());
		} else {
			viewEngine.request(Actions.Social.VALIDATE_SEARCH_KEYWORD, searchContactTextField.getText().trim(),
					new ResponseCallback<Boolean>() {
						@Override
						public void onResponse(Boolean success) {
							if (getSearchContactField().getText().isEmpty() || success) {
								validFields();
							} else {
								invalidFields();
							}
							getSearchButton().setEnabled(success);
							repaintSearchField();
						}
					});
		}
	}

	private void repaintSearchField() {
		searchButton.repaint();
		searchContactTextField.repaint();
		clearSearchButton.repaint();
	}

	private void invalidFields() {
		searchButton.setName(SEARCH_BUTTON_INVALID_NAME);
		searchContactTextField.setName(SEARCH_CONTACT_TEXT_FIELD_INVALID_NAME);
		clearSearchButton.setName(CLEAN_SEARCH_BUTTON_INVISIBLE_INVALID_NAME);
	}

	private void validFields() {
		searchButton.setName(SEARCH_BUTTON_NAME);
		searchContactTextField.setName(SEARCH_CONTACT_TEXT_FIELD_NAME);
		clearSearchButton.setName(CLEAN_SEARCH_BUTTON_INVISIBLE_NAME);
	}

	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new GridBagLayout());
			JPanel blankPanel = new JPanel();
			blankPanel.setMinimumSize(BLANK_PANEL_MINIMUM_SIZE);
			blankPanel.setPreferredSize(BLANK_PANEL_DEFAULT_SIZE);

			GridBagConstraints blankPanelConstraints = new GridBagConstraints();
			blankPanelConstraints.gridx = 0;
			blankPanelConstraints.gridy = 0;
			blankPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			blankPanelConstraints.weightx = 1.0;

			GridBagConstraints searchPanelConstraints = new GridBagConstraints();
			searchPanelConstraints.gridx = 1;
			searchPanelConstraints.gridy = 0;

			centerPanel.add(blankPanel, blankPanelConstraints);
			centerPanel.add(getSearchContactPanel(), searchPanelConstraints);
		}
		return centerPanel;
	}

	protected JPanel getLoaderPanel() {
		if (loaderPanel == null) {
			loaderPanel = new GrayBackgroundedLoaderPanel();
		}
		return loaderPanel;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine);
		viewEngine.addListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);
		viewEngine.addListener(Events.View.CURRENT_VIEW_CHANGED, onViewChangedListener);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
		viewEngine.removeListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);
		viewEngine.removeListener(Events.View.CURRENT_VIEW_CHANGED, onViewChangedListener);
	}
}
