package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.ALERTS_REQUEST_TYPE;
import static com.all.shared.messages.MessEngineConstants.DELETE_ALERT_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.alerts.FriendshipAlertsDelegate;
import com.all.client.alerts.MusicContentAlertDelegate;
import com.all.client.services.ContactCacheService;
import com.all.client.services.UserProfileClientService;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.common.ClientConstants;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.MessageMethod;
import com.all.shared.alert.Alert;
import com.all.shared.alert.FriendshipRequestAlert;
import com.all.shared.alert.FriendshipResponseAlert;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.alert.MusicContentAlert;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;

@Controller
public class AlertController {

	private static final List<String> DELETED_ALERTS = new ArrayList<String>();
	private static final List<String> BUFFERED_SENDERS = new ArrayList<String>();

	private final Log log = LogFactory.getLog(this.getClass());

	private final Set<Alert> currentAlerts = new TreeSet<Alert>();

	private final ScheduledExecutorService requestExecutor = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("AlertsRequester"));
	@Autowired
	private ContactController contactController;
	@Autowired
	private ContactCacheService contactCacheService;
	@Autowired
	private MusicContentAlertDelegate contentAlertDelegate;
	@Autowired
	private FriendshipAlertsDelegate friendshipAlertsDelegate;
	@Autowired
	private UserProfileClientService profileService;

	@Autowired
	private ControlEngine controlEngine;

	@Autowired
	private MessEngine messEngine;

	@MessageMethod(MessEngineConstants.LIBRARY_SYNC_DOWNLOAD_COMPLETE)
	public void onSyncDownloadComplete(AllMessage<String> message) {
		if (isCurrentUser(message.getBody())) {
			requestExecutor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					requestAlerts();
				}
			}, 0, ClientConstants.ALERTS_REQUEST_DELAY, TimeUnit.MILLISECONDS);
		}
	}

	@MessageMethod(MessEngineConstants.PUSH_ALERT_TYPE)
	public void onAlertMessageReceived(AllMessage<Alert> message) {
		if (validateNewAlert(message.getBody())) {
			addToCurrentAlerts(message.getBody());
		}
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void onUserSessionClosed() {
		removeAllAlerts();
	}

	@ActionMethod(Actions.Alerts.ALERT_ACTION_ACCEPT_ID)
	public void accept(Alert alert) {
		if (alert instanceof McRequestAlert) {
			acceptMcRequestAlert((McRequestAlert) alert);
		} else if (alert instanceof MusicContentAlert) {
			acceptMusicContentAlert((MusicContentAlert) alert);
		} else if (alert instanceof FriendshipRequestAlert) {
			acceptFriendshipRequestAlert((FriendshipRequestAlert) alert);
		}
	}

	public void acceptMcRequestAlert(McRequestAlert alert) {
		contentAlertDelegate.accept(alert);
		delete(alert);
	}

	public void acceptMusicContentAlert(MusicContentAlert alert) {
		contentAlertDelegate.accept(alert);
		delete(alert);
	}

	public void acceptFriendshipRequestAlert(FriendshipRequestAlert requestAlert) {
		friendshipAlertsDelegate.accept(requestAlert);
		delete(requestAlert);
	}

	@ActionMethod(Actions.Alerts.ALERT_ACTION_DELETE_ID)
	public void delete(Alert alert) {
		if (alert instanceof FriendshipRequestAlert) {
			friendshipAlertsDelegate.deny((FriendshipRequestAlert) alert);
		}
		sendDeleteRequest(alert);
		if (currentAlerts.remove(alert)) {
			DELETED_ALERTS.add(alert.getId());
			controlEngine.set(Model.CURRENT_ALERTS, currentAlerts, Events.Application.CURRENT_ALERTS_CHANGED);
		}
	}

	private void addToCurrentAlerts(Alert alert) {
		boolean newAlerts = currentAlerts.add(alert);
		if (newAlerts) {
			Sound.ALERT_RECEIVED.play();
			controlEngine.set(Model.CURRENT_ALERTS, currentAlerts, Events.Application.CURRENT_ALERTS_CHANGED);
			controlEngine.fireEvent(Events.Alerts.NEW_ALERT);
		}
	}

	private void addToCurrentAlerts(List<Alert> alerts) {
		if (alerts != null && !alerts.isEmpty()) {
			boolean newAlerts = this.currentAlerts.addAll(alerts);
			if (newAlerts) {
				Sound.ALERT_RECEIVED.play();
				controlEngine.set(Model.CURRENT_ALERTS, alerts, Events.Application.CURRENT_ALERTS_CHANGED);
				controlEngine.fireEvent(Events.Alerts.NEW_ALERT);
			}
		}
	}

	private void removeAllAlerts() {
		currentAlerts.clear();
		controlEngine.set(Model.CURRENT_ALERTS, currentAlerts, Events.Application.CURRENT_ALERTS_CHANGED);
	}

	public synchronized void requestAlerts() {
		if (controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
			try {
				log.info("Requesting user alerts...");
				Future<Message<?>> future = messEngine.request(new AllMessage<String>(ALERTS_REQUEST_TYPE, getCurrentUser()
						.getEmail()), MessEngineConstants.ALERTS_RESPONSE_TYPE, ClientConstants.ALERTS_REQUEST_DELAY / 2);
				@SuppressWarnings("unchecked")
				AllMessage<Collection<Alert>> message = (AllMessage<Collection<Alert>>) future.get();
				log.info("Alerts response received...");
				List<Alert> newAlerts = new ArrayList<Alert>();
				for (Alert alert : message.getBody()) {
					if (validateNewAlert(alert)) {
						newAlerts.add(alert);
					}
				}
				addToCurrentAlerts(newAlerts);
			} catch (Exception e) {
				log.error("Unexpected exception requesting user alerts", e);
			}
		}
	}

	private boolean validateNewAlert(Alert alert) {
		ContactInfo sender = alert.getSender();
		if (!BUFFERED_SENDERS.contains(sender.getEmail())) {
			profileService.requestAvatar(sender);
			BUFFERED_SENDERS.add(sender.getEmail());
		}
		contactCacheService.decorate(sender);
		if (alert instanceof FriendshipResponseAlert) {
			log.debug("!!!! RECEIVED FRIENDSHIP RESPONSE ALERT!!! ======>" + alert.getId());
			contactController.createContact(sender);
		}
		if (!DELETED_ALERTS.contains(alert.getId())) {
			return true;
		}
		return false;
	}

	private void sendDeleteRequest(Alert alert) {
		messEngine.send(new AllMessage<Alert>(DELETE_ALERT_TYPE, alert));
	}

	private boolean isCurrentUser(String userEmail) {
		return getCurrentUser().getEmail().equals(userEmail);
	}

	private User getCurrentUser() {
		return controlEngine.get(Model.CURRENT_USER);
	}

}
