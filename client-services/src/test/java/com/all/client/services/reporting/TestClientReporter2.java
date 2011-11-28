package com.all.client.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.client.services.reporting.ClientReporter;
import com.all.client.services.reporting.ReporterSessionFilterManager;
import com.all.client.services.reporting.SessionStatFilter;
import com.all.client.services.reporting.StatFilter;
import com.all.core.common.services.reporting.ReportSender;
import com.all.core.common.services.reporting.Reporter;
import com.all.messengine.MessEngine;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.UserSessionStat;
import com.all.shared.stats.usage.UserActionStat;

public class TestClientReporter2 {
	@InjectMocks
	private ClientReporter clientReporter;
	@InjectMocks
	private ReporterSessionFilterManager sessionFilter = new ReporterSessionFilterManager();
	@Spy
	@InjectMocks
	private Reporter reporter = new Reporter();
	@Mock
	private ReportSender sender;
	@SuppressWarnings("unused")
	@Mock
	private MessEngine messEngine;
	@Captor
	private ArgumentCaptor<List<AllStat>> statsCaptor;

	@Before
	public void setup() {
		clientReporter = new ClientReporter();
		MockitoAnnotations.initMocks(this);
		User user = mock(User.class);
		when(user.getEmail()).thenReturn("a@a.com");
		ArrayList<StatFilter> statFilters = new ArrayList<StatFilter>();
		statFilters.add(new SessionStatFilter());
		sessionFilter.setStatFilters(statFilters);
		clientReporter.login(user);
		sessionFilter.login(user);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldIgnoreReportingOnNonOpenReporter() throws Exception {
		verify(sender, never()).send(anyList());
		clientReporter.logUserAction(1);
		reporter.send();
		verify(sender).send(statsCaptor.capture());
		assertNotNull(statsCaptor.getValue());
		assertEquals(1, statsCaptor.getValue().size());
		assertEquals(1, ((UserActionStat) statsCaptor.getValue().get(0)).getAction());
		assertEquals(1, ((UserActionStat) statsCaptor.getValue().get(0)).getTimes());
		reset(sender);

		reporter.send();
		verify(sender, never()).send(anyList());
		reset(sender);

		clientReporter.logUserAction(1);
		clientReporter.logUserAction(1);
		reporter.send();
		verify(sender).send(statsCaptor.capture());
		assertEquals(1, statsCaptor.getValue().size());
		assertEquals(1, ((UserActionStat) statsCaptor.getValue().get(0)).getAction());
		assertEquals(2, ((UserActionStat) statsCaptor.getValue().get(0)).getTimes());
	}

	@Test
	public void shouldLogout() throws Exception {
		clientReporter.logout();
		sessionFilter.logout();
		verify(sender).send(statsCaptor.capture());
		assertFalse(statsCaptor.getValue().isEmpty());
		assertTrue(statsCaptor.getValue().get(0) instanceof UserSessionStat);
	}

}
