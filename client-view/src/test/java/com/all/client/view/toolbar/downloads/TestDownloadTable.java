package com.all.client.view.toolbar.downloads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.model.Download;
import com.all.client.view.util.ComponentPaintValidator;
import com.all.i18n.Messages;
import com.all.shared.model.TrackContainer;

@SuppressWarnings("deprecation")
public class TestDownloadTable extends UnitTestCase {
	private static final String AVAILABILITY_COLUMN_NAME = "downloadTableColumn.availability.title";

	private static final String NAME_COLUMN_NAME = "downloadTableColumn.name.title";

	private static final String SIZE_COLUMN_NAME = "downloadTableColumn.size.title";

	private static final String STATUS_COLUMN_NAME = "downloadTableColumn.status.title";

	private static final String TIME_COLUMN_NAME = "downloadTableColumn.time.title";

	private static final String PROGRESS_COLUMN_NAME = "downloadTableColumn.progress.title";

	@Mock
	private Messages messages;

	private static final String BLANK_COLUMN_HEADER = "";
	private DownloadTable table;
	private Download downloadInfo = new Download();

	@Before
	public void testname() throws Exception {
		UIManager.setLookAndFeel("com.all.plaf.hipecotech.HipecotechLookAndFeel");
		Thread.sleep(500);
		when(messages.getMessage(anyString())).thenReturn(PROGRESS_COLUMN_NAME);
		table = new DownloadTable(messages, TrackContainer.EMPTY);
		List<Download> content = new ArrayList<Download>();
		content.add(downloadInfo);
		table.setModel(content);
	}

	@Test
	public void shouldCreateADownloadTable() throws Exception {
		assertEquals(7, table.getColumnCount());
		assertEquals(1, table.getRowCount());
		assertFalse(table.getShowHorizontalLines());
		assertFalse(table.getShowVerticalLines());
		assertEquals(20, table.getRowHeight());
		assertColumn(0, 30, 30, 30, BLANK_COLUMN_HEADER, false);

		assertColumn(1, 230, 60, Integer.MAX_VALUE, NAME_COLUMN_NAME, true);
		assertColumn(2, 230, 230, 230, PROGRESS_COLUMN_NAME, false);
		assertColumn(3, 210, 210, 210, STATUS_COLUMN_NAME, false);
		assertColumn(4, 100, 60, 140, SIZE_COLUMN_NAME, true);
		assertColumn(5, 70, 70, 70, TIME_COLUMN_NAME, false);
		assertColumn(6, 80, 80, 80, AVAILABILITY_COLUMN_NAME, false);
		assertFalse(table.isCellEditable(0, 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBeAbleToSetModelOnceInitialized() throws Exception {
		table.setModel(new DefaultTableModel());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotBeAbleToSetColumnModelOnceInitialized() throws Exception {
		table.setColumnModel(new DefaultTableColumnModel());
	}

	@Test
	public void shouldSetTheTableModelUsingDownloadInfoList() throws Exception {
		List<Download> content = new ArrayList<Download>();
		content.add(new Download());
		content.add(new Download());
		content.add(new Download());
		table.setModel(content);
		assertEquals(3, table.getRowCount());
	}

	private void assertColumn(int columnIndex, int width, int minWidth, int maxWidth, String name, boolean resizeable) {
		assertEquals(maxWidth, table.getColumnModel().getColumn(columnIndex).getMaxWidth());
		assertEquals(minWidth, table.getColumnModel().getColumn(columnIndex).getMinWidth());
		assertEquals(name, table.getColumnModel().getColumn(columnIndex).getHeaderValue());
		assertEquals(resizeable, table.getColumnModel().getColumn(columnIndex).getResizable());
		assertEquals(width, table.getColumnModel().getColumn(columnIndex).getWidth());
	}

	@Ignore
	@Test
	public void shouldPaintCorrectly() throws Exception {
		table.setSize(1024, 100);
		shouldSetTheTableModelUsingDownloadInfoList();
		ComponentPaintValidator validator = new ComponentPaintValidator(table);
		// validator.verifyPixel(1, 1, new int[] { 245, 245, 245, 255 }, 0);
		validator.verifyPixel(1, 20, new int[] { 255, 255, 255, 255 }, 0);
		// validator.verifyPixel(1, 40, new int[] { 245, 245, 245, 255 }, 0);
		table.selectAll();
		validator.refresh();
		validator.verifyPixel(1, 0, new int[] { 210, 210, 210, 255 }, 0);
		validator.verifyPixel(1, 19, new int[] { 255, 255, 255, 255 }, 0);
		validator.verifyPixel(1, 20, new int[] { 210, 210, 210, 255 }, 0);
		validator.verifyPixel(1, 39, new int[] { 255, 255, 255, 255 }, 0);
	}

	@Test
	public void shouldGetDownloadStyle() throws Exception {
		DownloadTableStyle style = table.getStyle();
		assertNotNull(style);
	}

	@Test
	public void shouldAddRow() throws Exception {
		table.addRow(new Download());
		assertEquals(2, table.getRowCount());
	}

	@Test
	public void shouldRemoveRow() throws Exception {
		table.removeRow(downloadInfo);
		assertEquals(0, table.getRowCount());
	}

	@Test
	public void shouldNotifyADownloadInfoHasBeenModified() throws Exception {
		table.updateRow(downloadInfo);
		Set<Download> modifiedRows = table.getUpdatedRows();
		assertEquals(1, modifiedRows.size());
		assertEquals(downloadInfo, modifiedRows.iterator().next());
		modifiedRows = table.getUpdatedRows();
		assertNull(modifiedRows);
	}

	@Test
	public void shouldUpdateTableWithPendingChanges() throws Exception {
		TableModelListener listener = mock(TableModelListener.class);
		table.getModel().addTableModelListener(listener);
		table.updateRow(downloadInfo);
		table.updateTable();
		verify(listener).tableChanged(isA(TableModelEvent.class));
	}

	@Test
	public void shouldNotUpdateIfNoMorePendingChanges() throws Exception {
		TableModelListener listener = mock(TableModelListener.class);
		table.getModel().addTableModelListener(listener);
		table.updateTable();
		verify(listener, never()).tableChanged(isA(TableModelEvent.class));
	}

	@Test
	public void shouldPaintHeaderCorrectly() throws Exception {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setSize(100, 100);

		JScrollPane scroll = new JScrollPane(table);

		panel.add(scroll, BorderLayout.CENTER);

		ComponentPaintValidator panelValidator = new ComponentPaintValidator(panel);
		scroll.scrollRectToVisible(new Rectangle(1, 0, 100, 100));
		scroll.scrollRectToVisible(new Rectangle(2, 0, 100, 100));
		scroll.scrollRectToVisible(new Rectangle(3, 0, 100, 100));
		scroll.scrollRectToVisible(new Rectangle(4, 0, 100, 100));
		scroll.scrollRectToVisible(new Rectangle(5, 0, 100, 100));
		panelValidator.refresh();
	}
}
