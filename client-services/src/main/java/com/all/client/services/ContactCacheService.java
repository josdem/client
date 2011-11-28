package com.all.client.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.model.LocalModelDao;
import com.all.client.model.predicate.ContactMailPredicate;
import com.all.client.model.predicate.ContactNamePredicate;
import com.all.client.services.reporting.ClientReporter;
import com.all.client.util.PredicateUtil;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.common.services.ApplicationDao;
import com.all.core.common.util.ImageUtil;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.PresenceInfo;
import com.all.shared.model.User;
import com.all.shared.stats.usage.UserActions;

@Service
public class ContactCacheService {

	private final Log log = LogFactory.getLog(this.getClass());

	private final Set<ContactInfo> contacts = new HashSet<ContactInfo>();
	private final Set<ContactInfo> pendingEmails = new HashSet<ContactInfo>();
	private final Set<ContactInfo> cachedContacts = new HashSet<ContactInfo>();
	private final Set<ContactInfo> facebookContacts = new HashSet<ContactInfo>();

	private String currentFilter = null;

	@Autowired
	private ClientReporter reporter;
	@Autowired
	private PortraitUtil portraitUtil;
	@Autowired
	private LocalModelDao dao;
	@Autowired
	private ApplicationDao appDao;
	@Autowired
	private UserProfileClientService profileService;
	@Autowired
	private RemoteSeederTracksService remoteSeederService;
	@Autowired
	private ControlEngine controlEngine;

	private Set<ContactInfo> foundContacts;

