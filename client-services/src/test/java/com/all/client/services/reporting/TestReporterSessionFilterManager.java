package com.all.client.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.all.core.common.services.reporting.ReportSender;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;

public class TestReporterSessionFilterManager {
	@InjectMocks
	private ReporterSessionFilterManager manager = new ReporterSessionFilterManager();
	@Mock
	private StatFilter filter1;
	@Mock
	private StatFilter filter2;
	@Mock
	private AllStat stat;
	@Mock
	private ReportSender sender;
	@Captor
	private ArgumentCaptor<List<AllStat>> captor;

	private String email = "a@a.com";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		List<StatFilter> filters = new ArrayList<StatFilter>();
		filters.add(filter1);
		filters.add(filter2);
		manager.setStatFilters(filters);
	}

	@Test
	public void shouldNotFilterStatsWhileNotLoggedIn() throws Exception {
		manager.onStatReportedMessage(stat);
		verify(filter1, never()).filter(stat);
		verify(filter2, never()).filter(stat);
	}

	@Test
	public void shouldFilterStats() throws Exception {
		login(email);
		manager.onStatReportedMessage(stat);
		verify(filter1).filter(stat);
		verify(filter2).filter(stat);
	}

	@Test
	public void shouldLogout() throws Exception {
		shouldFilterStats();
		AllStat filterStat1 = mock(AllStat.class);
		AllStat filterStat2 = mock(AllStat.class);
		when(filter1.close()).thenAnswer(new IterableAnswer(filterStat1));
		when(filter2.close()).thenAnswer(new IterableAnswer(filterStat2));
		manager.logout();
		verify(sender).send(captor.capture());
		List<AllStat> stats = captor.getValue();
		assertEquals(2, stats.size());
		assertEquals(filterStat1, stats.get(0));
		assertEquals(filterStat2, stats.get(1));
	}

	@Test
	public void shouldLogout2() throws Exception {
		shouldFilterStats();
		AllStat filterStat1 = mock(AllStat.class);
		when(filter1.close()).thenAnswer(new IterableAnswer(filterStat1));
		when(filter2.close()).thenReturn(null);
		manager.logout();
		verify(sender).send(captor.capture());
		List<AllStat> stats = captor.getValue();
		assertEquals(1, stats.size());
		assertEquals(filterStat1, stats.get(0));
	}

	@Test
	public void shouldLogout3() throws Exception {
		shouldFilterStats();
		when(filter1.close()).thenReturn(null);
		when(filter2.close()).thenReturn(null);
		manager.logout();
		verify(sender, never()).send(captor.capture());
	}

	@Test
	public void shouldFilterEvenIfOneFilterFails() throws Exception {
		doThrow(new RuntimeException()).when(filter1).init(any(User.class));
		doThrow(new RuntimeException()).when(filter1).filter(any(AllStat.class));
		doThrow(new RuntimeException()).when(filter1).close();
		AllStat filterStat2 = mock(AllStat.class);
		when(filter2.close()).thenAnswer(new IterableAnswer(filterStat2));

		shouldFilterStats();
		manager.logout();
		verify(sender).send(captor.capture());
		List<AllStat> stats = captor.getValue();
		assertEquals(1, stats.size());
		assertEquals(filterStat2, stats.get(0));
	}

	private void login(String email) {
		User user = new User();
		user.setEmail(email);
		manager.login(user);
		verify(filter1).init(user);
		verify(filter2).init(user);
	}

	@SuppressWarnings("unchecked")
	class IterableAnswer implements Answer {

		private final Object[] objects;

		public IterableAnswer(Object... objects) {
			this.objects = objects;
		}

		@Override
		public Object answer(InvocationOnMock invocation) throws Throwable {
			return Arrays.asList(objects);
		}

	}
}
