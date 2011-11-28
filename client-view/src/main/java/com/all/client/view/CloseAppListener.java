package com.all.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.CloseFlow;

public class CloseAppListener implements ActionListener {

	private final ViewEngine viewEngine;
	private final DialogFactory dialogFactory;

	public CloseAppListener(ViewEngine viewEngine, DialogFactory dialogFactory) {
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new CloseFlow(viewEngine, dialogFactory).close();
	}
}
