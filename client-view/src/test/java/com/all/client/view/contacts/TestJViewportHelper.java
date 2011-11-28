package com.all.client.view.contacts;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class TestJViewportHelper {
	@InjectMocks
	private JViewportHelper jViewportHelper = new JViewportHelper();
	@Mock
	private JScrollPane jScrollPane;
	@Mock
	private JViewport viewPort;
	@Mock
	private Point point;
	
	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		when(jScrollPane.getViewport()).thenReturn(viewPort);
	}
	
	@Test
	public void shouldGetViewport() throws Exception {
		JViewport result = jViewportHelper.getViewport(jScrollPane);
		assertEquals(viewPort, result);
	}
	
	@Test
	public void shouldSetPosition() throws Exception {
		jViewportHelper.setViewPosition(point);
		verify(jScrollPane).getViewport();
		verify(viewPort).setViewPosition(point);
	}
}
