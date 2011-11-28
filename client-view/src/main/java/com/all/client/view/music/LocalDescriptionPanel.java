package com.all.client.view.music;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.ContactRoot;
import com.all.client.model.Download;
import com.all.client.model.PlaylistOrder;
import com.all.client.util.ViewModelUtils;
import com.all.client.view.ShortcutBinder;
import com.all.client.view.components.RateItems;
import com.all.client.view.components.TableCellInfo;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.flows.DeleteFlow;
import com.all.client.view.listeners.RestoreSelectionFocusListener;
import com.all.client.view.util.MacUtils;
import com.all.client.view.util.ViewColumnOptionsConverter;
import com.all.client.view.util.ViewRepository;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.EngineEnabled;
import com.all.core.actions.Actions;
import com.all.core.actions.UpdateTrackRatingAction;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.spring.InitializeService;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.JTextFieldLimit;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.common.view.util.SpacerKeyListener;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerTrackPlayedEvent;
import com.all.core.events.MusicPlayerErrorEvent;
import com.all.core.events.MusicPlayerErrorType;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.DisplayableMetadataFields;
import com.all.core.model.FacebookPost;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemoteFolder;
import com.all.shared.model.RemotePlaylist;
import com.all.shared.model.Root;
import com.all.shared.model.SimpleSmartPlaylist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.model.Root.ContainerType;
import com.all.shared.stats.RecommendedTrackFacebookStat;
import com.all.shared.stats.usage.UserActions;

@Component
@EngineEnabled
public class LocalDescriptionPanel extends JPanel implements Internationalizable {

	private static final Log LOG = LogFactory.getLog(LocalDescriptionPanel.class);

