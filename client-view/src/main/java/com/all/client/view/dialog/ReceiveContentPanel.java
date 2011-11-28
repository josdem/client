package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.all.client.model.ModelTransfereable;
import com.all.client.util.TrackRepository;
import com.all.client.view.dnd.DragDataFromTree;
import com.all.client.view.dnd.ModelDragRemoveListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.common.view.SynthColors;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public final class ReceiveContentPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Border CONTENT_TREE_BORDER = BorderFactory.createEmptyBorder(0, 15, 0, 0);

	private static final Dimension SIZE = new Dimension(360, 390);

	private static final Rectangle ACCEPT_ALL_BUTTON_BOUNDS = new Rectangle(179, 332, 80, 22);

	private static final Rectangle CONTAINER_PANEL_BOUNDS = new Rectangle(7, 63, 335, 216);

	private static final Rectangle CONTENT_SCROLL_BOUNDS = new Rectangle(6, 24, 324, 180);

	private static final Rectangle JPANEL_BOUNDS = new Rectangle(6, 6, 324, 18);

	private static final Rectangle INSTRUCTIONS_DETAIL_LABEL_BOUNDS = new Rectangle(73, 25, 260, 30);

	private static final Rectangle INSTRUCTIONS_TOP_LABEL_BOUNDS = new Rectangle(73, 10, 260, 15);

	private static final Rectangle JPANEL1_BOUNDS = new Rectangle(6, 194, 324, 15);

	private static final Rectangle MAIL_ICON_BOUNDS = new Rectangle(19, 13, 40, 40);

	private static final Rectangle REJECT_BUTTON_BOUNDS = new Rectangle(89, 332, 80, 22);

	private static final Rectangle SEPARATOR_BOUNDS = new Rectangle(6, 323, 337, 2);

	private static final Rectangle CONTENT_COUNT_LABEL_BOUNDS = new Rectangle(24, 289, 312, 20);

	private static final String ACCEPT_ALL_BUTTON_NAME = "buttonAcceptAll";

	private static final String CONTAINER_PANEL_NAME = "grayRoundedBorderPanel";

	private static final String CONTENT_TREE_NAME = "previewTree";

	private static final String JPANEL_NAME = "myMusicPanel";

	private static final String JPANEL1_NAME = "myMusicBottomLibPanel";

	private static final String LABEL = "JLabel";

	private static final String MAIL_ICON_NAME = "receiveContent.mailIcon";

	private static final String REJECT_BUTTON_NAME = "buttonReject";

	private static final String SEPARATOR_NAME = "bottomPanelSeparator";

	private int totalTracks = 0;

	private JButton acceptAllButton = null;

	private JButton rejectButton = null;

	private JLabel contentCountLabel = null;

	private JLabel contentTitleLabel = null;

	private JLabel instructionsDetailLabel = null;

	private JLabel instructionsTopLabel = null;

	private JPanel containerPanel = null;

	private JPanel jPanel = null;

	private JPanel jPanel1 = null;

	private JPanel mailIcon = null;

	private JPanel separator = null;

	private JScrollPane contentScroll = null;

	private JTree contentTree = null;

	private Map<Object, Integer> tracksInside = new HashMap<Object, Integer>();

	private final Messages messages;

	private final ModelCollection model;

	private final TrackRepository trackRepository;

	public ReceiveContentPanel(ModelCollection model, Messages messages, TrackRepository trackRepository) {
		this.model = model;
		this.messages = messages;
		this.trackRepository = trackRepository;
		initialize();
		setUpModel(model);
		setup();
	}

	private void setup() {
		getContentTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				int value = 0;
				TreePath[] selectionPaths = contentTree.getSelectionPaths();
				if (selectionPaths != null) {
					for (TreePath path : selectionPaths) {
						if (!isParentSelected(path.getParentPath())) {
							Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
							Integer current = tracksInside.get(userObject);
							if (current != null) {
								value += current;
							} else {
								value++;
							}
						}
					}
				}
				String message = messages.getMessage("receiveContent.countTracks", "" + value, "" + totalTracks);
				contentCountLabel.setText(message);
			}

			private boolean isParentSelected(TreePath parentPath) {
				if (parentPath == null) {
					return false;
				}
				if (contentTree.isPathSelected(parentPath)) {
					return true;
				} else {
					return isParentSelected(parentPath.getParentPath());
				}
			}
		});
		getContentTree().setDragEnabled(true);
		getContentTree().setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1L;
			private DragDataFromTree draggedData = null;

			@Override
			public Transferable createTransferable(JComponent c) {
				draggedData = null;
				java.util.List<Object> selection = new ArrayList<Object>();
				int[] selectionRows = getContentTree().getSelectionRows();
				if (selectionRows == null || selectionRows.length == 0) {
					return null;
				}
				ModelDragRemoveListener dragRemoveListener = new ModelDragRemoveListener(model);
				draggedData = new DragDataFromTree(dragRemoveListener);
				for (int row : selectionRows) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) getContentTree().getPathForRow(row)
							.getLastPathComponent();
					selection.add(node.getUserObject());
					draggedData.add(node);
				}
				ModelCollection modelCollection = new ModelCollection(selection);
				modelCollection.cleanUp();
				modelCollection.setRemote(true);
				ModelTransfereable modelTransfereable = new ModelTransfereable(ModelSource.remote(), modelCollection,
						trackRepository);
				return modelTransfereable;
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return false;
			}

			@Override
			public void exportDone(JComponent source, Transferable data, int action) {
				if (MultiLayerDropTargetListener.isLastDropSuccess() && draggedData != null) {
					draggedData.remove((DefaultTreeModel) getContentTree().getModel());
					getContentTree().invalidate();
					SwingUtilities.getWindowAncestor(getContentTree()).validate();
				}
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
		getContentTree().setDropTarget(null);
	}

	private void setUpModel(ModelCollection model) {
		DefaultMutableTreeNode root = new ModelMutableTreeNode("Root");
		for (Folder folder : model.getFolders()) {
			int folderTrack = 0;
			DefaultMutableTreeNode folderNode = new ModelMutableTreeNode(folder);
			for (Playlist playlist : folder.getPlaylists()) {
				DefaultMutableTreeNode playlistNode = new ModelMutableTreeNode(playlist);
				int playlistTrack = 0;
				for (Track track : playlist.getTracks()) {
					DefaultMutableTreeNode trackNode = new ModelMutableTreeNode(track);
					playlistNode.add(trackNode);
					totalTracks++;
					playlistTrack++;
					folderTrack++;
				}
				folderNode.add(playlistNode);
				tracksInside.put(playlist, playlistTrack);
			}
			tracksInside.put(folder, folderTrack);
			root.add(folderNode);
		}
		for (Playlist playlist : model.getPlaylists()) {
			DefaultMutableTreeNode playlistNode = new ModelMutableTreeNode(playlist);
			int playlistTrack = 0;
			for (Track track : playlist.getTracks()) {
				DefaultMutableTreeNode trackNode = new ModelMutableTreeNode(track);
				playlistNode.add(trackNode);
				totalTracks++;
				playlistTrack++;
			}
			tracksInside.put(playlist, playlistTrack);
			root.add(playlistNode);
		}
		for (Track track : model.getTracks()) {
			DefaultMutableTreeNode trackNode = new ModelMutableTreeNode(track);
			root.add(trackNode);
			totalTracks++;
		}
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		contentTree.setModel(treeModel);
		String message = messages.getMessage("receiveContent.countTracks", "" + totalTracks, "" + totalTracks);
		contentCountLabel.setText(message);
	}

	@Override
	public void internationalize(Messages messages) {
		rejectButton.setText(messages.getMessage("receiveContent.rejectButton"));
		acceptAllButton.setText(messages.getMessage("receiveContent.acceptButton"));
		instructionsTopLabel.setText(messages.getMessage("receiveContent.instructionsTop"));
		instructionsDetailLabel.setText(messages.getMessage("receiveContent.instructionsDetail"));
		contentTitleLabel.setText(messages.getMessage("receiveContent.contentTitle"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	private void initialize() {
		this.setLayout(null);
		this.setSize(SIZE);
		this.add(getMailIcon());
		this.add(getInstructionsTopLabel());
		this.add(getInstructionsDetailLabel());
		this.add(getContainerPanel());
		this.add(getContentCountLabel());
		this.add(getSeparator());
		this.add(getRejectButton());
		this.add(getAcceptAllButton());
	}

	private JLabel getInstructionsTopLabel() {
		if (instructionsTopLabel == null) {
			instructionsTopLabel = new JLabel();
			instructionsTopLabel.setText(LABEL);
			instructionsTopLabel.setBounds(INSTRUCTIONS_TOP_LABEL_BOUNDS);
			instructionsTopLabel.setName(SynthFonts.BOLD_FONT12_BLACK);
		}
		return instructionsTopLabel;
	}

	private JLabel getInstructionsDetailLabel() {
		if (instructionsDetailLabel == null) {
			instructionsDetailLabel = new JLabel();
			instructionsDetailLabel.setText(LABEL);
			instructionsDetailLabel.setBounds(INSTRUCTIONS_DETAIL_LABEL_BOUNDS);
		}
		return instructionsDetailLabel;
	}

	private JLabel getContentCountLabel() {
		if (contentCountLabel == null) {
			contentCountLabel = new JLabel();
			contentCountLabel.setText(LABEL);
			contentCountLabel.setBounds(CONTENT_COUNT_LABEL_BOUNDS);
			contentCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return contentCountLabel;
	}

	private JScrollPane getContentScroll() {
		if (contentScroll == null) {
			contentScroll = new JScrollPane();
			contentScroll.setBounds(CONTENT_SCROLL_BOUNDS);
			contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			contentScroll.setViewportView(getContentTree());
			contentScroll.getViewport().setBackground(getContentTree().getBackground());
		}
		return contentScroll;
	}

	private JTree getContentTree() {
		if (contentTree == null) {
			contentTree = new ContentTree();
			contentTree.setName(CONTENT_TREE_NAME);
			contentTree.setDragEnabled(true);
			contentTree.setRootVisible(false);
			contentTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			contentTree.setEditable(false);
			contentTree.setBorder(CONTENT_TREE_BORDER);
			contentTree.setCellRenderer(new ContentTreeCellRenderer());
		}
		return contentTree;
	}

	private JPanel getMailIcon() {
		if (mailIcon == null) {
			mailIcon = new JPanel();
			mailIcon.setName(MAIL_ICON_NAME);
			mailIcon.setLayout(new GridBagLayout());
			mailIcon.setBounds(MAIL_ICON_BOUNDS);
		}
		return mailIcon;
	}

	private JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel();
			containerPanel.setLayout(null);
			containerPanel.setBounds(CONTAINER_PANEL_BOUNDS);
			containerPanel.add(getContentScroll());
			containerPanel.add(getJPanel());
			containerPanel.add(getJPanel1());
			containerPanel.setName(CONTAINER_PANEL_NAME);
		}
		return containerPanel;
	}

	private JPanel getJPanel() {
		if (jPanel == null) {
			contentTitleLabel = new JLabel();
			contentTitleLabel.setText(LABEL);
			contentTitleLabel.setName(SynthFonts.BOLD_FONT12_BLACK);
			contentTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.setBounds(JPANEL_BOUNDS);
			jPanel.setName(JPANEL_NAME);
			jPanel.add(contentTitleLabel, BorderLayout.CENTER);
		}
		return jPanel;
	}

	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(null);
			jPanel1.setBounds(JPANEL1_BOUNDS);
			jPanel1.setName(JPANEL1_NAME);
		}
		return jPanel1;
	}

	private JPanel getSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setLayout(new GridBagLayout());
			separator.setName(SEPARATOR_NAME);
			separator.setBounds(SEPARATOR_BOUNDS);
		}
		return separator;
	}

	private JButton getRejectButton() {
		if (rejectButton == null) {
			rejectButton = new JButton();
			rejectButton.setBounds(REJECT_BUTTON_BOUNDS);
			rejectButton.setName(REJECT_BUTTON_NAME);
		}
		return rejectButton;
	}

	private JButton getAcceptAllButton() {
		if (acceptAllButton == null) {
			acceptAllButton = new JButton();
			acceptAllButton.setBounds(ACCEPT_ALL_BUTTON_BOUNDS);
			acceptAllButton.setName(ACCEPT_ALL_BUTTON_NAME);
		}
		return acceptAllButton;
	}

	public void addActionListenerToRejectButton(ActionListener actionListener) {
		getRejectButton().addActionListener(actionListener);
	}

	public void addActionListenerToAcceptAllButton(ActionListener actionListener) {
		getAcceptAllButton().addActionListener(actionListener);
	}

}

