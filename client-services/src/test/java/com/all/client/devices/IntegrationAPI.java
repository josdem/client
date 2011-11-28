package com.all.client.devices;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.detectdevices.DetectDevicesImpl;
import com.all.detectdevices.entity.Device;
import com.all.detectdevices.interfaces.ListenerDevice;

public class IntegrationAPI {

	private static Log log = LogFactory.getLog(IntegrationAPI.class);

	public static void main(String[] args) throws InterruptedException {
		checkJnaLibraryPathEnvVar();
		
		log.debug("Starting Devices Detection ------ ");
		log.debug("INSTRUCTIONS: Plug & Unplug USB Devices to test the library.");
		log.debug("IMPORTANT: Internal HARD DRIVES should't be detected by the library.");
		log.debug("This program will finish automatically after 1 minute.");
		
		IntegrationAPI integration = new IntegrationAPI();
		DetectDeviceThread thread = integration.getInstances();
		thread.start();

		Thread.sleep(60000);
		
		log.debug(" ------ Finishing Devices Detection");
		thread.stopProcess();
	}

	public DetectDeviceThread getInstances() {
		return new DetectDeviceThread();
	}

	private DetectDevicesImpl devImpl;
	private ListenerDeviceImpl listener;

	public class DetectDeviceThread extends Thread {

		public DetectDeviceThread() {
			devImpl = new DetectDevicesImpl();
			listener = new ListenerDeviceImpl();

			devImpl.addListenerDevice(listener);
		}

		public void run() {
			try {
				devImpl.start();
			} catch (Exception e) {
				log.error(e, e);
			}
		}

		public void stopProcess() {
			try {
				devImpl.stop();
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	public class ListenerDeviceImpl implements ListenerDevice {

		public void mountDevice(Device device) {
			log.debug("Device mount: " + device.getVolumeName());
			log.debug("Devices: " + devImpl.getDevices().size());
		}

		public void unMountDevice(Device device) {
			log.debug("Device unmount: " + device.getName());
		}

	}
	
	private static void checkJnaLibraryPathEnvVar() {
		if (System.getProperties().get("jna.library.path") == null) {
			String platform = System.getProperty("os.name").toLowerCase();
			String jnaLibraryPath = null;
			if (platform.startsWith("windows")) {
				jnaLibraryPath = "src/main/os/windows/native";
			} else if (platform.startsWith("mac")) {
				jnaLibraryPath = "src/main/os/mac/native";
			}

			if (jnaLibraryPath != null) {
				log.debug("jna.library.path not set, setting it to " + jnaLibraryPath);
				System.getProperties().put("jna.library.path", jnaLibraryPath);
			} else {
				log.debug("jna.library.path not set, could not determine OS to set appropriate");
			}
		}
	}


}
