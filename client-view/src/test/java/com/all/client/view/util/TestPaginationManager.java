package com.all.client.view.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.all.observ.ObserveObject;
import com.all.observ.Observer;

public class TestPaginationManager {
	private PaginationManager<String> manager = new PaginationManager<String>();

	@Before
	public void setup() {
		List<String> elements = new ArrayList<String>();
		elements.add("a");
		elements.add("b");

		elements.add("c");
		elements.add("d");

		elements.add("e");
		elements.add("f");

		elements.add("g");
		elements.add("h");

		elements.add("i");
		assertEquals(Collections.EMPTY_LIST, manager.getCurrentElements());
		manager.setCurrentPage(0);
		manager.setPageSize(2);
		manager.setElementSize(elements.size());
		manager.setPageSource(new ListPageSource<String>(elements));
	}

	@Test
	public void shouldGetTheItemsFromAPaginationSource() throws Exception {
		assertEquals(0, manager.getCurrentPage());
		assertEquals(5, manager.getPageCount());
		assertList(manager.getCurrentElements(), "a", "b");
	}

	@Test
	public void shouldMoveAPageForward() throws Exception {
		manager.nextPage();
		assertEquals(1, manager.getCurrentPage());
		assertEquals(5, manager.getPageCount());
		assertList(manager.getCurrentElements(), "c", "d");
	}

	@Test
	public void shouldJumpToPage() throws Exception {
		manager.setCurrentPage(3);
		assertEquals(3, manager.getCurrentPage());
		assertEquals(5, manager.getPageCount());
		assertList(manager.getCurrentElements(), "g", "h");
	}

	@Test
	public void shouldGoBackOnePage() throws Exception {
		shouldJumpToPage();
		manager.previousPage();
		assertEquals(2, manager.getCurrentPage());
		assertEquals(5, manager.getPageCount());
		assertList(manager.getCurrentElements(), "e", "f");
	}

	@Test
	public void shouldNotGoBeforePage0() throws Exception {
		manager.previousPage();
		shouldGetTheItemsFromAPaginationSource();
	}

	@Test
	public void shouldNotGoAfterLastPage() throws Exception {
		manager.setCurrentPage(4);

		assertEquals(4, manager.getCurrentPage());
		assertEquals(5, manager.getPageCount());
		assertList(manager.getCurrentElements(), "i");

		manager.nextPage();

		assertEquals(4, manager.getCurrentPage());
		assertEquals(5, manager.getPageCount());
		assertList(manager.getCurrentElements(), "i");
	}

	@Test
	public void shouldIgnoreGoToPageThatIsNotInRange() throws Exception {
		manager.setCurrentPage(-20);
		shouldGetTheItemsFromAPaginationSource();
		manager.setCurrentPage(-1);
		shouldGetTheItemsFromAPaginationSource();
		manager.setCurrentPage(5);
		shouldGetTheItemsFromAPaginationSource();
		manager.setCurrentPage(20);
		shouldGetTheItemsFromAPaginationSource();
	}

	@Test
	public void shouldJumpToFirtsAndLastPage() throws Exception {
		manager.setCurrentPage(4);
		assertList(manager.getCurrentElements(), "i");
		manager.setCurrentPage(0);
		assertList(manager.getCurrentElements(), "a", "b");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldLazylyAskForItems() throws Exception {
		PageSource mock = mock(PageSource.class);
		manager.setPageSource(mock);
		manager.getCurrentElements();
		manager.getCurrentElements();
		manager.getCurrentElements();
		verify(mock).getElements(0, 2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldLaunchEventIfPageChanges() throws Exception {
		Observer mock = mock(Observer.class);
		manager.onPageChange().add(mock);
		manager.setPageSize(2);
		verify(mock).observe(any(ObserveObject.class));
	}

	private void assertList(List<String> currentElements, String... strings) {
		if (strings == null && !currentElements.isEmpty()) {
			fail("LIST EXPECTED IS BAD " + currentElements.size() + " 0");
		}
		if (currentElements.size() != strings.length) {
			fail("LIST EXPECTED IS BAD " + currentElements.size() + " " + strings.length);
		}
		for (int i = 0; i < strings.length; i++) {
			assertEquals(strings[i], currentElements.get(i));
		}
	}
}
