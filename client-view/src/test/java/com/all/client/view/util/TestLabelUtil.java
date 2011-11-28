package com.all.client.view.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JLabel;

import org.junit.Test;

import com.all.client.view.util.LabelUtil;


public class TestLabelUtil {
	private static final Rectangle QUOTE_LABEL_BOUNDS = new Rectangle(5, 3, 220, 52);
	private static JLabel label = new JLabel();
	
	@Test
	public void shouldBreakStringInMiddle() throws Exception {
		int fontSize = 14;
		Color red = Color.RED;
		label.setBounds(QUOTE_LABEL_BOUNDS);
		String result = LabelUtil.splitTextInTwoRows(label, "WWWWWWWWWWWW This is a large string", red, fontSize);
		assertTrue(result.startsWith("<HTML>"));
		assertTrue(result.endsWith("</HTML>"));
		assertNotSame(-1, result.indexOf("<BR/>"));
		assertTrue(result.contains("color: #ff0000"));
		
		result = LabelUtil.splitTextInTwoRows(label, "Supercalifragilistoespialidoso This is a quite large string", red, fontSize);
		assertNotSame(-1, result.indexOf("<BR/>"));
	}
	
	@Test
	public void shouldNotBreakSmallString() throws Exception {
		label.setBounds(QUOTE_LABEL_BOUNDS);
		String result = LabelUtil.splitTextInTwoRows(label, "Hello World!", Color.CYAN, 140);
		assertTrue(result.startsWith("<HTML>"));
		assertTrue(result.endsWith("</HTML>"));
		assertEquals(-1, result.indexOf("<BR/>"));
	}
}
