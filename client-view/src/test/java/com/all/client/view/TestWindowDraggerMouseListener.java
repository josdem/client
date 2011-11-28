package com.all.client.view;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.SimpleGUITest;
import com.all.core.common.view.util.WindowDraggerMouseListener;

public class TestWindowDraggerMouseListener extends SimpleGUITest {
	@Mock
	JFrame mainFrame;
	@Mock
	JDialog dialog;
	@Mock
	MouseEvent mouseEvent;
	WindowDraggerMouseListener listener = new WindowDraggerMouseListener();

	@Before
	public void setup() {
		when(mouseEvent.getLocationOnScreen()).thenReturn(new Point(0, 0));
		when(mouseEvent.getSource()).thenReturn(mainFrame);
	}

	@Test
	public void shouldNotDoNothingIfWindowForComponentIsNull() throws Exception {
		listener.mouseDragged(mouseEvent);
		verify(mainFrame, never()).setLocation(anyInt(), anyInt());
		verify(dialog, never()).setLocation(anyInt(), anyInt());
	}

	@Test
	public void shouldDragMainframe() throws Exception {
		setupMainFrame(JFrame.NORMAL);

		listener.mousePressed(mouseEvent);
		listener.mouseDragged(mouseEvent);

		verify(mainFrame).setLocation(anyInt(), anyInt());
	}

	private void setupMainFrame(int state) {
		when(mainFrame.getLocationOnScreen()).thenReturn(new Point(0, 0));
		when(mainFrame.getParent()).thenReturn(mainFrame);
		when(mainFrame.getExtendedState()).thenReturn(state);
	}

	@Test
	public void shouldDragDialog() throws Exception {
		when(mouseEvent.getSource()).thenReturn(dialog);
		when(dialog.getLocationOnScreen()).thenReturn(new Point(0, 0));
		when(dialog.getParent()).thenReturn(dialog);

		listener.mousePressed(mouseEvent);
		listener.mouseDragged(mouseEvent);

		verify(dialog).setLocation(anyInt(), anyInt());
	}

	@Test
	public void shouldNotDragMainframeWhenIsMaximized() throws Exception {
		setupMainFrame(JFrame.MAXIMIZED_BOTH);

		listener.mousePressed(mouseEvent);
		listener.mouseDragged(mouseEvent);

		verify(mainFrame, never()).setLocation(anyInt(), anyInt());
	}
}
