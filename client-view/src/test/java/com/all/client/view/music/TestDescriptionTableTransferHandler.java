package com.all.client.view.music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.TransferHandler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.client.model.MockTrack;
import com.all.client.model.ModelTransfereable;
import com.all.client.util.TrackRepository;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;

public class TestDescriptionTableTransferHandler extends UnitTestCase {
	private ViewEngine viewEngine = mock(ViewEngine.class);
	private TrackRepository trackRepository = mock(TrackRepository.class);
	private DescriptionTableTransferHandler handler = new DescriptionTableTransferHandler(viewEngine);

	@Mock
	private DescriptionTable table;

	@Before
	public void setupFileManager() {
		when(viewEngine.get(Model.TRACK_REPOSITORY)).thenReturn(trackRepository);
		when(trackRepository.isLocallyAvailable(anyString())).thenReturn(true);
	}

	@Test
	public void shouldImplementImportantMethods() throws Exception {
		assertTrue(handler.importData(null));
		assertFalse(handler.canImport(null));
		assertEquals(TransferHandler.COPY, handler.getSourceActions(null));
	}

	@Test
	public void shouldCreateTransferable() throws Exception {
		when(table.getSelectedRows()).thenReturn(new int[] { 1, 3, 4 });
		MockTrack track1 = new MockTrack("1");
		when(table.getValue(1)).thenReturn(track1);
		when(table.getValue(3)).thenReturn(new MockTrack("3"));
		when(table.getValue(4)).thenReturn(new MockTrack("4"));

		ModelTransfereable transferable = handler.createTransferableFromTable(table);
		assertNotNull(transferable);
		ModelCollection model = getModelCollection(transferable);
		assertEquals(3, model.getTracks().size());
		assertTrue(model.getTracks().contains(track1));

		Transferable trans = handler.createTransferable(table);
		assertNotNull(trans);
		model = (ModelCollection) trans.getTransferData(ModelTransfereable.MODEL_FLAVOR);
		assertEquals(3, model.getTracks().size());
		assertTrue(model.getTracks().contains(track1));
	}

	private ModelCollection getModelCollection(ModelTransfereable transferable) throws UnsupportedFlavorException,
			IOException {
		ModelCollection model = (ModelCollection) transferable.getTransferData(ModelTransfereable.MODEL_FLAVOR);
		return model;
	}

	@Test
	public void shouldCreateRemoteModelCollection() throws Exception {
		int selectedRow = 7;
		when(table.getSelectedRows()).thenReturn(new int[] { selectedRow });
		MockTrack track = new MockTrack("1");
		when(table.getValue(selectedRow)).thenReturn(track);

		ModelTransfereable transferable = handler.createTransferableFromTable(table);
		ModelCollection modelCollection = getModelCollection(transferable);
		assertTrue(modelCollection.isRemote());
	}

}
