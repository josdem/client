package com.all.client.view.download;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.all.client.UnitTestCase;
import com.all.client.model.Download;
import com.all.downloader.bean.DownloadState;

public class TestAvailabilityRenderer extends UnitTestCase {

	@SuppressWarnings("deprecation")
	Download value = new Download();

	@Test
	public void shouldShowSeedsAndLeechesWhenIsDownloading() throws Exception {
		AvailabilityRenderer renderer = new AvailabilityRenderer();
		value.setStatus(DownloadState.Downloading);
		value.setFreeNodes(23);
		value.setBusyNodes(4);

		Object filter = renderer.filter(value, 0, 0);
		assertEquals("23 / 4", filter);

		value.setFreeNodes(1234);
		assertEquals("1,234 / 4", renderer.filter(value, 0, 0));
	}

	@Test
	public void shouldntShowAnythingWhenNotDownloading() throws Exception {
		AvailabilityRenderer renderer = new AvailabilityRenderer();
		value.setStatus(DownloadState.Queued);
		value.setFreeNodes(23);
		value.setBusyNodes(4);

		Object filter = renderer.filter(value, 0, 0);
		assertEquals("", filter);
	}
}