final class ContentTree extends JTree {
	private static final long serialVersionUID = 1L;

	private static final int NODE_HEIGHT = 20;

	private static final int SEPARATOR_HEIGHT = 19;

	private final Color selectionBGColor;
	private final Color treeBackgroundColor;

	public ContentTree() {
		selectionBGColor = SynthColors.BLUE175_205_225;
		treeBackgroundColor = SynthColors.GRAY235_240_245;
		super.setRowHeight(NODE_HEIGHT);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.setColor(treeBackgroundColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		int[] selectionRows = getSelectionRows();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (selectionRows != null) {
			for (int i : selectionRows) {
				int y = i * NODE_HEIGHT;
				g.setColor(selectionBGColor);
				g.fillRect(0, y, getWidth(), NODE_HEIGHT);
				g.setColor(treeBackgroundColor);
				g.fillRect(0, y + SEPARATOR_HEIGHT, getWidth(), 1);
			}
		}

		setOpaque(false);
		super.paintComponent(g);
		setOpaque(true);
	}

	@Override
	public void setRowHeight(int rowHeight) {
	}
}

final class ContentTreeCellRenderer implements TreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private final Icon trackIcon;
	private final Icon playlistIcon;
	private final Icon folderIcon;
	private final JLabel label;

	public ContentTreeCellRenderer() {
		folderIcon = UIManager.getDefaults().getIcon("icons.folderBlue");
		playlistIcon = UIManager.getDefaults().getIcon("icons.playlistBlue");
		trackIcon = UIManager.getDefaults().getIcon("icons.trackBlue");
		label = new JLabel();
		label.setName(SynthFonts.PLAIN_FONT11_BLACK);
		label.setIconTextGap(8);
		label.setOpaque(false);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object obj, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		int width = 294;
		Object value = ((DefaultMutableTreeNode) obj).getUserObject();
		TreeNode node = (TreeNode) obj;
		while (node.getParent() != null) {
			node = node.getParent();
			width -= 22;
		}
		if (value instanceof Track) {
			label.setIcon(trackIcon);
		}
		if (value instanceof Playlist) {
			label.setIcon(playlistIcon);
		}
		if (value instanceof Folder) {
			label.setIcon(folderIcon);
		}
		label.setText(value.toString());
		label.setPreferredSize(new Dimension(width, 20));
		return label;
	}
}

