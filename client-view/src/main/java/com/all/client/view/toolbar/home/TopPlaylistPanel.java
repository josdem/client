package com.all.client.view.toolbar.home;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.all.action.SwingResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.client.view.music.DescriptionTableColumns;
import com.all.client.view.toolbar.hundred.HundredModelSourceProvider;
import com.all.client.view.toolbar.hundred.HundredTablePanel;
import com.all.core.actions.Actions;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Playlist;

public final class TopPlaylistPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Border BORDER = BorderFactory.createEmptyBorder(0, 1, 1, 1);

	private static final Dimension MAXIMUM_SIZE = new Dimension(400, 640);

	private static final Dimension MINIMUM_SIZE = new Dimension(184, 548);

	private static final String NAME = "topPlaylistPanelBackground";

	private HundredTablePanel hundredTablePanel;

	private ViewEngine viewEngine;

	public TopPlaylistPanel() {
	}

	private void initialize() {
		this.setPreferredSize(MINIMUM_SIZE);
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setLayout(new BorderLayout());
		this.add(getHundredTablePanel(), BorderLayout.CENTER);
		this.setName(NAME);
		this.setBorder(BORDER);
	}

	private HundredTablePanel getHundredTablePanel() {
		if (hundredTablePanel == null) {
			List<DescriptionTableColumns> columns = new ArrayList<DescriptionTableColumns>();
			columns.add(DescriptionTableColumns.INDEX);
			columns.add(DescriptionTableColumns.NAME);
			hundredTablePanel = new HundredTablePanel(columns, new HundredModelSourceProvider());
			hundredTablePanel.initialize(viewEngine);
			hundredTablePanel.getTopPanel().setName(NAME);
		}
		return hundredTablePanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getHundredTablePanel().internationalize(messages);
		getHundredTablePanel().getDownloadLabel().setText("");
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getHundredTablePanel().removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getHundredTablePanel().setMessages(messages);

	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
		viewEngine.request(Actions.TopHundred.GET_RANDOM_PLAYLIST, null, new SwingResponseCallback<Playlist>() {
			@Override
			public void updateGui(Playlist playlist) {
				getHundredTablePanel().setPlaylist(playlist);
			}
		});
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		getHundredTablePanel().destroy(viewEngine);
	}
}
