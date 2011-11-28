package com.all.client.view.contacts;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.all.chat.ChatStatus;
import com.all.client.model.ContactTransferable;
import com.all.client.view.components.ContactsTreeCellEditor;
import com.all.client.view.components.ContactsTreeCellRenderer;
import com.all.client.view.listeners.SingleExpandTree;
import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.core.common.view.SynthColors;
import com.all.core.model.ContactCollection;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;
import com.all.shared.model.ContactInfo;

@org.springframework.stereotype.Component
public class ContactTree extends JTree implements Internationalizable {
	private static final String CONTACT_LIST_TREE_NAME = "contactListTree";

	private final static Log LOG = LogFactory.getLog(ContactTree.class);

	private static final long serialVersionUID = 5032389057639122420L;
	private static final int HIGHTLIGHT_WIDTH = 30;
	static final int NON_LEAF_WIDTH = 165;
	static final int NODE_HEIGHT = 44;
	static final int SEPARATOR_HEIGHT = 29;
	private JTreeCoordinateHelper treeHelper;

	private Integer dragOverObject;

	private FolderWrapper onlineFolder;
	private FolderWrapper offlineFolder;
	private FolderWrapper pendingFolder;

	private Observable<ObserveObject> mouseEvent = new Observable<ObserveObject>();

	@Autowired
	private Messages messages;

	private String onlineTitle;
	private String offlineTitle;
	private String pendingTitle;
	private DefaultMutableTreeNode online;
	private DefaultMutableTreeNode offline;
	private DefaultMutableTreeNode pending;
	boolean testing;
	

