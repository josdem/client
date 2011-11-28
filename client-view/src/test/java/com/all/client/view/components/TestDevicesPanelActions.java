package com.all.client.view.components;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Component;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.client.model.DeviceBase;
import com.all.event.ValueEvent;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;


@RunWith(MockInyectRunner.class)
public class TestDevicesPanelActions {

	@UnderTest
	private DevicesPanel devicesPanel;
	@Mock
	private DeviceBase device;
	@Mock
	private JPanel contentPanel;
	@Mock
	private ValueEvent<DeviceBase> event;
	
	@Before
	public void initialize() {
		contentPanel = devicesPanel.content();
		when(event.getValue()).thenReturn(device);
	}
	
	@Test
	public void shouldAddADevice() throws Exception {
		devicesPanel.addDevice(event);
		verify(contentPanel).add((Component)anyObject());
		assertEquals(1, devicesPanel.getDevicesLoaded());
	}
	
	@Test
	public void shouldRemoveADevice() throws Exception {
		ExternalDevicePanel devicePanel = mock(ExternalDevicePanel.class);
		Component[] components = {devicePanel};
		devicesPanel.addDevice(event);

		when(contentPanel.getComponents()).thenReturn(components);
		when(devicePanel.getDevice()).thenReturn(device);
		
		devicesPanel.removeDevice(event);
		
		verify(contentPanel).remove((Component)anyObject());
		assertEquals(0, devicesPanel.getDevicesLoaded());
	}
}
