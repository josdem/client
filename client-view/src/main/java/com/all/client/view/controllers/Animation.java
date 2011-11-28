package com.all.client.view.controllers;

public interface Animation {
	long animate(int frame);

	String id();

	void setup();

	void teardown();
}
