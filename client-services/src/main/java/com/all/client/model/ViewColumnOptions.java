package com.all.client.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.all.core.model.DisplayableMetadataFields;

@Entity
public class ViewColumnOptions implements DisplayableMetadataFields {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private boolean artistVisible = true;
	private boolean playsVisible = true;
	private boolean ratingVisible = true;
	private boolean timeVisible = true;
	private boolean albumVisible;
	private boolean bitrateVisible;
	private boolean dateAddedVisible;
	private boolean genreVisible;
	private boolean kindVisible;
	private boolean lastPlayedVisible;
	private boolean lastSkippedVisible;
	private boolean sizeVisible;
	private boolean skipsVisible;
	private boolean yearVisible;

	private int nameWidth = 320;
	private int artistWidth = 178;
	private int albumWidth = 300;

	private String sortingColumn = "NAME";
	private boolean isAscending = true;

	public int getId() {
		return id;
	}

	@Override
	public boolean isSortAscending() {
		return isAscending;
	}

	@Override
	public void setSortAscending(boolean isSortAscending) {
		this.isAscending = isSortAscending;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean isArtistVisible() {
		return artistVisible;
	}

	@Override
	public void setArtistVisible(boolean artistVisible) {
		this.artistVisible = artistVisible;
	}

	@Override
	public boolean isPlaysVisible() {
		return playsVisible;
	}

	@Override
	public void setPlaysVisible(boolean playsVisible) {
		this.playsVisible = playsVisible;
	}

	@Override
	public boolean isRatingVisible() {
		return ratingVisible;
	}

	@Override
	public void setRatingVisible(boolean ratingVisible) {
		this.ratingVisible = ratingVisible;
	}

	@Override
	public boolean isTimeVisible() {
		return timeVisible;
	}

	@Override
	public void setTimeVisible(boolean timeVisible) {
		this.timeVisible = timeVisible;
	}

	@Override
	public boolean isAlbumVisible() {
		return albumVisible;
	}

	@Override
	public void setAlbumVisible(boolean albumVisible) {
		this.albumVisible = albumVisible;
	}

	@Override
	public boolean isBitrateVisible() {
		return bitrateVisible;
	}

	@Override
	public void setBitrateVisible(boolean bitrateVisible) {
		this.bitrateVisible = bitrateVisible;
	}

	@Override
	public boolean isDateAddedVisible() {
		return dateAddedVisible;
	}

	@Override
	public void setDateAddedVisible(boolean dateAddedVisible) {
		this.dateAddedVisible = dateAddedVisible;
	}

	@Override
	public boolean isGenreVisible() {
		return genreVisible;
	}

	@Override
	public void setGenreVisible(boolean genreVisible) {
		this.genreVisible = genreVisible;
	}

	@Override
	public boolean isKindVisible() {
		return kindVisible;
	}

	@Override
	public void setKindVisible(boolean kindVisible) {
		this.kindVisible = kindVisible;
	}

	@Override
	public boolean isLastPlayedVisible() {
		return lastPlayedVisible;
	}

	@Override
	public void setLastPlayedVisible(boolean lastPlayedVisible) {
		this.lastPlayedVisible = lastPlayedVisible;
	}

	@Override
	public boolean isLastSkippedVisible() {
		return lastSkippedVisible;
	}

	@Override
	public void setLastSkippedVisible(boolean lastSkippedVisible) {
		this.lastSkippedVisible = lastSkippedVisible;
	}

	@Override
	public boolean isSizeVisible() {
		return sizeVisible;
	}

	@Override
	public void setSizeVisible(boolean sizeVisible) {
		this.sizeVisible = sizeVisible;
	}

	@Override
	public boolean isSkipsVisible() {
		return skipsVisible;
	}

	@Override
	public void setSkipsVisible(boolean skipsVisible) {
		this.skipsVisible = skipsVisible;
	}

	@Override
	public boolean isYearVisible() {
		return yearVisible;
	}

	@Override
	public void setYearVisible(boolean yearVisible) {
		this.yearVisible = yearVisible;
	}

	@Override
	public int getNameWidth() {
		return nameWidth;
	}

	@Override
	public void setNameWidth(int nameWidth) {
		this.nameWidth = nameWidth;
	}

	@Override
	public int getArtistWidth() {
		return artistWidth;
	}

	@Override
	public void setArtistWidth(int artistWidth) {
		this.artistWidth = artistWidth;
	}

	@Override
	public int getAlbumWidth() {
		return albumWidth;
	}

	@Override
	public void setAlbumWidth(int albumWidth) {
		this.albumWidth = albumWidth;
	}

	@Override
	public String getSortingColumn() {
		return sortingColumn;
	}

	@Override
	public void setSortingColumn(String sortingColumn) {
		this.sortingColumn = sortingColumn;
	}

	@Override
	public boolean isAscending() {
		return isAscending;
	}

	@Override
	public void setAscending(boolean isAscending) {
		this.isAscending = isAscending;
	}

}
