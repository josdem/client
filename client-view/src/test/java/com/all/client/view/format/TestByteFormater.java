package com.all.client.view.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.all.client.view.format.ByteFormater;

public class TestByteFormater {
	ByteFormater byteFormater = new ByteFormater();
	static final String BYTES_LABEL = " B";
	static final String KILO_BYTES_LABEL = " KB";
	static final String MEGA_BYTES_LABEL = " MB";
	static final String GIGA_BYTES_LABEL = " GB";
	static final Locale locale = Locale.getDefault();

	@Test
	public void shouldFormatBytes() throws Exception {
		String language = locale.getDisplayLanguage();
		
		assertEquals("0" + BYTES_LABEL, byteFormater.getFormat(0L));
		assertEquals("1" + KILO_BYTES_LABEL, byteFormater.getFormat(1024L));
		assertEquals("1" + MEGA_BYTES_LABEL, byteFormater.getFormat(1048576L));
		assertEquals("2" + MEGA_BYTES_LABEL, byteFormater.getFormat(2048576L));
		assertEquals("1" + GIGA_BYTES_LABEL, byteFormater.getFormat(1073741824L));		
		if (language == "English") {
			assertEquals("1,023" + BYTES_LABEL, byteFormater.getFormat(1023L));			
			assertEquals("1.3" + KILO_BYTES_LABEL, byteFormater.getFormat(1289L));
			assertEquals("22.4" + KILO_BYTES_LABEL, byteFormater.getFormat(22889L));
			assertEquals("1,024" + KILO_BYTES_LABEL, byteFormater.getFormat(1048575L));
			assertEquals("1,024" + MEGA_BYTES_LABEL, byteFormater.getFormat(1073741100L));
		} else {
			assertTrue(byteFormater.getFormat(1023L).equals("1.023" + BYTES_LABEL) || byteFormater.getFormat(1023L).equals("1,023" + BYTES_LABEL));
			assertTrue(byteFormater.getFormat(1289L).equals("1.3" + KILO_BYTES_LABEL) || byteFormater.getFormat(1289L).equals("1,3" + KILO_BYTES_LABEL));
			assertTrue(byteFormater.getFormat(22889L).equals("22.4" + KILO_BYTES_LABEL) || byteFormater.getFormat(22889L).equals("22,4" + KILO_BYTES_LABEL));
			assertTrue(byteFormater.getFormat(1048575L).equals("1.024" + KILO_BYTES_LABEL) || byteFormater.getFormat(1048575L).equals("1,024" + KILO_BYTES_LABEL));
			assertTrue(byteFormater.getFormat(1073741100L).equals("1.024" + MEGA_BYTES_LABEL) || byteFormater.getFormat(1073741100L).equals("1,024" + MEGA_BYTES_LABEL));
		}
	}
}