	public ContactTree() {
		onlineTitle = "";
		offlineTitle = "";
		pendingTitle = "";

		onlineFolder = new FolderWrapper(1, onlineTitle);
		offlineFolder = new FolderWrapper(2, offlineTitle);
		pendingFolder = new FolderWrapper(3, pendingTitle);

		treeHelper = new JTreeCoordinateHelper(this);

		ContactsTreeCellRenderer treeCellRenderer = new ContactsTreeCellRenderer();

		this.setName(CONTACT_LIST_TREE_NAME);
		this.setRootVisible(false);
		this.setEditable(true);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		this.setCellRenderer(treeCellRenderer);
		this.setRowHeight(NODE_HEIGHT);
		this.setEditable(false);
		this.addMouseMotionListener(new MouseAdapter() {
			private DefaultMutableTreeNode lastNode = null;

			@Override
			public void mouseMoved(MouseEvent e) {
				DefaultMutableTreeNode node = treeHelper.getDefaultMutableTreeNodeFromCoordinates(e.getX(), e.getY());
				if (node != lastNode) {
					String tooltip = null;
					if (node != null && node.getUserObject() instanceof ContactInfo) {
						tooltip = ((ContactInfo) node.getUserObject()).getTooltipText() ;
					}
					setToolTipText(tooltip);
					lastNode = node;

				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					DefaultMutableTreeNode nodeRightClicked = getTreeHelper().getDefaultMutableTreeNodeFromCoordinates(
							e.getPoint());
					TreePath nodePath = new TreePath(nodeRightClicked.getPath());
					if (!isPathSelected(nodePath)) {
						setSelectionPath(nodePath);
					}
				}
			}
		});

		this.setDragEnabled(true);
		this.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1440327048767545533L;

			@Override
			public Transferable createTransferable(JComponent c) {
				java.util.List<ContactInfo> selection = new ArrayList<ContactInfo>();
				for (TreePath path : getSelectionPaths()) {
					Object model = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					if (model instanceof ContactInfo) {
						ContactInfo contact = (ContactInfo) model;
						selection.add(contact);
					}
				}
				if (selection.isEmpty()) {
					return null;
				} else {
					ContactCollection contacts = new ContactCollection(selection);
					contacts.cleanUp();
					return new ContactTransferable(contacts);
				}
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return false;
			}

			@Override
			public void exportDone(JComponent source, Transferable data, int action) {
			}

			@Override
			public boolean importData(TransferSupport support) {
				return true;
			}

			@Override
			public int getSourceActions(JComponent c) {
				return COPY;
			}
		});
		// Remove default behavior added by the shit up there
		this.setDropTarget(null);
		this.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("")));
		SingleExpandTree.apply(this);
	}

	public ObserverCollection<ObserveObject> onMouseEvent() {
		return mouseEvent;
	}

	public void setModel(List<ContactInfo> contacts, boolean showPendingEmails, boolean expandNodes) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		online = new DefaultMutableTreeNode(onlineFolder);
		offline = new DefaultMutableTreeNode(offlineFolder);
		pending = new DefaultMutableTreeNode(pendingFolder);
		List<ContactInfo> contactsActive = new ArrayList<ContactInfo>();
		List<ContactInfo> contactsPending = new ArrayList<ContactInfo>();

		for (ContactInfo contact : contacts) {
			if (!contact.isPending()) {
				contactsActive.add(contact);
			} else {
				contactsPending.add(contact);
			}
		}

		Collections.sort(contactsActive);
		Collections.sort(contactsPending);
		// this a list to add the away at the end of the online node
		List<DefaultMutableTreeNode> awayContacts = new ArrayList<DefaultMutableTreeNode>();
		for (ContactInfo contact : contactsActive) {
			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(contact);
			switch (contact.getChatStatus()) {
			case OFFLINE:
				offline.add(newChild);
				break;
			case AWAY:
				awayContacts.add(newChild);
			case ONLINE:
				online.add(newChild);
				break;
			}
		}

		for (DefaultMutableTreeNode contactInfo : awayContacts) {
			online.add(contactInfo);
		}

		for (ContactInfo contact : contactsPending) {
			DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(contact);
			pending.add(newChild);
		}

		root.add(online);
		root.add(offline);
		if (showPendingEmails) {
			root.add(pending);
		}
		this.setModel(new DefaultTreeModel(root));
		if (expandNodes) {
			for (int row = 0; row < getRowCount(); row++) {
				expandRow(row);
			}
		}
		this.invalidate();
		if (!testing) {
			if (SwingUtilities.getWindowAncestor(this) != null) {
				SwingUtilities.getWindowAncestor(this).validate();
			}
			setMessages(messages);
		}

	}

	public boolean isEditablePathForLocation(Object o) {
		if (o instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;
			if (node.getUserObject() instanceof ContactInfo) {
				ContactInfo contact = (ContactInfo) node.getUserObject();
				if (!contact.getChatStatus().equals(ChatStatus.PENDING)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Integer highlightedRow = null;

		if (dragOverObject != null) {
			highlightedRow = dragOverObject / NODE_HEIGHT;
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Paint general background
		g2.setColor(SynthColors.CLEAR_GRAY243_243_247);
		g2.fillRect(0, 0, getWidth(), getHeight());
		// Paint Online Background
		int onlineHeight = 0;
		if (isExpanded(0)) {
			TreeNode onlineNode = (TreeNode) getPathForRow(0).getLastPathComponent();
			onlineHeight = (onlineNode.getChildCount() + 1) * NODE_HEIGHT;
		}
		if (onlineHeight != 0) {
			g2.setColor(SynthColors.GRAY229_229_235);
			g2.fillRect(0, 0, getWidth(), onlineHeight);
		}
		// Paint selected backgrounds
		int[] selectionRows = getSelectionRows();
		if (selectionRows != null) {
			for (int i : selectionRows) {
				if (((TreeNode) getPathForRow(i).getLastPathComponent()).isLeaf()) {
					int y = i * NODE_HEIGHT;
					Object userObject = ((DefaultMutableTreeNode) getPathForRow(i).getLastPathComponent())
							.getUserObject();
					if (userObject instanceof ContactInfo) {
						ContactInfo contact = (ContactInfo) userObject;
						if (contact.getChatStatus().equals(ChatStatus.OFFLINE)) {
							g2.setColor(SynthColors.GRAY190_190_190);
						} else {
							g2.setColor(SynthColors.PURPLE149_128_174);
						}
						g2.fillRect(0, y, getWidth(), 1);
						g2.fillRect(0, y + NODE_HEIGHT - 1, getWidth(), 1);
					}

					if (onlineHeight >= y) {
						g2.setColor(SynthColors.BLUE_201_202_230);
					} else {
						g2.setColor(SynthColors.CLEAR_GRAY220_220_220);
					}
					if (userObject instanceof ContactInfo) {
						g2.fillRect(0, y + 1, getWidth(), NODE_HEIGHT - 2);
					} else {
						g2.fillRect(0, y, getWidth(), NODE_HEIGHT);
					}
				}
			}
		}

		if (highlightedRow != null) {
			int y = highlightedRow * NODE_HEIGHT;
			if (isEditing() && getPathForLocation(30, y).equals(getEditingPath())) {
				TreePath editingPath = getEditingPath();
				clearSelection();
				setSelectionPath(editingPath);
			}

			g2.setColor(SynthColors.PURPLE193_171_218);
			g2.fillRoundRect(0, y, getWidth() - 1, NODE_HEIGHT, HIGHTLIGHT_WIDTH, HIGHTLIGHT_WIDTH);
			g2.setColor(SynthColors.WHITE255_255_255);
			g2.setStroke(new BasicStroke(1));
			g2.drawRoundRect(0, y, getWidth() - 1, NODE_HEIGHT, HIGHTLIGHT_WIDTH, HIGHTLIGHT_WIDTH);
		}

		setOpaque(false);
		super.paintComponent(g2);
		setOpaque(true);
	}

	public JTreeCoordinateHelper getTreeHelper() {
		return treeHelper;
	}

	public void setDragOverObject(Integer b) {
		dragOverObject = b;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// Force the table to fill the viewport's width
		final Component parent = getParent();
		if (!(parent instanceof JViewport)) {
			return false;
		}
		return true;
	}

	@Override
	public void internationalize(Messages messages) {
		onlineTitle = messages.getMessage("contactTree.Online.Title");
		offlineTitle = messages.getMessage("contactTree.Offline.Title");
		pendingTitle = messages.getMessage("contactTree.FutureContacts.Title");

		onlineFolder.setName(onlineTitle + " (" + online.getChildCount() + ")");
		offlineFolder.setName(offlineTitle + " (" + offline.getChildCount() + ")");
		pendingFolder.setName(pendingTitle + " (" + pending.getChildCount() + ")");

		online.setUserObject(onlineFolder);
		offline.setUserObject(offlineFolder);
		pending.setUserObject(pendingFolder);
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.nodeChanged(online);
		model.nodeChanged(offline);
		model.nodeChanged(pending);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public DefaultMutableTreeNode getPending() {
		return pending;
	}

	public DefaultMutableTreeNode getOffline() {
		return offline;
	}

	public void setNodesTitles() {
		onlineFolder.setName(onlineTitle + " (" + online.getChildCount() + ")");
		offlineFolder.setName(offlineTitle + " (" + offline.getChildCount() + ")");
		pendingFolder.setName(pendingTitle + " (" + pending.getChildCount() + ")");
		((DefaultTreeModel) getModel()).nodeChanged(online);
		((DefaultTreeModel) getModel()).nodeChanged(offline);
		((DefaultTreeModel) getModel()).nodeChanged(pending);
	}

	@Autowired
	public void setContactsCellEditor(ContactsTreeCellEditor contactsTreeCellEditor) {
		this.setCellEditor(contactsTreeCellEditor);
	}

	public void updateNode(ContactInfo contact) {
		DefaultMutableTreeNode node = findNode(contact);
        LOG.info("UPDATING CONTACT " + contact);
		if (node != null) {
			node.setUserObject(contact);
			DefaultMutableTreeNode parentNode = getParentNodeForStatus(contact.getChatStatus());
			DefaultTreeModel model = (DefaultTreeModel) getModel();
			if (parentNode.equals(node.getParent()) && !hasStatusChanged(node)) {
				LOG.info("IF NOT PARENT CHANGE AND STATUS NOT CHANGED JUST NOTIFIED CHANGES IN MODEL");
				model.nodeChanged(node);
			} else {
				LOG.info("REMOVING FROM PARENT AND UPDATING");
				TreePath editingPath = getEditingPath();
				stopEditing();
				model.removeNodeFromParent(node);
				model.insertNodeInto(node, parentNode, findIndex(contact, parentNode));
				setNodesTitles();
				expandRow(0);
				try {
					startEditingAtPath(editingPath);
				} catch (Exception e) {
					// this catch block if for the case when the removed node and inserted
					// is the same as the editing path
					LOG.error(e, e);
				}
			}
		}
	}

	public void addNode(ContactInfo contact) {
		DefaultMutableTreeNode parentNode = getParentNodeForStatus(contact.getChatStatus());
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(contact);
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.insertNodeInto(node, parentNode, findIndex(contact, parentNode));
		setNodesTitles();
		expandPendingNodeIfFirstElementHasBeenAdded(parentNode);
	}

	private void expandPendingNodeIfFirstElementHasBeenAdded(DefaultMutableTreeNode parentNode) {
		if (parentNode.getUserObject().equals(pendingFolder) && parentNode.getChildCount() == 1) {
			expandPath(treeHelper.getPathForValue(parentNode.getUserObject()));
		}
	}

	public void deleteNode(ContactInfo contact) {
		DefaultMutableTreeNode node = findNode(contact);
		DefaultTreeModel model = (DefaultTreeModel) getModel();
		model.removeNodeFromParent(node);
		setNodesTitles();
	}

	// THIS METHOD SUCKS BUT CURRENT EVENT CANNOT GIVE US THE PREVIOUS STATUS.
	private boolean hasStatusChanged(DefaultMutableTreeNode node) {
		ContactInfo contact = (ContactInfo) node.getUserObject();
		ChatStatus chatStatus = contact.getChatStatus();
		DefaultMutableTreeNode previous = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node.getParent())
				.getChildBefore(node);
		ChatStatus previousStatus = null;
		ContactInfo previousContact = null;
		if (previous != null) {
			previousContact = (ContactInfo) previous.getUserObject();
			previousStatus = previousContact.getChatStatus();
		}
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node.getParent())
				.getChildAfter(node);
		ContactInfo nextContact = null;
		ChatStatus nextStatus = null;
		if (next != null) {
			nextContact = (ContactInfo) next.getUserObject();
			nextStatus = nextContact.getChatStatus();
		}
		if (chatStatus == ChatStatus.AWAY && nextStatus == ChatStatus.ONLINE) {
			return true;
		}
		if (chatStatus == ChatStatus.ONLINE && previousStatus == ChatStatus.AWAY) {
			return true;
		}
		if (contact.compareTo(nextContact) > 0) {
			return true;
		}
		if (contact.compareTo(previousContact) < 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private int findIndex(ContactInfo contact, DefaultMutableTreeNode parentNode) {
		Enumeration children = parentNode.children();
		int index = 0;
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode leaf = (DefaultMutableTreeNode) children.nextElement();
			ContactInfo leafValue = (ContactInfo) leaf.getUserObject();
			if (contact.compareTo(leafValue) <= 0) {
				if (!(leafValue.getChatStatus() == ChatStatus.ONLINE && contact.getChatStatus() == ChatStatus.AWAY)) {
					break;
				}
			} else if (contact.getChatStatus() == ChatStatus.ONLINE
					&& !(leafValue.getChatStatus() == ChatStatus.ONLINE)) {
				break;
			}
			index++;
		}
		return index;
	}

	private DefaultMutableTreeNode getParentNodeForStatus(ChatStatus chatStatus) {
		DefaultMutableTreeNode parentNode = null;
		switch (chatStatus) {
		case ONLINE:
			parentNode = online;
			break;
		case AWAY:
			parentNode = online;
			break;
		case OFFLINE:
			parentNode = offline;
			break;
		case PENDING:
			parentNode = pending;
			break;
		}
		return parentNode;
	}

	private DefaultMutableTreeNode findNode(ContactInfo contact) {
		Collection<DefaultMutableTreeNode> allNodes = getAllChildren((DefaultMutableTreeNode) getModel().getRoot());
		for (DefaultMutableTreeNode node : allNodes) {
			if (node.getUserObject().equals(contact)) {
				return node;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Collection<DefaultMutableTreeNode> getAllChildren(DefaultMutableTreeNode startNode) {
		List<DefaultMutableTreeNode> branch = new LinkedList<DefaultMutableTreeNode>();
		branch.add(startNode);
		Enumeration children = startNode.children();
		while (children.hasMoreElements()) {
			branch.addAll(getAllChildren((DefaultMutableTreeNode) children.nextElement()));
		}
		return branch;
	}

}

class FolderWrapper {
	int id;
	String name;

	public FolderWrapper(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FolderWrapper) {
			return id == ((FolderWrapper) obj).id;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id;
	}

}
