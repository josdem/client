package com.all.client.view.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.ModelTransfereable;
import com.all.shared.model.ModelCollection;

public final class ClipboardConverter {
	
	private static Log log = LogFactory.getLog(ClipboardConverter.class);
	
	private ClipboardConverter() {
		
	}
	
	public static ModelCollection getModelCollection() {
		ModelCollection model = null;
		try {
			Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (clipBoard != null) {
				Transferable transferable = clipBoard.getContents(null);
				model = (ModelCollection) transferable.getTransferData(ModelTransfereable.MODEL_FLAVOR);
				return model;
			}
		} catch (UnsupportedFlavorException e) {
			return new ModelCollection();
		} catch (IOException e) {
			return new ModelCollection();
		} catch (Exception e) {
			log.warn("expected exception when the flavor is not register", e);
		}
		return new ModelCollection();
	}
}
