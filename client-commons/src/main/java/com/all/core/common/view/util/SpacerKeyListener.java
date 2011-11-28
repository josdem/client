package com.all.core.common.view.util;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SpacerKeyListener extends KeyAdapter {
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if(keyCode == KeyEvent.VK_SPACE){
			e.consume();
		}
	}
}
