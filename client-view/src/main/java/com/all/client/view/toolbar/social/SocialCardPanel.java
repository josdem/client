package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.DecoratedTwitterStatus;
import com.all.client.view.BottomPanel;
import com.all.client.view.HipecotechTopPanel;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.stage.StagePanel;
import com.all.commons.Environment;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.model.ApplicationActions;
import com.all.core.events.Events;
import com.all.core.model.SubViews;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;
import com.all.shared.stats.usage.UserActions;

/**
 * @Understands a class who knows how to create and show social Mix.
 */

public final class SocialCardPanel extends JPanel implements FocusListener, View {

	private static final long serialVersionUID = 1L;

	private static final int YOUTUBE_OPTION = 0;

	private static final int FACEBOOK_OPTION = 1;

	private static final Dimension CARD_PANEL_DEFAULT_SIZE = new Dimension(822, 658);

	private static final Insets TABS_PANEL_INSETS = new Insets(0, 0, 2, 0);

	private static final String ALL_PANEL = "allPanel";

	private static final String FACEBOOK_URL = "www.facebook.com";

	private static final String YOUTUBE_URL = "www.youtube.com";

	private static final String STAGE_PANEL = "stagePanel";

	private static final String TWITTER_PANEL = "twitterPanel";

	private static final int ALL_TAB = 0;

	private static final int TWITTER_TAB = 3;

	private DialogFactory dialogFactory;

	private JPanel browserContainer;

	private JPanel cardPanel;

	private JPanel profileContainer;

	private JPanel twitterContainer;

	private TwitterPanel twitterPanel;

	private HipecotechTopPanel hipecotechTopPanel;

	private Messages messages;

	private SocialToolbarPanel tabsPanel;

	private ViewEngine viewEngine;

	private Log log = LogFactory.getLog(SocialCardPanel.class);

	private StagePanel stagePanel;

	private BottomPanel bottomPanel;

