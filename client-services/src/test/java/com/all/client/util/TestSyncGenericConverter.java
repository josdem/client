package com.all.client.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.all.client.model.ContactFolder;
import com.all.client.model.ContactUserFolder;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalTrack;
import com.all.client.model.PlaylistTrack;
import com.all.shared.json.JsonConverter;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;
import com.all.shared.model.Folder;
import com.all.shared.model.Gender;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;
import com.all.shared.model.RemoteTrack;
import com.all.shared.model.SyncEventEntity.SyncOperation;
import com.all.shared.model.Track;
import com.all.shared.model.User;
import com.all.shared.sync.SyncAble;
import com.all.shared.sync.SyncGenericConverter;

@SuppressWarnings("unchecked")
public class TestSyncGenericConverter {

	private Log log = LogFactory.getLog(this.getClass());

	@Test
	public void shouldCreateAMapFromLocalTrackWithSaveAction() throws Exception {
		LocalTrack track = createTrack();
		Map<String, Object> map = getGenericMap(track, SyncOperation.SAVE);
		assertEquals(25, map.values().size());
		assertEquals("LocalTrack", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("dateAdded") instanceof Long);
		assertTrue(map.get("lastPlayed") instanceof Long);
		assertTrue(map.get("lastSkipped") instanceof Long);
		assertTrue(map.get("duration") instanceof Integer);
		assertTrue(map.get("enabled") instanceof Boolean);
		assertTrue(map.get("newContent") instanceof Boolean);

		map = JsonConverter.toBean(JsonConverter.toJson(map), HashMap.class);

		SyncAble track2 = SyncGenericConverter.toBean(map, LocalTrack.class);
		compareTrack(track, (LocalTrack) track2);
		assertEquals(track.isNewContent(), ((Track) track2).isNewContent());

		RemoteTrack track3 = new RemoteTrack();
		track3 = SyncGenericConverter.toBean(map, RemoteTrack.class);
		compareTrack(track, track3);
		compareTrack((LocalTrack) track2, track3);
	}

	@Test
	public void shouldCreateAMapFromLocalTrackWithUpdateAction() throws Exception {
		LocalTrack track = createTrack();
		Map<String, Object> map = getGenericMap(track, SyncOperation.UPDATE);
		assertEquals(13, map.values().size());
		assertEquals("LocalTrack", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("downloadString") instanceof String);
		assertTrue(map.get("lastPlayed") instanceof Long);
		assertTrue(map.get("lastSkipped") instanceof Long);
		assertTrue(map.get("skips") instanceof Integer);
		assertTrue(map.get("rating") instanceof Integer);
		assertTrue(map.get("enabled") instanceof Boolean);
		assertTrue(map.get("fileName") instanceof String);
		assertTrue(map.get("newContent") instanceof Boolean);

		map = JsonConverter.toBean(JsonConverter.toJson(map), HashMap.class);

		LocalTrack track2 = SyncGenericConverter.toBean(map, LocalTrack.class);

		assertEquals(track2.getDownloadString(), track.getDownloadString());
		assertEquals(track2.getLastPlayed(), track.getLastPlayed());
		assertEquals(track2.getPlaycount(), track.getPlaycount());
		assertEquals(track2.getFileName(), track.getFileName());
		assertEquals(track2.isNewContent(), track.isNewContent());
	}

