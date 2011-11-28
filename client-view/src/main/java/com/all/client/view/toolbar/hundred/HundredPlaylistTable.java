package com.all.client.view.toolbar.hundred;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.ModelTransfereable;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Playlist;

public final class HundredPlaylistTable extends JTable {

	private static final long serialVersionUID = 1L;

	private static final int ROW_HEIGHT = 20;

	private static final Dimension DOWNLOAD_BUTTON_DEFAULT_SIZE = new Dimension(30, 18);

	private static final Insets DOWNLOAD_BUTTON_INSETS = new Insets(0, 3, 0, 3);

	private static final Insets TITLE_PLAYLIST_LABEL_INSETS = new Insets(0, 15, 0, 0);

	private static final String DOWNLOAD_ALL_BUTTON_NAME = "downloadAllButton";

	private static final String HUNDRED_SELECTED_CELL_NAME = "hundredSelectedCell";

	private static final String HUNDRED_HIGHLIGHT_CELL_NAME = "hundredHighlightCell";

	private HundredTablePanel hundredTablePanel;

	private final ViewEngine viewEngine;

	private final HundredModelSourceProvider sourceProvider;

	public HundredPlaylistTable(ViewEngine viewEngine, HundredTablePanel hundredTablePanel,
			HundredModelSourceProvider sourceProvider) {
		this.viewEngine = viewEngine;
		this.hundredTablePanel = hundredTablePanel;
		this.sourceProvider = sourceProvider;
		initialize();
	}

	private void initialize() {
		this.setShowGrid(false);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setRowHeight(ROW_HEIGHT);
		this.setCellSelectionEnabled(true);
		setupTable();
	}

