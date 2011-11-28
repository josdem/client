package com.all.client.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.controller.beans.FeedsRequest;
import com.all.client.services.ContactCacheService;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.EngineEnabled;
import com.all.core.actions.Actions;
import com.all.core.common.spring.InitializeService;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.FeedsResponse;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.ContactInfo;
import com.all.shared.newsfeed.AllFeed;

@Controller
@EngineEnabled
public class FeedsController {

	private static final int INITIAL_DELAY = 1;

	private static final TimeUnit DELAY_UNIT = TimeUnit.MINUTES;

	private static final int DELAY = 5;

	@Autowired
	private ControlEngine controlEngine;

	@Autowired
	private MessEngine messEngine;

	@Autowired
	private ContactCacheService contactCacheService;

	static private final ScheduledExecutorService monitor = Executors
					.newSingleThreadScheduledExecutor(getThreadFactory());
	private Runnable task;

	private final Log log = LogFactory.getLog(this.getClass());

	private HashMap<Long, Set<AllFeed>> feedsCache = new HashMap<Long, Set<AllFeed>>();

	private Long userId;

	public FeedsController() {
	}

	@ActionMethod(Actions.Feeds.REQUEST_FEEDS_ID)
	public void retrieveFeeds() {
		ContactInfo contact = controlEngine.get(Model.CURRENT_PROFILE).getContact();
		this.userId = contact.getId();
		if (feedsCache.containsKey(contact.getId())) {
			controlEngine.fireValueEvent(Events.Feeds.NEW_FEEDS, feedsCache.get(contact.getId()));

			HashMap<String, Long> map = new HashMap<String, Long>();
			map.put("userId", this.userId);
			Set<AllFeed> feeds = feedsCache.get(this.userId);
			map.put("timestamp", getLastTimestamp(feeds));
			this.messEngine.send(new AllMessage<HashMap<String, Long>>(MessEngineConstants.LAST_FEED_REQUEST, map));
		} else {
			this.messEngine.send(new AllMessage<Long>(MessEngineConstants.FEEDS_REQUEST, this.userId));
		}
	}

	private long getLastTimestamp(Set<AllFeed> feeds) {
		return ((TreeSet<AllFeed>)feeds).last().getDate().getTime();
	}

	@SuppressWarnings("unchecked")
	@InitializeService
	public void setup() {
		try {
			this.userId = controlEngine.get(Model.CURRENT_USER).getId();
			AllMessage<Long> message = new AllMessage<Long>(MessEngineConstants.FEEDS_LOCAL_REQUEST, userId);
			Future<Message<?>> responseFuture = messEngine.request(message, MessEngineConstants.FEEDS_LOCAL_RESPONSE,
							30000L);
			Message<?> response;
			response = responseFuture.get(30, TimeUnit.SECONDS);
			FeedsResponse feedsResponse = (FeedsResponse) response.getBody();

			List feeds = feedsResponse.getFeeds();
			decorateFeeds(feeds);
			if (feedsResponse.getFeeds().size() > 0) {
				addToCache(feedsResponse.getOwnerId(), feeds);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		monitor.scheduleWithFixedDelay(getTask(), INITIAL_DELAY, DELAY, DELAY_UNIT);
	}

	@MessageMethod(MessEngineConstants.FEEDS_RESPONSE)
	public void handleRetrieveFeeds(AllMessage<FeedsResponse> message) {
		FeedsResponse response = message.getBody();
		List<AllFeed> feeds = response.getFeeds();
		decorateFeeds(feeds);
		if (response.getFeeds().size() > 0) {
			addToCache(response.getOwnerId(), feeds);
		}
		if (response.getOwnerId().equals(this.userId)) {
			controlEngine.fireValueEvent(Events.Feeds.NEW_FEEDS, new TreeSet<AllFeed>(feeds));
		}
		controlEngine.fireValueEvent(Events.Feeds.UPDATE_FEED_TIME_VIEW, response.getCurrentServerTime());
	}

	private void decorateFeeds(List<AllFeed> feeds) {
		AllFeed feed = null;
		for (int i = 0; i < feeds.size(); i++) {
			feed = feeds.get(i);
			for (ContactInfo contactInfo : feed.contacts()) {
				contactCacheService.decorate(contactInfo);
			}
		}
	}

	void addToCache(Long ownerId, List<AllFeed> sortFeeds) {
		if (feedsCache.containsKey(ownerId)) {
			Set<AllFeed> list = feedsCache.get(ownerId);
			list.addAll(sortFeeds);
		} else {
			feedsCache.put(ownerId, new TreeSet<AllFeed>(sortFeeds));
		}
	}

	@PreDestroy
	public void shutdown() {
		log.info("Feed monitor is shutting down now...");
		monitor.shutdownNow();
	}

	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void onUserLogout() {
		log.info("Feed monitor is going down...");
		monitor.shutdown();
	}

	private Runnable getTask() {
		task = new Runnable() {

			@Override
			public void run() {
				try {
					requestLastFeeds();
				} catch (Exception e) {
					log.error("Unexpected exception when getting feeds: " + e.getStackTrace());
				}
			}

		};
		return task;
	}

	@ActionMethod(Actions.Feeds.REQUEST_LAST_FEEDS_ID)
	private void requestLastFeeds() {
		HashMap<String, Long> map = new HashMap<String, Long>();
		map.put("userId", this.userId);
		if (feedsCache.containsKey(userId)) {
			Set<AllFeed> feeds = feedsCache.get(this.userId);
			map.put("timestamp", getLastTimestamp(feeds));
		} else {
			map.put("timestamp", 0L);
		}
		messEngine.send(new AllMessage<HashMap<String, Long>>(MessEngineConstants.LAST_FEED_REQUEST, map));
	}

	@SuppressWarnings("unchecked")
	@RequestMethod(Actions.Feeds.REQUEST_OLD_FEEDS_ID)
	public synchronized FeedsResponse requestOldFeeds(Long lastId) throws Exception {
		try {
			FeedsRequest request = new FeedsRequest(lastId, userId);
			AllMessage<FeedsRequest> message = new AllMessage<FeedsRequest>(MessEngineConstants.OLD_FEEDS_REQUEST,
							request);
			Future<Message<?>> responseFuture = messEngine.request(message, MessEngineConstants.OLD_FEEDS_RESPONSE,
							30000L);
			Message<?> response = responseFuture.get(30, TimeUnit.SECONDS);
			FeedsResponse feedResponse = (FeedsResponse) response.getBody();
			for (AllFeed feed : feedResponse.getFeeds()) {
				for (ContactInfo contactInfo : feed.contacts()) {
					contactCacheService.decorate(contactInfo);
				}
			}
			return feedResponse;
		} catch (Exception e) {
			log.error(e, e);
		}
		return new FeedsResponse(Collections.EMPTY_LIST, userId);
	}

	private static IncrementalNamedThreadFactory getThreadFactory() {
		return new IncrementalNamedThreadFactory("feedControllerThread");
	}

}
