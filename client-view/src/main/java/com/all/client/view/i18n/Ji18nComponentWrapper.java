package com.all.client.view.i18n;

import javax.swing.JComponent;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class Ji18nComponentWrapper<T extends JComponent> implements Internationalizable {

	private final T component;
	private final InternationalizableComponentSource<T> source;

	private String tooltipKey;
	private Object[] tooltipParameters;

	private String messageKey;
	private Object[] messageParameters;
	private Messages messages;

	public Ji18nComponentWrapper(T component, InternationalizableComponentSource<T> source) {
		this.component = component;
		this.source = source;
	}

	public void setTooltipMessage(String messageKey, String... messageParameters) {
		this.tooltipKey = messageKey;
		this.tooltipParameters = messageParameters;
		internationalize(messages);
	}

	public void setMessage(String messageKey, String... messageParameters) {
		this.messageKey = messageKey;
		this.messageParameters = messageParameters;
		internationalize(messages);
	}

	@Override
	public void internationalize(Messages messages) {
		if (messages != null) {
			if (messageParameters == null || messageParameters.length == 0) {
				source.setText(component, messages.getMessage(messageKey));
			} else {
				source.setText(component, messages.getMessage(messageKey, messageParameters));
			}
			if (tooltipParameters == null || tooltipParameters.length == 0) {
				component.setToolTipText(messages.getMessage(tooltipKey));
			} else {
				component.setToolTipText(messages.getMessage(tooltipKey, tooltipParameters));
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
