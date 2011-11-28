package com.all.client.view.i18n;

import javax.swing.JComponent;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class Ji18nTooltip<T extends JComponent> implements Internationalizable {
	private final T component;
	private String messageKey;
	private Object[] messageParameters;
	private Messages messages;

	public Ji18nTooltip(T component) {
		this.component = component;
	}

	public void setTooltipMessage(String messageKey, String... messageParameters) {
		this.messageKey = messageKey;
		this.messageParameters = messageParameters;
		internationalize(messages);

	}

	@Override
	public void internationalize(Messages messages) {
		if (messages != null) {
			if (messageParameters == null || messageParameters.length == 0) {
				component.setToolTipText(messages.getMessage(messageKey));
			} else {
				component.setToolTipText(messages.getMessage(messageKey, messageParameters));
			}
		}
	}

	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		this.messages = null;
	}

	public T getComponent() {
		return component;
	}
}
