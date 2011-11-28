package com.all.client.model;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.all.shared.model.Folder;
import com.all.shared.model.Root;
import com.all.shared.model.Root.ContainerType;

public class TestTopContainer {

	private Root containerAdapter;

	@Before
	public void setup() {
		containerAdapter = new MockRoot(ContainerType.CONTACT);
	}

	@Test
	public void shouldRootAddAFolder() throws Exception {
		@SuppressWarnings("deprecation")
		Folder folder = new LocalFolder();
		containerAdapter.add(folder);
		assertFalse(containerAdapter.isEmpty(Folder.class));
	}

}
