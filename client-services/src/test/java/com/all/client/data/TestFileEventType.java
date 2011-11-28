package com.all.client.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.sun.jna.platform.FileMonitor;


public class TestFileEventType {
	@Test
	public void shouldVerifyValues() throws Exception {
		assertEquals(FileEventType.FILE_DELETED, FileEventType.from(FileMonitor.FILE_DELETED));
		assertEquals(FileEventType.FILE_NAME_CHANGED_OLD, FileEventType.from(FileMonitor.FILE_NAME_CHANGED_OLD));
		assertEquals(FileEventType.FILE_NAME_CHANGED_NEW, FileEventType.from(FileMonitor.FILE_NAME_CHANGED_NEW));
	}
}
