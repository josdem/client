package com.all.client.services.delegates;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.model.LocalRoot;
import com.all.client.model.LocalTrash;
import com.all.core.actions.ModelDeleteAction.DeleteMode;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.event.ValueEvent;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;

public class TestDeleteFolderPlaylist {

	private ModelCollection modelCollection = new ModelCollection();
	private LocalTrash mockTrash = mock(LocalTrash.class);
	private LocalFolder folder;
	private LocalPlaylist playlist;
	private LocalRoot mockRoot = mock(LocalRoot.class);

	@Mock
	private ControlEngine controlEngine;

	@InjectMocks
	private DeleteDelegate deleteAction = new DeleteDelegate();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		playlist = new LocalPlaylist("playlist1");
		folder = new LocalFolder("folder1");
	}

	@Test
	public void shouldDeleteAFolder() throws Exception {
		folder.add(playlist);
		modelCollection.getFolders().add(folder);

		deleteAction.doDelete(mockTrash, mockRoot, mockRoot, modelCollection, DeleteMode.ONLY_REFERENCES);

		verify(mockTrash).addFolders(modelCollection.getFolders());
		verify(controlEngine).fireEvent(Events.Library.TREE_STRUCTURE_CHANGED, new ValueEvent<Root>(mockRoot));
		verify(controlEngine).fireEvent(eq(Events.Library.CONTAINER_MODIFIED), any(ContainerModifiedEvent.class));
	}

	@Test
	public void shouldDeleteAllFolders() throws Exception {
		folder.add(playlist);
		modelCollection.getFolders().add(folder);
		modelCollection.getFolders().add(folder);

		deleteAction.doDelete(mockTrash, mockRoot, mockRoot, modelCollection, DeleteMode.REF_AND_FILES);

		verify(mockTrash, times(2)).addFolderWithReferences(folder);
	}

	@Test
	public void shouldDeletePlaylist() throws Exception {
		modelCollection.getPlaylists().add(playlist);
		modelCollection.getFolders().add(folder);

		deleteAction.doDelete(mockTrash, mockRoot, mockRoot, modelCollection, DeleteMode.ONLY_REFERENCES);

		verify(mockTrash).addPlayLists(modelCollection.getPlaylists());
	}

	@Test
	public void shouldDeleteAllPlaylist() throws Exception {
		modelCollection.getPlaylists().add(playlist);
		modelCollection.getFolders().add(folder);

		deleteAction.doDelete(mockTrash, mockRoot, mockRoot, modelCollection, DeleteMode.ONLY_REFERENCES);

		verify(mockTrash, never()).addFolderWithReferences(folder);
		verify(mockTrash, never()).addFolder(folder);
		verify(mockTrash).addPlayLists(modelCollection.getPlaylists());
	}

}