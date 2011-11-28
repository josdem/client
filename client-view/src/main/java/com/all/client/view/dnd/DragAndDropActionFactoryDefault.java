package com.all.client.view.dnd;

import java.awt.Container;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DragAndDropActionFactoryDefault implements DragAndDropActionFactory {
	ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	@Override
	public DragAndDropAction getAction(Container frame) {
		DragAndDropActionThreaded dragAndDropActionImpl = new DragAndDropActionThreaded(frame);
		dragAndDropActionImpl.start(executorService);
		return dragAndDropActionImpl;
	}

	class DragAndDropActionThreaded extends DragAndDropActionImpl implements Runnable {
		private ScheduledFuture<?> scheduledFuture;

		public DragAndDropActionThreaded(Container currentTriggerFrame) {
			super(currentTriggerFrame);
		}

		public void start(ScheduledExecutorService executorService) {
			stop();
			this.scheduledFuture = executorService.scheduleAtFixedRate(this, 50, 50, TimeUnit.MILLISECONDS);
		}

		@Override
		protected void stop(boolean causeOfDrop) {
			super.stop(causeOfDrop);
			stop();
		}

		private void stop() {
			if (this.scheduledFuture != null) {
				this.scheduledFuture.cancel(false);
				this.scheduledFuture = null;
			}
		}

		@Override
		public void run() {
			updateDragOver();
		}

	}
}
