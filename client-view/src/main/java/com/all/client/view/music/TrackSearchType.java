package com.all.client.view.music;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.OrPredicate;

import com.all.client.model.AlbumPredicate;
import com.all.client.model.ArtistPredicate;
import com.all.client.model.GenrePredicate;
import com.all.client.model.NamePredicate;
import com.all.client.util.PredicateUtil;

public enum TrackSearchType {
	ALL("popup.criteria.all", 0), 
	ALBUM("popup.criteria.album", 1), 
	ARTIST("popup.criteria.artist", 2), 
	GENRE("popup.criteria.genre", 3), 
	NAME("popup.criteria.name", 4);
	
	private final String nameKey;
	private final int order;
	
	private TrackSearchType(String key, int order){
		this.nameKey = key;
		this.order = order;
	}
	
	public String getNameKey() {
		return nameKey;
	}
	
	public int getOrder() {
		return order;
	}

	public Predicate getPredicate(String text) {
		switch (this) {
		case ALBUM:
			return getAlbumPredicate(text);
		case ALL:
			return getAllPredicate(text);
		case ARTIST:
			return getArtistPredicate(text);
		case GENRE:
			return getGenrePredicate(text);
		case NAME:
			return getNamePredicate(text);
		}
		return null;
	}

	private static Predicate mergePredicates(List<Predicate> predicates) {
		return PredicateUtil.mergeAndPredicate(predicates);
	}

	private static Predicate getArtistPredicate(String artist) {
		String[] artists = artist.split(" ");
		List<Predicate> artistPredicates = new ArrayList<Predicate>();

		for (String artistSplitted : artists) {
			artistPredicates.add(new ArtistPredicate(artistSplitted));
		}

		return mergePredicates(artistPredicates);
	}

	private static Predicate getAlbumPredicate(String album) {
		String[] albums = album.split(" ");
		List<Predicate> albumPredicates = new ArrayList<Predicate>();

		for (String albumSplitted : albums) {
			albumPredicates.add(new AlbumPredicate(albumSplitted));
		}

		return mergePredicates(albumPredicates);
	}

	private static Predicate getGenrePredicate(String genre) {
		String[] genres = genre.split(" ");
		List<Predicate> genresPredicates = new ArrayList<Predicate>();

		for (String genreSplitted : genres) {
			genresPredicates.add(new GenrePredicate(genreSplitted));
		}

		return mergePredicates(genresPredicates);
	}

	private static Predicate getNamePredicate(String name) {
		String[] names = name.split(" ");
		List<Predicate> namePredicates = new ArrayList<Predicate>();

		for (String nameSplitted : names) {
			namePredicates.add(new NamePredicate(nameSplitted));
		}

		return mergePredicates(namePredicates);
	}

	private static Predicate getAllPredicate(String keyword) {
		String[] keywords = keyword.split(" ");
		List<Predicate> allPredicates = new ArrayList<Predicate>();

		for (String keywordSplitted : keywords) {
			Predicate namePredicate = new NamePredicate(keywordSplitted);
			Predicate artistPredicate = new ArtistPredicate(keywordSplitted);
			Predicate albumPredicate = new AlbumPredicate(keywordSplitted);
			Predicate genrePredicate = new GenrePredicate(keywordSplitted);
			Predicate allPredicate = new OrPredicate(new OrPredicate(new OrPredicate(namePredicate, artistPredicate),
					albumPredicate), genrePredicate);

			allPredicates.add(allPredicate);
		}

		return mergePredicates(allPredicates);
	}

}
