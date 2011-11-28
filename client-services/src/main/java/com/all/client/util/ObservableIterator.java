package com.all.client.util;

import java.util.Iterator;

public class ObservableIterator<T> implements Iterator<T> {

	private final Iterator<T> iterator;
	private final CollectionObserver<T> observer;
	protected T currentElement;

	public ObservableIterator(Iterator<T> iterator, CollectionObserver<T> observer) {
		this.iterator = iterator;
		this.observer = observer;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public T next() {
		currentElement = iterator.next();
		return currentElement;
	}

	@Override
	public void remove() {
		iterator.remove();
		observer.onRemove(currentElement, true);
	}

}
