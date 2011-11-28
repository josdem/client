package com.all.client.controller;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.client.services.ContactCacheService;
import com.all.client.util.ReflectionUtilities;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestLibraryLoaderFactory {
	@UnderTest
	private LibraryLoaderFactory libraryFactory;
	@Mock
	private ContactCacheService contactCacheService;
	
	@Test
	public void shouldLoadLibrary() throws Exception {
		String email = "name@server";
		OffLineLibraryLoaderController loaderController = libraryFactory.newLibraryLoaderController(email);
		assertEquals(email, ReflectionUtilities.getPrivateField(loaderController, "email"));
		assertEquals(contactCacheService, ReflectionUtilities.getPrivateField(loaderController, "contactCacheService"));
	}

}
