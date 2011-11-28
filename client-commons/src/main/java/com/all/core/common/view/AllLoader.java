package com.all.core.common.view;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class AllLoader extends JLabel {
	private static final long serialVersionUID = 1L;

	private static final int ANIMATION_FRAMES = 40;
	private static final String animationFrameName = "icons.loaderAnimationFrame";

	private Thread loadingAnimationThread;

	public AllLoader() {
		intialize();
	}

	private void intialize() {
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				stopAnimation();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				animate();
			}
		});
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {
				stopAnimation();
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
				animate();
			}
		});
		setIcon(UIManager.getDefaults().getIcon(animationFrameName + 1));
	}

	private void animate() {
		loadingAnimationThread = new Thread(new LoadingAnimationWorker());
		loadingAnimationThread.setDaemon(true);
		loadingAnimationThread.setName("Loading animation thread...");
		loadingAnimationThread.start();
	}

	private void stopAnimation() {
		loadingAnimationThread = null;
	}

	class LoadingAnimationWorker implements Runnable {

		@Override
		public void run() {
			Thread thisThread = Thread.currentThread();
			int i = 1;
			while (thisThread == loadingAnimationThread) {
				if (i > ANIMATION_FRAMES) {
					i = 1;
				}
				setIcon(UIManager.getDefaults().getIcon(animationFrameName + i));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				i++;
			}
		}
	}
}
