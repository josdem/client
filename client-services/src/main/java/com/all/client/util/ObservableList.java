package com.all.client.util;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class ObservableList<T> extends ObservableCollection<T> implements List<T> {
	private final List<T> list;
	private final ListObserver<T> observer;

	public ObservableList(List<T> list, ListObserver<T> observer) {
		super(list, observer);
		this.list = list;
		this.observer = observer;
	}

	@Override
	public void add(int index, T element) {
		list.add(index, element);
		observer.onAdd(element, true);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		boolean addAll = list.addAll(index, c);
		observer.onAddAll(c, addAll);
		return addAll;
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ObservableListIterator<T>(list.listIterator(), observer);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ObservableListIterator<T>(list.listIterator(index), observer);
	}

	@Override
	public T remove(int index) {
		T remove = list.remove(index);
		observer.onRemove(remove, true);
		return remove;
	}

	@Override
	public T set(int index, T element) {
		T set = list.set(index, element);
		observer.onSet(set, element);
		return set;
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}
}
