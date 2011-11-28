package com.all.client.view.dialog;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;

import com.all.appControl.control.TestEngine;
import com.all.client.view.DisplayBackPanel;
import com.all.client.view.MainFrame;
import com.all.client.view.alerts.DrawerDialog;
import com.all.core.events.Events;
import com.all.event.ValueEvent;

public class TestDialogFactory {

	@InjectMocks
	private DialogFactory dialogFactory = new DialogFactory();

	@Mock
	private DrawerDialog drawerDialog;
	@SuppressWarnings("unused")
	@Mock
	private MainFrame mainFrame;
	@SuppressWarnings("unused")
	@Mock
	private DisplayBackPanel displayBackPanel;
	@SuppressWarnings("unused")
	@Mock
	private ApplicationContext applicationContext;
	@Spy
	private TestEngine engine = new TestEngine();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		engine.setup(dialogFactory);
	}

	@After
	public void teardown() {
		engine.reset(dialogFactory);
	}

	@Test
	public void shouldReportAccessToDrawer() throws Exception {
		when(drawerDialog.isVisible()).thenReturn(false, true);

		engine.fireEvent(Events.View.DRAWER_DISPLAYED_CHANGED, new ValueEvent<Boolean>(true));

		verify(drawerDialog).setVisible(true);
	}
}
