package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.all.client.importx.itunes.ItunesImporterService;
import com.all.client.model.LocalModelDao;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/testDataLayer.xml", "/spring/clientApplicationContext.xml",
		"/spring/testApplicationContext.xml", "/core/common/CommonAppContext.xml" })
@Transactional
@TransactionConfiguration
public class TestItunesImporterServiceImpl {
	Log log = LogFactory.getLog(this.getClass());
	@Autowired
	private ItunesImporterService service;
	@Autowired
	private LocalModelDao localModelDao;

	// This library is subscribed to a podcast that doesn't have dowloaded
	// episodes
	File iTunesLibFile2 = new File("src/test/resources/iTunes/iTunesLibraryWithoutPodcasts.xml");

	private File itunesFileWithSpecialCharacters = new File(
			"src/test/resources/iTunes/iTunesLibrarySpecialCharacters.xml");

	File iTunesLibFileE = new File("src/test/resources/com/all/client/importx/itunes/iTunes Music Library Erik.xml");

	File iTunesLibFileF = new File("src/test/resources/com/all/client/importx/itunes/iTunes Music LibraryFanny.xml");
	/**
	 * TODO: verify it This test parses the iTunes Library defined in the
	 * src/test/resources/iTunes/iTunesLibrary.xml file.
	 * 
	 * This Library has this structure:
	 * 
	 * > RootPlaylist > TestSong1 > TestSong2 > TestSong3 > Folder > DeepFolder >
	 * DeepPlaylist > TestSong2 > Playlist > TestSong1 > Podcasts (User Created
	 * Folder) > El podcast falso > Episode3.mp3 > Podcasts (User Created
	 * Playlist) > Episode3.mp3 > Episode7.mp3 > Podcasts (Folder created after
	 * importing podcasts) > F r a n c o f o n > Episode6.mp3 > Episode7.mp3 >
	 * Oscar Podcast > Episode3.mp3
	 * 
	 * 
	 * */
	File iTunesLibFile = new File("src/test/resources/iTunes/iTunesLibrary.xml");

	File drmTestWithUserDefinedPlaylistAndFolder = new File(
			"src/test/resources/drmTest/drmTestWithUserDefinedPlaylistAndFolder.xml");

	@Test
	public void shouldCreateFailedImportPlaylist() throws Exception {
		ModelCollection result = service.importItunesLibrary(drmTestWithUserDefinedPlaylistAndFolder);
		// This XML at least should have 1 playlist with Not Imported Tracks
		assertTrue(result.getPlaylists().size() >= 1);
		for (Playlist p : result.getPlaylists()) {
			assertFalse(p.isEmpty());
		}
		assertTrue(result.getFolders().size() >= 1);
		for (Folder f : result.getFolders()) {
			boolean has_drmFolder = "drmFolder".equals(f.getName());
			if (has_drmFolder) {
				for (Playlist p : f.getPlaylists()) {
					boolean has_drmTest2Playlist = "drmTest2".equals(p.getName());
					assertTrue(has_drmTest2Playlist);
					assertFalse(p.isEmpty());
				}
			}
		}
	}

	@Test
	public void shouldNotAddPodcastFolderIfNoEpisodesFound() throws Exception {
		service.importItunesLibrary(iTunesLibFile2);

		List<Folder> folders = localModelDao.findAll(Folder.class);
		log.debug("Folders -> " + folders.size());
		Folder folder = null;
		for (Folder tmpFolder : folders) {
			if ("Podcasts".equals(tmpFolder.getName())) {
				folder = tmpFolder;
			}
		}
		assertNull(folder);
	}

	@Test
	public void shouldImportItunesLibFanny() {
		long start = System.currentTimeMillis();
		service.importItunesLibrary(iTunesLibFileF);
		long end = System.currentTimeMillis();
		long seconds = (start - end) / 1000L;
		log.debug(seconds);
	}

	@Test
	public void shouldImportITunesWithSpecialCharacterEnter() throws Exception {
		service.importItunesLibrary(itunesFileWithSpecialCharacters);
		List<Track> tracks = localModelDao.findAll(Track.class);
		assertNotNull(tracks);
		assertEquals(3, tracks.size());
	}

}