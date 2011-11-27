package com.all.client.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.all.client.SimpleDBTest;
import com.all.client.model.LocalTrack;
import com.all.client.model.TrackFile;

public class TestTrack extends SimpleDBTest {

	private String song1;
	private LocalTrack track;
	private String artist;
	private String album;
	private String filename;
	private String pathname;

	@SuppressWarnings("deprecation")
	@Before
	public void createTrack() {
		File file2 = new File("test_data/test");
		file2.mkdirs();

		song1 = "song1";
		artist = "artist";
		album = "album";
		filename = "TestSong3";
		pathname = "src/test/resources/playlist/" + filename + ".mp3";
		track = LocalTrack.createEmptyTrack(song1);
	}

	@Test
	public void shouldRenameTrack() throws Exception {
		assertEquals(song1, track.getName());
		String song2 = "song2";
		track.setName(song2);
		assertEquals(song2, track.getName());
	}

	@Test
	public void shouldFormatDuration() throws Exception {
		track.setDuration(60);
		assertEquals("1:00", track.getDurationMinutes());
	}

	@Test
	public void shouldGetArtistAlbum() throws Exception {
		track.setArtist(artist);
		assertEquals(artist, track.getArtistAlbum());
		track.setAlbum(album);
		track.setArtist(null);
		assertEquals(album, track.getArtistAlbum());
		track.setArtist("");
		assertEquals(album, track.getArtistAlbum());
		track.setAlbum(null);
		assertEquals("", track.getArtistAlbum());
	}

	@Test
	public void shouldGetAlbumArtist() throws Exception {
		track.setAlbum(album);
		assertEquals(album, track.getAlbumArtist());
		track.setAlbum(null);
		track.setArtist(artist);
		assertEquals(artist, track.getAlbumArtist());
		track.setArtist(null);
		assertEquals("", track.getAlbumArtist());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldFindByHashcode() throws Exception {
		assertNull(dao.findByHashcode("aaaa"));
		track = LocalTrack.createEmptyTrack();
		addFile();
		String hashcode = track.getHashcode();
		dao.save(track);
		assertEquals(track.getHashcode(), dao.findByHashcode(hashcode).getHashcode());
	}

	private void addFile() {
		TrackFile trackFile = new TrackFile(track.getHashcode());
		trackFile.setFilename(pathname);
		dao.saveOrUpdate(trackFile);
	}

	@Test
	public void shouldIncrementPlayCount() throws Exception {
		track.incrementPlaycount();
		assertEquals(1, track.getPlaycount());
	}

	@Test
	public void shouldDisplayFileSizeFormatted() throws Exception {
		track.setSize(1023L * 1024);
		assertEquals("1,023 KB", track.getFormattedSize());
		track.setSize(1025 * 1024);
		assertEquals("1 MB", track.getFormattedSize());
		track.setSize(2050 * 1024);
		assertEquals("2 MB", track.getFormattedSize());
		track.setSize(1048590 * 1024);
		assertEquals("1 GB", track.getFormattedSize());
	}

	@Test
	public void shouldGetEnabled() throws Exception {
		assertTrue("Track should be enabled by default", track.isEnabled());
		track.setEnabled(false);
		assertFalse("Should return track enabled value", track.isEnabled());
	}

	@Test
	public void shouldGetRating() throws Exception {
		assertNotNull("Track should not be rated by default", track.getRating());
		assertEquals(0, track.getRating());
		track.setRating(2);
		assertEquals(2, track.getRating());
	}

	@Test
	public void shouldDeleteTrack() throws Exception {
		dao.delete(track);
		assertNull(dao.findById(LocalTrack.class, track.getHashcode()));
	}
	
}
