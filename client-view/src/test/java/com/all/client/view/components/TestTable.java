package com.all.client.view.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.all.client.UnitTestCase;

public class TestTable extends UnitTestCase {
	Table<String, MockTableStyle> table;

	TableColumn column0 = new TableColumn();
	TableColumn column1 = new TableColumn();
	TableColumn column2 = new TableColumn();

	@Before
	public void setup() {
		table = new Table<String, MockTableStyle>(new MockTableStyle());
		// We create a table 3x3
		column0.setWidth(35);
		column1.setWidth(15);
		column2.setWidth(30);
		table.addColumn(column0);
		table.addColumn(column1);
		table.addColumn(column2);
		table.addRow("a");
		table.addRow("b");
		table.addRow("c");
		table.setRowHeight(20);
		table.setSize(300, 300);
	}

	@Test
	public void shouldGetTheLocationOfAClicOnTheTable() throws Exception {
		TableCellInfo<String> info = table.getCellData(new Point(10, 10));
		assertEquals(0, info.getRow());
		assertEquals(0, info.getColumn());
		assertEquals(column0, info.getTableColumn());
		assertEquals("a", info.getData());
		assertEquals(10, info.getXinColumn());
		assertEquals(10, info.getYinColumn());
		assertEquals(35, info.getWidth());
		assertEquals(20, info.getHeight());

		info = table.getCellData(new Point(40, 35));
		assertEquals(1, info.getRow());
		assertEquals(1, info.getColumn());
		assertEquals(column1, info.getTableColumn());
		assertEquals("b", info.getData());
		assertEquals(5, info.getXinColumn());
		assertEquals(15, info.getYinColumn());
		assertEquals(15, info.getWidth());
		assertEquals(20, info.getHeight());

	}

	@Test
	public void shouldReturnNullIfOutOfBounds() throws Exception {
		TableCellInfo<String> info = table.getCellData(new Point(100, 100));
		assertNull(info);
		info = table.getCellData(new Point(-1, -1));
		assertNull(info);
	}

	@Test
	public void shouldCheckForImprovedDragAndDropFunctionality() throws Exception {
		MouseListener[] mouseListeners = table.getMouseListeners();
		assertEquals(3, mouseListeners.length);
		assertTrue(mouseListeners[0] instanceof Table<?, ?>.DragAndDropMouseListenerHelper);
		assertTrue(mouseListeners[1] instanceof ToolTipManager);
		// assertTrue(mouseListeners[2] instanceof javax.swing.plaf.basic.BasicTableUI.Handler);
	}

	@Test
	@Ignore("This test is a visual aid for table functions it should be enabled for debugging purposes only")
	public void dragAndDropTestFrame() throws Exception {
		final Object lock = new Object();
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				synchronized (lock) {
					lock.notifyAll();
				}
			}
		});
		List<String> values = new ArrayList<String>(20);
		for (int i = 0; i < 20; i++) {
			values.add("DATA   " + i + "::::" + i);
		}

		final Table<String, MockTableStyle> table = new Table<String, MockTableStyle>(new MockTableStyle());
		TableColumn column = table.setupColumn(new TableColumnStyle<MockTableStyle>() {
			@Override
			public boolean resizable() {
				return false;
			}

			@Override
			public int minWidth() {
				return 0;
			}

			@Override
			public int maxWidth() {
				return 100;
			}

			@Override
			public String label() {
				return "THA STRING";
			}

			@Override
			public int index() {
				return 0;
			}

			@Override
			public TableCellRenderer getRenderer(MockTableStyle style) {
				return new DefaultTableCellRenderer();
			}

			@Override
			public int defaultWidth() {
				return 0;
			}

			@Override
			public Comparator<?> comparator(MockTableStyle styles) {
				return new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				};
			}
		});
		table.getColumnModel().addColumn(column);

		table.setDataModel(values);
		table.setDragEnabled(true);

		frame.add(new JScrollPane(table));
		frame.setSize(150, 300);
		frame.setVisible(true);

		synchronized (lock) {
			lock.wait();
		}
	}

}

class MockTableStyle implements TableStyle {

	@Override
	public Color getEvenRowColor() {
		// TODO Auto-generated method stub
		return Color.white;
	}

	@Override
	public Color getOddRowColor() {
		// TODO Auto-generated method stub
		return Color.LIGHT_GRAY;
	}

	@Override
	public Color getSelectedRowColor() {
		// TODO Auto-generated method stub
		return Color.gray;
	}

	@Override
	public Color getSelectedSeparatorColor() {
		// TODO Auto-generated method stub
		return Color.white;
	}

	@Override
	public Color getGridColor() {
		// TODO Auto-generated method stub
		return Color.blue;
	}

}