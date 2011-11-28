package com.all.client.view.dnd;

import java.awt.Component;

public interface DoInListeners<V, K> {
	V doIn(K listener, Component component, V lastResult);
	void done();
}
