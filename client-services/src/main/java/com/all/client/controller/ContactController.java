package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.FRIENDSHIP_REQUEST_RESULT_TYPE;
import static com.all.shared.messages.MessEngineConstants.FRIENDSHIP_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.FRIENDSHIP_RESPONSE_TYPE;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.model.LocalModelDao;
import com.all.client.services.ContactCacheService;
import com.all.client.services.ContactMessageService;
import com.all.client.services.ContactsPresenceService;
import com.all.client.services.PortraitUtil;
import com.all.client.services.SearchClientService;
import com.all.client.services.UserProfileClientService;
import com.all.client.services.ViewService;
import com.all.client.services.reporting.ClientReporter;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.ComposeView;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.actions.SendEmailInvitationAction;
import com.all.core.actions.ShareContentAction;
import com.all.core.actions.UpdateProfileAction;
import com.all.core.common.ClientConstants;
import com.all.core.common.bean.RegisterUserCommand;
import com.all.core.common.model.ApplicationModel;
import com.all.core.common.services.ApplicationDao;
import com.all.core.common.util.ImageUtil;
import com.all.core.events.ErrorMessageEvent;
import com.all.core.events.Events;
import com.all.core.events.FacebookPostContentEvent;
import com.all.core.events.ProfileLoadEvent;
import com.all.core.events.SendContentEvent;
import com.all.core.model.Model;
import com.all.core.model.Profile;
import com.all.core.model.SubViews;
import com.all.core.model.Views;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.ContactRequestResult;
import com.all.shared.messages.FriendshipRequestStatus;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Avatar;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactRequest;
import com.all.shared.model.PendingEmail;
import com.all.shared.model.User;
import com.all.shared.stats.usage.UserActions;

@Controller
public class ContactController {
	private final List<Long> FRIENDSHIP_RESPONSES = new ArrayList<Long>();
	private static final Log log = LogFactory.getLog(ContactController.class);

	@Autowired
	private ContactCacheService contactCacheService;
	@Autowired
	private UserProfileClientService profileService;
	@Autowired
	private ContactMessageService contactListService;
	@Autowired
	private ContactsPresenceService presenceService;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private LocalModelDao localModelDao;
	@Autowired
	private ClientReporter reporter;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private ViewService viewService;
	@Autowired
	private PortraitUtil portraitUtil;
	@Autowired
	private Validator validator;
	@Autowired
	private SearchClientService searchService;
	@Autowired
	private MessEngine messEngine;

	@MessageMethod(MessEngineConstants.LIBRARY_SYNC_DOWNLOAD_COMPLETE)
	public void onSyncDownloadComplete(AllMessage<String> message) {
		if (isCurrentUser(message.getBody())) {
			loadContacts();
		}
	}

	@MessageMethod(MessEngineConstants.PUSH_PENDING_EMAIL_TYPE)
	public void onNewPendingEmail(AllMessage<PendingEmail> message) {
		createContact(new ContactInfo(message.getBody()));
	}

	@RequestMethod(Actions.Social.IS_CONTACT_ACCESSIBLE_ID)
	public Boolean isAccessible(ContactInfo contact) {
		return isCurrentUser(contact.getEmail()) || contactCacheService.isContact(contact);
	}

	@RequestMethod(Actions.Social.GET_CONTACT_INFO_ID)
	public ContactInfo getContactInfo(Long contactId) {
		ContactInfo contact = findContact(contactId);
		if (contact == null) {
			controlEngine.fireEvent(Events.Errors.ERROR_MESSAGE, new ErrorMessageEvent("profile.contactNotReady"));
		}
		return contact;
	}

