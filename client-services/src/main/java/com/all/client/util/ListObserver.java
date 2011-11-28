package com.all.client.util;

public interface ListObserver<T> extends CollectionObserver<T> {

	void onSet(T oldElement, T newElement);

}
