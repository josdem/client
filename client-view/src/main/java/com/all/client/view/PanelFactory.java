package com.all.client.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ViewEngineConfigurator;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.feeds.FeedViewFactory;
import com.all.client.view.stage.StagePanel;
import com.all.client.view.toolbar.downloads.DownloadPanel;
import com.all.client.view.toolbar.home.HomeContainerPanel;
import com.all.client.view.toolbar.hundred.HundredContainerPanel;
import com.all.client.view.toolbar.search.P2PSearchPanel;
import com.all.client.view.toolbar.social.ProfileDropListener;
import com.all.client.view.toolbar.social.SocialCardPanel;
import com.all.client.view.toolbar.social.SocialContainerPanel;
import com.all.client.view.toolbar.users.BrowseMembersPanel;
import com.all.client.view.util.ViewRepository;
import com.all.core.events.Events;
import com.all.core.model.Views;
import com.all.event.EventMethod;
import com.all.i18n.Messages;

@Controller
public class PanelFactory {
	@Autowired
	private Messages messages;
	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private FeedViewFactory feedViewFactory;
	@Autowired
	private ViewEngineConfigurator configurer;
	@Autowired
	private MultiLayerDropTargetListener multiLayerDropTargetListener;
	@Autowired
	private ViewRepository viewRepository;
	@Autowired
	private BottomPanel bottomPanel;

	private DownloadPanel downloadPanel = null;
	private SocialContainerPanel profileContainerPanel = null;
	private P2PSearchPanel p2pSearchPanel = null;
	private BrowseMembersPanel browseMembersPanel = null;
	private HundredContainerPanel hundredPanel = null;
	private HomeContainerPanel homePanel = null;
	// WTFFFFFFFFFFFFFFFUUUUUUUUUUUUUUUUUUUUUUUUUUUU!!!!
	@Autowired
	private StagePanel stagePanel;

	@EventMethod(Events.Application.STOPED_ID)
	public void clearPanelFactory() {
		reset(downloadPanel);
		reset(profileContainerPanel);
		reset(p2pSearchPanel);
		reset(browseMembersPanel);
		reset(hundredPanel);
		reset(homePanel);

		downloadPanel = null;
		profileContainerPanel = null;
		p2pSearchPanel = null;
		browseMembersPanel = null;
		hundredPanel = null;
		homePanel = null;

	}

	private void reset(View view) {
		if (view != null) {
			view.destroy(viewEngine);
		}
	}

	public SocialContainerPanel getProfilePanel() {
		if (profileContainerPanel == null) {
			ProfileDropListener profileDropListener = new ProfileDropListener(viewEngine);
			SocialCardPanel profileCardPanel = new SocialCardPanel(dialogFactory, messages, viewEngine, stagePanel,
					bottomPanel);
			profileContainerPanel = new SocialContainerPanel(profileCardPanel, profileDropListener, viewEngine, messages,
					multiLayerDropTargetListener, dialogFactory, feedViewFactory);
			profileContainerPanel.initialize(viewEngine);
		}
		return profileContainerPanel;
	}

	private P2PSearchPanel getSearchPanel() {
		if (p2pSearchPanel == null) {
			p2pSearchPanel = new P2PSearchPanel();
			p2pSearchPanel.setDialogFactory(dialogFactory);
			p2pSearchPanel.setMessages(messages);
			p2pSearchPanel.initialize(viewEngine);
			p2pSearchPanel.setAppControlConfigurer(configurer);
			p2pSearchPanel.initialize();
		}
		return p2pSearchPanel;
	}

	public DownloadPanel getDownloadPanel() {
		if (downloadPanel == null) {
			downloadPanel = new DownloadPanel(messages);
			downloadPanel.setupDrags(multiLayerDropTargetListener, viewEngine);
			downloadPanel.setEngine(viewEngine, configurer);
			downloadPanel.setMusicPlayerController(viewEngine);
			downloadPanel.setDialogFactory(dialogFactory);
			downloadPanel.wireDownloadsController();
			downloadPanel.wire();
			downloadPanel.setupSearch();
			downloadPanel.setViewState(viewRepository);
			downloadPanel.initialize(viewEngine);
			downloadPanel.setMessages(messages);
			downloadPanel.initialize();
		}
		return downloadPanel;
	}

	private BrowseMembersPanel getBrowseMembersPanel() {
		if (browseMembersPanel == null) {
			browseMembersPanel = new BrowseMembersPanel(dialogFactory, viewEngine);
			browseMembersPanel.setAppControlConfigurer(configurer);
			browseMembersPanel.initialize(viewEngine);
			browseMembersPanel.setMessages(messages);
		}
		return browseMembersPanel;
	}

	private HundredContainerPanel getHundredPanel() {
		if (hundredPanel == null) {
			hundredPanel = new HundredContainerPanel(viewEngine);
			hundredPanel.initialize(viewEngine);
			hundredPanel.setMessages(messages);
		}
		return hundredPanel;
	}

	private HomeContainerPanel getHomePanel() {
		if (homePanel == null) {
			homePanel = new HomeContainerPanel(dialogFactory);
			homePanel.initialize(viewEngine);
			homePanel.setMessages(messages);
		}
		return homePanel;
	}

	public MiddleCloseablePanel createPanel(Views name) {
		MiddleCloseablePanel component = null;
		switch (name) {
		case PROFILE:
			component = getProfilePanel();
			break;
		case SEARCH:
			component = getSearchPanel();
			break;
		case DOWNLOAD:
			component = getDownloadPanel();
			break;
		case BROWSE_MEMBERS:
			component = getBrowseMembersPanel();
			break;
		case HUNDRED:
			component = getHundredPanel();
			break;
		case HOME:
			component = getHomePanel();
			break;
		default:
		}
		return component;
	}

}
