package com.all.client.view.wizard;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.all.browser.AllBrowser;

public class WizardBrowserPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String BROWSER_PANEL_NAME = "BROWSER";

	private AllBrowser demoBrowser;

	public WizardBrowserPanel(AllBrowser demoBrowser) {
		this.demoBrowser = demoBrowser;
		initialize();
	}

	private void initialize() {
		this.setLayout(new CardLayout());
		JPanel browserPanel = demoBrowser.getPanel();
		this.add(browserPanel, BROWSER_PANEL_NAME);
	}

}
