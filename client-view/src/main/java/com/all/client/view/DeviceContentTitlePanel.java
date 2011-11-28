package com.all.client.view;

import java.awt.Point;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.DragOverListener;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.FileDragSource;
import com.all.client.view.dnd.FileSelection;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.actions.DevicesCopyAction;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.ByteProgressEvent;
import com.all.core.events.Events;
import com.all.core.events.FileProgressEvent;
import com.all.event.EmptyEvent;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.i18n.Messages;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Root;
import com.all.shared.stats.usage.UserActions;

public final class DeviceContentTitlePanel extends ContentTitlePanel {

	private static final long serialVersionUID = 1L;

	protected static final String TITLE_LABEL_NAME_OVER = "myMusicDragOverLabel";

	protected static final String NAME = "deviceHeaderBackgroundPanel";

	protected static final String NAME_OVER = "deviceHeaderBackgroundPanelOver";

	private final Root deviceRoot;

	private final MultiLayerDropTargetListener multiLayer;

	private MyDeviceLabelDropListener myDeviceLabelDropListener;

	private DeviceCopyListenerImpl listener;

	private final ViewEngine viewEngine;

	private final Refreshable refreshable;

	private final DialogFactory dialogFactory;

	private Listener<FileProgressEvent> fileProgressListener;

	private Listener<ByteProgressEvent> byteProgressListener;

	private Listener<EmptyEvent> startCopyListener;

	private Listener<EmptyEvent> finishCopyListener;

	public DeviceContentTitlePanel(Root deviceRoot, MultiLayerDropTargetListener multiLayer, ViewEngine viewEngine,
			Refreshable refreshable, DialogFactory dialogFactory) {
		super();
		this.deviceRoot = deviceRoot;
		this.multiLayer = multiLayer;
		this.viewEngine = viewEngine;
		this.refreshable = refreshable;
		this.dialogFactory = dialogFactory;

		createProgressListeners();
		myDeviceLabelDropListener = new MyDeviceLabelDropListener(getTitleLabel(), this);
	}

	@Override
	public void initGui() {
		super.initialize();
		this.setName(NAME);
		FileDragSource.addDragSource(getTitleLabel(), new FileSelection() {
			@Override
			public List<File> selectedObjects(Point point) {
				List<File> files = new LinkedList<File>();
				for (File file : deviceRoot.getRootFiles()) {
					for (File file2 : file.listFiles()) {
						if (!file2.isHidden()) {
							files.add(file2);
						}
					}
				}
				return files;
			}

			@Override
			public boolean isFromExternalDevices(Point point) {
				return true;
			}
		});
		multiLayer.addDragListener(getTitleLabel(), (DragOverListener) myDeviceLabelDropListener);
		multiLayer.addDropListener(getTitleLabel(), (DropListener) myDeviceLabelDropListener);
	}

	protected JLabel getTitleLabel() {
		super.getTitleLabel();
		titleLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
		return titleLabel;
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(messages.getMessage("devices.title"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		multiLayer.removeListeners(getTitleLabel());
		viewEngine.removeListener(Events.Devices.BYTE_PROGRESS, byteProgressListener);
		viewEngine.removeListener(Events.Devices.FILE_PROGRESS, fileProgressListener);
		viewEngine.removeListener(Events.Devices.FINISH_COPY, finishCopyListener);
		viewEngine.removeListener(Events.Devices.START_COPY, startCopyListener);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		viewEngine.addListener(Events.Devices.BYTE_PROGRESS, byteProgressListener);
		viewEngine.addListener(Events.Devices.FILE_PROGRESS, fileProgressListener);
		viewEngine.addListener(Events.Devices.FINISH_COPY, finishCopyListener);
		viewEngine.addListener(Events.Devices.START_COPY, startCopyListener);
	}

	class MyDeviceLabelDropListener implements DragOverListener, DropListener {

		private final JLabel titleLabel;
		private final JPanel contentTitlePanel;

		public MyDeviceLabelDropListener(JLabel titleLabel, JPanel contentTitlePanel) {
			this.titleLabel = titleLabel;
			this.contentTitlePanel = contentTitlePanel;
		}

		@Override
		public void dragAllowedChanged(boolean newStatus) {
		}

		@Override
		public void dragEnter(DraggedObject draggedObject) {
			updateTitleLabel(DeviceContentTitlePanel.NAME_OVER, DeviceContentTitlePanel.TITLE_LABEL_NAME_OVER);
		}

		@Override
		public void dragExit(boolean dropped) {
			updateTitleLabel(DeviceContentTitlePanel.NAME, SynthFonts.BOLD_FONT12_GRAY77_77_77);
		}

		@Override
		public void dropOcurred(boolean success) {
		}

		private void updateTitleLabel(String contentTitlePanelName, String nameLabel) {
			contentTitlePanel.setName(contentTitlePanelName);
			titleLabel.setName(nameLabel);
			contentTitlePanel.repaint();
		}

		@Override
		public void updateLocation(Point location) {

		}

		@Override
		public void doDrop(DraggedObject model, Point location) {
			File file = deviceRoot.getRootFiles().iterator().next();
			createNewCopyListener();
			viewEngine.send(Actions.Devices.COPY, new DevicesCopyAction(model.get(ModelCollection.class), file));
		}

		@Override
		public boolean validateDrop(DraggedObject model, Point location) {
			return model.is(ModelCollection.class);
		}

		@Override
		public Class<?>[] handledTypes() {
			return new Class<?>[] { ModelCollection.class };
		}

	}

	private void refreshFileProgress(FileProgressEvent event) {
		listener.onFileProgress(event.getProgress(), event.getFiles(), event.getTotalFiles(), event.getName());
	}

	private void refreshByteProgress(ByteProgressEvent event) {
		listener.onByteProgress(event.getProgress(), event.getSize(), event.getTotalSize());
	}

	public void startCopy() {
		listener.onStart();
	}

	private void finishCopy() {
		listener.onFinish();
		viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Player.EXPORT_TO_EXTERNAL_DEVICES);
	}

	private void createNewCopyListener() {
		listener = new DeviceCopyListenerImpl(refreshable, dialogFactory);
	}

	private void createProgressListeners() {
		fileProgressListener = new EventListener<FileProgressEvent>() {
			@Override
			public void handleEvent(FileProgressEvent eventArgs) {
				refreshFileProgress(eventArgs);
			}
		};
		byteProgressListener = new EventListener<ByteProgressEvent>() {
			@Override
			public void handleEvent(ByteProgressEvent eventArgs) {
				refreshByteProgress(eventArgs);
			}
		};
		startCopyListener = new EventListener<EmptyEvent>() {
			@Override
			public void handleEvent(EmptyEvent eventArgs) {
				startCopy();
			}
		};
		finishCopyListener = new EventListener<EmptyEvent>() {
			@Override
			public void handleEvent(EmptyEvent eventArgs) {
				finishCopy();
			}
		};
	}
}
