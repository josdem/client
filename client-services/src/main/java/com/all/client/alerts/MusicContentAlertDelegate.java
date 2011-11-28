package com.all.client.alerts;

import static com.all.shared.messages.MessEngineConstants.FACEBOOK_RECOMMENDATION;
import static com.all.shared.messages.MessEngineConstants.PUT_ALERT_TYPE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatType;
import com.all.client.model.RemoteModelFactory;
import com.all.client.services.UploadContentService;
import com.all.client.services.delegates.MoveDelegate;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.Actions;
import com.all.core.actions.SendContentAction;
import com.all.core.events.Events;
import com.all.core.events.SendContentEvent;
import com.all.core.events.UploadContentDoneEvent;
import com.all.core.events.UploadContentListener;
import com.all.core.events.UploadContentProgressEvent;
import com.all.core.events.UploadContentStartedEvent;
import com.all.core.events.UploadContentUpdateEvent;
import com.all.core.model.ContactCollection;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageMethod;
import com.all.shared.alert.Alert;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.alert.MusicContentAlert;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.TrackContainer;
import com.all.shared.stats.usage.UserActions;

@Repository
public class MusicContentAlertDelegate implements UploadContentListener {

	private static final Log LOG = LogFactory.getLog(MusicContentAlertDelegate.class);

	@Autowired
	private RemoteModelFactory modelFactory;
	@Autowired
	private MoveDelegate moveDelegate;
	@Autowired
	private UploadContentService uploadContentService;
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private MessEngine messEngine;
	@Autowired
	private ClientReporter reporter;

	private final Map<Long, MusicContentAlertBoundle> pendingAlerts = Collections
			.synchronizedMap(new HashMap<Long, MusicContentAlertBoundle>());

	private boolean isFirst = true;

	@PostConstruct
	public void init() {
		uploadContentService.addUploadContentListener(this);
	}

	@Override
	public void onContentUploadUpdated(UploadContentUpdateEvent event) {
		LOG.debug("Upload " + event.getUploadId() + " has been updated.");
		controlEngine.fireEvent(Events.Alerts.UPLOAD_CONTENT_PROGRESS, new UploadContentProgressEvent(event.getUploadId(),
				event.getProgress(), event.getRemainingSeconds(), event.getUploadRate()));
	}

	@Override
	public void onContentUploadStarted(UploadContentStartedEvent event) {
		LOG.info("Upload " + event.getUploadId() + " has started.");
		controlEngine.fireValueEvent(Events.Alerts.UPLOAD_CONTENT_STARTED, Long.valueOf(event.getUploadId()));
	}

	@Override
	public void onContentUploadDone(UploadContentDoneEvent event) {
		Long uploadId = event.getUploadId();
		controlEngine.fireValueEvent(Events.Alerts.UPLOAD_CONTENT_DONE, uploadId);
		LOG.debug("UploadContentFinishedEvent received for uploadId " + uploadId);
		if (!event.isCanceled() && pendingAlerts.containsKey(uploadId)) {
			MusicContentAlertBoundle alertReference = pendingAlerts.remove(uploadId);
			ContactInfo sender = new ContactInfo(controlEngine.get(Model.CURRENT_USER));
			Date date = new Date();
			for (ContactInfo contact : alertReference.getContacts()) {
				if(contact.getChatType().equals(ChatType.FACEBOOK) && isFirst){
					sendWidgetRecommendation(alertReference.getRemoteModel());
					isFirst = false;
				}
				if(contact.getChatType().equals(ChatType.ALL)){
					send(new MusicContentAlert(sender, contact, date, alertReference.getRemoteModel(), alertReference.getMessage()));
				}
			}
			
			LOG.info("Music content alert send [uploadId: " + uploadId + ", musicAlertHolder: " + alertReference + "]");

		} else {
			LOG.warn("Could not find holder for upload id " + uploadId + ", current upload ids are " + pendingAlerts.keySet());
		}
	}
	
	private void sendWidgetRecommendation(ModelCollection model) {
		messEngine.send(new AllMessage<ModelCollection>(FACEBOOK_RECOMMENDATION, model));
	}

	@ActionMethod(Actions.Alerts.SEND_REQUEST_ALERT_ID)
	public void sendRequestAlert(McRequestAlert alert) {
		if (alert != null && alert.getId() != null) {
			messEngine.send(new AllMessage<Alert>(PUT_ALERT_TYPE, alert));
			reporter.logUserAction(UserActions.AllNetwork.ALERT_MC_REQUEST);
		}
	}

	private void send(MusicContentAlert alert) {
		messEngine.send(new AllMessage<Alert>(PUT_ALERT_TYPE, alert));
		reporter.logUserAction(UserActions.AllNetwork.ALERT_MC_CONTENT);
	}

	
	
	@RequestMethod(Actions.Social.REQUEST_UPLOAD_TIME_ID)
	public int getEstimatedTimeForUpload(ModelCollection modelCollection) {
		double totalByteSize = modelCollection.size();
		double uploadRate = uploadContentService.getUploadRate();
		return (int) (totalByteSize / uploadRate);
	}

	@RequestMethod(Actions.Social.REQUEST_UPLOADABLE_CONTENT_ID)
	public ModelCollection createUploadableModel(ModelCollection model) {
		return modelFactory.createRemoteModelWithoutReferences(model);
	}

	@ActionMethod(Actions.Social.SEND_CONTENT_ALERT_ID)
	public void send(SendContentAction action) {
		ModelCollection uploadableModel = modelFactory.createRemoteModelWithoutReferences(action.getModel());
		long uploadId = uploadContentService.submit(uploadableModel);
		pendingAlerts.put(uploadId,
				new MusicContentAlertBoundle(uploadableModel, action.getContacts(), action.getMessage()));
	}
	
	@MessageMethod(MessEngineConstants.FACEBOOK_RECOMMENDATION_ID)
	public void recommendationRequest(AllMessage<Long> response){
		Long recommendationId = response.getBody();
		controlEngine.fireEvent(Events.Alerts.RECOMMENDATION_RESPONSE, new ValueEvent<Long>(recommendationId));
	}
	
	private final class MusicContentAlertBoundle {

		private final ModelCollection remoteModel;
		private final ContactCollection contacts;
		private final String message;

		public MusicContentAlertBoundle(ModelCollection remoteModel, ContactCollection contacts, String message) {
			assert remoteModel.isRemote();
			this.remoteModel = remoteModel;
			this.contacts = contacts;
			this.message = message;
		}

		@SuppressWarnings("unchecked")
		public Collection<ContactInfo> getContacts() {
			return (Collection<ContactInfo>) (contacts != null ? contacts.getContacts() : Collections.emptyList());
		}

		public String getMessage() {
			return message;
		}

		public ModelCollection getRemoteModel() {
			return remoteModel;
		}

	}

	public void accept(MusicContentAlert alert) {
		ModelCollection model = alert.getModel();
		model.setRemote(true);
		TrackContainer destination = controlEngine.get(Model.USER_ROOT);
		moveDelegate.doMove(model, destination);
	}

	public void accept(McRequestAlert alert) {
		controlEngine.fireEvent(Events.Social.SHOW_SEND_CONTENT_DIALOG,
				new SendContentEvent(alert.getModel(), Arrays.asList(alert.getSender())));
	}
}
