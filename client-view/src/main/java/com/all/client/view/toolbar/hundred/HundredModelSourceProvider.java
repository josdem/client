package com.all.client.view.toolbar.hundred;

import com.all.shared.model.Category;
import com.all.shared.model.ModelSource;
import com.all.shared.model.ModelSourceProvider;
import com.all.shared.model.Playlist;

public class HundredModelSourceProvider implements ModelSourceProvider {

	private Category category;
	private Playlist playlist;

	@Override
	public ModelSource getSource() {
		return ModelSource.topHundred(category, playlist);
	}

	public void setCategory(Category category) {
		this.playlist = null;
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}
}
