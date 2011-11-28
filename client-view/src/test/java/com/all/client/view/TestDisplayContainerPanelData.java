package com.all.client.view;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.JButton;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.client.util.ReflectionUtilities;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public class TestDisplayContainerPanelData extends UnitTestCase {
	@InjectMocks
	private DisplayContainerPanel displayContainerPanel = new DisplayContainerPanel();
	@Mock
	ContactInfo requester;
	@Mock
	ContactInfo requested;
	@Mock
	Messages messages;
	@Mock
	ViewEngine viewEngine;

	@Before
	public void init() {
		when(messages.getMessage("tooltip.pulseDisabled")).thenReturn("Connect to the Internet to access messages.");
		when(messages.getMessage("tooltip.pulseOff")).thenReturn("Check Pulse Messages");
		when(messages.getMessage("tooltip.pulseOn")).thenReturn("You Have a New Message");
	}

	@Test
	public void shouldRemoveMessages() throws Exception {
		displayContainerPanel.removeMessages(messages);
		verify(messages).remove(displayContainerPanel);
	}

	@Test
	public void shouldShowAlertsDialog() throws Exception {

		JButton pulseBubbleButton = (JButton) ReflectionUtilities.invokePrivateMethod(displayContainerPanel, "getPulseBubbleButton");
		pulseBubbleButton.doClick();

		assertTrue(pulseBubbleButton.getName().endsWith("pulseBubbleButton"));
		verify(viewEngine).send(Actions.View.TOGGLE_DRAWER);
	}

}
