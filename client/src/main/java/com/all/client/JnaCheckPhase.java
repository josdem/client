package com.all.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.app.AppListener;
import com.all.commons.Environment;

public class JnaCheckPhase implements AppListener {
	private static final Log log = LogFactory.getLog(JnaCheckPhase.class);

	@Override
	public void initialize() {
		if (System.getProperties().get("jna.library.path") == null) {
			
			String jnaLibraryPath = null;
			
			if (Environment.isWindows()) {
				jnaLibraryPath = "src/main/os/windows/native";
			} else if (Environment.isMac()) {
				jnaLibraryPath = "src/main/os/mac/native";
			} else if (Environment.isLinux()){
				jnaLibraryPath = "src/main/os/linux/native";
			}

			if (jnaLibraryPath != null) {
				log.debug("jna.library.path not set, setting it to " + jnaLibraryPath);
				System.getProperties().put("jna.library.path", jnaLibraryPath);
			} else {
				log.debug("jna.library.path not set, could not determine OS to set appropriate");
			}
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
}
