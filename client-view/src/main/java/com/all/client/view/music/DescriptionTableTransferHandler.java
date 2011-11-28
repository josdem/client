package com.all.client.view.music;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.LocalTrack;
import com.all.client.model.ModelTransfereable;
import com.all.client.view.components.Table;
import com.all.core.model.Model;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelSource;
import com.all.shared.model.ModelSourceProvider;
import com.all.shared.model.Track;

public class DescriptionTableTransferHandler extends TransferHandler {

	// private Log log = LogFactory.getLog(this.getClass());

	private final ViewEngine viewEngine;
	private final ModelSourceProvider sourceProvider;

	public DescriptionTableTransferHandler(ViewEngine viewEngine) {
		this(viewEngine, null);
	}

	public DescriptionTableTransferHandler(ViewEngine viewEngine, ModelSourceProvider sourceProvider) {
		this.viewEngine = viewEngine;
		this.sourceProvider = sourceProvider;
	}

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected Transferable createTransferable(JComponent c) {
		return createTransferableFromTable((Table<Track, ?>) c);
	}

	public ModelTransfereable createTransferableFromTable(Table<Track, ?> table) {
		ModelCollection modeldragged = new ModelCollection();
		int[] selectedRows = table.getSelectedRows();
		List<Track> tracks = new ArrayList<Track>();
		for (int i = 0; i < selectedRows.length; i++) {
			tracks.add(table.getValue(selectedRows[i]));
		}
		modeldragged.getTracks().addAll(tracks);
		verifyModelRemote(modeldragged, tracks);
		ModelSource source = null;
		if (sourceProvider == null) {
			source = modeldragged.isRemote() ? ModelSource.remote() : ModelSource.local();
		} else {
			source = sourceProvider.getSource();
		}
		return new ModelTransfereable(source, modeldragged, viewEngine.get(Model.TRACK_REPOSITORY));
	}

	private void verifyModelRemote(ModelCollection modeldragged, List<Track> tracks) {
		if (!tracks.isEmpty()) {
			modeldragged.setRemote(!(tracks.get(0) instanceof LocalTrack));
		}
	}

	@Override
	public boolean importData(TransferSupport support) {
		return true;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		return false;
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

}