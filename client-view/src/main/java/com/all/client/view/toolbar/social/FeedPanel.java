package com.all.client.view.toolbar.social;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.client.view.components.GrayBackgroundedLoaderPanel;
import com.all.client.view.feeds.FeedView;
import com.all.client.view.feeds.FeedViewFactory;
import com.all.client.view.i18n.Ji18nLabel;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.messages.FeedsResponse;
import com.all.shared.newsfeed.AllFeed;

public class FeedPanel extends JPanel implements View {

	private static final Log LOG = LogFactory.getLog(FeedPanel.class);

	private static final long serialVersionUID = -965452393594886818L;

	private JScrollPane feedScrollPane;

	private ScrollablePanel feedsContainer;

	private final FeedViewFactory feedViewFactory;

	private ViewEngine viewEngine;

	private Listener<ValueEvent<Set<AllFeed>>> addNewFeedListener;

	private static final String DARK_FEED_BACKGROUND_COLOR = "feedDarkBackground";

	private static final String LIGHT_FEED_BACKGROUND_COLOR = "feedLightBackground";

	private String[] styles = { LIGHT_FEED_BACKGROUND_COLOR, DARK_FEED_BACKGROUND_COLOR };

	private JPanel loaderPanel;

	private boolean loadingFeeds = true;
	private boolean requestingMoFeeds = false;
	private boolean hasMoFeeds = true;
	private long lastId = 0;

	private Ji18nLabel feedStatusLabel;
	private final Messages messages;

	public FeedPanel(FeedViewFactory feedViewFactory, Messages messages) {
		this.feedViewFactory = feedViewFactory;
		this.messages = messages;
		addNewFeedListener = new EventListener<ValueEvent<Set<AllFeed>>>() {

			@Override
			public void handleEvent(ValueEvent<Set<AllFeed>> eventArgs) {
				getLoaderPanel().setVisible(true);
				Set<AllFeed> feeds = eventArgs.getValue();
				addNewFeedsCollection(feeds, false);
				loadingFeeds = false;
				feedStatusLabel.setMessage("feed.loadMore");
				getLoaderPanel().setVisible(false);
			}
		};
	}

	private void initialize() {
		feedStatusLabel = new Ji18nLabel();
		feedStatusLabel.setName(SynthFonts.BOLD_FOND13_PURPLE120_39_139);
		feedStatusLabel.setPreferredSize(new Dimension(100, 50));
		feedStatusLabel.setMessage("feed.loadingMore");
		this.setLayout(new GridBagLayout());
		GridBagConstraints feedsContainerConstraints = new GridBagConstraints();
		feedsContainerConstraints.gridx = 0;
		feedsContainerConstraints.gridy = 0;
		feedsContainerConstraints.fill = GridBagConstraints.BOTH;
		feedsContainerConstraints.weightx = 1.0;
		feedsContainerConstraints.weighty = 1.0;
		this.add(getFeedScrollPane(), feedsContainerConstraints);
		GridBagConstraints loaderPanelConstraints = new GridBagConstraints();
		loaderPanelConstraints.gridx = 0;
		loaderPanelConstraints.gridy = 0;
		loaderPanelConstraints.weightx = 1.0;
		loaderPanelConstraints.weighty = 1.0;
		loaderPanelConstraints.fill = GridBagConstraints.BOTH;
		this.add(getLoaderPanel(), loaderPanelConstraints);
		this.setComponentZOrder(getLoaderPanel(), 0);
		this.setComponentZOrder(getFeedScrollPane(), 1);
	}

