package com.all.client.view.dnd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.observ.ObservValue;
import com.all.observ.Observable;

public class TestMultiLayerDropTargetListener {
	private final Point pointComponent = new Point(1, 1);
	private final Point pointContainer = new Point(0, 0);
	@Mock
	private JFrame mockFrame;
	@Mock
	private Container mockContainer;
	@Mock
	private Component mockComponent;

	private DragAndDropAction dndAction;
	private Observable<ObservValue<Component>> componentChangedEvent = new Observable<ObservValue<Component>>();

	@InjectMocks
	private MultiLayerDropTargetListener dndListener;
	@Mock
	private DragAndDropActionFactory actionFactory;
	@Mock
	private DragOverListener dragOverMock;
	@Mock
	private DropListener dropMock;

	@Before
	public void setup() {
		dndListener = new MultiLayerDropTargetListener();
		MockitoAnnotations.initMocks(this);
		dndAction = mock(DragAndDropAction.class);
		when(actionFactory.getAction(mockFrame)).thenReturn(dndAction);
		when(mockContainer.getParent()).thenReturn(mockFrame);
		when(mockContainer.getLocationOnScreen()).thenReturn(new Point());
		when(mockComponent.getParent()).thenReturn(mockContainer);
		when(mockComponent.getLocationOnScreen()).thenReturn(new Point());
		when(mockFrame.getLocationOnScreen()).thenReturn(new Point());
		when(mockFrame.findComponentAt(pointComponent)).thenReturn(mockComponent);
		when(mockFrame.findComponentAt(pointContainer)).thenReturn(mockContainer);
		when(dndAction.onComponentChangedListener()).thenReturn(componentChangedEvent);
	}

	@Test
	public void dragEnterToInitializeDragAction() {
		dndListener.dragEnter(createDragEvent(pointContainer));
		verify(dndAction).setLocation(pointContainer);
		verify(actionFactory).getAction(mockFrame);
		reset(dndAction);
		reset(actionFactory);
	}

	@Test
	public void shouldNotInitializeTheDndActionIfNoPreviousDragEnterHappened() {
		dndListener.dragOver(createDragEvent(pointContainer));
		verify(dndAction, never()).setLocation(pointContainer);
		verify(actionFactory, never()).getAction(mockFrame);
		reset(dndAction);
	}

	@Test
	public void shouldAcceptDragIfDragIsToBeAcceptedYeahSoundsWeirdVERYWeird() throws Exception {
		dragEnterToInitializeDragAction();
		when(dndAction.validate(pointComponent)).thenReturn(true);
		DropTargetDragEvent dragEvent = createDragEvent(pointComponent);
		dndListener.dragOver(dragEvent);
		verify(dragEvent).acceptDrag(DnDConstants.ACTION_COPY);
	}

	@Test
	public void shouldDragExitCorrectly() throws Exception {
		dragEnterToInitializeDragAction();
		DropTargetDragEvent dragEvent = createDragEvent(pointComponent);
		dndListener.dragExit(dragEvent);
		verify(dndAction).dragExit();
	}

	@Test
	public void shouldDropSuccessfully() throws Exception {
		dragEnterToInitializeDragAction();
		when(dndAction.drop(pointComponent)).thenReturn(true);
		DropTargetDropEvent dropEvent = createDropEvent(pointComponent);
		dndListener.drop(dropEvent);
		verify(dropEvent).acceptDrop(DnDConstants.ACTION_COPY);
		verify(dndAction).drop(pointComponent);
	}

	@Test
	public void shouldSetListenersToAction() throws Exception {
		dragEnterToInitializeDragAction();
		dndListener.addDragListener(mockContainer, dragOverMock);
		dndListener.addDropListener(mockContainer, dropMock);

		componentChangedEvent.fire(new ObservValue<Component>(mockContainer));

		verify(dndAction).setDragListeners(argThat(match(DragOverListener.class).contains(mockContainer, dragOverMock)));
		verify(dndAction).setDropListeners(argThat(match(DropListener.class).contains(mockContainer, dropMock)));
		reset(dndAction);
	}