	@ActionMethod(Actions.Social.LOAD_USER_PROFILE_ID)
	public void setCurrentProfile(LoadContactProfileAction action) {
		ContactInfo contact = action.getContact();
		if (isAccessible(contact)) {
			boolean isUserProfile = isCurrentUser(contact.getEmail());
			controlEngine.fireEvent(Events.Social.PROFILE_LOAD, ProfileLoadEvent.startLoading(contact.getEmail()));
			Profile profile = new Profile(contact, isUserProfile);
			Collection<ContactInfo> friends = new ArrayList<ContactInfo>();
			if (isUserProfile) {
				friends.addAll(contactCacheService.getContactsByType(ChatType.ALL));
			} else {
				friends.addAll(getContactFriends(contact.getId()));
				reporter.logUserAction(UserActions.AllNetwork.BROWSE_FRIEND_PROFILE);
			}
			Iterator<ContactInfo> iterator = friends.iterator();
			while (iterator.hasNext()) {
				ContactInfo next = iterator.next();
				if (next.getChatStatus() == ChatStatus.PENDING) {
					iterator.remove();
				}
			}
			profile.setFriends(friends);
			controlEngine.set(Model.CURRENT_PROFILE, profile, Events.Social.CURRENT_PROFILE);

			ComposeView composeView = action.getComposeView();

			viewService.changeCurrentComposeView(new ValueAction<ComposeView>(composeView));

			controlEngine.fireEvent(Events.Social.PROFILE_LOAD, ProfileLoadEvent.finishLoading(contact.getEmail()));
		}
	}

	public void setCurrentProfile(String contactEmail) {
		ContactInfo contact = contactCacheService.findContactByEmail(contactEmail);
		setCurrentProfile(new LoadContactProfileAction(contact, new ComposeView(Views.PROFILE, SubViews.ALL)));
	}

	// TODO THIS SHOULD NOT BE HANDLED BY THIS CLASS
	@ActionMethod(Actions.Social.SHOW_SEND_CONTENT_DIALOG_ID)
	public void sendModelCollectionToContact(ShareContentAction action) {
		switch (action.getType()) {
		case ALL:
			controlEngine.fireEvent(Events.Social.SHOW_SEND_CONTENT_DIALOG,
					new SendContentEvent(action.getModel(), action.getContacts()));
			break;
		case FACEBOOK:
			controlEngine.fireEvent(Events.Social.SHOW_POST_CONTENT_ON_FACEBOOK_DIALOG,
					new FacebookPostContentEvent(action.getModel(), action.getContacts().get(0)));
			break;
		default:
			break;
		}
	}

	private boolean isCurrentUser(String userEmail) {
		return getCurrentUser().getEmail().equals(userEmail);
	}

	private ContactInfo findContact(Long contactId) {
		User currentUser = controlEngine.get(Model.CURRENT_USER);
		if (currentUser.getId().equals(contactId)) {
			return new ContactInfo(currentUser);
		}
		return contactCacheService.findContactById(contactId);
	}

	private void sendPendingMail(PendingEmail pendingEmail) {
		messEngine.send(new AllMessage<PendingEmail>(MessEngineConstants.SEND_EMAIL_TYPE, pendingEmail));
		reporter.logUserAction(UserActions.Friendships.EMAIL_INVITATION);
	}

	private void validateInternetConnection() {
		if (!controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
			controlEngine.fireValueEvent(Events.Application.GENERIC_ERROR_MESSAGE, ClientConstants.CONNECTION_ERROR_KEY);
			throw new IllegalStateException("trying to execute an action without internet connection.");
		}
	}

	@ActionMethod(Actions.Social.REQUEST_FRIENDSHIP_ID)
	public void onFriendshipRequest(ContactInfo contact) {
		validateInternetConnection();
		validateUser(contact.getEmail());
		ContactInfo user = new ContactInfo(getCurrentUser());
		ContactRequest request = new ContactRequest(user, contact);
		AllMessage<ContactRequest> message = new AllMessage<ContactRequest>(FRIENDSHIP_REQUEST_TYPE, request);
		messEngine.send(message);
		reporter.logUserAction(UserActions.Friendships.FRIENDSHIP_REQUEST);
	}

	@ActionMethod(Actions.Social.MULTIPLE_REQUEST_FRIENDSHIP_ID)
	public void sendMultipleFriendshipRequests(List<ContactInfo> contacts) {
		for (ContactInfo contactInfo : contacts) {
			onFriendshipRequest(contactInfo);
		}
	}

