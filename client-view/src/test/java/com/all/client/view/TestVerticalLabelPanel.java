package com.all.client.view;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.all.client.view.util.ComponentPaintValidator;

public class TestVerticalLabelPanel {
	@Test
	public void testname() throws Exception {
		VerticalLabelPanel component = new VerticalLabelPanel();
		component.setLabel("AAAAHHH!!!");
		component.setSize(200, 200);
		ComponentPaintValidator validator = new ComponentPaintValidator(component);
		validator.refresh();
		// validator.saveImage("label.png");
		assertTrue(true);
	}
}
