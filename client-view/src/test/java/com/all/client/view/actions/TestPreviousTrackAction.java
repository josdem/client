package com.all.client.view.actions;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.core.actions.Actions;

public class TestPreviousTrackAction extends UnitTestCase {

	@Mock
	ViewEngine viewEngine;

	@Test
	public void shouldCallBackTrackAction() throws Exception {

		BackTrackAction action = new BackTrackAction();
		action.setViewEngine(viewEngine);
		action.execute();
		Mockito.verify(viewEngine).send(Actions.Player.PREVIOUS);
	}
}
