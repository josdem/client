package com.all.client.view.toolbar.social;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;
import com.all.shared.model.ContactInfo;

public final class FriendsScrollPane extends JScrollPane {

	private static final long serialVersionUID = 1L;

	private static final int PROFILE_CONTAC_PANEL_WIDTH = 98;

	private static final int PROFILE_CONTAC_PANEL_HEIGHT = 125;

	private static final Dimension SCROLL_PANE_DEFAULT_SIZE = new Dimension(566, 248);

	private static final Dimension SCROLL_PANE_MINIMUM_SIZE = new Dimension(310, 168);

	private static final String FRIENDS_PANEL_NAME = "friendsPanelBackround";

	private static final String CURRENT_COMPONENT_NAME = "HighlightAddFriendsCellProfile";

	private boolean loaded;

	protected DialogFactory dialogFactory;

	public final Observable<ObserveObject> onLoadFriendsPanelEvent = new Observable<ObserveObject>();

	private JPanel friendsContainer;

	private PaginationFriendsPanelManager paginationFriendsPanelManager;

	private Component prevComponent;

	private final ViewEngine viewEngine;

	public FriendsScrollPane(DialogFactory dialogFactory, ViewEngine viewEngine) {
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		initialize();
	}

	public FriendsScrollPane(DialogFactory dialogFactory, PaginationFriendsPanelManager paginationFriendsPanelManager,
			ViewEngine viewEngine) {
		this.dialogFactory = dialogFactory;
		this.paginationFriendsPanelManager = paginationFriendsPanelManager;
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setSize(SCROLL_PANE_DEFAULT_SIZE);
		this.setPreferredSize(SCROLL_PANE_DEFAULT_SIZE);
		this.setMinimumSize(SCROLL_PANE_MINIMUM_SIZE);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setViewportView(getFriendsContainer());
		this.getViewport().setName(FRIENDS_PANEL_NAME);
		this.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
			@Override
			public void ancestorResized(HierarchyEvent e) {
				updateSizeFriendsContainerPanel();
			}
		});
	}

	public JPanel getFriendsContainer() {
		if (friendsContainer == null) {
			friendsContainer = new JPanel() {
				private static final long serialVersionUID = 1L;

				@Override
				public Dimension getSize() {
					Dimension size = updateSize();
					return size;
				}
			};
			friendsContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
		}
		return friendsContainer;
	}

	public void fillFirstPage() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				fillFriendsContainer(paginationFriendsPanelManager.getFirstFriendsPage());
				updateSizeFriendsContainerPanel();
				return null;
			}

			@Override
			protected void done() {
				loaded = true;
				onLoadFriendsPanelEvent.fire(ObserveObject.EMPTY);
			}
		}.execute();
	}

	public void updateSizeFriendsContainerPanel() {
		friendsContainer.setPreferredSize(new Dimension(getWidth(), friendsContainer.getSize().height));
		friendsContainer.setSize(new Dimension(getWidth(), friendsContainer.getSize().height));
	}

	private Dimension updateSize() {
		int cupoPorFila = ((getWidth() - this.getVerticalScrollBar().getWidth()) / PROFILE_CONTAC_PANEL_WIDTH);
		int filas = 0;
		if (cupoPorFila > 0) {
			filas = friendsContainer.getComponentCount() / cupoPorFila;
			filas = (friendsContainer.getComponentCount() % cupoPorFila > 0) ? filas + 1 : filas;
		}
		Dimension size = super.getSize();
		size.height = filas * PROFILE_CONTAC_PANEL_HEIGHT;
		return size;
	}

	public void fillFriendsContainer(List<ContactInfo> friends) {
		friendsContainer.removeAll();
		for (final ContactInfo contactInfo : friends) {
			final ProfileContactPanel profileContactPanel = new ProfileContactPanel(contactInfo, viewEngine, dialogFactory);
			profileContactPanel.onSelectProfile().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					final Component currentComponent = profileContactPanel;
					if (prevComponent != null) {
						prevComponent.setName("");
						((ProfileContactPanel) prevComponent).setHightLightPhotoPanel(false);
					}
					currentComponent.setName(CURRENT_COMPONENT_NAME);
					((ProfileContactPanel) currentComponent).setHightLightPhotoPanel(true);
					prevComponent = currentComponent;
				}
			});
			friendsContainer.add(profileContactPanel);
		}
		friendsContainer.repaint();
		friendsContainer.revalidate();
	}

	public ObserverCollection<ObserveObject> onLoadFriendsPanel() {
		return onLoadFriendsPanelEvent;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public void setPaginationFriendsPanelManager(PaginationFriendsPanelManager paginationFriendsPanelManager) {
		this.paginationFriendsPanelManager = paginationFriendsPanelManager;
	}

	public PaginationFriendsPanelManager getPaginationManager() {
		return paginationFriendsPanelManager;
	}

	public void updateContact(ContactInfo contact) {
		for (Component component : friendsContainer.getComponents()) {
			if (component instanceof ProfileContactPanel) {
				ProfileContactPanel contactPanel = (ProfileContactPanel) component;
				if (contact.equals(contactPanel.getContact())) {
					contactPanel.refresh(contact);
				}
			}
		}
	}
}
