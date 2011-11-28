package com.all.client.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.model.LocalDefaultSmartPlaylistImpl;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.view.components.GridBagConstraintsFactory;
import com.all.client.view.components.MenuItems;
import com.all.client.view.components.PreviewTree;
import com.all.client.view.components.PreviewTreeCellEditor;
import com.all.client.view.components.PreviewTreeCellRenderer;
import com.all.client.view.components.PreviewTreeEditListener;
import com.all.client.view.components.GridBagConstraintsFactory.FillMode;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.ModelTreeTransferHandler;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.dnd.ScrollPaneDragOverListener;
import com.all.client.view.dnd.TreeDropListener;
import com.all.client.view.flows.DeleteFlow;
import com.all.client.view.listeners.RestoreSelectionFocusListener;
import com.all.client.view.util.MacUtils;
import com.all.commons.Environment;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.actions.Actions;
import com.all.core.actions.ModelMoveAction;
import com.all.core.events.Events;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.SmartPlaylist;
import com.all.shared.model.TrackContainer;

@org.springframework.stereotype.Component
public class LocalPreviewPanel extends PreviewPanel {
	private static final Log log = LogFactory.getLog(PreviewPanel.class);
	private static final long serialVersionUID = 1L;

	private JMenuItem playMenuItem = null;
	private JMenuItem renamePlaylistMenuItem = null;
	private JMenuItem deleteItem = null;
	private JMenuItem createFolderMenuItem = null;
	private JMenuItem createPlaylistMenuItem = null;
	private JMenuItem createSmartPlaylistMenuItem = null;
	private JMenuItem moveOutFromFolderMenuItem = null;
	private JPopupMenu.Separator separatorAfterPlay = null;
	private JPopupMenu.Separator separatorAfterPlaylist = null;
	private JPopupMenu.Separator separatorAfterRename = null;
	private JPopupMenu.Separator separatorAfterDeleteFromFolder = null;
	private JPanel contentPanel = null;

