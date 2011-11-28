package com.all.client.view.util;

import com.all.client.view.music.DescriptionTableColumns;
import com.all.core.model.DisplayableMetadataFields;

public class ViewColumnOptionsConverter {
	public static int getWidth(DisplayableMetadataFields options, DescriptionTableColumns col) {
		switch (col) {
		case NAME:
			return options.getNameWidth();
		case ARTIST:
			return options.getArtistWidth();
		case ALBUM:
			return options.getAlbumWidth();
		default:
			return col.getDefaultWidth();
		}
	}

	public static void setWidth(DisplayableMetadataFields options, DescriptionTableColumns col, int width) {
		switch (col) {
		case NAME:
			options.setNameWidth(width);
			break;
		case ARTIST:
			options.setArtistWidth(width);
			break;
		case ALBUM:
			options.setAlbumWidth(width);
			break;
		}
	}

	public static boolean isVisible(DisplayableMetadataFields options, DescriptionTableColumns col) {
		switch (col) {
		case ALBUM:
			return options.isAlbumVisible();
		case BITRATE:
			return options.isBitrateVisible();
		case DATE_ADDED:
			return options.isDateAddedVisible();
		case ARTIST:
			return options.isArtistVisible();
		case GENRE:
			return options.isGenreVisible();
		case INDEX:
			return true;
		case KIND:
			return options.isKindVisible();
		case LAST_PLAYED:
			return options.isLastPlayedVisible();
		case LAST_SKIPPED:
			return options.isLastSkippedVisible();
		case NAME:
			return true;
		case PLAYS:
			return options.isPlaysVisible();
		case RATING:
			return options.isRatingVisible();
		case SIZE:
			return options.isSizeVisible();
		case SKIPS:
			return options.isSkipsVisible();
		case TIME:
			return options.isTimeVisible();
		case YEAR:
			return options.isYearVisible();
		}
		return false;
	}

	public static void setVisible(DisplayableMetadataFields options, DescriptionTableColumns col, boolean visible) {
		switch (col) {
		case ALBUM:
			options.setAlbumVisible(visible);
			break;
		case BITRATE:
			options.setBitrateVisible(visible);
			break;
		case DATE_ADDED:
			options.setDateAddedVisible(visible);
			break;
		case ARTIST:
			options.setArtistVisible(visible);
			break;
		case GENRE:
			options.setGenreVisible(visible);
			break;
		case KIND:
			options.setKindVisible(visible);
			break;
		case LAST_PLAYED:
			options.setLastPlayedVisible(visible);
			break;
		case LAST_SKIPPED:
			options.setLastSkippedVisible(visible);
			break;
		case PLAYS:
			options.setPlaysVisible(visible);
			break;
		case RATING:
			options.setRatingVisible(visible);
			break;
		case SIZE:
			options.setSizeVisible(visible);
			break;
		case SKIPS:
			options.setSkipsVisible(visible);
			break;
		case TIME:
			options.setTimeVisible(visible);
			break;
		case YEAR:
			options.setYearVisible(visible);
			break;
		}
	}

	public static DescriptionTableColumns getSortingColumn(DisplayableMetadataFields options) {
		DescriptionTableColumns column = DescriptionTableColumns.valueOf(options.getSortingColumn());
		return column == null ? DescriptionTableColumns.NAME : column;
	}

	public static void setSortingColumn(DisplayableMetadataFields options, DescriptionTableColumns columnOption) {
		options.setSortingColumn(columnOption.name());
	}

}
