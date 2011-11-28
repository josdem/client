package com.all.client.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.all.shared.model.Track;

public class TestNamePredicate {
	
	private NamePredicate predicate;
	private Track track;

	@Test
	public void shouldEvaluate() throws Exception {	
		setTestStrings("song", "ong");
		assertTrue("Should find track with name 'ong'", predicate.evaluate(track));
	}
	
	@Test
	public void shouldNotEvaluate() throws Exception {
		setTestStrings("song", "ing");
		assertFalse("Should not find track with name 'ing'", predicate.evaluate(track));		
	}

	@SuppressWarnings("deprecation")
	private void setTestStrings(String target, String source) {
		predicate = new NamePredicate(source);
		track = LocalTrack.createEmptyTrack(target);
	}
	
	@Test
	public void shouldEvaluateCaseInsensitive() throws Exception {
		setTestStrings("SONG", "ong");
		assertTrue("Should find track with name 'ong' case insesitive", predicate.evaluate(track));
	}
	
	@Test
	public void shouldEvaluateWithSpecialCharacters() throws Exception {
		setTestStrings("canci\u00F3n", "ion");
		assertTrue("Should find track with or without tilde", predicate.evaluate(track));
		
		setTestStrings("Wie hei\u00DFen Sie?", "heissen");
		assertTrue("Should find track with sharp s", predicate.evaluate(track));

		setTestStrings("el ni\u00F1o del tambor", "nino");
		assertTrue("Should find track with enie", predicate.evaluate(track));

		setTestStrings("el nino del tambor", "ni\u00F1o");
		assertTrue("Should find predicate with enie", predicate.evaluate(track));
		
		setTestStrings("no \u00E7e que mas poner", "ce");
		assertTrue("Should find predicate with enie", predicate.evaluate(track));
	}

	@Test
	public void shouldFindWithAtSign() throws Exception {
		setTestStrings("Live@Woodstock", "LIVE WOODSTOCK");
		assertTrue("Should find predicate with @", predicate.evaluate(track));
	}
	
	@Test
	public void shouldNotFindStringAndPreventInfiniteCycling() throws Exception {
		setTestStrings("Zweistimmenstäuschung", "at");
		assertFalse("Should find predicate with at", predicate.evaluate(track));
	}

}
