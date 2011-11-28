package com.all.client.view.listeners;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PlayerShorcutsListener implements KeyListener {
	
	private static final Log log = LogFactory.getLog(PlayerShorcutsListener.class);

	@Override
	public void keyPressed(KeyEvent e) {
		e.consume() ;
		log.debug("KEY PRESSED ************* " + e.getKeyChar()) ;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		e.consume() ;
		log.debug("KEY RELEASED ************* " + e.getKeyChar()) ;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		e.consume() ;
		log.debug("KEY TYPED ************* " + e.getKeyChar()) ;
	}

}
