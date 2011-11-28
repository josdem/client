package com.all.client.controller;

import static com.all.shared.messages.MessEngineConstants.ADD_DOWNLOAD_TRACK;
import static com.all.shared.messages.MessEngineConstants.DELETE_DOWNLOAD_COLLECTION;
import static com.all.shared.messages.MessEngineConstants.DELETE_DOWNLOAD_TRACK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.all.action.ValueAction;
import com.all.appControl.control.ControlEngine;
import com.all.client.controller.DownloadsController.DownloadFactory;
import com.all.client.model.DecoratedSearchData;
import com.all.client.model.Download;
import com.all.client.model.LocalModelDao;
import com.all.client.peer.share.ShareService;
import com.all.client.services.ModelService;
import com.all.client.services.MusicEntityService;
import com.all.client.services.delegates.MoveDelegate;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.actions.MoveDownloadsAction;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.downloader.bean.DownloadStatus;
import com.all.downloader.download.DownloadCompleteEvent;
import com.all.downloader.download.DownloadStatusImpl;
import com.all.downloader.download.DownloadUpdateEvent;
import com.all.downloader.download.Downloader;
import com.all.event.EventType;
import com.all.event.ValueEvent;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.stats.usage.UserActions;

public class TestDownloadsController {

	@InjectMocks
	private DownloadsController downloadsController = new DownloadsController();
	@Mock
	private LocalModelDao dao;
	@Mock
	private MusicEntityService musicEntityService;
	@Mock
	private MoveDelegate moveDelegate;
	@Mock
	private Downloader downloader;
	@Mock
	private DownloadFactory downloadFactory;
	@Mock
	private Download download;
	@Mock
	private ShareService shareService;
	@Mock
	private ClientReporter reporter;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private File mockFile;
	@Mock
	private Root root;
	@Mock
	private ModelService modelService;
	@Captor
	private ArgumentCaptor<ValueEvent<Download>> downloadValueEventCaptor;
	@Captor
	private ArgumentCaptor<Map<String, Download>> downloadsMapCaptor;
	@Captor
	private ArgumentCaptor<List<Download>> downloadCollectionCaptor;

	private String downloadId = "1212";

	private String trackName = "Topogigio";

