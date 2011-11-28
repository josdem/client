package com.all.client.view.stage;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.browser.AllBrowser;

@Component
public class StagePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	@Autowired(required = false)
	private AllBrowser youtubeBrowser;
	@Autowired(required = false)
	private AllBrowser facebookBrowser;

	private boolean youtubeVeryFirstTime = true;
	private boolean facebookVeryFirstTime = true;

	private Map<String, JPanel> browserPanels = new HashMap<String, JPanel>();

	@PostConstruct
	public void setup() {
		browserPanels.put("youtubeBrowserPanel", youtubeBrowser.getPanel());
		browserPanels.put("facebookBrowserPanel", facebookBrowser.getPanel());
		setLayout(new CardLayout(0, 0));

		addPanels();
	}

	private void addPanels() {
		Iterator<Entry<String, JPanel>> iterator = browserPanels.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, JPanel> componentNode = iterator.next();
			this.add(componentNode.getValue(), componentNode.getKey());
		}
	}

	public void loadUrl(String url) {
		if (url.contains("youtube")) {
			if (youtubeVeryFirstTime) {
				youtubeBrowser.loadUrl(url);
				youtubeVeryFirstTime = false;
			}
			switchVisiblePanel("youtubeBrowserPanel");
		} else if (url.contains("facebook")) {
			if (facebookVeryFirstTime) {
				facebookBrowser.loadUrl(url);
				facebookVeryFirstTime = false;
			}
			switchVisiblePanel("facebookBrowserPanel");
		}
	}

	private void switchVisiblePanel(String string) {
		CardLayout cl = (CardLayout) (this.getLayout());
		cl.show(this, string);
	}

}
