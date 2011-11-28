package com.all.core.common.services.reporting;

import static com.all.shared.messages.MessEngineConstants.USAGE_STATS_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
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

import com.all.core.common.messages.ErrorMessage;
import com.all.core.common.services.reporting.ReportSender;
import com.all.core.common.services.reporting.ReporterErrorMessageHandler;
import com.all.core.common.services.reporting.ReporterStatFileManager;
import com.all.messengine.MessEngine;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.usage.UserActionStat;

public class TestReporterErrorMessageHandler {
	@InjectMocks
	private ReporterErrorMessageHandler reporterErrorMessageHandler;
	@Mock
	private MessEngine messEngine;
	@Mock
	private ReportSender reportSender;
	@Mock
	private ReporterStatFileManager statFileManager;
	@Captor
	private ArgumentCaptor<List<AllStat>> reportSenderCaptor;

	@Before
	public void setup() {
		reporterErrorMessageHandler = new ReporterErrorMessageHandler();
		MockitoAnnotations.initMocks(this);
		reporterErrorMessageHandler.setup();
		verify(messEngine).addMessageListener(ErrorMessage.getType(USAGE_STATS_TYPE), reporterErrorMessageHandler);
	}

	@Test
	public void shouldWriteStats() throws Exception {
		ArrayList<AllStat> statList = new ArrayList<AllStat>();
		AllMessage<ArrayList<AllStat>> message = new AllMessage<ArrayList<AllStat>>(USAGE_STATS_TYPE, statList);
		ErrorMessage errorMessage = new ErrorMessage(message);
		reporterErrorMessageHandler.onMessage(errorMessage);
		verify(statFileManager).saveStats(statList);
		assertTrue(true);
	}

	@Test
	public void shouldSendLoadedStuffBackToMessengine() throws Exception {
		List<List<AllStat>> majorList = new ArrayList<List<AllStat>>();

		List<AllStat> statList1 = new ArrayList<AllStat>();
		List<AllStat> statList2 = new ArrayList<AllStat>();
		statList1.add(new UserActionStat());
		statList2.add(new UserActionStat());
		
		majorList.add(statList1);
		majorList.add(statList2);
		when(statFileManager.load()).thenReturn(majorList);

		reporterErrorMessageHandler.send();

		verify(reportSender, times(2)).send(reportSenderCaptor.capture());

		assertEquals(reportSenderCaptor.getAllValues().get(0), statList1);
		assertEquals(reportSenderCaptor.getAllValues().get(1), statList2);
	}
}
