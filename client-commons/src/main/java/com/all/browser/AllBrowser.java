package com.all.browser;

import javax.swing.JPanel;

public interface AllBrowser {

	JPanel getPanel();

	void loadUrl(String url);
	
	String getUrl();

	void refresh();
	
	boolean isInitialized();
	
}