	private static final Dimension DEFAULT_TABPANEL_SIZE = new Dimension(581, 24);
	private static final Dimension MAXIMUM_TABPANEL_SIZE = new Dimension(1005, 24);
	private static final Dimension MINIMUM_TABPANEL_SIZE = new Dimension(413, 24);
	private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);
	private static final Dimension MIMIMUM_SEPARATOR_PANEL_SIZE = new Dimension(10, 2);
	private static final Dimension DEFAULT_SEPARATOR_PANEL_SIZE = new Dimension(450, 2);
	private static final Dimension DEFAULT_CLEARSEARCH_BUTTON_SIZE = new Dimension(30, 22);
	private static final Dimension DEFAULT_LUPA_BUTTON_SIZE = new Dimension(28, 22);
	private static final Dimension DEFAULT_SEARCH_TEXT_SIZE = new Dimension(153, 22);
	private static final Dimension MIMIMUM_SEARCH_TEXT_SIZE = new Dimension(45, 22);
	private static final Dimension MAXIMUM_SEARCH_PANEL_SIZE = new Dimension(200, 22);
	private static final Dimension MINIMUM_SEARCH_PANEL_SIZE = new Dimension(200, 22);
	private static final Dimension DEFAULT_SEARCH_PANEL_SIZE = new Dimension(200, 22);
	private static final Dimension MINIMUM_TOOLBAR_SIZE = new Dimension(10, 24);
	private static final Dimension DEFAULT_TOOLBAR_SIZE = new Dimension(450, 24);
	private static final long serialVersionUID = 1L;
	private static final int MAX_CHARACTERS = 40;
	private static final Dimension DOWNLOAD_BUTTON_SIZE = new Dimension(30, 18);

	private JPanel toolbarPanel;
	private JPanel mainDescriptionPanel;
	private JPanel searchPanel;
	private JPanel tabPanel;
	private JPanel separatorPanel;
	private JScrollPane descriptionScrollPane;
	private JTextField searchTextField;
	private JPopupMenu libraryPopupMenu;
	private JMenuItem playMenuItem;
	private JMenuItem downloadMenuItem;
	private JMenuItem copyMenuItem;
	private JMenuItem pasteMenuItem;
	private JMenuItem deleteMenuItem;
	private JMenuItem recommendOnTwitterMenuItem;
	private JMenuItem recommendOnFacebookMenuItem;
	private JPopupMenu.Separator separatorItem;
	private JButton lupaButton;
	private JButton clearSearchButton;
	private JButton downloadAllButton;
	private DescriptionTable descriptionTable;
	private JLabel downloadAllLabel;
	private Track playingTrack;
	private List<String> trackSearchLabels = new ArrayList<String>();

	private TrackSearchType currentSearchType = TrackSearchType.ALL;

	private boolean paintShadow;

	private Messages messages;

	private WaitForUserInputWorker userInputWorker;
	private JXLayer<JPanel> shadowPanel;

	private Track lastDoubleClickOn;

	private Track trackSelected;

	private ViewEngine viewEngine;

	private Map<String, DownloadState> trackDownloads = new ConcurrentHashMap<String, DownloadState>();

	@Autowired
	ShortcutBinder shortcutBinder;
	@Autowired
	DialogFactory dialogFactory;
	@Autowired
	MultiLayerDropTargetListener multiLayerDropTargetListener;
	@Autowired
	ViewRepository viewState;

	@Autowired
	public LocalDescriptionPanel(Messages messages, ViewEngine viewEngine) {
		this.messages = messages;
		this.viewEngine = viewEngine;
		this.setName("descriptionPanel");
		this.setLayout(new BorderLayout());
		this.setSize(DEFAULT_SIZE);
		this.add(getShadowPanel(), BorderLayout.CENTER);
		setMessages(messages);
	}

	@EventMethod(Events.Player.TRACK_PLAYED_ID)
	public void onPlayerTrackPlayed(MediaPlayerTrackPlayedEvent eventArgs) {
		playingTrack = eventArgs.getTrack();
		getDescriptionTable().setPlayingTrack(playingTrack);
	}

	@EventMethod(Events.Player.PLAYING_PLAYLIST_CHANGED_ID)
	public void onPlayingPlaylistChanged(ValueEvent<TrackContainer> eventArgs) {
		getDescriptionTable().setPlayingPlaylist(eventArgs.getValue());
	}

	@EventMethod(Events.Player.PLAYCOUNT_CHANGED_ID)
	public void onPlaycountChangedOnAllMusicSelection(Track track) {
		getDescriptionTable().updateRow(track);
		getDescriptionTable().updateTable();
	}

	@EventMethod(Events.Library.TRACK_UPDATED_ID)
	public void onTrackUpdated(ValueEvent<Track> eventArgs) {
		getDescriptionTable().updateRow(eventArgs.getValue());
		getDescriptionTable().updateTable();
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getDescriptionTable().setMessages(messages);
	}

	private void initColumns() {
		DisplayableMetadataFields viewColumnOptions = (DisplayableMetadataFields) viewEngine
				.get(Model.UserPreference.DISPLAYABLE_METADATA_FIELDS);
		for (DescriptionTableColumns col : DescriptionTableColumns.values()) {
			if (col.equals(DescriptionTableColumns.NAME)) {
				getDescriptionTable().getColumn(col).setPreferredWidth(DescriptionTableColumns.NAME.getDefaultWidth());
			}
			getDescriptionTable().getColumn(col).setWidth(ViewColumnOptionsConverter.getWidth(viewColumnOptions, col));
			getDescriptionTable().setVisible(col, ViewColumnOptionsConverter.isVisible(viewColumnOptions, col));
		}
		int columnIndex = getDescriptionTable().getColumnModel().getColumnIndex(viewColumnOptions.getSortingColumn());
		SortOrder sortOrder = viewColumnOptions.isSortAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING;
		ArrayList<SortKey> keys = new ArrayList<SortKey>();
		keys.add(new SortKey(columnIndex, sortOrder));
		getDescriptionTable().getRowSorter().setSortKeys(keys);
	}

	@InitializeService
	public void initialize() {
		getDescriptionTable().addFocusListener(new RestoreSelectionFocusListener(viewEngine));

		getDescriptionTable().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
					int row = getDescriptionTable().getRowIndexAtLocation(e.getPoint());
					trackSelected = getDescriptionTable().getValue(row);
					getDescriptionTable().selectedRow(row);

					if (ViewModelUtils.isBrowsingLocalLibrary(viewEngine)) {
						getLibraryPopupMenu().add(getPasteMenuItem());
						getLibraryPopupMenu().add(getSeparator());
						getLibraryPopupMenu().add(getDeleteMenuItem());
						getRecommendOnTwitterMenuItem().setEnabled(getDescriptionTable().getSelectedRowCount() == 1 ? true : false);
						getRecommendOnFacebookMenuItem()
								.setEnabled(getDescriptionTable().getSelectedRowCount() == 1 ? true : false);
					} else {
						getLibraryPopupMenu().remove(getPlayMenuItem());
						getLibraryPopupMenu().remove(getPasteMenuItem());
						getLibraryPopupMenu().remove(getSeparator());
						getLibraryPopupMenu().remove(getDeleteMenuItem());
					}

					if (getDescriptionTable().getStyle().isTrackAvailable(trackSelected)) {
						getLibraryPopupMenu().add(getPlayMenuItem(), 0);
						getLibraryPopupMenu().remove(getDownloadMenuItem());
					} else {
						getLibraryPopupMenu().add(getDownloadMenuItem(), 0);
						getLibraryPopupMenu().remove(getPlayMenuItem());
					}
					getLibraryPopupMenu().invalidate();
					getLibraryPopupMenu().revalidate();
					getLibraryPopupMenu().show(getDescriptionTable(), e.getX(), e.getY());
					e.consume();
				}
			}
		});
		getDescriptionTable().getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
					getShowHidePopupMenu().show(getDescriptionTable().getTableHeader(), e.getX(), e.getY());
					e.consume();
				}
			}
		});
		getDescriptionTable().getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				DisplayableMetadataFields viewColumnOptions = (DisplayableMetadataFields) viewEngine
						.get(Model.UserPreference.DISPLAYABLE_METADATA_FIELDS);
				for (DescriptionTableColumns col : DescriptionTableColumns.values()) {
					ViewColumnOptionsConverter.setWidth(viewColumnOptions, col, getDescriptionTable().getColumn(col).getWidth());
				}
				try {
					SortKey sortKey = getDescriptionTable().getRowSorter().getSortKeys().get(0);
					viewColumnOptions.setSortAscending(sortKey.getSortOrder() == SortOrder.ASCENDING);
					ViewColumnOptionsConverter.setSortingColumn(viewColumnOptions,
							(DescriptionTableColumns) getDescriptionTable().getColumnModel().getColumn(sortKey.getColumn())
									.getIdentifier());
				} catch (Exception e) {
					LOG.error(e, e);
				}
				viewEngine.sendValueAction(Actions.UserPreference.SET_DISPLAYABLE_METADATA_FIELDS, viewColumnOptions);
			}
		});

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				getDescriptionTable().stopMonitor();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				getDescriptionTable().startMonitor();
			}
		});

		getSearchTextField().getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				resetFilter(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				resetFilter(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				resetFilter(e);
			}

			private void resetFilter(DocumentEvent event) {
				String text = "";
				try {
					text = event.getDocument().getText(0, event.getDocument().getLength());
				} catch (BadLocationException e) {
					LOG.error(e, e);
				}
				doSearch(text);
			}
		});
		getClearSearchButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getSearchTextField().setText("");
			}
		});
		getLupaBoton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getSearchPopupMenu().show(getLupaBoton(), e.getX(), e.getY());
				e.consume();
			}
		});
		shortcutBinder.whenCopyInDescriptionTable(this.getDescriptionTable());
		shortcutBinder.whenCutInDescriptionTable(this.getDescriptionTable());
		shortcutBinder.whenPasteInDescriptionTable(this.getDescriptionTable());

		getDescriptionTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					TableCellInfo<Track> cellData = getDescriptionTable().getCellData(e.getPoint());
					if (cellData != null) {
						Object identifier = cellData.getTableColumn().getIdentifier();
						if (identifier == DescriptionTableColumns.RATING && cellData.getXinColumn() > 15
								&& cellData.getXinColumn() < (cellData.getWidth() - 15)) {
							Track track = cellData.getData();
							RateItems item = RateItems.getItem(track.getRating());
							RateItems next = item.next();
							viewEngine
									.send(Actions.Library.UPDATE_TRACK_RATING, new UpdateTrackRatingAction(track, next.ratingValue));
							getDescriptionTable().updateRow(track);
							getDescriptionTable().updateTable();
						}
						if (identifier == DescriptionTableColumns.NAME && cellData.getXinColumn() < 20) {
							Track track = cellData.getData();
							viewEngine.sendValueAction(Actions.Library.TOGGLE_TRACK_ENABLED, track);
							getDescriptionTable().updateRow(track);
							getDescriptionTable().updateTable();
						}
					}
				}
			}
		});
		getDescriptionTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				List<Track> selectedTracks = descriptionTable.getSelectedValues();
				viewEngine.sendValueAction(Actions.View.SET_CLIPBOARD_SELECTION, selectedTracks);
				viewEngine.sendValueAction(Actions.Library.REMOVE_FROM_NEW_TRACKS, selectedTracks);
			}
		});
		getDescriptionTable().getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				TrackContainer container = viewEngine.get(Model.SELECTED_CONTAINER);
				checkAllDownloadButtonVisibility(container);
			}
		});
		// getDescriptionTable().setDisplayedPlaylist(appState.getSelectedItem());

		getDescriptionTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				TableCellInfo<Track> cellData = getDescriptionTable().getCellData(e.getPoint());
				if (cellData == null) {
					return;
				}
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					lastDoubleClickOn = cellData.getData();
					playOrDownloadTrack(dialogFactory, cellData);
				}
			}
		});
		getDescriptionTable().onVisibleRowsChanged().add(new Observer<ObservValue<List<Track>>>() {
			@Override
			public void observe(ObservValue<List<Track>> eventArgs) {
				PlaylistOrder playlistOrder = new PlaylistOrder(descriptionTable.getDisplayedPlaylist(), eventArgs.getValue());
				viewEngine.send(Actions.Player.UPDATE_PLAYLIST_ORDER, new ValueAction<PlaylistOrder>(playlistOrder));
				getDescriptionTable().setPlayingTrack(playingTrack);
			}
		});
		getPlayMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Player.PLAY);
			}
		});
		getDescriptionTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (getDescriptionTable().getSelectedRowCount() > 0) {
					viewEngine.sendValueAction(Actions.Player.UPDATE_CURRENT_INDEX, getDescriptionTable().getSelectedRows());
				} else {
					viewEngine.sendValueAction(Actions.Player.UPDATE_CURRENT_INDEX, new int[0]);
				}
			}
		});

		getCopyMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.CLIPBOARD_COPY);
			}
		});
		getPasteMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.View.CLIPBOARD_PASTE);
			}
		});
		getDeleteMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new DeleteFlow(viewEngine, dialogFactory).deleteTracks(getDescriptionTable().getDisplayedPlaylist(),
						getDescriptionTable().getSelectedValues());
			}
		});
		getDescriptionTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE && ViewModelUtils.isBrowsingLocalLibrary(viewEngine)) {
					new DeleteFlow(viewEngine, dialogFactory).deleteTracks(getDescriptionTable().getDisplayedPlaylist(),
							getDescriptionTable().getSelectedValues());
				}
			}
		});

		DescriptionTableFileSystemDropListener fileSystemListener = new DescriptionTableFileSystemDropListener(
				getDescriptionTable(), viewEngine);
		DescriptionTableModelDropListener modelListener = new DescriptionTableModelDropListener(getDescriptionTable(),
				viewEngine);
		DescriptionTableDragOverListener dragListener = new DescriptionTableDragOverListener(getDescriptionTable());

		multiLayerDropTargetListener.addDragListener(getDescriptionTable(), dragListener);
		multiLayerDropTargetListener.addDropListener(getDescriptionTable(), modelListener);
		multiLayerDropTargetListener.addDropListener(getDescriptionTable(), fileSystemListener);

		getDescriptionTable().onVisibleRowsChanged().add(new Observer<ObservValue<List<Track>>>() {
			@Override
			public void observe(ObservValue<List<Track>> eventArgs) {
				viewEngine.send(Actions.Application.SET_CURRENT_DISPLAYED_TRACKS,
						new ValueAction<List<Track>>(eventArgs.getValue()));
			}
		});

		getDownloadMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Root root = viewEngine.get(Model.SELECTED_ROOT);
				ModelCollection model = new ModelCollection(descriptionTable.getSelectedValues());
				if (root.getType() != ContainerType.LOCAL) {
					model.setRemote(true);
					model.source(isRemotePlaylistDisplayed() ? ModelSource.remote() : ModelSource.local());
				}
				viewEngine.sendValueAction(Actions.Downloads.ADD_MODEL_COLLECTION, model);
			}
		});

		getDownloadAllButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ModelCollection downloadTracks = new ModelCollection(getDescriptionTable().getDisplayedPlaylist().getTracks());
				Root root = viewEngine.get(Model.SELECTED_ROOT);
				if (root.getType() == ContainerType.CONTACT || root.getType() == ContainerType.REMOTE) {
					if (downloadTracks instanceof Playlist) {
						downloadTracks.add(getDescriptionTable().getDisplayedPlaylist());
					}
					downloadTracks.setRemote(true);
				}
				viewEngine.sendValueAction(Actions.Downloads.ADD_MODEL_COLLECTION, downloadTracks);
				getDescriptionTable().updateAllTable();
				getDescriptionTable().invalidate();
				SwingUtilities.getWindowAncestor(getDescriptionTable()).validate();
			}
		});

		getDescriptionTable().getStyle().setViewEngine(viewEngine);

		getRecommendOnTwitterMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (dialogFactory.isTwitterLoggedIn()) {
					dialogFactory.showTwitterRecommendationDialog(trackSelected);
				}
			}
		});

		getRecommendOnFacebookMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean postDialog = BooleanUtils.isTrue(viewEngine.get(Model.FACEBOOK_AUTHORIZED));
				if (!postDialog) {
					postDialog = dialogFactory.showFacebookLoginDialog();
				}

				if (postDialog) {
					String post = dialogFactory.showFacebookPostConfirmationDialog(new ModelCollection(trackSelected));
					if (post != null && !post.trim().isEmpty()) {
						String email = viewEngine.get(Model.CURRENT_USER).getEmail();
						viewEngine.sendValueAction(Actions.Facebook.POST_TO_FACEBOOK, new FacebookPost(null, post));
						viewEngine.sendValueAction(ApplicationActions.REPORT_USER_STAT, new RecommendedTrackFacebookStat(email,
								trackSelected.getHashcode()));
					}
				}
			}
		});

		getDescriptionTable().setToggleContext(viewState.getTableGroupContext());

	}

	@EventMethod(Events.Player.PLAY_ERROR_ID)
	public void onPlayError(MusicPlayerErrorEvent eventArgs) {
		if (lastDoubleClickOn != null && lastDoubleClickOn.equals(eventArgs.getTrack())
				&& eventArgs.getErrorType() == MusicPlayerErrorType.NO_CODECS) {
			String fileName = lastDoubleClickOn.getFileName();
			lastDoubleClickOn = null;
			fileName = fileName.substring(fileName.lastIndexOf('.'));
			dialogFactory.showCodecDialog(fileName);
		}
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onStartApp() {
		TrackContainer container = viewEngine.get(Model.SELECTED_CONTAINER);
		getDescriptionTable().setModel(container);
		initColumns();
	}

	@EventMethod(Events.View.SELECTED_TRACKCONTAINER_CHANGED_ID)
	public void onSelectedItemChange(SelectTrackContainerEvent eventArgs) {
		Root root = eventArgs.getRoot();
		TrackContainer container = eventArgs.getContainer();
		boolean paintShadow = ContainerType.DEVICE.equals(root.getType());
		if (paintShadow != this.paintShadow) {
			this.paintShadow = paintShadow;
			refreshPanel();
		}
		if (root.getType() != ContainerType.DEVICE) {
			getSearchTextField().setText("");
			getDescriptionTable().setModel(container);
			checkAllDownloadButtonVisibility(container);
		}
		getDescriptionTable().getStyle().setLocalLibrary(root.getType() == ContainerType.LOCAL);
	}

	protected void doSearch(String text) {
		if (userInputWorker == null || userInputWorker.isDone()) {
			userInputWorker = new WaitForUserInputWorker(text);
			userInputWorker.execute();
		} else {
			userInputWorker.refreshTime(text);
		}
	}

	@Override
	public void internationalize(Messages messages) {
		getPlayMenuItem().setText(messages.getMessage("descriptionPanel.playTrack"));
		getCopyMenuItem().setText(messages.getMessage("copy"));
		getPasteMenuItem().setText(messages.getMessage("paste"));
		getDeleteMenuItem().setText(messages.getMessage("delete"));
		getRecommendOnTwitterMenuItem().setText(messages.getMessage("twitter.popupmenu.recommend"));
		getRecommendOnFacebookMenuItem().setText(messages.getMessage("facebook.popupmenu.recommend"));

		getSearchTextField().setToolTipText(messages.getMessage("tooltip.searchFieldDT"));
		getDownloadAllButton().setToolTipText(messages.getMessage("tooltip.downloadAllDT"));

		for (TrackSearchType search : TrackSearchType.values()) {
			trackSearchLabels.add(search.getOrder(), messages.getMessage(search.getNameKey()));
		}
		getDownloadAllLabel().setText(messages.getMessage("descriptionTable.downloadAll"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getDescriptionTable().removeMessages(messages);
	}

	private JPopupMenu getSearchPopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.setName("MenuCriteria");
		for (TrackSearchType search : TrackSearchType.values()) {
			JCheckBoxMenuItem item = createPopupItem(search);
			item.setSelected(currentSearchType == search);
			menu.add(item);
		}
		return menu;
	}

	private JCheckBoxMenuItem createPopupItem(final TrackSearchType search) {
		final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();
		menuItem.setText(trackSearchLabels.get(search.getOrder()));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentSearchType = search;
			}
		});
		return menuItem;
	}

	private JPopupMenu getShowHidePopupMenu() {
		JPopupMenu menu = new JPopupMenu();
		menu.setName("MenuCriteria");
		for (DescriptionTableColumns column : DescriptionTableColumns.values()) {
			if (column != DescriptionTableColumns.INDEX && column != DescriptionTableColumns.NAME) {
				menu.add(createPopupItem(column));
			}
		}
		menu.add(new JSeparator());
		menu.add(getShowHideAll(true));
		menu.add(getShowHideAll(false));
		return menu;
	}

	private JMenuItem getShowHideAll(final boolean visible) {
		JMenuItem menuItem = new JCheckBoxMenuItem();
		if (visible) {
			menuItem.setText(messages.getMessage("descriptionPanel.showAll"));
		} else {
			menuItem.setText(messages.getMessage("descriptionPanel.hideAll"));
		}
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setColumnVisibility(visible, DescriptionTableColumns.values());
			}
		});
		return menuItem;
	}

	private JCheckBoxMenuItem createPopupItem(final DescriptionTableColumns column) {
		final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem();
		menuItem.setText(messages.getMessage(column.label()));
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean visible = !getDescriptionTable().isVisible(column);
				setColumnVisibility(visible, column);
			}
		});
		menuItem.setSelected(getDescriptionTable().isVisible(column));
		return menuItem;
	}

	private void setColumnVisibility(boolean visible, DescriptionTableColumns... columns) {
		DisplayableMetadataFields viewColumnOptions = (DisplayableMetadataFields) viewEngine
				.get(Model.UserPreference.DISPLAYABLE_METADATA_FIELDS);
		for (DescriptionTableColumns column : columns) {
			if (column != DescriptionTableColumns.INDEX && column != DescriptionTableColumns.NAME) {
				getDescriptionTable().setVisible(column, visible);
				ViewColumnOptionsConverter.setVisible(viewColumnOptions, column, visible);
			}
		}
		try {
			viewEngine.sendValueAction(Actions.UserPreference.SET_DISPLAYABLE_METADATA_FIELDS, viewColumnOptions);
		} catch (Exception e) {
			LOG.error(e, e);
		}
		getDescriptionTable().revalidate();
		getDescriptionScrollPane().revalidate();
		doSearch(getSearchTextField().getText());
	}

	private JXLayer<JPanel> getShadowPanel() {
		if (shadowPanel == null) {
			JPanel descriptionPanelContainer = new JPanel();
			descriptionPanelContainer.setLayout(new GridBagLayout());
			GridBagConstraints mainDescriptionConstraints = new GridBagConstraints();
			mainDescriptionConstraints.gridx = 0;
			mainDescriptionConstraints.fill = GridBagConstraints.BOTH;
			mainDescriptionConstraints.weightx = 1.0D;
			mainDescriptionConstraints.weighty = 1.0D;
			mainDescriptionConstraints.gridwidth = 2;
			mainDescriptionConstraints.gridy = 2;
			GridBagConstraints toolBarConstraints = new GridBagConstraints();
			toolBarConstraints.gridx = 0;
			toolBarConstraints.fill = GridBagConstraints.HORIZONTAL;
			toolBarConstraints.weightx = 1.0D;
			toolBarConstraints.gridy = 0;
			GridBagConstraints separatorConstraints = new GridBagConstraints();
			separatorConstraints.gridx = 0;
			separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			separatorConstraints.weightx = 1.0D;
			separatorConstraints.gridy = 1;

			descriptionPanelContainer.add(getToolbarPanel(), toolBarConstraints);
			descriptionPanelContainer.add(getMainDescriptionPanel(), mainDescriptionConstraints);
			descriptionPanelContainer.add(getSeparatorPanel(), separatorConstraints);
			shadowPanel = new JXLayer<JPanel>(descriptionPanelContainer);
			shadowPanel.setUI(new AbstractLayerUI<JPanel>() {
				private boolean lastShadow = false;

				@Override
				protected void paintLayer(final Graphics2D g2, final JXLayer<JPanel> panel) {
					super.paintLayer(g2, panel);
					if (paintShadow) {
						g2.setColor(new Color(0, 0, 0, 75));
						g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
					}
					if (lastShadow != paintShadow) {
						lastShadow = paintShadow;
						refreshPanel();
					}
				}
			});
		}
		return shadowPanel;
	}

	private JPanel getToolbarPanel() {
		if (toolbarPanel == null) {

			GridBagConstraints downloadAllButtonConstraints = new GridBagConstraints();
			downloadAllButtonConstraints.gridx = 0;
			downloadAllButtonConstraints.gridy = 0;
			downloadAllButtonConstraints.insets = new Insets(3, 6, 3, 0);

			GridBagConstraints downloadAllLabelConstraints = new GridBagConstraints();
			downloadAllLabelConstraints.gridx = 1;
			downloadAllLabelConstraints.gridy = 0;
			downloadAllLabelConstraints.insets = new Insets(3, 6, 3, 0);

			GridBagConstraints tabPanelConstraints = new GridBagConstraints();
			tabPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			tabPanelConstraints.gridx = 2;
			tabPanelConstraints.gridy = 0;
			tabPanelConstraints.gridwidth = 1;
			tabPanelConstraints.weightx = 1.0D;

			GridBagConstraints searchPanelConstraints = new GridBagConstraints();
			searchPanelConstraints.gridx = 3;
			searchPanelConstraints.gridy = 0;

			toolbarPanel = new JPanel();
			toolbarPanel.setLayout(new GridBagLayout());
			toolbarPanel.setPreferredSize(DEFAULT_TOOLBAR_SIZE);
			toolbarPanel.setMinimumSize(MINIMUM_TOOLBAR_SIZE);
			toolbarPanel.setName("toolbarPanel");
			toolbarPanel.setMaximumSize(MINIMUM_TOOLBAR_SIZE);
			toolbarPanel.add(getTabPanel(), tabPanelConstraints);
			toolbarPanel.add(getDownloadAllButton(), downloadAllButtonConstraints);
			toolbarPanel.add(getDownloadAllLabel(), downloadAllLabelConstraints);
			toolbarPanel.add(getSearchPanel(), searchPanelConstraints);
		}
		return toolbarPanel;
	}

	private JLabel getDownloadAllLabel() {
		if (downloadAllLabel == null) {
			downloadAllLabel = new JLabel();
			downloadAllLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return downloadAllLabel;
	}

	private JPanel getMainDescriptionPanel() {
		if (mainDescriptionPanel == null) {
			GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
			scrollPaneConstraints.fill = GridBagConstraints.BOTH;
			scrollPaneConstraints.gridy = 0;
			scrollPaneConstraints.weightx = 1.0;
			scrollPaneConstraints.weighty = 1.0;
			scrollPaneConstraints.gridx = 0;
			mainDescriptionPanel = new JPanel();
			mainDescriptionPanel.setLayout(new GridBagLayout());
			mainDescriptionPanel.add(getDescriptionScrollPane(), scrollPaneConstraints);
		}
		return mainDescriptionPanel;
	}

	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 0;
			searchPanel = new JPanel();
			searchPanel.setLayout(new BorderLayout());
			searchPanel.setPreferredSize(DEFAULT_SEARCH_PANEL_SIZE);
			searchPanel.setMinimumSize(MINIMUM_SEARCH_PANEL_SIZE);
			searchPanel.setMaximumSize(MAXIMUM_SEARCH_PANEL_SIZE);
			searchPanel.add(getLupaBoton(), BorderLayout.WEST);
			searchPanel.add(getSearchTextField(), BorderLayout.CENTER);
			searchPanel.add(getClearSearchButton(), BorderLayout.EAST);
		}
		return searchPanel;
	}

	private JScrollPane getDescriptionScrollPane() {
		if (descriptionScrollPane == null) {
			descriptionScrollPane = new JScrollPane();
			descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			descriptionScrollPane.setViewportView(getDescriptionTable());
			descriptionScrollPane.setName("descriptionScrollPane");
		}
		return descriptionScrollPane;
	}

	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
			searchTextField.setDocument(new JTextFieldLimit(MAX_CHARACTERS));
			searchTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			searchTextField.setName("searchTextField");
			searchTextField.setMinimumSize(MIMIMUM_SEARCH_TEXT_SIZE);
			searchTextField.setMaximumSize(DEFAULT_SEARCH_TEXT_SIZE);
			searchTextField.setPreferredSize(DEFAULT_SEARCH_TEXT_SIZE);
			searchTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
			searchTextField.addKeyListener(new SpacerKeyListener());
		}
		return searchTextField;
	}

	private JPopupMenu getLibraryPopupMenu() {
		if (libraryPopupMenu == null) {
			libraryPopupMenu = new JPopupMenu();
			libraryPopupMenu.addSeparator();
			libraryPopupMenu.add(getRecommendOnTwitterMenuItem());
			libraryPopupMenu.add(getRecommendOnFacebookMenuItem());
			libraryPopupMenu.addSeparator();
			libraryPopupMenu.add(getCopyMenuItem());
		}
		return libraryPopupMenu;
	}

	private JMenuItem getRecommendOnTwitterMenuItem() {
		if (recommendOnTwitterMenuItem == null) {
			recommendOnTwitterMenuItem = new JMenuItem();
		}
		return recommendOnTwitterMenuItem;
	}

	private JPopupMenu.Separator getSeparator() {
		if (separatorItem == null) {
			separatorItem = new JPopupMenu.Separator();
		}
		return separatorItem;
	}

	private JMenuItem getRecommendOnFacebookMenuItem() {
		if (recommendOnFacebookMenuItem == null) {
			recommendOnFacebookMenuItem = new JMenuItem();
		}
		return recommendOnFacebookMenuItem;
	}

	private JMenuItem getPlayMenuItem() {
		if (playMenuItem == null) {
			playMenuItem = new JMenuItem();
		}
		return playMenuItem;
	}

	private JMenuItem getDownloadMenuItem() {
		if (downloadMenuItem == null) {
			downloadMenuItem = new JMenuItem();
			downloadMenuItem.setText(messages.getMessage("descriptionPanel.download"));
		}
		return downloadMenuItem;
	}

	private JMenuItem getCopyMenuItem() {
		if (copyMenuItem == null) {
			copyMenuItem = new JMenuItem();
		}
		return copyMenuItem;
	}

	private JMenuItem getPasteMenuItem() {
		if (pasteMenuItem == null) {
			pasteMenuItem = new JMenuItem();
		}
		return pasteMenuItem;
	}

	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem();
			deleteMenuItem.setName("delete");
		}
		return deleteMenuItem;
	}

	private JButton getLupaBoton() {
		if (lupaButton == null) {
			lupaButton = new JButton();
			lupaButton.setPreferredSize(DEFAULT_LUPA_BUTTON_SIZE);
			lupaButton.setName("lupaSearchButton");
		}
		return lupaButton;
	}

	private JButton getClearSearchButton() {
		if (clearSearchButton == null) {
			clearSearchButton = new JButton();
			clearSearchButton.setName("clearSearchButtonInvisible");
			clearSearchButton.setPreferredSize(DEFAULT_CLEARSEARCH_BUTTON_SIZE);
		}
		return clearSearchButton;
	}

	private JButton getDownloadAllButton() {
		if (downloadAllButton == null) {
			downloadAllButton = new JButton();
			downloadAllButton.setName("downloadAllButton");
			downloadAllButton.setPreferredSize(DOWNLOAD_BUTTON_SIZE);
			downloadAllButton.setSize(DOWNLOAD_BUTTON_SIZE);
			downloadAllButton.setMinimumSize(DOWNLOAD_BUTTON_SIZE);
			downloadAllButton.setMaximumSize(DOWNLOAD_BUTTON_SIZE);
		}
		return downloadAllButton;
	}

	private JPanel getTabPanel() {
		if (tabPanel == null) {
			tabPanel = new JPanel();
			tabPanel.setLayout(new GridBagLayout());
			tabPanel.setMinimumSize(MINIMUM_TABPANEL_SIZE);
			tabPanel.setMaximumSize(MAXIMUM_TABPANEL_SIZE);
			tabPanel.setPreferredSize(DEFAULT_TABPANEL_SIZE);
		}
		return tabPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setName("backgroundPanel");
			separatorPanel.setPreferredSize(DEFAULT_SEPARATOR_PANEL_SIZE);
			separatorPanel.setMinimumSize(MIMIMUM_SEPARATOR_PANEL_SIZE);
			separatorPanel.setMaximumSize(MIMIMUM_SEPARATOR_PANEL_SIZE);
		}
		return separatorPanel;
	}

	private DescriptionTable getDescriptionTable() {
		if (descriptionTable == null) {
			descriptionTable = new DescriptionTable(viewEngine);
		}
		return descriptionTable;
	}

	@EventMethod(Events.Library.CONTAINER_MODIFIED_ID)
	public void onContainerModified(ContainerModifiedEvent eventArgs) {
		TrackContainer displayedPlaylist = getDescriptionTable().getDisplayedPlaylist();
		if (displayedPlaylist == null) {
			return;
		}
		if (eventArgs.isModified(displayedPlaylist)) {
			if (eventArgs.getContainerModelObject() != null) {
				descriptionTable.setModel(eventArgs.getContainerModelObject());
			} else {
				descriptionTable.setModel(displayedPlaylist);
			}
		}
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onShutdown() {
		getDescriptionTable().stopMonitor();
	}

	void playOrDownloadTrack(DialogFactory dialogFactory, TableCellInfo<Track> cellData) {
		Object identifier = cellData.getTableColumn().getIdentifier();
		if (identifier != DescriptionTableColumns.RATING
				&& !(identifier == DescriptionTableColumns.NAME && cellData.getXinColumn() < 20)) {
			Track clickedTrack = cellData.getData();
			Download download = getDescriptionTable().getDownload(clickedTrack.getHashcode());
			if (download == null || download.getStatus() == DownloadState.Complete) {
				if (isRemotePlaylistDisplayed()) {
					ModelCollection remoteModel = new ModelCollection(clickedTrack);
					remoteModel.source(ModelSource.remote());
					viewEngine.request(Actions.Downloads.REQUEST_ADD_TRACK, clickedTrack, new ResponseCallback<Download>() {
						@Override
						public void onResponse(Download download) {
							onDownloadUpdated(new ValueEvent<Download>(download));
						}
					});
				} else {
					viewEngine.send(Actions.Player.PLAY_DOWNLOAD);
				}
			} else {
				dialogFactory.showLongInfoDialog("popup.download.alreadyStarted", "popup.download.alreadyStarted.title", 400,
						150);
			}
			getDescriptionTable().updateRow(clickedTrack);
			getDescriptionTable().updateTable();
		}
	}

	protected boolean isRemotePlaylistDisplayed() {
		TrackContainer displayedPlaylist = descriptionTable.getDisplayedPlaylist();
		boolean isRemotePlaylist = (displayedPlaylist instanceof RemotePlaylist)
				|| (displayedPlaylist instanceof RemoteFolder) || (displayedPlaylist instanceof SimpleSmartPlaylist)
				|| (displayedPlaylist instanceof ContactRoot);
		return isRemotePlaylist;
	}

	void checkAllDownloadButtonVisibility(TrackContainer tracks) {
		viewEngine.request(Actions.Application.REQUEST_IS_TRACK_DOWNLODABLE, tracks, new ResponseCallback<Boolean>() {

			@Override
			public void onResponse(Boolean response) {
				getDownloadAllButton().setVisible(response);
				getDownloadAllLabel().setVisible(response);
			}
		});

	}

	public void selectAllTracksInDescriptionTable() {
		this.descriptionTable.selectAll();
		this.descriptionTable.repaint();
	}

	private void refreshPanel() {
		if (shadowPanel.isVisible()) {
			shadowPanel.invalidate();
			shadowPanel.repaint();
		}
	}

	// Swing Worker for waiting when the user is typing a search
	class WaitForUserInputWorker extends SwingWorker<Void, Void> {
		private static final int INTERVAL_TIME = 500;
		private long lastInputTime;
		private String textForFilter;

		WaitForUserInputWorker(String text) {
			lastInputTime = System.currentTimeMillis();
			textForFilter = text;
		}

		@Override
		protected Void doInBackground() {
			// Sleeps the thread while the user is typing
			while (System.currentTimeMillis() - lastInputTime < INTERVAL_TIME) {
				try {
					Thread.sleep(INTERVAL_TIME);
				} catch (InterruptedException e) {
					LOG.error(e, e);
				}
			}
			return null;
		}

		public void refreshTime(String text) {
			lastInputTime = System.currentTimeMillis();
			textForFilter = text;
		}

		@Override
		protected void done() {
			RowSorter<? extends TableModel> rowSorter = getDescriptionTable().getRowSorter();
			TableRowSorter<? extends TableModel> sorter = (TableRowSorter<? extends TableModel>) rowSorter;
			textForFilter = textForFilter.trim();
			if (textForFilter.equalsIgnoreCase("show power level") && getDescriptionTable().getRowCount() > 9000) {
				Sound.OVER_9000.play();
				dialogFactory.showMessageDialog("It's over nine thousaaaaand!!!");
			}
			if (textForFilter.length() == 0) {
				sorter.setRowFilter(null);
				getClearSearchButton().setName("clearSearchButtonInvisible");
			} else {
				getClearSearchButton().setName("clearSearchButtonVisible");
				sorter.setRowFilter(new DescriptionTableRowFilter(currentSearchType, textForFilter));
				viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Player.SEARCH_TRACK_LOCALLY);
			}
		}
	}

	@EventMethod(Events.Downloads.UPDATED_ID)
	public void onDownloadUpdated(ValueEvent<Download> valueEvent) {
		Download download = valueEvent.getValue();
		String trackId = download.getTrackId();
		DownloadState downloadState = trackDownloads.get(trackId);
		if (!download.getStatus().equals(downloadState)) {
			trackDownloads.put(trackId, download.getStatus());
			viewEngine.request(Actions.Application.REQUEST_FIND_TRACK, trackId, new ResponseCallback<Track>() {

				@Override
				public void onResponse(Track track) {
					getDescriptionTable().updateRow(track);
				}
			});
		}
	}

	@EventMethod(Events.Downloads.COMPLETED_ID)
	public void onDownloadCompleted(ValueEvent<Download> valueEvent) {
		getDescriptionTable().tryUpdateDownloadComplete(valueEvent.getValue());
	}

}
