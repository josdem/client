package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.util.TimeUtil;
import com.all.client.view.components.MenuItems;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.DragOverListener;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.FileTransferable;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.dnd.ScrollPaneDragOverListener;
import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.client.view.util.MacUtils;
import com.all.core.actions.Actions;
import com.all.core.actions.DevicesCopyAction;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.common.model.ApplicationActions;
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

public class DevicePreviewPanel extends SimplePanel implements Refreshable {

	private static final long serialVersionUID = 1L;

	private static final int ROW_HEIGHT = 20;

	private static final String NAME = "previewTreeBackground";

	private JScrollPane scroll;

	private DeviceTree tree;

	private final Root root;

	private final MultiLayerDropTargetListener multiLayerDropTargetListener;

	private DropListener modelDropListener;

	private JTreeCoordinateHelper treeHelper;

	private final DialogFactory dialogFactory;

	private DragOverListener dragOverListener;

	private final ViewEngine viewEngine;

	private DeviceLoader deviceLoader;

	private JMenuItem deleteItem = null;

	private DeviceCopyListener listener;
	
	private Listener<FileProgressEvent> fileProgressListener;
	
	private Listener<ByteProgressEvent> byteProgressListener;
	
	private Listener<EmptyEvent> startCopyListener;
	
	private Listener<EmptyEvent> finishCopyListener;

	private Listener<EmptyEvent> cannotCopyListener;

	private EventListener<EmptyEvent> deviceFullListener;

	public DevicePreviewPanel(Root root, MultiLayerDropTargetListener multiLayerDropTargetListener,
			DialogFactory dialogFactory, final ViewEngine viewEngine) {
		this.root = root;
		this.multiLayerDropTargetListener = multiLayerDropTargetListener;
		this.dialogFactory = dialogFactory;
		this.viewEngine = viewEngine;
		this.setName(NAME);
		this.setLayout(new BorderLayout());
		this.add(getScroll(), BorderLayout.CENTER);
		createProgressListeners();
		setDnDrops(multiLayerDropTargetListener);
		modelDropListener = new DropListener() {
			private final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

			@Override
			public boolean validateDrop(DraggedObject draggedObject, Point location) {
				JTreeCoordinateHelper treeHelper = tree.getTreeHelper();
				DefaultMutableTreeNode defaultMutableTreeNode = treeHelper.getDefaultMutableTreeNodeFromCoordinates(location.x,
						location.y);
				if (defaultMutableTreeNode.isLeaf()) {
					return false;
				} else {
					tree.setDragOverObject(location.y);
					tree.repaint();
					return true;
				}
			}

			@Override
			public void doDrop(DraggedObject draggedObject, Point location) {
				ModelCollection model = draggedObject.get(ModelCollection.class);
				Object val = treeHelper.getValueAt(location);
				File f = null;
				if (val instanceof File) {
					f = (File) val;
				} else {
					f = DevicePreviewPanel.this.root.getRootFiles().iterator().next();
				}
				createNewCopyListener();
				viewEngine.send(Actions.Devices.COPY, new DevicesCopyAction(model, f));
				
				tree.setDragOverObject(null);
				tree.repaint();
			}

			@Override
			public Class<?>[] handledTypes() {
				return classes;
			}
		};
		dragOverListener = new DragOverListener() {
			private long lastTime;
			private Object lastValue;

			@Override
			public void updateLocation(Point location) {
				Object newValue = treeHelper.getValueAt(location);
				if (lastValue == newValue) {
					if (TimeUtil.hasPassedTwoSecond(new Date().getTime(), lastTime)) {
						treeHelper.expandNodeFromCoordinates(location.x, location.y);
					}
				} else {
					lastTime = new Date().getTime();
					lastValue = newValue;
				}
			}

			@Override
			public void dragAllowedChanged(boolean newStatus) {
			}

			@Override
			public void dragExit(boolean dropped) {
				tree.setDragOverObject(null);
				tree.repaint();
				lastValue = null;
			}

			@Override
			public void dropOcurred(boolean success) {
			}

			@Override
			public void dragEnter(DraggedObject o) {
				lastTime = new Date().getTime();
			}

			@Override
			public Class<?>[] handledTypes() {
				return null;
			}
		};

		deviceLoader = new DeviceLoader(getTree(), root);

		addMouseListener();
	}

