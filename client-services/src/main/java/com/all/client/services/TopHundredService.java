package com.all.client.services;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.RequestMethod;
import com.all.core.actions.Actions;
import com.all.messengine.MessEngine;
import com.all.messengine.Message;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Category;
import com.all.shared.model.Playlist;

@Service
public class TopHundredService {
	@Autowired
	private MessEngine messEngine;

	private BlockingQueue<List<Category>> categories = new LinkedBlockingQueue<List<Category>>();

	@RequestMethod(Actions.TopHundred.getCategoriesId)
	public synchronized List<Category> getCategories(Void v) {
		if (categories.isEmpty()) {
			messEngine.send(new AllMessage<Void>(MessEngineConstants.TOP_HUNDRED_CATEGORY_LIST_REQUEST, null));
		}
		try {
			List<Category> take = categories.poll(30, TimeUnit.SECONDS);
			if (take == null) {
				return Collections.emptyList();
			} else {
				categories.offer(take);
				return take;
			}
		} catch (InterruptedException e) {
			return Collections.emptyList();
		}
	}

	@MessageMethod(MessEngineConstants.TOP_HUNDRED_CATEGORY_LIST_RESPONSE)
	public void onCategoryListResponse(Message<List<Category>> message) {
		this.categories.offer(message.getBody());
	}

	@RequestMethod(Actions.TopHundred.getPlaylistsFromCategoryAsyncId)
	public List<Playlist> getPlaylists(Category category) {
		return category.getPlaylists();
	}

	@RequestMethod(Actions.TopHundred.GET_RANDOM_PLAYLIST_ID)
	@SuppressWarnings("unchecked")
	public Playlist getRandomPlaylist() throws InterruptedException, ExecutionException {
		Future<Message<?>> future = messEngine.request(new AllMessage<Void>(
				MessEngineConstants.TOP_HUNDRED_RANDOM_PLAYLIST_REQUEST, null),
				MessEngineConstants.TOP_HUNDRED_RANDOM_PLAYLIST_RESPONSE, 30000);
		Message<Playlist> message = (Message<Playlist>) future.get();
		return message.getBody();
	}
}
