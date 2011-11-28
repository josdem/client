package com.all.client.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.client.model.MockTrack;
import com.all.shared.json.JsonConverter;
import com.all.shared.model.Track;
import com.all.shared.model.User;
import com.all.shared.newsfeed.AllFeed;
import com.all.shared.newsfeed.FeedType;
import com.all.shared.newsfeed.TrackContentFeed;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.FeedStat;

// WARNING:: INTEGRATION TEST
public class TestSessionTrackCounterFilter {
	@Mock
	AllStat stat1;
	@Mock
	AllStat stat2;
	@Mock
	AllStat stat3;
	Track track1;
	Track track2;
	Track track3;
	User user;

	@Spy
	SessionTrackCounterFilterTester filter = new SessionTrackCounterFilterTester();

	@Before
	public void setup() throws Exception {
		track1 = new MockTrack("1");
		track2 = new MockTrack("2");
		track3 = new MockTrack("3");
		user = new User();
		user.setId(44L);
		user.setEmail("a@a.com");
		MockitoAnnotations.initMocks(this);
		when(filter.getTrack(stat1)).thenReturn(track1);
		when(filter.getTrack(stat2)).thenReturn(track2);
		when(filter.getTrack(stat3)).thenReturn(track3);
	}

	@Test
	public void shouldInitAdd3TracksAndClose() throws Exception {
		filter.init(user);
		filter.filter(stat1);
		filter.filter(stat2);
		filter.filter(stat3);
		FeedStat stat = filter.close().iterator().next();
		assertEquals("a@a.com", stat.getEmail());
		TrackContentFeed feed = (TrackContentFeed) JsonConverter.toBean(stat.getJson(), AllFeed.class);
		assertEquals(3, feed.getTrackCount());
		assertEquals(3, feed.getTracks().size());
		assertEquals("a@a.com", feed.getOwner().getEmail());
		assertEquals("1", feed.getTracks().get(0).getHashcode());
		assertEquals("2", feed.getTracks().get(1).getHashcode());
		assertEquals("3", feed.getTracks().get(2).getHashcode());
		assertEquals(FeedType.DOWNLOADED_TRACKS, feed.getType());
	}

	@Test
	public void shouldDoNothingIfNotInitialized() throws Exception {
		filter.filter(stat1);
		verify(filter, never()).getTrack(any(AllStat.class));
	}

	class SessionTrackCounterFilterTester extends SessionTrackCounterFilter {
		public SessionTrackCounterFilterTester() {
			super(3, FeedType.DOWNLOADED_TRACKS);
		}

		@Override
		Track getTrack(AllStat stat) {
			return null;
		}

	}
}
