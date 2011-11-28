package com.all.client.view.contacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestContactListHeader {

	private ContactListHeaderPanel contactListHeaderPanel;

	@Test
	public void shouldCreateContactListHeaderPanel() throws Exception {
		contactListHeaderPanel = new ContactListHeaderPanel();
		assertNotNull(contactListHeaderPanel);
		assertEquals("contactListHeader", contactListHeaderPanel.getName());
		assertEquals(220, contactListHeaderPanel.getSize().getWidth(),0);
		assertEquals(88, contactListHeaderPanel.getSize().getHeight(),0);
		assertEquals(0, contactListHeaderPanel.getMinimumSize().getWidth(),0);
		assertEquals(36, contactListHeaderPanel.getMinimumSize().getHeight(),0);
	}

}
