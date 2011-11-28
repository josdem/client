package com.all.core.common.view.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.commons.Environment;

public final class CopyPasteKeyAdapterForMac extends KeyAdapter{
	Log log = LogFactory.getLog(this.getClass());
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (Environment.isMac()) {
			Object textComponent = e.getSource();
			int keyCode = e.getKeyCode();
			if (e.isMetaDown() && keyCode == KeyEvent.VK_V) {
				log.debug("Ctrl + V in Mac " + e.getSource());
				if (textComponent instanceof JTextField){
					((JTextField)textComponent).paste();
				}
				else if( textComponent instanceof JTextArea){
					((JTextArea)textComponent).paste();
				}
				e.consume();
				return;
			}else if (e.isMetaDown() && keyCode == KeyEvent.VK_C) {
				log.debug("Ctrl + C in Mac " + e.getSource());
				if (textComponent instanceof JTextField){
					((JTextField)textComponent).copy();
				}
				else if( textComponent instanceof JTextArea){
					((JTextArea)textComponent).copy();
				}
				else if (textComponent instanceof JEditorPane){
					((JEditorPane)textComponent).copy();
				}
				e.consume();
				return;
			}else if (e.isMetaDown() && keyCode == KeyEvent.VK_X) {
				log.debug("Ctrl + X in Mac " + e.getSource());
				if (textComponent instanceof JTextField){
					((JTextField)textComponent).cut();
				}
				else if( textComponent instanceof JTextArea){
					((JTextArea)textComponent).cut();
				}
				e.consume();
				return;
			}
		}
	}
}

