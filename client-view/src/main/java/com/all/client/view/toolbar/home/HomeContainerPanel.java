package com.all.client.view.toolbar.home;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.MiddleCloseablePanel;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class HomeContainerPanel extends MiddleCloseablePanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private MainHomeScrollPane mainHomeScrollPane;

	private final DialogFactory dialogFactory;

	private JPanel mainPanel;

	public HomeContainerPanel(DialogFactory dialogFactory) {
		super();
		this.dialogFactory = dialogFactory;
	}
	
	private void init() {
		getMiddlePanel().add(getMainPanel(), BorderLayout.CENTER);
	}

	@Override
	protected JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getMainHomeScrollPane(), BorderLayout.CENTER);
		}
		return mainPanel;
	}

	private MainHomeScrollPane getMainHomeScrollPane() {
		if (mainHomeScrollPane == null) {
			mainHomeScrollPane = new MainHomeScrollPane(dialogFactory, getViewEngine());
		}
		return mainHomeScrollPane;
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(messages.getMessage("home.title"));
		mainHomeScrollPane.internationalize(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		mainHomeScrollPane.setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		mainHomeScrollPane.removeMessages(messages);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine);
		init();
		mainHomeScrollPane.initialize(viewEngine);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
		mainHomeScrollPane.destroy(viewEngine);
	}
}
