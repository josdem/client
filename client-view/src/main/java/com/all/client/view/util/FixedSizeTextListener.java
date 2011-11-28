/**
 * 
 */
package com.all.client.view.util;

import java.awt.Toolkit;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;

public final class FixedSizeTextListener extends DocumentFilter implements DocumentListener {
	private static final Log log = LogFactory.getLog(FixedSizeTextListener.class);
	private final int size;
	private final JTextArea textArea;
	private Observable<ObservValue<String>> textModified = new Observable<ObservValue<String>>();

	public FixedSizeTextListener(int size, JTextArea textArea) {
		this.size = size;
		this.textArea = textArea;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		checkSize();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		checkSize();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		checkSize();
	}

	private void checkSize() {
		if (textArea.getText().length() > size) {
			Toolkit.getDefaultToolkit().beep();
			textArea.setText(textArea.getText().substring(0, size));
		} else {
			textModified.fire(new ObservValue<String>(textArea.getText()));
		}
	}

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		if (string == null) {
			return;
		} else {
			replace(fb, offset, 0, string, attr);
		}
	}

	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
		replace(fb, offset, length, "", null);
	}

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int selectedLength, String text, AttributeSet attrs)
			throws BadLocationException {
		text = text.replaceAll("\\\\n", " ");

		Document doc = fb.getDocument();
		int currentLength = doc.getLength();
		if (currentLength + text.length() - selectedLength < size) {
			fb.replace(offset, selectedLength, text, attrs);
		} else {
			text = text.substring(0, size - currentLength + selectedLength);
			Toolkit.getDefaultToolkit().beep();
			fb.replace(offset, selectedLength, text, attrs);
		}
	}

	public ObserverCollection<ObservValue<String>> onTextModified() {
		return textModified;
	}

	public static FixedSizeTextListener setFixed(JTextArea textArea, int size) {
		FixedSizeTextListener listener = new FixedSizeTextListener(size, textArea);
		textArea.getDocument().addDocumentListener(listener);
		try {
			((AbstractDocument) textArea.getDocument()).setDocumentFilter(listener);
		} catch (ClassCastException e) {
			log.warn("Text cannot be constrained correctly. " + e, e);
		}
		return listener;

	}
}