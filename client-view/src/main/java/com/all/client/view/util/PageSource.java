package com.all.client.view.util;

import java.util.List;

public interface PageSource<T> {
	List<T> getElements(int initialIndex, int finalIndex);
}