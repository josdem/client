package com.all.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.client.services.ContactCacheService;

@Service
public class LibraryLoaderFactory {
	@Autowired
	private ContactCacheService contactCacheService;

	public OffLineLibraryLoaderController newLibraryLoaderController(String email) {
		return new OffLineLibraryLoaderController(contactCacheService, email);
		
	}

}
