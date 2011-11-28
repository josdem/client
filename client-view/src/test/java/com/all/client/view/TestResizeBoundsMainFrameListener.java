package com.all.client.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.all.client.SimpleGUITest;

public class TestResizeBoundsMainFrameListener extends SimpleGUITest {
	@Test
	public void shouldSetPositionOfElementsBelongingToLayoutNull() throws Exception {
		MainPanel mainPanel = new MainPanel();
		MainFrame mainFrame = new MainFrame(mainPanel, messages);
		assertEquals(800, mainFrame.getWidth());
		assertEquals(600, mainFrame.getHeight());
		mainFrame.setSize(mainFrame.getMinimumSize());
	}
}