	private final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("repaintComponentsThread"));

	private Observable<ObserveObject> onCloseEvent = new Observable<ObserveObject>();

	private EventListener<ValueEvent<List<DecoratedTwitterStatus>>> timelineListener = new EventListener<ValueEvent<List<DecoratedTwitterStatus>>>() {
		@Override
		public void handleEvent(ValueEvent<List<DecoratedTwitterStatus>> eventArgs) {
			getTwitterPanel().updateTimeline(eventArgs.getValue());
		}
	};

	private int panelSelected;

	public SocialCardPanel(DialogFactory dialogFactory, Messages messages, ViewEngine viewEngine, StagePanel stagePanel,
			BottomPanel bottomPanel) {
		this.dialogFactory = dialogFactory;
		this.messages = messages;
		this.viewEngine = viewEngine;
		this.stagePanel = stagePanel;
		this.bottomPanel = bottomPanel;
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		GridBagConstraints topPanelConstraints = new GridBagConstraints();
		topPanelConstraints.gridx = 0;
		topPanelConstraints.gridy = 0;
		topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		topPanelConstraints.weightx = 1;

		GridBagConstraints tabsPanelConstraints = new GridBagConstraints();
		tabsPanelConstraints.gridx = 0;
		tabsPanelConstraints.gridy = 1;
		tabsPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		tabsPanelConstraints.weightx = 1;
		tabsPanelConstraints.insets = TABS_PANEL_INSETS;

		GridBagConstraints cardPanelConstraints = new GridBagConstraints();
		cardPanelConstraints.gridx = 0;
		cardPanelConstraints.gridy = 2;
		cardPanelConstraints.fill = GridBagConstraints.BOTH;
		cardPanelConstraints.weightx = 1;
		cardPanelConstraints.weighty = 1;

		this.add(getTabsPanel(), tabsPanelConstraints);
		this.add(getCardPanel(), cardPanelConstraints);
	}

	private SocialToolbarPanel getTabsPanel() {
		if (tabsPanel == null) {
			tabsPanel = new SocialToolbarPanel();
			tabsPanel.onAll().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					panelSelected = 0;
					manageAllEvent();
				}
			});
			tabsPanel.onFacebook().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					panelSelected = 2;
					manageBrowserEvent(FACEBOOK_OPTION);
				}
			});
			tabsPanel.onYoutube().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					panelSelected = 1;
					manageBrowserEvent(YOUTUBE_OPTION);
				}
			});
		}
		return tabsPanel;
	}

	// Bug 5042 bottom panel disappear when loading browser in MAC in the very
	// first time
	// Bug 5149 some components from HipecotechTopPanel needs to be repainted
	// because the same previous bug
	private void revalidateStagePanelParent() {
		if (!Environment.isWindows()) {
			scheduler.schedule(new RepaintComponentsTask(), 2, TimeUnit.SECONDS);
		}
	}

	class RepaintComponentsTask implements Runnable {

		@Override
		public void run() {
			JPanel parent = (JPanel) stagePanel.getParent();
			parent.invalidate();
			parent.validate();

			hipecotechTopPanel.invalidate();
			hipecotechTopPanel.validate();
			hipecotechTopPanel.repaint();

			bottomPanel.invalidate();
			bottomPanel.validate();
			bottomPanel.repaint();

			log.info("revalidating parent from stagePanel and hipecotechTopPanel and bottomPanel because the mozillaBrowser");
		}
	}

	private void showPanel(String name) {
		CardLayout layout = (CardLayout) cardPanel.getLayout();
		layout.show(cardPanel, name);
	}

	private JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel();
			cardPanel.setLayout(new CardLayout());
			cardPanel.setPreferredSize(CARD_PANEL_DEFAULT_SIZE);
			cardPanel.add(getProfileContainer(), ALL_PANEL);
		}
		return cardPanel;
	}

	private void getTwitterContainer() {
		if (twitterContainer == null) {
			twitterContainer = new JPanel();
			twitterContainer.setLayout(new BorderLayout());
			twitterContainer.add(getTwitterPanel(), BorderLayout.CENTER);
			cardPanel.add(twitterContainer, TWITTER_PANEL);
		}
	}

	private void createBrowserContainer() {
		if (browserContainer == null) {
			browserContainer = new JPanel();
			browserContainer.setLayout(new BorderLayout());
			browserContainer.add(stagePanel, BorderLayout.CENTER);
			cardPanel.add(browserContainer, STAGE_PANEL);
			stagePanel.setVisible(true);
		}
	}

	private TwitterPanel getTwitterPanel() {
		if (twitterPanel == null) {
			twitterPanel = new TwitterPanel(viewEngine, messages);
		}
		return twitterPanel;
	}

	JPanel getProfileContainer() {
		if (profileContainer == null) {
			profileContainer = new JPanel();
			profileContainer.setLayout(new BorderLayout());
		}
		return profileContainer;
	}

	private boolean isJavaRunningIn32BitMode() {
		if (Environment.isMac() && Environment.isRunningOn64BitMode()) {
			dialogFactory
					.showLongInfoDialog("dialog.warning.macArchRunMode", "dialog.warning.macArchRunMode.title", 350, 120);
			return false;
		}
		return true;
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		getParent().getParent().getParent().getParent().validate();
	}

	@Override
	public void focusGained(FocusEvent e) {
		getParent().getParent().getParent().getParent().validate();
	}

	public void setBottomPanel(BottomPanel bottomPanel) {
		this.bottomPanel = bottomPanel;
	}

	public ObserverCollection<ObserveObject> onClose() {
		return onCloseEvent;
	}

	private void showTwitterPanel() {
		if (dialogFactory.isTwitterLoggedIn()) {
			getTwitterContainer();
			tabsPanel.selectedButton(TWITTER_TAB);
			showPanel(TWITTER_PANEL);
			viewEngine
					.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.SocialNetworks.ACCESS_TWITTER_PANEL);
		} else {
			tabsPanel.selectedButton(panelSelected);
		}
	}

	private void manageAllEvent() {
		showPanel(ALL_PANEL);
		viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.SocialNetworks.ACCESS_ALL_PANEL);
	}

	private void manageBrowserEvent(int option) {
		if (isJavaRunningIn32BitMode()) {
			createBrowserContainer();
			showPanel(STAGE_PANEL);
			switch (option) {
			case YOUTUBE_OPTION:
				stagePanel.loadUrl(YOUTUBE_URL);
				viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION,
						UserActions.SocialNetworks.ACCESS_YOUTUBE_PANEL);
				break;
			case FACEBOOK_OPTION:
				stagePanel.loadUrl(FACEBOOK_URL);
				viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION,
						UserActions.SocialNetworks.ACCESS_FACEBOOK_PANEL);
				break;
			}
			revalidateStagePanelParent();
		}
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		getTabsPanel().initialize(viewEngine);
		viewEngine.addListener(Events.View.CURRENT_SUBVIEW_CHANGED, new EventListener<ValueEvent<SubViews>>() {
			@Override
			public void handleEvent(ValueEvent<SubViews> valueEvent) {
				switch (valueEvent.getValue()) {
				case ALL:
					showAllPanel();
					break;
				case TWITTER:
					showTwitterPanel();
					break;
				}
			}
		});
		viewEngine.addListener(Events.Social.TWITTER_USER_TIMELINE_CHANGED, timelineListener);
	}

	private void showAllPanel() {
		showPanel(ALL_PANEL);
		viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.SocialNetworks.ACCESS_ALL_PANEL);
		tabsPanel.selectedButton(ALL_TAB);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.Social.TWITTER_USER_TIMELINE_CHANGED, timelineListener);
	}
}
