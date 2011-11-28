package com.all.client.controller;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.DecoratedTwitterStatus;
import com.all.client.model.TwitterProfile;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.reporting.ClientReporter;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.actions.Actions;
import com.all.core.common.util.ImageUtil;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.core.model.Tweet;
import com.all.core.model.TwitterUser;
import com.all.event.ValueEvent;
import com.all.shared.stats.usage.UserActions;
import com.all.twitter.AllTwitter;
import com.all.twitter.AllTwitterException;
import com.all.twitter.TwitterStatus;
import com.all.twitter.UserProfile;

@Controller
public class TwitterController {

	private static final int ONE_HUNDRED = 100;
	private static final int FIRST_PAGE = 1;
	private static final int FRIENDS_TIMELINE_UPDATE_RATE = 30;

	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired(required = false)
	private AllTwitter twitter;
	@Autowired
	private ContactController contactController;
	@Autowired
	private UserPreferenceService userPreferenceService;
	@Autowired
	private ClientReporter reporter;
	@Autowired
	private ControlEngine controlEngine;

	private Set<DecoratedTwitterStatus> friendsTimeline = Collections
			.synchronizedSet(new HashSet<DecoratedTwitterStatus>());
	private TwitterProfile userProfile;
	private ScheduledExecutorService twitterExecutor = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("TwitterMonitor"));
	private EncryptorHelper encryptorHelper;

	@PreDestroy
	public void shutdown() {
		twitterExecutor.shutdownNow();
	}

	@RequestMethod(Actions.Twitter.LOGIN_ID)
	public void login(TwitterUser twitterUser) {
		try {
			String username = twitterUser.getUsername();
			String password = twitterUser.getPassword();
			boolean rememberMe = twitterUser.isRememberMe();

			twitter.login(username, password);

			reporter.logUserAction(UserActions.SocialNetworks.TWITTER_LOGIN);
			userProfile = new TwitterProfile(twitter.getUserProfile(), true, false);
			controlEngine.set(Model.TWITTER_LOGGED_IN, true, Events.Social.TWITTER_LOGGED);
			controlEngine.set(Model.TWITTER_PROFILE, userProfile, null);
			twitterExecutor.execute(new TwitterPostLoginTask(username, password, rememberMe));
		} catch (Exception e) {
			log.error("An error ocur while logging into twitter", e);
		}
	}
	
	@ActionMethod(Actions.Twitter.POST_AUTH)
	public void authorizePost(Boolean auth){
		log.info("saving Twitter Auth to: " + auth);
		userPreferenceService.setPostAuthorization(auth);
	}

	private void saveUserAndPassword(String username, String password, boolean rememberMe) {
		try {
			userPreferenceService.setTwitterUser(getEncryptorHelper().encrypt(username));
			userPreferenceService.setTwitterPassword(getEncryptorHelper().encrypt(password));
			userPreferenceService.setTwitterRememberMe(rememberMe);
		} catch (Exception e) {
			log.error("Unable to save twitter credentials ", e);
		}
	}

	private EncryptorHelper getEncryptorHelper() {
		if (encryptorHelper == null) {
			encryptorHelper = new EncryptorHelper(controlEngine.get(Model.CURRENT_USER));
		}
		return encryptorHelper;
	}

	@RequestMethod(Actions.Twitter.REQUEST_CREDENTIALS_ID)
	public TwitterUser getTwitterUser() {
		if (userPreferenceService.getTwitterUser() != null) {
			String username = getEncryptorHelper().decrypt(userPreferenceService.getTwitterUser());
			String password = getEncryptorHelper().decrypt(userPreferenceService.getTwitterPassword());
			boolean rememberMe = userPreferenceService.isTwitterRememberMe();

			return new TwitterUser(username, password, rememberMe);
		}
		return null;
	}

	private void initTimeLineMonitor() {
		log.trace("Enetered init monitor");
		twitterExecutor.scheduleWithFixedDelay(new TwitterMonitorTask() {
			@Override
			public List<TwitterStatus> updateTimeLine() throws AllTwitterException {
				log.trace("Retrieving friends timeline");
				return twitter.getFriendsTimeline();
			}
		}, FRIENDS_TIMELINE_UPDATE_RATE, FRIENDS_TIMELINE_UPDATE_RATE, TimeUnit.SECONDS);
	}

	private void updateAvatar() {
		if (!contactController.hasUserAvatar()) {
			contactController.updateAvatar(getProfileImage());
		}
	}

	private Image getProfileImage() {
		return ImageUtil.getImage(this.getUserProfile().getImageProfileUrl());
	}

	@RequestMethod(Actions.Twitter.UPDATE_STATUS_ID)
	public Boolean updateTwitterStatus(Tweet tweet) {
		if (controlEngine.get(Model.TWITTER_LOGGED_IN)) {
			try {
				long start = System.currentTimeMillis();
				TwitterStatus updateStatus = twitter.updateStatus(tweet.getStatus());
				log.info(String.format("Updated twitter status in %d ms: %s", System.currentTimeMillis() - start,
						tweet.getStatus()));
				if (friendsTimeline.add(new DecoratedTwitterStatus(updateStatus, getUserProfile().getScreenName()))) {
					start = System.currentTimeMillis();
					notifyTimeLineChanged(0);
					log.info(String.format("Notified time line changed event to listeners, %d ms", System.currentTimeMillis()
							- start));
				}
				reporter.logUserAction(tweet.getActionType());
				return Boolean.TRUE;
			} catch (AllTwitterException e) {
				log.error("An error ocur while updating the twitter status", e);
			}
		} else {
			log.warn("User not logged in twitter, unable to send tweet: " + tweet.getStatus());
		}
		return Boolean.FALSE;
	}

	private TwitterProfile getUserProfile() {
		return userProfile;
	}

	@RequestMethod(Actions.Twitter.REQUEST_USER_PROFILE_ID)
	public TwitterProfile getProfile(String screenName) {
		try {
			UserProfile userProfile = twitter.getUserProfile(screenName);
			return userProfile != null ? new TwitterProfile(userProfile, false, isFollowingToUser(screenName)) : null;
		} catch (Exception e) {
			log.error("Unable to retrieve user profile for " + screenName, e);
		}
		return null;
	}

	private void notifyTimeLineChanged(long newTweets) {
		log.trace("Notyfing time line changed");
		controlEngine.fireValueEvent(Events.Social.TWITTER_USER_TIMELINE_CHANGED, getTimeLine());
		controlEngine.fireEvent(Events.Social.TWITTER_NUMBER_OF_NEW_MESSAGES_RECEIVED, new ValueEvent<Long>(newTweets));
	}

	@RequestMethod(Actions.Twitter.LOAD_USER_TIMELINE_ID)
	public List<DecoratedTwitterStatus> loadTimeLine() {
		try {
			addAll(twitter.getFriendsTimeline(FIRST_PAGE, ONE_HUNDRED));
		} catch (AllTwitterException e) {
			log.error("Error while retrieving time line", e);
		}
		return getTimeLine();
	}

	private List<DecoratedTwitterStatus> getTimeLine() {
		synchronized (friendsTimeline) {
			return new ArrayList<DecoratedTwitterStatus>(friendsTimeline);
		}
	}

	@RequestMethod(Actions.Twitter.REQUEST_TIMELINE_ID)
	public List<DecoratedTwitterStatus> getTimeLine(String screenName) throws AllTwitterException {
		String loggedInScreenName = userProfile.getScreenName();
		if (loggedInScreenName.equalsIgnoreCase(screenName)) {
			return getTimeLine();
		}
		try {
			List<TwitterStatus> userTimeline = twitter.getUserTimeline(screenName);
			return decorateTwitterStatusList(userTimeline, loggedInScreenName);
		} catch (AllTwitterException e) {
			log.error("Unable to show retrieve user time line for " + screenName, e);
			controlEngine.fireValueEvent(Events.Social.TWITTER_ERROR, e.getStatusCode());
		}
		return Collections.emptyList();
	}

	private List<DecoratedTwitterStatus> decorateTwitterStatusList(List<TwitterStatus> userTimeline,
			String loggedInScreenName) {
		List<DecoratedTwitterStatus> decoratedTwitterStatusList = new ArrayList<DecoratedTwitterStatus>();
		for (TwitterStatus twitterStatus : userTimeline) {
			decoratedTwitterStatusList.add(new DecoratedTwitterStatus(twitterStatus, loggedInScreenName));
		}
		return decoratedTwitterStatusList;
	}

	@RequestMethod(Actions.Twitter.REQUEST_MENTIONS_ID)
	public List<DecoratedTwitterStatus> getMentions() throws AllTwitterException {
		try {
			return decorateTwitterStatusList(twitter.getMentions(), userProfile.getScreenName());
		} catch (AllTwitterException ate) {
			log.error("Error getting mentions", ate);
			controlEngine.fireValueEvent(Events.Social.TWITTER_ERROR, ate.getStatusCode());
			return Collections.emptyList();
		}
	}

	@RequestMethod(Actions.Twitter.REQUEST_DIRECT_MESSAGES_ID)
	public List<DecoratedTwitterStatus> getDirectMessages() throws AllTwitterException {
		try {
			return decorateTwitterStatusList(twitter.getDirectMessages(), userProfile.getScreenName());
		} catch (AllTwitterException ate) {
			log.error("Error getting direct messages", ate);
			controlEngine.fireValueEvent(Events.Social.TWITTER_ERROR, ate.getStatusCode());
			return Collections.emptyList();
		}
	}

	@ActionMethod(Actions.Twitter.FOLLOW_ID)
	public void follow(String screenName) {
		try {
			twitter.followUser(screenName);
		} catch (AllTwitterException ale) {
			log.error("Error trying to follow user", ale);
			controlEngine.fireValueEvent(Events.Social.TWITTER_ERROR, ale.getStatusCode());
		}
	}

	@ActionMethod(Actions.Twitter.UNFOLLOW_ID)
	public void unfollow(String screenName) {
		try {
			twitter.unfollowUser(screenName);
		} catch (AllTwitterException ale) {
			log.error("Error trying to follow user", ale);
			controlEngine.fireValueEvent(Events.Social.TWITTER_ERROR, ale.getStatusCode());
		}
	}
	
	@RequestMethod(Actions.Twitter.POST_AUTH_READ)
	public Boolean getAuthStatus(){
		boolean twitterAuthApproved = userPreferenceService.isPostTwitterAuthorized();
		log.info("Post Twitter Auth: " + twitterAuthApproved);
		return twitterAuthApproved;
	}

	public boolean isLoggedIn() {
		return twitter.isLoggedIn();
	}

	boolean isFollowingToUser(String userScreenName) {
		try {
			return twitter.isFollowingToUser(userScreenName);
		} catch (AllTwitterException ale) {
			log.warn("Error on trying to get user screenName", ale);
		}
		return false;
	}

	private long addAll(List<TwitterStatus> updatedTimeLine) {
		long newTweets = 0;
		for (TwitterStatus twitterStatus : updatedTimeLine) {
			if (friendsTimeline.add(new DecoratedTwitterStatus(twitterStatus, getUserProfile().getScreenName()))) {
				newTweets++;
			}
		}
		return newTweets;
	}

	private abstract class TwitterMonitorTask implements Runnable {
		@Override
		public void run() {
			try {
				log.trace("Entered run method");
				updateTimeLineIfLoggedIn();
			} catch (Exception e) {
				log.error("Unexpected error while monitoring time line", e);
			}
		}

		private void updateTimeLineIfLoggedIn() throws AllTwitterException {
			if (twitter.isLoggedIn()) {
				List<TwitterStatus> updatedTimeLine = updateTimeLine();
				long newTweets = addAll(updatedTimeLine);
				if (newTweets > 0) {
					notifyTimeLineChanged(newTweets);
				}
			}
		}

		public abstract List<TwitterStatus> updateTimeLine() throws AllTwitterException;

	}

	private final class TwitterPostLoginTask implements Runnable {
		private final String username;
		private final String password;
		private final boolean rememberMe;

		public TwitterPostLoginTask(String username, String password, boolean rememberMe) {
			this.username = username;
			this.password = password;
			this.rememberMe = rememberMe;
		}

		@Override
		public void run() {
			saveUserAndPassword(username, password, rememberMe);
			updateAvatar();
			initTimeLineMonitor();
		}

	}
}
