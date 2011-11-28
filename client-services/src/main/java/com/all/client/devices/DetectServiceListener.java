package com.all.client.devices;

import java.io.File;

public interface DetectServiceListener {
	void deviceConnected(File root);

	void deviceDisconnected(File root);
}
