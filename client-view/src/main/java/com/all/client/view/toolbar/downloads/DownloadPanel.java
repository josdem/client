package com.all.client.view.toolbar.downloads;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.appControl.ViewEngineConfigurator;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.model.PlaylistOrder;
import com.all.client.view.MiddleCloseablePanel;
import com.all.client.view.View;
import com.all.client.view.components.TableCellInfo;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.DownloadTableModelDropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.flows.DeleteFlow;
import com.all.client.view.util.MacUtils;
import com.all.client.view.util.ViewRepository;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.JTextFieldLimit;
import com.all.core.common.view.util.SpacerKeyListener;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerTrackPlayedEvent;
import com.all.core.model.Model;
import com.all.downloader.bean.DownloadState;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.RemotePlaylist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

public final class DownloadPanel extends MiddleCloseablePanel implements Internationalizable, View {

	private static final Log LOG = LogFactory.getLog(DownloadPanel.class);

	private static final long serialVersionUID = 1L;

	private static final int MAX_CHARACTERS = 40;

	private static final Dimension MAIN_DOWNLOAD_PANEL_SIZE = new Dimension(594, 540);

	private static final Dimension BOTTOM_PANEL_DEFAULT_SIZE = new Dimension(594, 54);

	private static final Dimension CLEARSEARCH_BUTTON_DEFAULT_SIZE = new Dimension(30, 22);

	private static final Dimension LUPA_BUTTON_DEFAULT_SIZE = new Dimension(28, 22);

	private static final Dimension MINIMUM_TOOLBAR_SIZE = new Dimension(10, 24);

	private static final Dimension TABPANEL_DEFAULT_SIZE = new Dimension(581, 24);

	private static final Dimension TABPANEL_MAXIMUM_SIZE = new Dimension(1005, 24);

	private static final Dimension TABPANEL_MINIMUM_SIZE = new Dimension(413, 24);

	private static final Dimension SEARCH_PANEL_DEFAULT_SIZE = new Dimension(208, 22);

	private static final Dimension SEARCH_TEXT_DEFAULT_SIZE = new Dimension(153, 22);

	private static final Dimension SEARCH_TEXT_MIMIMUM_SIZE = new Dimension(45, 22);

	private static final Dimension SEPARATOR_PANEL_DEFAULT_SIZE = new Dimension(450, 2);

	private static final Dimension SEPARATOR_PANEL_MIMIMUM_SIZE = new Dimension(10, 2);

	private static final EmptyBorder EMPTY_BORDER = new EmptyBorder(0, 10, 0, 0);

	private static final String BOTTOM_PANEL_NAME = "bottomDownloadPanel";

	private static final String CLEAR_SEARCH_BUTTON_NAME = "clearSearchButtonInvisible";

	private static final String CLEAR_SEARCH_BUTTON_VISIBLE_NAME = "clearSearchButtonVisible";

	private static final String DESCRIPTION_SCROLL_PANE_NAME = "descriptionScrollPane";

	private static final String LUPA_BUTTON_NAME = "lupaDownloadSearchButton";

	private static final String SEARCH_TEXT_FIELD_NAME = "searchTextField";

	private static final String SEPARATOR_PANEL_NAME = "backgroundPanel";

	private DownloadTable downloadTable;

	private DownloadButtonsPanel downloadButtonsPanel;

	private Observable<ObserveObject> onCloseEvent = new Observable<ObserveObject>();

	private JButton clearSearchButton;

	private JButton lupaButton;

	private JLabel downloadLabel;

	private JMenuItem pauseMenuItem = new JMenuItem("pause");

	private JMenuItem resumeMenuItem = new JMenuItem("resume");

	private JMenuItem cleanUpMenuItem = new JMenuItem("clean");

	private JMenuItem deleteMenuItem = new JMenuItem("delete");

	private JPanel bottomSeparatorPanel;

	private JPanel bottomPanel;

	private JPanel mainDownloadPanel;

	private JPanel searchPanel;

	private JPanel tabPanel;

