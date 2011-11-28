package com.all.client.view.music;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;

import com.all.client.view.components.TableColumnStyle;

public enum DescriptionTableColumns implements TableColumnStyle<DescriptionTableStyle> {
	INDEX(null, 12, 12, 12, false), NAME("descTableColumn.name.title", 320, 70, Integer.MAX_VALUE, true), ARTIST(
			"descTableColumn.artist.title", 178, 60, Integer.MAX_VALUE, true), PLAYS("descTableColumn.plays.title", 60, 60,
			60, false), RATING("descTableColumn.rating.title", 70, 70, 70, false), TIME("descTableColumn.time.title", 70, 60,
			100, true), ALBUM("descTableColumn.album.title", 300, 70, Integer.MAX_VALUE, true), BITRATE(
			"descTableColumn.bitrate.title", 100, 70, 120, true), DATE_ADDED("descTableColumn.dateAdded.title", 120, 100,
			160, true), GENRE("descTableColumn.genre.title", 120, 70, 160, true), KIND("descTableColumn.kind.title", 60, 60,
			100, true), LAST_PLAYED("descTableColumn.lastPlayed.title", 120, 100, 160, true), LAST_SKIPPED(
			"descTableColumn.lastSkipped.title", 120, 100, 160, true), SIZE("descTableColumn.size.title", 100, 60, 150, true), SKIPS(
			"descTableColumn.skips.title", 60, 60, 60, false), YEAR("descTableColumn.year.title", 60, 60, 100, true);

	private final String name;
	private final int defaultWidth;
	private final int minWidth;
	private final int maxWidth;
	private final boolean resizable;
	private final int index;

	private DescriptionTableColumns(String name, int defaultWidth, int minWidth, int maxWidth, boolean resizable) {
		this.name = name;
		this.defaultWidth = defaultWidth;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.resizable = resizable;
		this.index = ordinal();
	}

	public TableCellRenderer getRenderer(DescriptionTableStyle style) {
		switch (this) {
		case ALBUM:
			return DescriptionTableRenderers.getAlbumRenderer(style);
		case ARTIST:
			return DescriptionTableRenderers.getArtistRenderer(style);
		case BITRATE:
			return DescriptionTableRenderers.getBitrateRenderer(style);
		case DATE_ADDED:
			return DescriptionTableRenderers.getDateAddedRenderer(style);
		case GENRE:
			return DescriptionTableRenderers.getGenreRenderer(style);
		case INDEX:
			return DescriptionTableRenderers.getIndexRenderer(style);
		case KIND:
			return DescriptionTableRenderers.getKindRenderer(style);
		case LAST_PLAYED:
			return DescriptionTableRenderers.getLastPlayedRenderer(style);
		case LAST_SKIPPED:
			return DescriptionTableRenderers.getLastSkippedRenderer(style);
		case PLAYS:
			return DescriptionTableRenderers.getPlaysRenderer(style);
		case RATING:
			return DescriptionTableRenderers.getRatingRenderer(style);
		case SKIPS:
			return DescriptionTableRenderers.getSkipsRenderer(style);
		case TIME:
			return DescriptionTableRenderers.getTimeRenderer(style);
		case YEAR:
			return DescriptionTableRenderers.getYearRenderer(style);
		case NAME:
			return DescriptionTableRenderers.getNameRenderer(style);
		case SIZE:
			return DescriptionTableRenderers.getSizeRenderer(style);
		}
		return null;
	}

	public Comparator<?> comparator(DescriptionTableStyle style) {
		switch (this) {
		case ALBUM:
			return DescriptionTableComparators.getAlbumComparator();
		case ARTIST:
			return DescriptionTableComparators.getArtistComparator();
		case BITRATE:
			return DescriptionTableComparators.getBitrateComparator();
		case DATE_ADDED:
			return DescriptionTableComparators.getDateAddedComparator();
		case GENRE:
			return DescriptionTableComparators.getGenreComparator();
		case INDEX:
			return DescriptionTableComparators.getIndexComparator(style);
		case KIND:
			return DescriptionTableComparators.getKindComparator();
		case LAST_PLAYED:
			return DescriptionTableComparators.getLastPlayedComparator();
		case LAST_SKIPPED:
			return DescriptionTableComparators.getLastSkippedComparator();
		case PLAYS:
			return DescriptionTableComparators.getPlaysComparator();
		case RATING:
			return DescriptionTableComparators.getRatingComparator();
		case SKIPS:
			return DescriptionTableComparators.getSkipsComparator();
		case TIME:
			return DescriptionTableComparators.getTimeComparator();
		case YEAR:
			return DescriptionTableComparators.getYearComparator();
		case NAME:
			return DescriptionTableComparators.getNameComparator();
		case SIZE:
			return DescriptionTableComparators.getSizeComparator();
		}
		return null;
	}

	@Override
	public int defaultWidth() {
		return getDefaultWidth();
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public String label() {
		return getName() == null ? "" : getName();
	}

	@Override
	public int maxWidth() {
		return maxWidth;
	}

	@Override
	public int minWidth() {
		return minWidth;
	}

	@Override
	public boolean resizable() {
		return resizable;
	}

	public String getName() {
		return name;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}
}
