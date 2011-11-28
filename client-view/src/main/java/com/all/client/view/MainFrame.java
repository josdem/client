package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.AllClientFrame;
import com.all.client.view.components.MyMusicDnDPanel;
import com.all.client.view.contacts.ContactListMainPanel;
import com.all.client.view.contacts.ContactListPanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.MainFrameDragOverListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.listeners.MainFrameResizerMouseListener;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.common.view.util.DisplayManager;
import com.all.core.events.Events;
import com.all.core.model.ContactCollection;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.i18n.Messages;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.shared.model.ContactInfo;

@Component
public class MainFrame extends AllClientFrame {
	private static final int DEFAULT_SCREEN_HEIGHT = 600;
	private static final int DEFAULT_SCREEN_WIDTH = 800;

	private static final int TOOLTIP_DELAY = 5000;
	private static final int INITIAL_DELAY = 500;
	private static final Dimension DEFAULT_TOP_RIGHT_RESIZER_SIZE = new Dimension(2, 19);
	private static final long serialVersionUID = 7682391738958019272L;

	private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(800, 600);
	private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 600);
	private static final Point DEFAULT_TOP_RIGHT_RESIZER_LOCATION = new Point(1022, 0);

	private DisplayContainerPanel bubblePlayerPanel;

	@Autowired
	private DisplayBackPanel backBubblePanel;
	@Autowired
	private PokeballPanel pokeballPanel;
	@Autowired
	private BottomPanel bottomPanel;
	@Autowired
	private MyMusicDnDPanel myMusicDnDPanel;
	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private MainMenu mainMenu;
	@Autowired
	private DialogFactory dialogFactory;

	@Autowired
	private ContactListMainPanel contactList;

	@Autowired
	public void setUGLYDependencies(ContactListPanel contactListPanel, LocalDescriptionPanel localDescriptionPanel) {
		mainMenu.setUGLYdependencies(contactListPanel, this, localDescriptionPanel);
	}

	@Autowired
	public MainFrame(MainPanel mainPanel, Messages messages) {
		super(messages, true);
		initialize(mainPanel);
	}

	private void initialize(MainPanel mainPanel) {
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setDismissDelay(TOOLTIP_DELAY);
		ToolTipManager.sharedInstance().setInitialDelay(INITIAL_DELAY);
		this.setName("mainFrame");
		this.setSize(DEFAULT_WINDOW_SIZE);
		this.setPreferredSize(DEFAULT_WINDOW_SIZE);
		this.setMinimumSize(MINIMUM_WINDOW_SIZE);
		this.setJMenuBar(getJMenuBar());
		this.setTitle("All");
		getContentPanel().add(mainPanel, BorderLayout.CENTER);
		fixPaintBugNotWindows();
	}

	@PostConstruct
	protected void setListener() {
		setJMenuBar(mainMenu);

		getBottomLeftPanel().add(bottomPanel, BorderLayout.CENTER);
		JPanel topRightResizer = getTopRightResizer();

		this.getLayeredPane().add(topRightResizer, JLayeredPane.DRAG_LAYER);

		this.backBubblePanel.setDisplayContainerPanel(bubblePlayerPanel);
		this.backBubblePanel.recalculateBubbleBounds();

		this.getLayeredPane().add(myMusicDnDPanel, JLayeredPane.PALETTE_LAYER);
		myMusicDnDPanel.setVisible(false);

		// listen when the pokeball must be showed
		pokeballPanel.onDisplayContactFrame().add(new Observer<ObserveObject>() {
			public void observe(ObserveObject eventArgs) {
				contactList.setVisible(!contactList.isVisible());
			}
		});

		for (ActionListener listener : getCloseButton().getActionListeners()) {
			getCloseButton().removeActionListener(listener);
		}

		getCloseButton().addActionListener(new CloseAppListener(viewEngine, dialogFactory));

	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onApplicationStopped() {
		viewEngine.sendValueAction(Actions.UserPreference.SET_APPLICATION_BOUNDS, this.getBounds());
	}

	@EventMethod(Model.CURRENT_PROFILE_ID)
	public void onCurrentProfileChanged() {
		if (!isFocusOwner()) {
			requestFocusInWindow();
		}
	}

	@EventMethod(Events.Application.APP_CLOSE_ID)
	public void onAppClose() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Autowired
	@SuppressWarnings("unused")
	private void setBubblePanel(DisplayContainerPanel bubblePlayerPanel) {
		this.bubblePlayerPanel = bubblePlayerPanel;

		this.getLayeredPane().add(this.bubblePlayerPanel);
	}

	@Autowired
	public void setDragAndDrop(MultiLayerDropTargetListener dndListener, Messages messages,
			final LocalLibraryPanel localLibraryPanel) {
		this.setDropTarget(new DropTarget(this, dndListener));

		dndListener.addDragListener(this, new MainFrameDragOverListener(this, messages) {
			@Override
			public void dragEnter(DraggedObject o) {
				myMusicDnDPanel.setVisible(localLibraryPanel.isExpanded() && myMusicDnDPanel.isDropable(o));
				super.dragEnter(o);
			}

			@Override
			public void dragExit(boolean dropped) {
				if (!dropped) {
					myMusicDnDPanel.setVisible(false);
				}
				super.dragExit(dropped);
			}

			@Override
			public void dropOcurred(boolean success) {
				myMusicDnDPanel.setVisible(false);
				super.dropOcurred(success);
			}
		});
		dndListener.addDropListener(this, new DropListener() {
			@Override
			public void doDrop(DraggedObject draggedObject, Point location) {
				ContactCollection contacts = draggedObject.get(ContactCollection.class);
				List<ContactInfo> contactList = contacts.getContacts();
				final ContactInfo contactInfo = contactList.get(contactList.size() - 1);
				viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(contactInfo.getEmail()));
			}

			@Override
			public boolean validateDrop(DraggedObject draggedObject, Point location) {
				ContactCollection contacts = draggedObject.get(ContactCollection.class);
				if (!contacts.getPendingContacts().isEmpty()) {
					return false;
				}
				return true;
			}

			private Class<?>[] classes = new Class<?>[] { ContactCollection.class };

			@Override
			public Class<?>[] handledTypes() {
				return classes;
			}
		});
	}

	/*
	 * Overriding update is a well-known Swing technique to avoid flickering when other components are resized or moved
	 * 
	 * @see javax.swing.JFrame#update(java.awt.Graphics)
	 */
	@Override
	public void update(Graphics g) {
		// Keep empty, read above
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		if (messages != null) {
			messages.add(this);
			if (mainMenu != null) {
				mainMenu.setMessages(messages);
			}
		}
	}

	@Override
	public void removeMessages(Messages messages) {
		if (messages != null) {
			messages.remove(this);
			mainMenu.removeMessages(messages);
		}
	}

	private JPanel getTopRightResizer() {
		JPanel topRightResizer = new JPanel();
		topRightResizer.setName("topRightResizer");
		topRightResizer.setLocation(DEFAULT_TOP_RIGHT_RESIZER_LOCATION);
		topRightResizer.setSize(DEFAULT_TOP_RIGHT_RESIZER_SIZE);
		topRightResizer.setPreferredSize(DEFAULT_TOP_RIGHT_RESIZER_SIZE);
		MainFrameResizerMouseListener topRightListener = new MainFrameResizerMouseListener(topRightResizer,
				MainFrameResizerMouseListener.RESIZE_E);
		topRightResizer.addMouseListener(topRightListener);
		topRightResizer.addMouseMotionListener(topRightListener);
		topRightResizer.setBackground(Color.PINK);
		return topRightResizer;
	}

	public void setAutoBounds(Rectangle bounds) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		// we are always stuck to the first screen at least for now.
		int screenWidth = gs[0].getDisplayMode().getWidth();
		int screenHeight = gs[0].getDisplayMode().getHeight();
		boolean isMaximized = false;
		if (bounds.x + bounds.width > screenWidth) {
			centerFrame(bounds, screenWidth, screenHeight);
		}
		if (bounds.y + bounds.height > screenHeight) {
			centerFrame(bounds, screenWidth, screenHeight);
		}
		if (bounds.width >= screenWidth) {
			bounds.width = screenWidth - 100;
			isMaximized = true;
		}
		if (bounds.height >= screenHeight) {
			bounds.height = screenHeight - 100;
			isMaximized = true;
		}
		setBounds(bounds);
		if (isMaximized) {
			getMaximizeButton().doClick();
		}
	}

	private void centerFrame(Rectangle bounds, int screenWidth, int screenHeight) {
		bounds.x = (screenWidth - bounds.width) / 2;
		bounds.y = (screenHeight - bounds.height) / 2;
	}

	@Override
	public void internationalize(Messages messages) {
		super.internationalize(messages);
	}

	public void setRestoreBounds(Rectangle bounds) {
		Rectangle screenBounds = DisplayManager.getMaximumDisplayBounds(DisplayManager.MAIN_DISPLAY);
		Insets screenInsets = DisplayManager.getDeviceInsets(DisplayManager.MAIN_DISPLAY);
		if (!DisplayManager.belongsToMainDisplay(bounds.x)) {
			screenBounds = DisplayManager.getMaximumDisplayBounds(DisplayManager.SECONDARY_DISPLAY);
			screenInsets = DisplayManager.getDeviceInsets(DisplayManager.SECONDARY_DISPLAY);
		}

		if (bounds.x == 0 || bounds.x < (screenInsets.left + screenBounds.x)) {
			bounds.x = screenInsets.left + screenBounds.x;
		}

		if (bounds.y == 0 || bounds.y < screenInsets.top) {
			bounds.y = screenInsets.top;
		}

		if ((bounds.width == 0)) {
			bounds.width = DEFAULT_SCREEN_WIDTH;
		}
		if ((bounds.height == 0)) {
			bounds.height = DEFAULT_SCREEN_HEIGHT;
		}
		setBounds(bounds);
	}
}