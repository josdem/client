package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.all.client.notifiers.SyncEventInterceptor;
import com.all.client.sync.SyncDaoInterceptor;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.model.SyncEventEntity.SyncOperation;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestSyncEventNotifier {

	@UnderTest
	private SyncEventInterceptor syncNotifier;
	@Mock
	private LocalModelDao modelDao;
	private SyncDaoInterceptor daoNotifier = new SyncDaoInterceptor();
	private LocalPlaylist playlist;
	private PlaylistTrack playlisttrack;
	private LocalFolder folder;
	private LocalTrack track;

	@Before
	public void setup() {
		syncNotifier.setDaoNotifier(daoNotifier);
		folder = new LocalFolder("folder");
		playlist = createPlaylist();
		playlisttrack = createPlaylistTrack();
		track = createTrack();
	}

	@Test
	public void shouldCreateAddGenericEvent() throws Exception {
		ArgumentCaptor<SyncEventEntity> event = ArgumentCaptor.forClass(SyncEventEntity.class);
		daoNotifier.onSave(track, null, null, null, null);
		genericAssertion(event, SyncOperation.SAVE);
	}

	@Test
	public void shouldCreateAddPlaylistEvent() throws Exception {
		ArgumentCaptor<SyncEventEntity> event = ArgumentCaptor.forClass(SyncEventEntity.class);
		daoNotifier.onSave(playlist, null, null, null, null);
		HashMap<String, Object> map = genericAssertion(event, SyncOperation.SAVE);
		assertEquals(folder.getHashcode(), map.get("parentFolder"));
	}

	@Test
	public void shouldCreateAddPlaylistTrackEvent() throws Exception {
		ArgumentCaptor<SyncEventEntity> event = ArgumentCaptor.forClass(SyncEventEntity.class);
		daoNotifier.onSave(playlisttrack, null, null, null, null);
		HashMap<String, Object> map = genericAssertion(event, SyncOperation.SAVE);
		assertEquals(playlisttrack.getPlaylist().getHashcode(), map.get("playlist"));
		assertEquals(playlisttrack.getTrack().getHashcode(), map.get("track"));
		assertEquals(playlisttrack.getId(), map.get("id"));
	}

	@Test
	public void shouldCreateDeleteEvent() throws Exception {
		ArgumentCaptor<SyncEventEntity> event = ArgumentCaptor.forClass(SyncEventEntity.class);
		daoNotifier.onDelete(folder, null, null, null, null);
		genericAssertion(event, SyncOperation.DELETE);
	}

	@Test
	public void shouldCreateUpdateEvent() throws Exception {
		ArgumentCaptor<SyncEventEntity> event = ArgumentCaptor.forClass(SyncEventEntity.class);
		daoNotifier.onFlushDirty(folder, null, null, null, null, null);
		genericAssertion(event, SyncOperation.UPDATE);
	}

	private HashMap<String, Object> genericAssertion(ArgumentCaptor<SyncEventEntity> event, SyncOperation syncOperation) {
		verify(modelDao).save((event.capture()));
		assertTrue(event.getValue() instanceof SyncEventEntity);
		SyncEventEntity see = event.getValue();
		assertTrue(see.getEntity() instanceof Map<?, ?>);
		assertTrue(see.getOperation() == syncOperation);
		return see.getEntity();
	}

	private LocalPlaylist createPlaylist() {
		LocalTrack trackA = createTrack();
		trackA.setName("track A");
		LocalTrack trackB = createTrack();
		trackB.setName("track B");
		LocalPlaylist playlist = new LocalPlaylist();
		PlaylistTrack playlistTrackA = new PlaylistTrack(trackA, playlist);
		PlaylistTrack playlistTrackB = new PlaylistTrack(trackB, playlist);
		List<PlaylistTrack> playlistTracks = new ArrayList<PlaylistTrack>();
		playlistTracks.add(playlistTrackA);
		playlistTracks.add(playlistTrackB);
		playlist.setPlaylistTracks(playlistTracks);
		playlist.setHashcode("9876543210");
		playlist.setLastPlayed(new Date());
		playlist.setModifiedDate(new Date());
		playlist.setCreationDate(new Date());
		playlist.setLastPlayed(new Date());
		playlist.setName("playlist name");
		playlist.setOwner("somebody@all.com");
		playlist.setParentFolder(folder);
		playlist.setSmartPlaylist(true);
		return playlist;
	}

	@SuppressWarnings("deprecation")
	private LocalTrack createTrack() {
		LocalTrack track = new LocalTrack();
		track.setAlbum("some album");
		track.setArtist("some artist");
		track.setBitRate("a vbr");
		track.setDateAdded(new Date());
		track.setDownloadString("some download string");
		track.setDuration(1000);
		track.setEnabled(true);
		track.setFileFormat("mp3");
		track.setGenre("tropical");
		track.setHashcode("1234567890");
		track.setLastPlayed(new Date());
		track.setLastSkipped(new Date());
		track.setName("de quen chon");
		track.setFileName("track2.mp2");
		track.setPlaycount(150);
		track.setRating(66);
		track.setSampleRate("mmm dunno");
		track.setSize(1024 * 4);
		track.setSkips(100);
		track.setTrackNumber("1");
		track.setYear("1979");
		return track;
	}

	private PlaylistTrack createPlaylistTrack() {
		LocalPlaylist createPlaylist = createPlaylist();
		LocalTrack createTrack = createTrack();
		PlaylistTrack playlistTrack = new PlaylistTrack(createTrack, createPlaylist);
		playlistTrack.setTrackPosition(5);
		return playlistTrack;
	}

}