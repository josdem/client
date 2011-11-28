package com.all.client.view.toolbar.hundred;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.MiddleCloseablePanel;
import com.all.client.view.View;
import com.all.client.view.components.GrayBackgroundedLoaderPanel;
import com.all.client.view.music.DescriptionTableColumns;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class HundredContainerPanel extends MiddleCloseablePanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension LIST_PANEL_MINIMUM_SIZE = new Dimension(195, 222);

	private static final Dimension LIST_PANEL_DEFAULT_SIZE = new Dimension(296, 222);

	private static final Insets CATEGORIES_PANEL_INSETS = new Insets(0, 0, 0, 1);

	private static final Insets PLAYLIST_PANEL_INSETS = new Insets(0, 1, 0, 0);

	private static final Insets TABLE_PANEL_INSETS = new Insets(2, 0, 0, 0);

	private JPanel listPanel;

	private HundredTablePanel tablePanel;

	private CategoriesPanel categoriesPanel;

	private PlaylistPanel playlistPanel;

	private JPanel loaderPanel;

	private final ViewEngine viewEngine;

	private JPanel mainPanel;

	private final HundredModelSourceProvider sourceProvider;

	public HundredContainerPanel(ViewEngine viewEngine) {
		super();
		this.viewEngine = viewEngine;
		this.sourceProvider = new HundredModelSourceProvider();
		initialize();
	}

	private void initialize() {
		getMiddlePanel().add(getMainPanel(), BorderLayout.CENTER);

		GridBagConstraints loaderPanelConstraints = new GridBagConstraints();
		loaderPanelConstraints.gridx = 0;
		loaderPanelConstraints.gridy = 1;
		loaderPanelConstraints.weightx = 1;
		loaderPanelConstraints.weighty = 1;
		loaderPanelConstraints.fill = GridBagConstraints.BOTH;
		this.add(getLoaderPanel(), loaderPanelConstraints);
		this.setComponentZOrder(getMiddlePanel(), 1);
		this.setComponentZOrder(getLoaderPanel(), 0);
	}

	@Override
	protected JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints listPanelConstraints = new GridBagConstraints();
			listPanelConstraints.fill = GridBagConstraints.BOTH;
			listPanelConstraints.weightx = .5;
			listPanelConstraints.weighty = .5;
			GridBagConstraints tablePanelConstraints = new GridBagConstraints();
			tablePanelConstraints.gridy = 1;
			tablePanelConstraints.fill = GridBagConstraints.BOTH;
			tablePanelConstraints.weightx = .5;
			tablePanelConstraints.weighty = .5;
			tablePanelConstraints.insets = TABLE_PANEL_INSETS;
			mainPanel.add(getListPanel(), listPanelConstraints);
			mainPanel.add(getTablePanel(), tablePanelConstraints);
		}
		return mainPanel;
	}

	private HundredTablePanel getTablePanel() {
		if (tablePanel == null) {
			List<DescriptionTableColumns> columns = new ArrayList<DescriptionTableColumns>();

			columns.add(DescriptionTableColumns.INDEX);
			columns.add(DescriptionTableColumns.NAME);
			columns.add(DescriptionTableColumns.ARTIST);
			columns.add(DescriptionTableColumns.ALBUM);
			columns.add(DescriptionTableColumns.TIME);
			columns.add(DescriptionTableColumns.KIND);
			columns.add(DescriptionTableColumns.BITRATE);
			tablePanel = new HundredTablePanel(columns, sourceProvider);
		}
		return tablePanel;
	}

	private JPanel getListPanel() {
		if (listPanel == null) {
			listPanel = new JPanel();
			listPanel.setPreferredSize(LIST_PANEL_DEFAULT_SIZE);
			listPanel.setMinimumSize(LIST_PANEL_MINIMUM_SIZE);
			listPanel.setLayout(new GridBagLayout());
			GridBagConstraints categoriesListContraints = new GridBagConstraints();
			categoriesListContraints.fill = GridBagConstraints.BOTH;
			categoriesListContraints.weightx = .5;
			categoriesListContraints.weighty = .5;
			categoriesListContraints.insets = CATEGORIES_PANEL_INSETS;

			GridBagConstraints playlistListConstraints = new GridBagConstraints();
			playlistListConstraints.gridx = 1;
			playlistListConstraints.fill = GridBagConstraints.BOTH;
			playlistListConstraints.weightx = .5;
			playlistListConstraints.weighty = .5;
			playlistListConstraints.insets = PLAYLIST_PANEL_INSETS;

			listPanel.add(getCategoriesPanel(), categoriesListContraints);
			listPanel.add(getPlaylistPanel(), playlistListConstraints);
			getCategoriesPanel().setPlaylistPanel(getPlaylistPanel());
		}
		return listPanel;
	}

	private PlaylistPanel getPlaylistPanel() {
		if (playlistPanel == null) {
			playlistPanel = new PlaylistPanel(getTablePanel(), sourceProvider);
			playlistPanel.initialize(viewEngine);
		}
		return playlistPanel;
	}

	private CategoriesPanel getCategoriesPanel() {
		if (categoriesPanel == null) {
			categoriesPanel = new CategoriesPanel(getLoaderPanel(), getTablePanel(), sourceProvider);
			categoriesPanel.initialize(viewEngine);
		}
		return categoriesPanel;
	}

	private JPanel getLoaderPanel() {
		if (loaderPanel == null) {
			loaderPanel = new GrayBackgroundedLoaderPanel();
		}
		return loaderPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getPlaylistPanel().internationalize(messages);
		getCategoriesPanel().internationalize(messages);
		getTablePanel().internationalize(messages);
		getTitleLabel().setText(messages.getMessage("hundred.title"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getPlaylistPanel().removeMessages(messages);
		getCategoriesPanel().removeMessages(messages);
		getTablePanel().removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getPlaylistPanel().setMessages(messages);
		getCategoriesPanel().setMessages(messages);
		getTablePanel().setMessages(messages);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		super.initialize(viewEngine);
		getTablePanel().initialize(viewEngine);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		super.destroy(viewEngine);
		getTablePanel().destroy(viewEngine);
		getPlaylistPanel().destroy(viewEngine);
		getCategoriesPanel().destroy(viewEngine);
	}
}
