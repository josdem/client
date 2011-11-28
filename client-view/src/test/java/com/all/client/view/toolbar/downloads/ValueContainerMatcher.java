/**
 * 
 */
package com.all.client.view.toolbar.downloads;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class ValueContainerMatcher<T> extends BaseMatcher<T> {
	T value;

	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Object arg0) {
		this.value = (T) arg0;
		return true;
	}

	@Override
	public void describeTo(Description arg0) {
	}
}