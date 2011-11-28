package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.all.core.common.view.AllLoader;

public class TransparentLoaderPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private AllLoader loader;

	public TransparentLoaderPanel() {
		this.setLayout(new BorderLayout());
		this.add(getLoader(), BorderLayout.CENTER);
		addMouseListener(new DisableMouseListener());
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
	}

	private AllLoader getLoader() {
		if (loader == null) {
			loader = new AllLoader();
			loader.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return loader;
	}
	
	public void addLoader(){
		if (loader != null) {
			this.add(getLoader(), BorderLayout.CENTER);
		}		
	}
	
	public void removeLoader(){
		if (loader != null) {
			this.remove(loader);
		}
	}

	private class DisableMouseListener implements MouseListener {
		@Override
		public void mouseReleased(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			e.consume();
		}
	}


}
