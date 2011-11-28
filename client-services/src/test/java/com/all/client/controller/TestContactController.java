package com.all.client.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.all.appControl.control.ControlEngine;
import com.all.appControl.control.TestEngine;
import com.all.chat.ChatType;
import com.all.client.model.ContactFolder;
import com.all.client.model.ContactUserFolder;
import com.all.client.model.LocalModelDao;
import com.all.client.services.ContactCacheService;
import com.all.client.services.ContactMessageService;
import com.all.client.services.ContactsPresenceService;
import com.all.client.services.PortraitUtil;
import com.all.client.services.SearchClientService;
import com.all.client.services.UserProfileClientService;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.SendEmailInvitationAction;
import com.all.core.actions.ShareContentAction;
import com.all.core.actions.UpdateProfileAction;
import com.all.core.common.model.ApplicationModel;
import com.all.core.common.services.ApplicationDao;
import com.all.core.events.Events;
import com.all.core.events.FacebookPostContentEvent;
import com.all.core.events.SendContentEvent;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Avatar;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.PendingEmail;
import com.all.shared.model.User;

public class TestContactController {

	private final String userMail = "email@domain.com";
	@InjectMocks
	private ContactController controller = new ContactController();
	@Mock
	private ContactCacheService contactCacheservice;
	@Mock
	private ContactInfo contact;
	@Mock
	private User user;
	@Mock
	private ApplicationDao loginModelDao;
	@Mock
	private UserProfileClientService profileService;
	@Mock
	private PortraitUtil portraitUtil;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private ContactMessageService contactListService;
	@Mock
	private LocalModelDao localModelDao;
	@Mock
	private ContactsPresenceService presenceService;
	@Mock
	private Image image;
	@Spy
	private MessEngine stubEngine = new StubMessEngine();
	@Mock
	@SuppressWarnings("unused")
	private ClientReporter reporter;
	@Spy
	private TestEngine engine = new TestEngine();
	@Captor
	private ArgumentCaptor<SendContentEvent> sendTracksEventCaptor;
	@Captor
	private ArgumentCaptor<FacebookPostContentEvent> facebookEventCaptor;
	@SuppressWarnings("unused")
	@Spy
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	@Mock
	private SearchClientService service;

	private Long userId = 2L;
	private List<ContactInfo> contacts;
	private List<ContactFolder> contactFolders;
	private List<ContactUserFolder> contactUserFolders;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(true);
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
		when(user.getEmail()).thenReturn(userMail);
		when(user.getId()).thenReturn(userId);
		contacts = new ArrayList<ContactInfo>();
		contactFolders = new ArrayList<ContactFolder>();
		contactUserFolders = new ArrayList<ContactUserFolder>();
		when(contactListService.getUserContacts(user.getId())).thenReturn(contacts);
		when(localModelDao.find(isA(String.class), isA(Map.class))).thenReturn(contactUserFolders);

