package com.all.client.view.download;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.model.Download;
import com.all.client.view.toolbar.downloads.DownloadTableStyle;
import com.all.downloader.bean.DownloadState;
import com.all.i18n.Messages;

public class TestStatusRenderer extends UnitTestCase {
	@Mock
	private Messages messages;

	@Test
	public void shouldExecuteStatusRenderer() throws Exception {
		DownloadTableStyle style = new DownloadTableStyle(messages);
		StatusRenderer renderer = new StatusRenderer(style);
		@SuppressWarnings("deprecation")
		Download download = new Download();
		DownloadState state = DownloadState.Error;
		download.setStatus(state);

		renderer.filter(download, 0, 0);
		verify(messages).getMessage(state.getKey());
	}

}
