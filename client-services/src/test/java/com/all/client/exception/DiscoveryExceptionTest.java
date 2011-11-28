package com.all.client.exception;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class DiscoveryExceptionTest {
	@Test
	public void shouldCreateDiscoveryException() throws Exception {
		DiscoveryException discoveryException= new DiscoveryException("whatever");
		assertNotNull(discoveryException);
	}
	
	@Test
	public void shouldCreateDiscoveryExceptionWithThrowable() throws Exception {
		DiscoveryException discoveryException= new DiscoveryException("whatever",new Throwable());
		assertNotNull(discoveryException);
	}
	
}
