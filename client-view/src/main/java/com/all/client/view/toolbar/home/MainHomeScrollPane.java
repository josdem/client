package com.all.client.view.toolbar.home;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class MainHomeScrollPane extends JScrollPane implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension BOTTOM_PANEL_MAXIMUM_SIZE = new Dimension(1002, 230);

	private static final Dimension BOTTOM_PANEL_MINIMUM_SIZE = new Dimension(802, 138);

	private static final Dimension CONTAINER_PANEL_MAXIMUM_SIZE = new Dimension(1426, 656);

	private static final Dimension CONTAINER_PANEL_MINIMUM_SIZE = new Dimension(1010, 564);

	private static final Insets BOTTOM_PANEL_INSETS = new Insets(8, 8, 8, 0);

	private static final Insets ITUNES_IMPORT_PANEL_INSETS = new Insets(0, 0, 0, 4);

	private static final Insets MAIN_IMAGES_PANEL_INSETS = new Insets(8, 8, 0, 0);

	private static final Insets TIPS_PANEL_INSETS = new Insets(8, 0, 0, 0);

	private static final Insets TOP_PLAYLIST_PANEL_INSETS = new Insets(8, 8, 8, 8);

	private static final Insets WIZARD_PANEL_INSETS = new Insets(0, 4, 0, 0);

	private static final String MAIN_PANEL_NAME = "homeBackgroundPanel";

	private JPanel containerPanel;

	private WizardPanel wizardPanel;

	private TopPlaylistPanel topPlaylistPanel;

	private TipsPanel tipsPanel;

	private MainImagesPanel mainImagesPanel;

	private JPanel mainPanel;

	private ItunesImportPanel itunesImportPanel;

	private JPanel bottomPanel;

	private final DialogFactory dialogFactory;

	private final ViewEngine viewEngine;

	public MainHomeScrollPane(DialogFactory dialogFactory, ViewEngine viewEngine) {
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setViewportView(getMainPanel());
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			mainPanel.setName(MAIN_PANEL_NAME);
			mainPanel.add(getContainerPanel());
		}
		return mainPanel;
	}

	private JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel();
			containerPanel.setLayout(new GridBagLayout());
			containerPanel.setMaximumSize(CONTAINER_PANEL_MAXIMUM_SIZE);
			containerPanel.setMinimumSize(CONTAINER_PANEL_MINIMUM_SIZE);

			GridBagConstraints mainImagesPanelConstraints = new GridBagConstraints();
			mainImagesPanelConstraints.gridx = 0;
			mainImagesPanelConstraints.gridy = 0;
			mainImagesPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			mainImagesPanelConstraints.weightx = 1;
			mainImagesPanelConstraints.insets = MAIN_IMAGES_PANEL_INSETS;

			containerPanel.add(getMainImagesPanel(), mainImagesPanelConstraints);

			GridBagConstraints tipsPanelConstraints = new GridBagConstraints();
			tipsPanelConstraints.gridx = 1;
			tipsPanelConstraints.gridy = 0;
			tipsPanelConstraints.fill = GridBagConstraints.NONE;
			tipsPanelConstraints.insets = TIPS_PANEL_INSETS;
			containerPanel.add(getTipsPanel(), tipsPanelConstraints);

			GridBagConstraints topPlaylistConstraints = new GridBagConstraints();
			topPlaylistConstraints.gridx = 2;
			topPlaylistConstraints.gridy = 0;
			topPlaylistConstraints.fill = GridBagConstraints.BOTH;
			topPlaylistConstraints.gridheight = 2;
			topPlaylistConstraints.gridwidth = 1;
			topPlaylistConstraints.weightx = .98;
			topPlaylistConstraints.weighty = 1;
			topPlaylistConstraints.insets = TOP_PLAYLIST_PANEL_INSETS;
			containerPanel.add(getTopPlaylistPanel(), topPlaylistConstraints);

			GridBagConstraints bottompanelConstraints = new GridBagConstraints();
			bottompanelConstraints.gridx = 0;
			bottompanelConstraints.gridy = 1;
			bottompanelConstraints.fill = GridBagConstraints.BOTH;
			bottompanelConstraints.insets = BOTTOM_PANEL_INSETS;
			bottompanelConstraints.gridwidth = 2;
			bottompanelConstraints.weightx = 1;
			bottompanelConstraints.weighty = 1;
			containerPanel.add(getBottomPanel(), bottompanelConstraints);
		}
		return containerPanel;
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(new GridBagLayout());
			bottomPanel.setPreferredSize(BOTTOM_PANEL_MINIMUM_SIZE);
			bottomPanel.setMinimumSize(BOTTOM_PANEL_MINIMUM_SIZE);
			bottomPanel.setMaximumSize(BOTTOM_PANEL_MAXIMUM_SIZE);

			GridBagConstraints itunesImportConstraints = new GridBagConstraints();
			itunesImportConstraints.gridx = 0;
			itunesImportConstraints.gridy = 1;
			itunesImportConstraints.fill = GridBagConstraints.BOTH;
			itunesImportConstraints.weightx = .5;
			itunesImportConstraints.weighty = .5;
			itunesImportConstraints.insets = ITUNES_IMPORT_PANEL_INSETS;
			bottomPanel.add(getItunesImportPanel(), itunesImportConstraints);

			GridBagConstraints wizardConstraints = new GridBagConstraints();
			wizardConstraints.gridx = 1;
			wizardConstraints.gridy = 1;
			wizardConstraints.fill = GridBagConstraints.BOTH;
			wizardConstraints.weightx = .5;
			wizardConstraints.weighty = .5;
			wizardConstraints.insets = WIZARD_PANEL_INSETS;
			bottomPanel.add(getWizardPanel(), wizardConstraints);
		}
		return bottomPanel;
	}

	private MainImagesPanel getMainImagesPanel() {
		if (mainImagesPanel == null) {
			mainImagesPanel = new MainImagesPanel();
		}
		return mainImagesPanel;
	}

	private TipsPanel getTipsPanel() {
		if (tipsPanel == null) {
			tipsPanel = new TipsPanel();
		}
		return tipsPanel;
	}

	private TopPlaylistPanel getTopPlaylistPanel() {
		if (topPlaylistPanel == null) {
			topPlaylistPanel = new TopPlaylistPanel();
		}
		return topPlaylistPanel;
	}

	private WizardPanel getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new WizardPanel(dialogFactory, viewEngine);
		}
		return wizardPanel;
	}

	private JScrollPane getItunesImportPanel() {
		if (itunesImportPanel == null) {
			itunesImportPanel = new ItunesImportPanel(dialogFactory);
		}
		return itunesImportPanel;
	}

	@Override
	public void internationalize(Messages messages) {
		itunesImportPanel.internationalize(messages);
		getMainImagesPanel().internationalize(messages);
		getTipsPanel().internationalize(messages);
		getWizardPanel().internationalize(messages);
		getTopPlaylistPanel().internationalize(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		itunesImportPanel.setMessages(messages);
		getMainImagesPanel().setMessages(messages);
		getTipsPanel().setMessages(messages);
		getWizardPanel().setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		itunesImportPanel.removeMessages(messages);
		getMainImagesPanel().removeMessages(messages);
		getTipsPanel().remove(this);
		getWizardPanel().remove(this);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		itunesImportPanel.initialize(viewEngine);
		topPlaylistPanel.initialize(viewEngine);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		itunesImportPanel.destroy(viewEngine);
		topPlaylistPanel.destroy(viewEngine);
	}
}
