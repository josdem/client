package com.all.client.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestTrackFile {
	TrackFile trackFile = new TrackFile();

	@Test
	public void shouldGetFile() throws Exception {
		assertNull(trackFile.getFile());
		trackFile.setFilename("something.mp3");
		assertNotNull(trackFile.getFile());
	}

	@Test
	public void shouldExistsTrackPhysically() throws Exception {
		trackFile.setFilename(null);
		assertFalse(trackFile.exists());
		trackFile.setFilename("invalid name");
		assertFalse(trackFile.exists());
		trackFile.setFilename("src/test/resources/playlist/TestSong1.mp3");
		assertTrue(trackFile.exists());
	}
}
