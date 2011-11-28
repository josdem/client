package com.all.client.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.DecoratedTwitterStatus;
import com.all.client.model.TwitterProfile;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.reporting.ClientReporter;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.core.model.Tweet;
import com.all.core.model.TwitterUser;
import com.all.messengine.MessEngine;
import com.all.shared.stats.usage.UserActions;
import com.all.testing.Stub;
import com.all.twitter.AllTwitter;
import com.all.twitter.AllTwitterException;
import com.all.twitter.TwitterStatus;
import com.all.twitter.UserProfile;

public class TestTwitterController {
	@InjectMocks
	private TwitterController twitterController = new TwitterController();
	@Mock
	private AllTwitter twitter;
	@Mock
	private ContactController contactController;
	@Mock
	private Image image;
	@SuppressWarnings("unused")
	@Mock
	private MessEngine messEngine;
	@Mock
	private ControlEngine controlEngine;
	@Stub
	private ScheduledExecutorService twitterExecutor = Executors.newScheduledThreadPool(20,
			new IncrementalNamedThreadFactory("TwitterMonitor"));
	@Mock
	private TwitterStatus twitterStatus;
	@Mock
	private AllTwitterException allTwitterException;
	@Mock
	private UserProfile userProfile;
	@Mock
	private TwitterProfile decoratedUserProfile;
	@Mock
	private UserPreferenceService userPreferenceService;
	@SuppressWarnings("unused")
	@Stub
	private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	@Mock
	private ClientReporter reporter;
	@Mock
	private EncryptorHelper encryptorHelper;

	private int statusCode = 401;

