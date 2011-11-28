package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.Track;

public class TestModelCollection extends UnitTestCase {

	@Mock
	private Track track;
	@Mock
	private Track mockTrack;
	@Mock
	private Playlist playlist;
	@Mock
	private Folder folder;
	@Mock
	private SmartPlaylist smartPlaylist;
	@SuppressWarnings("unused")
	@Mock
	private LocalModelDao dao;

	private MockPlaylist stubPlaylist = new MockPlaylist();
	private MockPlaylist playlist2 = new MockPlaylist();

	@Test
	public void shouldTestAnEmptyCollectionState() throws Exception {
		ModelCollection collection = new ModelCollection();
		assertTrue(collection.empty(ModelTypes.tracks, ModelTypes.folders, ModelTypes.playlists, ModelTypes.others));
		for (ModelTypes type : ModelTypes.values()) {
			assertFalse(collection.has(type));
			assertTrue(collection.empty(type));
			assertFalse(collection.one(type));
			assertTrue(collection.only(type));
			assertFalse(collection.onlyOne(type));
			assertFalse(collection.hasAny(type));
		}
		assertFalse(collection.hasAny(ModelTypes.tracks, ModelTypes.folders, ModelTypes.playlists, ModelTypes.others));
		assertTrue(collection.isEmpty());
	}

	@Test
	public void shouldTestACollectionWithOneOfEachItems() throws Exception {
		List<Object> list = new ArrayList<Object>();
		list.add(mock(Track.class));
		list.add(mock(Playlist.class));
		list.add(mock(Folder.class));
		list.add(mock(Object.class));
		list.add(mock(SmartPlaylist.class));
		ModelCollection collection = new ModelCollection(list);
		assertTrue(collection.has(ModelTypes.tracks, ModelTypes.folders, ModelTypes.playlists, ModelTypes.others));
		assertTrue(collection.one(ModelTypes.tracks, ModelTypes.folders, ModelTypes.playlists, ModelTypes.others));
		for (ModelTypes type : ModelTypes.values()) {
			assertTrue(collection.hasAny(type));
			assertTrue(collection.has(type));
			assertFalse(collection.empty(type));
			assertTrue(collection.one(type));
			assertFalse(collection.only(type));
			assertFalse(collection.onlyOne(type));
		}
	}

	@Test
	public void shouldTestACollectionWithTwoOfEachItems() throws Exception {
		List<Object> list = new ArrayList<Object>();
		list.add(mock(Track.class));
		list.add(mock(Track.class));
		list.add(mock(Playlist.class));
		list.add(mock(Playlist.class));
		list.add(mock(Folder.class));
		list.add(mock(Folder.class));
		list.add(mock(Object.class));
		list.add(mock(Object.class));
		list.add(mock(SmartPlaylist.class));
		list.add(mock(SmartPlaylist.class));

		ModelCollection collection = new ModelCollection(list);
		assertTrue(collection.has(ModelTypes.tracks, ModelTypes.folders, ModelTypes.playlists, ModelTypes.others,
				ModelTypes.smartPlaylists));
		assertFalse(collection.one(ModelTypes.tracks, ModelTypes.folders, ModelTypes.playlists, ModelTypes.others,
				ModelTypes.smartPlaylists));
		for (ModelTypes type : ModelTypes.values()) {
			assertTrue(collection.has(type));
			assertFalse(collection.empty(type));
			assertFalse(collection.one(type));
			assertFalse(collection.only(type));
			assertFalse(collection.onlyOne(type));
		}
	}

	@Test
	public void shouldTestACollectionWithOnlyFolders() throws Exception {
		List<Object> list = new ArrayList<Object>();
		list.add(mock(Folder.class));
		list.add(mock(Folder.class));
		ModelCollection collection = new ModelCollection(list);
		assertTrue(collection.has(ModelTypes.folders));
		assertTrue(collection.empty(ModelTypes.tracks, ModelTypes.others, ModelTypes.playlists));
		assertTrue(collection.only(ModelTypes.folders));
		assertTrue(collection.only(ModelTypes.folders, ModelTypes.tracks));
		assertFalse(collection.onlyOne(ModelTypes.folders));
		assertTrue(collection.only(ModelTypes.folders, ModelTypes.tracks));
	}

	@Test
	public void shouldTestACollectionWithOnlyOneFolder() throws Exception {
		List<Object> list = new ArrayList<Object>();
		list.add(mock(Folder.class));
		ModelCollection collection = new ModelCollection(list);
		assertTrue(collection.has(ModelTypes.folders));
		assertTrue(collection.empty(ModelTypes.tracks, ModelTypes.others, ModelTypes.playlists));
		assertTrue(collection.only(ModelTypes.folders));
		assertTrue(collection.onlyOne(ModelTypes.folders));
	}

	@Test
	public void shouldReturnTheSameCollectionsAlwaysAndThoseShouldBeModificableByExternalCode() throws Exception {
		// i.e. This collection returns its collections always.
		ModelCollection collection = new ModelCollection();
		List<Track> tracks = collection.getTracks();
		tracks.add(mock(Track.class));
		List<Playlist> playlists = collection.getPlaylists();
		playlists.add(mock(Playlist.class));
		List<Folder> folders = collection.getFolders();
		folders.add(mock(Folder.class));
		List<Object> others = collection.getOthers();
		others.add(mock(Object.class));
		assertEquals(tracks, collection.getTracks());
		assertEquals(playlists, collection.getPlaylists());
		assertEquals(folders, collection.getFolders());
		assertEquals(others, collection.getOthers());
	}

