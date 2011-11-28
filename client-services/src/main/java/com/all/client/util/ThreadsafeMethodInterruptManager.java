package com.all.client.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadsafeMethodInterruptManager {
	private enum HitManStatus {
		IDLE, NOTHING_TO_KILL, KILLING, SUCESS, FAIL
	}

	private static final Log log = LogFactory.getLog(ThreadsafeMethodInterruptManager.class);
	private static final long TIMEOUT = 1;
	private static final long HITMAN_TIMEOUT = 13;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
	private static final int KILL_RETRIES = 5;

	public boolean start(InterruptableAction action) {
		ExecutorService executor = Executors.newFixedThreadPool(2);
		AmbassadorCommand ambassadorCommand = new AmbassadorCommand(action);
		HitmanCommand hitmanCommand = new HitmanCommand(action, ambassadorCommand);
		executor.execute(ambassadorCommand);
		executor.execute(hitmanCommand);
		try {
			// wait for the action
			ambassadorCommand.endSemaphore.tryAcquire(TIMEOUT, TIME_UNIT);
		} catch (InterruptedException e) {
		}
		// free the hitman if the process its stuck we try to kill if not then theres nothing to kill
		hitmanCommand.startSemaphore.release();
		try {
			// wait for the hitman
			hitmanCommand.endSemaphore.tryAcquire(HITMAN_TIMEOUT, TIME_UNIT);
		} catch (InterruptedException e) {
		}
		log.info("Hitman says: " + hitmanCommand.status);
		executor.shutdownNow();
		return ambassadorCommand.done && hitmanCommand.status == HitManStatus.NOTHING_TO_KILL;
	}

	class AmbassadorCommand implements Runnable {
		private final InterruptableAction action;
		private final Semaphore endSemaphore = new Semaphore(0);
		private boolean done = false;
		private Thread thread;

		public AmbassadorCommand(InterruptableAction action) {
			this.action = action;
		}

		@Override
		public void run() {
			thread = Thread.currentThread();
			try {
				action.action();
			} finally {
				done = true;
				endSemaphore.release();
				log.info("action died.");
			}
		}
	}

	class HitmanCommand implements Runnable {
		private final InterruptableAction interruptableAction;
		private final Semaphore startSemaphore = new Semaphore(0);
		private final Semaphore endSemaphore = new Semaphore(0);
		private final AmbassadorCommand actionCommand;
		private HitManStatus status = HitManStatus.IDLE;

		public HitmanCommand(InterruptableAction interruptableAction, AmbassadorCommand actionCommand) {
			this.interruptableAction = interruptableAction;
			this.actionCommand = actionCommand;
		}

		@Override
		public void run() {
			try {
				// wait
				try {
					startSemaphore.acquire();
				} catch (InterruptedException e) {
				}
				if (actionCommand.done) {
					status = HitManStatus.NOTHING_TO_KILL;
					return;
				}
				// and kill
				kill();
			} finally {
				endSemaphore.release();
				log.info("hitman died.");
			}
		}

		private void kill() {
			// someone is still processing we may have to kill!
			status = HitManStatus.KILLING;
			try {
				int attempts = 0;
				while (!actionCommand.done) {
					if (attempts == 0) {
						interruptableAction.interrupt();
					}
					attempts++;
					if (attempts > KILL_RETRIES) {
						throw new IllegalStateException("Could not kill thread.");
					}
					log.info("Thread " + actionCommand.thread + " is stuck... killing time");
					actionCommand.thread.interrupt();
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				log.fatal("Could not interrupt blocked thread", e);
			}
			if (actionCommand.done) {
				log.info("action successfully interrupted!");
				status = HitManStatus.SUCESS;
			} else {
				log.fatal("Unable to interrupt action!");
				status = HitManStatus.FAIL;
			}
		}
	}

}