	private String username = "username";
	private String password = "password";
	private String screenName = "screenName";
	private String usernameCypher = "SDFAVTSadfGAervDCA";
	private String passwordCypher = ">SDFCASsbrFVVSCVASFDV";

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(allTwitterException.getStatusCode()).thenReturn(statusCode);
		when(decoratedUserProfile.getScreenName()).thenReturn(screenName);
		when(encryptorHelper.encrypt(username)).thenReturn(usernameCypher);
		when(encryptorHelper.encrypt(password)).thenReturn(passwordCypher);
		when(encryptorHelper.decrypt(usernameCypher)).thenReturn(username);
		when(encryptorHelper.decrypt(passwordCypher)).thenReturn(password);
	}

	@After
	public void tearDown() {
		twitterExecutor.shutdownNow();
	}

	@Test
	public void shouldLoginAndUpdateAvatar() throws Exception {
		when(twitter.getUserProfile()).thenReturn(userProfile);

		twitterController.login(new TwitterUser(username, password, false));

		verify(twitter).login(username, password);
		verify(twitter).getUserProfile();
		verify(contactController, timeout(1000)).hasUserAvatar();
		verify(contactController, timeout(1000)).updateAvatar(null);
	}

	@Test
	public void shouldLoginAndNotUpdateAvatar() throws Exception {
		when(twitter.getUserProfile()).thenReturn(userProfile);
		when(contactController.hasUserAvatar()).thenReturn(true);

		twitterController.login(new TwitterUser(username, password, false));

		verify(twitter).login(username, password);
		verify(twitter).getUserProfile();
		verify(contactController, timeout(1000)).hasUserAvatar();
		verify(contactController, timeout(1000).never()).updateAvatar(any(Image.class));
	}

	@Test
	public void shouldLoginAndNotSaveUserAndPassword() throws Exception {
		boolean rememberMe = false;
		when(twitter.getUserProfile()).thenReturn(userProfile);
		when(contactController.hasUserAvatar()).thenReturn(true);

		twitterController.login(new TwitterUser(username, password, rememberMe));

		verify(twitter).login(username, password);
		verify(twitter).getUserProfile();
		verify(contactController, timeout(1000000)).hasUserAvatar();
		verify(contactController, timeout(1000000).never()).updateAvatar(any(Image.class));
		checkExpectations(userPreferenceService, usernameCypher, passwordCypher, false);
	}

	private void checkExpectations(UserPreferenceService userPref2, String username, String password, boolean rememberMe) {
		verify(userPref2, timeout(1000)).setTwitterUser(username);
		verify(userPref2, timeout(1000)).setTwitterPassword(password);
		verify(userPref2, timeout(1000)).setTwitterRememberMe(rememberMe);

	}

	@Test
	public void shouldLoginAndSaveUserAndPassword() throws Exception {
		boolean rememberMe = true;
		when(twitter.getUserProfile()).thenReturn(userProfile);
		when(contactController.hasUserAvatar()).thenReturn(rememberMe);

		twitterController.login(new TwitterUser(username, password, rememberMe));

		verify(twitter).login(username, password);
		verify(twitter).getUserProfile();
		verify(contactController, timeout(1000)).hasUserAvatar();
		verify(contactController, timeout(1000).never()).updateAvatar(any(Image.class));
		checkExpectations(userPreferenceService, usernameCypher, passwordCypher, rememberMe);
	}

	@Test
	public void shouldUpdateStaus() throws Exception {
		String status = "status";

		when(controlEngine.get(Model.TWITTER_LOGGED_IN)).thenReturn(true);
		when(twitter.getUserProfile()).thenReturn(userProfile);
		Tweet tweet = new Tweet(status, UserActions.SocialNetworks.TWITTER_STATUS);
		assertTrue(twitterController.updateTwitterStatus(tweet));

		verify(twitter).updateStatus(status);
		verify(reporter).logUserAction(tweet.getActionType());
		// verify(controlEngine).fireEvent(eq(Events.Social.TWITTER_NUMBER_OF_NEW_MESSAGES_RECEIVED),
		// any(ValueEvent.class));
	}

	@Test
	public void shouldNotUpdateStausIfApiException() throws Exception {
		String status = "status";
		when(controlEngine.get(Model.TWITTER_LOGGED_IN)).thenReturn(true);
		Tweet tweet = new Tweet(status, UserActions.SocialNetworks.TWITTER_STATUS);

		doThrow(allTwitterException).when(twitter).updateStatus(status);

		assertFalse(twitterController.updateTwitterStatus(tweet));

		verify(twitter).updateStatus(status);
		verify(reporter, never()).logUserAction(tweet.getActionType());
	}

	@Test
	public void shouldNotUpdateAppAvatarIfLoginError() throws Exception {
		doThrow(allTwitterException).when(twitter).login(anyString(), anyString());

		twitterController.login(new TwitterUser("", "", false));

		verify(contactController, never()).updateAvatar(image);
	}

	@Test
	public void shouldKnowIfLoggedIn() throws Exception {
		when(twitter.isLoggedIn()).thenReturn(false, true);
		assertFalse(twitterController.isLoggedIn());
		assertTrue(twitterController.isLoggedIn());
	}

	@Test
	public void shouldShutdown() throws Exception {
		twitterController.shutdown();
	}

	@Test
	public void shouldGetUserProfile() throws Exception {
		when(twitter.getUserProfile(screenName)).thenReturn(userProfile);

		TwitterProfile userProfileResult = twitterController.getProfile(screenName);

		verify(twitter).getUserProfile(screenName);
		verify(twitter).isFollowingToUser(screenName);
		assertEquals(false, userProfileResult.isLoggedInUser());
		assertEquals(false, userProfileResult.isFollowing());
	}

	@Test
	public void shouldGetUserProfileFromScreenNameTwiceBecasueCouldNotBeCached() throws Exception {
		assertNull(twitterController.getProfile(screenName));

		verify(twitter).getUserProfile(screenName);
		verify(twitter, never()).isFollowingToUser(anyString());
	}

	@Test
	public void shouldReturnNullIfAnExceptionHappes() throws Exception {
		doThrow(allTwitterException).when(twitter).getUserProfile(screenName);

		assertNull(twitterController.getProfile(screenName));

		verify(twitter).getUserProfile(screenName);
		verify(twitter, never()).isFollowingToUser(anyString());
	}

	@Test
	public void shouldGetTimeLineFromScreenName() throws Exception {
		List<TwitterStatus> statusList = new ArrayList<TwitterStatus>();
		statusList.add(twitterStatus);
		String otherScreenName = "OtherScreenName";

		when(twitter.isLoggedIn()).thenReturn(true);
		when(twitter.getUserProfile()).thenReturn(userProfile);
		when(userProfile.getScreenName()).thenReturn(screenName);
		when(twitter.getUserTimeline(otherScreenName)).thenReturn(statusList);

		Collection<DecoratedTwitterStatus> timeLine = twitterController.getTimeLine(otherScreenName);

		verify(twitter).getUserTimeline(anyString());
		assertEquals(1, timeLine.size());
		assertEquals(screenName, timeLine.iterator().next().getLoggedInScreenName());
	}

	@Test
	public void shouldGetTimeLineFromLoggedInUserWhenScreenNameEqualsUserProfileScreenName() throws Exception {
		when(twitter.isLoggedIn()).thenReturn(true);
		when(twitter.getUserProfile()).thenReturn(userProfile);
		when(userProfile.getScreenName()).thenReturn(screenName.toLowerCase());

		twitterController.getTimeLine(screenName);

		verify(twitter, never()).getUserTimeline(anyString());
	}

	@Test
	public void shouldGetEmptyTimeLineAndShowErrorOnTwitterException() throws Exception {
		List<TwitterStatus> statusList = new ArrayList<TwitterStatus>();
		statusList.add(twitterStatus);
		String otherScreenName = "OtherScreenName";

		when(twitter.isLoggedIn()).thenReturn(true);
		when(twitter.getUserProfile()).thenReturn(userProfile);
		when(userProfile.getScreenName()).thenReturn(screenName);
		doThrow(allTwitterException).when(twitter).getUserTimeline(otherScreenName);

		Collection<DecoratedTwitterStatus> timeLine = twitterController.getTimeLine(otherScreenName);

		assertTrue(timeLine.isEmpty());
		verify(controlEngine).fireValueEvent(Events.Social.TWITTER_ERROR, statusCode);
	}

	@Test
	public void shouldFollowUser() throws Exception {
		twitterController.follow(screenName);
		verify(twitter).followUser(screenName);
	}

	@Test
	public void shouldCallDialogFactoryIfFollowThrowsException() throws Exception {
		doThrow(allTwitterException).when(twitter).followUser(screenName);
		twitterController.follow(screenName);
		verify(controlEngine).fireValueEvent(Events.Social.TWITTER_ERROR, statusCode);
	}

	@Test
	public void shouldUnfollowUser() throws Exception {
		twitterController.unfollow(screenName);
		verify(twitter).unfollowUser(screenName);
	}

	@Test
	public void shouldCallDialogFactoryIfUnfollowThrowsException() throws Exception {
		doThrow(allTwitterException).when(twitter).unfollowUser(screenName);
		twitterController.unfollow(screenName);
		verify(controlEngine).fireValueEvent(Events.Social.TWITTER_ERROR, statusCode);
	}

	@Test
	public void shouldKnowIfUnfollowingToUser() throws Exception {
		twitterController.isFollowingToUser(screenName);
		verify(twitter).isFollowingToUser(screenName);
	}

	@Test
	public void shouldCallDialogFactoryIfIsFollowingThrowsException() throws Exception {
		doThrow(allTwitterException).when(twitter).isFollowingToUser(screenName);
		assertFalse(twitterController.isFollowingToUser(screenName));
	}

	@Test
	public void shouldGetMentions() throws Exception {
		Collection<DecoratedTwitterStatus> mentions = twitterController.getMentions();

		assertNotNull(mentions);
		assertTrue(mentions.isEmpty());
	}

	@Test
	public void shouldGetDirectMessages() throws Exception {
		Collection<DecoratedTwitterStatus> directMessages = twitterController.getDirectMessages();

		assertNotNull(directMessages);
		assertTrue(directMessages.isEmpty());
	}
	
	@Test
	public void shouldAuthorizePost() throws Exception {
		boolean auth = true;
		
		twitterController.authorizePost(auth);
		
		verify(userPreferenceService).setPostAuthorization(auth);
	}
	
	@Test
	public void shouldKnowTwitterAuthStatus() throws Exception {
		twitterController.getAuthStatus();
		
		verify(userPreferenceService).isPostTwitterAuthorized();
	}

}
