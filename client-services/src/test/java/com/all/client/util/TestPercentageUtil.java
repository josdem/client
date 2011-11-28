package com.all.client.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestPercentageUtil {
	@Test
	public void shouldConvertFromFloatToPercentage() throws Exception {
		assertEquals(50, PercentageUtil.convertFloat(.5f));
		assertEquals(10, PercentageUtil.convertFloat(.1f));		
	}
	
	@Test
	public void shouldConvertFromPercentageToFloat() throws Exception {
		assertEquals(.5f, PercentageUtil.convertPercentage(50), .05);	
	}
}
