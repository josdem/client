package com.all.client.view.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestDnDListenerEntries {
	@Mock
	private Component c;
	private DragAndDropListener listenerA1 = new BasicTestListener(TestA.class);
	private DragAndDropListener listenerA2 = new BasicTestListener(TestA.class);
	private DragAndDropListener listenerB1 = new BasicTestListener(TestB.class);
	private DragAndDropListener listenerB2 = new BasicTestListener(TestB.class);
	private DragAndDropListener listenerC1 = new BasicTestListener(TestC.class);
	private DragAndDropListener listenerC2 = new BasicTestListener(TestC.class);
	private DragAndDropListener listenerD1 = new BasicTestListener(TestD.class);
	private DragAndDropListener listenerE1 = new BasicTestListener(TestE.class);
	private DragAndDropListener listenerE2 = new BasicTestListener(TestE.class);
	private DragAndDropListener listenerF1 = new BasicTestListener(TestF.class);
	private DragAndDropListener listenerF2 = new BasicTestListener(TestF.class);
	private DragAndDropListener listenerNull = new BasicTestListener();
	private DnDListenerEntries<DragAndDropListener> entries = new DnDListenerEntries<DragAndDropListener>();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateEntries() throws Exception {
		assertTrue(entries.isEmpty());
		Class<?> clazz = TestA.class;
		entries.put(clazz, c, listenerA1);
		entries.put(clazz, c, listenerA2);
		assertEquals(2, entries.size());
		assertFalse(entries.isEmpty());
	}

	@Test
	public void shouldCreateHierarchyEntries() throws Exception {
		Class<?> clazz = TestA.class;
		entries.put(clazz, c, listenerB1);
		entries.put(clazz, c, listenerB2);
		assertEquals(2, entries.size());
	}

	@Test
	public void shouldRejectNonHierarchyEntries() throws Exception {
		Class<?> clazz = TestA.class;
		entries.put(clazz, c, listenerC1);
		entries.put(clazz, c, listenerC2);
		assertEquals(0, entries.size());
	}

	@Test
	public void shouldRejectInverseHierarchyEntries() throws Exception {
		Class<?> clazz = TestB.class;
		entries.put(clazz, c, listenerA1);
		entries.put(clazz, c, listenerA2);
		assertEquals(0, entries.size());
	}

	@Test
	public void shouldInsertImplementsHierarchyEntries() throws Exception {
		Class<?> clazz = TestD.class;
		entries.put(clazz, c, listenerE1);
		entries.put(clazz, c, listenerE2);
		entries.put(clazz, c, listenerF1);
		entries.put(clazz, c, listenerF2);
		assertEquals(4, entries.size());
	}

	@Test
	public void shouldAddAllOnNull() throws Exception {
		Class<?> clazz = null;
		entries.put(clazz, c, listenerA1);
		entries.put(clazz, c, listenerB1);
		entries.put(clazz, c, listenerC1);
		entries.put(clazz, c, listenerD1);
		entries.put(clazz, c, listenerE1);
		entries.put(clazz, c, listenerF1);
		entries.put(clazz, c, listenerNull);
		assertEquals(7, entries.size());
	}

	@Test
	public void shouldAddNullToAll() throws Exception {
		Class<?> clazz = BigDecimal.class;
		entries.put(clazz, c, listenerNull);
		assertEquals(1, entries.size());
	}

	@Test
	public void shouldCheckContains() throws Exception {
		shouldCreateEntries();
		assertTrue(entries.containsValue(c, listenerA1));
	}

	@Test
	public void shouldCheckNotContains() throws Exception {
		shouldCreateEntries();
		assertFalse(entries.containsValue(c, listenerB1));
		assertFalse(entries.containsValue(c, listenerB1));
	}

	@Test
	public void shouldReturnFalseForBadContainse() throws Exception {
		shouldCreateEntries();
		assertFalse(entries.containsValue(null, listenerA1));
		assertFalse(entries.containsValue(c, null));
		assertFalse(entries.containsValue(null, null));
	}

	@Test
	public void shouldTestForEachAbility() throws Exception {
		shouldCreateEntries();
		final Set<DragAndDropListener> listeners = new HashSet<DragAndDropListener>();
		final AtomicBoolean doneInvoked = new AtomicBoolean();
		Integer value = entries.forEach(new DoInListeners<Integer, DragAndDropListener>() {
			@Override
			public Integer doIn(DragAndDropListener listener, Component component, Integer lastResult) {
				assertEquals(c, component);
				assertFalse(listeners.contains(listener));
				listeners.add(listener);
				Integer result;
				if (lastResult == null) {
					result = 0;
				} else {
					result = lastResult;
				}
				result++;
				return result;
			}

			@Override
			public void done() {
				doneInvoked.set(true);
			}
		});
		assertEquals(Integer.valueOf(2), value);
		assertEquals(2, listeners.size());
		assertTrue(listeners.contains(listenerA1));
		assertTrue(listeners.contains(listenerA2));
		assertTrue(doneInvoked.get());
	}

	@Test
	public void shouldTestExcludingForEach() throws Exception {
		Class<?> clazz = TestA.class;
		shouldCreateEntries();
		final Set<DragAndDropListener> listeners = new HashSet<DragAndDropListener>();
		final AtomicBoolean doneInvoked = new AtomicBoolean();

		DnDListenerEntries<DragAndDropListener> excludeEntries = new DnDListenerEntries<DragAndDropListener>();
		excludeEntries.put(clazz, c, listenerA1);
		Integer value = entries.forEachExclude(new DoInListeners<Integer, DragAndDropListener>() {
			@Override
			public Integer doIn(DragAndDropListener listener, Component component, Integer lastResult) {
				assertEquals(c, component);
				assertFalse(listeners.contains(listener));
				listeners.add(listener);
				Integer result;
				if (lastResult == null) {
					result = 0;
				} else {
					result = lastResult;
				}
				result++;
				return result;
			}

			@Override
			public void done() {
				doneInvoked.set(true);
			}
		}, excludeEntries);
		assertEquals(Integer.valueOf(1), value);
		assertEquals(1, listeners.size());
		assertTrue(listeners.contains(listenerA2));
		assertTrue(doneInvoked.get());

	}

	@Test
	public void checkTheDragObjectByTheWay() throws Exception {
		DraggedObject draggedObject = new SimpleDraggedObject(new TestB());
		assertTrue(draggedObject.is(TestB.class));
		assertTrue(draggedObject.is(TestA.class));
	}

	@Test
	public void shouldGetAnEmptyDnDListenerEntries() throws Exception {
		DnDListenerEntries<DragAndDropListener> empty = DnDListenerEntries.empty();
		assertNotNull(empty);
		assertTrue(empty.isEmpty());
		empty.put(null, c, listenerA1);
		assertTrue(empty.isEmpty());
	}

	class TestA {
	}

	class TestB extends TestA {
	}

	class TestC {
	}

	interface TestD {
	}

	class TestE implements TestD {
	}

	class TestF extends TestC implements TestD {
	}
}
