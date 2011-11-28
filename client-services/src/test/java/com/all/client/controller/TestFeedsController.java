package com.all.client.controller;

import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.appControl.control.ControlEngine;
import com.all.client.services.ContactCacheService;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.messengine.impl.StubMessEngine;
import com.all.shared.messages.FeedsResponse;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.model.User;
import com.all.shared.newsfeed.AllFeed;
import com.all.shared.newsfeed.RemoteLibraryBrowsingFeed;

public class TestFeedsController {

	@InjectMocks
	private FeedsController feedsController = new FeedsController();
	
	@Spy
	private StubMessEngine stubEngine = new StubMessEngine();
	
	private AllMessage<FeedsResponse> feedsResponseMessage;
	
	@Mock
	private ContactCacheService contactCacheService;
	
	@Mock
	private ControlEngine controlEngine;
	
	@Mock
	private User user;
	
	private static final long userId = 3L;
	@SuppressWarnings("deprecation")
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		stubEngine.setup(feedsController);
		
		ContactInfo owner = new ContactInfo();
		ContactInfo visited = new ContactInfo();
		owner.setGender(Gender.FEMALE);
		owner.setNickName("nickname");
		visited.setGender(Gender.FEMALE);
		visited.setNickName("nickname");
		RemoteLibraryBrowsingFeed RLBFeed = new RemoteLibraryBrowsingFeed(owner, visited);
		
		List<AllFeed> feeds = new ArrayList<AllFeed>();
		feeds.add(RLBFeed);
		FeedsResponse feedsResponse = new FeedsResponse(feeds, userId);
		feedsResponse.setCurrentServerTime(new Date());
		feedsResponseMessage = new AllMessage<FeedsResponse>(MessEngineConstants.FEEDS_RESPONSE, feedsResponse);
	    
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
		when(user.getId()).thenReturn(userId);
		
		feedsController.setup();
	}
	
	@Test
	public void handleRetrieveFeeds() throws Exception {
		stubEngine.send(feedsResponseMessage);
		verify(contactCacheService, atLeastOnce()).decorate(isA(ContactInfo.class));
		verify(controlEngine).fireValueEvent(eq(Events.Feeds.NEW_FEEDS), anySetOf(AllFeed.class));
		verify(controlEngine).fireValueEvent(eq(Events.Feeds.UPDATE_FEED_TIME_VIEW), isA(Date.class));
	}
}
