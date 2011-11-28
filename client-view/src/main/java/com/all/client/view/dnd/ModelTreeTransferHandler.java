package com.all.client.view.dnd;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.ModelTransfereable;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;

public class ModelTreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	private final JTree tree;
	private final boolean remoteSource;
	private final ViewEngine viewEngine;

	public ModelTreeTransferHandler(JTree tree, boolean remoteSource, ViewEngine viewEngine) {
		this.tree = tree;
		this.remoteSource = remoteSource;
		this.viewEngine = viewEngine;
	}

	@Override
	public Transferable createTransferable(JComponent c) {
		java.util.List<Object> selection = new ArrayList<Object>();
		for (TreePath path : tree.getSelectionPaths()) {
			Object model = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			selection.add(model);
		}
		ModelCollection modelCollection = new ModelCollection(selection);
		modelCollection.cleanUp();
		if (remoteSource) {
			modelCollection.setRemote(true);
		}
		if (modelCollection.isEmpty()) {
			return null;
		} else {
			return new ModelTransfereable(remoteSource ? ModelSource.remote() : ModelSource.local(), modelCollection,
					viewEngine.get(Model.TRACK_REPOSITORY));
		}
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return false;
	}

	@Override
	public void exportDone(JComponent source, Transferable data, int action) {
	}

	@Override
	public boolean importData(TransferSupport support) {
		return true;
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}
}