	private void setupTable() {
		TableColumn mainColumn = new TableColumn();
		mainColumn.setIdentifier("");
		mainColumn.setCellRenderer(new PlaylistCellRenderer());
		mainColumn.setCellEditor(new PlaylistCellEditor());

		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(mainColumn);

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(mainColumn);

		this.setModel(model);
		this.setColumnModel(columnModel);

		setupDnD(this);

		this.addMouseMotionListener(new MouseMotionAdapter() {
			private int row = -1;
			private Point location = null;

			@Override
			public void mouseMoved(MouseEvent mouseEvent) {
				getCellEditor(0, 0).stopCellEditing();
				location = mouseEvent.getPoint();
				row = rowAtPoint(location);
				editCellAt(row, 0);
			}
		});

		this.addMouseListener(new MouseAdapter() {
			int row = -1;
			Point location = null;

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
				location = mouseEvent.getPoint();
				row = rowAtPoint(location);
				editCellAt(row, 0);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
				getCellEditor(row, 0).stopCellEditing();
				location = mouseEvent.getPoint();
				row = rowAtPoint(location);
				editCellAt(row, 0);
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				location = mouseEvent.getPoint();
				row = rowAtPoint(location);
				setRowSelectionInterval(row, row);
				editCellAt(row, 0);
			}
		});
		this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent paramListSelectionEvent) {
				int row = getSelectionModel().getMinSelectionIndex();
				if (row != -1) {
					Playlist playlistSelected = (Playlist) getValueAt(row, 0);
					hundredTablePanel.setPlaylist(playlistSelected);
				}
			}
		});
	}

	private class PlaylistCellRenderer implements TableCellRenderer {

		private JLabel titlePlaylistLabel;

		private JButton downloadButton;

		private JPanel container;

		private Icon PLAYLIST_ICON = UIManager.getDefaults().getIcon("Tree.leafIcon");

		public PlaylistCellRenderer() {
			container = new JPanel();
			downloadButton = new JButton();
			titlePlaylistLabel = new JLabel();
		}

		@Override
		public Component getTableCellRendererComponent(JTable paramJTable, Object value, boolean isSelected,
				boolean paramBoolean2, int paramInt1, int paramInt2) {
			container.setLayout(new GridBagLayout());
			Playlist hundredPlaylist = (Playlist) value;
			titlePlaylistLabel.setText(hundredPlaylist.getName());
			titlePlaylistLabel.setIcon(PLAYLIST_ICON);

			GridBagConstraints titlePlaylistLabelConstraints = new GridBagConstraints();
			titlePlaylistLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			titlePlaylistLabelConstraints.weightx = 1.0;
			titlePlaylistLabelConstraints.insets = TITLE_PLAYLIST_LABEL_INSETS;

			GridBagConstraints downloadButtonConstraints = new GridBagConstraints();
			downloadButtonConstraints.gridx = 1;
			downloadButtonConstraints.insets = DOWNLOAD_BUTTON_INSETS;

			downloadButton.setName(DOWNLOAD_ALL_BUTTON_NAME);
			downloadButton.setPreferredSize(DOWNLOAD_BUTTON_DEFAULT_SIZE);
			downloadButton.setMinimumSize(DOWNLOAD_BUTTON_DEFAULT_SIZE);

			container.add(titlePlaylistLabel, titlePlaylistLabelConstraints);
			container.add(downloadButton, downloadButtonConstraints);
			if (isSelected) {
				container.setName(HUNDRED_SELECTED_CELL_NAME);
				titlePlaylistLabel.setName(SynthFonts.BOLD_FONT12_GRAY50_50_50);
				downloadButton.setVisible(true);
				if (paramJTable.isEditing()) {
					if (value != paramJTable.getCellEditor().getCellEditorValue()) {
						downloadButton.setVisible(false);
					}
				}
			} else {
				container.setName("");
				downloadButton.setVisible(false);
				titlePlaylistLabel.setName(SynthFonts.PLAIN_FONT12_GRAY80_80_80);
			}
			return container;
		}
	}

	private class PlaylistCellEditor extends AbstractCellEditor implements TableCellEditor {

		private static final long serialVersionUID = 1L;

		private JLabel titlePlaylistLabel;

		private JButton downloadButton;

		private JPanel container;

		private Object actualValue;

		private Icon PLAYLIST_ICON = UIManager.getDefaults().getIcon("Tree.leafIcon");

		private Playlist hundredPlaylist;

		public PlaylistCellEditor() {
			container = new JPanel();
			titlePlaylistLabel = new JLabel();
			downloadButton = new JButton();
			downloadButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent paramActionEvent) {
					ModelCollection downloadTracks = new ModelCollection(hundredPlaylist);
					downloadTracks.setRemote(true);
					downloadTracks.source(sourceProvider.getSource());
					viewEngine.sendValueAction(Actions.Downloads.ADD_MODEL_COLLECTION, downloadTracks);
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			actualValue = value;
			container.setLayout(new GridBagLayout());
			hundredPlaylist = (Playlist) value;
			titlePlaylistLabel.setText(hundredPlaylist.getName());
			titlePlaylistLabel.setIcon(PLAYLIST_ICON);

			GridBagConstraints titlePlaylistLabelConstraints = new GridBagConstraints();
			titlePlaylistLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			titlePlaylistLabelConstraints.weightx = 1.0;
			titlePlaylistLabelConstraints.insets = TITLE_PLAYLIST_LABEL_INSETS;

			GridBagConstraints downloadButtonConstraints = new GridBagConstraints();
			downloadButtonConstraints.gridx = 1;
			downloadButtonConstraints.insets = DOWNLOAD_BUTTON_INSETS;

			downloadButton.setName(DOWNLOAD_ALL_BUTTON_NAME);
			downloadButton.setPreferredSize(DOWNLOAD_BUTTON_DEFAULT_SIZE);
			downloadButton.setMinimumSize(DOWNLOAD_BUTTON_DEFAULT_SIZE);

			container.add(titlePlaylistLabel, titlePlaylistLabelConstraints);
			container.add(downloadButton, downloadButtonConstraints);
			if (isSelected) {
				container.setName(HUNDRED_SELECTED_CELL_NAME);
				titlePlaylistLabel.setName(SynthFonts.BOLD_FONT12_GRAY50_50_50);
			} else {
				container.setName(HUNDRED_HIGHLIGHT_CELL_NAME);
				titlePlaylistLabel.setName(SynthFonts.PLAIN_FONT12_GRAY80_80_80);
			}
			return container;
		}

		@Override
		public Object getCellEditorValue() {
			return actualValue;
		}
	}

	private void setupDnD(JTable table) {
		table.setDragEnabled(true);
		table.setTransferHandler(new TransferHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			protected Transferable createTransferable(JComponent c) {
				ModelCollection modeldragged = new ModelCollection();
				JTable table = (JTable) c;
				int[] selectedRows = table.getSelectedRows();
				List<Playlist> playlists = new ArrayList<Playlist>();
				for (int i = 0; i < selectedRows.length; i++) {
					Playlist playlist = (Playlist) table.getModel().getValueAt(selectedRows[i], 0);
					// This sucks but thou shall not remove it.
					playlist.getTracks();
					playlists.add(playlist);
				}
				modeldragged.getPlaylists().addAll(playlists);
				modeldragged.setRemote(true);
				ModelSource source = null;
				if (playlists.isEmpty() && playlists.get(0) != null) {
					source = sourceProvider.getSource();
				} else {
					source = ModelSource.topHundred(sourceProvider.getCategory(), playlists.get(0));
				}

				return new ModelTransfereable(source, modeldragged, viewEngine.get(Model.TRACK_REPOSITORY));
			}

			@Override
			public boolean importData(TransferSupport support) {
				return true;
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return false;
			}

			public int getSourceActions(JComponent c) {
				return COPY;
			}
		});
		table.setDropTarget(null);
	}
}
