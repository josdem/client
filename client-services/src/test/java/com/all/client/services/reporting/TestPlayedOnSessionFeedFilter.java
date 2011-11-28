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
import com.all.shared.stats.PlayCountStat;

public class TestPlayedOnSessionFeedFilter {
	@InjectMocks
	private PlayedOnSessionFeedFilter filter = new PlayedOnSessionFeedFilter();
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
		filter.filter(new PlayCountStat("", trackId, 1));
		verify(topTracks).add(track);
	}

	@Test
	public void shouldNotAddOtherStat() throws Exception {
		filter.filter(mock(AllStat.class));
		verify(topTracks, never()).add(track);
	}

	@Test
	public void shouldNotAddNotFoundStat() throws Exception {
		filter.filter(new PlayCountStat("", "notAnId", 1));
		verify(topTracks, never()).add(track);
	}

}
