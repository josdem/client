package com.all.client.view.i18n;

import javax.swing.JComponent;

public interface InternationalizableComponentSource<T extends JComponent> {
	void setText(T component, String text);

	String getText(T component);
}
