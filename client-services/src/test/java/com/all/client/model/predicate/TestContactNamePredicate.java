package com.all.client.model.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.all.shared.model.ContactInfo;


@SuppressWarnings("deprecation")
public class TestContactNamePredicate {
	
	ContactInfo contact = new ContactInfo();
	private ContactNamePredicate predicate;
	
	@Before
	public void setup(){
		contact.setNickName("Rodrigo Ramos");
	}
	
	@Test
	public void shouldNotEvaluateWhenLetterIsNotInContactName() throws Exception {
		predicate = new ContactNamePredicate("z");
		assertFalse(predicate.evaluate(contact));
	}
	
	
	@Test
	public void shouldEvaluateWhenLetterIsContactName() throws Exception {
		predicate = new ContactNamePredicate("r");
		assertTrue(predicate.evaluate(contact));
	}
}
