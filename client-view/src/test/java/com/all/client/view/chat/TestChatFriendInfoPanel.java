package com.all.client.view.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.junit.Test;

import com.all.client.view.components.ImagePanel;

public class TestChatFriendInfoPanel {

	@Test
	public void shouldCreateChatFriendInfoPanel() throws Exception {
		ChatFriendInfoPanel friendInfoPanel = new ChatFriendInfoPanel();
		assertNotNull(friendInfoPanel);
		assertTrue(friendInfoPanel instanceof JPanel);
		assertTrue(friendInfoPanel.getLayout() instanceof GridBagLayout);
		assertEquals(new Dimension(396, 86), friendInfoPanel.getSize());
		assertEquals(new Dimension(396, 86), friendInfoPanel.getPreferredSize());
		assertEquals(new Dimension(330, 86), friendInfoPanel.getMinimumSize());
		assertEquals("friendInfoPanelChat", friendInfoPanel.getName());
	}

	@Test
	public void shouldHaveThreePanelsToOrderTheComponents() throws Exception {
		JPanel friendInfoPanel = new ChatFriendInfoPanel();
		assertNotNull(friendInfoPanel.getComponent(0));
		assertTrue(friendInfoPanel.getComponent(0) instanceof JPanel);
		assertNotNull(friendInfoPanel.getComponent(1));
		assertTrue(friendInfoPanel.getComponent(1) instanceof JPanel);
		assertNotNull(friendInfoPanel.getComponent(2));
		assertTrue(friendInfoPanel.getComponent(2) instanceof JPanel);

		GridBagLayout dialogPanelLayout = (GridBagLayout) friendInfoPanel.getLayout();
		assertEquals(0, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(0)).gridx);
		assertEquals(0, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(0)).gridy);

		assertEquals(1, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(1)).gridx);
		assertEquals(0, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(1)).gridy);
		assertEquals(1, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(1)).weightx, 0);
		assertEquals(GridBagConstraints.HORIZONTAL, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(1)).fill);

		assertEquals(2, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(2)).gridx);
		assertEquals(0, dialogPanelLayout.getConstraints(friendInfoPanel.getComponent(2)).gridy);
	}

	@Test
	public void shouldHaveLeftPanelImagePanel() throws Exception {
		JPanel friendInfoPanel = new ChatFriendInfoPanel();
		JPanel leftPanel = (JPanel) friendInfoPanel.getComponent(0);
		assertNotNull(leftPanel.getComponent(0));
		assertTrue(leftPanel.getComponent(0) instanceof ImagePanel);
		assertEquals(new Dimension(70, 70), leftPanel.getComponent(0).getSize());
	}

}
