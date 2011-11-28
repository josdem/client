package com.all.client.view.dnd;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.awt.Container;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestDragAndDropActionImpl {
	private static final Log log = LogFactory.getLog(TestDragAndDropActionImpl.class);
	private static final String DROP_OCURRED = "dropOcurred";
	private static final String DRAG_EXIT = "dragExit";
	private static final String UPDATE_LOCATION = "updateLocation";
	private static final String DRAG_ALLOWED_CHANGED = "dragAllowedChanged";
	private static final String DRAG_ENTER = "dragEnter";
	private static final String VALIDATE_DROP = "validateDrop";

	private DragAndDropAction action;
	@Mock
	private Container frame;
	private TestDragListener testListener = new TestDragListener();
	private DnDListenerEntries<DragOverListener> dragListeners;
	private DnDListenerEntries<DropListener> dropListeners;
	private DraggedObject draggedObject;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		dragListeners = new DnDListenerEntries<DragOverListener>();
		dropListeners = new DnDListenerEntries<DropListener>();
		draggedObject = new SimpleDraggedObject(new Object());
		dragListeners.put(null, frame, testListener);
		dropListeners.put(null, frame, testListener);
		when(frame.findComponentAt(any(Point.class))).thenReturn(frame);
		action = new DragAndDropActionImpl(frame);
		action.setDragObject(draggedObject);
		action.setDragListeners(dragListeners);
		action.setDropListeners(dropListeners);
	}

	@Test
	public void shouldTestLifecycicle() throws Exception {

		Point location = new Point(0, 0);

		action.validate(location);

		action.setLocation(location);

		action.drop(location);

		testListener.check(0, DRAG_ENTER, draggedObject);
		testListener.check(1, DRAG_ALLOWED_CHANGED, false);
		testListener.check(2, UPDATE_LOCATION, new Point(Integer.MIN_VALUE, Integer.MIN_VALUE));
		testListener.check(3, VALIDATE_DROP, draggedObject, new Point(Integer.MIN_VALUE, Integer.MIN_VALUE));
		testListener.check(4, DRAG_ALLOWED_CHANGED, true);
		testListener.check(5, VALIDATE_DROP, draggedObject, location);
		testListener.check(6, DRAG_EXIT, true);
		testListener.check(7, VALIDATE_DROP, draggedObject, location);
		// We dont check do drop since it occurs on a different thread.
		testListener.check(8, DROP_OCURRED, true);

		assertEquals(9, testListener.invocations.size());

	}

	class TestDragListener implements DragOverListener, DropListener {
		private List<Object[]> invocations;

		public TestDragListener() {
			invocations = new ArrayList<Object[]>();
		}

		@Override
		public boolean validateDrop(DraggedObject draggedObject, Point location) {
			invAdd(new Object[] { VALIDATE_DROP, draggedObject, location });
			return true;
		}

		@Override
		public void dragEnter(DraggedObject dragObject) {
			invAdd(new Object[] { DRAG_ENTER, dragObject });
		}

		@Override
		public void dragAllowedChanged(boolean newStatus) {
			invAdd(new Object[] { DRAG_ALLOWED_CHANGED, newStatus });
		}

		@Override
		public void updateLocation(Point location) {
			invAdd(new Object[] { UPDATE_LOCATION, location });
		}

		@Override
		public void dragExit(boolean dropped) {
			invAdd(new Object[] { DRAG_EXIT, dropped });
		}

		@Override
		public void doDrop(DraggedObject model, Point location) {
		}

		@Override
		public void dropOcurred(boolean success) {
			invAdd(new Object[] { DROP_OCURRED, success });
		}

		private void invAdd(Object[] objects) {
			log.info(Arrays.toString(objects));
			invocations.add(objects);
		}

		@Override
		public Class<?>[] handledTypes() {
			return null;
		}

		public void check(int order, String method, Object... arguments) {
			Object[] objects = invocations.get(order);
			assertEquals(method, objects[0]);
			assertEquals(arguments.length + 1, objects.length);
			for (int i = 0; i < arguments.length; i++) {
				assertEquals(arguments[i], objects[i + 1]);
			}
		}

	}
}
