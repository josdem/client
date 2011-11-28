package com.all.client.view.toolbar.social;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.feeds.FeedViewFactory;
import com.all.core.model.Model;
import com.all.core.model.Profile;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class ProfilePanel extends JPanel implements View {

	private static final long serialVersionUID = 1L;

	private static final Dimension CENTER_PROFILE_PANEL_DEFAULT_SIZE = new Dimension(560, 466);

	private static final Insets CARD_PANEL_INSETS = new Insets(0, 2, 0, 0);

	private final DialogFactory dialogFactory;

	private final Messages messages;

	private final MultiLayerDropTargetListener multiLayerDropListener;

	private final Profile profile;

	private ProfileInfoPanel profileInfoPanel;

	private final ProfileDropListener profileDropListener;

	private final ViewEngine viewEngine;

	private ProfileCardPanel profileCardPanel;

	private final FeedViewFactory feedViewFactory;

	public ProfilePanel(Messages messages, MultiLayerDropTargetListener dropListener, DialogFactory dialogFactory,
			ProfileDropListener profileDropListener, ViewEngine viewEngine, FeedViewFactory feedViewFactory) {
		this.multiLayerDropListener = dropListener;
		this.dialogFactory = dialogFactory;
		this.profileDropListener = profileDropListener;
		this.viewEngine = viewEngine;
		this.feedViewFactory = feedViewFactory;
		this.profile = viewEngine.get(Model.CURRENT_PROFILE);
		this.messages = messages;
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setSize(CENTER_PROFILE_PANEL_DEFAULT_SIZE);
		this.setPreferredSize(CENTER_PROFILE_PANEL_DEFAULT_SIZE);
		this.setMinimumSize(CENTER_PROFILE_PANEL_DEFAULT_SIZE);
		this.setMaximumSize(CENTER_PROFILE_PANEL_DEFAULT_SIZE);

		GridBagConstraints profileInfoPanelConstraints = new GridBagConstraints();
		profileInfoPanelConstraints.fill = GridBagConstraints.VERTICAL;
		profileInfoPanelConstraints.weighty = 1.0;

		GridBagConstraints profileCardPanelConstraints = new GridBagConstraints();
		profileCardPanelConstraints.gridx = 1;
		profileCardPanelConstraints.fill = GridBagConstraints.BOTH;
		profileCardPanelConstraints.weightx = 1.0;
		profileCardPanelConstraints.weighty = 1.0;
		profileCardPanelConstraints.insets = CARD_PANEL_INSETS;

		this.add(getProfileInfoPanel(), profileInfoPanelConstraints);
		this.add(getProfileCardPanel(), profileCardPanelConstraints);

		setDragAndDrop(profileDropListener);
	}

	private void setDragAndDrop(ProfileDropListener profileDropListener) {
		multiLayerDropListener.addDropListener(this, profileDropListener);
		// This is needed to listen when a contact is dropped on the info panel
		// because InfoPanel has its own drop listeners
		multiLayerDropListener.addDropListener(getProfileInfoPanel(), profileDropListener);
	}

	private ProfileInfoPanel getProfileInfoPanel() {
		if (profileInfoPanel == null) {
			profileInfoPanel = new ProfileInfoPanel(profile, messages, multiLayerDropListener, dialogFactory);
			profileInfoPanel.initialize(viewEngine);
		}
		return profileInfoPanel;
	}

	private ProfileCardPanel getProfileCardPanel() {
		if (profileCardPanel == null) {
			profileCardPanel = new ProfileCardPanel(messages, dialogFactory, feedViewFactory);
		}
		return profileCardPanel;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		getProfileInfoPanel().initialize(viewEngine);
		getProfileCardPanel().initialize(viewEngine);
	}

	public void onContactUpdated(ContactInfo contact) {
		getProfileInfoPanel().onContactUpdated(contact);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		getProfileInfoPanel().destroy(viewEngine);
		getProfileCardPanel().destroy(viewEngine);
	}

}
