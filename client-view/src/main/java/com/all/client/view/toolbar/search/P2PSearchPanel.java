package com.all.client.view.toolbar.search;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.all.action.ValueAction;
import com.all.appControl.ViewEngineConfigurator;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.DecoratedSearchData;
import com.all.client.model.ModelTransfereable;
import com.all.client.view.MiddleCloseablePanel;
import com.all.client.view.View;
import com.all.client.view.components.MenuItems;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.util.MacUtils;
import com.all.core.actions.Actions;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.JTextFieldLimit;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.common.view.util.SpacerKeyListener;
import com.all.core.events.Events;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.downloader.alllink.AllLink;
import com.all.downloader.search.SearchData;
import com.all.downloader.search.SearchDataEvent;
import com.all.downloader.search.SearchErrorEvent;
import com.all.downloader.search.SearchProgressEvent;
import com.all.event.EventMethod;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.stats.SearchP2PWordStat;
import com.all.shared.stats.usage.UserActions;
import com.all.shared.util.StringNormalizer;

public final class P2PSearchPanel extends MiddleCloseablePanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;
	
	private static final int MAX_CHARACTERS = 40;

	private static final Dimension BOTTOM_PANEL_DEFAULT_SIZE = new Dimension(100, 27);

	private static final Dimension BOTTOM_PANEL_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 27);

	private static final Dimension CLEAR_SEARCH_BUTTON_DEFAULT_SIZE = new Dimension(32, 30);

	private static final Dimension SEARCH_CONTAINER_DEFAULT_SIZE = new Dimension(100, 63);
	
	private static final Dimension SEARCH_CONTAINER_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 63);
	
	private static final Dimension SEARCH_TEXT_MAX_SIZE = new Dimension(300, 30);
	
	private static final Dimension SEARCH_TEXT_SIZE = new Dimension(145, 30);

	private static final Dimension TAB_SCROLL_BUTTON_DEFAULT_SIZE = new Dimension(22, 24);

	private static final String SEARCH_PANEL_NAME = "searchMiddlePannelBackground";

	private static final String TABBED_PANEL_NAME = "searchTabPannelBackground";

	private static final String TAB_SCROLL_LEFT_BUTTON_NAME = "searchTabPannelArrowLeft";

	private static final String TAB_SCROLL_RIGHT_BUTTON_NAME = "searchTabPannelArrowRigth";

	private JButton clearSearchButton;
	
	private JButton tabScrollRigtButton;
	
	private JButton tabScrollLeftButton;

	private JSlider progressBar;

	private P2PSearchTable searchTable;

	private JScrollPane searchScroll;

	private JPanel bottomPanel;

	private JToggleButton downloadButton;

	private JPanel searchContainer;

	private JLabel resultLabel;

	private JPanel searchTextPanel;

	private JTextField searchTextField;

	private JPanel tabbedPanel;

	private JPanel tabsPanel;

	private JScrollPane tabsScrollPane;

	private JMenuItem downloadItem;

	private JPopupMenu popupMenu;

	private JPanel searchPanel;

	private Map<String, TabSearchData> tabSearchDataMap = new HashMap<String, TabSearchData>();
	
	private TabSearchData currentTabSearchData = null;

	private DialogFactory dialogFactory;

	private Observable<ObserveObject> onCloseEvent = new Observable<ObserveObject>();


	private JPanel mainPanel;

	public P2PSearchPanel() {
		super();
	}

	public void setDialogFactory(DialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	public void initialize() {
		getMiddlePanel().add(getMainPanel(), BorderLayout.CENTER);
		showOrHideTabbedPanel();
		wire();
	}

	@Override
	protected JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints searchConstraints = new GridBagConstraints();
			searchConstraints.fill = GridBagConstraints.HORIZONTAL;
			searchConstraints.gridx = 0;
			searchConstraints.gridy = 1;
			searchConstraints.weightx = 1;
			searchConstraints.weighty = 0;
			searchConstraints.insets = new Insets(0, 0, 2, 0);

			GridBagConstraints tableConstraints = new GridBagConstraints();
			tableConstraints.fill = GridBagConstraints.BOTH;
			tableConstraints.gridx = 0;
			tableConstraints.gridy = 2;
			tableConstraints.weightx = 1;
			tableConstraints.weighty = 1;
			tableConstraints.insets = new Insets(0, 0, 0, 0);

			GridBagConstraints bottomConstraints = new GridBagConstraints();
			bottomConstraints.fill = GridBagConstraints.HORIZONTAL;
			bottomConstraints.gridx = 0;
			bottomConstraints.gridy = 3;
			bottomConstraints.weightx = 1;
			bottomConstraints.weighty = 0;
			bottomConstraints.insets = new Insets(2, 0, 0, 0);
			mainPanel.add(getSearchContainer(), searchConstraints);
			mainPanel.add(getSearchScroll(), tableConstraints);
			mainPanel.add(getBottomPanel(), bottomConstraints);
		}
		return mainPanel;
	}

	private void wire() {
		getSearchTextField().getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				checkClear();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkClear();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkClear();
			}

			private void checkClear() {
				String text = getSearchTextField().getText();
				if (text.length() == 0) {
					getClearSearchButton().setName("downloadClearSearchTextFieldInvisiable");
				} else {
					getClearSearchButton().setName("downloadClearSearchTextFieldVisiable");
				}
			}
		});
		getSearchTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				getDownloadButton().setEnabled(getSearchTable().getSelectedRowCount() > 0);
			}
		});
		getDownloadButton().setEnabled(getSearchTable().getSelectedRowCount() > 0);
		getSearchTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
					getDownloadItem().setEnabled(getSearchTable().getSelectedRowCount() > 0);
					getPopupMenu().show(getSearchTable(), e.getX(), e.getY());
				}
			}
		});
		getSearchTable().setDragEnabled(true);
		getSearchTable().setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Transferable createTransferable(JComponent c) {
				ModelCollection modeldragged = new ModelCollection();
				List<DecoratedSearchData> selectedValues = getSearchTable().getSelectedValues();
				for (DecoratedSearchData searchResult : selectedValues) {
					modeldragged.getTracks().add(searchResult.toTrack());
				}
				modeldragged.setRemote(true);
				if (modeldragged.isEmpty()) {
					return null;
				}

				getViewEngine().sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Downloads.DRAG_SEARCH_RESULT);
				return new ModelTransfereable(ModelSource.remote(), modeldragged, getViewEngine().get(Model.TRACK_REPOSITORY));
			}

			@Override
			public boolean importData(TransferSupport support) {
				return true;
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return false;
			}

			@Override
			public int getSourceActions(JComponent c) {
				return COPY;
			}

		});
		getSearchTable().setDropTarget(null);

		getSearchContainer().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent componentEvent) {
				showOrHideScrollbarButtons();
				resizeTabsPanel();
				showCompletelySelectedTab();
			}
		});

		getTabsPanel().addContainerListener(new ContainerListener() {
			@Override
			public void componentRemoved(ContainerEvent e) {
				showOrHideTabbedPanel();
				showOrHideScrollbarButtons();
				resizeTabsPanel();
				showCompletelySelectedTab();
				getTabsPanel().revalidate();
				getTabsPanel().repaint();
			}

			@Override
			public void componentAdded(ContainerEvent e) {
				showOrHideTabbedPanel();
				showOrHideScrollbarButtons();
				getTabsPanel().revalidate();
				getTabsPanel().repaint();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						resizeTabsPanel();
						showCompletelySelectedTab();
					}
				});
			}
		});

		getTabScrollLeftButton().addMouseListener(new TimingMouseAdapter(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JScrollBar scrollBar = getTabsScrollPane().getHorizontalScrollBar();
				scrollBar.setValue(scrollBar.getValue() - scrollBar.getUnitIncrement(0));
			}
		}));

		getTabScrollRigtButton().addMouseListener(new TimingMouseAdapter(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JScrollBar scrollBar = getTabsScrollPane().getHorizontalScrollBar();
				scrollBar.setValue(scrollBar.getValue() + scrollBar.getUnitIncrement(0));
			}
		}));

		getCloseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getViewEngine().send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
				onCloseEvent.fire(ObserveObject.EMPTY);
			}
		});

		getDownloadButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doDownload();
			}
		});
		getSearchTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
					doDownload();
				}
			}
		});
		getDownloadItem().setVisible(true);
		getDownloadItem().setEnabled(true);
		getDownloadItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doDownload();
			}
		});
		
		getSearchTextField().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					cleanUpResults();

					String toSearch = getSearchTextField().getText().trim();
					getSearchTextField().setText("");
					if ("".equals(toSearch)) {
						return;
					}

					if (tabSearchDataMap.containsKey(toSearch.toLowerCase())) {
						setCurrentTab(tabSearchDataMap.get(toSearch.toLowerCase()));
						return;
					}

					final SearchTab searchTab = new SearchTab(toSearch);

					TabSearchData tabSearchData = new TabSearchData(toSearch.toLowerCase(), searchTab);
					tabSearchDataMap.put(toSearch.toLowerCase(), tabSearchData);

					getTabsPanel().add(searchTab);

					setCurrentTab(tabSearchData);

					getViewEngine().sendValueAction(Actions.Downloads.SEARCH, toSearch);

					String normalizeSearchString = StringNormalizer.normalize(toSearch).toLowerCase();
					getViewEngine().sendValueAction(ApplicationActions.REPORT_USER_STAT,
							new SearchP2PWordStat(getViewEngine().get(Model.CURRENT_USER).getEmail(), normalizeSearchString));
				}
			}
		});

	}

	@EventMethod(Events.Downloads.ADDED_ID)
	public void onDownloadAdded() {
		getSearchTable().repaintIndex();
	}

	@EventMethod(Events.Downloads.REMOVED_ID)
	public void onDownloadRemoved() {
		getSearchTable().repaintIndex();
	}

	@EventMethod(Events.Downloads.UPDATED_ID)
	public void onDownloadUpdated() {
		getSearchTable().repaintIndex();
	}

	@EventMethod(Events.Downloads.ALL_MODIFIED_ID)
	public void onDownloadAllModified() {
		getSearchTable().repaintIndex();
	}
	
	@EventMethod(Events.Search.PROGRESS_ID)
	public void onSearchUpdateProgress(SearchProgressEvent progressEvent) {
		String keywordSearch = progressEvent.getKeywordSearch();
		int progress = progressEvent.getProgress();

		TabSearchData tabSearchData = tabSearchDataMap.get(keywordSearch.toLowerCase());
		// FIXME this second condition is a patch,
		// com.all.downloader.search.Searcher should have one stopSearch method
		// which also should free any resources and avoid two searches for the
		// same keyword
		if (tabSearchData != null && progress > tabSearchData.getProgress()) {
			tabSearchData.setProgress(progress);
			if (isCurrentSearchTab(tabSearchData)) {
				progressBar.setValue(progress);
			}
		}
	}

	@EventMethod(Events.Search.DATA_UPDATED_ID)
	public void onUpdatedSearchData(SearchDataEvent searchEvent) {
		String keywordSearch = searchEvent.getKeywordSearch();
		SearchData searchData = searchEvent.getSearchData();

		TabSearchData tabSearchData = tabSearchDataMap.get(keywordSearch.toLowerCase());

		if (tabSearchData == null) {
			return;
		}

		DecoratedSearchData searchResult = tabSearchData.getResults().get(searchData.getFileHash());
		if (searchResult != null) {
			searchResult.setPeers(searchResult.getPeers() + 1);
			if (isCurrentSearchTab(tabSearchData)) {
				getSearchTable().updateRow(searchResult);
				getSearchTable().updateTable();
			}
		} else {
			DecoratedSearchData decoratedSearchData = decorate(searchData);
			tabSearchData.getResults().put(searchData.getFileHash(), decoratedSearchData);
			if (isCurrentSearchTab(tabSearchData)) {
				getSearchTable().addRow(decoratedSearchData);
			}
		}
	}
	
	@EventMethod(Events.Search.ERROR_ID)
	public void onError(SearchErrorEvent searchErrorEvent) {
		TabSearchData tabSearchData = tabSearchDataMap.get(searchErrorEvent.getKeyword());
		if (tabSearchData != null) {
			dialogFactory.showErrorDialog("searchResults.network.error", searchErrorEvent.getKeyword());
		}
	}

	private DecoratedSearchData decorate(SearchData searchData) {
		String fileHash = searchData.getFileHash();
		int rowCount = getSearchTable().getModel().getRowCount();
		AllLink allLink = new AllLink(null, fileHash);
		
		DecoratedSearchData res = new DecoratedSearchData(searchData, rowCount, allLink.toString());
		
		return res;
	}
	
	private void doDownload() {
		List<DecoratedSearchData> selectedValues = getSearchTable().getSelectedValues();
		if (selectedValues != null && !selectedValues.isEmpty()) {
			for (DecoratedSearchData searchResult : selectedValues) {
				getViewEngine().send(Actions.Downloads.ADD_SEARCH_DATA, new ValueAction<DecoratedSearchData>(searchResult));
			}
		}
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(messages.getMessage("searchResults.title"));
		getDownloadButton().setText(messages.getMessage("searchResults.download"));
		getResultLabel().setText(messages.getMessage("searchResults.message"));
		MenuItems.DOWNLOAD.internationalize(getDownloadItem(), messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	private JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.add(getDownloadItem());
		}
		return popupMenu;
	}

	private JMenuItem getDownloadItem() {
		if (downloadItem == null) {
			downloadItem = MenuItems.DOWNLOAD.getItem();
			downloadItem.setVisible(false);
			downloadItem.setEnabled(false);
		}
		return downloadItem;
	}

	private JPanel getSearchContainer() {
		if (searchContainer == null) {
			searchContainer = new JPanel();
			searchContainer.setLayout(new MigLayout("fill, insets 0 0 0 0, gap 0! 0!, hidemode 2"));
			searchContainer.setMinimumSize(SEARCH_CONTAINER_DEFAULT_SIZE);
			searchContainer.setPreferredSize(SEARCH_CONTAINER_DEFAULT_SIZE);
			searchContainer.setMaximumSize(SEARCH_CONTAINER_MAXIMUM_SIZE);

			searchContainer.add(getSearchPanel(), "grow, h 38!");
			searchContainer.add(getTabbedPanel(), "newline, grow, h 25!");
		}
		return searchContainer;
	}

	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			searchPanel = new JPanel();
			searchPanel.setName(SEARCH_PANEL_NAME);
			searchPanel.setLayout(new MigLayout("insets 4 8 4 8", "[]push[]", "[center]"));
			searchPanel.add(getResultLabel());
			searchPanel.add(getSearchTextPanel(), "gapleft 5");
		}
		return searchPanel;
	}

	private JPanel getTabbedPanel() {
		if (tabbedPanel == null) {
			tabbedPanel = new JPanel();
			tabbedPanel.setName(TABBED_PANEL_NAME);
			tabbedPanel.setLayout(new BorderLayout());
			tabbedPanel.add(getTabScrollRigtButton(), BorderLayout.EAST);
			tabbedPanel.add(getTabScrollLeftButton(), BorderLayout.WEST);
			tabbedPanel.add(getTabsScrollPane(), BorderLayout.CENTER);
		}
		return tabbedPanel;
	}

	private JButton getTabScrollLeftButton() {
		if (tabScrollLeftButton == null) {
			tabScrollLeftButton = new JButton();
			tabScrollLeftButton.setName(TAB_SCROLL_LEFT_BUTTON_NAME);
			tabScrollLeftButton.setPreferredSize(TAB_SCROLL_BUTTON_DEFAULT_SIZE);
			tabScrollLeftButton.setMinimumSize(TAB_SCROLL_BUTTON_DEFAULT_SIZE);
			tabScrollLeftButton.setMaximumSize(TAB_SCROLL_BUTTON_DEFAULT_SIZE);
		}
		return tabScrollLeftButton;
	}

	private JButton getTabScrollRigtButton() {
		if (tabScrollRigtButton == null) {
			tabScrollRigtButton = new JButton();
			tabScrollRigtButton.setName(TAB_SCROLL_RIGHT_BUTTON_NAME);
			tabScrollRigtButton.setPreferredSize(TAB_SCROLL_BUTTON_DEFAULT_SIZE);
			tabScrollRigtButton.setMinimumSize(TAB_SCROLL_BUTTON_DEFAULT_SIZE);
			tabScrollRigtButton.setMaximumSize(TAB_SCROLL_BUTTON_DEFAULT_SIZE);
		}
		return tabScrollRigtButton;
	}

	private JScrollPane getTabsScrollPane() {
		if (tabsScrollPane == null) {
			tabsScrollPane = new JScrollPane();
			tabsScrollPane.setOpaque(false);
			tabsScrollPane.getViewport().setOpaque(false);
			tabsScrollPane.setViewportView(getTabsPanel());
			tabsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			tabsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			tabsScrollPane.getHorizontalScrollBar().setUnitIncrement(50);
		}
		return tabsScrollPane;
	}

	private JPanel getTabsPanel() {
		if (tabsPanel == null) {
			tabsPanel = new JPanel();
			tabsPanel.setLayout(new MigLayout("fill, insets 0 3 0 3, gap 0! 0!", "[110:250:250,grow,fill]", "[25!]"));
			tabsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		}
		return tabsPanel;
	}

	private JPanel getSearchTextPanel() {
		if (searchTextPanel == null) {
			searchTextPanel = new JPanel();
			searchTextPanel.setLayout(new BorderLayout());
			searchTextPanel.add(getSearchTextField(), BorderLayout.CENTER);
			searchTextPanel.add(getClearSearchButton(), BorderLayout.EAST);
		}
		return searchTextPanel;
	}

	private JButton getClearSearchButton() {
		if (clearSearchButton == null) {
			clearSearchButton = new JButton();
			clearSearchButton.setName("downloadClearSearchTextFieldInvisiable");
			clearSearchButton.setPreferredSize(CLEAR_SEARCH_BUTTON_DEFAULT_SIZE);
			clearSearchButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					getSearchTextField().setText("");
				}
			});
		}
		return clearSearchButton;
	}

	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
			searchTextField.setDocument(new JTextFieldLimit(MAX_CHARACTERS));
			searchTextField.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			searchTextField.setName("downloadSearchTextField");
			searchTextField.setMinimumSize(SEARCH_TEXT_SIZE);
			searchTextField.setMaximumSize(SEARCH_TEXT_SIZE);
			searchTextField.setPreferredSize(SEARCH_TEXT_MAX_SIZE);
			searchTextField.addKeyListener(new CopyPasteKeyAdapterForMac());
			searchTextField.addKeyListener(new SpacerKeyListener());
		}
		return searchTextField;
	}

	private JLabel getResultLabel() {
		if (resultLabel == null) {
			resultLabel = new JLabel();
			resultLabel.setName(SynthFonts.BOLD_FONT14_GRAY100_100_100);
		}
		return resultLabel;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setName("searchBottomPannelBackground");
			bottomPanel.setLayout(new GridBagLayout());
			bottomPanel.setPreferredSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setMinimumSize(BOTTOM_PANEL_DEFAULT_SIZE);
			bottomPanel.setMaximumSize(BOTTOM_PANEL_MAXIMUM_SIZE);

			GridBagConstraints progressBarConstraints = new GridBagConstraints();
			progressBarConstraints.gridx = 0;
			progressBarConstraints.gridy = 0;
			progressBarConstraints.weightx = 1;
			progressBarConstraints.weighty = 1;
			progressBarConstraints.fill = GridBagConstraints.BOTH;
			progressBarConstraints.insets = new Insets(8, 35, 7, 20);

			GridBagConstraints downloadButtonContraints = new GridBagConstraints();
			downloadButtonContraints.gridx = 1;
			downloadButtonContraints.gridy = 0;
			downloadButtonContraints.insets = new Insets(6, 24, 6, 6);

			bottomPanel.add(getProgressBar(), progressBarConstraints);
			bottomPanel.add(getDownloadButton(), downloadButtonContraints);
		}
		return bottomPanel;
	}

	private JToggleButton getDownloadButton() {
		if (downloadButton == null) {
			downloadButton = new JToggleButton();
			Dimension size = new Dimension(130, 27);
			downloadButton.setSize(size);
			downloadButton.setPreferredSize(size);
			downloadButton.setMaximumSize(size);
			downloadButton.setMinimumSize(size);
			downloadButton.setText("Download");
			downloadButton.setName("btnDownloadInSearch");
		}
		return downloadButton;
	}

	private JScrollPane getSearchScroll() {
		if (searchScroll == null) {
			searchScroll = new JScrollPane(getSearchTable());
		}
		return searchScroll;
	}

	private JSlider getProgressBar() {
		if (progressBar == null) {
			progressBar = new JSlider();
			progressBar.setName("bigGrayProgressBar");
			progressBar.setMaximum(100);
			progressBar.setRequestFocusEnabled(false);
			progressBar.setValue(0);
			progressBar.setPaintLabels(false);
			progressBar.setOpaque(false);
			progressBar.setFocusable(false);
			progressBar.setEnabled(false);
		}
		return progressBar;
	}

	private P2PSearchTable getSearchTable() {
		if (searchTable == null) {
			searchTable = new P2PSearchTable();
			searchTable.setViewEngine(getViewEngine());
		}
		return searchTable;
	}

	public ObserverCollection<ObserveObject> onClose() {
		return onCloseEvent;
	}

	private boolean isCurrentSearchTab(TabSearchData tabSearchData) {
		return tabSearchData == currentTabSearchData;
	}

	private void setCurrentTab(TabSearchData tabSearchData) {
		if (tabSearchData == null) {
			return;
		}

		currentTabSearchData = tabSearchData;

		// match the model with the selected tab
		getSearchTable().setModel(currentTabSearchData.getResults().values());
		getProgressBar().setValue(currentTabSearchData.getProgress());

		// deactive all tabs and activate current tab
		for (java.awt.Component component : getTabsPanel().getComponents()) {
			if (component instanceof SearchTab) {
				SearchTab searchTab = (SearchTab) component;
				searchTab.inactive();
			}
		}
		currentTabSearchData.getSearchTab().active();

		showCompletelySelectedTab();
	}

	private void showOrHideTabbedPanel() {
		if (getTabbedPanel().isVisible() && getTabsPanel().getComponentCount() == 0) {
			getTabbedPanel().setVisible(false);
			getSearchContainer().setMinimumSize(new Dimension(100, 38));
			getSearchContainer().setPreferredSize(new Dimension(100, 38));
			getSearchContainer().setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

		} else if (!getTabbedPanel().isVisible() && getTabsPanel().getComponentCount() > 0) {
			getTabbedPanel().setVisible(true);
			getSearchContainer().setMinimumSize(SEARCH_CONTAINER_DEFAULT_SIZE);
			getSearchContainer().setPreferredSize(SEARCH_CONTAINER_DEFAULT_SIZE);
			getSearchContainer().setMaximumSize(SEARCH_CONTAINER_MAXIMUM_SIZE);
		}
	}

	private void closeTab(TabSearchData tabSearchData) {
		// remove it logically
		tabSearchDataMap.remove(tabSearchData.getKeyword());
		tabSearchData.clear();

		// look for the index of the closed tab
		int index = 0;
		for (index = 0; index < getTabsPanel().getComponentCount(); index++) {
			if (getTabsPanel().getComponent(index) == tabSearchData.getSearchTab()) {
				break;
			}
		}

		// remove it graphically
		getTabsPanel().remove(tabSearchData.getSearchTab());

		// set the previous tab, or the next tab if is the first one
		if (getTabsPanel().getComponentCount() > 0) {
			index = index > 0 ? index - 1 : 0;
		} else {
			// if entered here, then this was the last tab
			cleanUpResults();
			return;
		}

		if (getTabsPanel().getComponent(index) instanceof SearchTab) {
			SearchTab searchTab = (SearchTab) getTabsPanel().getComponent(index);
			setCurrentTab(tabSearchDataMap.get(searchTab.getTabText().toLowerCase()));
		}

	}

	private void cleanUpResults() {
		getSearchTable().clear();
		getSearchTable().updateTable();
		getSearchTable().invalidate();
		SwingUtilities.getWindowAncestor(getSearchTable()).validate();
		getProgressBar().setValue(0);
	}

	private void showOrHideScrollbarButtons() {
		int tabsWidth = getTabsWidth();
		boolean showScrollButtons = tabsWidth > getTabbedPanel().getWidth();
		getTabScrollLeftButton().setVisible(showScrollButtons);
		getTabScrollRigtButton().setVisible(showScrollButtons);
	}

	private int getTabsWidth() {
		int tabsWidth = 6; // insets of 3px left and other 3px on the right
		// NOTE: we calculate the tabswidth like this because often the last tab has
		// a width of 0px, causing it not to be
		// displayed and to calculate the scroll buttons visibility wrongly
		if (getTabsPanel().getComponents().length > 0) {
			tabsWidth += getTabsPanel().getComponents()[0].getWidth() * getTabsPanel().getComponents().length;
		}
		return tabsWidth;
	}

	private void resizeTabsPanel() {
		Dimension newSize = new Dimension(getTabsWidth(), getTabsPanel().getHeight());
		getTabsPanel().setPreferredSize(newSize);
		getTabsPanel().setMinimumSize(newSize);
		getTabsPanel().setSize(newSize);
	}

	private void showCompletelySelectedTab() {
		// if tab not fully visible, show the tab completely
		Rectangle viewRect = getTabsScrollPane().getViewport().getViewRect();
		Rectangle tabBounds = new Rectangle();
		if (currentTabSearchData != null) {
			tabBounds = currentTabSearchData.getSearchTab().getBounds();
		}
		if (!viewRect.contains(tabBounds)) {
			if (viewRect.x > tabBounds.x) {
				getTabsScrollPane().getViewport().setViewPosition(tabBounds.getLocation());
			} else {
				viewRect.x += (tabBounds.x + tabBounds.width) - (viewRect.x + viewRect.width);
				getTabsScrollPane().getViewport().setViewPosition(viewRect.getLocation());
			}
		}
	}

	private final class TimingMouseAdapter extends MouseAdapter {
		private static final int DELAY = 50;
		private Timer timer;
		private ActionListener listener;

		public TimingMouseAdapter(ActionListener listener) {
			this.listener = listener;
		}

		public void mousePressed(MouseEvent e) {
			timer = new Timer(DELAY, listener);
			timer.start();
		}

		public void mouseReleased(MouseEvent e) {
			if (timer != null) {
				timer.stop();
			}
		}
	}

	private class SearchTab extends JPanel {

		private static final long serialVersionUID = 3307767642198927450L;
		private static final String INACTIVE_TAB = "searchTabInactivePanel";
		private static final String ACTIVE_TAB = "searchTabActivePanel";
		private final String tabText;
		private JLabel label;

		public SearchTab(final String tabText) {
			this.tabText = tabText;

			JButton closeButton = new JButton();
			closeButton.setName("searchTabCloseButton");

			label = new JLabel(tabText);
			label.setName(SynthFonts.BOLD_FONT12_GRAY110_110_110);

			this.setMinimumSize(new Dimension(110, 25));
			this.setPreferredSize(new Dimension(250, 25));
			this.setMaximumSize(new Dimension(250, 25));
			this.setName(INACTIVE_TAB);
			this.setLayout(new MigLayout("insets 0 9 4 9", "[]push[]", "[21!]"));
			this.add(label, "w 10:220:220, grow");
			this.add(closeButton, "w 18!, h 18!");

			this.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					setCurrentTab(tabSearchDataMap.get(tabText.toLowerCase()));
				}
			});

			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					closeTab(tabSearchDataMap.get(tabText.toLowerCase()));
				}
			});
		}

		public void active() {
			this.setName(SearchTab.ACTIVE_TAB);
			this.label.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
		}

		public void inactive() {
			this.setName(SearchTab.INACTIVE_TAB);
			this.label.setName(SynthFonts.BOLD_FONT12_GRAY110_110_110);
		}

		public String getTabText() {
			return tabText;
		}

	}

	private class TabSearchData {
		private final String keyword;
		private final SearchTab searchTab;
		private int progress = 0;
		private Map<String, DecoratedSearchData> results = new HashMap<String, DecoratedSearchData>();

		public TabSearchData(String keyword, SearchTab searchTab) {
			this.keyword = keyword;
			this.searchTab = searchTab;
		}

		public String getKeyword() {
			return keyword;
		}

		public void clear() {
			results.clear();
		}

		public SearchTab getSearchTab() {
			return searchTab;
		}

		public int getProgress() {
			return progress;
		}

		public void setProgress(int progress) {
			this.progress = progress;
		}

		public Map<String, DecoratedSearchData> getResults() {
			return results;
		}

		private P2PSearchPanel getOuterType() {
			return P2PSearchPanel.this;
		}

		@Override
		public String toString() {
			return TabSearchData.class.getSimpleName() + " for " + keyword;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			TabSearchData other = (TabSearchData) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (keyword == null) {
				if (other.keyword != null) {
					return false;
				}
			} else if (!keyword.equals(other.keyword)) {
				return false;
			}
			return true;
		}

	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine);
		getSearchTable().setViewEngine(viewEngine);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
	}

	public void setAppControlConfigurer(ViewEngineConfigurator configurer) {
		configurer.setupViewEngine(this);
	}

}
