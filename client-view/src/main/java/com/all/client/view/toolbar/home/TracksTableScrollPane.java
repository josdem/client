package com.all.client.view.toolbar.home;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.music.DescriptionTableColumns;
import com.all.client.view.toolbar.hundred.HundredTrackTable;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Playlist;

public final class TracksTableScrollPane extends JScrollPane implements Internationalizable {
	private static final long serialVersionUID = 1L;
	private HundredTrackTable homeTrackTable;
	private final ViewEngine viewEngine;

	public TracksTableScrollPane(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
	}

	public void setPlaylist(Playlist playlist) {
		getHomeTrackTable().setModel(playlist.getTracks());
	}

	private void initialize() {
		this.setViewportView(getHomeTrackTable());

	}

	private HundredTrackTable getHomeTrackTable() {
		if (homeTrackTable == null) {
			List<DescriptionTableColumns> columns = new ArrayList<DescriptionTableColumns>();

			columns.add(DescriptionTableColumns.INDEX);
			columns.add(DescriptionTableColumns.NAME);
			columns.add(DescriptionTableColumns.ARTIST);
			homeTrackTable = new HundredTrackTable(columns, viewEngine, null);
		}
		return homeTrackTable;
	}

	@Override
	public void internationalize(Messages messages) {
		getHomeTrackTable().internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getHomeTrackTable().removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		getHomeTrackTable().setMessages(messages);
	}

}
