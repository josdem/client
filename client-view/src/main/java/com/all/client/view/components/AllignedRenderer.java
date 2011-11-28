package com.all.client.view.components;

import javax.swing.table.DefaultTableCellRenderer;

public class AllignedRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AllignedRenderer(int alignment) {
		setHorizontalAlignment(alignment);
	}

}
