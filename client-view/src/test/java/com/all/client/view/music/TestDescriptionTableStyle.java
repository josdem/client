package com.all.client.view.music;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ViewEngine;
import com.all.client.util.TrackRepository;
import com.all.core.model.Model;
import com.all.shared.model.Track;

public class TestDescriptionTableStyle {
	
	@InjectMocks
	private DescriptionTableStyle descriptionTableStyle = new DescriptionTableStyle();
	@Mock
	private Track track;
	@Mock
	private ViewEngine viewEngine;
	@Mock
	private TrackRepository trackRepository;
	@Mock
	private File file;
	
	private String hashcode = "hashcode";
	
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(track.getHashcode()).thenReturn(hashcode);
		when(viewEngine.get(Model.TRACK_REPOSITORY)).thenReturn(trackRepository);
	}
	
	@Test
	public void shouldSeeATrackInMyLibrary() throws Exception {
		when(trackRepository.getFile(track.getHashcode())).thenReturn(file);
		
		boolean trackInMyLibrary = descriptionTableStyle.isTrackInMyLibrary(track);
		
		assertTrue(trackInMyLibrary);
	}
	
	@Test
	public void shouldNotSeeATrackInMyLibrary() throws Exception {
		boolean trackInMyLibrary = descriptionTableStyle.isTrackInMyLibrary(track);
		
		assertFalse(trackInMyLibrary);
	}
}
