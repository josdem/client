package com.all.client.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEvent;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.i18n.Messages;

@Component
public class LocalLibraryPanel extends LibraryPanel {
	private static final long serialVersionUID = 1L;
	@Autowired
	private ViewEngine viewEngine;

	@Autowired
	public LocalLibraryPanel(LocalContentPanel contentPanel, LocalHeaderPanel headerPanel, LocalFooterPanel footerPanel,
			LocalCollapsedLibraryPanel collapsedLibraryPanel, MultiLayerDropTargetListener listener) {
		super(contentPanel, headerPanel, footerPanel, collapsedLibraryPanel, listener, false);
	}

	@Override
	@Autowired
	public void internationalize(Messages messages) {
		super.internationalize(messages);
	}

	@EventMethod(Events.View.SELECTED_TRACKCONTAINER_CHANGED_ID)
	public void onSelectedContainerChanged(SelectTrackContainerEvent event) {
		super.onSelectedRootChanged(event.getRoot());
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void initialize() {
		initialize(viewEngine);
	}

	@Override
	@EventMethod(Events.Library.SYNC_DOWNLOAD_EVENT_ID)
	public void onSyncDownload(LibrarySyncEvent syncEvent) {
		super.onSyncDownload(syncEvent);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine, viewEngine.get(Model.USER_ROOT));
	}
}
