package com.all.client.view.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.junit.Test;


public class TestAudioFilter  {

	@Test
	public void shouldAcceptAudioFile() throws Exception {
		FileFilter filter = new AudioFilter();
		assertTrue(filter.accept(new File("src/test/resources/audio/TestSong1.mp3")));
		assertFalse(filter.accept(new File("src/test/resources/audio/TestSong1.mp8")));
	}

}
