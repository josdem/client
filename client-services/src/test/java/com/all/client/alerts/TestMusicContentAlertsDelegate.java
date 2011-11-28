package com.all.client.alerts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.appControl.control.ControlEngine;
import com.all.chat.ChatType;
import com.all.client.model.RemoteModelFactory;
import com.all.client.services.UploadContentService;
import com.all.client.services.delegates.MoveDelegate;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.SendContentAction;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.events.SendContentEvent;
import com.all.core.events.UploadContentDoneEvent;
import com.all.core.model.ContactCollection;
import com.all.core.model.Model;
import com.all.messengine.Message;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.alert.MusicContentAlert;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;
import com.all.shared.model.User;

public class TestMusicContentAlertsDelegate {

	@InjectMocks
	private MusicContentAlertDelegate delegate = new MusicContentAlertDelegate();
	@Mock
	private RemoteModelFactory modelFactory;
	@Mock
	private UploadContentService uploadContentService;
	@Captor
	private ArgumentCaptor<AllMessage<MusicContentAlert>> musicContentAlertCaptor;
	@Captor
	private ArgumentCaptor<AllMessage<ModelCollection>> faceRecommendationCaptor;
	@Mock
	private ModelCollection model;
	@Mock
	private ModelCollection remoteModel;
	@Mock
	private MoveDelegate moveDelegate;
	@Mock
	private ContactCollection contacts;
	@Mock
	private UploadContentDoneEvent event;
	@Mock
	private ContactInfo receiver;
	@Mock
	private ContactInfo contact;
	@Mock
	private User user;
	@Mock
	private ControlEngine controlEngine;
	@Spy
	private StubMessEngine messEngine;
	@SuppressWarnings("unused")
	@Mock
	private ClientReporter reporter;
	@Captor
	private ArgumentCaptor<SendContentEvent> sendTracksEventCaptor;

	private String message = "message";

	private Long uploadId = 12345L;

	@Before
	public void setup() {
		messEngine = new StubMessEngine();
		MockitoAnnotations.initMocks(this);
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
	}

