package com.all.client.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.ContactRoot;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalModelFactory;
import com.all.client.model.PlaylistTrack;
import com.all.client.services.delegates.DeleteDelegate;
import com.all.client.services.delegates.ImportFilesDelegate;
import com.all.client.services.delegates.MoveDelegate;
import com.all.core.actions.ModelMoveAction;
import com.all.core.common.services.reporting.Reporter;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.mc.manager.McManager;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.model.User;

public class TestModelService {
	@InjectMocks
	private ModelService controller = new ModelService();

	@Mock
	private ControlEngine controlEngine;
	@Mock
	@SuppressWarnings("unused")
	private Reporter reporter;
	@Mock
	private LocalModelFactory localModelFactory;
	@Mock
	@SuppressWarnings("unused")
	private LocalModelDao localModelDao;
	@Mock
	private MusicEntityService musicEntityService;
	@Mock
	@SuppressWarnings("unused")
	private ImportFilesDelegate importFilesDelegate;
	@Mock
	@SuppressWarnings("unused")
	private DeleteDelegate deleteDelegate;
	@Mock
	private MoveDelegate moveDelegate;
	@Mock
	private ContactCacheService contactCacheService;
	@Mock
	private McManager mcManager;

	private User user = mock(User.class);
	private Track track = mock(Track.class);
	private Track track2 = mock(Track.class);
	private File file = mock(File.class);
	private Root root = mock(Root.class);
	private ModelCollection modelCollection = mock(ModelCollection.class);
	private ContactRoot contactRoot = mock(ContactRoot.class);
	private ContactInfo contact = mock(ContactInfo.class);

	String id = "id";
	List<PlaylistTrack> playlistTracks = new ArrayList<PlaylistTrack>();
	private String contactEmail = "contact@all.com";

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
	}

	@Test
	public void shouldUpdateTrack() throws Exception {
		when(localModelFactory.findByHashCode(id)).thenReturn(track);
		when(musicEntityService.updateDownloadedTrack(track, file)).thenReturn(track2);
		when(track.getHashcode()).thenReturn("hashCode");
		when(track2.getHashcode()).thenReturn("hashCode");

		Track result = controller.updateDownloadedTrack(id, file);

		verify(musicEntityService).updateDownloadedTrack(track, file);
		verify(controlEngine, atLeastOnce()).fireEvent(eq(Events.Library.CONTAINER_MODIFIED), any(ContainerModifiedEvent.class));
		verify(controlEngine).fireEvent(Events.Library.NEW_CONTENT_AVAILABLE);
		assertEquals(track2, result);
	}

	@Test
	public void shouldImportFromRemoteLibraryOfAnOnlineContact() throws Exception {
		when(controlEngine.get(Model.SELECTED_ROOT)).thenReturn(contactRoot);

		when(contactRoot.getOwner()).thenReturn(contact);
		when(contact.getEmail()).thenReturn(contactEmail);
		when(contactCacheService.findContactByEmail(contactEmail)).thenReturn(contact);
		when(contact.isOnline()).thenReturn(true);

		controller.moveModel(new ModelMoveAction(modelCollection, root));

		verify(moveDelegate).doMove(modelCollection, root);
		verify(controlEngine, never()).fireValueEvent(eq(Events.Alerts.CONFIRM_REQUEST_ALERT), any(McRequestAlert.class));
	}

	@Test
	public void shouldImportFromRemoteLibraryOfAnOfflineContact() throws Exception {
		when(controlEngine.get(Model.SELECTED_ROOT)).thenReturn(contactRoot);
		when(contactRoot.getOwner()).thenReturn(contact);
		when(contact.getEmail()).thenReturn(contactEmail);
		when(contactCacheService.findContactByEmail(contactEmail)).thenReturn(contact);
		when(contact.isOnline()).thenReturn(false);
		when(modelCollection.rawTracks()).thenReturn(Arrays.asList(new Track[] { track }));
		String trackId = "s125789";
		when(track.getHashcode()).thenReturn(trackId);
		List<String> trackIds = new ArrayList<String>();
		trackIds.add(trackId);
		when(mcManager.getAvailableTracks(trackIds)).thenReturn(trackIds);

		controller.moveModel(new ModelMoveAction(modelCollection, root));

		verify(moveDelegate).doMove(modelCollection, root);
		verify(controlEngine, never()).fireValueEvent(eq(Events.Alerts.CONFIRM_REQUEST_ALERT), any(McRequestAlert.class));
	}

	@Test
	public void shouldImportFromeRemoteLibraryOfAnOfflineContactAndRequestContent() throws Exception {
		when(controlEngine.get(Model.SELECTED_ROOT)).thenReturn(contactRoot);
		when(contactRoot.getOwner()).thenReturn(contact);
		when(contact.getEmail()).thenReturn(contactEmail);
		when(contactCacheService.findContactByEmail(contactEmail)).thenReturn(contact);
		when(contact.isOnline()).thenReturn(false);
		when(modelCollection.rawTracks()).thenReturn(Arrays.asList(new Track[] { track }));
		String trackId = "s125789";
		when(track.getHashcode()).thenReturn(trackId);
		List<String> trackIds = new ArrayList<String>();
		trackIds.add(trackId);
		when(mcManager.getAvailableTracks(trackIds)).thenReturn(Arrays.asList(new String[] {}));

		controller.moveModel(new ModelMoveAction(modelCollection, root));

		verify(moveDelegate, never()).doMove(modelCollection, root);
		verify(controlEngine).fireValueEvent(eq(Events.Alerts.CONFIRM_REQUEST_ALERT), any(McRequestAlert.class));
	}

	@Test
	public void shouldTryImportFromRemoteLibraryOfAnOfflineContactAndCancelRequestContent() throws Exception {
		when(controlEngine.get(Model.SELECTED_ROOT)).thenReturn(contactRoot);
		when(contactRoot.getOwner()).thenReturn(contact);
		when(contact.getEmail()).thenReturn(contactEmail);
		when(contactCacheService.findContactByEmail(contactEmail)).thenReturn(contact);
		when(contact.isOnline()).thenReturn(false);
		when(modelCollection.rawTracks()).thenReturn(Arrays.asList(new Track[] { track }));
		String trackId = "s125789";
		when(track.getHashcode()).thenReturn(trackId);
		List<String> trackIds = new ArrayList<String>();
		trackIds.add(trackId);
		when(mcManager.getAvailableTracks(trackIds)).thenReturn(Arrays.asList(new String[] {}));
		controller.moveModel(new ModelMoveAction(modelCollection, root));

		verify(moveDelegate, never()).doMove(modelCollection, root);
		verify(controlEngine).fireValueEvent(eq(Events.Alerts.CONFIRM_REQUEST_ALERT), any(McRequestAlert.class));
	}

}
