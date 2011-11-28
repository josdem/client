package com.all.client.view.toolbar.downloads;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;

import com.all.client.model.Download;
import com.all.client.view.toolbar.downloads.DownloadTableRowFilter;

public class TestDownloadTableRowFilter {
	private javax.swing.RowFilter.Entry<? extends TableModel, ? extends Integer> entryMock;
	private Download downloadInfo;

	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		entryMock = mock(RowFilter.Entry.class);
		downloadInfo = mock(Download.class);
	}

	@Test
	public void shouldIncludeEntry() throws Exception {
		String text = "keySearch";
		when(entryMock.getValue(0)).thenReturn(downloadInfo);
		when(downloadInfo.getDisplayName()).thenReturn(text);
		DownloadTableRowFilter downloadTableRowFilter = new DownloadTableRowFilter(text);
		downloadTableRowFilter.include(entryMock);
		assertTrue(downloadTableRowFilter.include(entryMock));
	}
}
