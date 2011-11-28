package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.FACEBOOK_CHAT_LOGIN;
import static com.all.shared.messages.MessEngineConstants.REPORT_USER_ACTION;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatCredentials;
import com.all.chat.ChatType;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.UserProfileClientService;
import com.all.commons.Environment;
import com.all.core.actions.Actions;
import com.all.core.events.ErrorMessageEvent;
import com.all.core.events.Events;
import com.all.core.model.FacebookPost;
import com.all.core.model.Model;
import com.all.facebook.FacebookService;
import com.all.facebook.FacebookServiceException;
import com.all.facebook.FacebookServiceOAuthException;
import com.all.facebook.FacebookUser;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.stats.usage.UserActions;
import com.all.shared.util.StringNormalizer;

@Controller
public class FacebookController {

	private static final Log LOG = LogFactory.getLog(FacebookController.class);
	@Autowired(required = false)
	private FacebookService facebookService;
	@Autowired
	private UserProfileClientService profileService;
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private UserPreferenceService userPreferenceService;

	private EncryptorHelper encryptorHelper;

	private Map<String, FacebookUser> friends = new HashMap<String, FacebookUser>();

	@MessageMethod(MessEngineConstants.USER_SESSION_STARTED_TYPE)
	public void checkPersistedAccesToken() {
		try {
			String accessToken = readToken();
			facebookService.authorize(accessToken);
			setFacebookAuthorizedFlag(true);

			LOG.info("Valid facebook access token retrieved from persistent storage!");

			if (userPreferenceService.isFacebookChatLogin()) {
				loginIntoFacebookChat(accessToken);
			}
			obtainFriends();
		} catch (FacebookServiceException e) {
			LOG.warn("Unable to recover persisted access token or access token has expired");
		}
	}

	@RequestMethod(Actions.Facebook.AUTHORIZE_ID)
	public Void authorize(String responseUrl) {
		try {
			final String accessToken = facebookService.authorizeUrl(responseUrl);
			setFacebookAuthorizedFlag(true);
			saveToken(accessToken);
			loginIntoFacebookChat(accessToken);
			obtainFriends();

		} catch (FacebookServiceException e) {
			LOG.error("Error while authorizing facebook service", e);
			if (!e.isAccessDenied() && !Environment.isLinux()) {
				controlEngine.fireEvent(Events.Errors.ERROR_MESSAGE,
						new ErrorMessageEvent("facebook.dialog.error.auth", e.getMessage()));
			}
		}
		return null;
	}

	private void setFacebookAuthorizedFlag(boolean loggedIn) {
		controlEngine.set(Model.FACEBOOK_AUTHORIZED, loggedIn, null);
	}

	private void obtainFriends() {
		try {
			for (FacebookUser facebookUser : facebookService.getFriends()) {
				friends.put(StringNormalizer.normalize(facebookUser.getName()), facebookUser);
			}
		} catch (FacebookServiceException e) {
			LOG.error("Error while getting the facebook friends", e);
		}
	}

	private void loginIntoFacebookChat(String accessToken) {
		try {
			String user = facebookService.getXmppUser(accessToken);
			String password = facebookService.getXmppPassword(accessToken);

			ChatCredentials chatCredentials = new ChatCredentials(user, password, ChatType.FACEBOOK);

			messEngine.send(new AllMessage<ChatCredentials>(FACEBOOK_CHAT_LOGIN, chatCredentials));

			messEngine.send(new AllMessage<Integer>(REPORT_USER_ACTION, UserActions.SocialNetworks.FACEBOOK_CHAT_LOGIN));
		} catch (Exception e) {
			LOG.error("Error while logging into facebook chat", e);
		}
	}

	@ActionMethod(Actions.Facebook.CHAT_LOGIN_ID)
	public void loginIntoFacebookChat() {
		accessTokenIsStillValid();
		String accessToken = readToken();
		loginIntoFacebookChat(accessToken);
	}

	private void accessTokenIsStillValid() {
		try {
			String accessToken = readToken();
			facebookService.authorize(accessToken);
			controlEngine.set(Model.FACEBOOK_AUTHORIZED, true, null);
		} catch (FacebookServiceOAuthException e) {
			controlEngine.fireEvent(Events.Errors.ERROR_MESSAGE, new ErrorMessageEvent("facebook.dialog.error.expired.auth"));
			controlEngine.set(Model.FACEBOOK_AUTHORIZED, false, null);
		} catch (FacebookServiceException e) {
			controlEngine.set(Model.FACEBOOK_AUTHORIZED, false, null);
		}
	}

	private void saveToken(String accessToken) {
		try {
			User user = controlEngine.get(Model.CURRENT_USER);
			user.setFbToken(getEncryptorHelper(user).encrypt(accessToken));
			profileService.updateProfile(user);
		} catch (Exception e) {
			LOG.error("Error while persisting access token", e);
		}
	}

	private String readToken() {
		User user = controlEngine.get(Model.CURRENT_USER);
		return getEncryptorHelper(user).decrypt(user.getFbToken());
	}

	private EncryptorHelper getEncryptorHelper(User user) {
		// TODO chek if encryptorHelper was successfully created, if negative,
		// implements another approach.
		if (encryptorHelper == null) {
			encryptorHelper = new EncryptorHelper(user);
		}
		return encryptorHelper;
	}

	@ActionMethod(Actions.Facebook.POST_TO_FACEBOOK_ID)
	public void publishMessage(ValueAction<FacebookPost> facebookPostAction) {
		FacebookPost facebookPost = facebookPostAction.getValue();
		ContactInfo contactInfo = facebookPost.getContactInfo();
		String message = facebookPost.getPost();
		String connection = null;

		try {

			if (contactInfo == null) {
				connection = "me"; // connection to my own facebook user
			} else {
				String nomalizedName = StringNormalizer.normalize(contactInfo.getChatName());
				FacebookUser facebookUser = friends.get(nomalizedName);
				connection = facebookUser.getId();
			}

			accessTokenIsStillValid();

			if (BooleanUtils.isTrue(controlEngine.get(Model.FACEBOOK_AUTHORIZED))) {
				facebookService.publishFeed(connection, message);
				LOG.info("Succesfully published message in wall: " + connection);
			}

		} catch (Exception e) {
			controlEngine.fireEvent(Events.Errors.ERROR_MESSAGE,
					new ErrorMessageEvent("facebook.dialog.error.publish", e.getMessage()));
			LOG.error("Error while publishing a message in friend wall: " + connection, e);
		}
	}

	@RequestMethod(Actions.Facebook.GET_AUTH_AND_REDIRECT_URLS_ID)
	public String[] getAuthAndRedirectUrls() {
		String authorizationUrl = facebookService.authorizationUrl();
		String redirectUrl = facebookService.redirectUrl();
		return new String[] { authorizationUrl, redirectUrl };
	}

}
