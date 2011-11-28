package com.all.client.services.reporting;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.services.MusicEntityService;
import com.all.shared.model.Track;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.DownloadStat;

public class TestDownloadOnSessionFeedFilter {
	@InjectMocks
	private SessionTrackCounterFilter filter = new DownloadOnSessionFeedFilter();
	@Mock
	MusicEntityService musicEntityService;
	@Mock
	TopTrack topTracks;
	@Mock
	Track track;

	String trackId = "aa";

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(musicEntityService.getTrack(trackId)).thenReturn(track);
	}

	@Test
	public void shouldAddAndFilterStat() throws Exception {
		filter.filter(new DownloadStat("", 1, trackId));
		verify(topTracks).add(track);
	}

	@Test
	public void shouldNotAddOtherStat() throws Exception {
		filter.filter(mock(AllStat.class));
		verify(topTracks, never()).add(track);
	}

	@Test
	public void shouldNotAddNotFoundStat() throws Exception {
		filter.filter(new DownloadStat("", 1, "NotAnId"));
		verify(topTracks, never()).add(track);
	}

}
