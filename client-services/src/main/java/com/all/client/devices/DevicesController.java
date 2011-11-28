package com.all.client.devices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.app.AppListener;
import com.all.client.model.DeviceBase;
import com.all.commons.Environment;
import com.all.detectdevices.DetectDevicesImpl;
import com.all.detectdevices.DismountDevice;
import com.all.detectdevices.entity.Device;
import com.all.detectdevices.interfaces.ListenerDevice;
import com.all.messengine.MessEngine;
import com.all.messengine.MessageListener;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.AllMessage;

public class DevicesController implements AppListener, ListenerDevice {

	private static Log log = LogFactory.getLog(DevicesController.class);
	private final DetectDevicesImpl detectDevices;
	private Thread deviceThread;
	private Semaphore devicesLock = new Semaphore(0);

	private Map<DeviceBase, Device> devices = new HashMap<DeviceBase, Device>();
	private MessEngine messEngine;

	public DevicesController() {
		detectDevices = new DetectDevicesImpl();
		detectDevices.addListenerDevice(this);
		ThreadGroup parentThreadGroup = Thread.currentThread().getThreadGroup();
		log.info("parent thread group:" + Thread.currentThread().getThreadGroup().getName());
		deviceThread = new Thread(parentThreadGroup, new Runnable() {
			@Override
			public void run() {
				log.info("Initializing devices detector");
				try {
					if (!Environment.isLinux()) {
						// TODO: remove this line
						// detectDevices.start();
					}
				} catch (Exception e) {
					log.error(e, e);
				}
				log.info("Terminating devices detector");
				devicesLock.release();
			}

			@Override
			protected void finalize() throws Throwable {
				log.info("finalizing devices detector");
				super.finalize();
			}
		});
		deviceThread.setDaemon(true);
		deviceThread.setName("DevicesDetectorBridge");
	}

	public synchronized void setMessEngine(MessEngine messEngine) {
		this.messEngine = messEngine;

		this.messEngine.addMessageListener(MessEngineConstants.DISCONNEC_DEVICE_TYPE,
				new MessageListener<AllMessage<DeviceBase>>() {
					@Override
					public void onMessage(AllMessage<DeviceBase> message) {
						DeviceBase deviceBase = message.getBody();
						disconnect(deviceBase);
					}
				});

		Iterator<Entry<DeviceBase, Device>> iterator = devices.entrySet().iterator();
		List<DeviceBase> devicesBase = new ArrayList<DeviceBase>();
		while (iterator.hasNext()) {
			Map.Entry<DeviceBase, Device> device = iterator.next();
			devicesBase.add(device.getKey());
		}
		this.messEngine.send(new AllMessage<List<DeviceBase>>(MessEngineConstants.SET_DEVICES_TYPE, devicesBase));
	}

	@Override
	public synchronized void mountDevice(Device device) {
		if (device == null) {
			return;
		}
		log.debug("Adding device: " + device.getName() + " - " + device.getPath() + " -> "
				+ ReflectionToStringBuilder.toString(device));
		ExternalDriveDevice dev = new ExternalDriveDevice(device);
		devices.put(dev, device);

		if (this.messEngine != null) {
			this.messEngine.send(new AllMessage<DeviceBase>(MessEngineConstants.ADD_DEVICE_TYPE, dev));
		}
	}

	@Override
	public synchronized void unMountDevice(Device unmountedDevice) {
		if (unmountedDevice == null) {
			return;
		}
		log.debug("Removing device: " + ReflectionToStringBuilder.toString(unmountedDevice));
		// Since the device is unmounted already we need to search the device on the
		// devices list to remove it!!!
		Map.Entry<DeviceBase, Device> device = null;
		log.debug(" Devices on list " + devices.size());
		synchronized (devices) {
			Iterator<Entry<DeviceBase, Device>> iterator = devices.entrySet().iterator();
			while (iterator.hasNext()) {
				try {
					device = iterator.next();
					if (device.getValue().getName().equals(unmountedDevice.getName())) {
						iterator.remove();
						break;
					}
				} catch (ClassCastException e) {
					device = null;
					log.error(e, e);
				}
			}
		}
		if (this.messEngine != null) {
			this.messEngine.send(new AllMessage<DeviceBase>(MessEngineConstants.REMOVE_DEVICE_TYPE, device.getKey()));
		}
	}

	@Override
	public void initialize() {
		deviceThread.start();
	}

	@Override
	public void destroy() {
		deviceThread.setContextClassLoader(null);
		ThreadGroup parentThreadGroup = Thread.currentThread().getThreadGroup();
		Thread killThread = new Thread(parentThreadGroup, new Runnable() {
			@Override
			public void run() {
				// Kill the thread if it takes too long!
				try {
					Thread.sleep(10000);
					log.info("interrupting device thread");

					deviceThread.interrupt();
					devicesLock.release();
				} catch (Exception e) {
				}
			}

			@Override
			protected void finalize() throws Throwable {
				log.info("finalizing device detection killer");
				super.finalize();
			}
		});
		killThread.setName("DevicesDetectionKiller");
		killThread.setDaemon(true);
		killThread.start();
		try {
			devicesLock.acquire();
		} catch (InterruptedException e) {
			log.error(e, e);
		}
		devices.clear();
		try {
			detectDevices.removeListenerDevice(this);
			detectDevices.stop();
		} catch (Exception e) {
			log.error("Unexpected error stopping detectDevices.", e);
		}
	}

	public void disconnect(DeviceBase deviceBase) {
		try {
			DismountDevice dismount = new DismountDevice(detectDevices);
			DeviceBase deviceBaseKey = deviceBase;
			Device device = devices.get(deviceBaseKey);

			if (detectDevices.isMac()) {
				boolean isArrayDevice = dismount.isArrayDevice(device);

				if (isArrayDevice) {
					dismount.dismountDeviceMacOS(device);
				} else {
					dismount.dismountDeviceMacOS(device);
					dismount.ejectDeviceMacOS(device);
				}
			}
			if (detectDevices.isWindows()) {
				dismount.dismountDeviceWindows(device);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
}
