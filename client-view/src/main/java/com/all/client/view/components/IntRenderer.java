package com.all.client.view.components;

import javax.swing.table.DefaultTableCellRenderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.util.Formatters;

public class IntRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(this.getClass());

	public IntRenderer() {
		setHorizontalAlignment(IntRenderer.RIGHT);
	}
	
	@Override
	protected void setValue(Object value) {		
		try {
			setText(value == null ? "" : Formatters.formatInteger((Integer) value));
		} catch (Exception e) {
			log.error(e, e);
		}
	}


}
