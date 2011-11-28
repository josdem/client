package com.all.client.view.util;

import junit.framework.AssertionFailedError;

public class ColorsException extends AssertionFailedError {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "Colors are not contained in this thing";
	}

}
