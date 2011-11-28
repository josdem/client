package com.all.client.view.music;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.action.ActionHandler;
import com.all.action.EmptyAction;
import com.all.action.RequestAction;
import com.all.action.ResponseCallback;
import com.all.appControl.control.TestEngine;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.model.LocalModelDao;
import com.all.client.model.LocalTrack;
import com.all.client.model.MockPlaylist;
import com.all.client.util.TrackRepository;
import com.all.client.view.components.TableCellInfo;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.model.DisplayableMetadataFields;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;
import com.all.shared.model.RemoteTrack;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public class TestLocalDescriptionPanel {
	@InjectMocks
	private LocalDescriptionPanel descriptionPanel;
	@Mock
	private DescriptionTable descriptionTable;
	@Mock
	private JTableHeader tableHeader;
	@Mock
	private TableColumn mockColumn;
	@Mock
	private TableColumnModel tableColumnModel;
	@Mock
	@SuppressWarnings("unchecked")
	private RowSorter rowSorter;
	@Mock
	private LocalModelDao dao;
	@Mock
	private DialogFactory dialogFactory;
	@Mock
	private ListSelectionModel mock;
	@Mock
	private TableModel tableModel;
	@Mock
	private DescriptionTableStyle descriptionTableStyle;
	@Spy
	private TestEngine engine = new TestEngine();
	@Mock
	private TrackRepository trackRepository;
	private ViewEngine viewEngine;

	private ArrayList<Track> trackList;
	private Playlist selection;
	@Mock
	private ActionHandler<RequestAction<Track, Download>> downloadHandler;
	@Mock
	private ActionHandler<RequestAction<String, Track>> findHandler;
	@Mock
	private ActionHandler<EmptyAction> playDownloadHandler;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		viewEngine = mock(ViewEngine.class);
		descriptionPanel = new LocalDescriptionPanel(mock(Messages.class), viewEngine);
		MockitoAnnotations.initMocks(this);
		engine.addActionHandler(Actions.Downloads.REQUEST_ADD_TRACK, downloadHandler);
		engine.addActionHandler(Actions.Application.REQUEST_FIND_TRACK, findHandler);
		engine.addActionHandler(Actions.Player.PLAY_DOWNLOAD, playDownloadHandler);
		when(viewEngine.get(Model.TRACK_REPOSITORY)).thenReturn(trackRepository);
		trackList = new ArrayList<Track>();
		selection = new MockPlaylist();

		when(descriptionTable.getTableHeader()).thenReturn(tableHeader);
		when(descriptionTable.getColumn(anyObject())).thenReturn(mockColumn);
		when(descriptionTable.getColumnModel()).thenReturn(tableColumnModel);
		when(descriptionTable.getRowSorter()).thenReturn(rowSorter);
		when(descriptionTable.getSelectionModel()).thenReturn(mock);
		when(descriptionTable.getModel()).thenReturn(tableModel);
		when(descriptionTable.getStyle()).thenReturn(descriptionTableStyle);
		DisplayableMetadataFields columnOptions = mock(DisplayableMetadataFields.class);
		when(viewEngine.get(Model.UserPreference.DISPLAYABLE_METADATA_FIELDS)).thenReturn(columnOptions);
		engine.setup(descriptionTable);
		// engine.set(Model.SELECTED_ROOT, root, null);
		engine.set(Model.SELECTED_CONTAINER, selection, null);
		engine.start();

	}

	@Test
	public void shouldInternationalizeCorrectly() throws Exception {
		Messages messages = mock(Messages.class);
		descriptionPanel.setMessages(messages);
		verify(messages).add(descriptionPanel);
	}

	@Test
	public void shouldAskForTheRightResourceBundles() throws Exception {
		Messages messages = mock(Messages.class);
		descriptionPanel.internationalize(messages);
	}

	@Test
	public void shouldReturnFalseWhenPlaylistIsNotSelected() throws Exception {
		viewEngine.request(Actions.Application.REQUEST_IS_TRACK_DOWNLODABLE, mock(Folder.class),
				new ResponseCallback<Boolean>() {

					@Override
					public void onResponse(Boolean response) {
						assertFalse(response);
					}
				});
	}

	@Test
	public void shouldReturnTrueWhenRemotePlaylistSelectedAndOneTrackDisabled() throws Exception {
		Track track1 = mock(RemoteTrack.class);
		Track track2 = mock(RemoteTrack.class);
		Track track3 = mock(RemoteTrack.class);

		File file = mock(File.class);
		when(trackRepository.getFile(null)).thenReturn(file, file, null);
		when(trackRepository.isRemotelyAvailable(null)).thenReturn(true);

		trackList.add(track1);
		trackList.add(track2);
		trackList.add(track3);

		TrackContainer playlist = mock(Playlist.class);
		when(playlist.getTracks()).thenReturn(trackList);
		viewEngine.request(Actions.Application.REQUEST_IS_TRACK_DOWNLODABLE, playlist, new ResponseCallback<Boolean>() {

			@Override
			public void onResponse(Boolean response) {
				assertTrue(response);
			}
		});
	}

	@Test
	public void shouldReturnTrueWhenPlaylistSelectedAndOneTrackDisabled() throws Exception {
		Track track1 = mock(LocalTrack.class);
		Track track2 = mock(LocalTrack.class);
		Track track3 = mock(LocalTrack.class);

		File file = mock(File.class);
		when(trackRepository.getFile(null)).thenReturn(file, null, null);
		when(dao.findByHashcode(anyString())).thenReturn(track1);

		trackList.add(track1);
		trackList.add(track2);
		trackList.add(track3);

		TrackContainer playlist = mock(Playlist.class);
		when(playlist.getTracks()).thenReturn(trackList);
		viewEngine.request(Actions.Application.REQUEST_IS_TRACK_DOWNLODABLE, playlist, new ResponseCallback<Boolean>() {

			@Override
			public void onResponse(Boolean response) {
				assertTrue(response);
			}
		});
	}

	@Test
	public void shouldReturnFalseWhenPlaylistSelectedAndOneTrackDisabled() throws Exception {
		Track track1 = mock(Track.class);
		Track track2 = mock(Track.class);
		Track track3 = mock(Track.class);

		File file = mock(File.class);
		when(trackRepository.getFile(null)).thenReturn(file);

		trackList.add(track1);
		trackList.add(track2);
		trackList.add(track3);

		TrackContainer playlist = mock(Playlist.class);
		when(playlist.getTracks()).thenReturn(trackList);

		viewEngine.request(Actions.Application.REQUEST_IS_TRACK_DOWNLODABLE, playlist, new ResponseCallback<Boolean>() {

			@Override
			public void onResponse(Boolean response) {
				assertFalse(response);
			}
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldShowDialogWhenTrackIsReference() throws Exception {
		String hashcode = "hashcode";

		TableCellInfo tableCellInfo = mock(TableCellInfo.class);
		TableColumn tableColumn = mock(TableColumn.class);
		Track track = mock(Track.class);
		Download download = mock(Download.class);

		when(tableCellInfo.getTableColumn()).thenReturn(tableColumn);
		when(tableCellInfo.getData()).thenReturn(track);
		when(track.getHashcode()).thenReturn(hashcode);
		when(descriptionTable.getDownload(hashcode)).thenReturn(download);
		when(download.getStatus()).thenReturn(DownloadState.Downloading);

		descriptionPanel.playOrDownloadTrack(dialogFactory, tableCellInfo);

		verify(dialogFactory).showLongInfoDialog(anyString(), anyString(), anyInt(), anyInt());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void shouldPlayTrackWhenIsNotRemotePlaylist() throws Exception {
		TableCellInfo tableCellInfo = mock(TableCellInfo.class);
		TableColumn tableColumn = mock(TableColumn.class);
		Track track = mock(Track.class);
		Playlist playlist = mock(Playlist.class);
		when(descriptionTable.getDisplayedPlaylist()).thenReturn(playlist);

		when(tableCellInfo.getTableColumn()).thenReturn(tableColumn);
		when(tableCellInfo.getData()).thenReturn(track);
		descriptionPanel.playOrDownloadTrack(dialogFactory, tableCellInfo);
		verify(viewEngine, never()).request(Actions.Downloads.REQUEST_ADD_TRACK, track, new ResponseCallback<Download>() {
			@Override
			public void onResponse(Download t) {
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldDownloadATrackWhenIsAReference() throws Exception {
		TableCellInfo tableCellInfo = mock(TableCellInfo.class);
		TableColumn tableColumn = mock(TableColumn.class);
		Track track = mock(Track.class);
		RemoteFolder remoteFolder = mock(RemoteFolder.class);

		when(tableCellInfo.getTableColumn()).thenReturn(tableColumn);
		when(tableCellInfo.getData()).thenReturn(track);
		when(descriptionTable.getDisplayedPlaylist()).thenReturn(remoteFolder);

		descriptionPanel.playOrDownloadTrack(dialogFactory, tableCellInfo);

	}

	@Test
	public void shouldUpdateDownloadListenerOnlyOnce() throws Exception {
		Download download = mock(Download.class);
		String trackId = "1";
		when(download.getTrackId()).thenReturn(trackId);
		when(download.getStatus()).thenReturn(DownloadState.Downloading);

		descriptionPanel.onDownloadUpdated(new ValueEvent<Download>(download));

		viewEngine.request(Actions.Application.REQUEST_FIND_TRACK, trackId, new ResponseCallback<Track>() {

			@Override
			public void onResponse(Track track) {
				verify(dao).findById(LocalTrack.class, "1");
			}
		});

	}

}
