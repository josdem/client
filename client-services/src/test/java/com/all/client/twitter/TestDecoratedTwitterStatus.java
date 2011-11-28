package com.all.client.twitter;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.model.DecoratedTwitterStatus;
import com.all.twitter.TwitterStatus;


public class TestDecoratedTwitterStatus {
	private DecoratedTwitterStatus decoratedTwitterStatus;
	@Mock
	private TwitterStatus twitterStatus;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		decoratedTwitterStatus = new DecoratedTwitterStatus(twitterStatus, "screenName");
	}

	@Test
	public void shouldGetDirectMessages() throws Exception {
		decoratedTwitterStatus.isDirect();
		verify(twitterStatus).isDirect();
	}
}
