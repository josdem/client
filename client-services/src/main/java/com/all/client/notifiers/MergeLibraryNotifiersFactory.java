package com.all.client.notifiers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ControlEngine;
import com.all.messengine.MessEngine;
import com.all.shared.model.ContactInfo;

@Component
public class MergeLibraryNotifiersFactory {

	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private MessEngine messEngine;

	public MergeLibraryNotifier newLocalLibraryNotifier(ContactInfo libraryOwner) {
		return new MergeLibraryNotifier(libraryOwner, controlEngine, messEngine);
	}

	public MergeRemoteLibraryNotifier newRemoteLibraryNotifier(ContactInfo libraryOwner) {
		return new MergeRemoteLibraryNotifier(libraryOwner, controlEngine, messEngine);
	}

}
