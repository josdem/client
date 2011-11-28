package com.all.client.view.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Locale;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.view.format.TimeFormater;
import com.all.i18n.Messages;

public class TestTimeFormater extends UnitTestCase {
	TimeFormater timeFormater = new TimeFormater();
	static final String SEC_LABEL = "Secs";
	static final String MINUTES_LABEL = "Minutes";
	static final String HOURS_LABEL = "Hours";
	static final Locale locale = Locale.getDefault();
    
	@Mock
	Messages messages;
	
	@Test
	public void shouldFormatTime() throws Exception {
		when(messages.getMessage("secs")).thenReturn(SEC_LABEL);
		when(messages.getMessage("minutes")).thenReturn(MINUTES_LABEL);
		when(messages.getMessage("hours")).thenReturn(HOURS_LABEL);
		
		timeFormater.internationalize(messages);
		String language = locale.getDisplayLanguage();
		
		assertEquals("0 " + SEC_LABEL, timeFormater.getFormat(0L));
		assertEquals("40 " + SEC_LABEL, timeFormater.getFormat(40L));
		assertEquals("59 " + SEC_LABEL, timeFormater.getFormat(59L));
		assertEquals("1 "  + MINUTES_LABEL, timeFormater.getFormat(60L));
		assertEquals("1 " + HOURS_LABEL, timeFormater.getFormat(3600L));
		assertEquals("78 " + HOURS_LABEL, timeFormater.getFormat(280800L));
		if (language == "English") {
			assertEquals("4.8 " + MINUTES_LABEL, timeFormater.getFormat(290L));
		} else {
			assertTrue(timeFormater.getFormat(290L).equals("4.8 " + MINUTES_LABEL) || timeFormater.getFormat(290L).equals("4,8 " + MINUTES_LABEL));
		}
	}
}