	private JScrollPane descriptionScrollPane;

	private JTextField searchTextField;

	private Track playingTrack = null;

	private Playlist downloadPlaylist = new RemotePlaylist();

	private JPanel mainPanel;

	List<Download> selectedDownloads = Collections.emptyList();

	private DialogFactory dialogFactory;

	public DownloadPanel(Messages messages) {
		((RemotePlaylist)downloadPlaylist).setName("downloadTrackContainer");
		downloadTable = new DownloadTable(messages, downloadPlaylist);
	}

	public void wire() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				downloadTable.stopMonitor();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				downloadTable.startMonitor();
				enableCleanUpAction();
			}
		});
		this.downloadTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				enableAvailableActions();
			}
		});
		this.downloadTable.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						enableCleanUpAction();
					}
				});
			}
		});
		getDownloadTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectedDownloads = downloadTable.getSelectedValues();
				getViewEngine().sendValueAction(Actions.View.SET_CLIPBOARD_SELECTION, selectedDownloads);
			}
		});
	}

	void enableCleanUpAction() {
		boolean enableCleanUp = false;
		for (int i = 0; i < downloadTable.getRowCount(); i++) {
			Download download = (Download) downloadTable.getModel().getValueAt(downloadTable.convertRowIndexToModel(i), 0);
			if (download.getStatus() == DownloadState.Complete) {
				enableCleanUp = true;
				break;
			}
		}
		getDownloadButtonsPanel().getCleanUpButton().setEnabled(enableCleanUp);
		cleanUpMenuItem.setEnabled(enableCleanUp);
	}

	void enableAvailableActions() {
		boolean enablePause = false;
		boolean enableResume = false;
		boolean enableDelete = false;
		for (Download download : selectedDownloads) {
			enableDelete = true;
			switch (download.getStatus()) {
			case Downloading:
				enablePause = true;
				break;
			case Paused:
			case MoreSourcesNeeded:
			case Error:
				enableResume = true;
				break;
			}
		}
		getDownloadButtonsPanel().getPauseButton().setEnabled(enablePause);
		getDownloadButtonsPanel().getResumeButton().setEnabled(enableResume);
		getDownloadButtonsPanel().getDeleteButton().setEnabled(enableDelete);
		getPauseMenuItem().setEnabled(enablePause);
		getResumeMenuItem().setEnabled(enableResume);
		deleteMenuItem.setEnabled(enableDelete);
	}

	private DownloadTable getDownloadTable() {
		return downloadTable;
	}

	public void setupSearch() {
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

			@SuppressWarnings("unchecked")
			private void resetFilter(DocumentEvent event) {

				TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) getDownloadTable().getRowSorter();

				String text = "";
				try {
					text = event.getDocument().getText(0, event.getDocument().getLength());
				} catch (BadLocationException e) {
					LOG.error(e, e);
				}
				text = text.trim();

				if (text.length() == 0) {
					sorter.setRowFilter(null);
					clearSearchButton.setName(CLEAR_SEARCH_BUTTON_NAME);
				} else {
					clearSearchButton.setName(CLEAR_SEARCH_BUTTON_VISIBLE_NAME);
					sorter.setRowFilter(new DownloadTableRowFilter(text));
				}
			}
		});
	}

	public void setupDrags(MultiLayerDropTargetListener listener, ViewEngine viewEngine) {
		downloadTable.setDragEnabled(true);
		downloadTable.setTransferHandler(new DownloadTableTransferHandler());
		downloadTable.setDropTarget(null);

		DownloadTableModelDropListener modelDropListener = new DownloadTableModelDropListener(getDownloadTable(),
				viewEngine);
		listener.addDragListener(getDownloadTable(), modelDropListener);
		listener.addDropListener(getDownloadTable(), modelDropListener);

	}

	@EventMethod(Events.Downloads.ADDED_ID)
	public void onDownloadAdded(ValueEvent<Download> valueEvent) {
		getDownloadTable().addRow(valueEvent.getValue());
	}

	@EventMethod(Events.Downloads.REMOVED_ID)
	public void onDownloadRemoved(ValueEvent<Download> valueEvent) {
		getDownloadTable().removeRow(valueEvent.getValue());
	}

	@EventMethod(Events.Downloads.UPDATED_ID)
	public void onDownloadUpdated(ValueEvent<Download> valueEvent) {
		getDownloadTable().updateRow(valueEvent.getValue());
		enableAvailableActions();
	}

	@EventMethod(Events.Downloads.ALL_MODIFIED_ID)
	public void onDownloadAllModified() {
		getDownloadTable().setModel(getViewEngine().get(Model.DOWNLOADS_SORTED_BY_PRIORITY));
	}

	@EventMethod(Events.Downloads.COMPLETED_ID)
	public void onDownloadCompleted(ValueEvent<Download> valueEvent) {
		getDownloadTable().updateRow(valueEvent.getValue());
		getDownloadTable().updateAllTable();
	}

	public void wireDownloadsController() {
		getDownloadTable().setModel(getViewEngine().get(Model.DOWNLOADS_SORTED_BY_PRIORITY));

		getDownloadButtonsPanel().getCleanUpButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanUpDownloads();
			}
		});
		this.cleanUpMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cleanUpDownloads();
			}
		});

		getDownloadButtonsPanel().getDeleteButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteDownloads();
			}
		});
		this.deleteMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteDownloads();
			}
		});

		this.getPauseMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseDownloads();
			}
		});
		getDownloadButtonsPanel().getPauseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseDownloads();
			}
		});

		this.getResumeMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resumeDownloads();
			}
		});
		getDownloadButtonsPanel().getResumeButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resumeDownloads();
			}
		});
		getDownloadTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteDownloads();
				}
			}
		});
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onShutdown() {
		getDownloadTable().stopMonitor();
	}

	public void setEngine(final ViewEngine viewEngine, ViewEngineConfigurator configurer) {
		this.setViewEngine(viewEngine);
		getDownloadTable().getRowSorter().addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				SortKey key = (SortKey) e.getSource().getSortKeys().get(0);
				viewEngine.sendValueAction(Actions.UserPreference.SET_DOWNLOAD_TABLE_SORT_COLUMN, key);
			}
		});
		ArrayList<SortKey> sortKeys = new ArrayList<SortKey>();
		sortKeys.add(viewEngine.get(Model.UserPreference.DOWNLOAD_TABLE_SORT_COLUMN));
		try {
			getDownloadTable().getRowSorter().setSortKeys(sortKeys);
		} catch (Exception e) {
			LOG.error(e, e);
		}

		configurer.setupViewEngine(this);
	}

	@EventMethod(Events.Player.TRACK_PLAYED_ID)
	public void onPlayerTrackPlayed(MediaPlayerTrackPlayedEvent eventArgs) {
		playingTrack = eventArgs.getTrack();
		getDownloadTable().setPlayingTrack(playingTrack);
		getDownloadTable().repaint();
	}

	@EventMethod(Events.Player.PLAYING_PLAYLIST_CHANGED_ID)
	public void onPlayerPlayingPlaylistChanged(ValueEvent<TrackContainer> eventArgs) {
		getDownloadTable().setPlayingPlaylist(eventArgs.getValue());
	}

	public void setMusicPlayerController(final ViewEngine viewEngine) {
		getDownloadTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				TableCellInfo<Download> cellData = getDownloadTable().getCellData(e.getPoint());
				if (cellData == null) {
					return;
				}
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					playOrDownloadTrack(viewEngine, cellData);
				}
			}
		});
		getDownloadTable().onVisibleRowsChanged().add(new Observer<ObservValue<List<Download>>>() {
			@Override
			public void observe(ObservValue<List<Download>> eventArgs) {
				setTracksToController(viewEngine, eventArgs.getValue());
			}
		});
		getDownloadTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				setIndexToController(viewEngine);
			}

		});
	}

	void playOrDownloadTrack(final ViewEngine viewEngine, TableCellInfo<Download> cellData) {
		// Object identifier = cellData.getTableColumn().getIdentifier();
		// Track clickedTrack = ;
		Download download = cellData.getData();// getDescriptionTable().getDownload(clickedTrack.getHashcode());
		if (download != null && download.getStatus() == DownloadState.Complete) {
			viewEngine.send(Actions.Player.PLAY_DOWNLOAD);
		}
	}

	private void setTracksToController(final ViewEngine viewEngine, List<Download> eventArgs) {
		List<String> hashcodes = new ArrayList<String>();
		for (Download download : eventArgs) {
			hashcodes.add(download.getTrackId());
		}
		viewEngine.request(Actions.Library.FIND_TRACKS_BY_HASHCODES, hashcodes, new ResponseCallback<List<Track>>() {
			@Override
			public void onResponse(List<Track> tracks) {
				PlaylistOrder playlistOrder = new PlaylistOrder(downloadPlaylist, tracks);
				viewEngine.sendValueAction(Actions.Player.UPDATE_PLAYLIST_ORDER, playlistOrder);
				getDownloadTable().setPlayingTrack(playingTrack);
			}
		});
	}

	private void setIndexToController(final ViewEngine viewEngine) {
		if (getDownloadTable().getSelectedRowCount() > 0) {
			viewEngine.sendValueAction(Actions.Player.UPDATE_CURRENT_INDEX, getDownloadTable().getSelectedRows());
		} else {
			viewEngine.send(Actions.Player.UPDATE_CURRENT_INDEX, null);
		}
	}

	public void setViewState(ViewRepository viewState) {
		getDownloadTable().setToggleContext(viewState.getTableGroupContext());
	}

	public void initialize() {
		GridBagConstraints tabPanelConstraints = new GridBagConstraints();
		tabPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		tabPanelConstraints.gridx = 0;
		tabPanelConstraints.gridy = 0;
		tabPanelConstraints.gridwidth = 1;
		tabPanelConstraints.weightx = 1.0D;

		GridBagConstraints searchPanelConstraints = new GridBagConstraints();
		searchPanelConstraints.gridx = 1;
		searchPanelConstraints.gridy = 0;

		getTopPanel().setMaximumSize(MINIMUM_TOOLBAR_SIZE);
		getTopPanel().add(getSearchPanel(), searchPanelConstraints);
		getTopPanel().add(getTabPanel(), tabPanelConstraints);
		getMiddlePanel().add(getMainPanel(), BorderLayout.CENTER);
	}

	@Override
	protected JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints separatorConstraints = new GridBagConstraints();
			separatorConstraints.gridx = 0;
			separatorConstraints.gridy = 1;
			separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			separatorConstraints.weightx = 1.0D;
			GridBagConstraints mainDownloadConstraints = new GridBagConstraints();
			mainDownloadConstraints.gridx = 0;
			mainDownloadConstraints.gridy = 2;
			mainDownloadConstraints.fill = GridBagConstraints.BOTH;
			mainDownloadConstraints.weightx = 1.0D;
			mainDownloadConstraints.weighty = 1.0D;
			GridBagConstraints bottomSeparatorConstraints = new GridBagConstraints();
			bottomSeparatorConstraints.gridx = 0;
			bottomSeparatorConstraints.gridy = 3;
			bottomSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			bottomSeparatorConstraints.weightx = 1.0D;
			GridBagConstraints bottomPanelConstraints = new GridBagConstraints();
			bottomPanelConstraints.gridx = 0;
			bottomPanelConstraints.gridy = 4;
			bottomPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			bottomPanelConstraints.weightx = 1.0D;
			mainPanel.add(getMainDownloadPanel(), mainDownloadConstraints);
			mainPanel.add(getBottomSeparatorPanel(), bottomSeparatorConstraints);
			mainPanel.add(getBottomPanel(), bottomPanelConstraints);
		}
		return mainPanel;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.setPreferredSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setMaximumSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setMinimumSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setName(BOTTOM_PANEL_NAME);
			bottomPanel.add(getDownloadButtonsPanel(), BorderLayout.CENTER);
			getDownloadTable().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int i = getDownloadTable().rowAtPoint(e.getPoint());
					if (i < 0) {
						getDownloadTable().clearSelection();
					}
					if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
						if (!getDownloadTable().isRowSelected(i) && i >= 0) {
							getDownloadTable().setRowSelectionInterval(i, i);
						}
						if (getDownloadTable().getSelectedRowCount() > 0) {
							getPopupMenu().show(getDownloadTable(), e.getX(), e.getY());
						}
					}
				}
			});
		}
		return bottomPanel;
	}

	public void resumeDownloads() {
		getViewEngine().sendValueAction(Actions.Downloads.RESUME, selectedDownloads);
		enableAvailableActions();
	}

	public void deleteDownloads() {
		new DeleteFlow(getViewEngine(), dialogFactory).deleteDownloads(selectedDownloads);
		enableAvailableActions();
	}

	public void pauseDownloads() {
		getViewEngine().sendValueAction(Actions.Downloads.PAUSE, selectedDownloads);
		enableAvailableActions();
	}

	public void cleanUpDownloads() {
		getViewEngine().send(Actions.Downloads.CLEAN_UP);
		enableAvailableActions();
	}

	private JPopupMenu getPopupMenu() {
		JPopupMenu popUpMenu = new JPopupMenu();
		popUpMenu.add(getPauseMenuItem());
		popUpMenu.add(getResumeMenuItem());
		popUpMenu.add(deleteMenuItem);
		popUpMenu.add(cleanUpMenuItem);
		return popUpMenu;
	}

	/**
	 * This method initializes mainDescriptionPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainDownloadPanel() {
		if (mainDownloadPanel == null) {
			GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
			scrollPaneConstraints.fill = GridBagConstraints.BOTH;
			scrollPaneConstraints.gridy = 0;
			scrollPaneConstraints.weightx = 1.0;
			scrollPaneConstraints.weighty = 1.0;
			scrollPaneConstraints.gridx = 0;
			mainDownloadPanel = new JPanel();
			mainDownloadPanel.setSize(MAIN_DOWNLOAD_PANEL_SIZE);
			mainDownloadPanel.setPreferredSize(MAIN_DOWNLOAD_PANEL_SIZE);
			mainDownloadPanel.setLayout(new GridBagLayout());
			mainDownloadPanel.add(getDescriptionScrollPane(), scrollPaneConstraints);
		}
		return mainDownloadPanel;
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
			searchPanel.setPreferredSize(SEARCH_PANEL_DEFAULT_SIZE);
			searchPanel.setMinimumSize(SEARCH_PANEL_DEFAULT_SIZE);
			searchPanel.setMaximumSize(SEARCH_PANEL_DEFAULT_SIZE);
			searchPanel.add(getLupaBoton(), BorderLayout.WEST);
			searchPanel.add(getSearchTextField(), BorderLayout.CENTER);
			searchPanel.add(getClearSearchButton(), BorderLayout.EAST);
		}
		return searchPanel;
	}

	/**
	 * This method initializes descriptionScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getDescriptionScrollPane() {
		if (descriptionScrollPane == null) {
			descriptionScrollPane = new JScrollPane();
			descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			descriptionScrollPane.setViewportView(getDownloadTable());
			descriptionScrollPane.setName(DESCRIPTION_SCROLL_PANE_NAME);
		}
		return descriptionScrollPane;
	}

	/**
	 * This method initializes searchTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
			searchTextField.setDocument(new JTextFieldLimit(MAX_CHARACTERS));
			searchTextField.setName(SEARCH_TEXT_FIELD_NAME);
			searchTextField.setMinimumSize(SEARCH_TEXT_MIMIMUM_SIZE);
			searchTextField.setMaximumSize(SEARCH_TEXT_DEFAULT_SIZE);
			searchTextField.setPreferredSize(SEARCH_TEXT_DEFAULT_SIZE);
			searchTextField.addKeyListener(new SpacerKeyListener());
		}
		return searchTextField;
	}

	/**
	 * This method initializes lupaBoton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getLupaBoton() {
		if (lupaButton == null) {
			lupaButton = new JButton();
			lupaButton.setPreferredSize(LUPA_BUTTON_DEFAULT_SIZE);
			lupaButton.setName(LUPA_BUTTON_NAME);
		}
		return lupaButton;
	}

	/**
	 * This method initializes clearSearchButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getClearSearchButton() {
		if (clearSearchButton == null) {
			clearSearchButton = new JButton();
			clearSearchButton.setName(CLEAR_SEARCH_BUTTON_NAME);
			clearSearchButton.setPreferredSize(CLEARSEARCH_BUTTON_DEFAULT_SIZE);
			clearSearchButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getSearchTextField().setText("");
				}
			});
		}
		return clearSearchButton;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getTabPanel() {
		if (tabPanel == null) {
			tabPanel = new JPanel();
			tabPanel.setLayout(new BorderLayout());
			tabPanel.setMinimumSize(TABPANEL_MINIMUM_SIZE);
			tabPanel.setMaximumSize(TABPANEL_MAXIMUM_SIZE);
			tabPanel.setPreferredSize(TABPANEL_DEFAULT_SIZE);
			JLabel downloadLabel = getDownloadLabel();
			tabPanel.add(downloadLabel, BorderLayout.WEST);
		}
		return tabPanel;
	}

	private JLabel getDownloadLabel() {
		if (downloadLabel == null) {
			downloadLabel = new JLabel();
			downloadLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
			downloadLabel.setBorder(EMPTY_BORDER);
		}
		return downloadLabel;
	}

	/**
	 * This method initializes separatorPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getBottomSeparatorPanel() {
		if (bottomSeparatorPanel == null) {
			bottomSeparatorPanel = new JPanel();
			bottomSeparatorPanel.setName(SEPARATOR_PANEL_NAME);
			bottomSeparatorPanel.setPreferredSize(SEPARATOR_PANEL_DEFAULT_SIZE);
			bottomSeparatorPanel.setMinimumSize(SEPARATOR_PANEL_MIMIMUM_SIZE);
			bottomSeparatorPanel.setMaximumSize(SEPARATOR_PANEL_MIMIMUM_SIZE);
		}
		return bottomSeparatorPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getPauseMenuItem().setText(messages.getMessage("downloads.pauseLabel"));
		getResumeMenuItem().setText(messages.getMessage("downloads.resumeLabel"));
		cleanUpMenuItem.setText(messages.getMessage("downloads.cleanUpLabel"));
		deleteMenuItem.setText(messages.getMessage("downloads.deleteLabel"));
		getSearchTextField().setToolTipText(messages.getMessage("tooltip.searchDownloadDT"));
		getDownloadLabel().setText(messages.getMessage("downloads.title"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getDownloadTable().removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getDownloadTable().setMessages(messages);
	}

	public void selectAllTracksInDownloadTable() {
		this.downloadTable.selectAll();

	}

	public ObserverCollection<ObserveObject> onClose() {
		return onCloseEvent;
	}

	public void setResumeMenuItem(JMenuItem resumeMenuItem) {
		this.resumeMenuItem = resumeMenuItem;
	}

	public JMenuItem getResumeMenuItem() {
		return resumeMenuItem;
	}

	public void setPauseMenuItem(JMenuItem pauseMenuItem) {
		this.pauseMenuItem = pauseMenuItem;
	}

	public JMenuItem getPauseMenuItem() {
		return pauseMenuItem;
	}

	private DownloadButtonsPanel getDownloadButtonsPanel() {
		if (downloadButtonsPanel == null) {
			downloadButtonsPanel = new DownloadButtonsPanel();
		}
		return downloadButtonsPanel;
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
	}

	public void setDownloadButtonsPanel(DownloadButtonsPanel downloadButtonsPanel) {
		this.downloadButtonsPanel = downloadButtonsPanel;
	}

	public void setDialogFactory(DialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}
}