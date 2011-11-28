package com.all.client.util;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;

public class TestObservableCollection extends UnitTestCase {
	@Mock
	private CollectionObserver<String> observer;
	@Mock
	private Collection<String> collection;

	private Collection<String> observableCollection;

	List<String> temp = new ArrayList<String>();
	String e = "a";

	@Before
	public void init() {
		observableCollection = new ObservableCollection<String>(collection, observer);
	}

	@Test
	public void shouldAdd() throws Exception {
		observableCollection.add(e);
		verify(collection).add(e);
		verify(observer).onAdd(e, false);
	}

	@Test
	public void shouldAddAll() throws Exception {
		observableCollection.addAll(temp);
		verify(collection).addAll(temp);
		verify(observer).onAddAll(temp, false);
	}

	@Test
	public void shouldClear() throws Exception {
		observableCollection.clear();
		verify(collection).clear();
		verify(observer).onClear();
	}

	@Test
	public void shouldRemove() throws Exception {
		observableCollection.remove(e);
		verify(collection).remove(e);
		verify(observer).onRemove(e, false);
	}

	@Test
	public void shouldRemoveAll() throws Exception {
		observableCollection.removeAll(temp);
		verify(collection).removeAll(temp);
		verify(observer).onRemoveAll(temp, false);
	}

	@Test
	public void shouldRetainAll() throws Exception {
		observableCollection.retainAll(temp);
		verify(collection).retainAll(temp);
		verify(observer).onRetainAll(temp, false);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldNotModifyCollection() throws Exception {
		String[] array = new String[] {};

		observableCollection.contains(e);
		verify(collection).contains(e);

		observableCollection.containsAll(temp);
		verify(collection).containsAll(temp);

		observableCollection.isEmpty();
		verify(collection).isEmpty();

		observableCollection.iterator();
		verify(collection).iterator();

		observableCollection.size();
		verify(collection).size();

		observableCollection.toArray();
		verify(collection).toArray();

		observableCollection.toArray(array);
		verify(collection).toArray(array);

		verify(observer, never()).onAdd(isA(String.class), anyBoolean());
		verify(observer, never()).onAddAll(isA(Collection.class), anyBoolean());
		verify(observer, never()).onClear();
		verify(observer, never()).onRemove(isA(Collection.class), anyBoolean());
		verify(observer, never()).onRemoveAll(isA(Collection.class), anyBoolean());
		verify(observer, never()).onRetainAll(isA(Collection.class), anyBoolean());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldTestTheIterator() throws Exception {
		Iterator mockIterator = mock(Iterator.class);
		when(collection.iterator()).thenReturn(mockIterator);
		Iterator iterator = observableCollection.iterator();
		assertNotNull(iterator);
		iterator.next();
		verify(mockIterator).next();
		iterator.hasNext();
		verify(mockIterator).hasNext();
		iterator.remove();
		verify(mockIterator).remove();
		verify(observer).onRemove(null, true);
	}

}
