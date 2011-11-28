package com.all.client.view.dnd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JToggleButton;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.client.util.TrackRepository;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class TestDownloadButtonDropListener extends UnitTestCase {
	DownloadButtonDropListener listener;

	JToggleButton button = mock(JToggleButton.class);

	Point location = new Point(0, 0);
	Folder folder = mock(Folder.class);
	Playlist playlist = mock(Playlist.class);
	Track track = mock(Track.class);
	private List<Track> tracks = new ArrayList<Track>();

	@Mock
	private ViewEngine viewEngine;
	@Mock
	private TrackRepository trackRepository;

	@Before
	public void setup() {
		listener = new DownloadButtonDropListener(button, viewEngine);
		tracks.add(track);
		when(viewEngine.get(Model.TRACK_REPOSITORY)).thenReturn(trackRepository);
	}

	@Test
	public void shouldAcceptContentIfOnlyTracksWithoutFiles() throws Exception {
		ModelCollection model = new ModelCollection(tracks);
		when(trackRepository.isAllLocallyAvailable(model)).thenReturn(false);
		listener.dragEnter(new SimpleDraggedObject(model));
		assertTrue(listener.validateDrop(new SimpleDraggedObject(model), location));
	}

	@Test
	public void shouldRejectIfPlaylistOrFolderIsDragged() throws Exception {
		// Only tracks can be dragged to the download button, Folders and Playlist
		// are not permited
		assertFalse(listener.validateDrop(new SimpleDraggedObject(new ModelCollection(folder)), location));
		assertFalse(listener.validateDrop(new SimpleDraggedObject(new ModelCollection(playlist)), location));
		assertFalse(listener.validateDrop(new SimpleDraggedObject(new ModelCollection(playlist, folder)), location));
		assertFalse(listener.validateDrop(new SimpleDraggedObject(new ModelCollection(playlist, folder, track)), location));
	}

	@Test
	public void shouldAddTrackToDownloadsWhenDrop() throws Exception {
		listener.doDrop(new SimpleDraggedObject(new ModelCollection(tracks)), location);
		verify(viewEngine).sendValueAction(eq(Actions.Downloads.ADD_MODEL_COLLECTION), (ModelCollection) anyObject());
	}

	@Test
	public void shouldChangeStyleIfAllowed() throws Exception {
		when(button.isEnabled()).thenReturn(true);
		ModelCollection dragObject = new ModelCollection(track);
		listener.dragEnter(new SimpleDraggedObject(dragObject));
		listener.validateDrop(new SimpleDraggedObject(dragObject), location);
		listener.dragAllowedChanged(true);
		listener.updateLocation(location);
		verify(button).setEnabled(false);

		listener.dragExit(false);
		listener.validateDrop(new SimpleDraggedObject(dragObject), location);
		listener.doDrop(new SimpleDraggedObject(dragObject), location);
		verify(button).setEnabled(true);
	}
}
