package com.all.client.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;
import com.all.shared.model.Gender;

public class TestContact {
	private static final String name = "Rosario";
	private static final String message = "hola";
	private static final String email = "mail@all.com";
	@SuppressWarnings("deprecation")
	private ContactInfo contact = new ContactInfo();
//name, message, mail, ContactStatus.online, Gender.FEMALE
	
	@Before
	public void setup() {
		contact.setName(name);	
		contact.setMessage(message);
		contact.setEmail(email);
		contact.setStatus(ContactStatus.online);
		contact.setGender(Gender.FEMALE);
		contact.setNickName(name);
	}
	
	@Test
	public void shouldGetSimpleProperties() throws Exception {
		assertEquals(name, contact.getName());
		assertEquals(message, contact.getMessage());
		assertEquals(email, contact.getEmail());
		assertEquals(ContactStatus.online, contact.getStatus());
		assertEquals(Gender.FEMALE, contact.getGender());
	}

	@Test
	public void shouldGetTooltipForContact() throws Exception {
		assertEquals(name + " - " + message, contact.getTooltipText());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldGetTooltipForPendingContact() throws Exception {
		ContactInfo contact = new ContactInfo();
		contact.setEmail(email);
		contact.setStatus(ContactStatus.pending);
		assertEquals(email, contact.getTooltipText());
	}
}
