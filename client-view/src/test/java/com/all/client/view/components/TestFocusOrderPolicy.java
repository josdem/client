package com.all.client.view.components;

import static org.junit.Assert.assertEquals;

import java.awt.Component;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;

import com.all.core.common.view.util.FocusOrderPolicy;

public class TestFocusOrderPolicy {
	
	private JLabel first = new JLabel("one");
	private JLabel second = new JLabel("dos");
	private FocusTraversalPolicy focusOrderPolicy ;
	
	@Before
	public void init() {
		focusOrderPolicy = new FocusOrderPolicy(new ArrayList<Component>(Arrays.asList(new JComponent[] {first, second})));
	}
	
	@Test
	public void shouldGetComponentAfter() throws Exception {
		assertEquals(second, focusOrderPolicy.getComponentAfter(null, first));
		assertEquals(first, focusOrderPolicy.getComponentAfter(null, second));
	}
	
	@Test
	public void shouldGetComponentBefore() throws Exception {
		assertEquals(first, focusOrderPolicy.getComponentBefore(null, second));
		assertEquals(second, focusOrderPolicy.getComponentBefore(null, first));
	}
	
	@Test
	public void shouldGetDefaultComponent() throws Exception {
		assertEquals(first, focusOrderPolicy.getDefaultComponent(null));
	}
	
	@Test
	public void shouldGetFirstComponent() throws Exception {
		assertEquals(first, focusOrderPolicy.getFirstComponent(null));
	}
	
	@Test
	public void shouldGetLastComponent() throws Exception {
		assertEquals(second, focusOrderPolicy.getLastComponent(null));
	}
}
