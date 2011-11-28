package com.all.client.view.dialog;

import java.awt.Dialog;
import java.awt.Frame;

import com.all.i18n.Messages;

public abstract class SocialNetworkAuthenticationDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	public SocialNetworkAuthenticationDialog(Dialog dialog, Messages messages) {
		super(dialog, messages);
	}

	public SocialNetworkAuthenticationDialog(Frame frame, Messages messages) {
		super(frame, messages);
		initializeContentPane();
		internationalizeDialog(messages);
	}
}