	private final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("repaintComponentsThread"));
	@Autowired
	private ShortcutBinder shortcutBinder;
	@Autowired
	private MultiLayerDropTargetListener multiLayer;
	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private MyMusicHoverAnimation animation;
	private PreviewTree previewTree;

	@Autowired
	public LocalPreviewPanel(ViewEngine viewEngine) {
		super(viewEngine);
	}

	@Autowired
	private ViewEngine viewEngine;

	@Override
	protected void onRootSet(Root root) {
	}

	@PostConstruct
	public void setup() {
		TreeDropListener treeDropListener = new TreeDropListener(animation, viewEngine, getPreviewTree());

		this.setName("previewTreeBackground");

		getPreviewTree().addFocusListener(new RestoreSelectionFocusListener(viewEngine));
		getSmartPlaylistTree().addFocusListener(new RestoreSelectionFocusListener(viewEngine));

		getPreviewTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				checkShowMenu(e);
			}
		});
		getSmartPlaylistTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if ((e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e))
						&& getSmartPlaylistTree().getSelectionCount() == 1) {
					getDeleteItem().setEnabled(false);
					getDeleteItem().setVisible(false);
					getRenamePlaylistMenuItem().setEnabled(false);
					getRenamePlaylistMenuItem().setVisible(false);
					getPlayMenuItem().setEnabled(true);
					getSeparatorAfterPlay().setVisible(false);
					getSeparatorAfterPlaylist().setVisible(false);
					showDeleteFromFolderItem(false);
					getSeparatorAfterRename().setVisible(false);
					getGenericPopupMenu().show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		shortcutBinder.whenCopyInPreviewTree(this.getPreviewTree());
		shortcutBinder.whenPasteInPreviewTree(this.getPreviewTree());
		shortcutBinder.whenCopyInSmartPlaylistTree(this.getSmartPlaylistTree());
		shortcutBinder.whenCutInSmartPlaylistTree(this.getSmartPlaylistTree());
		shortcutBinder.whenPasteInSmartPlaylistTree(this.getSmartPlaylistTree());

		getPlayMenuItem().addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewEngine.send(Actions.Player.PLAY);
			}
		});
		getDeleteItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new DeleteFlow(viewEngine, dialogFactory).delete(getSelection());
			}
		});
		getRenamePlaylistMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_REQUEST_RENAME);
			}
		});
		getMoveOutFromFolderMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Root root = viewEngine.get(Model.USER_ROOT);
				ModelCollection model = new ModelCollection(viewEngine.get(Model.SELECTED_CONTAINER));
				viewEngine.send(Actions.Library.MODEL_MOVE, new ModelMoveAction(model, root));
			}
		});
		getCreatePlaylist().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_CREATE_PLAYLIST);
			}
		});
		getCreateFolder().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.MODEL_CREATE_FOLDER);
			}
		});

		multiLayer.addDragListener(getPreviewTree(), treeDropListener);
		multiLayer.addDropListener(getPreviewTree(), treeDropListener);
		multiLayer.addDragListener(getJScrollPane(), new ScrollPaneDragOverListener(getJScrollPane()));

		// Make it draggeable without the default behavior
		getPreviewTree().setTransferHandler(new ModelTreeTransferHandler(getPreviewTree(), false, viewEngine));
		// Remove default behavior added by the shit up there
		getPreviewTree().setDropTarget(null);

		getPreviewTree().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (!getPreviewTree().isEditing() && e.getKeyCode() == KeyEvent.VK_DELETE) {
					new DeleteFlow(viewEngine, dialogFactory).delete(getSelection());
				}
			}
		});
		getPreviewTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					DefaultMutableTreeNode nodeRightClicked = getPreviewTree().getTreeHelper()
							.getDefaultMutableTreeNodeFromCoordinates(e.getPoint());
					TreePath nodePath = new TreePath(nodeRightClicked.getPath());
					if (!getPreviewTree().isPathSelected(nodePath)) {
						getPreviewTree().setSelectionPath(nodePath);
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				revalidateHipecotechTopPanel();
				getPreviewTree().setEditable(false);
				TreePath selectedPath = getPreviewTree().getPathForLocation(50, e.getY());
				if (selectedPath == null && getPreviewTree().getFirstNodeSelected() != null) {
					getPreviewTree().setSelectionPath(new TreePath(getPreviewTree().getFirstNodeSelected().getPath()));
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				getPreviewTree().requestFocus();
				if (e.getClickCount() == 2) {
					getPreviewTree().setEditable(true);
					getPreviewTree().startEditingAtPath(getPreviewTree().getPathForLocation(e.getX(), e.getY()));
				}
			}
		});

		getSmartPlaylistTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					DefaultMutableTreeNode nodeRightClicked = getSmartPlaylistTree().getTreeHelper()
							.getDefaultMutableTreeNodeFromCoordinates(e.getPoint());
					TreePath nodePath = new TreePath(nodeRightClicked.getPath());
					if (!getSmartPlaylistTree().isPathSelected(nodePath)) {
						getSmartPlaylistTree().setSelectionPath(nodePath);
					}
				}
			}

			public void mousePressed(MouseEvent e) {
				revalidateHipecotechTopPanel();
				getSmartPlaylistTree().setEditable(false);
				TreePath selectedPath = getSmartPlaylistTree().getPathForLocation(50, e.getY());
				if (selectedPath == null && getSmartPlaylistTree().getLastSelectedPathComponent() != null) {
					getSmartPlaylistTree().setSelectionPath(new TreePath(getSmartPlaylistTree().getLastSelectedPathComponent()));
				}
			}
		});

		getPreviewTree().setEditListener(new PreviewTreeEditListener() {
			@Override
			public boolean edited(Object userObject, String newValue, DefaultMutableTreeNode editingNode) {
				try {
					if (userObject instanceof LocalPlaylist) {
						((LocalPlaylist) userObject).setName(newValue);
						// dao.update(userObject);
						viewEngine.send(Actions.Application.UPDATE_USER_OBJECT_EDITTED, new ValueAction<Object>(userObject));
					} else if (userObject instanceof LocalFolder) {
						((LocalFolder) userObject).setName(newValue);
						// dao.update(userObject);
						viewEngine.send(Actions.Application.UPDATE_USER_OBJECT_EDITTED, new ValueAction<Object>(userObject));
					}
					getPreviewTree().getTreeHelper().saveState();
					getPreviewTree().reorderFolderNode(editingNode);
					getPreviewTree().getTreeHelper().restoreState();
				} catch (IllegalArgumentException ex) {
					dialogFactory.showMessageDialog(ex.getMessage());
					return false;
				} catch (Exception ex) {
					log.error(ex, ex);
					return false;
				}
				return true;
			}
		});

	}

	@EventMethod(Events.Application.STARTED_ID)
	public void initService() {
		previewTree = getPreviewTree();
		previewTree.setEditable(true);
		PreviewTreeCellRenderer cellRenderer = (PreviewTreeCellRenderer) previewTree.getCellRenderer();
		PreviewTreeCellEditor cellEditor = new PreviewTreeCellEditor(previewTree, cellRenderer);
		previewTree.setCellEditor(cellEditor);

		Root root = viewEngine.get(Model.USER_ROOT);
		super.initialize(viewEngine, root);
	}
	
	@EventMethod(Events.View.SELECTED_PLAYING_TRACKCONTAINER_ID)
	public void handleSelectedPlayingTrackContainer(ValueEvent<TrackContainer> valueEvent){
		previewTree.getTreeHelper().selectByValue(valueEvent.getValue());
	}

	@PreDestroy
	public void shutdown() {
		scheduler.shutdownNow();
	}

	/**
	 * This method initializes treePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getContentPanel() {
		if (contentPanel == null) {
			GridBagConstraintsFactory factory = new GridBagConstraintsFactory();
			factory.grid(0, 0).fill(1, 0, FillMode.HORIZONTAL);
			GridBagConstraints smartPlaylistTreeConstraints = factory.get();
			factory.grid(0, 1).fill(1, 1, FillMode.BOTH);
			GridBagConstraints previewTreeConstraints = factory.get();
			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());
			contentPanel.add(getSmartPlaylistTree(), smartPlaylistTreeConstraints);
			contentPanel.add(getPreviewTree(), previewTreeConstraints);
		}
		return contentPanel;
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		super.setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		super.removeMessages(messages);
	}

	@Override
	public void internationalize(Messages messages) {
		getPlayMenuItem().setText(messages.getMessage("previewPanel.playmenu.label"));
		MenuItems.CREATE_PLAYLIST.internationalize(getCreatePlaylist(), messages);
		MenuItems.CREATE_FOLDER.internationalize(getCreateFolder(), messages);
		MenuItems.CREATE_SMART_PLAYLIST.internationalize(getCreateSmartPlaylist(), messages);
		MenuItems.RENAME.internationalize(getRenamePlaylistMenuItem(), messages);
		MenuItems.DELETE.internationalize(getDeleteItem(), messages);
		MenuItems.MOVEOUT_FROM_FOLDER.internationalize(getMoveOutFromFolderMenuItem(), messages);
	}

	private JPopupMenu getLibraryPopupMenu() {
		setMenuItemsEnabled(true);
		getDeleteItem().setEnabled(true);

		showDeleteFromFolderItem(false);
		JPopupMenu libraryPopupMenu = getGenericPopupMenu();

		libraryPopupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
			public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
				getDeleteItem().setVisible(true);
				getRenamePlaylistMenuItem().setVisible(true);
				getSeparatorAfterRename().setVisible(true);

				int size = 0;
				boolean folderSelected = false;
				List<?> userSelection = viewEngine.get(Model.CLIPBOARD_SELECTION);
				for (Iterator<?> iterator = userSelection.iterator(); iterator.hasNext();) {
					Object objectSelected = (Object) iterator.next();
					if (objectSelected instanceof Folder) {
						deleteItem.setEnabled(true);
						showDeleteFromFolderItem(false);
						folderSelected = true;
						size += userSelection.size();
					} else if (objectSelected instanceof Playlist) {
						deleteItem.setEnabled(true);
						TrackContainer container = viewEngine.get(Model.SELECTED_CONTAINER);
						boolean moveOutFromFolderVisible = false;
						if (container instanceof Playlist) {
							Playlist playlist = (Playlist) container;
							size += userSelection.size();
							if (!folderSelected) {
								moveOutFromFolderVisible = !(playlist.getParentFolder() == null);
							}
						}
						showDeleteFromFolderItem(moveOutFromFolderVisible);
					}
				}
				if (userSelection.size() > 1) {
					showDeleteFromFolderItem(false);
				}

				int selectionCount = (getPreviewTree().getSelectionRows() == null ? 0 : 1);

				setMenuItemsEnabled(selectionCount == 1 || !(size > 1 || size == 0));

			}

			public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
			}

			public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
			}
		});

		return libraryPopupMenu;
	}

	private void showDeleteFromFolderItem(boolean show) {
		getMoveOutFromFolderMenuItem().setVisible(show);
		getSeparatorAfterDeleteFromFolder().setVisible(show);
	}

	private void setMenuItemsEnabled(boolean isEnabled) {
		getPlayMenuItem().setEnabled(isEnabled);
		getRenamePlaylistMenuItem().setEnabled(isEnabled);
		getDeleteItem().setEnabled(isEnabled);
	}

	private JMenuItem getCreatePlaylist() {
		if (createPlaylistMenuItem == null) {
			createPlaylistMenuItem = MenuItems.CREATE_PLAYLIST.getItem();
		}
		return createPlaylistMenuItem;
	}

	private JMenuItem getCreateFolder() {
		if (createFolderMenuItem == null) {
			createFolderMenuItem = MenuItems.CREATE_FOLDER.getItem();
		}
		return createFolderMenuItem;
	}

	private JMenuItem getCreateSmartPlaylist() {
		if (createSmartPlaylistMenuItem == null) {
			createSmartPlaylistMenuItem = MenuItems.CREATE_SMART_PLAYLIST.getItem();
		}
		return createSmartPlaylistMenuItem;
	}

	private JMenuItem getRenamePlaylistMenuItem() {
		if (renamePlaylistMenuItem == null) {
			renamePlaylistMenuItem = MenuItems.RENAME.getItem();
		}
		return renamePlaylistMenuItem;
	}

	/**
	 * This method initializes playMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getPlayMenuItem() {
		if (playMenuItem == null) {
			playMenuItem = new JMenuItem();
		}
		return playMenuItem;
	}

	private void checkShowMenu(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
			getLibraryPopupMenu().show(getPreviewTree(), e.getX(), e.getY());
		}
	}

	private JMenuItem getDeleteItem() {
		if (deleteItem == null) {
			deleteItem = MenuItems.DELETE.getItem();
		}
		return deleteItem;
	}

	private JMenuItem getMoveOutFromFolderMenuItem() {
		if (moveOutFromFolderMenuItem == null) {
			moveOutFromFolderMenuItem = MenuItems.MOVEOUT_FROM_FOLDER.getItem();
		}
		return moveOutFromFolderMenuItem;
	}

	private JPopupMenu getGenericPopupMenu() {
		JPopupMenu popUpMenu = new JPopupMenu();
		popUpMenu.add(getPlayMenuItem());
		popUpMenu.add(getSeparatorAfterPlay());
		popUpMenu.add(getCreateFolder());
		popUpMenu.add(getCreatePlaylist());
		popUpMenu.add(getSeparatorAfterPlaylist());
		popUpMenu.add(getRenamePlaylistMenuItem());
		popUpMenu.add(getSeparatorAfterRename());
		popUpMenu.add(getMoveOutFromFolderMenuItem());
		popUpMenu.add(getSeparatorAfterDeleteFromFolder());
		popUpMenu.add(getDeleteItem());
		return popUpMenu;
	}

	private JPopupMenu.Separator getSeparatorAfterRename() {
		if (separatorAfterRename == null) {
			separatorAfterRename = new JPopupMenu.Separator();
		}
		return separatorAfterRename;
	}

	private JPopupMenu.Separator getSeparatorAfterDeleteFromFolder() {
		if (separatorAfterDeleteFromFolder == null) {
			separatorAfterDeleteFromFolder = new JPopupMenu.Separator();
		}
		return separatorAfterDeleteFromFolder;
	}

	private JPopupMenu.Separator getSeparatorAfterPlay() {
		if (separatorAfterPlay == null) {
			separatorAfterPlay = new JPopupMenu.Separator();
		}
		return separatorAfterPlay;
	}

	private JPopupMenu.Separator getSeparatorAfterPlaylist() {
		if (separatorAfterPlaylist == null) {
			separatorAfterPlaylist = new JPopupMenu.Separator();
		}
		return separatorAfterPlaylist;
	}

	// Bug 5042 bottom panel disappear when loading browser in MAC in the very
	// first time
	// Bug 5149 some components from HipecotechTopPanel needs to be repainted
	// because the same previous bug
	@Autowired
	private HipecotechTopPanel hipecotechTopPanel;
	@Autowired
	private BottomPanel bottomPanel;

	private void revalidateHipecotechTopPanel() {
		if (Environment.isMac()) {
			scheduler.schedule(new RepaintComponentsTask(), 1, TimeUnit.SECONDS);
		}
	}

	class RepaintComponentsTask implements Runnable {

		@Override
		public void run() {
			hipecotechTopPanel.invalidate();
			hipecotechTopPanel.validate();
			hipecotechTopPanel.repaint();

			bottomPanel.invalidate();
			bottomPanel.validate();
			bottomPanel.repaint();
		}
	}

	@EventMethod(Events.Library.RESTORE_SELECTION_ID)
	public void onGotoAllMusic(ValueEvent<Object> eventArgs) {
		if (eventArgs.getValue() != null && !(eventArgs.getValue() instanceof Root)) {
			getPreviewTree().getTreeHelper().selectByValue(eventArgs.getValue());
			getPreviewTree().getTreeHelper().saveState();
		} else {
			getSmartPlaylistTree().selectAllMusic();
		}
	}

	@EventMethod(Events.Library.PLAYLIST_CREATED_ID)
	public void onUserCreatedPlaylist(ValueEvent<Playlist> eventArgs) {
		getPreviewTree().addToModel(eventArgs.getValue());
	}

	@EventMethod(Events.Library.FOLDER_CREATED_ID)
	public void onUserCreatedFolder(ValueEvent<Folder> eventArgs) {
		getPreviewTree().addToModel(eventArgs.getValue());
	}

	@EventMethod(Events.Library.EDIT_CURRENT_SELECTION_ID)
	public void onUserRequestedEdit() {
		getPreviewTree().startEditingAtPath(getPreviewTree().getSelectionPath());
	}

	@EventMethod(value = Model.CURRENT_VIEW_ID, eager = true)
	@Override
	public void onModelViewCurrentViewChanged(ValueEvent<ContainerView> event) {
		super.onModelViewCurrentViewChanged(event);
	}

	@EventMethod(Events.Library.TREE_STRUCTURE_CHANGED_ID)
	public void onTreeStructureChanged(ValueEvent<Root> eventArgs) {
		super.onTreeStructureChanged(eventArgs);
	}

	@EventMethod(Events.Library.FOLDER_UPDATED_ID)
	public void onFolderUpdated(ValueEvent<Folder> eventArgs) {
		super.onFolderUpdated(eventArgs);
	}

	@EventMethod(Events.View.SELECTED_TRACKCONTAINER_CHANGED_ID)
	public void onSelectedContainerChanged(SelectTrackContainerEvent eventArgs) {
		super.onSelectedContainerChanged(eventArgs);
	}

	@EventMethod(Events.Library.NEW_CONTENT_AVAILABLE_ID)
	public void onNewContentAvailable() {
		super.onNewContentAvailable();
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
	}

	@EventMethod(Events.Downloads.COMPLETED_ID)
	public void clearSmartPlaylist(ValueEvent<Download> eventArgs) {
		// this code was moved from the com.all.client.Initializer class
		for (SmartPlaylist sp : viewEngine.get(Model.USER_ROOT).getSmartPlaylists()) {
			if (sp instanceof LocalDefaultSmartPlaylistImpl) {
				LocalDefaultSmartPlaylistImpl dsp = (LocalDefaultSmartPlaylistImpl) sp;
				dsp.reset();
			}
		}
		if (viewEngine.get(Model.USER_ROOT).getAllMusicSmartPlaylist() instanceof LocalDefaultSmartPlaylistImpl) {
			LocalDefaultSmartPlaylistImpl dsp = (LocalDefaultSmartPlaylistImpl) viewEngine.get(Model.USER_ROOT)
					.getAllMusicSmartPlaylist();
			dsp.reset();
		}
	}

}
