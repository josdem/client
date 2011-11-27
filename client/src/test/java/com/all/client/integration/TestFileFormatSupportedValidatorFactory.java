package com.all.client.integration;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.all.client.itunes.UnableReadMetadataException;
import com.all.client.model.FileExtension;
import com.all.client.model.FileFormatSupportedValidatorFactory;
import com.all.client.model.format.FileFormatSupportedValidator;

public class TestFileFormatSupportedValidatorFactory {

	FileFormatSupportedValidatorFactory factory = new FileFormatSupportedValidatorFactory();

	@Test
	public void shouldDetectM4pDrm() throws Exception {
		String f1 = "src/test/resources/drmTest/10 Little Boxes.m4p";
		String f2 = "src/test/resources/drmTest/10 Little Boxes.M4P";
		FileFormatSupportedValidator v1 = this.factory.createValidator(new File(f1));
		FileFormatSupportedValidator v2 = this.factory.createValidator(new File(f2));
		assertTrue(v1.isDrmProtected());
		assertTrue(v2.isDrmProtected());
	}
	
	@Test
	public void shouldDetectMp4() throws Exception {
		String f1 = "src/test/resources/drmTest/10 Little Boxes.mp4";
		String f2 = "src/test/resources/drmTest/10 Little Boxes.MP4";
		FileFormatSupportedValidator v1 = this.factory.createValidator(new File(f1));
		FileFormatSupportedValidator v2 = this.factory.createValidator(new File(f2));
		assertTrue(v1.isDrmProtected());
		assertTrue(v2.isDrmProtected());
	}
	
	@Test
	public void shouldDetectIfAFileIsDrmProtected() throws UnableReadMetadataException {
		for (int i = 0; i < FileExtension.values().length; i++) {
			String fileName = "src/test/resources/drmTest/theOne";
			String extension = FileExtension.values()[i].toString();
			String fullName1 = fileName + "." + extension;
			String fullName2 = fileName + "." + extension.toLowerCase();
			FileFormatSupportedValidator v1 = this.factory.createValidator(new File(fullName1));
			FileFormatSupportedValidator v2 = this.factory.createValidator(new File(fullName2));
			boolean value1 = v1.isDrmProtected();
			boolean value2 = v2.isDrmProtected();
			switch (FileExtension.values()[i]) {
			case M4P:
				assertTrue(value1);
				assertTrue(value2);
				break;
			case MP3:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case M4A:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case OGG:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case AIFF:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case WMA:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case WAV:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case AU:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case M4B:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case MP2:
				assertTrue(!value1);
				assertTrue(!value2);
				break;
			case MP4:
				assertTrue(!value2);
				break;
			}
		}
	}

	@Test
	public void shouldNotifyIfCantDetermineDrmProtection() throws UnableReadMetadataException {
		String fileName = "aFileName";
		String extension = "unknowExtension";
		String f1 = fileName + "." + extension;
		String f2 = fileName + "." + extension.toLowerCase();
		FileFormatSupportedValidator v1 = this.factory.createValidator(new File(f1));
		FileFormatSupportedValidator v2 = this.factory.createValidator(new File(f2));
		assertNull(v1);
		assertNull(v2);
	}
}