	@PostConstruct
	public void initialize() {
		controlEngine.set(Model.SELECTED_CHAT_TYPE, ChatType.ALL, null);
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void reset() {
		contacts.clear();
		pendingEmails.clear();
		facebookContacts.clear();
		cachedContacts.clear();
	}

	public void removeContactsByType(ChatType chatType) {
		switch (chatType) {
		case FACEBOOK:
			facebookContacts.clear();
			break;
		case ALL:
			contacts.clear();
			pendingEmails.clear();
		default:
			break;
		}
	}

	public synchronized void addContact(ContactInfo contact) {
		boolean shouldFireEvent = controlEngine.get(Model.SELECTED_CHAT_TYPE) == contact.getChatType();
		log.info("Adding contact " + contact.getEmail());
		add(contact);
		if (shouldFireEvent) {
			controlEngine.fireValueEvent(Events.Social.CONTACT_ADDED, contact);
		}
	}

	public synchronized void addContacts(Set<ContactInfo> contacts) {
		boolean shouldFireEvent = contacts.isEmpty();
		for (ContactInfo contactInfo : contacts) {
			add(contactInfo);
			if (!shouldFireEvent) {
				shouldFireEvent = controlEngine.get(Model.SELECTED_CHAT_TYPE) == contactInfo.getChatType();
			}
		}
		if (shouldFireEvent) {
			controlEngine.set(Model.DISPLAYED_CONTACT_LIST, new ArrayList<ContactInfo>(getSelectedChatContacts()),
					Events.Chat.DISPLAYED_CONTACT_LIST_UPDATED);
		}
	}

	private void add(ContactInfo contactInfo) {
		decorate(contactInfo);
		if (ChatType.FACEBOOK == contactInfo.getChatType()) {
			this.facebookContacts.add(contactInfo);
		} else {
			this.contacts.add(contactInfo);
		}
	}

	private void replace(ContactInfo previousContact, ContactInfo contact) {
		remove(previousContact);
		add(contact);
	}

	private synchronized void remove(ContactInfo contact) {
		if (ChatType.FACEBOOK == contact.getChatType()) {
			this.facebookContacts.remove(contact);
		} else {
			this.contacts.remove(contact);
		}
	}

	private List<ContactInfo> getSelectedChatContacts() {
		switch (controlEngine.get(Model.SELECTED_CHAT_TYPE)) {
		case ALL:
			List<ContactInfo> allContacts = new ArrayList<ContactInfo>(contacts);
			allContacts.addAll(pendingEmails);
			return allContacts;
		case FACEBOOK:
			return new ArrayList<ContactInfo>(facebookContacts);
		default:
			return Collections.emptyList();
		}
	}

	public void decorate(ContactInfo contact) {
		if (ChatType.ALL == contact.getChatType()) {
			contact.setAvatar(portraitUtil.getAvatarData(contact));
			if (contact.getIdLocation() != null) {
				City city = appDao.findCity(contact.getIdLocation());
				contact.setCity(city);
			}
		}
		if (contact.getAvatar() == null) {
			contact.setAvatar(ImageUtil.getDefaultAvatar());
		}
	}

	public synchronized void updateProfileInfo(ContactInfo contact) {
		ContactInfo previousContact = findByChatId(contact.getChatId());
		if (previousContact != null) {
			log.info("Updating contact " + contact.getEmail());
			contact.setStatus(previousContact.getStatus());
			contact.setChatStatus(previousContact.getChatStatus());
			contact.setAvatar(previousContact.getAvatar());
			City city = appDao.findCity(contact.getIdLocation());
			contact.setCity(city);
			replace(previousContact, contact);
			dao.update(contact);
			controlEngine.fireValueEvent(Events.Social.CONTACT_UPDATED, contact);
		}
	}

	public ContactInfo findByChatId(String chatId) {
		for (ContactInfo contact : contacts) {
			if (chatId.equals(contact.getChatId())) {
				return contact;
			}
		}
		for (ContactInfo contact : facebookContacts) {
			if (chatId.equals(contact.getChatId())) {
				return contact;
			}
		}
		for (ContactInfo contact : cachedContacts) {
			if (chatId.equals(contact.getChatId())) {
				return contact;
			}
		}
		return null;
	}

	public synchronized void deletePendingEmail(ContactInfo contact) {
		if (pendingEmails.remove(contact)) {
			if (contact.getChatType() == controlEngine.get(Model.SELECTED_CHAT_TYPE)) {
				controlEngine.fireValueEvent(Events.Social.CONTACT_DELETED, contact);
			}
		}
	}

	public void addPendingEmail(ContactInfo pendingEmail) {
		pendingEmails.add(pendingEmail);
	}

	@SuppressWarnings("unchecked")
	@ActionMethod(Actions.Social.SEARCH_CONTACTS_LOCALLY_ID)
	public synchronized void searchContactsLocally(String string) {
		if (StringUtils.isEmpty(string)) {
			string = null;
		}
		if (currentFilter != null && currentFilter.equals(string)) {
			return;
		}
		currentFilter = null;

		if (string == null) {
			controlEngine.set(Model.DISPLAYED_CONTACT_LIST, getSelectedChatContacts(),
					Events.Chat.DISPLAYED_CONTACT_LIST_UPDATED);
			foundContacts = Collections.emptySet();
			return;
		}

		Predicate predicate = getSearchPredicate(string);

		foundContacts = new HashSet<ContactInfo>(CollectionUtils.select(getSelectedChatContacts(), predicate));
		controlEngine.set(Model.DISPLAYED_CONTACT_LIST, new ArrayList<ContactInfo>(foundContacts),
				Events.Chat.DISPLAYED_CONTACT_LIST_UPDATED);
		reporter.logUserAction(UserActions.ContactList.SEARCH_CONTACT_LOCALLY);
	}

	private Predicate getSearchPredicate(String search) {
		Predicate predicate;
		String[] searchTerms = search.split(" ", -1);
		if (searchTerms.length == 1) {
			Predicate mailPredicate = new ContactMailPredicate(search);
			Predicate namePredicate = new ContactNamePredicate(search);
			predicate = new OrPredicate(namePredicate, mailPredicate);
		} else {
			List<Predicate> searchPredicates = new ArrayList<Predicate>();

			for (String wordSplitted : searchTerms) {
				// if ()
				searchPredicates.add(new ContactNamePredicate(wordSplitted));
			}
			predicate = PredicateUtil.mergeAndPredicate(searchPredicates);
		}
		return predicate;
	}

	public ContactInfo findContactById(Long id) {
		for (ContactInfo contactInfo : contacts) {
			if (id.equals(contactInfo.getId())) {
				return contactInfo;
			}
		}
		for (ContactInfo contactInfo : cachedContacts) {
			if (id.equals(contactInfo.getId())) {
				return contactInfo;
			}
		}
		return null;
	}

	public ContactInfo findContactByEmail(String email) {
		for (ContactInfo contactInfo : contacts) {
			if (email.equals(contactInfo.getEmail())) {
				return contactInfo;
			}
		}
		for (ContactInfo contactInfo : cachedContacts) {
			if (email.equals(contactInfo.getEmail())) {
				return contactInfo;
			}
		}
		return null;
	}

	public void deleteContacts(Collection<ContactInfo> contacts) {
		for (ContactInfo contact : contacts) {
			remove(contact);
			if (contact.getChatType() == controlEngine.get(Model.SELECTED_CHAT_TYPE)) {
				controlEngine.fireValueEvent(Events.Social.CONTACT_DELETED, contact);
			}
		}
	}

	public void updateStatus(PresenceInfo presenceInfo) {
		updateStatus(ChatType.ALL, presenceInfo.getEmail(), (presenceInfo.isOnline() ? ChatStatus.ONLINE
				: ChatStatus.OFFLINE));
	}

	private synchronized void updateStatus(ChatType chatType, String chatId, ChatStatus newStatus) {
		ContactInfo previousContact = findByChatId(chatId);
		if (previousContact != null) {
			if (newStatus != previousContact.getChatStatus()) {
				log.info(previousContact.getChatName() + " has changed its status from " + previousContact.getChatStatus()
						+ " to " + newStatus);
				if (newStatus == ChatStatus.OFFLINE) {
					Sound.CONTACT_OFFLINE.play();
				}
				if (newStatus == ChatStatus.ONLINE) {
					if (chatType == ChatType.ALL) {
						Sound.CONTACT_ONLINE.play();
					}
					refresh(previousContact);
				}
				previousContact.setChatStatus(newStatus);
				if (previousContact.getChatType() == controlEngine.get(Model.SELECTED_CHAT_TYPE)) {
					controlEngine.fireValueEvent(Events.Social.CONTACT_UPDATED, previousContact);
				}
				if (previousContact.getChatType() == ChatType.ALL) {
					remoteSeederService.updateCache(previousContact);
				}
			}
		}
	}

	private void refresh(ContactInfo contact) {
		if (contact.getChatType() == ChatType.ALL) {
			profileService.requestAvatar(contact);
			profileService.requestContactInfo(contact);
		}
	}

	public void updateStatus(ContactInfo contact) {
		updateStatus(contact.getChatType(), contact.getChatId(), contact.getChatStatus());
	}

	public void setSelectedChatType(ChatType newValue) {
		if (newValue != controlEngine.get(Model.SELECTED_CHAT_TYPE)) {
			controlEngine.set(Model.SELECTED_CHAT_TYPE, newValue, Events.Chat.SELECTED_CHAT_TYPE);
			controlEngine.set(Model.DISPLAYED_CONTACT_LIST, getSelectedChatContacts(),
					Events.Chat.DISPLAYED_CONTACT_LIST_UPDATED);
		}
	}

	@RequestMethod(Actions.Social.REQUEST_CONTACTS_ID)
	public List<ContactInfo> getContactsByType(ChatType type) {
		switch (type) {
		case ALL:
			return new ArrayList<ContactInfo>(contacts);
		case FACEBOOK:
			return new ArrayList<ContactInfo>(facebookContacts);
		default:
			return Collections.emptyList();
		}
	}

	public synchronized void updateQuote(Long contactId, String quote) {
		ContactInfo contact = findContactById(contactId);
		if (contact != null) {
			log.info("Updating quote of " + contact.getEmail() + " from '" + contact.getMessage() + "' to '" + quote + "'");
			contact.setMessage(quote);
			controlEngine.fireValueEvent(Events.Social.CONTACT_UPDATED, contact);
			dao.update(contact);
		}
	}

	public synchronized void updateAvatar(String chatId, byte[] avatarData, ChatType chatType) {
		if (chatType == ChatType.ALL) {
			// facebook ids change every chat session, so we'll have differents ids
			// over time for the same user, thus having
			// the same image with different names
			portraitUtil.saveAvatarInDefaultLocation(chatId, avatarData);
		}
		ContactInfo contact = findByChatId(chatId);
		if (contact != null) {
			log.info("Updating avatar for " + chatId);
			contact.setAvatar(avatarData);
			controlEngine.fireValueEvent(Events.Social.CONTACT_UPDATED, contact);
		}
		if (isCurrentUser(chatId)) {
			User currentUser = controlEngine.get(Model.CURRENT_USER);
			currentUser.setAvatar(avatarData);
			controlEngine.fireValueEvent(Events.UserProfile.USER_PROFILE_UPDATED, currentUser);
		}
	}

	private boolean isCurrentUser(String email) {
		return email.equals(controlEngine.get(Model.CURRENT_USER).getEmail());
	}

	public List<ContactInfo> getContactsByChatStatus(ChatType type, ChatStatus status) {
		if (ChatStatus.PENDING == status) {
			return new ArrayList<ContactInfo>(pendingEmails);
		}
		List<ContactInfo> contacts = getContactsByType(type);
		Iterator<ContactInfo> iterator = contacts.iterator();
		while (iterator.hasNext()) {
			ContactInfo contact = iterator.next();
			if (contact.getChatStatus() != status) {
				iterator.remove();
			}
		}
		return contacts;
	}

	public boolean isContact(ContactInfo contact) {
		return contacts.contains(contact);
	}

	public void deletePendingEmails(Set<ContactInfo> pendingContacts) {
		for (ContactInfo contact : pendingContacts) {
			deletePendingEmail(contact);
		}
	}

	public void cache(ContactInfo contact) {
		cachedContacts.remove(contact);
		decorate(contact);
		cachedContacts.add(contact);
	}

	public void loadContacts() {
		List<ContactInfo> contacts = dao.findAll(ContactInfo.class);
		for (ContactInfo contact : contacts) {
			try {
				if (!contact.isPending()) {
					contact.setChatStatus(ChatStatus.OFFLINE);
					add(contact);
				} else {
					addPendingEmail(contact);
				}
			} catch (Exception e) {
				log.error("Could not set city to conctact " + contact.getEmail());
			}
		}
		controlEngine.set(Model.DISPLAYED_CONTACT_LIST, getSelectedChatContacts(),
				Events.Chat.DISPLAYED_CONTACT_LIST_UPDATED);
	}
}
