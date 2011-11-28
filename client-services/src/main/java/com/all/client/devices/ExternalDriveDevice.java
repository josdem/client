package com.all.client.devices;

import java.io.File;

import com.all.client.model.DeviceBase;
import com.all.client.model.DeviceRoot;
import com.all.detectdevices.entity.Device;
import com.all.shared.model.Root;

public class ExternalDriveDevice implements DeviceBase {

	private final File file;
	private DeviceRoot root;
	private final Device device;
	private final String id;

	public ExternalDriveDevice(Device device) {
		this.device = device;
		String volumePath = device.getPath();
		String name = device.getVolumeName();
		File file = new File(volumePath);
		this.id = file.getAbsolutePath();
		this.file = file;
		if (name == null || "".equals(name.trim())) {
			name = file.getName();
		}
		if (name == null || "".equals(name.trim())) {
			name = file.getAbsolutePath();
		}
		root = new DeviceRoot(name, file);
	}
	
	@Override
	public Root getDeviceRoot() {
		return root;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ExternalDriveDevice) {
			ExternalDriveDevice dev = (ExternalDriveDevice) o;
			return (this.id.equals(dev.id));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String getDeviceIcon() {
		return "Devices.hdIcon";
	}

	@Override
	public long getTotalSize() {
		if(file != null) {
			return file.getTotalSpace();
		}
		return 0L;
	}

	@Override
	public long getUsedSpace() {
		return getTotalSize() - getFreeSpace();
	}

	@Override
	public long getFreeSpace() {
		if(file != null) {
			return file.getUsableSpace();
		}
		return 0L;
	}
	
	public boolean hasDevice(Device deviceToCompare) {
		//TODO: Maybe implement a better way to compare the devices
		return getDevice().getName().equals(deviceToCompare.getName());
	}

	public Device getDevice() {
		return device;
	}


}
