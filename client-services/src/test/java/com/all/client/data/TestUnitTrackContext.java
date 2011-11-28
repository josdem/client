package com.all.client.data;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestUnitTrackContext {
	@UnderTest
	private TrackFactory trackContext;
	
	@Test
	public void shouldFindAllMusicInADirectory() throws Exception {
		File dir = new File("src/test/resources/mixedDir");
		List<File> musicFiles = trackContext.listTracks(dir);
		assertEquals( 3, musicFiles.size());
	}
}
