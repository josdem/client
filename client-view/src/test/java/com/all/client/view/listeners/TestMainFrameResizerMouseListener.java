package com.all.client.view.listeners;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;


public class TestMainFrameResizerMouseListener extends UnitTestCase {

	private MainFrameResizerMouseListener mainFrameResizerMouseListener;
	
	@Mock
	private JFrame frameMock;

	@Mock
	private MouseEvent mouseEventMock;
	
	private Point fakePoint;
	
	private Point fakeFramePoint;
	
	@Before
	public void createMainFrameResizeMouseListener() {
		
		fakePoint= new Point(10,10);
		
		fakeFramePoint= new Point(0,0);

		when(mouseEventMock.getLocationOnScreen()).thenReturn(fakePoint);
		when(mouseEventMock.getPoint()).thenReturn(fakePoint);
		when(frameMock.getLocationOnScreen()).thenReturn(fakeFramePoint);
		
		mainFrameResizerMouseListener= new MainFrameResizerMouseListener(frameMock);
		
	}

	@Test
	public void shouldCreateMainFrameResizerMouseListener() throws Exception {
		assertNotNull(mainFrameResizerMouseListener);
	}
	
	@Test
	public void shouldCatchMouseReleaseEvent() throws Exception {
		mainFrameResizerMouseListener.mouseReleased(mouseEventMock);
	}
	
	@Test
	public void shouldCatchMousePressedEvent() throws Exception {
		mainFrameResizerMouseListener.mousePressed(mouseEventMock);
	}
	
	@Test
	public void shouldCatchMouseClickedEvent() throws Exception {
		mainFrameResizerMouseListener.mouseClicked(mouseEventMock);
	}
	
	@Test
	public void shouldCatchMouseDraggedEvent() throws Exception {
		mainFrameResizerMouseListener.init(); //necessary to avoid NullPointerException
		mainFrameResizerMouseListener.mouseDragged(mouseEventMock);
	}
	
	@Test
	public void shouldCatchMouseEnteredEvent() throws Exception {
		mainFrameResizerMouseListener.mouseEntered(mouseEventMock);
	}
	
	@Test
	public void shouldCatchMouseExitedEvent() throws Exception {
		mainFrameResizerMouseListener.mouseExited(mouseEventMock);
	}
	
	@Test
	public void shouldCatchMouseMovedEvent() throws Exception {
		mainFrameResizerMouseListener.mouseMoved(mouseEventMock);
	}
	
}
