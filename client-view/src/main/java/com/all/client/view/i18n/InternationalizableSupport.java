package com.all.client.view.i18n;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class InternationalizableSupport implements Internationalizable, InternationalizableComponent {
	private String messageKey;
	private Object[] messageParameters;
	private Messages messages;
	private final InternationalizableComponent component;

	public InternationalizableSupport(InternationalizableComponent component) {
		this.component = component;
	}

	@Override
	public void internationalize(Messages messages) {
		if (messageParameters != null && messageParameters.length > 0) {
			this.setText(messages.getMessage(messageKey, messageParameters));
		} else {
			this.setText(messages.getMessage(messageKey));
		}
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		this.messages = null;
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		this.messages = messages;
	}

	@Override
	public String getText() {
		return component.getText();
	}

	@Override
	public void setMessage(String key, String... parameters) {
		this.messageKey = key;
		this.messageParameters = parameters;
		if (messages != null) {
			internationalize(messages);
		}
	}

	@Override
	public void setText(String text) {
		component.setText(text);
	}
}
