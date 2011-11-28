package com.all.client.view.listeners;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JTree;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestSingleExpandTree {
	@Test
	public void shouldApplySingleExpandToTree() throws Exception {
		JTree tree = mock(JTree.class);
		SingleExpandTree.apply(tree);
		verify(tree).addMouseListener(any(SingleExpandTree.class));
		verifyNoMoreInteractions(tree);
	}

	@Test
	public void shouldCheckSingleExpandTree() throws Exception {
		JTree tree = mock(JTree.class);
		SingleExpandTree singleExpand = getAndApply(tree);
		when(tree.getRowForLocation(10, 30)).thenReturn(3);
		singleExpand.mouseClicked(new MouseEvent(tree, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
				MouseEvent.BUTTON1, 10, 30, 100, 130, 1, false, MouseEvent.BUTTON1));
		verify(tree).expandRow(3);
	}

	private SingleExpandTree getAndApply(JTree tree) {
		final AtomicReference<SingleExpandTree> obj = new AtomicReference<SingleExpandTree>();
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				SingleExpandTree set = (SingleExpandTree) invocation.getArguments()[0];
				obj.set(set);
				return null;
			}
		}).when(tree).addMouseListener(any(MouseListener.class));
		SingleExpandTree.apply(tree);
		return obj.get();
	}
}
