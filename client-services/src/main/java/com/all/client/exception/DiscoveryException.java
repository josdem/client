package com.all.client.exception;

public class DiscoveryException extends Exception {
	private static final long serialVersionUID = 6348944560676354356L;

	public DiscoveryException(String message) {
		super(message);
	}
	
	public DiscoveryException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
