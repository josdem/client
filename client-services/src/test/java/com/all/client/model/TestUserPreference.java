package com.all.client.model;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestUserPreference {
	UserPreference userPreference = new UserPreference();

	@Test
	public void shouldSetTwitterPostAuthorization() throws Exception {
		boolean auth = true;
		userPreference.setTwitterPostAuthorization(auth);
		
		assertTrue(userPreference.isTwitterPostAuthorized());
	}
}
