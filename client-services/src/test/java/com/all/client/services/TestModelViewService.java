package com.all.client.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.action.ValueAction;
import com.all.appControl.control.TestEngine;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;

public class TestModelViewService {

	@InjectMocks
	private ViewService service = new ViewService();
	@Spy
	private TestEngine controlEngine = new TestEngine();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldSetCurrentViewAndRaiseEvent() throws Exception {
		service.initialize();
		assertEquals(Views.HOME, controlEngine.get(Model.CURRENT_VIEW));
		service.changeCurrentView(new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
		verify(controlEngine).set(Model.CURRENT_VIEW, Views.LOCAL_MUSIC, null);
		// TODO Commented for review
		// verify(controlEngine).fireEvent(Events.View.CURRENT_VIEW_CHANGED, new ValueEvent<ContainerView>(new
		// ContainerView(Views.LOCAL_MUSIC)));
		assertEquals(Views.LOCAL_MUSIC, controlEngine.get(Model.CURRENT_VIEW));
	}

	@Test
	public void shouldPayNoHeedToNull() throws Exception {
		service.initialize();
		assertEquals(Views.HOME, controlEngine.get(Model.CURRENT_VIEW));
		service.changeCurrentView(new ValueAction<ContainerView>(null));
		assertEquals(Views.HOME, controlEngine.get(Model.CURRENT_VIEW));
	}

}
