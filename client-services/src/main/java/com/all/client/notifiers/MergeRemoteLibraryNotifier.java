package com.all.client.notifiers;

import com.all.appControl.control.ControlEngine;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEvent;
import com.all.core.events.LibrarySyncEventType;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.model.ContactInfo;

public class MergeRemoteLibraryNotifier extends MergeLibraryNotifier {
	private final ControlEngine controlEngine;
	private final String email;
	private final String name;

	public MergeRemoteLibraryNotifier(ContactInfo libraryOwner, ControlEngine controlEngine, MessEngine messEngine) {
		super(libraryOwner, controlEngine, messEngine);
		email = libraryOwner.getEmail();
		name = libraryOwner.getNickName();
		this.controlEngine = controlEngine;
		this.setState(State.LOADING_CONTEXT);
	}

	@Override
	public void notifyMergeLibraryStarted() {
		controlEngine.fireEvent(Events.Library.LOADING_LIBRARY, new ValueEvent<String>(name));
		controlEngine.fireEvent(Events.Library.SYNC_DOWNLOAD_EVENT, new LibrarySyncEvent(email, name, LibrarySyncEventType.SYNC_STARTED));
	}
}
