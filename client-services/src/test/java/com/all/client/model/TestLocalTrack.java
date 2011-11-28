package com.all.client.model;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@SuppressWarnings("deprecation")
@RunWith(MockInyectRunner.class)
public class TestLocalTrack {

	@UnderTest
	private LocalTrack track;

	@Test
	public void shouldCloneALocalTrack() throws Exception {
		track = createTrackWithData();
		LocalTrack newTrack = new LocalTrack();

		newTrack.clone(track);
		assertEquals(track.getDownloadString(), newTrack.getDownloadString());
		assertEquals(track.getLastPlayed(), newTrack.getLastPlayed());
		assertEquals(track.getLastSkipped(), newTrack.getLastSkipped());
		assertEquals(track.getPlaycount(), newTrack.getPlaycount());
		assertEquals(track.getRating(), newTrack.getRating());
		assertEquals(track.getSkips(), newTrack.getSkips());
		assertEquals(track.getTrackNumber(), newTrack.getTrackNumber());
		assertEquals(track.getFileName(), newTrack.getFileName());
		assertEquals(track.getDateDownloaded(), newTrack.getDateDownloaded());
	}

	private LocalTrack createTrackWithData() {
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
		track.setDateDownloaded(new Date());
		return track;
	}

}
