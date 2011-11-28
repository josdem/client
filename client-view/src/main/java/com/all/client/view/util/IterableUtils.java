package com.all.client.view.util;

import java.util.LinkedList;
import java.util.List;

public final class IterableUtils {
	
	private IterableUtils() {
		
	}
	
	public static <T> List<T> toList(Iterable<T> iterable) {
		List<T> list = new LinkedList<T>();
		for (T t : iterable) {
			list.add(t);
		}
		return list;
	}
}
