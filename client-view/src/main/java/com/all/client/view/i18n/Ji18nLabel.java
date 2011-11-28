package com.all.client.view.i18n;

import javax.swing.JLabel;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class Ji18nLabel extends JLabel implements Internationalizable, InternationalizableComponent {
	private static final long serialVersionUID = 1L;
	private InternationalizableSupport i18nSupport = new InternationalizableSupport(this);

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
