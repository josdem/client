package com.all.client.view.components;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestDateRenderer {

	private String formattedDate;

	// private String dateTextFormat = "MM/dd/yy hh:mm aa";

	private String dateTextPattern = "(0[1-9]|1[012])[ /.](0[1-9]|[12][0-9]|3[01])[ /.][0-9][0-9] [0-9][0-9]:[0-9][0-9] (AM|PM)";

	@Test
	public void shouldSetFormatedDateText() throws Exception {

		Date testDate = new Date();

		DateRenderer dateRenderer = new DateRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setText(String text) {
				formattedDate = text;
			}
		};

		dateRenderer.setValue(testDate);

		assertTrue(Pattern.matches(dateTextPattern, formattedDate));

	}

	@Test
	public void shouldSetNonJavaUtilDateClassInstance() throws Exception {

		Object object = new Object();

		DateRenderer dateRenderer = new DateRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setText(String text) {
				formattedDate = text;
			}
		};

		dateRenderer.setValue(object);

		assertFalse(Pattern.matches(dateTextPattern, formattedDate));
	}

	@Test
	public void shouldSetNullValue() throws Exception {

		DateRenderer dateRenderer = new DateRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public void setText(String text) {
				formattedDate = text;
			}
		};

		dateRenderer.setValue(null);

		assertTrue(Pattern.matches("", formattedDate));
	}

}
