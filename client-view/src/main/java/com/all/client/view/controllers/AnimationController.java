package com.all.client.view.controllers;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.stereotype.Controller;

@Controller
public class AnimationController extends Thread {
	private final LinkedList<AnimationContainer> animations;
	private final Object lock = new Object();
	private final AnimationComparator comparator;

	public AnimationController() {
		comparator = new AnimationComparator();
		animations = new LinkedList<AnimationContainer>();
		this.setDaemon(true);
		this.setName("AnimationController");
	}

	@Override
	public void run() {
		try {
			synchronized (lock) {
				while (true) {
					Long animate = animate();
					if (animate == null) {
						lock.wait();
					} else {
						lock.wait(animate > 0 ? animate : 1);
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}

	public Long animate() throws InterruptedException {
		Collections.sort(animations, comparator);
		AnimationContainer anim = null;
		long timeout = 0;
		if (!animations.isEmpty()) {
			anim = animations.getFirst();
		}
		if (anim != null) {
			long now = System.currentTimeMillis();
			long next = anim.getNextRunOn();
			if (now >= next) {
				if (!anim.animate(now)) {
					stop(anim);
				}
				Collections.sort(animations, comparator);
				if (!animations.isEmpty()) {
					anim = animations.getFirst();
					next = anim.getNextRunOn();
				} else {
					anim = null;
				}
			}
			timeout = next - now;
			if (anim != null) {
				return timeout;
			}
		}
		return null;
	}

	public void animate(Animation animation) {
		if (animation == null) {
			return;
		}
		synchronized (lock) {
			AnimationContainer anim = new AnimationContainer(animation);
			if (!animations.contains(anim)) {
				animations.add(anim);
				lock.notifyAll();
			}
		}
	}

	public void stop(Animation animation) {
		if (animation == null) {
			return;
		}
		synchronized (lock) {
			AnimationContainer anim = new AnimationContainer(animation);
			stop(anim);
		}
	}

	private void stop(AnimationContainer anim) {
		if (anim == null) {
			return;
		}
		synchronized (lock) {
			boolean notify = false;
			for (Iterator<AnimationContainer> iter = animations.iterator(); iter.hasNext();) {
				AnimationContainer animationContainer = iter.next();
				if (anim == animationContainer || anim.equals(animationContainer)) {
					animationContainer.teardown();
					iter.remove();
					notify = true;
				}
			}
			if (notify) {
				lock.notifyAll();
			}
		}
	}

	@PostConstruct
	@Override
	public synchronized void start() {
		super.start();
	}

	@PreDestroy
	@Override
	public final void interrupt() {
		super.interrupt();
	}
}

class AnimationContainer {
	private long nextRunOn;
	private int frame;
	private final Animation animation;
	private String id;

	public AnimationContainer(Animation animation) {
		this.animation = animation;
		this.id = animation.id();
		nextRunOn = System.currentTimeMillis();

	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnimationContainer) {
			AnimationContainer other = (AnimationContainer) obj;
			return id.equals(other.id);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public boolean animate(long time) {
		if (frame == 0) {
			animation.setup();
		}
		long animate = animation.animate(frame);
		frame++;
		if (animate > 0) {
			nextRunOn = time + animate;
			return true;
		} else {
			return false;
		}
	}

	public void teardown() {
		animation.teardown();
	}

	public long getNextRunOn() {
		return nextRunOn;
	}
}

class AnimationComparator implements Comparator<AnimationContainer> {
	@Override
	public int compare(AnimationContainer o1, AnimationContainer o2) {
		return (int) (o1.getNextRunOn() - o2.getNextRunOn());
	}
}