	@Test
	public void shouldGetTheInverseArrayInTheModelTypesEnum() {
		ModelTypes[] matrixToInvert = new ModelTypes[] { ModelTypes.playlists, ModelTypes.tracks };
		ModelTypes[] expectedInverseMatrix = new ModelTypes[] { ModelTypes.folders, ModelTypes.others,
				ModelTypes.smartPlaylists };
		ModelTypes[] actualInverseMatrix = ModelTypes.invert(matrixToInvert);
		assertEquals(expectedInverseMatrix.length, actualInverseMatrix.length);
	}

	@Test
	public void shouldGetTheInverseArrayInTheModelTypesEnumOfAnEmptyArray() {
		ModelTypes[] matrixToInvert = new ModelTypes[] {};
		ModelTypes[] expectedInverseMatrix = new ModelTypes[] { ModelTypes.folders, ModelTypes.tracks, ModelTypes.others,
				ModelTypes.playlists, ModelTypes.smartPlaylists };
		ModelTypes[] actualInverseMatrix = ModelTypes.invert(matrixToInvert);
		assertEquals(expectedInverseMatrix.length, actualInverseMatrix.length);
	}

	@Test
	public void shouldGetTheInverseArrayInTheModelTypesEnumOfAnArrayWithAllThePosibilities() {
		ModelTypes[] matrixToInvert = new ModelTypes[] { ModelTypes.playlists, ModelTypes.tracks, ModelTypes.folders,
				ModelTypes.others, ModelTypes.smartPlaylists };
		ModelTypes[] expectedInverseMatrix = new ModelTypes[] {};
		ModelTypes[] actualInverseMatrix = ModelTypes.invert(matrixToInvert);
		assertEquals(expectedInverseMatrix.length, actualInverseMatrix.length);
	}

	@Test
	public void shouldGetTheInverseArrayInTheModelTypesEnumWithNoArguments() {
		ModelTypes[] expectedInverseMatrix = new ModelTypes[] { ModelTypes.folders, ModelTypes.others,
				ModelTypes.playlists, ModelTypes.tracks, ModelTypes.smartPlaylists };
		ModelTypes[] actualInverseMatrix = ModelTypes.invert();
		assertEquals(expectedInverseMatrix.length, actualInverseMatrix.length);
	}

	@Test
	public void shouldHackEmmaToRunSomeInsertedMethodsOnTheModelTypesEnum() throws Exception {
		ModelTypes.tracks.compareTo(ModelTypes.tracks);
		ModelTypes.tracks.name();
		ModelTypes.tracks.ordinal();
		ModelTypes.valueOf("tracks");
	}

	@Test
	public void shouldGetTotalNumberOfTracksExcludingRepeatedOnes() throws Exception {

		List<Track> tracksA = new ArrayList<Track>();
		tracksA.add(mock(Track.class));
		tracksA.add(mockTrack);
		stubPlaylist.setTrackList(tracksA);

		List<Track> tracksB = new ArrayList<Track>();
		tracksB.add(mock(Track.class));
		playlist2.setTrackList(tracksB);

		MockFolder folder = new MockFolder();
		folder.add(stubPlaylist);
		folder.add(playlist2);

		SmartPlaylist mockSmartPlaylist = mock(SmartPlaylist.class);
		when(mockSmartPlaylist.getPlaylist()).thenReturn(new MockPlaylist());

		ModelCollection collection = new ModelCollection(mockTrack, stubPlaylist, folder, mockSmartPlaylist);

		assertEquals(3, collection.trackCount());
	}

	@Test
	public void shouldRemoveAPlaylistIfItIsInsideAFolder() throws Exception {
		List<Playlist> playlists = new ArrayList<Playlist>();
		Playlist playlist = mock(Playlist.class);
		playlists.add(playlist);
		Folder folder = mock(Folder.class);
		when(folder.getPlaylists()).thenReturn(playlists);
		ModelCollection collection = new ModelCollection(playlist, folder);
		collection.cleanUp();
		assertEquals(1, collection.getFolders().size());
		assertEquals(0, collection.getPlaylists().size());
	}

	@Test
	public void shouldDefineATLocalTrackAsTrack() throws Exception {
		assertTrue(Track.class.isAssignableFrom(LocalTrack.class));
		assertFalse(Folder.class.isAssignableFrom(LocalTrack.class));
	}

	@Test
	public void shouldConstructAModelCollectionFromListsOfDifferentShit() throws Exception {
		List<Track> tracks = new ArrayList<Track>();
		tracks.add(track);
		List<Playlist> playlists = new ArrayList<Playlist>();
		playlists.add(playlist);
		List<Folder> folders = new ArrayList<Folder>();
		folders.add(folder);
		List<SmartPlaylist> smartPlaylists = new ArrayList<SmartPlaylist>();
		smartPlaylists.add(smartPlaylist);

		ModelCollection model = new ModelCollection(tracks, playlists, folders, smartPlaylists);

		assertTrue(model.getTracks().contains(track));
		assertTrue(model.getPlaylists().contains(playlist));
		assertTrue(model.getFolders().contains(folder));
		assertTrue(model.getSmartPlaylists().contains(smartPlaylist));
	}

	@Test
	public void shouldFilterTracksWithoutMagnetlink() throws Exception {
		Track mockTrack3 = mock(Track.class);
		playlist2.add(mockTrack3);
		MockFolder folder = new MockFolder();
		folder.add(playlist2);
		ModelCollection modelCollection = new ModelCollection(folder);
		modelCollection.filterTracksWithoutMagnetlink();

		List<Folder> folders = modelCollection.getFolders();
		assertTrue(folders.get(0).getPlaylist().getTracks().isEmpty());
	}

}
