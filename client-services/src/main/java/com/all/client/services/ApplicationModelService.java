package com.all.client.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.action.ValueAction;
import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.controller.ContactController;
import com.all.client.model.ContactRoot;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.events.Events;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.event.ValueEvent;
import com.all.shared.model.EmptyTrackContainer;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.model.Root.ContainerType;

@Service
public class ApplicationModelService {
	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private MusicEntityService musicEntityService;

	private List<Root> libraries = new ArrayList<Root>();;

	private LinkedList<Root> lastLibraries = new LinkedList<Root>();

	private Object librariesLock = new Object();
	@Autowired
	private ViewService viewService;
	@Autowired
	private ContactController contactController;

	@ActionMethod(Actions.View.SELECT_TRACKCONTAINER_ID)
	public synchronized void setSelectedTrackContainer(SelectTrackContainerAction action) {
		Root root = action.getRoot();
		if (root != null) {
			TrackContainer container = action.getTrackContainer();
			TrackContainer lastContainer = controlEngine.get(Model.SELECTED_CONTAINER);
			Root lastRoot = controlEngine.get(Model.SELECTED_ROOT);
			if (container == null) {
				if (root == lastRoot) {
					container = lastContainer;
				} else {
					container = EmptyTrackContainer.INSTANCE;
				}
			}
			if (lastContainer instanceof Playlist && ((Playlist) lastContainer).isNewContent()) {
				unmarkNewTracks(lastContainer.getTracks(), lastContainer);
			}
			if (!libraries.contains(root)) {
				addLibrary(root);
			}
			if (changeView(root, lastRoot) || !modelSame(root, container)) {
				controlEngine.set(Model.SELECTED_ROOT, root, null);
				controlEngine.set(Model.SELECTED_CONTAINER, container, null);
				controlEngine.fireEvent(Events.View.SELECTED_TRACKCONTAINER_CHANGED, new SelectTrackContainerEvent(
						root, container));
			}
		}
		// throw new RuntimeException();
	}

	@ActionMethod(Actions.View.SELECT_ROOT_ID)
	public synchronized void selectRoot(Root root) {
		Root lastRoot = controlEngine.get(Model.SELECTED_ROOT);
		if (!lastRoot.equals(root)) {
			EmptyTrackContainer container = EmptyTrackContainer.INSTANCE;
			controlEngine.set(Model.SELECTED_ROOT, root, null);
			controlEngine.set(Model.SELECTED_CONTAINER, container, null);
			controlEngine.fireEvent(Events.View.SELECTED_TRACKCONTAINER_CHANGED, new SelectTrackContainerEvent(root,
					container));
		}
	}

	private boolean modelSame(Root root, TrackContainer container) {
		TrackContainer lastContainer = controlEngine.get(Model.SELECTED_CONTAINER);
		Root lastRoot = controlEngine.get(Model.SELECTED_ROOT);
		return lastContainer.equals(container) && lastRoot.equals(root);
	}

	@ActionMethod(Actions.Library.REMOVE_FROM_NEW_TRACKS_ID)
	public void unmarkNewTracks(Iterable<Track> tracks) {
		unmarkNewTracks(tracks, controlEngine.get(Model.SELECTED_CONTAINER));
	}

	public void unmarkNewTracks(Iterable<Track> tracks, TrackContainer trackContainer) {
		boolean shouldValidate = false;
		if (trackContainer instanceof Playlist) {
			shouldValidate = ((Playlist) trackContainer).isNewContent();
		}

		for (Track track : tracks) {
			if (track.isNewContent()) {
				musicEntityService.removeFromNewContent(track);
			}
		}
		if (trackContainer instanceof LocalPlaylist || trackContainer instanceof LocalFolder) {
			for (Track track : trackContainer.getTracks()) {
				if (track.isNewContent()) {
					return;
				}
			}
			if (shouldValidate) {
				controlEngine.fireEvent(Events.Library.NEW_CONTENT_AVAILABLE);
			}
		}
	}

	public void addLibrary(Root library) {
		synchronized (librariesLock) {
			if (libraries.contains(library)) {
				controlEngine.fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(library));
			} else {
				if (library.getType() == ContainerType.CONTACT && !lastLibraries.contains(library)) {
					if (lastLibraries.size() == 5) {
						lastLibraries.removeLast();
					}
					lastLibraries.addFirst(library);
				}

				if (library.getType() == ContainerType.CONTACT || library.getType() == ContainerType.REMOTE
						|| library.getType() == ContainerType.DEVICE) {
					List<Root> removeLibraries = new ArrayList<Root>();
					for (Root lib : libraries) {
						if (lib.getType() == ContainerType.CONTACT) {
							removeLibraries.add(lib);
						}
						if (lib.getType() == ContainerType.REMOTE) {
							removeLibraries.add(lib);
						}
						if (lib.getType() == ContainerType.DEVICE) {
							removeLibraries.add(lib);
						}
					}
					for (Root root : removeLibraries) {
						removeLibrary(root);
					}
				}

				libraries.add(library);

				controlEngine.fireValueEvent(Events.Library.LIBRARY_ROOT_ADDED, library);
			}
			// setSelectedTrackContainer(new SelectTrackContainerAction(library, null));
			// SET CURRENT PROFILE
			if(library instanceof ContactRoot){
				contactController.setCurrentProfile(library.getOwnerMail());
			}
		}
	}

	@ActionMethod(Actions.Library.LIBRARY_ROOT_REMOVED_ID)
	public void removeLibrary(Root library) {
		synchronized (librariesLock) {
			if (libraries.contains(library)) {
				libraries.remove(library);
				controlEngine.fireValueEvent(Events.Library.LIBRARY_ROOT_REMOVED, library);
				if (libraries.isEmpty()) {
					setSelectedTrackContainer(new SelectTrackContainerAction(controlEngine.get(Model.USER_ROOT), null));
				}
			}
		}
	}

	public void replaceRoot(Root loadedRoot) {
		synchronized (librariesLock) {
			if (libraries.remove(loadedRoot)) {
				libraries.add(loadedRoot);
			}
			if (lastLibraries.remove(loadedRoot)) {
				lastLibraries.add(loadedRoot);
			}
		}
	}

	public Root getLibraryForContact(String email) {
		Predicate ownerPredicate = new BeanPropertyValueEqualsPredicate("owner.email", email);
		return (Root) CollectionUtils.find(lastLibraries, ownerPredicate);
	}

	public boolean addRootFromCache(String email) {
		long expirationTime = System.currentTimeMillis() - (1000L * 60 * 10);
		for (Root root : lastLibraries) {
			if (root instanceof ContactRoot) {
				ContactRoot contactRoot = (ContactRoot) root;
				String ownerMail = contactRoot.getOwnerMail();
				long creationTime = contactRoot.getCreationTime();
				if (ownerMail.equals(email) && creationTime > expirationTime) {
					addLibrary(root);
					return true;
				}
			}
		}
		return false;
	}

	private boolean changeView(Root newroot, Root oldRoot) {
		if (controlEngine.get(Model.CURRENT_VIEW) == Views.LOCAL_MUSIC) {
			if (oldRoot != newroot && !oldRoot.equals(newroot)) {
				Sound.LIBRARY_SWITCH.play();
			}
		} else {
			switch (newroot.getType()) {
			case LOCAL:
			case CONTACT:
			case REMOTE:
				viewService.changeCurrentView(new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
				return true;
			default:
				break;
			}
		}
		return false;
	}

	@RequestMethod(Actions.Library.GET_LAST_LIBRARIES_ID)
	public List<Root> getLastLibraries() {
		return lastLibraries;
	}

}
