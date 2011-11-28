package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.i18n.Messages;
import com.all.shared.model.ModelCollection;

public class UnimportedItunesDialog extends AllDialog {

	public enum ReceiveContentResult {
		CANCEL, DELETE_ALL, KEEP_ALL;
	}

	private static final long serialVersionUID = 1L;
	private static final Dimension DEFAULT_SIZE = new Dimension(360, 480);
	private final ContentPanel contentPanel;

	private ModelCollection model;

	private final ViewEngine viewEngine;

	public UnimportedItunesDialog(Window owner, final ModelCollection model, Messages messages, ViewEngine viewEngine) {
		super((JFrame) owner, messages);
		this.viewEngine = viewEngine;
		this.model = model;

		// TODO: Validate when there is no model stored

		this.contentPanel = new ContentPanel(this.model, viewEngine.get(Model.TRACK_REPOSITORY));
		init();
		setModal(true);
	}

	private void init() {
		MultiLayerDropTargetListener dndListener = new MultiLayerDropTargetListener();
		this.setDropTarget(new DropTarget(this, dndListener));
		dndListener.addDragListener(this, new MainFrameDragOverListener(this, getMessages()));

		contentPanel.getLeftButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.DELETE_ITUNES_UNIMPORTED_FILES);
			}
		});

		contentPanel.getRightButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.send(Actions.Library.SAVE_ITUNES_UNIMPORTED_FILES, new ValueAction<ModelCollection>(model));
			}
		});

		contentPanel.getRightButton().addActionListener(new CloseListener());
		contentPanel.getLeftButton().addActionListener(new CloseListener());

		this.pack();
		this.setModal(false);
		this.setAlwaysOnTop(true);
		this.initializeContentPane();
		this.setSize(DEFAULT_SIZE);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("unimportedItunes.title");
	}

	@Override
	JComponent getContentComponent() {
		return contentPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		contentPanel.internationalize(messages);
	}

}
