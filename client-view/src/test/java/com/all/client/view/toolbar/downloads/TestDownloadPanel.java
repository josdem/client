package com.all.client.view.toolbar.downloads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableModel;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.ViewEngineConfigurator;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.view.dialog.DeleteDownloadsDialog.DeleteDownloadsAction;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;

public class TestDownloadPanel {

	private static final int USERPREF_COLUMN = 1;

	@InjectMocks
	private DownloadPanel downloadPanel;
	@Mock
	private DownloadTable downloadTable;
	@Mock
	private ListSelectionModel selectionModel;
	@Mock
	private TableModel tableModel;
	@Mock
	private Download download;
	@Mock
	private DownloadButtonsPanel buttonPanel;
	@Mock
	private JButton pauseButton;
	@Mock
	private JButton resumeButton;
	@Mock
	private JButton cleanUpButton;
	@Mock
	private JButton deleteButton;
	@Mock
	private ViewEngine viewEngine;
	@Mock
	private DialogFactory dialogFactory;

	@Before
	public void setup() {
		Messages messages = mock(Messages.class);

		downloadPanel = new DownloadPanel(messages);

		MockitoAnnotations.initMocks(this);

		when(downloadTable.getSelectionModel()).thenReturn(selectionModel);
		when(downloadTable.getModel()).thenReturn(tableModel);

		when(buttonPanel.getCleanUpButton()).thenReturn(cleanUpButton);
		when(buttonPanel.getDeleteButton()).thenReturn(deleteButton);
		when(buttonPanel.getPauseButton()).thenReturn(pauseButton);
		when(buttonPanel.getResumeButton()).thenReturn(resumeButton);

		when(dialogFactory.showDeleteDownloadsDialog()).thenReturn(DeleteDownloadsAction.DELETE_DOWNLOAD);

		downloadPanel.wireDownloadsController();
		downloadPanel.setDownloadButtonsPanel(buttonPanel);
	}

	@Test
	public void shouldAddDownloadOnDownloadAdded() throws Exception {
		downloadPanel.onDownloadAdded(new ValueEvent<Download>(download));
		verify(downloadTable).addRow(download);
	}

	@Test
	public void shouldDeleteDownloadOnDownloadDeleted() throws Exception {
		downloadPanel.onDownloadRemoved(new ValueEvent<Download>(download));
		verify(downloadTable).removeRow(download);
	}

	@Test
	public void shouldUpdateRowOnDownloadUpdated() throws Exception {
		downloadPanel.onDownloadUpdated(new ValueEvent<Download>(download));
		verify(downloadTable).updateRow(download);
	}

	@Test
	public void shouldEnableDragAndDropsFROMtheTable() throws Exception {
		downloadPanel.setupDrags(mock(MultiLayerDropTargetListener.class), mock(ViewEngine.class));
		ValueContainerMatcher<DownloadTableTransferHandler> matcher = new ValueContainerMatcher<DownloadTableTransferHandler>();
		verify(downloadTable).setDragEnabled(true);
		verify(downloadTable).setTransferHandler(Matchers.argThat(matcher));
		verify(downloadTable).setDropTarget(null);

		DownloadTableTransferHandler transferHandler = matcher.value;
		assertNotNull(transferHandler);
	}

	@SuppressWarnings({ "unchecked" })
	public void shouldBindUserPreference() throws Exception {
		ViewEngineConfigurator appControlConfigurer = mock(ViewEngineConfigurator.class);
		ViewEngine viewEngine = mock(ViewEngine.class);
		SortKey sortKey = new SortKey(USERPREF_COLUMN, SortOrder.ASCENDING);
		when(viewEngine.get(Model.UserPreference.DOWNLOAD_TABLE_SORT_COLUMN)).thenReturn(sortKey);

		RowSorter rowSorter = mock(RowSorter.class);
		when(downloadTable.getRowSorter()).thenReturn(rowSorter);

		// Intercept listener to rowSorter
		ValueContainerMatcher<RowSorterListener> matcher = new ValueContainerMatcher<RowSorterListener>();
		doNothing().when(rowSorter).addRowSorterListener(Matchers.argThat(matcher));

		// Bind userPreference
		downloadPanel.setEngine(viewEngine, appControlConfigurer);
		verify(rowSorter, times(1)).setSortKeys(Matchers.argThat(new SortKeyMatcher()));

		List<SortKey> sortkeys = new ArrayList<SortKey>();
		int newSortedColumn = 4;
		SortKey newSortKey = new SortKey(newSortedColumn, SortOrder.DESCENDING);
		sortkeys.add(newSortKey);
		when(rowSorter.getSortKeys()).thenReturn(sortkeys);

		// SimmulateEvent
		matcher.value.sorterChanged(new RowSorterEvent(rowSorter));

		verify(viewEngine).sendValueAction(Actions.UserPreference.SET_DOWNLOAD_TABLE_SORT_COLUMN, newSortKey);
	}

