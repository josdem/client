package com.all.client.services;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.EmptyTransferable;
import com.all.client.model.ModelTransfereable;
import com.all.client.util.ViewModelUtils;
import com.all.core.actions.Actions;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.Root;
import com.all.shared.model.TrackContainer;

@Service
public class ClipboardService {
	private static final Log log = LogFactory.getLog(ClipboardService.class);

	@Autowired
	private ControlEngine controlEngine;
	@Autowired
	private ModelService modelService;

	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	@ActionMethod(Actions.View.SET_CLIPBOARD_SELECTION_ID)
	public void setClipboardSelection(List<?> selection) {
		if (selection == null) {
			selection = Collections.emptyList();
		}
		controlEngine.set(Model.CLIPBOARD_SELECTION, selection, Events.View.CLIPBOARD_SELECTION_CHANGED);
	}

	@ActionMethod(Actions.View.CLIPBOARD_COPY_ID)
	public void copy() {
		Transferable transferable = null;
		List<?> selectedItems = controlEngine.get(Model.CLIPBOARD_SELECTION);
		ModelCollection collection = new ModelCollection(selectedItems);
		boolean isRemote = ViewModelUtils.isBrowsingRemoteLibrary(controlEngine);
		ModelSource source = isRemote ? ModelSource.remote() : ModelSource.local();
		transferable = new ModelTransfereable(source, collection, controlEngine.get(Model.TRACK_REPOSITORY));
		clipboard.setContents(transferable, null);
	}

	@ActionMethod(Actions.View.CLIPBOARD_PASTE_ID)
	public void paste() {
		ModelCollection model = getModelCollection(clipboard);
		TrackContainer container = controlEngine.get(Model.SELECTED_CONTAINER);
		Root root = controlEngine.get(Model.SELECTED_ROOT);
		modelService.move(model, root, container);
		clipboard.setContents(EmptyTransferable.INSTANCE, null);
	}

	private ModelCollection getModelCollection(Clipboard clipboard) {
		ModelCollection model = null;
		try {
			if (clipboard != null) {
				Transferable transferable = clipboard.getContents(null);
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
