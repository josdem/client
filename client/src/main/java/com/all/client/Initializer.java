package com.all.client;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;

import com.all.appControl.control.ControlEngine;
import com.all.client.devices.DevicesController;
import com.all.client.model.LocalDefaultSmartPlaylist;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalRoot;
import com.all.client.model.LocalTrash;
import com.all.client.peer.share.ShareService;
import com.all.client.services.ContactsPresenceService;
import com.all.client.services.PortraitUtil;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.UserProfileClientService;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.common.logging.ErrorEventStatProcessor;
import com.all.core.common.services.UltrapeerProxy;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

@Service
public class Initializer {
	private static final Log log = LogFactory.getLog(Initializer.class);

	@Autowired
	private UserPreferenceService userPreferenceService;
	@Autowired
	private ShareService shareService;
	@Autowired
	@Qualifier("userJdbcTemplate")
	private SimpleJdbcTemplate jdbcTemplate;
	@Autowired
	private LocalModelDao dao;
	@Autowired
	private UltrapeerProxy ultrapeerProxy;
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ContactsPresenceService presenceService;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private PortraitUtil portraitUtil;
	@Autowired
	private UserProfileClientService profileService;
	@Autowired
	private DevicesController devicesController;

	public void init(User user) {
		initializeModel(user);
		controlEngine.start();
		ultrapeerProxy.initSessionForUser(user);
		messEngine.send(new AllMessage<User>(MessEngineConstants.USER_SESSION_STARTED_TYPE, user));
		controlEngine.fireEvent(Events.Application.STARTED, new ValueEvent<User>(user));
		devicesController.setMessEngine(messEngine);
		profileService.requestAvatar(new ContactInfo(user));

		shareService.run();

		ErrorEventStatProcessor.getInstance().setUser(user.getEmail());
	}

	private void initializeModel(User user) {
		user.setAvatar(portraitUtil.getAvatarData(new ContactInfo(user)));
		controlEngine.set(Model.CURRENT_USER, user, null);
		LocalRoot root = new LocalRoot(dao);
		root.setUserMail(user.getEmail());

		if (!userPreferenceService.isSmartPlaylistDeleted(LocalDefaultSmartPlaylist.CRAPPY_KBPS)) {
			root.add(LocalDefaultSmartPlaylist.CRAPPY_KBPS.create(dao));
		}
		if (!userPreferenceService.isSmartPlaylistDeleted(LocalDefaultSmartPlaylist.TRACK_IN_TITLE)) {
			root.add(LocalDefaultSmartPlaylist.TRACK_IN_TITLE.create(dao));
		}

		controlEngine.set(Model.USER_ROOT, root, null);
		controlEngine.set(Model.USER_TRASH, new LocalTrash(dao), null);
		controlEngine.set(Model.SELECTED_ROOT, root, null);
		controlEngine.set(Model.SELECTED_CONTAINER, root, null);
		controlEngine.set(Model.CLIPBOARD_SELECTION, Collections.emptyList(), null);
	}

	public void shutdown() {
		try {
			log.info("Shutting down client...");
			User currentUser = controlEngine.get(Model.CURRENT_USER);
			controlEngine.fireEvent(Events.Application.STOPED, new ValueEvent<User>(currentUser));
			messEngine.send(new AllMessage<User>(MessEngineConstants.USER_SESSION_CLOSED_TYPE, currentUser));
			Sound.LOGIN_GOODBYE.play();
			presenceService.stop();
			shareService.interrupt();
			// stops hsqldb thread
			jdbcTemplate.update("SHUTDOWN");
			dao.close();
			// TODO AWAIT MESSENGINE TERMINATION
			Thread.sleep(5000);
			List<Message<?>> unprocessedMessages = messEngine.reset();
			if (!unprocessedMessages.isEmpty()) {
				log.warn("There were still some messages that could not be processed by MessEngine: " + unprocessedMessages);
			}
			controlEngine.stop();
			log.info("Core Application Shutdown");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
