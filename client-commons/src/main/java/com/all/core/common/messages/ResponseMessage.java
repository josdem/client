package com.all.core.common.messages;

import com.all.messengine.Message;
import com.all.shared.model.AllMessage;

public class ResponseMessage implements Message<AllMessage<?>> {
	public static final String RESPONSE_CODE = "RESPONSE_CODE";
	private static final String RESPONSE = "*_*:(RESPONSE:(*_*";
	private final AllMessage<?> message;

	public ResponseMessage(String responseCode, AllMessage<?> message) {
		this.message = message;
		this.putProperty(RESPONSE_CODE, responseCode);
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
		return type + RESPONSE;
	}
}
