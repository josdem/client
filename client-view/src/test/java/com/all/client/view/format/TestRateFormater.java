package com.all.client.view.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.all.client.view.format.RateFormater;


public class TestRateFormater {
	Log log = LogFactory.getLog(TestRateFormater.class);
	static final String KILO_BYTES_LABEL = " KB/s";
	static final String MEGA_BYTES_LABEL = " MB/s";
	RateFormater rateFormater = new RateFormater();
	static final Locale locale = Locale.getDefault();
	
	@Test
	public void shouldExistMegaKiloLabels() throws Exception {
		assertEquals(KILO_BYTES_LABEL, RateFormater.KILO_BYTES_LABEL);
		assertEquals(MEGA_BYTES_LABEL, RateFormater.MEGA_BYTES_LABEL);
	}
	
	@Test
	public void shouldFormat() throws Exception {
		String language = locale.getDisplayLanguage();
		
		assertEquals("0 B/s", rateFormater.getFormat(0L));
		assertEquals("204 B/s", rateFormater.getFormat(204L));
		assertEquals("1 KB/s", rateFormater.getFormat(1024L));
		assertEquals("110 KB/s", rateFormater.getFormat(112640L));
		assertEquals("1 MB/s", rateFormater.getFormat(1048576L));		
		if (language == "English") {
			assertEquals("1,023 B/s", rateFormater.getFormat(1023L));
			assertEquals("1.2 KB/s", rateFormater.getFormat(1228L));
			assertEquals("1,023 KB/s", rateFormater.getFormat(1047552L));
			assertEquals("1.1 MB/s", rateFormater.getFormat(1126400L));
			assertEquals("12.3 MB/s", rateFormater.getFormat(12897485L));
			assertEquals("123.4 MB/s", rateFormater.getFormat(129394278L));
		} else {
			assertTrue(rateFormater.getFormat(1023L).equals("1.023 B/s") || rateFormater.getFormat(1023L).equals("1,023 B/s"));
			assertTrue(rateFormater.getFormat(1228L).equals("1.2 KB/s") || rateFormater.getFormat(1228L).equals("1,2 KB/s"));
			assertTrue(rateFormater.getFormat(1047552L).equals("1.023 KB/s") || rateFormater.getFormat(1047552L).equals("1,023 KB/s"));
			assertTrue(rateFormater.getFormat(1126400L).equals("1.1 MB/s") || rateFormater.getFormat(1126400L).equals("1,1 MB/s"));
			assertTrue(rateFormater.getFormat(12897485L).equals("12.3 MB/s") || rateFormater.getFormat(12897485L).equals("12,3 MB/s"));
			assertTrue(rateFormater.getFormat(129394278L).equals("123.4 MB/s") || rateFormater.getFormat(129394278L).equals("123,4 MB/s"));
		}
	}
	
}
