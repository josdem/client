package com.all.client.config;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.all.core.common.services.ApplicationConfig;


public class TestApplicationConfig {
	@InjectMocks
	private ApplicationConfig applicationConfig = new ApplicationConfig();
	
	@Before
	public void before() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldGetMusicDirectory() throws Exception {
		assertTrue(applicationConfig.getMusicLibraryDirectory().contains("Music"));
		assertTrue(applicationConfig.getMusicLibraryDirectory().contains("All"));
	}
	
	@Test
	public void shouldGetDownloadDirectory() throws Exception {
		assertTrue(applicationConfig.getDownloadsDirectory().contains("Music"));
		assertTrue(applicationConfig.getDownloadsDirectory().contains("All"));
		assertTrue(applicationConfig.getDownloadsDirectory().contains("Downloads"));
	}
}