	private Track track = createTrack(downloadId, trackName, 1L);

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(controlEngine.get(Model.USER_ROOT)).thenReturn(root);
		when(track.getName()).thenReturn(trackName);
		when(track.getHashcode()).thenReturn(downloadId);
		when(track.getDownloadString()).thenReturn("magnet:?xt=urn:btih:HBALBWFEGMM3AFIZMROHOUS4EJOST4IX");
		when(download.getDownloadId()).thenReturn(downloadId);
		when(download.getStatus()).thenReturn(DownloadState.Queued);
		when(downloadFactory.createDownload(track)).thenReturn(download);
		when(download.getDownloadFile()).thenReturn(mockFile);
		when(mockFile.exists()).thenReturn(true);
		when(moveDelegate.relocate(any(Track.class))).thenAnswer(new Answer<Track>() {
			@Override
			public Track answer(InvocationOnMock invocation) throws Throwable {
				return (Track) invocation.getArguments()[0];
			}
		});

	}

	@Test
	public void shouldStart() throws Exception {
		List<Download> downloads = mockedDownloads(3);

		when(dao.findAll(Download.class)).thenReturn(downloads);

		downloadsController.start();

		for (Download download : downloads) {
			verify(download).setStatus(DownloadState.Queued);
			verify(dao).saveOrUpdate(download);
		}
	}

	@Test
	public void shouldStartAndAddDownloadsOnlyOnce() throws Exception {
		List<Download> downloads = mockedDownloads(3);
		String downloadId = "downloadId";

		when(dao.findAll(Download.class)).thenReturn(downloads);

		when(downloads.get(0).getDownloadId()).thenReturn(downloadId);
		when(downloads.get(0).getStatus()).thenReturn(DownloadState.Downloading, DownloadState.Queued);
		when(downloads.get(0).isStarted()).thenReturn(false, true);
		when(downloads.get(1).getStatus()).thenReturn(DownloadState.Complete);
		when(downloads.get(2).getStatus()).thenReturn(DownloadState.Complete);

		downloadsController.start();

		verify(downloads.get(0)).setStatus(DownloadState.Queued);
		verify(downloads.get(0)).setStarted(true);
		verify(downloader).download(downloadId);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldNotStartTwice() throws Exception {
		downloadsController.start();
		downloadsController.start();
	}

	@Test
	public void shouldStop() throws Exception {
		downloadsController.start();

		downloadsController.add(track);
		assertIsDownloading(downloadId);
		verify(dao).saveOrUpdate(download);

		downloadsController.stop();

		verify(dao, times(2)).saveOrUpdate(download);
		assertIsNotDownloading(downloadId);
	}

	@Test
	public void shouldAddATrack() throws Exception {
		downloadsController.add(track);

		assertIsDownloading(downloadId);
	}

	@Test
	public void shouldAddATrackThroughMessageMethod() throws Exception {
		downloadsController.add(new AllMessage<Track>(ADD_DOWNLOAD_TRACK, track));

		assertIsDownloading(downloadId);
	}

	private void assertIsDownloading(String downloadId) {
		Map<String, Download> downloads = getDownloadsFromModel();
		assertTrue(downloads.containsKey(downloadId));
	}

	private void assertIsNotDownloading(String downloadId) {
		Map<String, Download> downloads = getDownloadsFromModel();
		assertFalse(downloads.containsKey(downloadId));
	}

	@SuppressWarnings("unchecked")
	private Map<String, Download> getDownloadsFromModel() {
		verify(controlEngine, atLeastOnce()).set(eq(Model.ALL_DOWNLOADS), downloadsMapCaptor.capture(),
				any(EventType.class));
		LinkedList<Map<String, Download>> allValues = new LinkedList<Map<String, Download>>(
				downloadsMapCaptor.getAllValues());
		return allValues.getLast();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotAddAnExistantTrack() throws Exception {
		when(musicEntityService.isFileAvailable(track)).thenReturn(true);

		assertNull(downloadsController.add(track));

		verify(controlEngine, never()).set(eq(Model.ALL_DOWNLOADS), downloadsMapCaptor.capture(), any(EventType.class));
	}

	@Test
	public void shouldCleanAllCompletedDownloads() throws Exception {
		Download download1 = mock(Download.class);
		Download download2 = mock(Download.class);
		when(downloadFactory.createDownload(track)).thenReturn(download1, download2);
		when(download1.getDownloadId()).thenReturn("download1");
		when(download2.getDownloadId()).thenReturn("download2");
		downloadsController.add(track);
		downloadsController.add(track);
		when(download1.getStatus()).thenReturn(DownloadState.Complete);

		downloadsController.cleanUp();

		verify(download2).setPriority(0);
		verify(controlEngine).fireEvent(Events.Downloads.ALL_MODIFIED);
		verify(dao).delete(download1);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldAddDownload() throws Exception {
		when(downloadFactory.createDownload(track)).thenReturn(download);

		Download downloadInfo = downloadsController.add(track);

		assertIsDownloading(downloadId);
		verify(controlEngine).fireEvent(eq(Events.Downloads.ADDED), downloadValueEventCaptor.capture());
		verify(controlEngine).set(eq(Model.ALL_DOWNLOADS), downloadsMapCaptor.capture(), any(EventType.class));
		verify(dao).saveOrUpdate(download);
		verify(downloader).download(downloadId);
		verify(download).setStarted(true);

		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals(download, valueEvent.getValue());
		assertNotNull(downloadInfo);
		Map<String, Download> downloads = downloadsMapCaptor.getValue();
		assertTrue(downloads.containsKey(download.getDownloadId()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotAddADownloadTwice() throws Exception {
		when(downloadFactory.createDownload(any(Track.class))).thenReturn(download);

		Download info1 = downloadsController.add(createTrack("1212", "Topogigio", 1L));
		Download info2 = downloadsController.add(createTrack("1212", "Topogigio", 1L));

		assertIsDownloading(downloadId);
		verify(controlEngine).fireEvent(eq(Events.Downloads.ADDED), downloadValueEventCaptor.capture());
		verify(controlEngine).set(eq(Model.ALL_DOWNLOADS), downloadsMapCaptor.capture(), any(EventType.class));
		verify(dao, times(1)).saveOrUpdate(info1);
		assertEquals(info1, info2);
		Map<String, Download> downloads = downloadsMapCaptor.getValue();
		assertTrue(downloads.containsKey(download.getDownloadId()));
	}

	@Test
	public void shouldFireEventWhenADownloadInfoHasBeenModified() throws Exception {
		when(downloadFactory.createDownload(any(Track.class))).thenReturn(download);

		Download download = downloadsController.add(createTrack(downloadId, "Topogigio", 1L));

		DownloadStatus downloadStatus = mock(DownloadStatus.class);
		when(downloadStatus.getDownloadId()).thenReturn(downloadId);
		when(downloadStatus.getState()).thenReturn(DownloadState.Downloading);
		DownloadUpdateEvent downloadUpdateEvent = new DownloadUpdateEvent(this, downloadId, downloadStatus);

		reset(dao);
		downloadsController.onDownloadUpdated(downloadUpdateEvent);

		verify(dao).saveOrUpdate(download);
		verify(controlEngine).fireEvent(eq(Events.Downloads.UPDATED), downloadValueEventCaptor.capture());
		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals(download, valueEvent.getValue());
	}

	@Test
	public void shouldNotThrowUpdateEventIfTheDownloadInfoIsNotInsideTheDownloadsObject() throws Exception {
		DownloadStatus downloadStatus = mock(DownloadStatus.class);
		when(downloadStatus.getDownloadId()).thenReturn(downloadId);
		when(downloadStatus.getState()).thenReturn(DownloadState.Downloading);
		DownloadUpdateEvent downloadUpdateEvent = new DownloadUpdateEvent(this, downloadId, downloadStatus);

		downloadsController.onDownloadUpdated(downloadUpdateEvent);

		verify(dao, never()).saveOrUpdate(download);
		verify(controlEngine, never()).fireEvent(eq(Events.Downloads.UPDATED), downloadValueEventCaptor.capture());
	}

	@Test
	public void shouldRemoveDownloadAndReferenceAndFile() throws Exception {
		when(download.getStatus()).thenReturn(DownloadState.Downloading);
		when(download.getDownloadId()).thenReturn(downloadId);
		when(downloadFactory.createDownload(track)).thenReturn(download);

		downloadsController.add(track);
		downloadsController.deleteDownloadByTrackReference(new AllMessage<Track>(DELETE_DOWNLOAD_TRACK, track));

		assertIsNotDownloading(downloadId);
		verify(controlEngine).fireEvent(eq(Events.Downloads.REMOVED), downloadValueEventCaptor.capture());
		verify(mockFile).delete();
		verify(dao).delete(download);

		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals(download, valueEvent.getValue());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotLaunchEventOnRemoveNotExistentDownload() throws Exception {
		shouldAddDownload();

		Observer<ObservValue<Download>> l = mock(Observer.class);
		Download download2 = mock(Download.class);
		String myDownloadId = "12";
		when(download2.getDownloadId()).thenReturn(myDownloadId);
		when(download2.getTrackId()).thenReturn(myDownloadId);
		when(download2.getDisplayName()).thenReturn(myDownloadId);
		when(download2.getSize()).thenReturn(12L);

		downloadsController.deleteDownloadCollection(new AllMessage<List<Download>>(DELETE_DOWNLOAD_COLLECTION, Arrays
				.asList(new Download[] { download2 })));

		assertIsNotDownloading(myDownloadId);
		verify(l, never()).observe(isA(ObservValue.class));
		verify(controlEngine, never()).fireEvent(eq(Events.Downloads.REMOVED), downloadValueEventCaptor.capture());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldContainDownloadInfoInsideTheDownloadEvent() throws Exception {
		when(downloadFactory.createDownload(track)).thenReturn(download);
		when(download.getDisplayName()).thenReturn("Topogigio");

		downloadsController.add(track);

		verify(controlEngine).fireEvent(eq(Events.Downloads.ADDED), downloadValueEventCaptor.capture());
		verify(controlEngine).set(eq(Model.ALL_DOWNLOADS), downloadsMapCaptor.capture(), any(EventType.class));
		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals("Topogigio", valueEvent.getValue().getDisplayName());
		Map<String, Download> downloads = downloadsMapCaptor.getValue();
		assertTrue(downloads.containsKey(download.getDownloadId()));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldGetIterableForTheDownloads() throws Exception {
		Download download2 = mock(Download.class);
		Download download1 = mock(Download.class);
		when(downloadFactory.createDownload(any(Track.class))).thenReturn(download, download1, download2);
		when(download.getDownloadId()).thenReturn("1212");
		when(download1.getDownloadId()).thenReturn("12");
		when(download1.getDownloadId()).thenReturn("66");

		downloadsController.add(track);
		downloadsController.add(createTrack("12", "Metal", 500));
		downloadsController.add(createTrack("66", "Of Doom", 6));

		verify(controlEngine, atLeastOnce()).set(eq(Model.DOWNLOADS_SORTED_BY_PRIORITY),
				downloadCollectionCaptor.capture(), any(EventType.class));

		LinkedList<Collection<Download>> allValues = new LinkedList<Collection<Download>>(
				downloadCollectionCaptor.getAllValues());
		Iterable<Download> iterable = allValues.getLast();

		assertNotNull(iterable);
		Iterator<Download> iterator = iterable.iterator();
		for (int i = 0; i < 3; i++) {
			if (i < 3) {
				assertTrue(iterator.hasNext());
			} else {
				assertFalse(iterator.hasNext());
			}
			assertNotNull(iterator.next());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldSetOrderToDownloadInfosDependingOnTheAddOrder() throws Exception {
		Download download1 = mock(Download.class);
		Download download2 = mock(Download.class);
		when(downloadFactory.createDownload(any(Track.class))).thenReturn(download, download1, download2);
		when(download.getDownloadId()).thenReturn(downloadId);
		when(download1.getDownloadId()).thenReturn("downloadId1");
		when(download2.getDownloadId()).thenReturn("downloadId2");
		downloadsController.add(track);
		downloadsController.add(createTrack("12", "Metal", 500));
		downloadsController.add(createTrack("66", "Of Doom", 6));

		verify(controlEngine, atLeastOnce()).set(eq(Model.DOWNLOADS_SORTED_BY_PRIORITY),
				downloadCollectionCaptor.capture(), any(EventType.class));

		LinkedList<Collection<Download>> allValues = new LinkedList<Collection<Download>>(
				downloadCollectionCaptor.getAllValues());
		Iterator<Download> iterator = allValues.getLast().iterator();

		int i;
		for (i = 0; iterator.hasNext(); i++) {
			Download next = iterator.next();
			verify(next).setPriority(i);
		}
		assertEquals(3, i);

		downloadsController.deleteDownloadByTrackReference(new AllMessage<Track>(DELETE_DOWNLOAD_TRACK, track));

		verify(controlEngine, atLeastOnce()).set(eq(Model.DOWNLOADS_SORTED_BY_PRIORITY),
				downloadCollectionCaptor.capture(), any(EventType.class));

		allValues = new LinkedList<Collection<Download>>(downloadCollectionCaptor.getAllValues());
		iterator = allValues.getLast().iterator();

		for (i = 0; iterator.hasNext(); i++) {
			Download next = iterator.next();
			verify(next).setPriority(i);
		}
		assertEquals(2, i);
	}

	private Track createTrack(String hashcode, String name, long size) {
		Track track = mock(Track.class);
		when(track.getName()).thenReturn(name);
		when(track.getHashcode()).thenReturn(hashcode);
		when(track.getDownloadString()).thenReturn("magnet:?xt=urn:btih:HBALBWFEGMM3AFIZMROHOUS4EJOST4IX");
		return track;
	}

	@Test
	public void shouldDeleteCompletelyDownloadsAndTrackReferences() throws Exception {
		when(downloadFactory.createDownload(track)).thenReturn(download);
		downloadsController.add(track);

		downloadsController.deleteDownloadCollection(new AllMessage<List<Download>>(DELETE_DOWNLOAD_COLLECTION, Arrays
				.asList(new Download[] { download })));

		verify(mockFile).delete();
		verify(dao).delete(download);
		assertIsNotDownloading(downloadId);
	}

	@Test
	public void shouldDeleteCompletelyDownloadsAndTrackReferences2() throws Exception {
		when(downloadFactory.createDownload(track)).thenReturn(download);
		downloadsController.add(track);

		downloadsController.deleteDownloadCollection(new ValueAction<List<Download>>(Arrays
				.asList(new Download[] { download })));

		verify(mockFile).delete();
		verify(dao).delete(download);
		assertIsNotDownloading(downloadId);
	}

	@Test
	public void shouldAddListOfTracks() throws Exception {
		String trackId1 = "trackId1";
		Track track1 = createTrack(trackId1, "track1", 23);
		String trackId2 = "trackId2";
		Track track2 = createTrack(trackId2, "track2", 33);
		Track track3 = createTrack("trackId3", "track3", 43);

		List<Track> tracksList = new ArrayList<Track>();
		tracksList.add(track1);
		tracksList.add(track2);
		tracksList.add(track3);

		List<Track> tracksWithFile = new ArrayList<Track>();
		tracksWithFile.add(track3);
		Download download1 = mock(Download.class);
		Download download2 = mock(Download.class);
		Download download3 = mock(Download.class);
		when(downloadFactory.createDownload(any(Track.class))).thenReturn(download1, download2, download3);

		when(download1.getDownloadId()).thenReturn(trackId1);
		when(download2.getDownloadId()).thenReturn(trackId2);
		when(download3.getDownloadId()).thenReturn("trackId3");
		when(download1.getStatus()).thenReturn(DownloadState.Queued);
		when(download2.getStatus()).thenReturn(DownloadState.Queued);

		when(musicEntityService.isFileAvailable(any(Track.class))).thenReturn(false);

		downloadsController.add(new ModelCollection(tracksList));

		assertIsDownloading(trackId1);
		assertIsDownloading(trackId2);
	}

	@Test
	public void shouldRemoveADownloadFromATrack() throws Exception {
		when(downloadFactory.createDownload(track)).thenReturn(download);

		downloadsController.add(track);

		assertIsDownloading(downloadId);

		downloadsController.deleteDownloadByTrackReference(new AllMessage<Track>(DELETE_DOWNLOAD_TRACK, track));

		assertIsNotDownloading(downloadId);
		verify(controlEngine).fireEvent(eq(Events.Downloads.REMOVED), downloadValueEventCaptor.capture());
		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals(download, valueEvent.getValue());
	}

	private List<Download> mockedDownloads(int nrDownloads) {
		List<Download> downloads = new ArrayList<Download>();
		for (int i = 0; i < nrDownloads; i++) {
			downloads.add(createMockDownloadWithDownloadingStatus());
		}
		return downloads;
	}

	private Download createMockDownloadWithDownloadingStatus() {
		Download download = mock(Download.class);
		when(download.getStatus()).thenReturn(DownloadState.Downloading);
		return download;
	}

	@Test
	public void shouldPauseSelectedDownloads() throws Exception {
		List<Download> downloads = new ArrayList<Download>();
		downloads.add(download);
		downloads.add(download);
		when(download.getStatus()).thenReturn(DownloadState.Downloading);

		ValueAction<Collection<Download>> downloadsValue = new ValueAction<Collection<Download>>(downloads);
		downloadsController.pause(downloadsValue);

		verify(downloader, times(2)).pause(anyString());
	}

	@Test
	public void shouldResumeDownload() throws Exception {
		List<Download> downloads = new ArrayList<Download>();
		downloads.add(download);
		when(download.getStatus()).thenReturn(DownloadState.Paused);

		downloadsController.add(track);

		ValueAction<Collection<Download>> downloadsValue = new ValueAction<Collection<Download>>(downloads);
		downloadsController.resume(downloadsValue);

		verify(downloader).resume(anyString());
	}

	@Test
	public void shouldQueueResumedDownloadsIfCannotResumeImmediately() throws Exception {
		shouldAddListOfTracks();
		when(download.getStatus()).thenReturn(DownloadState.Queued);
		when(downloadFactory.createDownload(track)).thenReturn(download);
		downloadsController.add(track);
		List<Download> downloads = new ArrayList<Download>();
		downloads.add(download);
		when(download.getStatus()).thenReturn(DownloadState.Paused);

		ValueAction<Collection<Download>> downloadsValue = new ValueAction<Collection<Download>>(downloads);
		downloadsController.resume(downloadsValue);

		verify(download).setStatus(DownloadState.Queued);
		verify(dao, times(2)).saveOrUpdate(download);
	}

	@Test
	public void shouldResumeDownloadWithStatusError() throws Exception {
		addAndSetDownloadTo(DownloadState.Error);

		List<Download> downloads = createSingleElementDownloadList();
		when(download.getStatus()).thenReturn(DownloadState.Error, DownloadState.Queued);

		ValueAction<Collection<Download>> downloadsValue = new ValueAction<Collection<Download>>(downloads);
		downloadsController.resume(downloadsValue);

		verify(download).setStatus(DownloadState.Queued);
		verify(dao).saveOrUpdate(download);
		verify(downloader).download(downloadId);
		verify(download, times(3)).setStarted(true);
	}

	@Test
	public void shouldResumeDownloadWithStatusMorSourcesNeeded() throws Exception {
		addAndSetDownloadTo(DownloadState.MoreSourcesNeeded);

		List<Download> downloads = createSingleElementDownloadList();
		when(download.getStatus()).thenReturn(DownloadState.MoreSourcesNeeded, DownloadState.Queued);

		ValueAction<Collection<Download>> downloadsValue = new ValueAction<Collection<Download>>(downloads);
		downloadsController.resume(downloadsValue);

		verify(download).setStatus(DownloadState.Queued);
		verify(dao).saveOrUpdate(download);
		verify(downloader).download(downloadId);
		verify(download, times(3)).setStarted(true);
	}

	private void addAndSetDownloadTo(DownloadState state) {
		downloadsController.add(track);

		DownloadStatusImpl status = new DownloadStatusImpl(downloadId);
		status.setState(state);
		status.setDownloadRate(1);
		status.setProgress(80);
		status.setRemainingSeconds(20);

		DownloadUpdateEvent event = new DownloadUpdateEvent(this, downloadId, status);

		when(dao.findByHashcode(downloadId)).thenReturn(track);

		downloadsController.onDownloadUpdated(event);

		reset(dao);
		reset(downloader);
	}

	private List<Download> createSingleElementDownloadList() {
		List<Download> downloads = new ArrayList<Download>();
		downloads.add(download);
		return downloads;
	}

	@Test
	public void shouldCreateATrackIfP2PSearchResultNotExist() throws Exception {
		// int size = 1234;
		// String filename = "filename";
		// String ext = ".ext";
		// DecoratedSearchData search = new DecoratedSearchData(urnSha, 0, filename
		// + ext, size, "type", 1, 128);
		// search.setUri(allLink);

		String urnSha = "TTYJZFSAJF5EXZYII7CKPISWSAWNCCHI";
		String allLink = "allLink:urnsha1=TTYJZFSAJF5EXZYII7CKPISWSAWNCCHI";
		DecoratedSearchData search = mock(DecoratedSearchData.class);
		when(search.getFileHash()).thenReturn(urnSha);
		when(search.getAllLink()).thenReturn(allLink);
		when(search.toTrack()).thenReturn(track);
		when(moveDelegate.relocate(eq(track))).thenReturn(track);

		when(downloadFactory.createDownload(any(Track.class))).thenReturn(download);

		downloadsController.addSearchData(new ValueAction<DecoratedSearchData>(search));

		verify(downloadFactory).createDownload(track);
		verify(reporter).logUserAction(UserActions.Downloads.DOWNLOAD_SEARCH_RESULT);
	}

	@Test
	public void shouldMoveDownloadsToTheTopOfTheList() throws Exception {
		createAndAddDownloads(6);
		List<Download> downloads = assertOrder("track0", "track1", "track2", "track3", "track4", "track5");

		List<Download> movedDownloads = new ArrayList<Download>();
		movedDownloads.add(downloads.get(2));
		movedDownloads.add(downloads.get(4));

		downloadsController.moveDownloads(new MoveDownloadsAction(movedDownloads, 0));

		downloads = assertOrder("track2", "track4", "track0", "track1", "track3", "track5");

		movedDownloads.clear();
		movedDownloads.add(downloads.get(2));
		movedDownloads.add(downloads.get(3));

		downloadsController.moveDownloads(new MoveDownloadsAction(movedDownloads, 5));

		downloads = assertOrder("track2", "track4", "track3", "track5", "track0", "track1");

		movedDownloads.clear();
		movedDownloads.add(downloads.get(0));

		downloadsController.moveDownloads(new MoveDownloadsAction(movedDownloads, 4));

		downloads = assertOrder("track4", "track3", "track5", "track0", "track2", "track1");

		movedDownloads.clear();
		movedDownloads.add(downloads.get(2));

		downloadsController.moveDownloads(new MoveDownloadsAction(movedDownloads, 0));

		downloads = assertOrder("track5", "track4", "track3", "track0", "track2", "track1");

		movedDownloads.clear();
		movedDownloads.add(downloads.get(3));

		downloadsController.moveDownloads(new MoveDownloadsAction(movedDownloads, 5));

		assertOrder("track5", "track4", "track3", "track2", "track1", "track0");
	}

	@SuppressWarnings("unchecked")
	private List<Download> assertOrder(String... expectedOrder) {
		verify(controlEngine, atLeastOnce()).set(eq(Model.DOWNLOADS_SORTED_BY_PRIORITY),
				downloadCollectionCaptor.capture(), any(EventType.class));

		LinkedList<List<Download>> allValues = new LinkedList<List<Download>>(downloadCollectionCaptor.getAllValues());
		List<Download> allDownloads = allValues.getLast();

		assertEquals(expectedOrder.length, allDownloads.size());
		Iterator<Download> iterator = allDownloads.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			assertEquals(expectedOrder[i], iterator.next().getDownloadId());
			i++;
		}
		return allDownloads;
	}

	private void createAndAddDownloads(int total) {
		for (int i = 0; i < total; i++) {
			String trackId = "track" + i;
			Track track = createTrack(trackId, trackId, 0);
			Download download = mock(Download.class);
			when(download.getDownloadId()).thenReturn(trackId);
			when(download.toString()).thenReturn(trackId);
			when(downloadFactory.createDownload(track)).thenReturn(download);
			downloadsController.add(track);
		}
	}

	@Test
	public void shouldProcessUpdateEvent() throws Exception {
		when(download.getStatus()).thenReturn(DownloadState.Queued);
		downloadsController.add(track);
		assertIsDownloading(downloadId);
		DownloadStatusImpl status = new DownloadStatusImpl(downloadId);
		status.setState(DownloadState.Downloading);
		status.setDownloadRate(1);
		status.setProgress(80);
		status.setRemainingSeconds(20);

		DownloadUpdateEvent event = new DownloadUpdateEvent(this, downloadId, status);
		when(dao.findByHashcode(downloadId)).thenReturn(track);

		downloadsController.onDownloadUpdated(event);

		verify(download).setStatus(DownloadState.Downloading);
		verify(download).setProgress(status.getProgress());
		verify(download).setRemainingSeconds(status.getRemainingSeconds());
		verify(download).setRate(status.getDownloadRate());
		verify(dao, atLeastOnce()).saveOrUpdate(download);
		verify(controlEngine).fireEvent(eq(Events.Downloads.UPDATED), downloadValueEventCaptor.capture());
		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals(download, valueEvent.getValue());
	}

	@Test
	public void shouldProcessCompleteEvent() throws Exception {
		when(download.getStatus()).thenReturn(DownloadState.Queued);

		downloadsController.add(track);

		assertIsDownloading(downloadId);
		File file = mock(File.class);
		DownloadCompleteEvent event = new DownloadCompleteEvent(this, downloadId, file);
		when(modelService.updateDownloadedTrack(downloadId, file)).thenReturn(track);
		String filepath = "filepath";
		when(file.getAbsolutePath()).thenReturn(filepath);
		String filename = "filename";
		when(file.getName()).thenReturn(filename);

		downloadsController.onDownloadCompleted(event);

		verify(download).complete();
		verify(dao).update(download);
		verify(download).updateTrackInfo(track);
		verify(shareService).run();
		verify(controlEngine).fireEvent(eq(Events.Downloads.COMPLETED), downloadValueEventCaptor.capture());

		ValueEvent<Download> valueEvent = downloadValueEventCaptor.getValue();
		assertEquals(download, valueEvent.getValue());
	}

}