	private void stubSendContentMethods() {
		when(controlEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(true);
		when(modelFactory.createRemoteModelWithoutReferences(model)).thenReturn(remoteModel);
		when(remoteModel.isRemote()).thenReturn(true);
		when(uploadContentService.submit(remoteModel)).thenReturn(uploadId);
	}

	@Test
	public void shouldItselfAddAsListener() throws Exception {
		delegate.init();
		verify(uploadContentService).addUploadContentListener(delegate);
	}

	@Test
	public void shouldSendMusicContentOnCompleteEvent() throws Exception {
		stubSendContentMethods();
		SendContentAction action = new SendContentAction(model, contacts, message);
		delegate.send(action);

		ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
		contactList.add(receiver);

		when(event.getUploadId()).thenReturn(uploadId);
		when(event.isCanceled()).thenReturn(false);
		when(receiver.getChatType()).thenReturn(ChatType.ALL);
		when(contacts.getContacts()).thenReturn(contactList);

		delegate.onContentUploadDone(event);

		verify(messEngine).send(musicContentAlertCaptor.capture());
		MusicContentAlert musicContentAlert = musicContentAlertCaptor.getValue().getBody();
		assertEquals(new ContactInfo(user), musicContentAlert.getSender());
		assertEquals(receiver, musicContentAlert.getReceiver());
		assertEquals(remoteModel, musicContentAlert.getModel());
		assertEquals(message, musicContentAlert.getPersonalizedMessage());
	}

	@Test
	public void shouldSendFacebookRecommendation() throws Exception {
		stubSendContentMethods();
		SendContentAction action = new SendContentAction(model, contacts, message);
		delegate.send(action);

		ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
		contactList.add(receiver);

		when(event.getUploadId()).thenReturn(uploadId);
		when(event.isCanceled()).thenReturn(false);
		when(receiver.getChatType()).thenReturn(ChatType.FACEBOOK);
		when(contacts.getContacts()).thenReturn(contactList);

		delegate.onContentUploadDone(event);

		verify(messEngine).send(faceRecommendationCaptor.capture());
		assertTrue(faceRecommendationCaptor.getValue().getBody() instanceof ModelCollection);
		verify(messEngine, times(1)).send(faceRecommendationCaptor.capture());

		List<Message<?>> sentMessages = messEngine.getSentMessages();
		for (Message<?> message : sentMessages) {
			assertFalse(message.getBody() instanceof MusicContentAlert);
		}
	}

	@Test
	public void shouldSendOneFacebookRecommendAndAll() throws Exception {
		stubSendContentMethods();
		SendContentAction action = new SendContentAction(model, contacts, message);
		delegate.send(action);

		ContactInfo otherFacebookContact = mock(ContactInfo.class);
		ArrayList<ContactInfo> contactList = new ArrayList<ContactInfo>();
		contactList.add(receiver);
		contactList.add(contact);
		contactList.add(otherFacebookContact);

		when(event.getUploadId()).thenReturn(uploadId);
		when(event.isCanceled()).thenReturn(false);
		when(contact.getChatType()).thenReturn(ChatType.ALL);
		when(receiver.getChatType()).thenReturn(ChatType.FACEBOOK);
		when(otherFacebookContact.getChatType()).thenReturn(ChatType.FACEBOOK);
		when(contacts.getContacts()).thenReturn(contactList);

		delegate.onContentUploadDone(event);

		List<Message<?>> sentMessages = messEngine.getSentMessages();
		assertTrue(sentMessages.size() == 2);
		assertTrue(sentMessages.get(0).getBody() instanceof ModelCollection);
		assertTrue(sentMessages.get(1).getBody() instanceof MusicContentAlert);
	}

	@Test
	public void shouldNotSendMusicContentIfUploadIdNotFound() throws Exception {
		delegate.onContentUploadDone(event);

		verify(messEngine, never()).send(any(AllMessage.class));
	}

	@Test
	public void shouldQueueSendContentAlertInUploadService() throws Exception {
		stubSendContentMethods();
		SendContentAction action = new SendContentAction(model, contacts, message);

		delegate.send(action);

		verify(modelFactory).createRemoteModelWithoutReferences(model);
		verify(uploadContentService).submit(remoteModel);
	}

	@Test
	public void shouldAcceptAllMusicContent() throws Exception {
		MusicContentAlert alert = mock(MusicContentAlert.class);
		String alertId = "alertId";
		when(alert.getId()).thenReturn(alertId);
		when(alert.getType()).thenReturn(MusicContentAlert.TYPE);
		ModelCollection model = mock(ModelCollection.class);
		when(alert.getModel()).thenReturn(model);

		delegate.accept(alert);

		verify(moveDelegate).doMove(eq(model), any(Root.class));
	}

	@Test
	public void shouldRespondMcRequest() throws Exception {
		McRequestAlert alert = mock(McRequestAlert.class);
		String alertId = "alertId";
		when(alert.getId()).thenReturn(alertId);
		when(alert.getType()).thenReturn(McRequestAlert.TYPE);
		ModelCollection model = mock(ModelCollection.class);
		when(alert.getModel()).thenReturn(model);
		ContactInfo sender = mock(ContactInfo.class);
		when(alert.getSender()).thenReturn(sender);

		delegate.accept(alert);

		verify(controlEngine).fireEvent(eq(Events.Social.SHOW_SEND_CONTENT_DIALOG), sendTracksEventCaptor.capture());

		SendContentEvent sendTracksEvent = sendTracksEventCaptor.getValue();
		assertEquals(model, sendTracksEvent.getModel());
		assertTrue(sendTracksEvent.getContacts().contains(sender));
	}

	@Test
	public void shouldGetEstimatedTime() throws Exception {
		int one_second = 1;

		when(model.size()).thenReturn(1024L);
		when(uploadContentService.getUploadRate()).thenReturn(1024);

		int estimatedTimeRequired = delegate.getEstimatedTimeForUpload(model);

		assertEquals(one_second, estimatedTimeRequired);
	}

}
