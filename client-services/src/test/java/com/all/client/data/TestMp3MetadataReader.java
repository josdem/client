package com.all.client.data;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;


public class TestMp3MetadataReader extends UnitTestCase {
	private Mp3MetadataReader reader;
	@Mock
	private MP3File audioFile;
	@Mock
	private Tag tag;
	
	@Before
	public void setup(){
		reader = new Mp3MetadataReader(audioFile);
		reader.tag = tag;
	}
	
	@Test
	public void shouldGetGenreWhenRoundBrackets() throws Exception {
		when(tag.getFirst(FieldKey.GENRE)).thenReturn("(0)");
		assertEquals("Blues", reader.getGenre());
	}
	
	@Test
	public void shouldGetGenreByNumerics() throws Exception {
		when(tag.getFirst(FieldKey.GENRE)).thenReturn("0");
		assertEquals("Blues", reader.getGenre());
	}
	
	@Test
	public void shouldGetGenreByStrings() throws Exception {
		when(tag.getFirst(FieldKey.GENRE)).thenReturn("Blues");
		assertEquals("Blues", reader.getGenre());
	}
	
	@Test
	public void shouldNotGetNullIfNoValidIndex() throws Exception {
		when(tag.getFirst(FieldKey.GENRE)).thenReturn("150");
		assertEquals(StringUtils.EMPTY, reader.getGenre());
	}
}
