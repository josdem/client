package com.all.client.util;

import java.util.Collection;
import java.util.Iterator;

public class ObservableCollection<T> implements Collection<T> {

	private final CollectionObserver<T> observer;
	private final Collection<T> collection;

	public ObservableCollection(Collection<T> collection, CollectionObserver<T> observer) {
		this.collection = collection;
		this.observer = observer;
	}

	@Override
	public boolean add(T e) {
		boolean add = collection.add(e);
		observer.onAdd(e, add);
		return add;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean addAll = collection.addAll(c);
		observer.onAddAll(c, addAll);
		return addAll;
	}

	@Override
	public void clear() {
		collection.clear();
		observer.onClear();
	}

	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return new ObservableIterator<T>(collection.iterator(), observer);
	}

	@Override
	public boolean remove(Object o) {
		boolean remove = collection.remove(o);
		observer.onRemove(o, remove);
		return remove;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean removeAll = collection.removeAll(c);
		observer.onRemoveAll(c, removeAll);
		return removeAll;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean retainAll = collection.retainAll(c);
		observer.onRetainAll(c, retainAll);
		return retainAll;
	}

	@Override
	public int size() {
		return collection.size();
	}

	@Override
	public Object[] toArray() {
		return collection.toArray();
	}

	@Override
	public <V> V[] toArray(V[] a) {
		return collection.toArray(a);
	}

}
