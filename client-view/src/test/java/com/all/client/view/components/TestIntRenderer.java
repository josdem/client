package com.all.client.view.components;

import static org.junit.Assert.assertEquals;

import javax.swing.JLabel;

import org.junit.Test;

public class TestIntRenderer {

	@Test
	public void shouldGetText() {
		IntRenderer renderer = new IntRenderer();
		renderer.setValue(1500);
		assertEquals("1,500", renderer.getText());
		assertEquals(JLabel.RIGHT, renderer.getHorizontalAlignment());
	}

}
