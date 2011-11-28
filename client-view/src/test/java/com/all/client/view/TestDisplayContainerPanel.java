package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import com.all.client.SimpleGUITest;

public class TestDisplayContainerPanel extends SimpleGUITest {

	@InjectMocks
	private DisplayContainerPanel displayContainerPanel = new DisplayContainerPanel();
	@Spy
	public InfoPlayerPanel infoPlayerPanel = new InfoPlayerPanel();
	@Spy
	public LogoPanel logoPanel = new LogoPanel();

	@Before
	public void setUp() throws Exception {
		displayContainerPanel.setMessages(messages);
		displayContainerPanel.initialize();
	}

	@Test
	public void shouldHaveLeftBubbleSide() throws Exception {
		assertNotNull(displayContainerPanel.getLeftBubbleSide());
		assertTrue(displayContainerPanel.getLeftBubbleSide() instanceof JPanel);
		assertEquals(135, displayContainerPanel.getLeftBubbleSide().getMinimumSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getLeftBubbleSide().getMinimumSize().getHeight(), 0);
		assertEquals(135, displayContainerPanel.getLeftBubbleSide().getSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getLeftBubbleSide().getSize().getHeight(), 0);
		assertEquals(135, displayContainerPanel.getLeftBubbleSide().getPreferredSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getLeftBubbleSide().getPreferredSize().getHeight(), 0);
		assertEquals(135, displayContainerPanel.getLeftBubbleSide().getMaximumSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getLeftBubbleSide().getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldHaveCenterBubble() throws Exception {
		assertNotNull((JPanel) displayContainerPanel.getInfoPlayerPanel());
		assertTrue(displayContainerPanel.getInfoPlayerPanel() instanceof JPanel);
		assertEquals(180, displayContainerPanel.getInfoPlayerPanel().getMinimumSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getInfoPlayerPanel().getMinimumSize().getHeight(), 0);
		assertEquals(294, displayContainerPanel.getInfoPlayerPanel().getSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getInfoPlayerPanel().getSize().getHeight(), 0);
		assertEquals(294, displayContainerPanel.getInfoPlayerPanel().getPreferredSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getInfoPlayerPanel().getPreferredSize().getHeight(), 0);
		assertEquals(729, displayContainerPanel.getInfoPlayerPanel().getMaximumSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getInfoPlayerPanel().getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldHaveRightBubbleSide() throws Exception {
		assertNotNull(displayContainerPanel.getRightBubbleSide());
		assertTrue(displayContainerPanel.getRightBubbleSide() instanceof JPanel);
		assertEquals(135, displayContainerPanel.getRightBubbleSide().getMinimumSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getRightBubbleSide().getMinimumSize().getHeight(), 0);
		assertEquals(135, displayContainerPanel.getRightBubbleSide().getSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getRightBubbleSide().getSize().getHeight(), 0);
		assertEquals(135, displayContainerPanel.getRightBubbleSide().getPreferredSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getRightBubbleSide().getPreferredSize().getHeight(), 0);
		assertEquals(135, displayContainerPanel.getRightBubbleSide().getMaximumSize().getWidth(), 0);
		assertEquals(82, displayContainerPanel.getRightBubbleSide().getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldHaveCorrectDistribution() throws Exception {
		assertNotNull(((GridBagLayout) displayContainerPanel.getLayout()));
		assertEquals(0, ((GridBagLayout) displayContainerPanel.getLayout()).getConstraints(displayContainerPanel
				.getLeftBubbleSide()).gridx);
		assertEquals(0, ((GridBagLayout) displayContainerPanel.getLayout()).getConstraints(displayContainerPanel
				.getLeftBubbleSide()).gridy);
		assertEquals(2, ((GridBagLayout) displayContainerPanel.getLayout()).getConstraints(displayContainerPanel
				.getRightBubbleSide()).gridx);
		assertEquals(0, ((GridBagLayout) displayContainerPanel.getLayout()).getConstraints(displayContainerPanel
				.getRightBubbleSide()).gridy);
	}

}
