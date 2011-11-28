package com.all.client.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.shared.model.User;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.UserSessionStat;

public class TestSessionStatFilter {
	@InjectMocks
	private SessionStatFilter filter = new SessionStatFilter();
	@Mock
	private UserSessionStat sessionStat;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAddStatToSessionStat() throws Exception {
		AllStat stat = mock(AllStat.class);
		filter.filter(stat);
		verify(sessionStat).add(stat);
	}

	@Test
	public void shouldClose() throws Exception {
		Iterable<UserSessionStat> close = filter.close();
		verify(sessionStat).close();
		assertEquals(sessionStat, close.iterator().next());
	}

	@Test
	public void closingTwiceReturnsNull() throws Exception {
		filter.close();
		Iterable<UserSessionStat> close = filter.close();
		assertNull(close);
	}

	@Test
	public void shouldInitializeAndCloseCorrectly() throws Exception {
		User user = new User();
		String email = "a@a.com";
		user.setEmail(email);
		filter.init(user);
		sessionStat = filter.close().iterator().next();
		assertEquals(email, sessionStat.getEmail());

	}
}
