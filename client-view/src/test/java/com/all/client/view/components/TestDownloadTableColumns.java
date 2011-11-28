package com.all.client.view.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;
import org.mockito.Mockito;

import com.all.client.UnitTestCase;
import com.all.client.model.Download;
import com.all.client.view.toolbar.downloads.DownloadTableColumns;
import com.all.client.view.toolbar.downloads.DownloadTableStyle;

public class TestDownloadTableColumns extends UnitTestCase {
	@Test
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void shouldGetAvailabilityColumnComparator() throws Exception {
		Comparator<Download> availabilityColumnComparator = (Comparator<Download>) DownloadTableColumns.AVAILABILITY
				.comparator(Mockito.mock(DownloadTableStyle.class));
		Download o1 = new Download();
		Download o2 = new Download();

		o1.setFreeNodes(12);
		o2.setFreeNodes(8);

		assertTrue(availabilityColumnComparator.compare(o1, o2) > 0);

		o1.setFreeNodes(8);
		assertEquals(0, availabilityColumnComparator.compare(o1, o2));

		o1.setBusyNodes(200);
		o2.setBusyNodes(4);
		assertTrue(availabilityColumnComparator.compare(o1, o2) > 0);
	}
}