final class ModelMutableTreeNode extends DefaultMutableTreeNode implements Comparable<ModelMutableTreeNode> {
	private static final long serialVersionUID = 1L;
	private ModelComparator comparator;

	public ModelMutableTreeNode(Object object) {
		super(object);
		comparator = new ModelComparator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void insert(final MutableTreeNode newChild, final int childIndex) {
		super.insert(newChild, childIndex);
		Collections.sort(this.children);
	}

	@Override
	public int compareTo(ModelMutableTreeNode o) {
		return comparator.compare(this.getUserObject(), o.getUserObject());
	}

	final class ModelComparator implements Comparator<Object> {

		@Override
		public int compare(Object obj1, Object obj2) {
			if (obj1 instanceof Track) {
				if (obj2 instanceof Track) {
					return ((Track) obj1).getName().compareTo(((Track) obj2).getName());
				}
				if (obj2 instanceof Playlist) {
					return 1;
				}
				if (obj2 instanceof Folder) {
					return 1;
				}
			}
			if (obj1 instanceof Playlist) {
				if (obj2 instanceof Track) {
					return -1;
				}
				if (obj2 instanceof Playlist) {
					return ((Playlist) obj1).getName().compareTo(((Playlist) obj2).getName());
				}
				if (obj2 instanceof Folder) {
					return 1;
				}
			}
			if (obj1 instanceof Folder) {
				if (obj2 instanceof Track) {
					return -1;
				}
				if (obj2 instanceof Playlist) {
					return -1;
				}
				if (obj2 instanceof Folder) {
					return ((Folder) obj1).getName().compareTo(((Folder) obj2).getName());
				}
			}
			throw new RuntimeException("WTF");
		}

	}
}
