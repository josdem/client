package com.all.client.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.core.events.Events;
import com.all.downloader.search.SearchDataEvent;
import com.all.downloader.search.SearchErrorEvent;
import com.all.downloader.search.SearchException;
import com.all.downloader.search.SearchProgressEvent;
import com.all.downloader.search.Searcher;

public class SearchControllerTest {

	@InjectMocks
	private SearchController searchController = new SearchController();
	@Mock
	private Searcher searcher;
	@Mock
	private ControlEngine controlEngine;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAddAsListenerOnInitialize() throws Exception {
		searchController.intialize();
		verify(searcher).addSearcherListener(searchController);
	}

	@Test
	public void shouldDelegateSeearchAction() throws Exception {
		String keyword = "keyword";
		searchController.search(keyword);
		verify(searcher).search(keyword);

		// raise coverage
		doThrow(new SearchException("")).when(searcher).search(keyword);
		searchController.search(keyword);
	}

	@Test
	public void shouldFireEventOnUpdateSearchData() throws Exception {
		SearchDataEvent updateSearchEvent = mock(SearchDataEvent.class);
		searchController.updateSearchData(updateSearchEvent);
		verify(controlEngine).fireValueEvent(Events.Search.DATA_UPDATED, updateSearchEvent);
	}

	@Test
	public void shouldFireEventOnUpdateProgress() throws Exception {
		SearchProgressEvent searchProgressEvent = mock(SearchProgressEvent.class);
		searchController.updateProgress(searchProgressEvent);
		verify(controlEngine).fireValueEvent(Events.Search.PROGRESS, searchProgressEvent);
	}

	@Test
	public void shouldFireEventOnError() throws Exception {
		SearchErrorEvent searchErrorEvent = mock(SearchErrorEvent.class);
		searchController.onError(searchErrorEvent);
		verify(controlEngine).fireValueEvent(Events.Search.ERROR, searchErrorEvent);
	}
}