	private void addMouseListener() {
		getTree().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				viewEngine.send(Actions.View.SELECT_TRACKCONTAINER, new SelectTrackContainerAction(root, null));
				if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
					getPopupMenu().show(getTree(), e.getX(), e.getY());
				}
			}
			
		});
	}

	private void setDnDrops(MultiLayerDropTargetListener multiLayerDropTargetListener) {
		multiLayerDropTargetListener.removeListeners(getTree());
		multiLayerDropTargetListener.removeListeners(getScroll());

	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		multiLayerDropTargetListener.addDropListener(getTree(), modelDropListener);
		multiLayerDropTargetListener.addDragListener(getTree(), dragOverListener);
		multiLayerDropTargetListener.addDragListener(getScroll(), new ScrollPaneDragOverListener(getScroll()));

		deviceLoader.start();

		getTree().addTreeExpansionListener(new TreeExpansionListener() {
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				deviceLoader.populate((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				deviceLoader.clear((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
			}
		});

		getTree().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					doDelete();
				}
			}
		});

		getDeleteItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doDelete();
			}
		});
		
			viewEngine.addListener(Events.Devices.BYTE_PROGRESS, byteProgressListener);
			viewEngine.addListener(Events.Devices.FILE_PROGRESS, fileProgressListener);
			viewEngine.addListener(Events.Devices.FINISH_COPY, finishCopyListener);
			viewEngine.addListener(Events.Devices.START_COPY, startCopyListener);
			viewEngine.addListener(Events.Devices.CANNOT_COPY, cannotCopyListener);
			viewEngine.addListener(Events.Devices.DEVICE_FULL, deviceFullListener);
	}

	private void doDelete() {
		final List<File> files = getSelectedFiles();
		if (files.isEmpty()) {
			return;
		}
		if (dialogFactory.showDeleteUsbContentDialog()) {
			viewEngine.send(Actions.Devices.DELETE, new ValueAction<List<File>>(files));
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		deviceLoader.setRunning(false);
		multiLayerDropTargetListener.removeListeners(getTree());
		viewEngine.removeListener(Events.Devices.BYTE_PROGRESS, byteProgressListener);
		viewEngine.removeListener(Events.Devices.FILE_PROGRESS, fileProgressListener);
		viewEngine.removeListener(Events.Devices.FINISH_COPY, finishCopyListener);
		viewEngine.removeListener(Events.Devices.START_COPY, startCopyListener);
		viewEngine.removeListener(Events.Devices.CANNOT_COPY, cannotCopyListener);
		viewEngine.removeListener(Events.Devices.DEVICE_FULL, deviceFullListener);
	}

	private JScrollPane getScroll() {
		if (scroll == null) {
			scroll = new JScrollPane();
			scroll.setViewportView(getTree());
		}
		return scroll;
	}

	private JTree getTree() {
		if (tree == null) {
			tree = new DeviceTree(new DefaultMutableTreeNode("LOADING..."));
			tree.setRowHeight(ROW_HEIGHT);
			tree.setName("previewTree");
			tree.setCellRenderer(new FileSystemTreeCellRenderer(viewEngine));
			tree.setDragEnabled(true);
			treeHelper = new JTreeCoordinateHelper(tree);
			tree.setTransferHandler(new TransferHandler() {
				private static final long serialVersionUID = 1L;

				@Override
				protected Transferable createTransferable(JComponent c) {
					List<File> files = getSelectedFiles();
					if (!files.isEmpty()) {
						return new FileTransferable(true, files);
					}
					return null;
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

			});
			tree.setDropTarget(null);
		}
		return tree;
	}

	private List<File> getSelectedFiles() {
		List<?> selectedValues = treeHelper.getSelectedValues();
		List<File> files = new ArrayList<File>();
		if (!selectedValues.isEmpty()) {
			for (Object object : selectedValues) {
				if (object instanceof File) {
					files.add((File) object);
				}
			}
		}
		return files;
	}

	private JMenuItem getDeleteItem() {
		if (deleteItem == null) {
			deleteItem = MenuItems.DELETE.getItem();
		}
		return deleteItem;
	}

	private JPopupMenu getPopupMenu() {
		JPopupMenu popUpMenu = new JPopupMenu();
		popUpMenu.add(getDeleteItem());
		return popUpMenu;
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void removeMessages(Messages messages) {
	}

	@Override
	public void setMessages(Messages messages) {
	}

	@Override
	public void refresh() {
		deviceLoader.refresh();
	}

	private void refreshFileProgress(FileProgressEvent event) {
		listener.onFileProgress(event.getProgress(), event.getFiles(), event.getTotalFiles(), event.getName());
	}

	private void refreshByteProgress(ByteProgressEvent event) {
		listener.onByteProgress(event.getProgress(), event.getSize(), event.getTotalSize());
	}

	public void startCopy(){
		listener.onStart();  
	}

	private void finishCopy(){
		listener.onFinish();
		viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Player.EXPORT_TO_EXTERNAL_DEVICES);
	}

	private void createNewCopyListener() {
		this.listener = new DeviceCopyListenerImpl(this, dialogFactory);
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
		cannotCopyListener = new EventListener<EmptyEvent>() {
			@Override
			public void handleEvent(EmptyEvent eventArgs) {
				listener.onCannotCopyNotExistFile();
			}
		};
		deviceFullListener = new EventListener<EmptyEvent>() {
			@Override
			public void handleEvent(EmptyEvent eventArgs) {
				listener.onDeviceFull();
			}
		};
	}
}
