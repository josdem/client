package com.all.client.view;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.swing.JMenuBar;

import org.junit.Before;
import org.junit.Test;

import com.all.i18n.Internationalizable;


public class TestMainMenu {
	
	private MainMenu mainMenu;
	
	@Before
	public void createMainMenu() {
		mainMenu= new MainMenu();
	}

	@Test
	public void shouldNotBeNull() throws Exception {
		assertNotNull(mainMenu);
	}
	
	@Test
	public void shouldBeChildOfSwingsJMenuBar() throws Exception {
		assertTrue(mainMenu instanceof JMenuBar);
	}
	
	@Test
	public void shouldBeInternazionalizable() throws Exception {
		assertTrue(mainMenu instanceof Internationalizable);
	}
	

}
