package com.all.client.view.music;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;

import org.junit.Test;

public class TestDescriptionTableDragOverListener {
	DescriptionTable descriptionTable = mock(DescriptionTable.class);
	DescriptionTableDragOverListener listener = new DescriptionTableDragOverListener(descriptionTable);

	@Test
	public void shouldTestDescriptionTableDragOverListener1() throws Exception {
		listener.dragEnter(null);
		// This method should do nothing.
		verify(descriptionTable, never()).setDropRowIndex(anyInt());
	}

	@Test
	public void shouldTestDescriptionTableDragOverListener2() throws Exception {
		listener.dragExit(false);
		verify(descriptionTable, times(1)).setDropRowIndex(-1);
	}

	@Test
	public void shouldTestDescriptionTableDragOverListener3() throws Exception {
		listener.dragAllowedChanged(false);
		listener.updateLocation(new Point());
		verify(descriptionTable, never()).setDropRowIndex(anyInt());
	}

	@Test
	public void shouldTestDescriptionTableDragOverListener4() throws Exception {
		Point location = new Point(10, 52);
		when(descriptionTable.getRowHeight()).thenReturn(10);
		when(descriptionTable.getRowIndexAtLocation(location)).thenReturn(5);
		listener.dragAllowedChanged(true);
		listener.updateLocation(location);
		verify(descriptionTable, times(1)).setDropRowIndex(5);
	}
}
