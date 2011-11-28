package com.all.client.devices;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class DetectService implements Runnable {
	private Set<File> roots = new HashSet<File>();
	private Thread currentService = null;
	private DetectServiceListener listener;

	public void startService() {
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.setName("detect service");
		currentService = t;
		t.start();
	}

	public void setListener(DetectServiceListener listener) {
		this.listener = listener;
		for (File f : roots) {
			listener.deviceConnected(f);
		}
	}

	@Override
	public void run() {
		Thread currentThread = Thread.currentThread();
		synchronized (this) {
			while (currentService == currentThread) {
				try {
					File[] listRoots = File.listRoots();
					if (listRoots.length == 1 && "/".equals(listRoots[0].getAbsolutePath())) {
						// Unix like filesystem we have to delve inside it, either mac mount path: /Volumes, or linux default /mnt
						listRoots = delveUnixPath();
					}
					Set<File> newRoots = new HashSet<File>(listRoots.length);
					for (File f : listRoots) {
						newRoots.add(f);
						if (!roots.contains(f)) {
							// A root has been added
							if (listener != null) {
								listener.deviceConnected(f);
							}
							roots.add(f);
						}
					}
					for (Iterator<File> iter = roots.iterator(); iter.hasNext();) {
						File f = iter.next();
						if (!newRoots.contains(f)) {
							// A root has been removed
							if (listener != null) {
								listener.deviceDisconnected(f);
							}
							iter.remove();
						}
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private File[] delveUnixPath() {
		List<File> roots = new ArrayList<File>();
		// try /Volumes
		File dir = new File("/Volumes");
		if (dir.exists() && dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory()) {
					roots.add(f);
				}
			}
		}
		// try /mnt
		dir = new File("/mnt");
		if (dir.exists() && dir.isDirectory()) {
			for (File f : dir.listFiles()) {
				if (f.isDirectory()) {
					roots.add(f);
				}
			}
		}
		return roots.toArray(new File[] {});
	}

	public void stopService() {
		currentService = null;
	}
}