	@MessageMethod(FRIENDSHIP_REQUEST_RESULT_TYPE)
	public void onContactAddedRequestResponse(AllMessage<ContactRequestResult> message) {
		FriendshipRequestStatus result = message.getBody().getStatus();
		// TODO: TEMPORAL PATCH FOR STRANGE BUG
		// TEMPORAL?? MY ASS
		if (FriendshipRequestStatus.ALREADY_FRIENDS == result || FriendshipRequestStatus.RECIPROCAL_INVITATION == result
				|| FriendshipRequestStatus.REACTIVATED_FRIENDSHIP == result) {
			createContact(message.getBody().getRequested());
		}
		controlEngine.fireValueEvent(Events.Social.REQUEST_FRIENDSHIP_RESPONSE, result);
	}

	@ActionMethod(Actions.UserProfile.UPDATE_PROFILE_ID)
	public void updateUserProfile(UpdateProfileAction action) {
		User currentUser = getCurrentUser();
		User user = action.getUser();
		currentUser.update(user);

		profileService.updateProfile(currentUser);
		currentUser.setCity((City) applicationDao.findCity(user.getIdLocation()));
		controlEngine.fireEvent(Events.UserProfile.USER_PROFILE_UPDATED, new ValueEvent<User>(currentUser));
		if (action.getAvatar() != null) {
			updateAvatar(action.getAvatar());
		}
	}

	public void respondFriendshipRequest(ContactRequest request) {
		AllMessage<ContactRequest> message = new AllMessage<ContactRequest>(FRIENDSHIP_RESPONSE_TYPE, request);
		if (FRIENDSHIP_RESPONSES.contains(request.getId())) {
			log.error("!!!!! ERROR: TRYING TO RESPOND AN ALREADY RESPONDED REQUEST: " + this + " !!!!!!");
		} else {
			FRIENDSHIP_RESPONSES.add(request.getId());
			log.debug("=========!!!! RESPONDING A FRIENDSHIP REQUEST FROM:  " + this + " !!!!!!=======");
			messEngine.send(message);
		}
	}

	boolean hasUserAvatar() {
		return portraitUtil.hasAvatar(getCurrentUser().getEmail());
	}

	@ActionMethod(Actions.UserProfile.UPDATE_AVATAR_ID)
	public void updateAvatar(Image image) {
		User user = getCurrentUser();
		byte[] avatarData = ImageUtil.extractAvatarData(image);
		Avatar avatar = new Avatar(user.getId(), avatarData);
		user.setAvatar(avatarData);
		portraitUtil.saveAvatarInDefaultLocation(user.getEmail(), image);
		profileService.updateAvatar(avatar);
		controlEngine.fireEvent(Events.UserProfile.USER_PROFILE_UPDATED, new ValueEvent<User>(user));
	}

	@ActionMethod(Actions.UserProfile.UPDATE_QUOTE_ID)
	public void updateQuote(String quote) {
		User user = getCurrentUser();
		user.setQuote(quote);
		profileService.updateQuote(quote);
		controlEngine.fireEvent(Events.UserProfile.USER_PROFILE_UPDATED, new ValueEvent<User>(user));
	}

	@ActionMethod(Actions.Social.SEND_EMAIL_INVITATION_ID)
	public void sendEmailInvitations(SendEmailInvitationAction action) {
		validateInternetConnection();
		User currentUser = getCurrentUser();
		String emailUser = currentUser.getEmail();
		Long userId = currentUser.getId();
		for (String recipient : action.getRecipients()) {
			PendingEmail email = new PendingEmail(emailUser, recipient, userId, action.getBody());
			email.setNickName(currentUser.getNickName());
			sendPendingMail(email);
		}
		controlEngine.fireValueEvent(Events.Social.EMAIL_INVITATION_SENT, action.getRecipients());
	}

