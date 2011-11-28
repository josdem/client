package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Dimension;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.core.model.Model;
import com.all.i18n.Messages;
import com.all.shared.model.Root;
import com.all.shared.model.User;
import com.all.shared.model.Root.ContainerType;

public class TestTitleHeaderPanel extends UnitTestCase {

	@InjectMocks
	private LocalTitleHeaderPanel header = new LocalTitleHeaderPanel();
	@Mock
	private Messages messages;
	@Mock
	private ViewEngine viewEngine;

	@Test
	public void shouldCreateAHeaderPanel() throws Exception {
		header.initialize(viewEngine);
		header.initialize();
		assertEquals(new Dimension(200, 19), header.getSize());
	}

	@Test
	public void shouldGetLibraryName() throws Exception {
		Root root = mock(Root.class);
		when(viewEngine.get(Model.USER_ROOT)).thenReturn(root);
		when(root.getType()).thenReturn(ContainerType.LOCAL);
		when(messages.getMessage(anyString(), anyString())).thenReturn("");

		header.initialize(viewEngine);
		header.initialize();

		header.setMessages(messages);
		User user = new User();
		String nickName = "Alice";
		user.setNickName(nickName);
		when(viewEngine.get(Model.CURRENT_USER)).thenReturn(user);
		header.onUserSessionStarted();

		verify(messages).getMessage("headerPanel.mylibrary.label", nickName);

		User user2 = new User();
		String email = "mail@domain.com";
		user2.setEmail(email);
		when(viewEngine.get(Model.CURRENT_USER)).thenReturn(user2);

		header.onUserSessionStarted();
		verify(messages).getMessage("headerPanel.mylibrary.label", email);
	}
}
