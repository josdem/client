package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.PreviewTree;
import com.all.client.view.components.SmartPlaylistTree;
import com.all.client.view.model.PreviewTreeModel;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.events.SelectTrackContainerEvent;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;
import com.all.shared.model.TrackContainer;

public abstract class PreviewPanel extends SimplePanel {
	private static final int SCROLL_BUTTON_INCREMENT = 36;
	private static final Dimension JSCROLLPANE_DEFAULT_SIZE = new Dimension(197, 165);
	private static final Dimension JSCROLLPANE_MAXIMUM_SIZE = new Dimension(197, Integer.MAX_VALUE);
	private static final Dimension JSCROLLPANE_MINIMUM_SIZE = new Dimension(197, 16);
	public static final int INNER_WIDTH = 198;
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane = null;

	private SmartPlaylistTree smartPlaylistTree;

	private PreviewTree previewTree;
	private TreeSelectionListener treeSelectionListener;
	protected final ViewEngine viewEngine;

	private Root root;

	private Folder lastFolderUpdated = null;
	private boolean suspendActions;

	public PreviewPanel(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;

		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), BorderLayout.CENTER);

		createListeners();

		wireViewListeners();
	}

	private void createListeners() {
		treeSelectionListener = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				if (suspendActions) {
					return;
				}
				JTree tree = (JTree) event.getSource();
				List<Object> selectedNodes = new ArrayList<Object>();
				TrackContainer selection = null;
				if (tree.getSelectionPaths() != null) {
					for (TreePath selectedPath : tree.getSelectionPaths()) {
						Object userObject = ((DefaultMutableTreeNode) selectedPath.getLastPathComponent()).getUserObject();
						if (!(userObject instanceof String)) {
							selectedNodes.add(userObject);
						}
					}
				}
				if (event.getNewLeadSelectionPath() != null && event.getNewLeadSelectionPath().getLastPathComponent() != null) {
					Object userObject = ((DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent())
							.getUserObject();
					if (userObject instanceof TrackContainer) {
						selection = (TrackContainer) userObject;
					}
				}
				if (selection != null) {
					viewEngine.send(Actions.View.SELECT_TRACKCONTAINER, new SelectTrackContainerAction(root, selection));
					if (!selectedNodes.isEmpty()) {
						viewEngine.sendValueAction(Actions.View.SET_CLIPBOARD_SELECTION, selectedNodes);
					}
				}
			}
		};

	}

	protected abstract void onRootSet(Root root);

	protected void setRoot(Root root) {
		this.root = root;
		reloadSmartPlaylists();
		reloadData();
		onRootSet(root);
	}

	private void wireViewListeners() {
		getPreviewTree().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (getPreviewTree().getSelectionCount() > 0) {
					getSmartPlaylistTree().clearSelection();
				}
			}
		});

		getSmartPlaylistTree().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (getSmartPlaylistTree().getSelectionCount() > 0) {
					Object selection = null;
					if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getLastPathComponent() != null) {
						selection = ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject();
					}
					if (selection != null && !(selection instanceof String)) {
						getPreviewTree().clearSelection();
					}
				}
			}
		});

		getPreviewTree().addTreeSelectionListener(treeSelectionListener);
		getSmartPlaylistTree().addTreeSelectionListener(treeSelectionListener);
		getPreviewTree().addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				if (!previewTree.getTreeHelper().isRestoring()) {
					Sound.LIBRARY_OPEN_NODE.play();
				}
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				if (!previewTree.getTreeHelper().isRestoring()) {
					Sound.LIBRARY_CLOSE_NODE.play();
				}
			}
		});

		MouseListener viewResetMouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!suspendActions) {
					viewEngine.sendValueAction(Actions.View.SELECT_ROOT, root);
					viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)));
				}
			}
		};

		getPreviewTree().addMouseListener(viewResetMouseListener);
		getSmartPlaylistTree().addMouseListener(viewResetMouseListener);
	}

	public void onModelViewCurrentViewChanged(ValueEvent<ContainerView> eventArgs) {
		if (eventArgs.getValue().getViews() == Views.LOCAL_MUSIC) {
			checkTrees();
		} else {
			getPreviewTree().clearSelection();
			getSmartPlaylistTree().clearSelection();
		}
	}

	public void initialize(final ViewEngine viewEngine, Root root) {
		setRoot(root);
		checkTrees();
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	final void reloadData() {
		getPreviewTree().removeTreeSelectionListener(treeSelectionListener);
		getPreviewTree().getTreeHelper().saveState();
		TreeModel treeModel = PreviewTreeModel.convertToTreeModel(root);
		getPreviewTree().setModel(treeModel);
		getPreviewTree().getTreeHelper().restoreState();
		getPreviewTree().addTreeSelectionListener(treeSelectionListener);
	}

	final void reloadSmartPlaylists() {
		getSmartPlaylistTree().removeTreeSelectionListener(treeSelectionListener);
		getSmartPlaylistTree().getTreeHelper().saveState();
		getSmartPlaylistTree().setSmartPlaylists(root.getSmartPlaylists(), root.getAllMusicSmartPlaylist());
		getSmartPlaylistTree().getTreeHelper().restoreState();
		getSmartPlaylistTree().addTreeSelectionListener(treeSelectionListener);
	}

	protected abstract JPanel getContentPanel();

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getSmartPlaylistTree().setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getSmartPlaylistTree().removeMessages(messages);
	}

	private void checkTrees() {
		TrackContainer value = viewEngine.get(Model.SELECTED_CONTAINER);
		Root root = viewEngine.get(Model.SELECTED_ROOT);
		suspendActions = true;
		if (root.equals(this.root)) {
			searchAndDestroy(getSmartPlaylistTree(), (DefaultMutableTreeNode) getSmartPlaylistTree().getModel().getRoot(),
					value);
			searchAndDestroy(getPreviewTree(), (DefaultMutableTreeNode) getPreviewTree().getModel().getRoot(), value);
		} else {
			getSmartPlaylistTree().clearSelection();
			getPreviewTree().clearSelection();
		}
		suspendActions = false;
	}

	private boolean searchAndDestroy(JTree tree, DefaultMutableTreeNode node, TrackContainer value) {
		if (node == null || node.getUserObject() == null || value == null) {
			return false;
		}
		Object nodeValue = node.getUserObject();
		if (nodeValue == value || value.equals(nodeValue)) {
			TreePath path = new TreePath(node.getPath());
			if (!tree.isPathSelected(path)) {
				tree.setSelectionPath(path);
			}
			return true;
		}
		Enumeration<?> children = node.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode nextNode = (DefaultMutableTreeNode) children.nextElement();
			if (searchAndDestroy(tree, nextNode, value)) {
				return true;
			}
		}
		return false;
	}

	protected JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getContentPanel());
			jScrollPane.setPreferredSize(JSCROLLPANE_DEFAULT_SIZE);
			jScrollPane.setMaximumSize(JSCROLLPANE_MAXIMUM_SIZE);
			jScrollPane.setSize(JSCROLLPANE_DEFAULT_SIZE);
			jScrollPane.setMinimumSize(JSCROLLPANE_MINIMUM_SIZE);
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane.getVerticalScrollBar().setUnitIncrement(SCROLL_BUTTON_INCREMENT);
		}
		return jScrollPane;
	}

	final SmartPlaylistTree getSmartPlaylistTree() {
		if (smartPlaylistTree == null) {
			smartPlaylistTree = new SmartPlaylistTree();
		}
		return smartPlaylistTree;
	}

	public PreviewTree getPreviewTree() {
		if (previewTree == null) {
			previewTree = new PreviewTree();
		}
		return previewTree;
	}

	public void onTreeStructureChanged(ValueEvent<Root> eventArgs) {
		if (eventArgs.getValue() != root) {
			return;
		}
		reloadData();
		if (lastFolderUpdated != null) {
			forceFolderExpand(lastFolderUpdated);
		}
	}

	public void onFolderUpdated(ValueEvent<Folder> eventArgs) {
		lastFolderUpdated = eventArgs.getValue();
	}

	protected void forceFolderExpand(Folder folder) {
		TreePath pathForValue = getPreviewTree().getTreeHelper().getPathForValue(folder);

		if (pathForValue != null) {
			getPreviewTree().expandPath(pathForValue);
		}
		List<?> list = viewEngine.get(Model.CLIPBOARD_SELECTION);
		if (!list.isEmpty()) {
			getPreviewTree().clearSelection();
			for (Object userSelection : list) {
				TreePath pathForValue2 = getPreviewTree().getTreeHelper().getPathForValue(userSelection);
				getPreviewTree().addSelectionPath(pathForValue2);
			}
		}
		lastFolderUpdated = null;
	}

	public void onSelectedContainerChanged(SelectTrackContainerEvent eventArgs) {
		checkTrees();
	}

	public void onNewContentAvailable() {
		reloadData();
	}

	public ModelCollection getSelection() {
		ModelCollection collection = new ModelCollection();
		addToCollection(collection, getPreviewTree().getSelectionPaths());
		addToCollection(collection, getSmartPlaylistTree().getSelectionPaths());
		return collection;
	}

	private void addToCollection(ModelCollection collection, TreePath[] selectionPaths) {
		if (selectionPaths != null && selectionPaths.length > 0) {
			for (TreePath treePath : selectionPaths) {
				collection.add(((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject());
			}
		}
	}

}