	@ActionMethod(Actions.Social.DELETE_CONTACTS_ID)
	public void deleteContacts(Set<ContactInfo> contacts) {
		for (ContactInfo contactInfo : contacts) {
			localModelDao.delete(contactInfo);
		}
		boolean arePendingEmails = contacts.iterator().next().isPending();
		if (arePendingEmails) {
			contactListService.deletePendingEmails(contacts);
			contactCacheService.deletePendingEmails(contacts);
		} else {
			contactCacheService.deleteContacts(contacts);
			contactListService.deleteContacts(getCurrentUser().getId(), contacts);
		}
		Sound.CONTACT_DELETE.play();
	}

	public void loadContacts() {
		contactCacheService.loadContacts();
		presenceService.start();
	}

	public void createContact(ContactInfo contact) {
		ContactInfo current = localModelDao.findById(ContactInfo.class, contact.getEmail());
		if (current == null) {
			saveContact(contact);
		} else if (current.isPending() && !contact.isPending()) {
			localModelDao.delete(current);
			contactCacheService.deletePendingEmail(contact);
			saveContact(contact);
		}
	}

	private void saveContact(ContactInfo contact) {
		localModelDao.save(contact);
		contactCacheService.addContact(contact);
		presenceService.requestContactStatus(contact);
		profileService.requestAvatarIfNeeded(contact);
	}

	private List<ContactInfo> getContactFriends(Long userId) {
		List<ContactInfo> contacts = contactListService.getUserContacts(userId);
		Iterator<ContactInfo> iterator = contacts.iterator();
		while (iterator.hasNext()) {
			ContactInfo contact = iterator.next();
			if (contact.isPending()) {
				iterator.remove();
			} else {
				contactCacheService.cache(contact);
			}
		}
		profileService.requestAvatarsIfNeeded(contacts);
		return contacts;
	}

	@RequestMethod(Actions.Social.REQUEST_ONLINE_USERS_ID)
	public List<ContactInfo> getOnlineUsers() {
		List<ContactInfo> onlineUsers = contactListService.getOnlineUsers();
		for (ContactInfo onlineUser : onlineUsers) {
			contactCacheService.cache(onlineUser);
		}
		profileService.requestAvatarsIfNeeded(onlineUsers);
		return onlineUsers;
	}

	private User getCurrentUser() {
		return controlEngine.get(Model.CURRENT_USER);
	}

	private void validateUser(String email) {
		if (isCurrentUser(email)) {
			controlEngine.fireValueEvent(Events.Application.GENERIC_ERROR_MESSAGE, ClientConstants.SELF_INVITATION_ERROR_KEY);
			throw new IllegalArgumentException("Cannot add same user as friend.");
		}
	}

	@RequestMethod(Actions.Social.VALIDATE_SEARCH_KEYWORD_ID)
	public Boolean validateSearchKeyword(String keyword) {
		for (String name : keyword.split(" ")) {
			if (name.length() < 2) {
				return Boolean.FALSE;
			}
		}

		RegisterUserCommand user = new RegisterUserCommand();
		Set<ConstraintViolation<RegisterUserCommand>> violations = null;
		String[] split = keyword.split(" ");
		if (split.length == 1) {
			if (keyword.contains("@")) {
				user.setEmail(keyword.toUpperCase());
				violations = validator.validateProperty(user, "email");
			} else {
				user.setNickName(keyword.toUpperCase());
				violations = validator.validateProperty(user, "nickName");
			}
		} else if (split.length >= 2) {
			user.setFirstName(keyword.toUpperCase());
			violations = validator.validateProperty(user, "firstName");
		}
		return Boolean.valueOf(violations.isEmpty());
	}

	@RequestMethod(Actions.Social.SEARCH_CONTACTS_ID)
	public List<ContactInfo> search(String keyword) {
		List<ContactInfo> contacts = searchService.search(keyword);
		for (ContactInfo contact : contacts) {
			contactCacheService.cache(contact);
			profileService.requestAvatarIfNeeded(contact);
		}
		return contacts;
	}

}
