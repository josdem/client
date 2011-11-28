package com.all.client.view.music;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.table.TableRowSorter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.appControl.control.ViewEngine;
import com.all.i18n.Messages;

@SuppressWarnings( { "unchecked" })
public class TestLocalDescriptionPanelSearch {

	@InjectMocks
	private LocalDescriptionPanel panel;
	@Mock
	private DescriptionTable descriptionTable;
	@Mock
	private TableRowSorter rowSorter;

	private ViewEngine viewEngine;

	@Before
	public void setup() {
		viewEngine = mock(ViewEngine.class);
		panel = new LocalDescriptionPanel(mock(Messages.class), viewEngine);
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void shouldReportSearch() throws Exception {

		when(descriptionTable.getRowSorter()).thenReturn(rowSorter);
		panel.doSearch("some text");
		verify(rowSorter, timeout(2000)).setRowFilter(isA(DescriptionTableRowFilter.class));
	}
}
