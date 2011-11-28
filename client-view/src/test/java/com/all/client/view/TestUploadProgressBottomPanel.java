package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.all.client.UnitTestCase;

public class TestUploadProgressBottomPanel extends UnitTestCase {

	private UploadProgressBottomPanel uploadProgressBottomPanel = new UploadProgressBottomPanel();

	@Test
	public void shouldCreateAUploadProgressBottomPanel() throws Exception {
		assertEquals(11, uploadProgressBottomPanel.getComponents().length);
		assertEquals("bottomProgressPanel", uploadProgressBottomPanel.getName());
		assertFalse(uploadProgressBottomPanel.isVisible());
	}

}
