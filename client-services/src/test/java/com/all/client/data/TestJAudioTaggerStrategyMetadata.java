package com.all.client.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.Before;
import org.junit.Test;

import com.all.client.model.TrackFile;
import com.all.shared.model.Track;

public class TestJAudioTaggerStrategyMetadata {
	private Track track;
	private Tag tag;
	private TrackFile trackFile;
	private JAudioTaggerStrategy strategy;
	private AudioHeader audioHeader;

	@Before
	public void initialize() {
		strategy = new JAudioTaggerStrategy();

		audioHeader = mock(AudioHeader.class);
		tag = mock(Tag.class);
		File file = mock(File.class);
		MP3File audioFile = mock(MP3File.class);
		trackFile = mock(TrackFile.class);

		strategy.audioFile = audioFile;

		when(audioFile.getTag()).thenReturn(tag);
		when(audioFile.getAudioHeader()).thenReturn(audioHeader);
		when(audioFile.hasID3v2Tag()).thenReturn(true);

		when(audioHeader.getBitRate()).thenReturn("256");
		when(audioHeader.getSampleRate()).thenReturn("44100");
		when(audioHeader.getTrackLength()).thenReturn(8888);

		when(tag.getFirst(FieldKey.TRACK)).thenReturn("11");
		when(tag.getFirst(FieldKey.ARTIST)).thenReturn("Armin Van Buuren");
		when(tag.getFirst(FieldKey.ALBUM)).thenReturn("Kaleidoscope");
		when(tag.getFirst(FieldKey.GENRE)).thenReturn("Progressive");
		when(tag.getFirst(FieldKey.YEAR)).thenReturn("2010");
		when(tag.getFirst(FieldKey.TITLE)).thenReturn("Pushing Air");

		when(file.getName()).thenReturn("TrackName.mp3");

		when(trackFile.getFile()).thenReturn(file);
		when(trackFile.getHashcode()).thenReturn("0123456789abcdef0123456789abcdef01234567");
	}

	@Test
	public void shouldgetBitrateMP3() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("256", track.getBitRate());
		assertFalse(track.isVBR());
	}

	@Test
	public void shouldgetBitrateMP3WhenIsVariableBitRate() throws Exception {
		when(audioHeader.getBitRate()).thenReturn("~256");

		track = strategy.createTrack(trackFile);
		assertEquals("256", track.getBitRate());
		assertTrue(track.isVBR());
	}

	@Test
	public void shouldSetSampleRate() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("44100", track.getSampleRate());
	}

	@Test
	public void shouldSetDuration() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals(8888, track.getDuration());
	}

	@Test
	public void shouldSetTrackNumber() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("11", track.getTrackNumber());
	}

	@Test
	public void shouldHandleAErrorOnSetTrackNumber() throws Exception {
		when(tag.getFirst(FieldKey.TRACK)).thenReturn(null);
		track = strategy.createTrack(trackFile);
		assertEquals("", track.getTrackNumber());
	}

	@Test
	public void shouldSetArtist() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("Armin Van Buuren", track.getArtist());
	}

	@Test
	public void shouldSetAlbum() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("Kaleidoscope", track.getAlbum());
	}

	@Test
	public void shouldSetGenre() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("Progressive", track.getGenre());
	}

	@Test
	public void shouldSetYear() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("2010", track.getYear());
	}

	@Test
	public void shouldSetTitle() throws Exception {
		track = strategy.createTrack(trackFile);
		assertEquals("Pushing Air", track.getName());
	}

	@Test
	public void shouldGetArtistWhitAnMp4() throws Exception {

	}

	// @Test
	// public void shouldGetBitrateFlac() throws Exception {
	// trackFile = new TrackFile(new
	// File("src/test/resources/audio/Sample1.flac"));
	// track = context.getTrack(trackFile, localModelDao);
	// assertEquals("909 kbps", track.getBitRate());
	// }
	//
	// @Test
	// public void shouldGetBitrateAlac() throws Exception {
	// trackFile = new TrackFile(new
	// File("src/test/resources/audio/Sample1.flac"));
	// track = context.getTrack(trackFile, localModelDao);
	// assertEquals("909 kbps", track.getBitRate());
	// }
	//
	// @Test
	// public void shouldGetBitrateAAC() throws Exception {
	// trackFile = new TrackFile(new File("src/test/resources/audio/AAC.m4a"));
	// track = context.getTrack(trackFile, localModelDao);
	// assertEquals("128 kbps", track.getBitRate());
	// }

}
