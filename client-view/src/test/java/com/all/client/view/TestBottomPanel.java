package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ViewEngine;
import com.all.client.SimpleGUITest;
import com.all.client.model.Download;
import com.all.core.common.model.ApplicationModel;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.event.ValueEvent;

public class TestBottomPanel extends SimpleGUITest {

	@InjectMocks
	private BottomPanel bottomPanel = new BottomPanel();
	@Mock
	private Download downloadOne;
	@Mock
	private Download downloadTwo;
	@Mock
	private ViewEngine viewEngine;
	
	private BorderLayout bottomLayout;

	@Before
	@SuppressWarnings("unchecked")
	public void initialize() {
		MockitoAnnotations.initMocks(this);

		bottomPanel.setMessages(messages);
		bottomLayout = (BorderLayout) bottomPanel.getLayout();
		when(viewEngine.get(Model.CURRENT_DOWNLOAD_IDS)).thenReturn(Collections.EMPTY_LIST);
	}

	@Test
	public void shouldCreateANewBottomPanel() throws Exception {
		assertEquals(new Dimension(1016, 26), bottomPanel.getSize());
	}

	@Test
	public void shouldHaveBorderLayout() throws Exception {
		assertTrue(bottomPanel.getLayout() instanceof BorderLayout);
	}

	@Test
	public void shouldHaveStatusPanelAtLeftCorner() throws Exception {
		JPanel statusPanel = (JPanel) bottomLayout.getLayoutComponent(BorderLayout.WEST);
		assertNotNull(statusPanel);
		Dimension dimension = new Dimension(202, 26);
		assertEquals(dimension, statusPanel.getSize());
		assertEquals(dimension, statusPanel.getPreferredSize());
		assertEquals(dimension, statusPanel.getMaximumSize());
		assertEquals(dimension, statusPanel.getMinimumSize());

		assertNull(statusPanel.getLayout());
		JLabel iconLabel = (JLabel) statusPanel.getComponent(0);
		assertEquals("iconLabelOffline", iconLabel.getName());
		Rectangle iconBounds = new Rectangle(0, 0, 30, 26);
		assertEquals(iconBounds, iconLabel.getBounds());
		JLabel statusLabel = (JLabel) statusPanel.getComponent(1);
		Rectangle statusLabelBounds = new Rectangle(40, -1, 92, 28);
		assertEquals(statusLabelBounds, statusLabel.getBounds());
		JPanel separatorPanel = (JPanel) statusPanel.getComponent(3);
		Rectangle separatorBounds = new Rectangle(200, -3, 2, 30);
		assertEquals(separatorBounds, separatorPanel.getBounds());
		assertEquals("verticalSeparator", separatorPanel.getName());
	}

	@Test
	public void shouldUpdateLoginAndLogOutStatus() throws Exception {
		when(viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(true);

		bottomPanel.onAppStarted();

		JLabel statusLabel = (JLabel) bottomPanel.getStatusPanel().getComponentAt(50, 2);
		assertEquals(messages.getMessage("BottomPanel.online"), statusLabel.getText());
		JLabel iconLabel = (JLabel) bottomPanel.getStatusPanel().getComponentAt(2, 2);
		assertEquals("iconLabelOnline", iconLabel.getName());

		bottomPanel.onUserLogout();
		assertEquals(messages.getMessage("BottomPanel.offline"), statusLabel.getText());
		assertEquals("iconLabelOffline", iconLabel.getName());
		when(viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)).thenReturn(false);
		bottomPanel.onAppStarted();
		assertEquals(messages.getMessage("BottomPanel.offline"), statusLabel.getText());
		assertEquals("iconLabelOffline", iconLabel.getName());
	}

	@Test
	public void shouldSetDownloadsController() throws Exception {
		when(downloadOne.getDownloadId()).thenReturn("1111");
		when(downloadOne.getStatus()).thenReturn(DownloadState.Downloading);
		when(downloadTwo.getDownloadId()).thenReturn("2222");
		when(downloadTwo.getStatus()).thenReturn(DownloadState.Downloading);

		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadOne));
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadOne));
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadOne));
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadOne));
		
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadTwo));
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadTwo));
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadTwo));
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadTwo));
		
		assertEquals(2, bottomPanel.downloadsInProcess.size());

		bottomPanel.onDownloadCompleted(new ValueEvent<Download>(downloadTwo));

		assertEquals(1, bottomPanel.downloadsInProcess.size());
		when(downloadOne.getStatus()).thenReturn(DownloadState.Canceled);

		bottomPanel.onDownloadAllModified();

		assertEquals(0, bottomPanel.downloadsInProcess.size());
	}

	@Test
	public void shouldCalculateRateOfCurrentDownloads() throws Exception {
		when(downloadOne.getDownloadId()).thenReturn("1111");
		when(downloadOne.getRate()).thenReturn(24L);
		when(downloadOne.getStatus()).thenReturn(DownloadState.Downloading);
		when(downloadOne.getDownloadId()).thenReturn("2222");
		when(downloadTwo.getRate()).thenReturn(75L);
		when(downloadTwo.getStatus()).thenReturn(DownloadState.Downloading);

		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadOne));
		assertEquals(24, bottomPanel.calculateRateOfCurrentDownloads());
		bottomPanel.onDownloadUpdated(new ValueEvent<Download>(downloadTwo));
		assertEquals(99, bottomPanel.calculateRateOfCurrentDownloads());
	}

}
