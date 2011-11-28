package com.all.client.view.music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.model.MockTrack;
import com.all.client.view.util.ComponentPaintValidator;
import com.all.core.model.Model;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public class TestDescriptionTable {
	private MockTrack track1 = new MockTrack("asdasd");
	private MockTrack track2 = new MockTrack("trgfdd");
	private MockTrack track3 = new MockTrack("prgsda");
	private ViewEngine viewEngine = mock(ViewEngine.class);
	private DescriptionTable table = new DescriptionTable(viewEngine);
	private List<Track> tracks;

	public static void setupLnF() throws Exception {
		UIManager.setLookAndFeel("com.all.plaf.hipecotech.HipecotechLookAndFeel");
	}

	@Before
	public void setup() throws Exception {
		setupLnF();
		tracks = new ArrayList<Track>();
		tracks.add(track1);
		tracks.add(track2);
		tracks.add(track3);
		Playlist mock = mock(Playlist.class);
		when(((TrackContainer) mock).getTracks()).thenReturn(tracks);
		table.setModel(mock);
	}

	@Ignore
	@Test
	// TODO i18nize
	public void shouldCreateADescriptionTable() throws Exception {
		assertTrue(table.getDragEnabled());
		assertEquals(16, table.getColumnCount());
		assertEquals(3, table.getRowCount());
		assertFalse(table.getShowHorizontalLines());
		assertFalse(table.getShowVerticalLines());
		assertEquals(20, table.getRowHeight());
		assertColumn(0, 20, 20, 20, "", false);
		assertColumn(1, 320, 70, Integer.MAX_VALUE, "Name", true);
		assertColumn(2, 178, 60, Integer.MAX_VALUE, "Artist", true);
		assertColumn(3, 60, 60, 60, "Plays", false);
		assertColumn(4, 70, 70, 70, "Rating", false);
		assertColumn(5, 70, 60, 100, "Time", true);
		assertColumn(6, 300, 70, Integer.MAX_VALUE, "Album", true);
		assertColumn(7, 100, 70, 120, "Bitrate", true);
		assertColumn(8, 120, 100, 160, "Date Added", true);
		assertColumn(9, 120, 70, 160, "Genre", true);
		assertColumn(10, 60, 60, 100, "Kind", true);
		assertColumn(11, 120, 100, 160, "Last Played", true);
		assertColumn(12, 120, 100, 160, "Last Skipped", true);
		assertColumn(13, 100, 60, 150, "Size", true);
		assertColumn(14, 60, 60, 60, "Skips", false);
		assertColumn(15, 60, 60, 100, "Year", true);
		assertFalse(table.isCellEditable(0, 0));
		assertEquals(track1, table.getValueAt(0, 0));
	}

	@Ignore
	@Test
	public void shouldPaintTableCorrectly() throws Exception {
		table.setSize(1024, 100);
		ComponentPaintValidator validator = new ComponentPaintValidator(table);
		// validator.verifyPixel(1, 10, new int[] { 245, 245, 245, 255 }, 0);
		// validator.verifyPixel(1, 30, new int[] { 255, 255, 255, 255 }, 0);
		// validator.verifyPixel(1, 50, new int[] { 245, 245, 245, 255 }, 0);
		table.selectAll();
		validator.refresh();
		validator.verifyPixel(1, 0, new int[] { 175, 205, 225, 255 }, 0);
		validator.verifyPixel(1, 19, new int[] { 255, 255, 255, 255 }, 0);
		validator.verifyPixel(1, 20, new int[] { 175, 205, 225, 255 }, 0);
		validator.verifyPixel(1, 39, new int[] { 255, 255, 255, 255 }, 0);
	}

	@Test
	public void shouldSetSizeForIndexColumnToDisplayNumberedLabelInsideIt() throws Exception {
		assertColumn(0, 30, 30, 30, "", false);
		TableColumn column = table.getColumnModel().getColumn(0);
		TableCellRenderer cellRenderer = column.getCellRenderer();
		Component component = cellRenderer.getTableCellRendererComponent(table, track1, false, true, 0, 0);
		assertTrue(component instanceof Container);
		Container container = (Container) component;
		assertEquals(2, container.getComponents().length);
		assertEquals("", ((JLabel) container.getComponent(0)).getText());
		assertEquals("1", ((JLabel) container.getComponent(1)).getText());
		assertTrue(container.getComponent(1).isVisible());
	}

	@Test
	public void shouldSetSizeForIndexColumnToShowOnlyTheIcon() throws Exception {
		Folder folder = mock(Folder.class);
		when(folder.getTracks()).thenReturn(tracks);
		table.setModel(folder);
		assertColumn(0, 20, 20, 20, "", false);
		TableColumn column = table.getColumnModel().getColumn(0);
		TableCellRenderer cellRenderer = column.getCellRenderer();
		Component component = cellRenderer.getTableCellRendererComponent(table, track1, false, true, 0, 0);
		assertTrue(component instanceof Container);
		Container container = (Container) component;
		assertEquals(2, container.getComponents().length);
		assertEquals("", ((JLabel) container.getComponent(0)).getText());
		assertFalse(container.getComponent(1).isVisible());
	}

	@Test
	public void shouldSelectRowIfNotSelectedAlready() throws Exception {
		table.selectedRow(1);
		assertEquals(1, table.getSelectedRow());
	}

	@Test
	public void shouldNotChangeSelectionIfWeTryToSelectARowThatIsAlreadySelected() throws Exception {
		table.getSelectionModel().setSelectionInterval(0, 2);
		table.selectedRow(1);
		assertEquals(3, table.getSelectedRows().length);
		assertEquals(1, table.getSelectedRows()[1]);
	}

	private void assertColumn(int columnIndex, int width, int minWidth, int maxWidth, String name, boolean resizeable) {
		assertEquals(maxWidth, table.getColumnModel().getColumn(columnIndex).getMaxWidth());
		assertEquals(minWidth, table.getColumnModel().getColumn(columnIndex).getMinWidth());
		assertEquals(name, table.getColumnModel().getColumn(columnIndex).getHeaderValue());
		assertEquals(resizeable, table.getColumnModel().getColumn(columnIndex).getResizable());
		assertEquals(width, table.getColumnModel().getColumn(columnIndex).getWidth());
	}

	@Test
	public void shouldGetTheDownloadsFromTheModel() throws Exception {
		Download download = mock(Download.class);
		String hashcode = "AADDSS";
		Map<String, Download> downloads = new HashMap<String, Download>();
		downloads.put(hashcode, download);

		when(viewEngine.get(Model.ALL_DOWNLOADS)).thenReturn(downloads);

		assertEquals(download, table.getDownload(hashcode));
	}
}
