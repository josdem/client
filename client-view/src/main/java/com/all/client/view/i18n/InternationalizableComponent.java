package com.all.client.view.i18n;

public interface InternationalizableComponent {
	void setMessage(String key, String... parameters);

	void setText(String text);

	String getText();
}
