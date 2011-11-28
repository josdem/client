package com.all.client.view;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEvent;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public class ContactLibraryPanel extends LibraryPanel {
	private static final long serialVersionUID = 1L;
	private Listener<LibrarySyncEvent> syncDownloadListener;
	private final Root root;
	private Listener<SelectTrackContainerEvent> selectedItemListener;

	public ContactLibraryPanel(ContentPanel contentPanel, HeaderPanel headerPanel, FooterPanel footerPanel,
			CollapsedLibraryPanel collapsedLibraryPanel, Root root, MultiLayerDropTargetListener listener) {
		super(contentPanel, headerPanel, footerPanel, collapsedLibraryPanel, listener, true);
		this.root = root;
		createListeners();
		onSelectedRootChanged(root);
	}

	private void createListeners() {
		syncDownloadListener = new EventListener<LibrarySyncEvent>() {
			@Override
			public void handleEvent(LibrarySyncEvent eventArgs) {
				onSyncDownload(eventArgs);
			}
		};
		selectedItemListener = new EventListener<SelectTrackContainerEvent>() {
			@Override
			public void handleEvent(SelectTrackContainerEvent eventArgs) {
				onSelectedRootChanged(eventArgs.getRoot());
			}
		};
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine, root);
		viewEngine.addListener(Events.Library.SYNC_DOWNLOAD_EVENT, syncDownloadListener);
		viewEngine.addListener(Events.View.SELECTED_TRACKCONTAINER_CHANGED, selectedItemListener);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
		viewEngine.removeListener(Events.Library.SYNC_DOWNLOAD_EVENT, syncDownloadListener);
		viewEngine.removeListener(Events.View.SELECTED_TRACKCONTAINER_CHANGED, selectedItemListener);
	}

}
