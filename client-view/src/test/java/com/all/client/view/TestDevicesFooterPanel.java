package com.all.client.view;

import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import com.all.client.SimpleGUITest;


public class TestDevicesFooterPanel extends SimpleGUITest{

	private DevicesFooterPanel devicesFooterPanel;
	
	@Before
	public void initialize() {
		devicesFooterPanel = new DevicesFooterPanel(null, messages);
	}

	@Test
	public void shouldFormatBytes() throws Exception {
		String kiloBytes = devicesFooterPanel.formatBytes(messages, 12345L);
		String megaBytes = devicesFooterPanel.formatBytes(messages, 1234567L);
		String gigaBytes = devicesFooterPanel.formatBytes(messages, 1234567890L);
		
		assertNotSame(-1, kiloBytes.indexOf("K"));
		assertNotSame(-1, megaBytes.indexOf("M"));
		assertNotSame(-1, gigaBytes.indexOf("G"));
	}
	
}
