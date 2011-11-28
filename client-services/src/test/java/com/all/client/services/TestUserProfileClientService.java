package com.all.client.services;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.appControl.control.ControlEngine;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.model.Model;
import com.all.messengine.MessEngine;
import com.all.shared.model.AllMessage;
import com.all.shared.model.Avatar;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;


@RunWith(MockInyectRunner.class)
public class TestUserProfileClientService {

	@UnderTest
	private UserProfileClientService service;
	@Mock
	private MessEngine messEngine;
	@Mock
	private ContactInfo contact;
	private Avatar avatar = new Avatar();
	private User user = new User();
	@SuppressWarnings("unused")
	@Mock
	private ContactCacheService contactCacheService;
	@SuppressWarnings("unused")
	@Mock
	private ClientReporter reporter;
	@Mock
	private ControlEngine controlEngine;

	@Before
	public void setup(){
		when(controlEngine.get(Model.CURRENT_USER)).thenReturn(user);
	}
	
	@Test
	public void shouldUpdateUserProfile() throws Exception {
		
		service.updateProfile(user);

		verify(messEngine, times(1)).send(isA(AllMessage.class));
	}
	
	@Test
	public void shouldUpdateAvatar() throws Exception {
		service.updateAvatar(avatar);
		
		verify(messEngine, times(1)).send(isA(AllMessage.class));
	}
	
	@Test
	public void shouldGetAvatar() throws Exception {
		Long id = 2L;
		when(contact.getId()).thenReturn(id);

		service.requestAvatar(contact);
		
//		verify(messEngine).send(argThat(new TextMessageMatcher(MessEngineConstants.AVATAR_REQUEST_TYPE, id.toString())));
	}
	
	@Test
	public void shouldUpdateUserQuote() throws Exception {
		String quote = "userQuote";
		user.setId(1L);
		service.updateQuote(quote);
		
//		verify(messEngine).send(argThat(new TextMessageMatcher(MessEngineConstants.UPDATE_USER_QUOTE_TYPE, quote){
//			@Override
//			public boolean matches(Object arg0) {
//				if (arg0 instanceof TextMessage) {
//					TextMessage message = (TextMessage) arg0;
//					return super.matches(arg0) && message.getProperty(MessEngineConstants.MESSAGE_SENDER).equals(id.toString());
//				} 
//				return false;
//			}
//		}));
	}
	
}
