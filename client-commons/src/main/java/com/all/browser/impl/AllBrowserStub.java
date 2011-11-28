package com.all.browser.impl;

import javax.swing.JPanel;

import com.all.browser.AllBrowser;

public class AllBrowserStub implements AllBrowser {

	@Override
	public JPanel getPanel() {
		return new JPanel();
	}

	@Override
	public void loadUrl(String url) {
	}

	@Override
	public void refresh() {
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public String getUrl() {
		return null;
	}

}
