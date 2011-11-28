package com.all.client.view.toolbar.downloads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.client.model.Download;
import com.all.client.model.DownloadCollection;
import com.all.client.model.DownloadTransferable;
import com.all.client.util.TrackRepository;
import com.all.core.model.Model;
import com.all.i18n.Messages;
import com.all.shared.model.TrackContainer;

public class TestDownloadTableTransferHandler extends UnitTestCase {
	@Mock
	private Messages messages;
	@Mock
	private ViewEngine viewEngine;

	DownloadTableTransferHandler transferHandler;
	// We are not using mocks due to excesive complexity that this shit can get
	// at.
	DownloadTable downloadTable;

	private String expectedColumnName = "expectedColumnName";
	@Mock
	private TrackRepository trackRepository;

	@SuppressWarnings("deprecation")
	@Before
	public void setup() throws Exception {
		when(messages.getMessage(anyString())).thenReturn(expectedColumnName);
		when(viewEngine.get(Model.TRACK_REPOSITORY)).thenReturn(trackRepository);
		downloadTable = new DownloadTable(messages, TrackContainer.EMPTY);

		transferHandler = new DownloadTableTransferHandler();
		ArrayList<Download> content = new ArrayList<Download>();

		// TODO improve this
		Download download = new Download();
		setValueToPrivateField(download, "trackId", "333");

		content.add(download);
		content.add(download);
		content.add(download);
		content.add(download);
		content.add(download);
		content.add(download);
		downloadTable.setModel(content);
		downloadTable.setRowSelectionInterval(1, 3);
	}

	@Test
	public void shouldHaveCommonBehaviorForTransferHandlers() throws Exception {
		assertTrue(transferHandler.importData(null));
		assertFalse(transferHandler.canImport((TransferSupport) null));
		assertEquals(TransferHandler.COPY, transferHandler.getSourceActions(downloadTable));
	}

	@Test
	public void shouldCreateTransferable() throws Exception {
		Transferable transferable = transferHandler.createTransferableFromTable(downloadTable);
		assertNotNull(transferable);
		assertTrue(transferable instanceof DownloadTransferable);
		DownloadCollection transferData = (DownloadCollection) transferable
				.getTransferData(DownloadTransferable.DOWNLOADS_FLAVOR);
		assertEquals(3, transferData.getDownloads().size());
	}

}
