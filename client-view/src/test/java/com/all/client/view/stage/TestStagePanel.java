package com.all.client.view.stage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.browser.AllBrowser;

public class TestStagePanel {
	private static final String FACEBOOK_URL = "www.facebook.com";

	private static final String YOUTUBE_URL = "www.youtube.com";

	@InjectMocks
	StagePanel stagePanel = new StagePanel();
	
	@Mock 
	private AllBrowser youtubeBrowser;
	@Mock
	private AllBrowser facebookBrowser;
	@Spy
	private JPanel youtubePanel = new JPanel();
	@Spy
	private JPanel facebookPanel = new JPanel();
	
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		when(youtubeBrowser.getPanel()).thenReturn(youtubePanel);
		when(facebookBrowser.getPanel()).thenReturn(facebookPanel);
		stagePanel.setup();
	}
	
	@Test
	public void shouldShowYoutubeBrowserPanel() throws Exception {
		stagePanel.loadUrl(YOUTUBE_URL);
		assertTrue(youtubePanel.isVisible());
		assertFalse(facebookPanel.isVisible());
	}
	
	@Test
	public void shouldShowFacebookBrowserPanel() throws Exception {
		stagePanel.loadUrl(FACEBOOK_URL);
		assertTrue(facebookPanel.isVisible());
		assertFalse(youtubePanel.isVisible());
	}
	
}
