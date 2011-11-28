package com.all.client.view.components;

import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class JTextPaneAligned extends JTextPane{

	private static final long serialVersionUID = 1L;

	public JTextPaneAligned(int alignment) {
		this(getDocument(alignment));
	}
	
	public JTextPaneAligned(StyledDocument document){
		super(document);
	}
	
	private static StyledDocument getDocument(int alignment){
		StyleContext context = new StyleContext();
		StyledDocument document = new DefaultStyledDocument(context);

		Style style = context.getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setAlignment(style, alignment);
		return document;
	}

}