	@Test
	public void shouldRemoveListeners() throws Exception {
		shouldSetListenersToAction();
		dndListener.removeDropListener(mockContainer, dropMock);

		componentChangedEvent.fire(new ObservValue<Component>(mockContainer));

		verify(dndAction).setDragListeners(argThat(match(DragOverListener.class).contains(mockContainer, dragOverMock)));
		verify(dndAction).setDropListeners(argThat(match(DropListener.class)));

		reset(dndAction);

		dndListener.removeDragListener(mockContainer, dragOverMock);

		componentChangedEvent.fire(new ObservValue<Component>(mockContainer));

		verify(dndAction).setDragListeners(argThat(match(DragOverListener.class)));
		verify(dndAction).setDropListeners(argThat(match(DropListener.class)));
	}

	@Test
	public void shouldRemoveListenerSimpleWay() throws Exception {
		shouldSetListenersToAction();
		dndListener.removeListeners(mockContainer);

		componentChangedEvent.fire(new ObservValue<Component>(mockContainer));

		verify(dndAction).setDragListeners(argThat(match(DragOverListener.class)));
		verify(dndAction).setDropListeners(argThat(match(DropListener.class)));
	}

	private DropTargetDragEvent createDragEvent(Point point) {
		Transferable mockTransferable = mock(Transferable.class);
		DropTargetDragEvent mockDropTargetDragEvent = mock(DropTargetDragEvent.class);
		DropTargetContext dropTargetContext = mock(DropTargetContext.class);

		when(mockDropTargetDragEvent.getTransferable()).thenReturn(mockTransferable);
		when(mockDropTargetDragEvent.getLocation()).thenReturn(point);
		when(mockDropTargetDragEvent.getDropTargetContext()).thenReturn(dropTargetContext);

		when(dropTargetContext.getComponent()).thenReturn(mockFrame);
		return mockDropTargetDragEvent;
	}

	private DropTargetDropEvent createDropEvent(Point point) {
		Transferable mockTransferable = mock(Transferable.class);
		DropTargetDropEvent mockDropTargetDropEvent = mock(DropTargetDropEvent.class);
		DropTargetContext dropTargetContext = mock(DropTargetContext.class);

		when(mockDropTargetDropEvent.getTransferable()).thenReturn(mockTransferable);
		when(mockDropTargetDropEvent.getLocation()).thenReturn(point);
		when(mockDropTargetDropEvent.getDropTargetContext()).thenReturn(dropTargetContext);

		when(dropTargetContext.getComponent()).thenReturn(mockFrame);
		return mockDropTargetDropEvent;
	}

	public static <T extends DragAndDropListener> DnDMatcher<T> match(Class<T> clazz) {
		return new DnDMatcher<T>();
	}

	private static class DnDMatcher<T extends DragAndDropListener> extends BaseMatcher<DnDListenerEntries<T>> {
		private List<Component> components = new ArrayList<Component>();
		private List<T> ts = new ArrayList<T>();

		public DnDMatcher() {
		}

		public DnDMatcher<T> contains(Component c, T t) {
			components.add(c);
			ts.add(t);
			return this;
		}

		@Override
		public boolean matches(Object arg0) {
			@SuppressWarnings("unchecked")
			DnDListenerEntries<T> c = (DnDListenerEntries<T>) arg0;
			if (!components.isEmpty()) {
				assertFalse(c.isEmpty());
				for (int i = 0; i < components.size(); i++) {
					Component component = components.get(i);
					T value = ts.get(i);
					assertTrue("Expected [" + component + ", " + value + "]", c.containsValue(component, value));
				}
			} else {
				assertTrue(c.isEmpty());
			}
			return true;
		}

		@Override
		public void describeTo(Description arg0) {
		}

	}

}
