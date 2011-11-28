package com.all.client.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestPasswordDigest {
	
	@Test
	public void shouldDigestPassword() throws Exception {
		String password = "12345678";
		String md5 = "25d55ad283aa400af464c76d713c07ad";
		assertEquals(md5, PasswordDigest.md5(password.getBytes()));
	}

}
