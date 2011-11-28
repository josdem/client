package com.all.client.view.observs;

import java.util.EventObject;

public class ApplicationCloseEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private boolean cancel = false;

	public ApplicationCloseEvent(Object source) {
		super(source);
	}

	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isCancel() {
		return cancel;
	}

}
