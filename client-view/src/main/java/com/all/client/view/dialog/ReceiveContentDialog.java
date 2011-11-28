package com.all.client.view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.all.client.util.TrackRepository;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observer;
import com.all.shared.model.ModelCollection;

public class ReceiveContentDialog extends AllDialog {

	public enum ReceiveContentResult {
		CANCEL, REJECT_ALL, ACCEPT_ALL;
	}

	private static final long serialVersionUID = 1L;
	private static final Dimension DEFAULT_SIZE = new Dimension(360, 390);
	private final ReceiveContentPanel receiveContentPanel;

	private Observer<ObservValue<ReceiveContentResult>> listener;

	public ReceiveContentDialog(final ModelCollection model, Messages messages, Point location,
			TrackRepository trackRepository) {
		super((Frame) null, messages);
		this.receiveContentPanel = new ReceiveContentPanel(model, messages, trackRepository);

		MultiLayerDropTargetListener dndListener = new MultiLayerDropTargetListener();
		this.setDropTarget(new DropTarget(this, dndListener));
		dndListener.addDragListener(this, new MainFrameDragOverListener(this, messages));

		receiveContentPanel.addActionListenerToRejectButton(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.observe(new ObservValue<ReceiveContentResult>(ReceiveContentResult.REJECT_ALL));
			}
		});

		receiveContentPanel.addActionListenerToAcceptAllButton(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listener.observe(new ObservValue<ReceiveContentResult>(ReceiveContentResult.ACCEPT_ALL));

			}
		});

		receiveContentPanel.addActionListenerToAcceptAllButton(new CloseListener());
		receiveContentPanel.addActionListenerToRejectButton(new CloseListener());

		this.pack();
		this.setModal(false);
		this.setAlwaysOnTop(true);
		this.initializeContentPane();
		this.setSize(DEFAULT_SIZE);
		this.setLocation(location);
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("receiveContent.title");
	}

	@Override
	JComponent getContentComponent() {
		return receiveContentPanel;
	}

	@Override
	void internationalizeDialog(Messages messages) {
		receiveContentPanel.internationalize(messages);
	}

	public void setDialogListener(Observer<ObservValue<ReceiveContentResult>> listener) {
		this.listener = listener;
	}

}
