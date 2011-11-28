package com.all.client.view.i18n;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public abstract class InternationalizableWrapper<T> implements InternationalizableComponent, Internationalizable {
	private T wrappedComponent;
	private InternationalizableSupport i18nSupport = new InternationalizableSupport(this);

	public InternationalizableWrapper(T wrappedComponent) {
		this.wrappedComponent = wrappedComponent;
	}

	public abstract String getText();

	public abstract void setText(String text);

	public T getWrappedComponent() {
		return wrappedComponent;
	}

	@Override
	public void internationalize(Messages messages) {
		i18nSupport.internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		i18nSupport.removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		i18nSupport.setMessages(messages);
	}

	@Override
	public void setMessage(String key, String... parameters) {
		i18nSupport.setMessage(key, parameters);
	}

}