	@Test
	public void shouldCreateAMapFromLocalTrackWithDeleteAction() throws Exception {
		LocalTrack track = createTrack();
		Map<String, Object> map = getGenericMap(track, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldCreateAMapFromLocalPlaylistWithSaveAction() throws Exception {
		LocalPlaylist playlist = createPlaylist();
		Map<String, Object> map = getGenericMap(playlist, SyncOperation.SAVE);

		assertEquals(9, map.values().size());
		assertEquals("LocalPlaylist", map.get(SyncGenericConverter.ENTITY));

		assertTrue(map.get("hashcode") instanceof String);
		assertTrue(map.get("modifiedDate") instanceof Long);
		assertEquals(map.get("parentFolder"), "1234567890");
		assertFalse(map.containsKey("playlistTracks"));
		SyncAble playlist2 = SyncGenericConverter.toBean(map, LocalPlaylist.class);
		comparePlaylist(playlist, (LocalPlaylist) playlist2, false);

	}

	@Test
	public void shouldCreateAMapFromLocalPlaylistWithUpdateAction() throws Exception {
		LocalPlaylist playlist = createPlaylist();
		Map<String, Object> map = getGenericMap(playlist, SyncOperation.UPDATE);

		assertEquals(7, map.values().size());
		assertEquals("LocalPlaylist", map.get(SyncGenericConverter.ENTITY));

		assertTrue(map.get("hashcode") instanceof String);
		assertTrue(map.get("modifiedDate") instanceof Long);
		assertEquals(map.get("parentFolder"), "1234567890");

		LocalPlaylist playlist2 = SyncGenericConverter.toBean(map, LocalPlaylist.class);

		assertEquals(playlist.getName(), playlist2.getName());
		assertEquals(playlist.getModifiedDate(), playlist2.getModifiedDate());
		assertEquals(playlist.getLastPlayed(), playlist2.getLastPlayed());
	}

	@Test
	public void shouldCreateAMapFromLocalPlaylistWithDeleteAction() throws Exception {
		LocalPlaylist playlist = createPlaylist();
		Map<String, Object> map = getGenericMap(playlist, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldCreateAMapFromPlaylistTrackWithSaveAction() throws Exception {
		PlaylistTrack playlistTrack = createPlaylistTrack();
		Map<String, Object> map = getGenericMap(playlistTrack, SyncOperation.SAVE);

		assertTrue(map.values().size() == 6);
		assertEquals("PlaylistTrack", map.get(SyncGenericConverter.ENTITY));

		assertTrue(map.get("id") instanceof String);
		assertEquals("9876543210", map.get("playlist"));
		assertEquals("1234567890", map.get("track"));
		assertTrue(map.get("trackPosition") instanceof Integer);

		PlaylistTrack plt = SyncGenericConverter.toBean(map, PlaylistTrack.class);

		assertEquals(plt.getId(), playlistTrack.getId());
		assertEquals(plt.getTrackPosition(), playlistTrack.getTrackPosition());
	}

	@Test
	public void shouldCreateAMapFromPlaylistTrackWithUpdateAction() throws Exception {
		PlaylistTrack playlistTrack = createPlaylistTrack();
		Map<String, Object> map = getGenericMap(playlistTrack, SyncOperation.UPDATE);

		assertTrue(map.values().size() == 6);
		assertEquals("PlaylistTrack", map.get(SyncGenericConverter.ENTITY));

		assertTrue(map.get("id") instanceof String);
		assertEquals("9876543210", map.get("playlist"));
		assertEquals("1234567890", map.get("track"));
		assertTrue(map.get("trackPosition") instanceof Integer);

		PlaylistTrack plt = SyncGenericConverter.toBean(map, PlaylistTrack.class);

		assertEquals(plt.getId(), playlistTrack.getId());
		assertEquals(plt.getTrackPosition(), playlistTrack.getTrackPosition());
	}

	@Test
	public void shouldCreateAMapFromPlaylistTrackWithDeleteAction() throws Exception {
		PlaylistTrack playlistTrack = createPlaylistTrack();
		Map<String, Object> map = getGenericMap(playlistTrack, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldCreateAMapFromLocalFolderWithSaveAction() throws Exception {
		LocalFolder folder = createFolder();
		Map<String, Object> map = getGenericMap(folder, SyncOperation.SAVE);
		assertTrue(map.values().size() == 6);
		assertEquals("LocalFolder", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("hashcode") instanceof String);
		assertTrue(map.get("name") instanceof String);
		assertTrue(map.get("creationDate") instanceof Long);
		assertTrue(map.get("modifiedDate") instanceof Long);
		assertFalse(map.containsKey("playlists"));

		SyncAble folder2 = SyncGenericConverter.toBean(map, LocalFolder.class);
		compareFolder(folder, (LocalFolder) folder2, false);

		RemoteFolder folder3 = SyncGenericConverter.toBean(map, RemoteFolder.class);
		compareFolder(folder, folder3, true);
		compareFolder(folder3, (LocalFolder) folder2, true);
	}

	@Test
	public void shouldCreateAMapFromLocalFolderWithUpdateAction() throws Exception {
		LocalFolder folder = createFolder();
		Map<String, Object> map = getGenericMap(folder, SyncOperation.UPDATE);
		assertTrue(map.values().size() == 5);
		assertEquals("LocalFolder", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("hashcode") instanceof String);
		assertTrue(map.get("name") instanceof String);
		assertTrue(map.get("modifiedDate") instanceof Long);

		LocalFolder folder2 = SyncGenericConverter.toBean(map, LocalFolder.class);

		assertEquals(folder.getName(), folder2.getName());
		assertEquals(folder.getModifiedDate(), folder2.getModifiedDate());
	}

	@Test
	public void shouldCreateAMapFromLocalFolderWithDeleteAction() throws Exception {
		LocalFolder folder = createFolder();
		Map<String, Object> map = getGenericMap(folder, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldCreateAMapFromContactInfo() throws Exception {
		ContactInfo contact = createContact();
		Map<String, Object> map = getGenericMap(contact, SyncOperation.SAVE);
		assertTrue(map.values().size() == 16);
		assertEquals("ContactInfo", map.get(SyncGenericConverter.ENTITY));

		assertTrue(map.get("message") instanceof String);
		assertTrue(map.get("isDropping") instanceof Boolean);
		assertTrue(map.get("month") instanceof Integer);
		// NOTICE THAT SOME VALUES CHANGE AFTER SERIALIZING TO JSON
		assertTrue(map.get("gender") instanceof String);
		assertTrue(map.get("id") instanceof Integer);

		SyncAble contact2 = SyncGenericConverter.toBean(map, ContactInfo.class);
		compareContactInfo(contact, (ContactInfo) contact2);
	}

	@Test
	public void shouldCreateAMapFromContactInfoWithDeleteAction() throws Exception {
		ContactInfo contact = createContact();
		Map<String, Object> map = getGenericMap(contact, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldCreateAMapFromContactFolder() throws Exception {
		ContactFolder cf = createContactFolder();
		Map<String, Object> map = getGenericMap(cf, SyncOperation.SAVE);
		assertTrue(map.values().size() == 4);
		assertEquals("ContactFolder", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("name") instanceof String);

		ContactFolder cf2 = SyncGenericConverter.toBean(map, ContactFolder.class);
		assertEquals(cf.getId(), cf2.getId());
		assertEquals(cf.getName(), cf2.getName());
	}

	@Test
	public void shouldCreateAMapFromContactFolderWithUpdateAction() throws Exception {
		ContactFolder cf = createContactFolder();
		Map<String, Object> map = getGenericMap(cf, SyncOperation.UPDATE);
		assertTrue(map.values().size() == 4);
		assertEquals("ContactFolder", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("name") instanceof String);

		ContactFolder cf2 = SyncGenericConverter.toBean(map, ContactFolder.class);
		assertEquals(cf.getId(), cf2.getId());
		assertEquals(cf.getName(), cf2.getName());
	}

	@Test
	public void shouldCreateAMapFromContactFolderWithDeleteAction() throws Exception {
		ContactFolder cf = createContactFolder();
		Map<String, Object> map = getGenericMap(cf, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldCreateAMapFromContactUserFolder() throws Exception {
		ContactUserFolder cuf = createContactUserFolder();
		Map<String, Object> map = getGenericMap(cuf, SyncOperation.SAVE);
		assertTrue(map.values().size() == 5);
		assertEquals("ContactUserFolder", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("id") instanceof String);
		assertTrue(map.get("email") instanceof String);
		assertTrue(map.get("idFolder") instanceof String);

		ContactUserFolder cuf2 = SyncGenericConverter.toBean(map, ContactUserFolder.class);
		assertEquals(cuf.getId(), cuf2.getId());
		assertEquals(cuf.getEmail(), cuf2.getEmail());
		assertEquals(cuf.getIdFolder(), cuf2.getIdFolder());
	}

	@Test
	public void shouldCreateAMapFromContactUserFolderWithUpdateAction() throws Exception {
		ContactUserFolder cuf = createContactUserFolder();
		Map<String, Object> map = getGenericMap(cuf, SyncOperation.UPDATE);
		assertTrue(map.values().size() == 5);
		assertEquals("ContactUserFolder", map.get(SyncGenericConverter.ENTITY));
		assertTrue(map.get("id") instanceof String);
		assertTrue(map.get("email") instanceof String);
		assertTrue(map.get("idFolder") instanceof String);

		ContactUserFolder cuf2 = SyncGenericConverter.toBean(map, ContactUserFolder.class);
		assertEquals(cuf.getId(), cuf2.getId());
		assertEquals(cuf.getEmail(), cuf2.getEmail());
		assertEquals(cuf.getIdFolder(), cuf2.getIdFolder());
	}

	@Test
	public void shouldCreateAMapFromContactUserFolderWithDeleteAction() throws Exception {
		ContactUserFolder cuf = createContactUserFolder();
		Map<String, Object> map = getGenericMap(cuf, SyncOperation.DELETE);
		assertTrue(map.values().size() == 2);
	}

	@Test
	public void shouldReturnBecauseNoClassWasSent() throws Exception {
		assertNull(SyncGenericConverter.toBean(new HashMap<String, Object>(), null));
	}

	private Map<String, Object> getGenericMap(SyncAble syncAble, SyncOperation op) {
		Map<String, Object> map = SyncGenericConverter.toMap(syncAble, op);
		assertNotNull(map);
		assertNotNull(map.get(SyncGenericConverter.SYNC_HASHCODE));
		assertNotNull(map.get(SyncGenericConverter.ENTITY));
		for (String key : map.keySet()) {
			log.debug(key + " : " + map.get(key));
		}
		map = JsonConverter.toBean(JsonConverter.toJson(map), HashMap.class);
		return map;
	}

	private void compareContactInfo(ContactInfo contact, ContactInfo contact2) {
		assertEquals(contact.getDay(), contact2.getDay());
		assertEquals(contact.getEmail(), contact2.getEmail());
		assertEquals(contact.getFirstName(), contact2.getFirstName());
		assertEquals(contact.getGender(), contact2.getGender());
		assertEquals(contact.getId(), contact2.getId());
		assertEquals(contact.getIdLocation(), contact2.getIdLocation());
		assertEquals(contact.getLastName(), contact2.getLastName());
	}

	private LocalFolder createFolder() {
		LocalFolder folder = new LocalFolder("folder");
		folder.setHashcode("1234567890");
		folder.setModifiedDate(new Date());
		folder.setCreationDate(new Date());
		return folder;
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
		track.setPlaycount(150);
		track.setRating(66);
		track.setSampleRate("mmm dunno");
		track.setSize(1024 * 4);
		track.setSkips(100);
		track.setTrackNumber("1");
		track.setYear("1979");
		track.setFileName("track.mp3");
		track.setTrackNumber("10");
		track.setVbr(true);
		track.setNewContent(true);
		return track;
	}

	private LocalPlaylist createPlaylist() {
		LocalTrack trackA = createTrack();
		trackA.setName("track A");
		LocalTrack trackB = createTrack();
		trackB.setName("track B");
		LocalFolder folder = new LocalFolder("folder");
		folder.setHashcode("1234567890");
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

	private ContactInfo createContact() {
		ContactInfo contact = new ContactInfo(createUser());
		contact.setStatus(ContactStatus.offline);
		return contact;
	}

	private User createUser() {
		User user = new User();
		user.setId(100L);
		user.setFirstName("First Name");
		user.setLastName("Last Name");
		user.setEmail("user@all.com");
		user.setPassword("password");
		user.setGender(Gender.FEMALE);
		user.setIdLocation("123456");
		user.setQuote("some quote");
		user.setZipCode("54766");
		user.setVersion(3L);
		user.setDay(24);
		user.setMonth(8);
		user.setYear(1984);
		user.setCity(new City());
		return user;
	}

	private ContactUserFolder createContactUserFolder() {
		ContactUserFolder cuf = new ContactUserFolder();
		cuf.setIdFolder("1234567890");
		cuf.setEmail("test@alll.com");
		return cuf;
	}

	private ContactFolder createContactFolder() {
		ContactFolder cf = new ContactFolder("folder");
		return cf;
	}

	private void compareFolder(Folder expected, Folder actual, boolean isRemote) {
		if (!isRemote) {
			assertTrue(expected.getCreationDate().equals(actual.getCreationDate()));
		}
		compareFolder(expected, actual);
	}

	private void compareFolder(Folder expected, Folder actual) {
		assertNotNull(actual);
		assertEquals(expected.getHashcode(), actual.getHashcode());
		assertEquals(expected.getName(), actual.getName());
	}

	private void comparePlaylist(Playlist playlist, Playlist result, boolean isRemotePlaylist) {
		comparePlaylist(playlist, result);
		if (!isRemotePlaylist) {
			assertEquals(playlist.getCreationDate(), result.getCreationDate());
		}
	}

	private void comparePlaylist(Playlist playlist, Playlist result) {
		assertNotNull(result);
		assertEquals(playlist.getName(), result.getName());
		assertEquals(playlist.getOwner(), result.getOwner());
		assertEquals(playlist.getLastPlayed(), result.getLastPlayed());
		assertEquals(playlist.getHashcode(), result.getHashcode());
		assertEquals(playlist.getModifiedDate(), result.getModifiedDate());
	}

	private void compareTrack(Track expected, Track actual) {
		assertNotNull(actual);
		assertEquals(expected.getDownloadString(), actual.getDownloadString());
		assertEquals(expected.getGenre(), actual.getGenre());
		assertEquals(expected.isEnabled(), actual.isEnabled());
		assertEquals(expected.getAlbum(), actual.getAlbum());
		assertEquals(expected.getDateAdded(), actual.getDateAdded());
		assertEquals(expected.getArtist(), actual.getArtist());
		assertEquals(expected.getLastPlayed(), actual.getLastPlayed());
		assertEquals(expected.getPlaycount(), actual.getPlaycount());
		assertEquals(expected.getHashcode(), actual.getHashcode());
		assertEquals(expected.getTrackNumber(), actual.getTrackNumber());
		assertEquals(expected.getSize(), actual.getSize());
		assertEquals(expected.getSampleRate(), actual.getSampleRate());
		assertEquals(expected.getDuration(), actual.getDuration());
		assertEquals(expected.getLastSkipped(), actual.getLastSkipped());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.getSkips(), actual.getSkips());
		assertEquals(expected.getYear(), actual.getYear());
		assertEquals(expected.getRating(), actual.getRating());
		assertEquals(expected.getBitRate(), actual.getBitRate());
		assertEquals(expected.getFileFormat(), actual.getFileFormat());
		assertEquals(expected.getFileName(), actual.getFileName());
	}

	private PlaylistTrack createPlaylistTrack() {
		LocalPlaylist createPlaylist = createPlaylist();
		LocalTrack createTrack = createTrack();
		PlaylistTrack playlistTrack = new PlaylistTrack(createTrack, createPlaylist);
		playlistTrack.setTrackPosition(5);
		return playlistTrack;
	}
}
