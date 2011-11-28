package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.all.client.model.ModelTransfereable;
import com.all.client.util.TrackRepository;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public final class ContentPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final Border CONTENT_TREE_BORDER = BorderFactory.createEmptyBorder(0, 15, 0, 0);

	private static final Dimension BUTTON_DEFAULT_SIZE = new Dimension(80, 22);

	private static final Dimension CONTAINER_PANEL_DEFAULT_SIZE = new Dimension(330, 270);

	private static final Dimension CONTENT_SCROLL_DEFAULT_SIZE = new Dimension(318, 224);

	private static final Dimension DEFAULT_SIZE = new Dimension(360, 480);

	private static final Dimension FOOTER_ROUNDED_PANEL_DEFAULT_SIZE = new Dimension(318, 17);

	private static final Dimension HEADER_ROUNDED_PANEL_DEFAULT_SIZE = new Dimension(318, 18);

	private static final Dimension INSTRUCTIONS_DETAIL_LABEL_DEFAULT_SIZE = new Dimension(250, 30);

	private static final Dimension INSTRUCTIONS_TOP_LABEL_DEFAULT_SIZE = new Dimension(260, 15);

	private static final Dimension MESSAGE_LABEL_DEFAULT_SIZE = new Dimension(322, 114);

	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(337, 2);

	private static final Point CONTENT_SCROLL_LOCATION = new Point(6, 23);

	private static final Point CONTAINER_PANEL_LOCATION = new Point(7, 63);

	private static final Point FOOTER_ROUNDED_PANEL_LOCATION = new Point(6, 247);

	private static final Point HEADER_ROUNDED_PANEL_LOCATION = new Point(6, 6);

	private static final Point INSTRUCTIONS_DETAIL_LABEL_LOC_BLACK = new Point(73, 25);

	private static final Point INSTRUCTIONS_TOP_LABEL_LOCATION = new Point(73, 10);

	private static final Point LEFT_BUTTON_LOCATION = new Point(89, 422);

	private static final Point MESSAGE_LABEL_LOCATION = new Point(12, 315);

	private static final Point RIGHT_BUTTON_LOCATION = new Point(179, 422);

	private static final Point SEPARATOR_LOCATION = new Point(6, 413);

	private static final Rectangle CONTENT_ICON_BOUNDS = new Rectangle(19, 13, 40, 40);

	private static final String CONTAINER_PANEL_NAME = "grayRoundedBorderPanel";

	private static final String CONTENT_ICON_NAME = "unimportedItunes.musicIcon";

	private static final String CONTENT_TREE_NAME = "previewTree";

	private static final String HEADER_ROUNDED_PANEL_NAME = "myMusicPanel";

	private static final String FOOTER_ROUNDED_PANEL_NAME = "myMusicBottomLibPanel";

	private static final String LABEL_TEXT = "JLabel";

	private static final String LEFT_BUTTON_NAME = "buttonReject";

	private static final String RIGHT_BUTTON_NAME = "buttonAcceptAll";

	private static final String SEPARATOR_NAME = "bottomPanelSeparator";

	private JButton leftButton = null;

	private JButton rightButton = null;

	private JLabel contentTitleLabel = null;

	private JLabel instructionsDetailLabelBlack = null;

	private JLabel instructionsTopLabel = null;

	private JLabel messageLabel = null;

	private JPanel containerPanel = null;

	private JPanel contentIcon = null;

	private JPanel footerRoundedPanel = null;

	private JPanel headerRoundedPanel = null;

	private JPanel separator = null;

	private JScrollPane contentScroll = null;

	private JTree contentTree = null;

	private Map<Object, Integer> tracksInside = new HashMap<Object, Integer>();

	private final TrackRepository trackRepository;

	public ContentPanel(ModelCollection model, TrackRepository trackRepository) {
		this.trackRepository = trackRepository;
		initialize();
		setUpModel(model);
		setup();
	}

	private void setup() {
		TransparencyManagerFactory.getManager().setWindowOpaque(SwingUtilities.getWindowAncestor(this), false);
		getContentTree().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				int value = 0;
				TreePath[] selectionPaths = contentTree.getSelectionPaths();
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

			@Override
			public Transferable createTransferable(JComponent c) {
				java.util.List<Object> selection = new ArrayList<Object>();
				for (TreePath path : getContentTree().getSelectionPaths()) {
					Object model = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					selection.add(model);
				}
				ModelCollection modelCollection = new ModelCollection(selection);
				modelCollection.cleanUp();
				modelCollection.setRemote(true);
				return new ModelTransfereable(ModelSource.remote(), modelCollection, trackRepository);
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
				playlistTrack++;
			}
			tracksInside.put(playlist, playlistTrack);
			root.add(playlistNode);
		}
		for (Track track : model.getTracks()) {
			DefaultMutableTreeNode trackNode = new ModelMutableTreeNode(track);
			root.add(trackNode);
		}
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		contentTree.setModel(treeModel);
	}

	private void initialize() {
		getMessageLabel();
		getInstructionsDetailLabelBlack();
		this.setLayout(null);
		this.setSize(DEFAULT_SIZE);
		this.add(getContentIcon(), null);
		this.add(getInstructionsTopLabel(), null);
		this.add(getInstructionsDetailLabelBlack(), null);
		this.add(getContainerPanel(), null);
		this.add(getMessageLabel(), null);
		this.add(getSeparator(), null);
		this.add(getLeftButton(), null);
		this.add(getRightButton(), null);
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setText(LABEL_TEXT);
			messageLabel.setSize(MESSAGE_LABEL_DEFAULT_SIZE);
			messageLabel.setLocation(MESSAGE_LABEL_LOCATION);
		}
		return messageLabel;
	}

	private JLabel getInstructionsDetailLabelBlack() {
		if (instructionsDetailLabelBlack == null) {
			instructionsDetailLabelBlack = new JLabel();
			instructionsDetailLabelBlack.setLocation(INSTRUCTIONS_DETAIL_LABEL_LOC_BLACK);
			instructionsDetailLabelBlack.setSize(INSTRUCTIONS_DETAIL_LABEL_DEFAULT_SIZE);
		}
		return instructionsDetailLabelBlack;
	}

	private JLabel getInstructionsTopLabel() {
		if (instructionsTopLabel == null) {
			instructionsTopLabel = new JLabel();
			instructionsTopLabel.setText(LABEL_TEXT);
			instructionsTopLabel.setSize(INSTRUCTIONS_TOP_LABEL_DEFAULT_SIZE);
			instructionsTopLabel.setLocation(INSTRUCTIONS_TOP_LABEL_LOCATION);
			instructionsTopLabel.setName(SynthFonts.BOLD_FONT12_BLACK);
		}
		return instructionsTopLabel;
	}

	public JButton getLeftButton() {
		if (leftButton == null) {
			leftButton = new JButton();
			leftButton.setLocation(LEFT_BUTTON_LOCATION);
			leftButton.setSize(BUTTON_DEFAULT_SIZE);
			leftButton.setName(LEFT_BUTTON_NAME);
		}
		return leftButton;
	}

	public JButton getRightButton() {
		if (rightButton == null) {
			rightButton = new JButton();
			rightButton.setLocation(RIGHT_BUTTON_LOCATION);
			rightButton.setSize(BUTTON_DEFAULT_SIZE);
			rightButton.setName(RIGHT_BUTTON_NAME);
		}
		return rightButton;
	}

	private JPanel getContentIcon() {
		if (contentIcon == null) {
			contentIcon = new JPanel();
			contentIcon.setName(CONTENT_ICON_NAME);
			contentIcon.setLayout(new GridBagLayout());
			contentIcon.setBounds(CONTENT_ICON_BOUNDS);
		}
		return contentIcon;
	}

	private JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel();
			containerPanel.setLayout(null);
			containerPanel.setSize(CONTAINER_PANEL_DEFAULT_SIZE);
			containerPanel.setLocation(CONTAINER_PANEL_LOCATION);
			containerPanel.add(getContentScroll(), null);
			containerPanel.add(getHeaderRoundedPanel(), null);
			containerPanel.add(getFooterRoundedPanel(), null);
			containerPanel.setName(CONTAINER_PANEL_NAME);
		}
		return containerPanel;
	}

	private JScrollPane getContentScroll() {
		if (contentScroll == null) {
			contentScroll = new JScrollPane();
			contentScroll.setLocation(CONTENT_SCROLL_LOCATION);
			contentScroll.setSize(CONTENT_SCROLL_DEFAULT_SIZE);
			contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			contentScroll.setViewportView(getContentTree());
			contentScroll.getViewport().setBackground(getContentTree().getBackground());
		}
		return contentScroll;
	}

	private JPanel getHeaderRoundedPanel() {
		if (headerRoundedPanel == null) {
			contentTitleLabel = new JLabel();
			contentTitleLabel.setText(LABEL_TEXT);
			contentTitleLabel.setName(SynthFonts.BOLD_FONT12_BLACK);
			contentTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			headerRoundedPanel = new JPanel();
			headerRoundedPanel.setLayout(new BorderLayout());
			headerRoundedPanel.setSize(HEADER_ROUNDED_PANEL_DEFAULT_SIZE);
			headerRoundedPanel.setLocation(HEADER_ROUNDED_PANEL_LOCATION);
			headerRoundedPanel.setName(HEADER_ROUNDED_PANEL_NAME);
			headerRoundedPanel.add(contentTitleLabel, BorderLayout.CENTER);
		}
		return headerRoundedPanel;
	}

	private JPanel getFooterRoundedPanel() {
		if (footerRoundedPanel == null) {
			footerRoundedPanel = new JPanel();
			footerRoundedPanel.setLayout(null);
			footerRoundedPanel.setLocation(FOOTER_ROUNDED_PANEL_LOCATION);
			footerRoundedPanel.setSize(FOOTER_ROUNDED_PANEL_DEFAULT_SIZE);
			footerRoundedPanel.setName(FOOTER_ROUNDED_PANEL_NAME);
		}
		return footerRoundedPanel;
	}

	private JPanel getSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setLayout(new GridBagLayout());
			separator.setName(SEPARATOR_NAME);
			separator.setSize(SEPARATOR_DEFAULT_SIZE);
			separator.setLocation(SEPARATOR_LOCATION);
		}
		return separator;
	}

	private JTree getContentTree() {
		if (contentTree == null) {
			contentTree = new ContentTree();
			contentTree.setRootVisible(false);
			contentTree.setEditable(false);
			contentTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
			contentTree.setName(CONTENT_TREE_NAME);
			contentTree.setBorder(CONTENT_TREE_BORDER);
			contentTree.setCellRenderer(new ContentTreeCellRenderer());
		}
		return contentTree;
	}

	@Override
	public void internationalize(Messages messages) {
		leftButton.setText(messages.getMessage("unimportedItunes.leftButton"));
		rightButton.setText(messages.getMessage("unimportedItunes.rightButton"));
		contentTitleLabel.setText(messages.getMessage("unimportedItunes.contentTitle"));
		instructionsTopLabel.setText(messages.getMessage("unimportedItunes.instructionsTop"));
		instructionsDetailLabelBlack.setText(messages.getMessage("unimportedItunes.instructionsDetailBlack"));
		messageLabel.setText(messages.getMessage("unimportedItunes.message"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}
