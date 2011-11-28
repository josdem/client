package com.all.client.model;

import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.Predicate;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TestGenrePredicate {
	LocalTrack track = LocalTrack.createEmptyTrack();
	private Predicate predicate;
	
	private Predicate initPredicate(String genre, String pattern) {
		track.setGenre(genre);
		predicate = new GenrePredicate(pattern);
		return predicate;
	}

	@Test
	public void shouldEvaluateByAlbumName() throws Exception {
		Predicate predicate = initPredicate("One", "o");
		assertTrue(predicate.evaluate(track));
	}
	
	@Test
	public void shouldEvaluateByAlbumWithSpecialCharacters() throws Exception {
		Predicate predicate = initPredicate("O\u00F1e", "on");
		assertTrue(predicate.evaluate(track));
	}
}
