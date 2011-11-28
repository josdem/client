package com.all.client.view.controller;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.all.client.view.controllers.Animation;
import com.all.client.view.controllers.AnimationController;

public class TestAnimationController {
	private static final Log log = LogFactory.getLog(TestAnimationController.class);

	@Test
	public void shouldDoSomething() throws Exception {
		assertTrue(true);
	}

	/**
	 * Integration test, should run this controller stand alone mode.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		AnimationController animationController = new AnimationController();
		animationController.start();
		animationController.animate(new Animation() {

			@Override
			public String id() {
				return "a";
			}

			@Override
			public long animate(int frame) {
				log.info("A" + frame);
				return 200;
			}

			@Override
			public void setup() {
				log.info("Starting . A");
			}

			@Override
			public void teardown() {
				log.info("Stoping . A");
			}
		});
		animationController.animate(new Animation() {

			@Override
			public String id() {
				return "b";
			}

			@Override
			public long animate(int frame) {
				log.info("B" + frame);
				return 100;
			}

			@Override
			public void setup() {
				log.info("Starting . B");
			}

			@Override
			public void teardown() {
				log.info("Stoping . B");
			}
		});
		Thread.sleep(2000);
		animationController.stop(new Animation() {
			@Override
			public String id() {
				return "a";
			}

			@Override
			public long animate(int frame) {
				return 10;
			}

			@Override
			public void setup() {
				log.info("Starting . DEATH");
			}

			@Override
			public void teardown() {
				log.info("Stoping . DEATH");
			}
		});
		animationController.animate(new Animation() {

			@Override
			public String id() {
				return "a";
			}

			@Override
			public long animate(int frame) {
				log.info("DUPE" + frame);
				if (frame < 50) {
					return 50;
				} else {
					return -1;
				}
			}

			@Override
			public void setup() {
				log.info("Starting . DUPE");
			}

			@Override
			public void teardown() {
				log.info("Stoping . DUPE");
			}
		});
		Thread.sleep(5000);
		animationController.interrupt();
		animationController.join();
	}

}
