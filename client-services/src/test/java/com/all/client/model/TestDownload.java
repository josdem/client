package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Test;

import com.all.downloader.bean.DownloadState;
import com.all.shared.model.Track;

public class TestDownload {
	private static final String OTHERTRACK_HASH = "765";
	private static final String OTHERTRACK_NAME = "SomeOtherTrack";
	private static final long OTHERTRACK_SIZE = 1L;
	private static final long TRACK_SIZE = 1122L;
	private static final String TRACK_NAME = "Topogigio";
	private static final String TRACK_HASH = "1214";

	Track track = createTrack(TRACK_HASH, TRACK_NAME, TRACK_SIZE);
	Download download = new Download(track);

	@Test
	public void shouldCreateADownloadInfoFromATrack() throws Exception {
		assertEquals(0.0f, download.getRate(), 0.0);
		assertEquals(0, download.getProgress());
		assertEquals(0, download.getRemainingSeconds());
		assertEquals(0, download.getFreeNodes());
		assertEquals(0, download.getBusyNodes());
		assertEquals(0, download.getPriority());
		assertEquals(DownloadState.Queued, download.getStatus());

		assertEquals(0, download.getSize());
		assertEquals(track.getName(), download.getDisplayName());
		assertEquals(track.getHashcode(), download.getTrackId());
	}

	@Test
	public void shouldCompareTwoDownloadInfoCreatedFromEquivalentTracks() throws Exception {
		Track track1 = createTrack(TRACK_HASH, TRACK_NAME, TRACK_SIZE);
		Download info1 = new Download(track1);
		Track track2 = createTrack(TRACK_HASH, TRACK_NAME, TRACK_SIZE);
		Download info2 = new Download(track2);
		assertEquals(info1, info2);
		assertEquals(info1.hashCode(), info2.hashCode());
		assertFalse(info1 == info2);
	}

	@Test
	public void shouldCompareTwoDownloadInfoThatAreNOTequal() throws Exception {
		Track track1 = createTrack(TRACK_HASH, TRACK_NAME, TRACK_SIZE);
		Download info1 = new Download(track1);
		Track track2 = createTrack(OTHERTRACK_HASH, OTHERTRACK_NAME, OTHERTRACK_SIZE);
		Download info2 = new Download(track2);
		assertFalse(info1.equals(info2));
		assertFalse(info1.hashCode() == info2.hashCode());
		assertFalse(info1 == info2);
	}

	@Test
	public void shouldCompareADownloadInfoWithSomethingElse() throws Exception {
		assertFalse(download.equals(new Object()));
	}

	@Test
	public void shouldCheckSetterFunctionality() throws Exception {
		assertEquals(0.0f, download.getRate(), 0.0f);
		assertEquals(0, download.getProgress());
		assertEquals(0, download.getRemainingSeconds());
		assertEquals(0, download.getFreeNodes());
		assertEquals(0, download.getBusyNodes());
		assertEquals(0, download.getPriority());
		assertEquals(DownloadState.Queued, download.getStatus());
		download.setRate(12);
		assertEquals(12.0, download.getRate(), 0.0);
		download.setProgress(50);
		assertEquals(50, download.getProgress());
		download.setRemainingSeconds(1324);
		assertEquals(1324, download.getRemainingSeconds());
		download.setFreeNodes(3);
		assertEquals(3, download.getFreeNodes());
		download.setBusyNodes(2);
		assertEquals(2, download.getBusyNodes());
		download.setPriority(3);
		assertEquals(3, download.getPriority());
		download.setStatus(DownloadState.Downloading);
		assertEquals(DownloadState.Downloading, download.getStatus());
	}

	private Track createTrack(String hashCode, String name, long size) {
		Track track = mock(Track.class);
		when(track.getName()).thenReturn(name);
		when(track.getHashcode()).thenReturn(hashCode);
		when(track.getDownloadString()).thenReturn("magnet:?xt=urn:btih:HBALBWFEGMM3AFIZMROHOUS4EJOST4IX");
		// when(track.getFileSize()).thenReturn(size);
		return track;
	}

	@Test
	public void shouldCheckMoreResourcesAvailable() throws Exception {
		download.setRate(2);
		download.checkResourcesAvailable();
		assertTrue(System.currentTimeMillis() - download.moreSourcesTimestamp < 1000);

		download.setRate(90);
		download.checkResourcesAvailable();
		assertEquals(0, download.moreSourcesTimestamp);
	}

	@Test
	public void shouldChangeToMoreResourceStatus_WhenBitrateIsLow_ForTenMinutes() throws Exception {
		download.setRate(2);
		long timePassed = System.currentTimeMillis();
		int MORE_THAN__TEN_MINUTES = 11 * 60 * 1000;
		download.moreSourcesTimestamp = timePassed - MORE_THAN__TEN_MINUTES;

		download.checkResourcesAvailable();

		assertEquals(DownloadState.MoreSourcesNeeded, download.getStatus());
	}
	
	@Test
	public void shouldChangeToMoreResourceStatus_WhenBitrateIsLow_ForNineMinutes() throws Exception {
		download.setRate(2);
		long timePassed = System.currentTimeMillis();
		int LESS_THAN__TEN_MINUTES = 9 * 60 * 1000;
		download.moreSourcesTimestamp = timePassed - LESS_THAN__TEN_MINUTES;

		download.checkResourcesAvailable();

		assertEquals(DownloadState.Queued, download.getStatus());
	}
	
	@Test
	public void shouldNotPassToNeedMoreSourcesIfDownloading() throws Exception {
		download.setRate(2);
		long timePassed = System.currentTimeMillis();
		int MORE_THAN__TEN_MINUTES = 11 * 60 * 1000;
		download.moreSourcesTimestamp = timePassed - MORE_THAN__TEN_MINUTES;

		download.setStatus(DownloadState.Downloading);
		
		download.checkResourcesAvailable();

		assertEquals(DownloadState.Downloading, download.getStatus());
	}

	@Test
	public void shouldGetDownloadFile() throws Exception {
		Download download = new Download(track);
		assertNull("getDownloadFile should be null", download.getDownloadFile());
		download.setFilepath("someInvalidFilePath");
		assertNull(download.getDownloadFile());
		download.setFilepath("src/test/resources/playlist/TestSong1.mp3");
		assertNotNull(download.getDownloadFile());
	}

	@Test
	public void shouldGetFastResumeFile() throws Exception {
		Download download = new Download(track);
		assertNull(download.getFastResumeFile());
		download.setFilepath("someInvalidFilePath");
		assertNull(download.getFastResumeFile());
		download.setFilepath("src/test/resources/fastResume/TestSong1.mp3");
		assertNotNull(download.getFastResumeFile());
		assertTrue(download.getFastResumeFile().exists());
	}

	@Test
	public void shouldGetFastResumeString() throws Exception {
		assertNull(download.getFastResumePath());

		download.filepath = "src/test/resources/fastResume/TestSong1.mp3";
		String fastResumePath = download.getFastResumePath().replace(File.separatorChar, '/');
		
		assertTrue(fastResumePath.endsWith(download.filepath + Download.RESUME_EXTENSION));
	}
	
	@Test
	public void shouldSetStatusAndProgressFieldsWhenComplete() throws Exception {
		Download download = new Download(track);
		download.complete();
		assertEquals(DownloadState.Complete, download.getStatus());
		assertEquals(100, download.getProgress());
		assertEquals(0, download.getRemainingSeconds());
	}

}
