package com.all.client.view.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestChatMainPanel {
    @UnderTest
	private ChatMainPanel mainPanel;
    @Mock
    JFrame frame ;
    @Mock
    ViewEngine viewEngine;

	@Before
	public void initialize() {
		mainPanel = new ChatMainPanel(frame, viewEngine);
	}

	@Test
	public void shouldCreateAChatMainPanel() throws Exception {
		assertNotNull(mainPanel);
		assertTrue(mainPanel instanceof JPanel);
		assertTrue(mainPanel.getLayout() instanceof GridBagLayout);
	}

	@Test
	public void shouldHaveMainPanelDialogPanel() throws Exception {
		Component dialogPanel = mainPanel.getComponent(0);
		assertNotNull(dialogPanel);
		assertTrue(dialogPanel instanceof ChatDialogPanel);
		GridBagLayout mainPanelLayout = (GridBagLayout) mainPanel.getLayout();
		assertEquals(0, mainPanelLayout.getConstraints(dialogPanel).gridx);
		assertEquals(1, mainPanelLayout.getConstraints(dialogPanel).gridy);
		assertEquals(1, mainPanelLayout.getConstraints(dialogPanel).weightx, 0);
		assertEquals(1, mainPanelLayout.getConstraints(dialogPanel).weighty, 0);
		assertEquals(GridBagConstraints.BOTH, mainPanelLayout.getConstraints(dialogPanel).fill);
	}

	@Test
	public void shouldHaveMainPanelMessagePanel() throws Exception {
		Component messagePanel = mainPanel.getComponent(1);
		assertNotNull(messagePanel);
		assertTrue(messagePanel instanceof JPanel);
		assertEquals(new Dimension(316, 88), messagePanel.getSize());
		assertEquals(new Dimension(316, 88), messagePanel.getPreferredSize());
		assertEquals(new Dimension(250, 88), messagePanel.getMinimumSize());
		assertEquals("messagePanelChat", messagePanel.getName());
		GridBagLayout mainPanelLayout = (GridBagLayout) mainPanel.getLayout();
		assertEquals(0, mainPanelLayout.getConstraints(messagePanel).gridx);
		assertEquals(2, mainPanelLayout.getConstraints(messagePanel).gridy);
		assertEquals(1, mainPanelLayout.getConstraints(messagePanel).weightx, 0);
		assertEquals(GridBagConstraints.HORIZONTAL, mainPanelLayout.getConstraints(messagePanel).fill);
		assertEquals(new Insets(0, 2, 0, 2), mainPanelLayout.getConstraints(messagePanel).insets);
	}


}
