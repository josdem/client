package com.all.client.services.reporting;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalTrack;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.FeedStat;
import com.all.shared.stats.MediaImportStat;

public class TestTotalTracksSessionStatFilter {
	@InjectMocks
	private TotalTracksSessionStatFilter filter = new TotalTracksSessionStatFilter();
	@Mock
	LocalModelDao dao;
	@Mock
	User user;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldReturnNullIfNoLogin() throws Exception {
		assertNull(filter.close());
	}

	@Test
	public void shouldReturnNullIfLoggedInButNoFilterOccured() throws Exception {
		filter.init(user);
		assertNull(filter.close());
	}

	@Test
	public void shouldReturnNullIfAnImportStatIsNeverFiltered() throws Exception {
		filter.init(user);
		filter.filter(mock(AllStat.class));
		assertNull(filter.close());
	}

	@Test
	public void shouldReturnStatIfImportIsFiltered() throws Exception {
		filter.init(user);
		filter.filter(mock(MediaImportStat.class));
		when(dao.count(LocalTrack.class)).thenReturn(20L);
		FeedStat feedStat = filter.close().iterator().next();
		assertNotNull(feedStat);
	}

}
