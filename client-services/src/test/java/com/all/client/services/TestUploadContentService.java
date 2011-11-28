package com.all.client.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.core.events.UploadContentDoneEvent;
import com.all.core.events.UploadContentEvent;
import com.all.core.events.UploadContentListener;
import com.all.core.events.UploadContentStartedEvent;
import com.all.core.events.UploadContentUpdateEvent;
import com.all.mc.manager.McManager;
import com.all.mc.manager.uploads.UploadStatus;
import com.all.mc.manager.uploads.UploaderListener;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Track;

public class TestUploadContentService {

	@InjectMocks
	private UploadContentService service = new UploadContentService();
	@Mock
	private McManager mcManager;
	@Mock
	private UploadContentListener listener;
	@Mock
	private MusicEntityService musicEntityService;
	// Aux mocks
	@Mock
	private ModelCollection model;
	@Mock
	private File file;
	@Mock
	private Track track;
	@Captor
	private ArgumentCaptor<UploaderListener> listenerCaptor;
	@Captor
	private ArgumentCaptor<UploadContentEvent> eventCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		service.addUploadContentListener(listener);
		when(musicEntityService.getFile(any(Track.class))).thenReturn(file);
	}

	@After
	public void teardown() {
		service.removeUploadContentListener(listener);
	}

	@Test
	public void shouldGetUploadRate() throws Exception {
		service.getUploadRate();
		verify(mcManager).getUploadRate();
	}

	@Test
	public void shouldStartContentUploadAndNotify() throws Exception {
		when(model.rawTracks()).thenReturn(new ArrayList<Track>(0));
		long uploadId = service.submit(model);

		verify(mcManager, timeout(1000)).addUploaderListener(listenerCaptor.capture());
		verify(listener, timeout(1000)).onContentUploadStarted((UploadContentStartedEvent) eventCaptor.capture());

		assertEquals(uploadId, eventCaptor.getValue().getUploadId());
	}

	@Test
	public void shouldUploadTrackAndNotifyWhenDone() throws Exception {
		String trackId = "trackA";
		when(model.rawTracks()).thenReturn(Arrays.asList(new Track[] { track }));

		long uploadId = service.submit(model);

		verify(mcManager, timeout(1000)).addUploaderListener(listenerCaptor.capture());
		verify(listener, timeout(1000)).onContentUploadStarted((UploadContentStartedEvent) eventCaptor.capture());

		UploaderListener uploaderListener = listenerCaptor.getValue();
		int progress = 100;
		int rate = 1024;
		UploadStatus.UploadState state = UploadStatus.UploadState.COMPLETED;
		UploadStatus uploadStatus = mockStatus(trackId, progress, rate, state);
		uploaderListener.onUploadUpdated(uploadStatus);
		verify(listener, timeout(1000)).onContentUploadDone((UploadContentDoneEvent) eventCaptor.capture());
		UploadContentDoneEvent doneEvent = (UploadContentDoneEvent) eventCaptor.getValue();
		assertEquals(uploadId, doneEvent.getUploadId());
		assertFalse(doneEvent.hasError());
		assertFalse(doneEvent.isCanceled());
	}

	@Test
	public void shouldUploadTrackAndContinueWhenError() throws Exception {
		String trackId = "trackA";
		long trackSize = 1024L;
		Track trackA = mockTrack(trackId, trackSize);
		when(model.rawTracks()).thenReturn(Arrays.asList(new Track[] { trackA }));

		long uploadId = service.submit(model);

		verify(mcManager, timeout(1000)).addUploaderListener(listenerCaptor.capture());
		verify(listener, timeout(1000)).onContentUploadStarted((UploadContentStartedEvent) eventCaptor.capture());

		UploaderListener uploaderListener = listenerCaptor.getValue();
		int progress = 0;
		int rate = 0;
		UploadStatus.UploadState state = UploadStatus.UploadState.ERROR;
		UploadStatus uploadStatus = mockStatus(trackId, progress, rate, state);
		uploaderListener.onUploadUpdated(uploadStatus);
		verify(listener, timeout(1000)).onContentUploadDone((UploadContentDoneEvent) eventCaptor.capture());
		UploadContentDoneEvent doneEvent = (UploadContentDoneEvent) eventCaptor.getValue();
		assertEquals(uploadId, doneEvent.getUploadId());
		assertTrue(doneEvent.hasError());
	}

	@Test
	public void shouldStartUploadingTrackAndCancel() throws Exception {
		String trackId = "trackA";
		long trackSize = 1024L;
		Track trackA = mockTrack(trackId, trackSize);
		when(model.rawTracks()).thenReturn(Arrays.asList(new Track[] { trackA }));

		long uploadId = service.submit(model);

		verify(mcManager, timeout(1000)).addUploaderListener(listenerCaptor.capture());
		verify(listener, timeout(1000)).onContentUploadStarted((UploadContentStartedEvent) eventCaptor.capture());

		service.cancel(uploadId);
		verify(listener, timeout(1000)).onContentUploadDone((UploadContentDoneEvent) eventCaptor.capture());
		UploadContentDoneEvent doneEvent = (UploadContentDoneEvent) eventCaptor.getValue();
		assertEquals(uploadId, doneEvent.getUploadId());
		assertFalse(doneEvent.hasError());
		assertTrue(doneEvent.isCanceled());
	}

	@Test
	public void shouldUploadTrackAndNotifyWhenProgress() throws Exception {
		String trackAId = "trackA";
		String trackBId = "trackB";
		long trackSize = 1024L;
		Track trackA = mockTrack(trackAId, trackSize);
		Track trackB = mockTrack(trackBId, trackSize);
		List<Track> tracks = Arrays.asList(new Track[] { trackA, trackB });
		when(model.rawTracks()).thenReturn(tracks);
		when(model.size()).thenReturn(trackSize * 2);
		long uploadId = service.submit(model);

		verify(mcManager, timeout(1000)).addUploaderListener(listenerCaptor.capture());
		verify(listener, timeout(1000)).onContentUploadStarted((UploadContentStartedEvent) eventCaptor.capture());

		UploaderListener uploaderListener = listenerCaptor.getValue();
		int progress = 50;
		int rate = 10;
		UploadStatus.UploadState state = UploadStatus.UploadState.UPLOADING;
		UploadStatus uploadStatus = mockStatus(trackAId, progress, rate, state);
		uploaderListener.onUploadUpdated(uploadStatus);
		verify(listener, timeout(1000)).onContentUploadUpdated((UploadContentUpdateEvent) eventCaptor.capture());
		UploadContentUpdateEvent updateEvent = (UploadContentUpdateEvent) eventCaptor.getValue();
		assertEquals(uploadId, updateEvent.getUploadId());
		assertEquals(progress / tracks.size(), updateEvent.getProgress());
		assertEquals(trackSize * tracks.size(), updateEvent.getTotalSize());
		assertEquals(trackSize + trackSize / (100 / progress), updateEvent.getRemainingBytes());
		assertEquals(tracks.size(), updateEvent.getTotalTracks());
		assertEquals(uploadId, updateEvent.getUploadId());
		assertEquals(updateEvent.getRemainingBytes() / rate, updateEvent.getRemainingSeconds());
		assertEquals(rate, updateEvent.getUploadRate());
	}

	private Track mockTrack(String id, long size) {
		Track track = mock(Track.class);
		when(track.getHashcode()).thenReturn(id);
		when(track.getSize()).thenReturn(size);
		return track;
	}

	private UploadStatus mockStatus(String trackId, int progress, int rate, UploadStatus.UploadState state) {
		UploadStatus status = mock(UploadStatus.class);
		when(status.getTrackId()).thenReturn(trackId);
		when(status.getProgress()).thenReturn(progress);
		when(status.getUploadRate()).thenReturn(rate);
		when(status.getState()).thenReturn(state);
		return status;
	}

}
