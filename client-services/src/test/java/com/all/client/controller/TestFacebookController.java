package com.all.client.controller;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.all.action.ValueAction;
import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatCredentials;
import com.all.chat.ChatType;
import com.all.client.UnitTestCase;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.UserProfileClientService;
import com.all.core.events.ErrorMessageEvent;
import com.all.core.events.Events;
import com.all.core.model.FacebookPost;
import com.all.core.model.Model;
import com.all.facebook.FacebookService;
import com.all.facebook.FacebookServiceException;
import com.all.facebook.FacebookUser;
import com.all.messengine.MessEngine;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.stats.usage.UserActions;

public class TestFacebookController extends UnitTestCase {

	@InjectMocks
	private FacebookController facebookController = new FacebookController();

	@Mock
	private FacebookServiceException exception;
	@Mock
	private FacebookService facebookService;
	@Mock
	private EncryptorHelper encryptorHelper;
	@Mock
	private MessEngine messEngine;
	@Mock
	private UserProfileClientService profileService;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private User user;
	@Mock
	private UserPreferenceService userPreferenceService;
	@Captor
	private ArgumentCaptor<AllMessage<?>> allMesageCaptor;
	@Captor
	private ArgumentCaptor<ErrorMessageEvent> errorMessageEventCaptor;

	private String accessToken = "accessToken";

	private String responseUrl = "responseUrl";

	private String username = "username";

	private String password = "password";

	private String encryptedAccessToken = "encryptedAccessToken";

	@Before
	public void setup() throws FacebookServiceException {
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
	}

	@Test
	public void shouldCheckForSavedAccesToken() throws Exception {
		when(encryptorHelper.decrypt(user.getFbToken())).thenReturn(accessToken);
		when(facebookService.getXmppUser(accessToken)).thenReturn(username);
		when(facebookService.getXmppPassword(accessToken)).thenReturn(password);
		when(userPreferenceService.isFacebookChatLogin()).thenReturn(true);

		facebookController.checkPersistedAccesToken();

		verify(facebookService).authorize(accessToken);
		verify(controlEngine).set(Model.FACEBOOK_AUTHORIZED, true, null);

		checkPostAuthorizeCalls();
		verify(facebookService).getFriends();
	}

	@Test
	public void shouldNotDoPostAuthorizeOpsIfAccessTokenInvalid() throws Exception {
		when(encryptorHelper.decrypt(user.getFbToken())).thenReturn(accessToken);
		doThrow(new FacebookServiceException("")).when(facebookService).authorize(accessToken);

		facebookController.checkPersistedAccesToken();

		verify(controlEngine, never()).set(Model.FACEBOOK_AUTHORIZED, true, null);
		verify(facebookService, never()).getXmppUser(accessToken);
		verify(facebookService, never()).getXmppPassword(accessToken);
		verify(messEngine, never()).send(allMesageCaptor.capture());
		verify(facebookService, never()).getFriends();
	}

	@Test
	public void shouldAuthorize() throws Exception {
		prepareForAuthorization();

		facebookController.authorize(responseUrl);

		verify(facebookService).authorizeUrl(responseUrl);
		verify(controlEngine).set(Model.FACEBOOK_AUTHORIZED, true, null);
		verify(user).setFbToken(encryptedAccessToken);
		verify(profileService).updateProfile(user);

		checkPostAuthorizeCalls();
		verify(facebookService).getFriends();
	}

	private void checkPostAuthorizeCalls() throws FacebookServiceException {
		verify(messEngine, times(2)).send(allMesageCaptor.capture());

		List<AllMessage<?>> allCaptureValues = allMesageCaptor.getAllValues();
		
		ChatCredentials chatCredentials = (ChatCredentials) allCaptureValues.get(0).getBody();
		assertEquals(username, chatCredentials.getUsername());
		assertEquals(password, chatCredentials.getPassword());
		assertEquals(ChatType.FACEBOOK, chatCredentials.getType());
		
		Integer reportUserAction = (Integer) allCaptureValues.get(1).getBody();
		
		assertEquals(UserActions.SocialNetworks.FACEBOOK_CHAT_LOGIN, reportUserAction.intValue());
	}

	private void prepareForAuthorization() throws FacebookServiceException {
		when(facebookService.authorizeUrl(responseUrl)).thenReturn(accessToken);
		when(facebookService.getXmppUser(accessToken)).thenReturn(username);
		when(facebookService.getXmppPassword(accessToken)).thenReturn(password);
		when(encryptorHelper.encrypt(accessToken)).thenReturn(encryptedAccessToken);
	}