	private JScrollPane getFeedScrollPane() {
		if (feedScrollPane == null) {
			feedScrollPane = new JScrollPane();
			feedScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			feedScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			feedScrollPane.getVerticalScrollBar().setUnitIncrement(16);
			feedScrollPane.setViewportView(getFeedsContainer());
			feedScrollPane.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					getFeedsContainer().checkHorizontalScrollbarVisibility();
				}
			});
			feedScrollPane.getViewport().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (!loadingFeeds && !requestingMoFeeds && hasMoFeeds && isAtBottom(feedScrollPane.getViewport())) {
						requestingMoFeeds = true;
						feedStatusLabel.setMessage("feed.loadingMore");
						viewEngine.request(Actions.Feeds.REQUEST_OLD_FEEDS, lastId, new ResponseCallback<FeedsResponse>() {
							@Override
							public void onResponse(FeedsResponse response) {
								List<AllFeed> feeds = response.getFeeds();
								if (feeds.isEmpty()) {
									hasMoFeeds = false;
									feedStatusLabel.setMessage("feed.noMoreFeeds");
								} else {
									addNewFeedsCollection(feeds, true);
									feedStatusLabel.setMessage("feed.loadMore");
								}
								requestingMoFeeds = false;
							}
						});
					}
				}

				private boolean isAtBottom(JViewport viewport) {
					Rectangle bounds = viewport.getView().getBounds();
					Rectangle viewRect = viewport.getViewRect();
					return viewRect.getY() + viewRect.getHeight() > (bounds.getHeight() - 10);
				}
			});

		}
		return feedScrollPane;
	}

	private ScrollablePanel getFeedsContainer() {
		if (feedsContainer == null) {
			feedsContainer = new ScrollablePanel();
			feedsContainer.add(feedStatusLabel);
		}
		return feedsContainer;
	}

	private void requestFeeds() {
		viewEngine.send(Actions.Feeds.REQUEST_FEEDS);
	}

	private void addNewFeedsCollection(Collection<AllFeed> feeds, boolean atBottom) {
		for (AllFeed allFeed : feeds) {
			// LOG.error( allFeed.getOwner() + " - Feed ("+allFeed.getType()+") en vista: " + allFeed.getDate());
			addNewFeed(allFeed, atBottom);
		}

		// we revalidate after all feeds were added to improve efficiency
		getFeedsContainer().revalidate();
		getFeedsContainer().repaint();

		getFeedsContainer().checkHorizontalScrollbarVisibility();
	}

	private void addNewFeed(AllFeed feed, boolean bottom) {
		try {
			FeedView<?> feedView = feedViewFactory.create(feed);
			int lastFeedIndex = getFeedsContainer().getComponentCount();
			String style = styles[lastFeedIndex & 1];
			feedView.setStyle(style);
			if (bottom) {
				getFeedsContainer().add(feedView, getFeedsContainer().getComponentCount() - 1);
			} else {
				getFeedsContainer().add(feedView, 0);
			}
			if (lastId == 0) {
				lastId = feed.getDate().getTime();
			} else {
				lastId = Math.min(feed.getDate().getTime(), lastId);
			}
		} catch (IllegalArgumentException iae) {
			LOG.error(iae, iae);
		}
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
		feedStatusLabel.setMessages(messages);
		viewEngine.addListener(Events.Feeds.NEW_FEEDS, addNewFeedListener);
		requestFeeds();
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		feedStatusLabel.removeMessages(messages);
		viewEngine.removeListener(Events.Feeds.NEW_FEEDS, addNewFeedListener);
		this.viewEngine = null;
	}

	private JPanel getLoaderPanel() {
		if (loaderPanel == null) {
			loaderPanel = new GrayBackgroundedLoaderPanel();
			loaderPanel.setOpaque(false);
		}
		return loaderPanel;
	}

	// 422 min width feed size
	class ScrollablePanel extends JPanel implements Scrollable {

		private static final long serialVersionUID = 1L;

		public ScrollablePanel() {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return null;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 10;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			// return forceFeedViewWidth;
			return true;
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			return 10;
		}

		/**
		 * Call it after objects have been re validated and laid out
		 */
		private void checkHorizontalScrollbarVisibility() {
			int feedScrollPaneWidth = getFeedScrollPane().getWidth() - getFeedScrollPane().getVerticalScrollBar().getWidth();

			int maxInnerWidth = 0;
			for (int i = 0; i < getFeedsContainer().getComponentCount(); i++) {
				Component component = getFeedsContainer().getComponent(i);
				if (component instanceof FeedView<?>) {
					FeedView<?> feedView = (FeedView<?>) component;
					maxInnerWidth = Math.max(feedView.getInternalWidth(), maxInnerWidth);
				}
			}

			LOG.debug("maxInnerWidth " + maxInnerWidth + " feedScrollPaneWidth " + feedScrollPaneWidth);
			if (maxInnerWidth > feedScrollPaneWidth) {
				LOG.debug("SHOW HORIZONTAL SCROLLBAR", new Exception());
				getFeedScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			} else {
				LOG.debug("hide HORIZONTAL SCROLLBAR", new Exception());
				getFeedScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			}
		}

	}
}
