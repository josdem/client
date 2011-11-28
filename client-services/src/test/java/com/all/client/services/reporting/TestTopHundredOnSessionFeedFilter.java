package com.all.client.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.shared.json.JsonConverter;
import com.all.shared.model.Category;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.User;
import com.all.shared.newsfeed.AllFeed;
import com.all.shared.newsfeed.TopHundredFeed;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.DownloadStat;
import com.all.shared.stats.FeedStat;
import com.all.shared.stats.TopHundredStat;

public class TestTopHundredOnSessionFeedFilter {
	@InjectMocks
	private TopHundredOnSessionStatFilter filter = new TopHundredOnSessionStatFilter();
	@Mock
	TopTrack topTracks;
	@Mock
	Track track;
	@Mock
	User user;

	private String mail = "a@a.com";

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(user.getEmail()).thenReturn(mail);
	}

	@Test
	public void shouldReturnEmptyWhateverByDefault() throws Exception {
		assertFalse(filter.close().iterator().hasNext());
	}

	@Test
	public void shouldHaveEmptyWhateverIfNotInitialized() throws Exception {
		filter.filter(createTopHundred(1, "a", "c", "d"));
		filter.filter(createTopHundred(1, "b", "c", "d"));
		assertFalse(filter.close().iterator().hasNext());
		verify(topTracks, never()).add(track);
	}

	@Test
	public void shouldReturnSimpleStat() throws Exception {
		filter.init(user);
		filter.filter(createTopHundred(1, "a", "c", "d"));
		Iterator<FeedStat> iterator = filter.close().iterator();
		assertTrue(iterator.hasNext());
		FeedStat next = iterator.next();
		TopHundredFeed feed = (TopHundredFeed) JsonConverter.toBean(next.getJson(), AllFeed.class);
		assertEquals(1L, feed.getCategoryId());
		assertEquals("a", feed.getPlaylistHash());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void shouldReturnOnlyOneStatForMultipleInvocationsToSameTop100() throws Exception {
		filter.init(user);
		filter.filter(createTopHundred(1, "a", "c", "d"));
		filter.filter(createTopHundred(1, "a", "c", "d"));
		filter.filter(createTopHundred(1, "a", "c", "d"));
		filter.filter(createTopHundred(1, "a", "c", "d"));
		Iterator<FeedStat> iterator = filter.close().iterator();
		assertTrue(iterator.hasNext());
		FeedStat next = iterator.next();
		TopHundredFeed feed = (TopHundredFeed) JsonConverter.toBean(next.getJson(), AllFeed.class);
		assertEquals(1L, feed.getCategoryId());
		assertEquals("a", feed.getPlaylistHash());
		assertEquals("c", feed.getCategoryName());
		assertEquals("d", feed.getPlaylistName());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void shouldGet2Stats() throws Exception {
		filter.init(user);
		filter.filter(createTopHundred(1, "a", "c", "d"));
		filter.filter(createTopHundred(1, "a", "c", "d"));
		filter.filter(createTopHundred(1, "b", "e", "f"));
		filter.filter(createTopHundred(1, "b", "e", "f"));
		Iterator<FeedStat> iterator = filter.close().iterator();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertTrue(iterator.hasNext());
		iterator.next();
		assertFalse(iterator.hasNext());

	}

	private AllStat createTopHundred(long catId, String playHash, String catName, String playName) {
		TopHundredStat stat = mock(TopHundredStat.class);
		Category category = mock(Category.class);
		Playlist playlist = mock(Playlist.class);

		when(category.getId()).thenReturn(catId);
		when(category.getName()).thenReturn(catName);
		when(playlist.getHashcode()).thenReturn(playHash);
		when(playlist.getName()).thenReturn(playName);
		when(stat.category()).thenReturn(category);
		when(stat.playlist()).thenReturn(playlist);
		return stat;
	}

	@Test
	public void shouldNotAddNotFoundStat() throws Exception {
		filter.filter(new DownloadStat("", 1, "NotAnId"));
		verify(topTracks, never()).add(track);
	}

}
