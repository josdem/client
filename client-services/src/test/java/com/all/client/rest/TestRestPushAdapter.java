package com.all.client.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.client.RestTemplate;

import com.all.messengine.Message;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.json.JsonConverter;
import com.all.shared.model.AllMessage;
import com.all.shared.model.User;

public class TestRestPushAdapter {
	private static final Long USER_ID = 2L;
	private static final String serverPath = "http://TTTT/serv";
	private static final String servicePath = "/rest/push";

	private static final String url = serverPath + servicePath;

	@InjectMocks
	private RestPushAdapter pushAdapter = new RestPushAdapter();
	@Spy
	private StubMessEngine messEngine = new StubMessEngine();
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private User user;
	@Mock
	private ExecutorService executorService;
	@Mock
	private Properties clientSettings;

	private Runnable task;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(user.getId()).thenReturn(USER_ID);
		when(clientSettings.getProperty("all.server.url")).thenReturn(serverPath);
		when(clientSettings.getProperty("push.url")).thenReturn(servicePath);

		pushAdapter.start(user);
		ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
		verify(executorService).execute(captor.capture());
		task = captor.getValue();
		assertNotNull(task);
		reset(executorService);
	}

	@After
	public void teardown() {
		pushAdapter.stop();
		verify(executorService).shutdownNow();
	}

	@Test
	public void shouldGetAMessageAndSendItToMessEngine() throws Exception {
		AllMessage<String> message = new AllMessage<String>("aaa", "bbb");
		String json = JsonConverter.toJson(message);
		when(restTemplate.getForObject(url, String.class, USER_ID)).thenReturn(json);
		task.run();
		Message<?> currentMessage = messEngine.getCurrentMessage();
		assertNotNull(currentMessage);
		assertEquals("aaa", currentMessage.getType());
		assertEquals("bbb", currentMessage.getBody());
		// verify we re-execute the task
		verify(executorService).execute(task);
	}

	@Test
	public void shouldNotBreakIfNoMessageReceived() throws Exception {
		task.run();
		verify(messEngine, never()).send(any(Message.class));
		verify(executorService).execute(task);

	}
}