		HibernateTemplate ht = mock(HibernateTemplate.class);
		when(localModelDao.getHibernateTemplate()).thenReturn(ht);
		engine.setup(controller);
		((StubMessEngine) stubEngine).setup(controller);
	}

	@After
	public void teardown() {
		engine.reset(controller);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldSearchContacts() throws Exception {
		String keyword = "some name";
		when(service.search(keyword)).thenReturn(Collections.EMPTY_LIST);

		List<ContactInfo> result = controller.search(keyword);

		verify(service).search(keyword);
		assertTrue(result.isEmpty());
	}

	@Test
	public void shouldValidateSearchKeyword() throws Exception {
		String keywordA = "some@valid.com";
		String keywordB = "SOME NAME";
		String keywordC = "Some2 Invalid";
		String keywordD = "assf@a .co;//";
		String keywordE = "a b";
		String keywordF = "ab c";
		String keywordG = "a bc";
		String keywordH = "nickname";
		String keywordI = "nickname ";

		assertTrue(controller.validateSearchKeyword(keywordA));
		assertTrue(controller.validateSearchKeyword(keywordB));
		assertFalse(controller.validateSearchKeyword(keywordC));
		assertFalse(controller.validateSearchKeyword(keywordD));
		assertFalse(controller.validateSearchKeyword(keywordE));
		assertFalse(controller.validateSearchKeyword(keywordF));
		assertFalse(controller.validateSearchKeyword(keywordG));
		assertTrue(controller.validateSearchKeyword(keywordH));
		assertFalse(controller.validateSearchKeyword(keywordI));
	}

	@Test
	public void shouldSendModelCollectionToAllContact() throws Exception {
		ModelCollection model = Mockito.mock(ModelCollection.class);
		when(contact.getChatType()).thenReturn(ChatType.ALL);
		List<ContactInfo> contacts2 = Arrays.asList(contact);
		controller.sendModelCollectionToContact(new ShareContentAction(model, contacts2));

		verify(controlEngine).fireEvent(eq(Events.Social.SHOW_SEND_CONTENT_DIALOG), sendTracksEventCaptor.capture());

		SendContentEvent sendTracksEvent = sendTracksEventCaptor.getValue();
		assertEquals(model, sendTracksEvent.getModel());
		assertEquals(contacts2, sendTracksEvent.getContacts());
	}

	@Test
	public void shouldSendModelCollectionToFacebookContact() throws Exception {
		ModelCollection model = Mockito.mock(ModelCollection.class);
		when(contact.getChatType()).thenReturn(ChatType.FACEBOOK);
		List<ContactInfo> contacts = Arrays.asList(contact);
		controller.sendModelCollectionToContact(new ShareContentAction(model, contacts));

		verify(controlEngine).fireEvent(eq(Events.Social.SHOW_POST_CONTENT_ON_FACEBOOK_DIALOG),
				facebookEventCaptor.capture());

		FacebookPostContentEvent event = facebookEventCaptor.getValue();
		assertEquals(model, event.getModel());
		assertEquals(contact, event.getContact());
	}

	@Test
	public void shouldDeletePendingEmails() throws Exception {
		Set<ContactInfo> pendingEmails = new HashSet<ContactInfo>();
		PendingEmail pendingEmail = new PendingEmail();
		pendingEmails.add(new ContactInfo(pendingEmail));

		controller.deleteContacts(pendingEmails);

		verify(contactCacheservice).deletePendingEmails(pendingEmails);
		// verify(contactListService).deletePendingEmails(isA(Set.class));
		verify(localModelDao).delete(isA(ContactInfo.class));
	}

	@Test
	public void shouldSearchAContactAndAddItAsFriend() throws Exception {
		controller.onFriendshipRequest(contact);
		Message<?> message = ((StubMessEngine) stubEngine).getCurrentMessage();
		assertEquals(MessEngineConstants.FRIENDSHIP_REQUEST_TYPE, message.getType());
	}

	@Test
	public void shouldEditProfile() throws Exception {
		String cityId = "cityId";
		when(user.getIdLocation()).thenReturn(cityId);
		when(engine.get(Model.CURRENT_VIEW)).thenReturn(Views.HOME);
		City city = mock(City.class);
		when(loginModelDao.findCity(cityId)).thenReturn(city);
		User updatedUser = mock(User.class);
		Image image = mock(Image.class);
		UpdateProfileAction action = new UpdateProfileAction(updatedUser, image);
		String email = "user@mail.com";
		when(user.getEmail()).thenReturn(email);

		controller.updateUserProfile(action);

		verify(user).update(updatedUser);
		verify(profileService).updateProfile(user);
	}

	@Test
	public void shouldUpdateAvatar() throws Exception {
		Image image = mock(Image.class);
		String email = "user@mail.com";
		when(user.getEmail()).thenReturn(email);
		Long id = 2L;
		when(user.getId()).thenReturn(id);

		controller.updateAvatar(image);

		// verify(portraitUtil).extractAvatarData(image);
		verify(portraitUtil).saveAvatarInDefaultLocation(email, image);
		verify(profileService).updateAvatar(argThat(new AvatarMatcher(id)));
	}

	@Test
	public void shouldUpdateQuote() throws Exception {
		when(user.getId()).thenReturn(1L);
		String quote = "userQuote";

		controller.updateQuote(quote);

		verify(user).setQuote(quote);
		verify(profileService).updateQuote(quote);
	}

	@Test
	public void shouldSendInvitations() throws Exception {
		String contactMail = "email@server";
		List<String> recipients = Arrays.asList(contactMail);
		user.setEmail(userMail);

		controller.sendEmailInvitations(new SendEmailInvitationAction(recipients, "invite"));

		PendingEmail email = (PendingEmail) ((StubMessEngine) stubEngine).getCurrentMessage().getBody();
		assertEquals(userMail, email.getFromMail());
		assertEquals(contactMail, email.getToMail());
		assertEquals("Join to All.com", email.getSubject());
		// verify(controlEngine).fireValueEvent(eq(Events.Social.EMAIL_INVITATION_SENT),
		// isA(List.class));
	}

	class AvatarMatcher extends BaseMatcher<Avatar> {

		private final Long id;

		public AvatarMatcher(Long id) {
			this.id = id;
		}

		@Override
		public boolean matches(Object arg0) {
			if (arg0 instanceof Avatar) {
				Avatar avatar = (Avatar) arg0;
				return avatar.getIdUser().equals(id);
			}
			return false;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Avatar Matcher error");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldLoadContactsButNoContactsFoundAndContactsFoldersFound() throws Exception {
		contactFolders.add(new ContactFolder("folder1"));

		when(localModelDao.findAll(ContactInfo.class)).thenReturn(contacts);
		when(localModelDao.findAll(ContactFolder.class)).thenReturn(contactFolders);
		when(localModelDao.find(isA(String.class), isA(Map.class))).thenReturn(contactUserFolders);

		controller.loadContacts();

		verify(contactCacheservice).loadContacts();
		verify(presenceService).start();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Test
	public void shouldLoadContactsAndFolders() throws Exception {
		contacts.add(new ContactInfo());
		contactFolders.add(new ContactFolder("folder1"));
		when(localModelDao.findAll(ContactInfo.class)).thenReturn(contacts);
		when(localModelDao.findAll(ContactFolder.class)).thenReturn(contactFolders);
		when(localModelDao.find(isA(String.class), isA(Map.class))).thenReturn(contactUserFolders);

		controller.loadContacts();

		verify(contactCacheservice).loadContacts();
		verify(presenceService).start();
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldDeleteContacts() throws Exception {
		contacts.add(new ContactInfo());
		Set<ContactInfo> contactInfo = new HashSet<ContactInfo>();
		contactInfo.addAll(contacts);
		controller.deleteContacts(contactInfo);
		verify(localModelDao).delete(isA(ContactInfo.class));
	}

	@Test
	public void shoudlCreateContactButAlreadyExists() throws Exception {
		@SuppressWarnings("deprecation")
		ContactInfo contactInfo = new ContactInfo();
		String email = "test@all.com";
		contactInfo.setEmail(email);
		contactInfo.setId(1L);
		when(localModelDao.findById(ContactInfo.class, email)).thenReturn(contactInfo);

		controller.createContact(contactInfo);

		verify(localModelDao, never()).save(eq(contactInfo));
	}

	@Test
	public void shouldCreateContact() throws Exception {
		String email = "test@all.com";
		@SuppressWarnings("deprecation")
		ContactInfo contactInfo = new ContactInfo();
		contactInfo.setEmail(email);
		contactInfo.setId(1L);

		controller.createContact(contactInfo);

		verify(localModelDao).save(eq(contactInfo));
	}

	@Test
	public void shouldSaveAvatarWhenCreateAccount() throws Exception {
		String email = "test@all.com";
		@SuppressWarnings("deprecation")
		ContactInfo contactInfo = new ContactInfo();
		contactInfo.setEmail(email);
		contactInfo.setId(1l);
		Avatar avatar = Mockito.mock(Avatar.class);
		byte[] avatarBytes = new byte[100];
		when(avatar.getAvatarData()).thenReturn(avatarBytes);
		when(localModelDao.findById(ContactInfo.class, email)).thenReturn(null);

		controller.createContact(contactInfo);

		verify(localModelDao).save(eq(contactInfo));
		verify(profileService).requestAvatarIfNeeded(eq(contactInfo));
	}

	@Test
	public void shouldSavePendingEmailAsAnOfflineContact() throws Exception {
		String email = "test@all.com";
		@SuppressWarnings("deprecation")
		ContactInfo contactInfo = new ContactInfo();
		contactInfo.setEmail(email);
		contactInfo.setId(1L);
		@SuppressWarnings("deprecation")
		ContactInfo currentContact = new ContactInfo();
		currentContact.setStatus(ContactStatus.pending);
		currentContact.setEmail(email);
		when(localModelDao.findById(ContactInfo.class, email)).thenReturn(currentContact);

		controller.createContact(contactInfo);

		verify(localModelDao).save(eq(contactInfo));
		verify(localModelDao).delete(contactInfo);
		verify(profileService).requestAvatarIfNeeded(eq(contactInfo));
	}

	@Test
	public void shouldLoadContactsWhenUserSyncFinished() throws Exception {
		when(user.getEmail()).thenReturn(userMail);
		stubEngine.send(new AllMessage<String>(MessEngineConstants.LIBRARY_SYNC_DOWNLOAD_COMPLETE, userMail));

	}

	@Test
	public void shouldKnowIfCurrentUserHasAvatarSet() throws Exception {
		when(user.getEmail()).thenReturn(userMail);
		when(portraitUtil.hasAvatar(userMail)).thenReturn(false, true);

		assertFalse(controller.hasUserAvatar());
		assertTrue(controller.hasUserAvatar());
	}

	@Test
	public void shouldUpdateCurrentUserAvatar() throws Exception {
		when(user.getEmail()).thenReturn(userMail);

		controller.updateAvatar(image);

		// verify(portraitUtil).extractAvatarData(image);
		verify(portraitUtil).saveAvatarInDefaultLocation(userMail, image);
		verify(profileService).updateAvatar(any(Avatar.class));

	}

	@Test
	public void shouldGetContactInfoIds() throws Exception {
		ContactInfo contactInfo = mock(ContactInfo.class);
		when(contactInfo.getId()).thenReturn(null);
		contacts.add(contactInfo);
		when(localModelDao.findAll(ContactInfo.class)).thenReturn(contacts);

		controller.loadContacts();

		verify(presenceService).start();
	}

}