	@Test
	public void shouldNotLoginPersistTokenIfAuthorizeFails() throws Exception {
		FacebookServiceException facebookServiceException = new FacebookServiceException("",
				"ANY_OTHER_EXCEPTION_THAT_ACCESS_DENIED");
		when(facebookService.authorizeUrl(responseUrl)).thenThrow(facebookServiceException);
		when(exception.isAccessDenied()).thenReturn(true);

		facebookController.authorize(responseUrl);

		verify(controlEngine).fireEvent(eq(Events.Errors.ERROR_MESSAGE), errorMessageEventCaptor.capture());
		verify(messEngine, never()).send(allMesageCaptor.capture());
		verify(user, never()).setFbToken(anyString());
		verify(profileService, never()).updateProfile(any(User.class));
	}

	@Test
	public void shouldContinuePersistingTokenIfChatLoginFails() throws Exception {
		when(facebookService.authorizeUrl(responseUrl)).thenReturn(accessToken);
		when(facebookService.getXmppUser(accessToken)).thenReturn(username);
		when(facebookService.getXmppPassword(accessToken)).thenThrow(new FacebookServiceException(""));
		when(encryptorHelper.encrypt(accessToken)).thenReturn(encryptedAccessToken);

		facebookController.authorize(responseUrl);

		verify(controlEngine).set(Model.FACEBOOK_AUTHORIZED, true, null);
		verify(user).setFbToken(encryptedAccessToken);
		verify(profileService).updateProfile(user);
	}

	@Test
	public void shouldPublishMessageInFriendWall() throws Exception {
		String message = "message";
		String id = "id";
		ContactInfo contactInfo = mock(ContactInfo.class);
		FacebookUser facebookUser = mock(FacebookUser.class);
		ValueAction<FacebookPost> facebookPostAction = new ValueAction<FacebookPost>(new FacebookPost(contactInfo, message));

		when(facebookService.getFriends()).thenReturn(Arrays.asList(new FacebookUser[] { facebookUser }));
		when(contactInfo.getChatName()).thenReturn(username);
		when(facebookUser.getName()).thenReturn(username);
		when(facebookUser.getId()).thenReturn(id);
		when(controlEngine.get(Model.FACEBOOK_AUTHORIZED)).thenReturn(true);
		prepareForAuthorization();
		facebookController.authorize(responseUrl);

		facebookController.publishMessage(facebookPostAction);

		verify(facebookService).publishFeed(id, message);
	}

	@Test
	public void shouldPublishMessageInOwnWall() throws Exception {
		String message = "message";
		String id = "me";
		ValueAction<FacebookPost> facebookPostAction = new ValueAction<FacebookPost>(new FacebookPost(null, message));
		when(controlEngine.get(Model.FACEBOOK_AUTHORIZED)).thenReturn(true);

		facebookController.publishMessage(facebookPostAction);

		verify(facebookService).publishFeed(id, message);
	}

	@Test
	public void shouldNotPublishMessageInOwnWall() throws Exception {
		ValueAction<FacebookPost> facebookPostAction = new ValueAction<FacebookPost>(new FacebookPost(null, null));
		String id = "me";
		when(controlEngine.get(Model.FACEBOOK_AUTHORIZED)).thenReturn(false);

		facebookController.publishMessage(facebookPostAction);

		verify(facebookService, never()).publishFeed(id, null);
	}

	@Test
	public void shouldNotLoginIntoFacebookChat() throws Exception {
		when(encryptorHelper.decrypt(user.getFbToken())).thenReturn(accessToken);
		when(userPreferenceService.isFacebookChatLogin()).thenReturn(false);

		facebookController.checkPersistedAccesToken();

		verify(facebookService).authorize(accessToken);
		verify(messEngine, never()).send(allMesageCaptor.capture());
	}

	@Test
	public void shouldGetAuthAndRedirectUrls() throws Exception {
		when(facebookService.authorizationUrl()).thenReturn("authorizationUrl");
		when(facebookService.redirectUrl()).thenReturn("redirectUrl");

		String[] authAndRedirectUrls = facebookController.getAuthAndRedirectUrls();

		assertArrayEquals(new String[] { "authorizationUrl", "redirectUrl" }, authAndRedirectUrls);
	}

	@Test
	public void shouldVerifyTokenAndLoginIntoFacebookChat() throws Exception {
		when(encryptorHelper.decrypt(user.getFbToken())).thenReturn(accessToken);
		when(facebookService.getXmppUser(accessToken)).thenReturn(username);
		when(facebookService.getXmppPassword(accessToken)).thenReturn(password);

		facebookController.loginIntoFacebookChat();
		
		checkPostAuthorizeCalls();
		verify(facebookService).authorize(accessToken);
	}

}
