package com.all.client.services;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalModelDao;
import com.all.client.model.UserPreference;
import com.all.core.model.Model;


public class TestUserPreferenceService {
	@InjectMocks
	private UserPreferenceService userPreferenceService;
	@Mock
	private UserPreference userPreference;
	@Mock
	private ControlEngine controlEngine;
	private LocalModelDao dao;
	
	@Before
	public void setup() throws Exception {
		dao = mock(LocalModelDao.class); 
		userPreferenceService = new UserPreferenceService(dao);
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldSetFacebookChatStatus() throws Exception {
		boolean rememberMe = true;
		userPreferenceService.setFacebookChatStatus(rememberMe);
		verify(userPreference).setFacebookChatStatus(rememberMe);
		verify(dao).update(userPreference);
		verify(controlEngine).set(Model.UserPreference.FACEBOOK_CHAT_STATUS, rememberMe, null);
	}
	
	@Test
	public void shouldGetFacebookChatStatus() throws Exception {
		userPreferenceService.isFacebookChatLogin();
		verify(userPreference).isFacebookChatLogin();
	}
	
	@Test
	public void shouldSetPostAuthorization() throws Exception {
		boolean auth = true;
		
		userPreferenceService.setPostAuthorization(auth);
		
		verify(userPreference).setTwitterPostAuthorization(auth);
	}
	
	@Test
	public void shouldKnowIfPostAuthorized() throws Exception {
		when(userPreference.isTwitterPostAuthorized()).thenReturn(true);
		
		assertTrue(userPreferenceService.isPostTwitterAuthorized());
	}
	
}
