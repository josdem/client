package com.all.client.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;


public class TestFormatters {
	@Test
	public void shouldFormatInt() throws Exception {
		assertEquals("1,234", Formatters.formatInteger(1234));
	}
	
	@Test
	public void shouldFormatdate() throws Exception {
		Calendar cal = new GregorianCalendar(1945, 10, 19);
		assertEquals("November 19th", Formatters.formatDate(cal.getTime(), "MMMM d'th'"));
	}
	
	@Test
	public void shouldFormatFloatValues() throws Exception {
		float floatValue = 3.1819f;
		
		assertEquals("3.18", Formatters.formatFloat(floatValue, 2));
		assertEquals("3.2", Formatters.formatFloat(floatValue, 1));
	}
	
	@Test
	public void shouldTestFormatSpeed() throws Exception {
		long data = 1234532424L;
		assertEquals("1 gbps", Formatters.formatSpeed(data));
	}
	
	@Test
	public void shouldGetSize() throws Exception {
		long size = 12345324L;
		assertEquals("11 MB", Formatters.formatDataSize(size, false));
	}
	
	@Test
	public void shouldGetSizeInKB() throws Exception {
		long size = 12345324L;
		assertEquals("12,055 KB", Formatters.formatDataSize(size, true));
	}
}
