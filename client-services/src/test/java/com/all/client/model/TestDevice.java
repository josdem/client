package com.all.client.model;

import static org.junit.Assert.assertNotNull;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.junit.Test;

public class TestDevice {
	
	@Test
	public void shouldCreateADevice() throws Exception {
		Device d = new Device();
		d.setIconFile(new ImageIcon());
		d.setName("Ipod");
		assertNotNull(d.getIconFile());
	}
	
	@Test
	public void shouldCreateAIconFromDevice() throws Exception {
		Device d = new Device();
		d.setIconFile(new ImageIcon());
		d.setName("Ipod");
		Icon ii = d.getIconFile();
		assertNotNull(ii);
	}
}
