package com.all.client.view.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.PopupMenu;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.all.client.util.ReflectionUtilities;

public class TestDevicesPanel {
	private static final boolean DEBUG = false;
	private DevicesPanel collapsable = new DevicesPanel();

	private Log log = LogFactory.getLog(this.getClass());
	
	@Before
	public void initializeTree() {
		collapsable.setPreferredSize(new Dimension(150, 16));
		collapsable.setMinimumSize(new Dimension(150, 16));
		collapsable.setMaximumSize(new Dimension(999999, 9999999));
		collapsable.setSize(new Dimension(150, 16));
		JPanel firstPanel = new JPanel();
		firstPanel.setMinimumSize(new Dimension(0, 20));
		firstPanel.setMaximumSize(new Dimension(9999, 20));
		firstPanel.setPreferredSize(new Dimension(100, 20));
		firstPanel.setSize(new Dimension(100, 20));
		firstPanel.setBackground(Color.BLUE);
		collapsable.content().add(firstPanel);
		if (DEBUG) {
			JFrame frame = new JFrame();
			frame.setSize(150, 300);
			frame.setLayout(new BorderLayout());
			frame.add(collapsable, BorderLayout.NORTH);
			frame.setVisible(true);
		}
	}

	@Test
	public void shouldCreateACollapsablePanelAsAnInstanceOfPanel() throws Exception {
		JPanel collapsable = this.collapsable;
		assertNotNull(collapsable);
		assertEquals(16, collapsable.getHeight());
	}

	@Test
	public void shouldAddTitleToCollapsablePanel() throws Exception {
		String demoTitle = "Devices";
		JLabel titleLabel = (JLabel) ReflectionUtilities.getPrivateField(collapsable, "titleLabel");
		assertEquals("", titleLabel.getText());
		collapsable.setTitle(demoTitle);
		assertEquals(demoTitle, titleLabel.getText());
	}

	@Test
	public void shouldCollapsePanel() throws Exception {
		collapsable.collapse();
		assertEquals(16, collapsable.getHeight());
	}

	@Test
	public void shouldExpandPanel() throws Exception {
		collapsable.collapse();
		collapsable.expand();
		assertEquals(16, collapsable.getHeight());
	}

	@Test
	public void shouldSimulateClickingThaButton() throws Exception {
		assertEquals(16, collapsable.getHeight());
		JButton arrowButton = (JButton) ReflectionUtilities.getPrivateField(collapsable, "arrowButton");
		arrowButton.doClick();
		assertEquals(16, collapsable.getHeight());
		arrowButton.doClick();
		assertEquals(16, collapsable.getHeight());
	}

	@Test
	public void shouldNotAllowNormalPanelOperations() throws Exception {
		Component comp = mock(Component.class);
		try {
			collapsable.add(comp);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.add(comp, 0);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.add(comp, new Object());
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.add(comp, new Object(), 0);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.add(mock(PopupMenu.class));
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.add("", comp);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.remove(comp);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.remove(0);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.remove(mock(PopupMenu.class));
			fail();
		} catch (RuntimeException e) {
		}
		try {
			collapsable.removeAll();
			fail();
		} catch (RuntimeException e) {
		}
	}

	@After
	public void waitIfDebug() {
		if (DEBUG) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				log.debug(e,e);
			}
		}
	}
}
