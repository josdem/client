package com.all.client.view.contacts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;


@SuppressWarnings("deprecation")
public class TestContactTree {
	@InjectMocks
	private ContactTree contactTree = new ContactTree();
	private boolean showPendingEmails;
	private boolean expandNodes;
	private List<ContactInfo> contacts;
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		contactTree.testing = true;
	}
	
	@Test
	public void shouldSetModel() throws Exception {
		contacts = new ArrayList<ContactInfo>();
		
		setPendingContacts();
		setActiveContacts();
		
		contactTree.setModel(contacts, showPendingEmails , expandNodes);
		
		assertEquals(2, contactTree.getPending().getChildCount());
		assertEquals(2, contactTree.getOffline().getChildCount());
	}

	private void setPendingContacts() {
		ContactInfo pendingContact1 = new ContactInfo();
		pendingContact1.setEmail("nelly@all.com");
		pendingContact1.setStatus(ContactStatus.pending);
		assertTrue(pendingContact1.isPending());
		ContactInfo pendingContact2 = new ContactInfo();
		pendingContact2.setEmail("herenians@all.com");
		pendingContact2.setStatus(ContactStatus.pending);
		assertTrue(pendingContact2.isPending());
		
		contacts.add(pendingContact1);
		contacts.add(pendingContact2);
	}
	
	private void setActiveContacts() {
		ContactInfo pendingContact1 = new ContactInfo();
		pendingContact1.setNickName("josdem");
		pendingContact1.setEmail("josdem@all.com");
		ContactInfo pendingContact2 = new ContactInfo();
		pendingContact2.setNickName("nanny");
		pendingContact2.setEmail("nanny@all.com");
		
		contacts.add(pendingContact1);
		contacts.add(pendingContact2);
	}
}
