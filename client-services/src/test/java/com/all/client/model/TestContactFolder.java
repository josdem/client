package com.all.client.model;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.all.shared.model.ContactInfo;

public class TestContactFolder {

	@Test
	public void shouldGetProperties() throws Exception {
		ContactFolder contactFolder = new ContactFolder("all.com");
		assertEquals("all.com", contactFolder.getName());
		Set<ContactInfo> contactList   = new HashSet<ContactInfo>();
		contactFolder.setContacts(contactList);
		assertEquals(contactList, contactFolder.getContacts());
	}

	@Test
	public void shouldHaveAUniqueId() throws Exception {
		String folderName = "folderName";
		int folderNum = 1000;
		Set<String> ids = new HashSet<String>(folderNum);
		for(long i=0; i<folderNum; i++ ){
			ids.add(new ContactFolder(folderName+i).getId());
		}
		assertEquals(folderNum, ids.size());
	}
	
}
