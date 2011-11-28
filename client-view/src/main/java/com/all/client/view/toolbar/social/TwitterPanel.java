package com.all.client.view.toolbar.social;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ActionType;
import com.all.action.RequestAction;
import com.all.action.ResponseCallback;
import com.all.action.SwingResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.DecoratedTwitterStatus;
import com.all.client.model.TwitterProfile;
import com.all.client.view.components.GrayBackgroundedLoaderPanel;
import com.all.client.view.listeners.TwitterActionListener;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.core.model.Tweet;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.stats.usage.UserActions;
import com.all.twitter.TwitterStatus;
import com.all.twitter.TwitterStatus.TwitterStatusType;

public class TwitterPanel extends JPanel implements TwitterActionListener, Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(TwitterPanel.class);

	private static final Dimension RIGHT_PANEL_DEFAULT_SIZE = new Dimension(410, 466);

	private static final Insets RIGHT_PANEL_INSETS = new Insets(0, 2, 0, 0);

	private JPanel middlePanel;

	private TwitterInfoPanel twitterInfoPanel;

	private JPanel rightPanel;

	private TwitterWallPanel twitterWallPanel;

	private Messages messages;

	private TwitterProfile currentProfile;

	private JPanel loaderPanel;

	private final ViewEngine viewEngine;

	public TwitterPanel(ViewEngine viewEngine, Messages messages) {
		this.viewEngine = viewEngine;
		setMessages(messages);
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		getMiddlePanel().setLayout(new GridBagLayout());

		GridBagConstraints twitterInfoPanelConstraints = new GridBagConstraints();
		twitterInfoPanelConstraints.gridx = 0;
		twitterInfoPanelConstraints.gridy = 0;
		twitterInfoPanelConstraints.fill = GridBagConstraints.VERTICAL;
		twitterInfoPanelConstraints.weighty = 1.0;

		GridBagConstraints wallTwitterPanelConstraints = new GridBagConstraints();
		wallTwitterPanelConstraints.gridx = 1;
		wallTwitterPanelConstraints.gridy = 0;
		wallTwitterPanelConstraints.fill = GridBagConstraints.BOTH;
		wallTwitterPanelConstraints.weightx = 1.0;
		wallTwitterPanelConstraints.weighty = 1.0;
		wallTwitterPanelConstraints.insets = RIGHT_PANEL_INSETS;

		getMiddlePanel().add(getTwitterInfoPanel(), twitterInfoPanelConstraints);
		getMiddlePanel().add(getWallTwitterPanel(), wallTwitterPanelConstraints);

		GridBagConstraints middlePanelConstraints = new GridBagConstraints();
		middlePanelConstraints.gridx = 0;
		middlePanelConstraints.gridy = 0;
		middlePanelConstraints.fill = GridBagConstraints.BOTH;
		middlePanelConstraints.weightx = 1;
		middlePanelConstraints.weighty = 1;

		GridBagConstraints loaderPanelConstraints = new GridBagConstraints();
		loaderPanelConstraints.gridx = 0;
		loaderPanelConstraints.gridy = 0;
		loaderPanelConstraints.weightx = 1;
		loaderPanelConstraints.weighty = 1;
		loaderPanelConstraints.fill = GridBagConstraints.BOTH;

		this.add(getMiddlePanel(), middlePanelConstraints);
		this.add(getLoaderPanel(), loaderPanelConstraints);
		this.setComponentZOrder(getMiddlePanel(), 1);
		this.setComponentZOrder(getLoaderPanel(), 0);
		showTimeLineForTheFirstTime();
	}

	private void showTimeLineForTheFirstTime() {
		currentProfile = viewEngine.get(Model.TWITTER_PROFILE);
		getTwitterInfoPanel().showProfile(currentProfile);
		viewEngine.request(Actions.Twitter.LOAD_USER_TIMELINE, new ResponseCallback<List<DecoratedTwitterStatus>>() {
			@Override
			public void onResponse(List<DecoratedTwitterStatus> timeline) {
				getLoaderPanel().setVisible(false);
				showTimeline(currentProfile, timeline, TwitterStatusType.FRIENDS);
			}
		});
	}

	public void showTimeline(TwitterProfile profile, List<DecoratedTwitterStatus> timeline, TwitterStatusType type) {
		try {
			long startTime = System.currentTimeMillis();
			switch (type) {
			case FRIENDS:
				getTwitterWallPanel().showHome(timeline, profile);
				break;
			case MENTIONS:
			case DIRECT:
				getTwitterWallPanel().showMentionsOrDirectMessages(timeline, profile.getScreenName());
			}
			LOG.debug("Timeline repainting took " + (System.currentTimeMillis() - startTime) + " ms to complete.");
		} catch (Exception e) {
			LOG.error("Unexpected error showing timeline.", e);
		}
	}

	private JPanel getMiddlePanel() {
		if (middlePanel == null) {
			middlePanel = new JPanel();
		}
		return middlePanel;
	}

	private TwitterInfoPanel getTwitterInfoPanel() {
		if (twitterInfoPanel == null) {
			twitterInfoPanel = new TwitterInfoPanel(this, messages);
		}
		return twitterInfoPanel;
	}

	private JPanel getWallTwitterPanel() {
		if (rightPanel == null) {
			rightPanel = new JPanel();
			rightPanel.setSize(RIGHT_PANEL_DEFAULT_SIZE);
			rightPanel.setPreferredSize(RIGHT_PANEL_DEFAULT_SIZE);
			rightPanel.setMinimumSize(RIGHT_PANEL_DEFAULT_SIZE);
			rightPanel.setMaximumSize(RIGHT_PANEL_DEFAULT_SIZE);
			final GridBagLayout layoutManager = new GridBagLayout();
			rightPanel.setLayout(layoutManager);

			final GridBagConstraints wallPanelConstraints = new GridBagConstraints();
			wallPanelConstraints.gridx = 0;
			wallPanelConstraints.gridy = 0;
			wallPanelConstraints.fill = GridBagConstraints.BOTH;
			wallPanelConstraints.weightx = 1;
			wallPanelConstraints.weighty = 1;
			rightPanel.add(getTwitterWallPanel(), wallPanelConstraints);
		}
		return rightPanel;
	}

	private TwitterWallPanel getTwitterWallPanel() {
		if (twitterWallPanel == null) {
			twitterWallPanel = new TwitterWallPanel(this, messages);
		}
		return twitterWallPanel;
	}

	private void showProfileAndTimeLineFrom(final TwitterProfile userProfile) {
		getTwitterInfoPanel().showProfile(userProfile);
		currentProfile = userProfile;
		loadTimeline(userProfile);
	}

	public void loadTimeline(final TwitterProfile profile) {
		getLoaderPanel().setVisible(true);
		viewEngine.request(Actions.Twitter.REQUEST_TIMELINE, profile.getScreenName(),
				new ResponseCallback<List<DecoratedTwitterStatus>>() {
					@Override
					public void onResponse(List<DecoratedTwitterStatus> timeline) {
						getLoaderPanel().setVisible(false);
						getTwitterWallPanel().showHome(timeline, profile);
					}
				});
	}

	@Override
	public void onRetweetedStatus(TwitterStatus twitterStatus) {
		showLoggedInUserProfile();
		StringBuilder sb = new StringBuilder();
		sb.append("RT @");
		sb.append(twitterStatus.getScreenName());
		sb.append(": ");
		sb.append(twitterStatus.getText());
		getTwitterWallPanel().setStatus(sb.toString());
	}

	private void showLoggedInUserProfile() {
		TwitterProfile userProfile = viewEngine.get(Model.TWITTER_PROFILE);
		if (!userProfile.equals(currentProfile)) {
			showProfileAndTimeLineFrom(userProfile);
		}
	}

	@Override
	public void onTwitterStatustReplied(TwitterStatus twitterStatus) {
		showLoggedInUserProfile();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("@");
		writeOnWallPanel(twitterStatus, stringBuilder);
	}

	@Override
	public void onTwitterStatustDirect(TwitterStatus twitterStatus) {
		showLoggedInUserProfile();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("D ");
		writeOnWallPanel(twitterStatus, stringBuilder);
	}

	private void writeOnWallPanel(TwitterStatus twitterStatus, StringBuilder sb) {
		sb.append(twitterStatus.getScreenName());
		sb.append(" ");
		getTwitterWallPanel().setStatus(sb.toString());
	}

	public void updateTimeline(List<DecoratedTwitterStatus> timeline) {
		TwitterProfile userProfile = viewEngine.get(Model.TWITTER_PROFILE);
		if (userProfile.equals(currentProfile)) {
			getTwitterWallPanel().updateTimeLine(timeline, userProfile);
		}
	}

	@Override
	public void onUserProfileRequested(final String screenName) {
		TwitterProfile userProfile = viewEngine.get(Model.TWITTER_PROFILE);
		if (!userProfile.getScreenName().equalsIgnoreCase(screenName)) {
			getLoaderPanel().setVisible(true);
			viewEngine.request(Actions.Twitter.REQUEST_USER_PROFILE, screenName, new ResponseCallback<TwitterProfile>() {
				@Override
				public void onResponse(TwitterProfile profile) {
					showProfileAndTimeLineFrom(profile);
				}
			});
		}
	}

	@Override
	public void onFollowUser(String screenName) {
		viewEngine.sendValueAction(Actions.Twitter.FOLLOW, screenName);
	}

	@Override
	public void onUnfollowUser(String screenName) {
		viewEngine.sendValueAction(Actions.Twitter.UNFOLLOW, screenName);
	}

	@Override
	public void onTwitterStatusUpdated(final String status) {
		LOG.info("Disabling Twitter text area...");
		getTwitterWallPanel().disableTextArea();
		viewEngine.request(Actions.Twitter.UPDATE_STATUS, new Tweet(status, UserActions.SocialNetworks.TWITTER_STATUS),
				new SwingResponseCallback<Boolean>() {
					@Override
					public void updateGui(final Boolean success) {
						LOG.info("Enabling Twitter Text area...");
						getTwitterWallPanel().enableTextArea(success);
						LOG.info("Twitter Text area enabled");
					}
				});
	}

	@Override
	public void onHome() {
		showProfileAndTimeLineFrom(viewEngine.get(Model.TWITTER_PROFILE));
	}

	@Override
	public void onMentions() {
		loadMentionsOrDirectMessages(Actions.Twitter.REQUEST_MENTIONS);
	}

	@Override
	public void onDirectMessages() {
		loadMentionsOrDirectMessages(Actions.Twitter.REQUEST_DIRECT_MESSAGES);
	}

	public void loadMentionsOrDirectMessages(ActionType<RequestAction<Void, List<DecoratedTwitterStatus>>> action) {
		getLoaderPanel().setVisible(true);
		viewEngine.request(action, new ResponseCallback<List<DecoratedTwitterStatus>>() {
			@Override
			public void onResponse(List<DecoratedTwitterStatus> timeline) {
				getLoaderPanel().setVisible(false);
				getTwitterWallPanel().showMentionsOrDirectMessages(timeline, currentProfile.getScreenName());
			}
		});
	}

	protected JPanel getLoaderPanel() {
		if (loaderPanel == null) {
			loaderPanel = new GrayBackgroundedLoaderPanel();
		}
		return loaderPanel;
	}

	@Override
	public final void setMessages(Messages messages) {
		this.messages = messages;
		getTwitterInfoPanel().setMessages(messages);
		getTwitterWallPanel().setMessages(messages);
	}

	@Override
	public void internationalize(Messages messages) {
		getTwitterInfoPanel().internationalize(messages);
		getTwitterWallPanel().internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getTwitterInfoPanel().removeMessages(messages);
		getTwitterWallPanel().removeMessages(messages);
	}

}
