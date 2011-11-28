package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.junit.Before;
import org.junit.Test;

public class TestProgressBarPanel {

	private ProgressBarPanel progressBarPanel;

	@Before
	public void initTest() {
		progressBarPanel = new ProgressBarPanel();
	}


	@Test
	public void shouldHaveLabelIconMusic() throws Exception {
		JLabel mediaIconLabel = (JLabel) progressBarPanel.getComponent(2);
		assertNotNull(mediaIconLabel);
		assertEquals("syncMusicIcon", mediaIconLabel.getName());
	}

	@Test
	public void shouldHaveMusicTextPanel() throws Exception {
		JPanel mediaTextLabel = (JPanel) progressBarPanel.getComponent(4);
		assertNotNull(mediaTextLabel);
	}

	@Test
	public void shouldHaveAProgressBar() throws Exception {
		JSlider progressBar = (JSlider) progressBarPanel.getComponent(6);
		assertNotNull(progressBar);
		assertEquals("bigProgressBar", progressBar.getName());
	}

	@Test
	public void shouldHavePercentTextPanel() throws Exception {
		JPanel percentTextLabel = (JPanel) progressBarPanel.getComponent(8);
		assertNotNull(percentTextLabel);
	}

}
