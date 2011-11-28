package com.all.client.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.core.actions.Actions;
import com.all.core.common.spring.InitializeService;
import com.all.core.events.Events;
import com.all.downloader.search.SearchDataEvent;
import com.all.downloader.search.SearchErrorEvent;
import com.all.downloader.search.SearchException;
import com.all.downloader.search.SearchProgressEvent;
import com.all.downloader.search.Searcher;
import com.all.downloader.search.SearcherListener;

@Controller
public class SearchController implements SearcherListener {
	
	private static final Log LOG = LogFactory.getLog(SearchController.class); 

	@Qualifier("searcherManager")
	@Autowired
	private Searcher searcher;
	@Autowired
	private ControlEngine controlEngine;
	
	@InitializeService
	public void intialize() {
		searcher.addSearcherListener(this);
	}
	
	@ActionMethod(Actions.Downloads.SEARCH_ID)
	public void search(String keyword) {
		try {
			searcher.search(keyword);
		} catch (SearchException e) {
			LOG.error("Unable to perform search", e);
		}
	}

	@Override
	public void updateSearchData(SearchDataEvent updateSearchEvent) {
		controlEngine.fireValueEvent(Events.Search.DATA_UPDATED, updateSearchEvent);
	}

	@Override
	public void updateProgress(SearchProgressEvent updateProgressEvent) {
		controlEngine.fireValueEvent(Events.Search.PROGRESS, updateProgressEvent);
	}

	@Override
	public void onError(SearchErrorEvent searchErrorEvent) {
		controlEngine.fireValueEvent(Events.Search.ERROR, searchErrorEvent);
	}
	
}
