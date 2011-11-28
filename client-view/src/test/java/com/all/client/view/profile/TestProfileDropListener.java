package com.all.client.view.profile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.SimpleDraggedObject;
import com.all.client.view.toolbar.social.ProfileDropListener;
import com.all.core.model.ContactCollection;
import com.all.shared.model.ContactInfo;

public class TestProfileDropListener {

	ProfileDropListener profileDropListener;
	@Mock
	ViewEngine viewEngine;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		profileDropListener = new ProfileDropListener(viewEngine);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldDrop() throws Exception {
		ContactInfo contact = new ContactInfo();
		ContactCollection contacts = new ContactCollection(contact);

		profileDropListener.doDrop(new SimpleDraggedObject(contacts), new Point());

		// verify(profileState).setProfile(contact);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldOnlyAcceptOneContact() throws Exception {

		ContactInfo contact = new ContactInfo();
		ContactCollection contacts = new ContactCollection(contact);

		assertTrue(profileDropListener.validateDrop(new SimpleDraggedObject(contacts), new Point()));

		contacts.add(new ContactInfo());
		assertFalse(profileDropListener.validateDrop(new SimpleDraggedObject(contacts), new Point()));
	}

}
