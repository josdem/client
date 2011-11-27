package com.all.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.app.Attributes;
import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.core.actions.Actions;
import com.all.core.actions.CloseType;
import com.all.core.events.Events;

@Service
public class ClientApplicationServices {
	@Autowired
	private Attributes attributes;
	@Autowired
	private ControlEngine controlEngine;

	@ActionMethod(Actions.Application.APP_CLOSE_ID)
	public void shutdown(CloseType type) {
		switch (type) {
		case LOGOUT:
			attributes.setAttribute(ClientAttributes.LOGGED_OUT, true);
		case EXIT:
		case RESTART:
		}
		controlEngine.fireValueEvent(Events.Application.APP_CLOSE, type);
	}

}
