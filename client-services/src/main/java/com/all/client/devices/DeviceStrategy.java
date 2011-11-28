package com.all.client.devices;

import com.all.client.model.DeviceBase;

public interface DeviceStrategy<T extends Object> {
	DeviceBase getDevice(T object);
}
