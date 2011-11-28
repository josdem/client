package com.all.client.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.all.shared.model.AllMessage;

public class AllMessageMatcher extends BaseMatcher<Object> {

	private final Object body;
	private final String type;
	private Object actualBody;
	private String actualType;

	public AllMessageMatcher(String type, Object body) {
		this.type = type;
		this.body = body;
	}

	@Override
	public boolean matches(Object arg0) {
		if (arg0 instanceof AllMessage<?>) {
			AllMessage<?> message = (AllMessage<?>) arg0;
			actualType = message.getType();
			actualBody = message.getBody();
			return type.equals(actualType) && body.equals(actualBody);
		}
		return false;
	}

	@Override
	public void describeTo(Description arg0) {
		arg0.appendText("\nThe message content is incorrect.\nExpected content: " + body + "/Actual content: " + actualBody);
	}
}
