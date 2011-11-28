package com.all.core.common.observables;

import com.all.observ.ObserveObject;

public class ConnectionChangedEvent extends ObserveObject {

	private static final long serialVersionUID = 1L;
	private final boolean connected;

	public ConnectionChangedEvent(boolean connected) {
		this.connected = connected;
	}

	public boolean isConnected() {
		return connected;
	}

}
