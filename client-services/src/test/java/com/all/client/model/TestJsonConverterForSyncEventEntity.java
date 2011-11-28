package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.all.shared.json.JsonConverter;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;
import com.all.shared.model.Gender;
import com.all.shared.model.SyncEventEntity;
import com.all.shared.model.SyncEventEntity.SyncOperation;
import com.all.shared.model.SyncValueObject;
import com.all.shared.model.User;
import com.all.shared.sync.SyncAble;
import com.all.shared.sync.SyncGenericConverter;

public class TestJsonConverterForSyncEventEntity {

	@SuppressWarnings("unchecked")
	@Test
	public void shouldCreateJsonFromMap() throws Exception {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("int", new Integer(1));
		map.put("String", new String("string"));
		map.put("boolean", new Boolean(true));
		map.put("long", new Long(2L));
		map.put("double", new Double(3.8));
		String json = JsonConverter.toJson(map);
		map = JsonConverter.toBean(json, HashMap.class);
		assertTrue(map.get("int") instanceof Integer);
		assertTrue(map.get("boolean") instanceof Boolean);
		assertTrue(map.get("String") instanceof String);
		assertTrue(map.get("double") instanceof Double);
		assertTrue(map.get("long") instanceof Integer);
		assertTrue((Integer)map.get("int") == 1);
		assertTrue((Double)map.get("double") == 3.8);
		assertTrue(((Integer)map.get("long")).longValue() == 2);
		assertTrue(((String)map.get("String")).equals("string"));
		assertTrue((Boolean)map.get("boolean"));
	}
	@Test
	public void shouldCreateSyncEventEntityWithAMapFromLocalTrack() throws Exception {
		LocalTrack track = createTrack();
		SyncEventEntity target = getSyncEventEntity(track);
		assertEquals("LocalTrack", target.getEntity().get("ENTITY"));
	}

	@Test
	public void shouldCreateSyncEventEntityWithAMapFromLocalPlaylist() throws Exception {
		LocalPlaylist playlist = createPlaylist();
		SyncEventEntity target = getSyncEventEntity(playlist);
		assertEquals("LocalPlaylist", target.getEntity().get("ENTITY"));
	}

	@Test
	public void shouldCreateSyncEventEntityWithAMapFromLocalFolder() throws Exception {
		LocalFolder folder = createFolder();
		SyncEventEntity target = getSyncEventEntity(folder);
		assertEquals("LocalFolder", target.getEntity().get("ENTITY"));
	}

	@Test
	public void shouldCreateSyncEventEntityWithAMapFromContactInfo() throws Exception {
		ContactInfo ci = createContact();
		SyncEventEntity target = getSyncEventEntity(ci);
		assertEquals("ContactInfo", target.getEntity().get("ENTITY"));
	}

	@Test
	public void shouldCreateSyncEventEntityWithAMapFromContactFolder() throws Exception {
		ContactFolder cf = createContactFolder();
		SyncEventEntity target = getSyncEventEntity(cf);
		assertEquals("ContactFolder", target.getEntity().get("ENTITY"));
	}

	@Test
	public void shouldCreateSyncEventEntityWithAMapFromContactUserFolder() throws Exception {
		ContactUserFolder cuf = createContactUserFolder();
		SyncEventEntity target = getSyncEventEntity(cuf);
		assertEquals("ContactUserFolder", target.getEntity().get("ENTITY"));
	}

	@Test
	public void shouldCreateSyncEventEntityListAndGetItsJson() throws Exception {
		String str = "testing";
		String str2 = "sync";
		SyncValueObject source = new SyncValueObject("test@all.com", 1, 1, System.currentTimeMillis());
		source.getEvents().add(str);
		source.getEvents().add(str2);
		String json = JsonConverter.toJson(source);
		SyncValueObject target = (SyncValueObject) JsonConverter.toBean(json, SyncValueObject.class);
		assertEquals("testing", new String(target.getEvents().get(0)));
		assertEquals("sync", new String(target.getEvents().get(1)));
		assertNotNull(target);
		assertEquals(source.getEmail(), target.getEmail());
		assertEquals(source.getDelta(), target.getDelta());
		assertEquals(source.getSnapshot(), target.getSnapshot());
		assertEquals(source.getTimestamp(), target.getTimestamp());
		assertTrue(target.getEvents().size() == 2);
	}

	private ContactUserFolder createContactUserFolder() {
		ContactUserFolder cuf = new ContactUserFolder();
		cuf.setIdFolder("1234567890");
		cuf.setEmail("test@alll.com");
		return cuf;
	}

	private SyncEventEntity genericAssertion(HashMap<String, Object> map) {
		assertNotNull(map);
		assertTrue(map.values().size() > 0);
		assertNotNull(map.get(SyncGenericConverter.SYNC_HASHCODE));
		assertNotNull(map.get(SyncGenericConverter.ENTITY));
		SyncEventEntity source = new SyncEventEntity(SyncOperation.SAVE, map);
		source.setTimestamp(System.currentTimeMillis());
		String json = JsonConverter.toJson(source);
		SyncEventEntity target = (SyncEventEntity) JsonConverter.toBean(json, SyncEventEntity.class);
		assertNotNull(target);
		assertEquals(source.getOperation(), target.getOperation());
		assertEquals(source.getTimestamp(), target.getTimestamp());
		return target;
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
		return track;
	}

	private LocalPlaylist createPlaylist() {
		LocalTrack trackA = createTrack();
		trackA.setName("track A");
		LocalTrack trackB = createTrack();
		trackB.setName("track B");
		LocalFolder folder = new LocalFolder("folder");
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

	private ContactFolder createContactFolder() {
		ContactFolder cf = new ContactFolder("folder");
		return cf;
	}

	private SyncEventEntity getSyncEventEntity(SyncAble sa) {
		HashMap<String, Object> map = SyncGenericConverter.toMap(sa, SyncOperation.SAVE);
		SyncEventEntity target = genericAssertion(map);
		return target;
	}

}
