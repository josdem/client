package com.all.client.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.ContactRoot;
import com.all.client.view.components.GridBagConstraintsFactory;
import com.all.client.view.components.GridBagConstraintsFactory.FillMode;
import com.all.client.view.dnd.ContactSmartPlaylistTreeTransferHandler;
import com.all.client.view.dnd.ModelTreeTransferHandler;
import com.all.core.events.Events;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.ContainerView;
import com.all.event.EmptyEvent;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Root;

public class ContactPreviewPanel extends PreviewPanel {

	private static final long serialVersionUID = 1L;

	private JPanel contentPanel;

	private Root root;

	private Listener<ValueEvent<ContainerView>> currentViewChangedListener;
	private Listener<ValueEvent<Root>> treeStructureChangedListener;
	private Listener<SelectTrackContainerEvent> selectedTrackContainerListener;
	private Listener<EmptyEvent> newContentAvailableListener;

	public ContactPreviewPanel(Root root, ViewEngine viewEngine) {
		super(viewEngine);
		this.root = root;
		this.createListeners();
		this.setName("previewTreeBackground");
		setupTree();
	}

	private void createListeners() {
		currentViewChangedListener = new EventListener<ValueEvent<ContainerView>>(true) {
			@Override
			public void handleEvent(ValueEvent<ContainerView> eventArgs) {
				onModelViewCurrentViewChanged(eventArgs);
			}
		};
		treeStructureChangedListener = new EventListener<ValueEvent<Root>>() {
			@Override
			public void handleEvent(ValueEvent<Root> eventArgs) {
				onTreeStructureChanged(eventArgs);
			}
		};
		selectedTrackContainerListener = new EventListener<SelectTrackContainerEvent>() {
			@Override
			public void handleEvent(SelectTrackContainerEvent eventArgs) {
				onSelectedContainerChanged(eventArgs);
			}
		};
		newContentAvailableListener = new EventListener<EmptyEvent>() {
			@Override
			public void handleEvent(EmptyEvent eventArgs) {
				onNewContentAvailable();
			}
		};
	}

	private void setupTree() {
		getPreviewTree().setEditable(false);
		getPreviewTree().getInputMap().put(KeyStroke.getKeyStroke("F2"), "none");
		// Make it draggeable without the default behavior
		getPreviewTree().setTransferHandler(new ModelTreeTransferHandler(getPreviewTree(), true, viewEngine));
		// Remove default behavior added by the shit up there
		getPreviewTree().setDropTarget(null);

		getSmartPlaylistTree().setEditable(false);
		getSmartPlaylistTree().getInputMap().put(KeyStroke.getKeyStroke("F2"), "none");
		getSmartPlaylistTree().setDragEnabled(true);
		getSmartPlaylistTree().setTransferHandler(
				new ContactSmartPlaylistTreeTransferHandler(((ContactRoot) root), getSmartPlaylistTree(), true,
						viewEngine));
		getSmartPlaylistTree().setDropTarget(null);

		getPreviewTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getPreviewTree().requestFocus();
				if (e.getClickCount() == 2) {
					getPreviewTree().setEditable(false);
				}
			}
		});
	}

	@Override
	protected void onRootSet(Root root) {
		this.root = root;
	}

	public void onTreeStructureChanged(ValueEvent<Root> eventArgs) {
		Root root2 = eventArgs.getValue();
		if (root instanceof ContactRoot && root2 instanceof ContactRoot) {
			ContactRoot contactRoot1 = (ContactRoot) root;
			ContactRoot contactRoot2 = (ContactRoot) root2;
			if (contactRoot1 == contactRoot2 || contactRoot1.equals(contactRoot2)) {
				setRoot(root2);
			}
		} else if (eventArgs.getValue() == root) {
			setRoot(root);
		}
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine, root);
		viewEngine.addListener(Events.View.CURRENT_VIEW_CHANGED, currentViewChangedListener);
		viewEngine.addListener(Events.View.SELECTED_TRACKCONTAINER_CHANGED, selectedTrackContainerListener);
		viewEngine.addListener(Events.Library.TREE_STRUCTURE_CHANGED, treeStructureChangedListener);
		viewEngine.addListener(Events.Library.NEW_CONTENT_AVAILABLE, newContentAvailableListener);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
		viewEngine.removeListener(Events.View.CURRENT_VIEW_CHANGED, currentViewChangedListener);
		viewEngine.removeListener(Events.View.SELECTED_TRACKCONTAINER_CHANGED, selectedTrackContainerListener);
		viewEngine.removeListener(Events.Library.TREE_STRUCTURE_CHANGED, treeStructureChangedListener);
		viewEngine.removeListener(Events.Library.NEW_CONTENT_AVAILABLE, newContentAvailableListener);
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	protected JPanel getContentPanel() {
		if (contentPanel == null) {
			GridBagConstraintsFactory factory = new GridBagConstraintsFactory();
			factory.grid(0, 0).fill(1, 0, FillMode.HORIZONTAL);
			GridBagConstraints smartPlaylistTreeConstraints = factory.get();
			factory.grid(0, 1).fill(1, 1, FillMode.BOTH);
			GridBagConstraints previewTreeConstraints = factory.get();
			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());
			contentPanel.add(getPreviewTree(), previewTreeConstraints);
			contentPanel.add(getSmartPlaylistTree(), smartPlaylistTreeConstraints);
		}
		return contentPanel;
	}
}
