package com.all.client.view.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;

public class TestDnDListenerCollection {
	private DragAndDropListener listenerCompA1 = new BasicTestListener();
	private DragAndDropListener listenerCompA2 = new BasicTestListener();
	private DragAndDropListener listenerCompB1 = new BasicTestListener();
	private DragAndDropListener listenerCompB2 = new BasicTestListener();
	private DragAndDropListener listenerCompC1 = new BasicTestListener();
	private DragAndDropListener listenerCompC2 = new BasicTestListener();
	private DragAndDropListener listenerCompD1 = new BasicTestListener();
	private DragAndDropListener listenerCompD2 = new BasicTestListener();

	private JPanel compA = new JPanel();
	private JPanel compB = new JPanel();
	private JPanel compC = new JPanel();
	private JPanel compD = new JPanel();

	private DnDListenerCollection<DragAndDropListener> collection = new DnDListenerCollection<DragAndDropListener>();

	@Before
	public void setup() {
		compA.add(compB);
		compA.add(compD);
		compB.add(compC);
		collection.put(compA, listenerCompA1);
		collection.put(compA, listenerCompA2);
		collection.put(compB, listenerCompB1);
		collection.put(compB, listenerCompB2);
		collection.put(compC, listenerCompC1);
		collection.put(compC, listenerCompC2);
		collection.put(compD, listenerCompD1);
		collection.put(compD, listenerCompD2);
	}

	@Test
	public void shouldCheckInmediateEntries() throws Exception {
		DnDListenerEntries<DragAndDropListener> entries = collection.getInmediateEntries(null, compA);
		assertEquals(2, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA1));
		assertTrue(entries.containsValue(compA, listenerCompA2));
	}

	@Test
	public void shouldNotAddListenersMultipleTimes() throws Exception {
		shouldCheckInmediateEntries();
		collection.put(compA, listenerCompA1);
		collection.put(compA, listenerCompA1);
		collection.put(compA, listenerCompA1);
		collection.put(compA, listenerCompA1);
		shouldCheckInmediateEntries();
		
	}

	@Test
	public void shouldRemoveAllForComponent() throws Exception {
		collection.remove(compA);
		DnDListenerEntries<DragAndDropListener> entries = collection.getInmediateEntries(null, compA);
		assertEquals(0, entries.size());
		assertTrue(entries.isEmpty());
	}

	@Test
	public void shouldRemoveSingleItem() throws Exception {
		collection.remove(compA, listenerCompA1);
		DnDListenerEntries<DragAndDropListener> entries = collection.getInmediateEntries(null, compA);
		assertEquals(1, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA2));
	}

	@Test
	public void shouldRemoveNothingIfNotFound() throws Exception {
		shouldCheckInmediateEntries();
		collection.remove(compA, listenerCompB1);
		shouldCheckInmediateEntries();
	}

	@Test
	public void shouldRemoveAllForComponentOneByOne() throws Exception {
		collection.remove(compA, listenerCompA1);
		collection.remove(compA, listenerCompA2);
		collection.remove(compA, listenerCompA1);
		DnDListenerEntries<DragAndDropListener> entries = collection.getInmediateEntries(null, compA);
		assertEquals(0, entries.size());
		assertTrue(entries.isEmpty());
	}
	@Test
	public void shouldGetInmediateUpwardEntries() throws Exception {
		collection.remove(compB);
		DnDListenerEntries<DragAndDropListener> entries = collection.getInmediateEntries(null, compB);
		assertEquals(2, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA1));
		assertTrue(entries.containsValue(compA, listenerCompA2));

	}

	@Test
	public void shouldGetHierarchichalListenersPART1() throws Exception {
		DnDListenerEntries<DragAndDropListener> entries = collection.getUpwardEntries(null, compA);
		assertEquals(2, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA1));
		assertTrue(entries.containsValue(compA, listenerCompA2));
	}

	@Test
	public void shouldGetHierarchichalListenersPART2() throws Exception {
		DnDListenerEntries<DragAndDropListener> entries = collection.getUpwardEntries(null, compB);
		assertEquals(4, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA1));
		assertTrue(entries.containsValue(compA, listenerCompA2));
		assertTrue(entries.containsValue(compB, listenerCompB1));
		assertTrue(entries.containsValue(compB, listenerCompB2));
	}

	@Test
	public void shouldGetHierarchichalListenersPART3() throws Exception {
		DnDListenerEntries<DragAndDropListener> entries = collection.getUpwardEntries(null, compC);
		assertEquals(6, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA1));
		assertTrue(entries.containsValue(compA, listenerCompA2));
		assertTrue(entries.containsValue(compB, listenerCompB1));
		assertTrue(entries.containsValue(compB, listenerCompB2));
		assertTrue(entries.containsValue(compC, listenerCompC1));
		assertTrue(entries.containsValue(compC, listenerCompC2));
	}

	@Test
	public void shouldGetHierarchichalListenersPART4() throws Exception {
		DnDListenerEntries<DragAndDropListener> entries = collection.getUpwardEntries(null, compD);
		assertEquals(4, entries.size());
		assertTrue(entries.containsValue(compA, listenerCompA1));
		assertTrue(entries.containsValue(compA, listenerCompA2));
		assertTrue(entries.containsValue(compD, listenerCompD1));
		assertTrue(entries.containsValue(compD, listenerCompD2));
	}

}
