package com.all.client.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.i18n.Messages;

@Component
public class LocalCollapsedLibraryPanel extends CollapsedLibraryPanel {
	@Autowired
	private ViewEngine viewEngine;

	@Autowired
	public LocalCollapsedLibraryPanel(Messages messages) {
		super(messages);
	}

	private static final long serialVersionUID = 1L;

	@EventMethod(Events.Application.STARTED_ID)
	public void onStart() {
		initialize(viewEngine);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine, viewEngine.get(Model.USER_ROOT));
	}

}
