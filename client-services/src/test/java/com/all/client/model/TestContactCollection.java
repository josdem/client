package com.all.client.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.all.core.model.ContactCollection;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;

public class TestContactCollection {
	@SuppressWarnings("deprecation")
	ContactInfo contactA = new ContactInfo();
	@SuppressWarnings("deprecation")
	ContactInfo contactB = new ContactInfo();
	@SuppressWarnings("deprecation")
	ContactInfo contactC = new ContactInfo();

	@Before
	public void setup(){
		contactA.setId(1L);
		contactB.setId(2L);
		contactC.setId(3L);
		contactC.setStatus(ContactStatus.pending);
	}
	
	@Test
	public void shouldCreateAContactCollectionWithOfflineOnlineContacts() throws Exception {
		ContactCollection contacts = new ContactCollection(contactA, contactB);
		assertEquals(2, contacts.getContacts().size());
		assertEquals(0, contacts.getPendingContacts().size());
	}

	@Test
	public void testname() throws Exception {
		ContactCollection contacts = new ContactCollection(contactA, contactB, contactC);
		assertEquals(2, contacts.getContacts().size());
		assertEquals(1, contacts.getPendingContacts().size());
	}
	
	@Test
	public void shouldCleanUpContactCollection() throws Exception {
		ContactCollection contacts = new ContactCollection(contactA, contactB, contactC);
		assertEquals(2, contacts.getContacts().size());
		assertEquals(1, contacts.getPendingContacts().size());
		
		contacts.cleanUp();
		
		assertEquals(2, contacts.getContacts().size());
		assertEquals(0, contacts.getPendingContacts().size());
	}
	
}
