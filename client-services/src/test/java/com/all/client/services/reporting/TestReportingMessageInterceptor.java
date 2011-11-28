package com.all.client.services.reporting;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.services.reporting.ClientReporter;
import com.all.client.services.reporting.ReportingMessageInterceptor;
import com.all.messengine.MessEngine;
import com.all.messengine.support.MessEngineConfigurator;
import com.all.shared.model.AllMessage;
import com.all.shared.stats.usage.UserActions;

public class TestReportingMessageInterceptor {
	@Mock
	private MessEngine messEngine;
	@Mock
	private ClientReporter reporter;
	@InjectMocks
	private ReportingMessageInterceptor interceptor;

	@Before
	public void setup() {
		interceptor = new ReportingMessageInterceptor();
		MockitoAnnotations.initMocks(this);
		MessEngineConfigurator configurator = new MessEngineConfigurator();
		configurator.setMessEngine(messEngine);
		configurator.setupMessEngine(interceptor);
	}

	@Test
	public void shouldTestMessageRedirection() throws Exception {
		interceptor.onMessage(new AllMessage<Integer>(UserActions.USER_ACTION_MESSAGE_TYPE, 12));
		verify(reporter).logUserAction(12);
	}

	@Test
	public void shouldTestDownloadStatMsgRedirection() throws Exception {
		AllMessage<Integer> message = new AllMessage<Integer>(UserActions.USER_ACTION_MESSAGE_TYPE, 12);
		String HASHCODE = "SOME_HASHCODE";
		message.putProperty(ReportingMessageInterceptor.TRACK_ID, HASHCODE);
		interceptor.onMessage(message);
		verify(reporter).logUserAction(12);
		verify(reporter).logDownloadAction(12, HASHCODE);
	}
}
