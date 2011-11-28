package com.all.client.view.actions;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.core.actions.Actions;

public class TestNextTrackAction extends UnitTestCase {

	@Mock
	private ViewEngine viewEngine;

	@Test
	public void shouldCallMusicPlayerForward() throws Exception {
		NextTrackAction action = new NextTrackAction();
		action.setViewEngine(viewEngine);
		action.execute();
		Mockito.verify(viewEngine).send(Actions.Player.FORWARD);
	}

}
