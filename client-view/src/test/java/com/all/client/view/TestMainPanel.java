package com.all.client.view;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.client.view.contacts.ContactListMainPanel;

public class TestMainPanel {

	@Spy
	private HipecotechTopPanel hipecotechTopPanel;
	@Spy
	@SuppressWarnings("unused")
	private MiddlePanel middlePanel;
	@Spy
	@SuppressWarnings("unused")
	private ContactListMainPanel contactPanel;
	@Spy
	@SuppressWarnings("unused")
	private ToolBarPanel toolBarPanel;

	@InjectMocks
	private MainPanel mainPanel = new MainPanel();

	@Before
	public void setUp() {
		hipecotechTopPanel = new HipecotechTopPanel();
		middlePanel = new MiddlePanel();
		contactPanel = new ContactListMainPanel();
		toolBarPanel = new ToolBarPanel();
		MockitoAnnotations.initMocks(this);
		mainPanel.initialize();
	}

	@Test
	public void shouldCreateMainPanel() throws Exception {
		assertEquals(new Dimension(1020, 741), mainPanel.getSize());
		assertEquals(mainPanel, hipecotechTopPanel.getParent());
	}
}
