package com.all.client.view.music;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JCheckBox;
import javax.swing.JTable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.shared.model.Track;

public class TestDescriptionTableRenderers {
	@SuppressWarnings("unused")
	@InjectMocks
	private DescriptionTableRenderers descriptionTableRenderers = new DescriptionTableRenderers();
	@Mock
	private DescriptionTableStyle style;
	@Mock
	private JTable table;
	@Mock
	private Track track;
	private DescriptionTableNameRenderer nameRenderer;
	boolean isSelected;
	boolean hasFocus;
	int row = 1;
	int column = 1;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(style.isTrackInMyLibrary(track)).thenReturn(true);

		nameRenderer = (DescriptionTableNameRenderer) DescriptionTableRenderers.getNameRenderer(style);
	}

	@Test
	public void shouldShowCheckBoxes() throws Exception {
		when(style.getShowCheckboxes()).thenReturn(true);

		Component[] components = getPanelContainer(isSelected, hasFocus, row, column);
		Component component = null;
		for (int i = 0; i < components.length; i++) {
			component = components[i];
			if (component instanceof JCheckBox) {
				assertTrue(component.isVisible());
			}
		}
	}

	@Test
	public void shouldHideCheckBoxes() throws Exception {
		when(style.getHideCheckboxes()).thenReturn(false);

		Component[] components = getPanelContainer(isSelected, hasFocus, row, column);
		Component component = null;
		for (int i = 0; i < components.length; i++) {
			component = components[i];
			if (component instanceof JCheckBox) {
				assertFalse(component.isVisible());
			}
		}
	}

	private Component[] getPanelContainer(boolean isSelected, boolean hasFocus, int row, int column) {
		Component panel = nameRenderer.getTableCellRendererComponent(table, track, isSelected, hasFocus, row, column);
		Component[] components = ((Container) panel).getComponents();
		return components;
	}
}
