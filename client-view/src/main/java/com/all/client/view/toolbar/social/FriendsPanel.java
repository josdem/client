package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.model.Model;
import com.all.core.model.Profile;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;

public final class FriendsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Dimension FRIENDS_PANEL_DEFAULT_SIZE = new Dimension(560, 168);

	private static final Dimension FRIENDS_PANEL_MINIMUM_SIZE = new Dimension(310, 168);

	private JButton nextButton;

	private JButton previousButton;

	private JPanel friendsPanel;

	private FriendsScrollPane scrollPane;

	private Profile profile;

	private DialogFactory dialogFactory;

	private PaginationFriendsPanelManager paginationManager;

	private final ViewEngine viewEngine;

	public FriendsPanel(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
		this.profile = viewEngine.get(Model.CURRENT_PROFILE);
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getFriendsPanel(), BorderLayout.CENTER);
	}

	private JPanel getFriendsPanel() {
		if (friendsPanel == null) {
			friendsPanel = new JPanel();
			friendsPanel.setLayout(new BorderLayout());
			friendsPanel.setPreferredSize(FRIENDS_PANEL_DEFAULT_SIZE);
			friendsPanel.setMinimumSize(FRIENDS_PANEL_MINIMUM_SIZE);
			friendsPanel.add(getScrollPane(), BorderLayout.CENTER);
		}
		return friendsPanel;
	}

	FriendsScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new FriendsScrollPane(dialogFactory, getPaginationManager(), viewEngine);
			scrollPane.fillFirstPage();
		}
		return scrollPane;
	}

	void updateScrollPaneSize() {
		scrollPane.setPreferredSize(new Dimension(getWidth(), scrollPane.getSize().height));
		scrollPane.setSize(new Dimension(getWidth(), scrollPane.getSize().height));
	}

	PaginationFriendsPanelManager getPaginationManager() {
		if (paginationManager == null) {
			Collections.sort(profile.getFriends());
			paginationManager = new PaginationFriendsPanelManager(profile.getFriends());
		}
		return paginationManager;
	}

	void enableControls() {
		previousButton.setEnabled(getPaginationManager().isPrevPage());
		nextButton.setEnabled(getPaginationManager().isNextPage());
	}

	ObserverCollection<ObserveObject> onLoadFriendsPanel() {
		return scrollPane.onLoadFriendsPanel();
	}

	boolean isLoaded() {
		return scrollPane.isLoaded();
	}
}
