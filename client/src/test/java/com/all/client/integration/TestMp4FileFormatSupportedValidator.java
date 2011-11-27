package com.all.client.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.all.client.model.FileFormatSupportedValidatorFactory;
import com.all.client.model.format.FileFormatSupportedValidator;

public class TestMp4FileFormatSupportedValidator {

	FileFormatSupportedValidatorFactory factory;
	
	@Before
	public void setUp(){
		this.factory = new FileFormatSupportedValidatorFactory();
	}
	
	@Test
	public void shouldValidateDrmProtectedAudioM4pFile(){
		File file = new File("src/test/resources/drmTest/10 Little Boxes.m4p");
		FileFormatSupportedValidator v = factory.createValidator(file);
		assertTrue(v.isDrmProtected());
		assertTrue(v.isAudioFile());
		assertFalse(v.isAllowedToBeImportedByBusinessRule());//TODO edgar checar esto
	}

	@Test
	public void shouldValidateDrmProtectedAudioMp4File(){
		File file = new File("src/test/resources/drmTest/10 Little Boxes.mp4");
		FileFormatSupportedValidator v = factory.createValidator(file);
		assertTrue(v.isDrmProtected());
		assertTrue(v.isAudioFile());
		assertFalse(v.isAllowedToBeImportedByBusinessRule());
	}
	
	@Test
	public void shouldValidateNonDrmProtectedAudioM4aFile(){
		File file = new File("src/test/resources/drmTest/theOne.m4a");
		FileFormatSupportedValidator v = factory.createValidator(file);
		assertFalse(v.isDrmProtected());
		assertTrue(v.isAudioFile());
		assertTrue(v.isAllowedToBeImportedByBusinessRule());
	}

	@Test
	public void shouldValidateNonDrmProtectedAudioMp4File(){
		File file = new File("src/test/resources/drmTest/theOne.mp4");
		FileFormatSupportedValidator v = factory.createValidator(file);
		assertFalse(v.isDrmProtected());
		assertTrue(v.isAudioFile());
		assertFalse(v.isAllowedToBeImportedByBusinessRule());
	}
}
