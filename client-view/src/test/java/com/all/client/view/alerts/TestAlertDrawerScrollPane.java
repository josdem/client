package com.all.client.view.alerts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.core.model.Model;
import com.all.shared.alert.Alert;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith (MockInyectRunner.class)
public class TestAlertDrawerScrollPane {
	@UnderTest
	private AlertDrawerScrollPane alertDrawer;
	@Mock
	private Alert updateAlert;
	@Mock
	private ViewEngine viewEngine;
	
	private Set<Alert> alerts;

	@Before
	public void setup() throws Exception {
		alerts = new TreeSet<Alert>();
		alerts.add(updateAlert);
	}
	
	@Test
	public void shouldGetAlertCount() throws Exception {
		when(viewEngine.get(Model.CURRENT_ALERTS)).thenReturn(alerts);
		assertEquals(1, alertDrawer.getAlertCount().intValue());
	}
}
