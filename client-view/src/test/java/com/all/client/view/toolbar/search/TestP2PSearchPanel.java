package com.all.client.view.toolbar.search;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.client.view.dialog.DialogFactory;

public class TestP2PSearchPanel {

	@InjectMocks
	private P2PSearchPanel p2pSearchPanel = new P2PSearchPanel();
	@Mock
	@SuppressWarnings("unused")
	private DialogFactory dialogFactory;
	@Mock
	private P2PSearchTable searchTable;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldRepaintTableIndexOnDownloadAdded() throws Exception {
		p2pSearchPanel.onDownloadAdded();
		verify(searchTable).repaintIndex();
	}

	@Test
	public void shouldRepaintTableIndexOnDownloadRemoved() throws Exception {
		p2pSearchPanel.onDownloadRemoved();
		verify(searchTable).repaintIndex();
	}

	@Test
	public void shouldRepaintTableIndexOnDownloadUpdated() throws Exception {
		p2pSearchPanel.onDownloadUpdated();
		verify(searchTable).repaintIndex();
	}

	@Test
	public void shouldRepaintTableIndexOnDownloadAllModified() throws Exception {
		p2pSearchPanel.onDownloadAllModified();
		verify(searchTable).repaintIndex();
	}

}
