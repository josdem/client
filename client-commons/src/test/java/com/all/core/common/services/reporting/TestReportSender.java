package com.all.core.common.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.messengine.MessEngine;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.AllStat;

public class TestReportSender {
	@InjectMocks
	private ReportSender reportSender;
	@Mock
	private MessEngine messEngine;
	@Captor
	private ArgumentCaptor<AllMessage<List<AllStat>>> messEngineAllStatCaptor;

	@Before
	public void setup() {
		reportSender = new ReportSender();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSendMessageToMessEngine() throws Exception {
		ArrayList<AllStat> stats = new ArrayList<AllStat>();
		reportSender.send(stats);
		verify(messEngine).send(messEngineAllStatCaptor.capture());
		AllMessage<List<AllStat>> message = messEngineAllStatCaptor.getValue();
		assertEquals(MessEngineConstants.USAGE_STATS_TYPE, message.getType());
		assertEquals(stats, message.getBody());
	}
}
