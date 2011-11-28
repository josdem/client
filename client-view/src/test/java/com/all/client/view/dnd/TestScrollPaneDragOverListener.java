package com.all.client.view.dnd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.swing.JScrollPane;

import org.junit.Before;
import org.junit.Test;

import com.all.client.UnitTestCase;

public class TestScrollPaneDragOverListener extends UnitTestCase {

	private int autoScrollAreaLimit;

	private int autoScrollAreaHeight;
	
	private int begin ;

	private JScrollPane scrollPane = new JScrollPane();
	
	private ScrollPaneDragOverListener scrollDragListener = new ScrollPaneDragOverListener(scrollPane);
	
	@Before
	public void setup() throws Exception {
		scrollPane.setSize(200,500);
		autoScrollAreaHeight = (Integer) getPrivateField(scrollDragListener, "AUTO_SCROLL_AREA_HEIGHT");
		autoScrollAreaLimit = (Integer) getPrivateField(scrollDragListener, "AUTO_SCROLL_AREA_LIMIT");
		begin = (Integer) getPrivateField(scrollDragListener, "BEGIN");
	}
	
	@Test
	public void shouldValidUpperAreaForScrolling() throws Exception {
		assertTrue(scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaLimit - 10) < 0);
		assertEquals(0, scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaLimit + 10));
		assertEquals(0, scrollDragListener.checkAreaForScrollVelocity(-10));
	}

	@Test
	public void shouldValidBottomAreaForScrolling() throws Exception {
		int height = scrollPane.getHeight();

		assertTrue(scrollDragListener.checkAreaForScrollVelocity(height - autoScrollAreaLimit + 10 ) > 0);
		assertEquals(0, scrollDragListener.checkAreaForScrollVelocity(height - autoScrollAreaLimit - 10));
		assertEquals(0, scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaLimit + 10));
	}

	@Test
	public void shouldGetAreaSpeed() throws Exception {
		int height = scrollPane.getHeight();

		assertEquals(-10, scrollDragListener.checkAreaForScrollVelocity(begin));
		assertEquals(-10, scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaHeight));
		assertEquals(-9, scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaHeight * 2));
		assertEquals(-8, scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaHeight * 3));
		assertEquals(0, scrollDragListener.checkAreaForScrollVelocity(begin + autoScrollAreaHeight * 4));

		assertEquals(10, scrollDragListener.checkAreaForScrollVelocity(height));
		assertEquals(10, scrollDragListener.checkAreaForScrollVelocity(height - autoScrollAreaHeight));
		assertEquals(9, scrollDragListener.checkAreaForScrollVelocity(height - autoScrollAreaHeight * 2));
		assertEquals(8, scrollDragListener.checkAreaForScrollVelocity(height - autoScrollAreaHeight * 3));
		assertEquals(0, scrollDragListener.checkAreaForScrollVelocity(height - autoScrollAreaHeight * 4));
	}
	
	@Test
	public void shouldStartToMoveAfterDndMusicPanelOnlyOnTop() throws Exception {
		assertEquals(160, begin);
	}

}
