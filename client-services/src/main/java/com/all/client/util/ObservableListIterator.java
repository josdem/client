package com.all.client.util;

import java.util.ListIterator;

public class ObservableListIterator<T> extends ObservableIterator<T> implements ListIterator<T> {

	private final ListObserver<T> observer;
	private final ListIterator<T> iterator;

	public ObservableListIterator(ListIterator<T> iterator, ListObserver<T> observer) {
		super(iterator, observer);
		this.iterator = iterator;
		this.observer = observer;
	}

	@Override
	public boolean hasPrevious() {
		return iterator.hasPrevious();
	}

	@Override
	public int nextIndex() {
		return iterator.nextIndex();
	}

	@Override
	public T previous() {
		currentElement = iterator.previous();
		return currentElement;
	}

	@Override
	public int previousIndex() {
		return iterator.previousIndex();
	}

	@Override
	public void set(T e) {
		iterator.set(e);
		observer.onSet(currentElement, e);
	}

	@Override
	public void add(T e) {
		iterator.add(e);
		observer.onAdd(e, true);
	}
}
