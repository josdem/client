package com.all.client.util;

import java.util.Collection;

public interface CollectionObserver<T> {
	void onRemove(Object e, boolean success);

	void onAdd(T e, boolean success);

	void onAddAll(Collection<? extends T> e, boolean success);

	void onClear();

	void onRetainAll(Collection<?> c, boolean success);

	void onRemoveAll(Collection<?> c, boolean success);
}
