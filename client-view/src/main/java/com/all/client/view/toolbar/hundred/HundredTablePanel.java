package com.all.client.view.toolbar.hundred;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.view.View;
import com.all.client.view.components.TableCellInfo;
import com.all.client.view.model.SingleValueTableModel;
import com.all.client.view.music.DescriptionTableColumns;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.downloader.bean.DownloadState;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public final class HundredTablePanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Insets DOWNLOAD_BUTTON_INSETS = new Insets(0, 8, 0, 5);

	private static final Dimension DOWNLOAD_PANEL_DEFAULT_SIZE = new Dimension(146, 22);

	private static final Dimension DOWNLOAD_BUTTON_DEFAULT_SIZE = new Dimension(30, 18);

	private static final Dimension TOP_PANEL_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 22);

	private static final Insets TITLE_LABEL_INSETS = new Insets(4, 10, 4, 0);

	private static final Insets TOP_PANEL_INSETS = new Insets(0, 0, 2, 0);

	private static final Dimension TABLE_PANEL_MINIMUM_SIZE = new Dimension(392, 206);

	private static final Dimension TABLE_PANEL_DEFAULT_SIZE = new Dimension(594, 206);

	private static final Dimension TOP_PANEL_DEFAULT_SIZE = new Dimension(100, 22);

	private static final String DOWNLOAD_BUTTON_NAME = "downloadAllButton";

	private static final String TOP_PANEL_NAME = "hundredTableHeaderBackgroundPanel";

	private JPanel topPanel;

	private JLabel titleLabel;

	private JButton downloadButton;

	private JPanel downloadPanel;

	private JLabel downloadLabel;

	private HundredTrackTable descriptionTable;

	private JScrollPane scrollPane;

	private Playlist playlist;

	private final List<DescriptionTableColumns> columns;

	private ViewEngine viewEngine;

	private Listener<ValueEvent<Download>> downloadAddedListener;
	private Listener<ValueEvent<Download>> downloadUpdatedListener;
	private Listener<ValueEvent<Download>> downloadCompletedListener;

	private final HundredModelSourceProvider sourceProvider;

	public HundredTablePanel(List<DescriptionTableColumns> columns, HundredModelSourceProvider sourceProvider) {
		this.columns = columns;
		this.sourceProvider = sourceProvider;
		downloadAddedListener = new DownloadAddedListener();
		downloadUpdatedListener = new DownloadUpdatedListener();
		downloadCompletedListener = new DownloadCompletedListener();
	}

	private void setup() {
		getDescriptionTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				TableCellInfo<Track> cellData = getDescriptionTable().getCellData(e.getPoint());
				if (cellData == null) {
					return;
				}
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					Track clickedTrack = cellData.getData();
					ModelCollection downloadTracks = new ModelCollection(clickedTrack);
					downloadTracks.source(sourceProvider.getSource());
					downloadTracks.setRemote(true);
					viewEngine.request(Actions.Downloads.REQUEST_ADD_TRACK, clickedTrack, new ResponseCallback<Download>() {
						@Override
						public void onResponse(Download response) {
						}
					});
				}
			}
		});
	}

	private void initialize() {
		this.setPreferredSize(TABLE_PANEL_DEFAULT_SIZE);
		this.setMinimumSize(TABLE_PANEL_MINIMUM_SIZE);
		this.setLayout(new GridBagLayout());
		GridBagConstraints topPanelConstraints = new GridBagConstraints();
		topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		topPanelConstraints.weightx = 1;
		topPanelConstraints.insets = TOP_PANEL_INSETS;
		GridBagConstraints middlePanelConstraints = new GridBagConstraints();
		middlePanelConstraints.fill = GridBagConstraints.BOTH;
		middlePanelConstraints.gridx = 0;
		middlePanelConstraints.gridy = 1;
		middlePanelConstraints.weightx = 1;
		middlePanelConstraints.weighty = 1;

		this.add(getTopPanel(), topPanelConstraints);
		this.add(getScrollPane(), middlePanelConstraints);
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getDescriptionTable());
		}
		return scrollPane;
	}

	private HundredTrackTable getDescriptionTable() {
		if (descriptionTable == null) {
			descriptionTable = new HundredTrackTable(columns, viewEngine, sourceProvider);
		}
		return descriptionTable;
	}

	public JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			topPanel.setName(TOP_PANEL_NAME);
			topPanel.setLayout(new GridBagLayout());
			topPanel.setPreferredSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setMinimumSize(TOP_PANEL_DEFAULT_SIZE);
			topPanel.setMaximumSize(TOP_PANEL_MAXIMUM_SIZE);
			GridBagConstraints titleConstraints = new GridBagConstraints();
			titleConstraints.weightx = 1;
			titleConstraints.weighty = 1;
			titleConstraints.fill = GridBagConstraints.BOTH;
			titleConstraints.insets = TITLE_LABEL_INSETS;

			GridBagConstraints downloadPanelConstraints = new GridBagConstraints();
			downloadPanelConstraints.gridx = 1;
			topPanel.add(getTitleLabel(), titleConstraints);
			topPanel.add(getDownloadPanel(), downloadPanelConstraints);
		}
		return topPanel;
	}

	private JPanel getDownloadPanel() {
		if (downloadPanel == null) {
			downloadPanel = new JPanel();
			downloadPanel.setPreferredSize(DOWNLOAD_PANEL_DEFAULT_SIZE);
			downloadPanel.setLayout(new GridBagLayout());

			GridBagConstraints downloadLabelConstraints = new GridBagConstraints();

			GridBagConstraints downloadButtonConstraints = new GridBagConstraints();
			downloadButtonConstraints.gridx = 1;
			downloadButtonConstraints.insets = DOWNLOAD_BUTTON_INSETS;

			downloadPanel.add(getDownloadLabel(), downloadLabelConstraints);
			downloadPanel.add(getDownloadButton(), downloadButtonConstraints);
			downloadPanel.setVisible(false);
		}
		return downloadPanel;
	}

	public JLabel getDownloadLabel() {
		if (downloadLabel == null) {
			downloadLabel = new JLabel();
			downloadLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return downloadLabel;
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return titleLabel;
	}

	private JButton getDownloadButton() {
		if (downloadButton == null) {
			downloadButton = new JButton();
			downloadButton.setPreferredSize(DOWNLOAD_BUTTON_DEFAULT_SIZE);
			downloadButton.setName(DOWNLOAD_BUTTON_NAME);
			downloadButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					ModelCollection downloadTracks = new ModelCollection(playlist);
					downloadTracks.setRemote(true);
					viewEngine.sendValueAction(Actions.Downloads.ADD_MODEL_COLLECTION, downloadTracks);
					getDescriptionTable().updateAllTable();
					getDescriptionTable().invalidate();
					SwingUtilities.getWindowAncestor(getDescriptionTable()).validate();
				}
			});
		}
		return downloadButton;
	}

	@Override
	public void internationalize(Messages messages) {
		downloadLabel.setText(messages.getMessage("descriptionTable.downloadAll"));
		getDescriptionTable().internationalize(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getDescriptionTable().setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getDescriptionTable().removeMessages(messages);
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
		sourceProvider.setPlaylist(playlist);
		getTitleLabel().setText(playlist.getName());
		getDescriptionTable().setModel(playlist.getTracks());
		getDownloadPanel().setVisible(true);
	}

	@SuppressWarnings("unchecked")
	public void cleanPanel() {
		getTitleLabel().setText("");
		getDownloadPanel().setVisible(false);
		SingleValueTableModel<Track> contentModel = (SingleValueTableModel<Track>) getDescriptionTable().getModel();
		if (playlist != null) {
			for (Track track : playlist.getTracks()) {
				removeRowFor(track, contentModel);
			}
		}
	}

	private void removeRowFor(Track track, SingleValueTableModel<Track> model) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(track)) {
				model.removeRow(i);
				return;
			}
		}
	}

	private final class DownloadAddedListener extends EventListener<ValueEvent<Download>> {

		@Override
		public void handleEvent(ValueEvent<Download> eventArgs) {
			getDescriptionTable().repaintIndex();
			getDescriptionTable().updateRow(eventArgs.getValue().getDownloadId());
			getDescriptionTable().updateTable();
		}

	}

	private final class DownloadUpdatedListener extends EventListener<ValueEvent<Download>> {
		@Override
		public void handleEvent(ValueEvent<Download> eventArgs) {
			Download download = eventArgs.getValue();
			Map<String, DownloadState> trackDownloads = new ConcurrentHashMap<String, DownloadState>();
			String trackId = download.getTrackId();
			DownloadState downloadState = trackDownloads.get(trackId);
			if (!download.getStatus().equals(downloadState)) {
				trackDownloads.put(trackId, download.getStatus());
				getDescriptionTable().updateRow(trackId);
			}

		}

	}

	private final class DownloadCompletedListener extends EventListener<ValueEvent<Download>> {

		@Override
		public void handleEvent(ValueEvent<Download> eventArgs) {
			getDescriptionTable().tryUpdateDownloadComplete(eventArgs.getValue());

		}

	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.Downloads.ADDED, downloadAddedListener);
		viewEngine.removeListener(Events.Downloads.UPDATED, downloadUpdatedListener);
		viewEngine.removeListener(Events.Downloads.COMPLETED, downloadCompletedListener);

	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
		setup();
		viewEngine.addListener(Events.Downloads.ADDED, downloadAddedListener);
		viewEngine.addListener(Events.Downloads.UPDATED, downloadUpdatedListener);
		viewEngine.addListener(Events.Downloads.COMPLETED, downloadCompletedListener);
	}

}
