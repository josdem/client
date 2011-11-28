package com.all.client.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTimeComparator {

	@Test
	public void shouldCompareTime() {
		TimeComparator timeComparator = new TimeComparator();
		assertEquals(0, timeComparator.compare("1:23", "1:23"));
		assertEquals(1, timeComparator.compare("1:23", "0:56"));
		assertEquals(-1, timeComparator.compare("1:23", "10:00"));
		
		assertEquals(0, timeComparator.compare("1:01:23", "1:01:23"));
		assertEquals(1, timeComparator.compare("2:29:23", "3:56"));
		assertEquals(-1, timeComparator.compare("41:23", "9:10:00"));
	}

}
