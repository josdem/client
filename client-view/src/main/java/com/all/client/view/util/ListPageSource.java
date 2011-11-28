package com.all.client.view.util;

import java.util.ArrayList;
import java.util.List;

public class ListPageSource<T> implements PageSource<T> {

	private final List<T> elements;

	public ListPageSource(List<T> elements) {
		this.elements = new ArrayList<T>(elements);
	}

	@Override
	public List<T> getElements(int initialIndex, int finalIndex) {
		ArrayList<T> list = new ArrayList<T>(finalIndex - initialIndex);
		for (int i = initialIndex; i < finalIndex; i++) {
			if (i >= elements.size()) {
				break;
			}
			list.add(elements.get(i));
		}
		return list;
	}
}
