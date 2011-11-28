package com.all.client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.UIManager;

import com.all.appControl.control.ViewEngine;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.model.Model;
import com.all.shared.alert.Alert;

public class PulseBubbleButton extends JButton {

	private static final long serialVersionUID = 4193570560979874732L;

	private static final Dimension DEFAULT_BUTTON_SIZE = new Dimension(42, 42);

	private static final String ALERT_STATE = "purplePulseBubbleButton";

	private static final String NORMAL_STATE = "pulseBubbleButton";

	private final PulseAnimationTask pulseAnimationTask = new PulseAnimationTask();

	private final ExecutorService executor = Executors.newCachedThreadPool(new IncrementalNamedThreadFactory(
			"PulseAnimateThread"));

	private AtomicBoolean animating = new AtomicBoolean();

	private final ViewEngine viewEngine;

	public PulseBubbleButton(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setMinimumSize(DEFAULT_BUTTON_SIZE);
		this.setMaximumSize(DEFAULT_BUTTON_SIZE);
		this.setPreferredSize(DEFAULT_BUTTON_SIZE);
		this.setName(NORMAL_STATE);
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopAnimation();
			}
		});
	}

	public synchronized void animate() {
		if (animating.compareAndSet(false, true)) {
			executor.submit(pulseAnimationTask);
		}
	}

	public synchronized void stopAnimation() {
		animating.set(false);
		updateStatus();
	}

	public synchronized void updateStatus() {
		if(!animating.get()){
			Collection<Alert> collection = viewEngine.get(Model.CURRENT_ALERTS);
			if (collection == null || collection.isEmpty()) {
				setCurrentValues(NORMAL_STATE);
			} else {
				setCurrentValues(ALERT_STATE);
			}
		}
	}

	private void setCurrentValues(String name) {
		this.setIcon(null);
		this.setName(name);
		this.repaint();
	}

	class PulseAnimationTask implements Runnable {

		private static final int ANIMATION_CYCLES = 8;
		private final String[] animationFrames = new String[] { "icons.pulseAnimationFrame0", "icons.pulseAnimationFrame1",
				"icons.pulseAnimationFrame2", "icons.pulseAnimationFrame3" };

		@Override
		public void run() {
			PulseBubbleButton.this.setName("");
			animationLoop: for (int i = 0; i < ANIMATION_CYCLES; i++) {
				for (String animationFrame : animationFrames) {
					if (!animating.get()) {
						break animationLoop;
					}
					PulseBubbleButton.this.setIcon(UIManager.getIcon(animationFrame));
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
				}
			}
			animating.set(false);
			updateStatus();
		}

	}

}