	@Test
	public void shouldEnablePauseAndDisableResumeIfSelectedDownloadsAreDownloading() throws Exception {
		DownloadState[] states = { DownloadState.Downloading, DownloadState.Downloading };
		setDownloadsSelectedExpectations(2, states);

		downloadPanel.enableAvailableActions();

		verifyPauseAndResumeEnabled(true, false);
	}

	@Test
	public void shouldEnableResumeAndDisablePauseIfSelectedDownloadsArePaused() throws Exception {
		DownloadState[] states = { DownloadState.Paused, DownloadState.Paused };
		setDownloadsSelectedExpectations(2, states);

		downloadPanel.enableAvailableActions();

		verifyPauseAndResumeEnabled(false, true);
	}

	@Test
	public void shouldEnableResumeAndPauseIfSelectedDownloadsAreInPausedAndDownloadingState() throws Exception {
		DownloadState[] states = { DownloadState.Paused, DownloadState.Downloading };
		setDownloadsSelectedExpectations(2, states);

		downloadPanel.enableAvailableActions();

		verifyPauseAndResumeEnabled(true, true);
	}

	@Test
	public void shouldDisablePauseAndResumeIfSelectedDownloadsAreNotInPausedOrDownloadingState() throws Exception {
		DownloadState[] states = { DownloadState.Queued, DownloadState.Complete };
		setDownloadsSelectedExpectations(2, states);

		downloadPanel.enableAvailableActions();

		verifyPauseAndResumeEnabled(false, false);
	}

	@Test
	public void shouldResumeSelectedDownloads() throws Exception {
		DownloadState[] states = { DownloadState.Paused, DownloadState.Downloading };
		List<Download> selectedDownloads = setDownloadsSelectedExpectations(2, states);

		downloadPanel.resumeDownloads();

		verify(viewEngine).sendValueAction(Actions.Downloads.RESUME, selectedDownloads);
	}

	@Test
	public void shouldDeleteSomeDownloads() throws Exception {
		downloadPanel.deleteDownloads();
		verify(dialogFactory).showDeleteDownloadsDialog();
	}

	@Test
	public void shouldEnableDeleteButton() throws Exception {
		DownloadState[] states = { DownloadState.Queued };
		setDownloadsSelectedExpectations(1, states);

		downloadPanel.enableAvailableActions();
		verify(deleteButton).setEnabled(true);
	}

	@Test
	public void shouldEnableCleanUpButton() throws Exception {
		when(downloadTable.getRowCount()).thenReturn(1);
		int rowIndex = 0;
		int columnIndex = 0;
		when(downloadTable.convertColumnIndexToModel(rowIndex)).thenReturn(rowIndex);
		when(tableModel.getValueAt(rowIndex, columnIndex)).thenReturn(download);
		when(download.getStatus()).thenReturn(DownloadState.Complete);
		downloadPanel.enableCleanUpAction();
		verify(cleanUpButton).setEnabled(true);
	}

	@Test
	public void shouldDisableCleanUpButton() throws Exception {
		when(downloadTable.getRowCount()).thenReturn(1);
		int rowIndex = 0;
		int columnIndex = 0;
		when(downloadTable.convertColumnIndexToModel(rowIndex)).thenReturn(rowIndex);
		when(tableModel.getValueAt(rowIndex, columnIndex)).thenReturn(download);
		when(download.getStatus()).thenReturn(DownloadState.Downloading);
		downloadPanel.enableCleanUpAction();
		verify(cleanUpButton).setEnabled(false);
	}

	private List<Download> setDownloadsSelectedExpectations(int nrDownloads, DownloadState[] states) {
		List<Download> selectedDownloads = new ArrayList<Download>();
		for (int i = 0; i < nrDownloads; i++) {
			Download download = mock(Download.class);
			when(download.getStatus()).thenReturn(states[i]);
			selectedDownloads.add(download);
		}
		when(buttonPanel.getResumeButton()).thenReturn(resumeButton);
		downloadPanel.selectedDownloads = selectedDownloads;
		return selectedDownloads;
	}

	private void verifyPauseAndResumeEnabled(boolean pauseEnabled, boolean resumeEnabled) {
		verify(pauseButton).setEnabled(pauseEnabled);
		assertEquals(pauseEnabled, downloadPanel.getPauseMenuItem().isEnabled());
		verify(resumeButton).setEnabled(resumeEnabled);
		assertEquals(resumeEnabled, downloadPanel.getResumeMenuItem().isEnabled());
	}

	@SuppressWarnings("unchecked")
	private final class SortKeyMatcher extends BaseMatcher<List> {
		@Override
		public void describeTo(Description arg0) {
		}

		@Override
		public boolean matches(Object arg0) {
			try {
				SortKey key = (SortKey) ((List) arg0).get(0);
				return key.getColumn() == USERPREF_COLUMN && key.getSortOrder() == SortOrder.ASCENDING;
			} catch (Exception e) {
				return false;
			}
		}
	}

}
