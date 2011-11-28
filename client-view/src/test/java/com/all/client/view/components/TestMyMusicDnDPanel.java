package com.all.client.view.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

import com.all.client.UnitTestCase;

public class TestMyMusicDnDPanel extends UnitTestCase {

	private static final Point LOCATION_EXPECTED = new Point(0, 131);
	private static final Dimension SIZE_EXPECTED = new Dimension(204, 184);
	private MyMusicDnDPanel myMusicDnDPanel;

	@Before
	public void init() {
		myMusicDnDPanel = new MyMusicDnDPanel();

	}

	@Test
	public void shouldCreateMyMusicDnDPanel() throws Exception {
		assertTrue(myMusicDnDPanel instanceof JPanel);
		assertTrue(myMusicDnDPanel.getLayout() instanceof GridBagLayout);
		assertEquals("myMusicDnDPanel", myMusicDnDPanel.getName());
		assertEquals(SIZE_EXPECTED, myMusicDnDPanel.getSize());
		assertEquals(SIZE_EXPECTED, myMusicDnDPanel.getPreferredSize());
		assertEquals(SIZE_EXPECTED, myMusicDnDPanel.getMinimumSize());
		assertEquals(SIZE_EXPECTED, myMusicDnDPanel.getMaximumSize());
		assertEquals(LOCATION_EXPECTED, myMusicDnDPanel.getLocation());
	}

	@Test
	public void shouldHaveElementsOnCorrectOrderAndStyle() throws Exception {
		assertEquals("boldFont12Gray77_77_77", myMusicDnDPanel.getComponent(0).getName());
		assertEquals("boldFont23Purple130_60_165", myMusicDnDPanel.getComponent(1).getName());
		assertEquals("plainFont12Purple130_60_165", myMusicDnDPanel.getComponent(2).getName());
	}

}
