package com.all.client.util;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.springframework.util.StringUtils;


public class TestStringUtil {
	@Test
	public void shouldRecognizeEmpty() throws Exception {
		assertFalse(StringUtils.hasText(null));
		assertFalse(StringUtils.hasText(""));
	}

}
