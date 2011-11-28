package com.all.client.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class ErrorTypeTest {
	@Test
	public void shouldCreateErrorType() throws Exception {
		ErrorType connectionError= ErrorType.CONNECTION_ERROR;
		assertNotNull(connectionError);
	}
}
