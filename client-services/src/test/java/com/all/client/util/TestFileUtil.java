package com.all.client.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TestFileUtil {

	private static final String FILE_ADDRESS_INPUT = "p2p/ultrapeerAdressTest";
	private static final String FILE_ADDRESS_OUTPUT = "ultrapeerAdressWriteTest";

	FileUtil fileUtil = new FileUtil();

	@Before
	public void init() {
		cleanFile();
	}

	@After
	public void clean() {
		cleanFile();
	}

	public void cleanFile() {
		File file = new File(FILE_ADDRESS_OUTPUT);
		if (file.exists()) {
			file.delete(); 
		}
	}

	@Test
	public void shouldGetExtension() throws Exception {
		String r = FileUtil.getExtension(new File("src/test/resources/audio/TestSong1.mp3"));
		assertEquals("MP3", r);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowException(){
		FileUtil.getExtension(null);
	}
	
	@Test
	public void shouldReturnNullForNullFileNames(){
		File f = Mockito.mock(File.class);
//		assertNull(fileUtil.getExtension(f));
		assertEquals("",FileUtil.getExtension(f));
	}
	
	@Test
	public void shouldReturnNullForFileWithNonExtension(){
		String fileName = "withoutExtension";
		String r = FileUtil.getExtension(new File(fileName));
		assertEquals(fileName,r);
	}


	@Test
	public void shouldReturnNullForFileWithEmptyString(){
		String r = FileUtil.getExtension(new File(""));
		assertEquals("",r);
	}
	
	@Test
	public void shouldReturnEmptyStringForFileWithEmptyStringExtension(){
		String r = FileUtil.getExtension(new File("hola."));
		assertEquals("",r);
	}

	@Test
	public void shouldLoadFileIntoList() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add("192.168.1.77");
		expected.add("192.168.1.55");
		expected.add("192.168.1.190");

		List<String> list = FileUtil.readLinesToList(FILE_ADDRESS_INPUT);
		assertEquals(expected, list);
	}

	@Test
	public void shouldWriteLinestoFile() throws Exception {
		List<String> mergedList = new ArrayList<String>();
		mergedList.add("192.168.1.77");
		mergedList.add("192.168.1.55");
		mergedList.add("192.168.1.190");
		mergedList.add("192.168.1.90");
		mergedList.add("192.168.1.99");
		mergedList.add("192.168.1.111");

		assertFalse(new File(FILE_ADDRESS_OUTPUT).exists());

		fileUtil.writeLinesToFile(mergedList, FILE_ADDRESS_OUTPUT);

		assertTrue(new File(FILE_ADDRESS_OUTPUT).exists());

		List<String> result = FileUtil.readLinesToList(FILE_ADDRESS_OUTPUT);
		assertEquals(mergedList, result);
	}
}
