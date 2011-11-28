package com.all.client.view.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

public class DateRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 7821075683142263721L;
	
	private DateFormat formater = new SimpleDateFormat("MM/dd/yy hh:mm aa");

	@Override
	protected void setValue(Object value) {
		if (value instanceof Date) {
			setText(formater.format(value));
		}
		else {
			setText(value == null ? "" : value.toString());
		}
	}
}
