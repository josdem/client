package com.all.client.view.dnd;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class TestMyMusicDropListener extends UnitTestCase {
	MyMusicDropListener listener;
	@Mock
	private Folder folder;
	@Mock
	private Playlist playlist;
	@Mock
	private Track track;

	private ModelCollection model;

	@Before
	public void setup() {
		listener = new MyMusicDropListener(null, null);
		model = new ModelCollection(folder, playlist, track);
		model.setRemote(true);
	}

	@Test
	public void shouldAllowToDropRemoteContent() throws Exception {
		assertTrue(listener.validateDrop(model));
	}

}
