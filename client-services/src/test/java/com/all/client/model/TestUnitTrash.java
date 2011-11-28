package com.all.client.model;

//import static org.junit.Assert.*;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.shared.model.Track;


public class TestUnitTrash extends UnitTestCase {
	@Mock
	private LocalModelDao dao;
	@Mock
	private Track track;
	@Mock
	private PlaylistTrack playlistTrack; 
	
	private LocalTrash trash;
	
	@Before
	public void init() {
		trash = new LocalTrash(dao);
	}
		
	@SuppressWarnings("unchecked")
	@Test
	public void shouldDeleteTracks() throws Exception {
		when(dao.findAll(anyString(), anyMap(), anyBoolean())).thenReturn(Arrays.asList(playlistTrack));
		
		trash.addTrack(track);
		
		verify(dao).delete(track);
		verify(dao).delete(playlistTrack);
	}
}
