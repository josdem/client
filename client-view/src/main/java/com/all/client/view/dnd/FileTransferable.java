package com.all.client.view.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileTransferable implements Transferable {

	private final List<File> files;
	private final boolean isFromExternalPanel;
	public static final DataFlavor EXTERNAL_DEVICES_FLAVOR = new DataFlavor(Boolean.class, "external-devices-source");


	public FileTransferable(boolean isFromExternalPanel, List<File> files) {
		this.isFromExternalPanel = isFromExternalPanel;
		this.files = files;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(DataFlavor.javaFileListFlavor)) {
			return files;
		} else if(flavor.equals(EXTERNAL_DEVICES_FLAVOR)){
			return isFromExternalPanel;
		}else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.javaFileListFlavor, EXTERNAL_DEVICES_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(DataFlavor.javaFileListFlavor) || flavor.equals(EXTERNAL_DEVICES_FLAVOR);
	}

}
