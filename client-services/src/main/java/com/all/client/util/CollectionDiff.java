package com.all.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionDiff<T> {
	private List<T> added = new ArrayList<T>();
	private List<T> removed = new ArrayList<T>();

	public CollectionDiff(Collection<T> listA, Collection<T> listB) {
		for (T t : listA) {
			if (!listB.contains(t)) {
				removed.add(t);
			}
		}
		for (T t : listB) {
			if (!listA.contains(t)) {
				added.add(t);
			}
		}
	}

	public List<T> getAdded() {
		return added;
	}

	public List<T> getRemoved() {
		return removed;
	}

}
