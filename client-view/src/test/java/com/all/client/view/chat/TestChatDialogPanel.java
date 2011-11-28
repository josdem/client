package com.all.client.view.chat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

public class TestChatDialogPanel {

	private ChatDialogPanel dialogPanel;

	@Before
	public void initialize() {
		dialogPanel = new ChatDialogPanel();
	}

	@Test
	public void shouldCreateChatDialogPanel() throws Exception {
		assertNotNull(new ChatDialogPanel());
		assertTrue(new ChatDialogPanel() instanceof JPanel);
		assertTrue(new ChatDialogPanel().getLayout() instanceof GridBagLayout);
		assertEquals(new Dimension(396, 403), new ChatDialogPanel().getSize());
		assertEquals(new Dimension(396, 403), new ChatDialogPanel().getPreferredSize());
		assertEquals(new Dimension(259, 403), new ChatDialogPanel().getMinimumSize());
	}

	@Test
	public void shouldHaveDialogPanelHeaderPanel() throws Exception {
		assertNotNull(dialogPanel.getComponent(0));
		assertTrue(dialogPanel.getComponent(0) instanceof JPanel);
		assertEquals(new Dimension(396, 18), dialogPanel.getComponent(0).getSize());
		assertEquals(new Dimension(396, 18), dialogPanel.getComponent(0).getPreferredSize());
		assertEquals(new Dimension(330, 18), dialogPanel.getComponent(0).getMinimumSize());
		assertEquals("headerPanelDialogChat", dialogPanel.getComponent(0).getName());
		GridBagLayout dialogPanelLayout = (GridBagLayout) dialogPanel.getLayout();
		assertEquals(0, dialogPanelLayout.getConstraints(dialogPanel.getComponent(0)).gridx);
		assertEquals(0, dialogPanelLayout.getConstraints(dialogPanel.getComponent(0)).gridy);
		assertEquals(1, dialogPanelLayout.getConstraints(dialogPanel.getComponent(0)).weightx, 0);
		assertEquals(GridBagConstraints.HORIZONTAL, dialogPanelLayout.getConstraints(dialogPanel.getComponent(0)).fill);
		assertEquals(new Insets(0, 2, 0, 2), dialogPanelLayout.getConstraints(dialogPanel.getComponent(0)).insets);
	}

	@Test
	public void shouldHaveDialogPanelScrollPane() throws Exception {
		assertNotNull(dialogPanel.getComponent(1));
		assertTrue(dialogPanel.getComponent(1) instanceof JPanel);
		assertEquals(new Dimension(396, 368), dialogPanel.getComponent(1).getSize());
		assertEquals(new Dimension(396, 368), dialogPanel.getComponent(1).getPreferredSize());
		assertEquals(new Dimension(330, 368), dialogPanel.getComponent(1).getMinimumSize());

		GridBagLayout dialogPanelLayout = (GridBagLayout) dialogPanel.getLayout();
		assertEquals(0, dialogPanelLayout.getConstraints(dialogPanel.getComponent(1)).gridx);
		assertEquals(1, dialogPanelLayout.getConstraints(dialogPanel.getComponent(1)).gridy);
		assertEquals(1, dialogPanelLayout.getConstraints(dialogPanel.getComponent(1)).weightx, 0);
		assertEquals(1, dialogPanelLayout.getConstraints(dialogPanel.getComponent(1)).weighty, 0);
		assertEquals(GridBagConstraints.BOTH, dialogPanelLayout.getConstraints(dialogPanel.getComponent(1)).fill);
		assertEquals(new Insets(0, 2, 0, 2), dialogPanelLayout.getConstraints(dialogPanel.getComponent(1)).insets);

	}

	@Test
	public void shouldHaveDialogPanelFooterPanel() throws Exception {

		assertNotNull(dialogPanel.getComponent(2));
		assertTrue(dialogPanel.getComponent(2) instanceof JPanel);
		assertEquals("footerPanelDialogChat", dialogPanel.getComponent(2).getName());
		assertEquals(new Dimension(396, 17), dialogPanel.getComponent(2).getSize());
		assertEquals(new Dimension(396, 17), dialogPanel.getComponent(2).getPreferredSize());
		assertEquals(new Dimension(330, 17), dialogPanel.getComponent(2).getMinimumSize());

		GridBagLayout dialogPanelLayout = (GridBagLayout) dialogPanel.getLayout();
		assertEquals(0, dialogPanelLayout.getConstraints(dialogPanel.getComponent(2)).gridx);
		assertEquals(2, dialogPanelLayout.getConstraints(dialogPanel.getComponent(2)).gridy);
		assertEquals(1, dialogPanelLayout.getConstraints(dialogPanel.getComponent(2)).weightx, 0);
		assertEquals(GridBagConstraints.HORIZONTAL, dialogPanelLayout.getConstraints(dialogPanel.getComponent(2)).fill);
		assertEquals(new Insets(0, 2, 2, 2), dialogPanelLayout.getConstraints(dialogPanel.getComponent(2)).insets);
	}

}
