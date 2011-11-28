package com.all.client.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;

public class TestObservableList extends UnitTestCase {
	@Mock
	List<String> list;
	@Mock
	ListObserver<String> observer;
	@Mock
	Collection<String> temp;

	int index = 0;
	String s = "a";

	List<String> observableList;

	@Before
	public void init() {
		assertNotNull(observer);
		assertNotNull(list);
		observableList = new ObservableList<String>(list, observer);
		assertNotNull(observableList);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotModifyCollection() throws Exception {
		observableList.get(index);
		verify(list).get(index);

		observableList.indexOf(s);
		verify(list).indexOf(s);

		observableList.lastIndexOf(s);
		verify(list).lastIndexOf(s);

		observableList.listIterator();
		verify(list).listIterator();

		observableList.listIterator(index);
		verify(list).listIterator(index);

		observableList.subList(index, 1);
		verify(list).subList(index, 1);

		verify(observer, never()).onAdd(anyString(), anyBoolean());
		verify(observer, never()).onAddAll(isA(Collection.class), anyBoolean());
		verify(observer, never()).onClear();
		verify(observer, never()).onRemove(anyString(), anyBoolean());
		verify(observer, never()).onRemoveAll(isA(Collection.class), anyBoolean());
		verify(observer, never()).onRetainAll(isA(Collection.class), anyBoolean());
		verify(observer, never()).onSet(anyString(), anyString());
	}

	@Test
	public void shouldAdd() throws Exception {
		observableList.add(index, s);
		verify(list).add(index, s);
		verify(observer).onAdd(s, true);
	}

	@Test
	public void shouldAddAll() throws Exception {
		observableList.addAll(index, temp);
		verify(list).addAll(index, temp);
		verify(observer).onAddAll(temp, false);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldListIterator() throws Exception {
		ListIterator mockIterator = mock(ListIterator.class);
		when(list.listIterator()).thenReturn(mockIterator);
		ListIterator<String> iterator = observableList.listIterator();
		assertNotNull(iterator);

		iterator.hasPrevious();
		verify(mockIterator).hasPrevious();

		iterator.nextIndex();
		verify(mockIterator).nextIndex();

		iterator.previous();
		verify(mockIterator).previous();

		iterator.previousIndex();
		verify(mockIterator).previousIndex();

		iterator.add(s);
		verify(mockIterator).add(s);
		verify(observer).onAdd(s, true);

		iterator.set(s);
		verify(mockIterator).set(s);
		verify(observer).onSet(null, s);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldListIteratorIndex() throws Exception {
		ListIterator mockIterator = mock(ListIterator.class);
		when(list.listIterator(index)).thenReturn(mockIterator);
		ListIterator<String> iterator = observableList.listIterator(index);
		assertNotNull(iterator);

		iterator.hasPrevious();
		verify(mockIterator).hasPrevious();

		iterator.nextIndex();
		verify(mockIterator).nextIndex();

		iterator.previous();
		verify(mockIterator).previous();

		iterator.previousIndex();
		verify(mockIterator).previousIndex();

		iterator.add(s);
		verify(mockIterator).add(s);
		verify(observer).onAdd(s, true);

		iterator.set(s);
		verify(mockIterator).set(s);
		verify(observer).onSet(null, s);
	}

	@Test
	public void shouldRemove() throws Exception {
		observableList.remove(index);
		verify(list).remove(index);
		verify(observer).onRemove(anyObject(), eq(true));
	}

	@Test
	public void shouldSet() throws Exception {
		observableList.set(index, s);
		verify(list).set(index, s);
		verify(observer).onSet(null, s);
	}
}
