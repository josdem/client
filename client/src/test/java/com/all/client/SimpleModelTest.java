package com.all.client;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.appControl.control.ControlEngine;
import com.all.client.data.TrackFactory;
import com.all.client.model.LocalModelFactory;

public class SimpleModelTest extends SimpleDBTest {
	protected LocalModelFactory localModelFactory;

	@Mock
	protected ControlEngine controlEngine;

	@Before
	public void initLocalModelFactory() throws Exception {
		localModelFactory = new LocalModelFactory();
		setValueToPrivateField(localModelFactory, "dao", dao);
		setValueToPrivateField(localModelFactory, "trackFactory", new TrackFactory());
		setValueToPrivateField(localModelFactory, "controlEngine", controlEngine);
	}

	@Test
	public void shouldInitialize() throws Exception {
		assertTrue(true);
	}
}
