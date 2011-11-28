package com.all.client.view.toolbar.hundred;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.model.ModelContainerView;
import com.all.core.model.TopHundredModelContainer;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Playlist;

public final class PlaylistPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private final static Log log = LogFactory.getLog(PlaylistPanel.class);

	private static final Dimension DEFAULT_SIZE = new Dimension(296, 222);

	private static final Dimension MINIMUM_SIZE = new Dimension(195, 222);

	private static final Dimension PLAYLIST_SCROLLPANE_DEFAULT_SIZE = new Dimension(296, 160);

	private static final Dimension PLAYLIST_SCROLLPANEL_MINIMUM_SIZE = new Dimension(195, 160);

	private static final Dimension PLAYLIST_TOP_PANEL_MINIMUM_SIZE = new Dimension(195, 24);

	private static final Dimension PLAYLIST_TOP_PANEL_DEFAULT_SIZE = new Dimension(296, 24);

	private static final Dimension PLAYLIST_BOTTOM_PANEL_MINIMUM_SIZE = new Dimension(195, 16);

	private static final Dimension PLAYLIST_BOTTOM_PANEL_DEFAULT_SIZE = new Dimension(296, 16);

	private static final Dimension PLAYLIST_TITLE_PANEL_DEFAULT_SIZE = new Dimension(296, 20);

	private static final Dimension PLAYLIST_TITLE_PANEL_MINIMUM_SIZE = new Dimension(195, 20);

	private static final Insets INSTRUCTIONS_LABEL_INSETS = new Insets(0, 15, 0, 0);

	private static final Insets TITLE_LABEL_INSETS = new Insets(0, 10, 0, 0);

	private static final String NAME = "hundredBackgroundPanel";

	private static final String PLAYLIST_CONTAINER_PANEL_NAME = "hundredContainerBackgroundPanel";

	private JPanel playlistTopPanel;

	private JPanel playlistBottomPanel;

	private JPanel playlistContainerPanel;

	private JPanel playlistTitlePanel;

	private JScrollPane playlistScrollPane;

	private JLabel titleLabel;

	private JLabel instructionsLabel;

	private JTable playlistTable;

	private HundredTablePanel hundredTablePanel;

	private ViewEngine viewEngine;

	private final HundredModelSourceProvider sourceProvider;
	private Listener<ValueEvent<ModelContainerView>> selectPlaylistListener;

	private List<Playlist> playlists;

	public PlaylistPanel(HundredTablePanel hundredTablePanel, HundredModelSourceProvider sourceProvider) {
		this.hundredTablePanel = hundredTablePanel;
		this.sourceProvider = sourceProvider;
		selectPlaylistListener = new EventListener<ValueEvent<ModelContainerView>>() {

			@Override
			public void handleEvent(ValueEvent<ModelContainerView> eventArgs) {
				TopHundredModelContainer container = (TopHundredModelContainer) eventArgs.getValue();
				doSelectPlaylist(container);
			}

			private void doSelectPlaylist(TopHundredModelContainer modelContainerView) {
				String currentPlaylistHash = modelContainerView.getPlaylistHash();
				for (int index = 0; index < playlists.size(); index++) {
					Playlist playlist = playlists.get(index);
					if (playlist.getHashcode().equals(currentPlaylistHash)) {
						playlistTable.getSelectionModel().clearSelection();
						playlistTable.getCellEditor(playlistTable.getEditingRow(), 0).stopCellEditing();
						playlistTable.getSelectionModel().setSelectionInterval(index, index);
					}
				}
			}

		};
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setMinimumSize(MINIMUM_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.add(getPlaylistTitlePanel(), BorderLayout.NORTH);
		this.add(getPlaylistContainerPanel(), BorderLayout.CENTER);
		this.setName(NAME);
	}

	private JPanel getPlaylistTitlePanel() {
		if (playlistTitlePanel == null) {
			playlistTitlePanel = new JPanel();
			playlistTitlePanel.setMinimumSize(PLAYLIST_TITLE_PANEL_MINIMUM_SIZE);
			playlistTitlePanel.setPreferredSize(PLAYLIST_TITLE_PANEL_DEFAULT_SIZE);
			playlistTitlePanel.setLayout(new GridBagLayout());
			GridBagConstraints titleConstraints = new GridBagConstraints();
			titleConstraints.gridx = 0;
			titleConstraints.gridy = 0;
			titleConstraints.weightx = 1;
			titleConstraints.weighty = 1;
			titleConstraints.fill = GridBagConstraints.BOTH;
			titleConstraints.insets = TITLE_LABEL_INSETS;
			playlistTitlePanel.add(getTitleLabel(), titleConstraints);
		}
		return playlistTitlePanel;
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setName(SynthFonts.BOLD_FONT11_GRAY64_64_64);
		}
		return titleLabel;
	}

	private JPanel getPlaylistContainerPanel() {
		if (playlistContainerPanel == null) {
			playlistContainerPanel = new JPanel();
			playlistContainerPanel.setLayout(new BorderLayout());
			playlistContainerPanel.add(getPlaylistTopPanel(), BorderLayout.NORTH);
			playlistContainerPanel.add(getPlaylistScrollPane(), BorderLayout.CENTER);
			playlistContainerPanel.add(getPlaylistBottomPanel(), BorderLayout.SOUTH);
			playlistContainerPanel.setName(PLAYLIST_CONTAINER_PANEL_NAME);
			playlistContainerPanel.setMinimumSize(MINIMUM_SIZE);
			playlistContainerPanel.setPreferredSize(DEFAULT_SIZE);
		}
		return playlistContainerPanel;
	}

	private JPanel getPlaylistBottomPanel() {
		if (playlistBottomPanel == null) {
			playlistBottomPanel = new JPanel();
			playlistBottomPanel.setPreferredSize(PLAYLIST_BOTTOM_PANEL_DEFAULT_SIZE);
			playlistBottomPanel.setMinimumSize(PLAYLIST_BOTTOM_PANEL_MINIMUM_SIZE);
		}
		return playlistBottomPanel;
	}

	private JPanel getPlaylistTopPanel() {
		if (playlistTopPanel == null) {
			playlistTopPanel = new JPanel();
			playlistTopPanel.setPreferredSize(PLAYLIST_TOP_PANEL_DEFAULT_SIZE);
			playlistTopPanel.setMinimumSize(PLAYLIST_TOP_PANEL_MINIMUM_SIZE);
			playlistTopPanel.setLayout(new GridBagLayout());
			GridBagConstraints instructionsConstraints = new GridBagConstraints();
			instructionsConstraints.gridx = 0;
			instructionsConstraints.gridy = 0;
			instructionsConstraints.weightx = 1;
			instructionsConstraints.weighty = 1;
			instructionsConstraints.fill = GridBagConstraints.BOTH;
			instructionsConstraints.insets = INSTRUCTIONS_LABEL_INSETS;
			playlistTopPanel.add(getInstructionsLabel(), instructionsConstraints);
		}
		return playlistTopPanel;
	}

	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			instructionsLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return instructionsLabel;
	}

	private JScrollPane getPlaylistScrollPane() {
		if (playlistScrollPane == null) {
			playlistScrollPane = new JScrollPane();

			playlistScrollPane.setViewportView(getPlaylistTable());
			playlistScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			playlistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			playlistScrollPane.setMinimumSize(PLAYLIST_SCROLLPANEL_MINIMUM_SIZE);
			playlistScrollPane.setPreferredSize(PLAYLIST_SCROLLPANE_DEFAULT_SIZE);
		}
		return playlistScrollPane;
	}

	@Override
	public void internationalize(Messages messages) {
		instructionsLabel.setText(messages.getMessage("hundred.playlist.list.instructions"));
		titleLabel.setText(messages.getMessage("hundred.playlist.list.title"));
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	public JTable getPlaylistTable() {
		if (playlistTable == null) {
			playlistTable = new HundredPlaylistTable(viewEngine, hundredTablePanel, sourceProvider);
		}
		return playlistTable;
	}

	public void setPlaylists(List<Playlist> playlists) {
		this.playlists = playlists;
		log.info("THIS IS THE PLAYLIST " + playlists);
		playlistTable.clearSelection();
		DefaultTableModel contentModel = (DefaultTableModel) playlistTable.getModel();
		if (contentModel.getRowCount() > 0) {
			// TODO:Review this is working but is not the best way to solve
			// this is because some times is remaining one value on the table
			removeRows();
			removeRows();
		}
		for (Playlist playlist : playlists) {
			contentModel.addRow(new Object[] { playlist });
		}
		playlistTable.repaint();
	}

	private void removeRows() {
		DefaultTableModel model = (DefaultTableModel) playlistTable.getModel();
		int row = 0;
		while (model.getRowCount() > row) {
			playlistTable.getCellEditor(row, 0).stopCellEditing();
			model.removeRow(row);
			row++;
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.View.TOP_HUNDRED_PLAYLIST_MODEL_SELECTION, selectPlaylistListener);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
		viewEngine.addListener(Events.View.TOP_HUNDRED_PLAYLIST_MODEL_SELECTION, selectPlaylistListener);

	}
}
