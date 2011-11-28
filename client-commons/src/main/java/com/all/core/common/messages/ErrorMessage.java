package com.all.core.common.messages;

import com.all.messengine.Message;
import com.all.shared.model.AllMessage;

public class ErrorMessage implements Message<AllMessage<?>> {

	private static final String ERROR = "*_*:(ERROR:(*_*";
	private final AllMessage<?> message;

	public ErrorMessage(AllMessage<?> message) {
		this.message = message;
	}

	@Override
	public AllMessage<?> getBody() {
		return message;
	}

	@Override
	public String getProperty(String key) {
		return message.getProperty(key);
	}

	@Override
	public String getType() {
		return getType(message.getType());
	}

	@Override
	public void putProperty(String key, String value) {
		message.putProperty(key, value);
	}

	public static String getType(String type) {
		return type + ERROR;
	}

}
