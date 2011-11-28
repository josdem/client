package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.events.Events;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.shared.model.Root;

@Component
public class MiddlePanel extends JPanel implements Internationalizable {
	private static final Dimension MINIMUM_SIZE = new Dimension(792, 459);
	private static final Dimension DEFAULT_SIZE = new Dimension(1016, 627);
	private static final Log LOG = LogFactory.getLog(MiddlePanel.class);
	private static final long serialVersionUID = 1L;
	private LocalLibraryPanel libraryPanel = null;
	private Map<Root, LibraryPanel> libraries;
	private Map<String, MiddleCloseablePanel> cardPanelComponentsName = new HashMap<String, MiddleCloseablePanel>();

	private JPanel cardPanel = null;
	private JPanel containerPanel;

	private LibraryPanelFactory libraryPanelFactory;
	private transient LibraryCloseListener closeListener;
	@Autowired
	ShortcutBinder shortcutBinder;

	private Messages messages;

	private String currentView = "";
	private JPanel containerDescriptionPanel;

	private Object refreshLock = new Object();
	private Thread refresher;
	private ViewEngine viewEngine;
	private boolean appStarted = false;

	@Autowired
	PanelFactory panelFactory;

	@Autowired
	public MiddlePanel(LocalLibraryPanel libraryPanel, LibraryPanelFactory libraryPanelFactory) {
		super();
		this.libraryPanel = libraryPanel;
		this.libraryPanelFactory = libraryPanelFactory;
		this.closeListener = new LibraryCloseListener();
		this.libraries = new HashMap<Root, LibraryPanel>();
		initialize();
		startRefresh();

	}

	protected MiddlePanel() {
	}

	@Autowired
	public void setTrashPanel(TrashPanel trashPanel) {
		getCardPanel().add(trashPanel, Views.TRASH.getCardName());
	}

	@Autowired
	public void setLocalDescriptionPanel(LocalDescriptionPanel localDescriptionPanel) {
		getCardPanel().add(localDescriptionPanel, Views.LOCAL_MUSIC.getCardName());
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void initService() {
		appStarted = true;
		refreshView();
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onAppStoped() {
		stopRefresher();
	}

	@Autowired
	public void setViewEngine(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

	@EventMethod(Events.Library.LIBRARY_ROOT_ADDED_ID)
	public void onLibraryAdded(Root root) {
		LibraryPanel libPanel = libraryPanelFactory.createLibraryPanel(root);
		if (libPanel == null) {
			return;
		}

		libPanel.onClose().add(closeListener);

		libraries.put(root, libPanel);

		getContainerPanel().add(libPanel);

		libPanel.expand();
		Sound.LIBRARY_REMOTE_OPEN.play();

		defferRefresh();
	}

	@EventMethod(Events.Library.LIBRARY_ROOT_REMOVED_ID)
	public void onLibraryRemoved(Root root) {
		LibraryPanel panel = libraries.remove(root);
		panel.onClose().remove(closeListener);
		panel.removeMessages(messages);
		panel.newSelected = false;
		getContainerPanel().remove(panel);
		panel.destroy(viewEngine);
		defferRefresh();
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onShutdown() {
		stopRefresher();
		refresher = null;

	}

	private void startRefresh() {
		refresher = new Thread(new Runnable() {
			@Override
			public void run() {
				while (refresher == Thread.currentThread()) {
					try {
						synchronized (refreshLock) {
							refreshLock.wait();
						}
						Thread.sleep(400);
					} catch (InterruptedException e) {
						break;
					}
					invalidate();
					SwingUtilities.getWindowAncestor(getContainerPanel()).validate();
				}
			}
		});
		refresher.setName("middlePanelRefresherThread");
		refresher.setDaemon(true);
		refresher.start();
	}

	private void defferRefresh() {
		synchronized (refreshLock) {
			refreshLock.notifyAll();
		}
	}

	@EventMethod(value = Model.CURRENT_VIEW_ID, eager = true)
	public void onModelViewCurrentViewChanged(ValueEvent<ContainerView> event) {
		gotoView(event.getValue().getViews());
		ContainerView value = event.getValue();
		if (value.getModelContainerView() != null && value.getViews().equals(Views.HUNDRED)) {
			viewEngine.sendValueAction(Actions.View.SET_TOP_HUNDRED_CATEGORY_VIEW, event.getValue().getModelContainerView());
		}
	}

	@Autowired
	private ToolBarPanel toolbarPanel;

	private void gotoView(Views view) {
		if (!isComponentInCard(view.getCardName()) && panelFactory.createPanel(view) != null) {
			MiddleCloseablePanel createdPanel = panelFactory.createPanel(view);
			getCardPanel().add(createdPanel, view.getCardName());
			cardPanelComponentsName.put(view.getCardName(), createdPanel);
			refreshView();
			createdPanel.onClose().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					toolbarPanel.clearSelectionToolbar();
				}
			});
		}
		((CardLayout) getCardPanel().getLayout()).show(getCardPanel(), view.getCardName());
	}

	public void refreshView() {
		if (viewEngine != null && appStarted) {
			CardLayout cardLayout = (CardLayout) getCardPanel().getLayout();
			Views view = viewEngine.get(Model.CURRENT_VIEW);
			String newViewName = view.getCardName();
			if (!currentView.equals(newViewName)) {
				currentView = newViewName;
				cardLayout.show(getCardPanel(), newViewName);
			}
		}
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(DEFAULT_SIZE);
		this.setLayout(new BorderLayout());
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.add(getContainerPanel(), BorderLayout.WEST);
		this.add(getContainerDescriptionPanel(), BorderLayout.CENTER);
	}

	private JPanel getContainerDescriptionPanel() {
		if (containerDescriptionPanel == null) {
			containerDescriptionPanel = new JPanel();
			containerDescriptionPanel.setLayout(new BorderLayout());
			containerDescriptionPanel.add(getCardPanel(), BorderLayout.CENTER);
		}
		return containerDescriptionPanel;
	}

	private JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel();
			BoxLayout layout = new BoxLayout(containerPanel, BoxLayout.X_AXIS);
			containerPanel.setLayout(layout);
			containerPanel.add(libraryPanel);
		}
		return containerPanel;
	}

	private JPanel getCardPanel() {
		if (cardPanel == null) {
			CardLayout layout = new CardLayout();
			cardPanel = new JPanel(layout);
		}
		return cardPanel;
	}

	private boolean isComponentInCard(String nameCard) {
		return cardPanelComponentsName.containsKey(nameCard) ? true : false;
	}

	private final class LibraryCloseListener implements Observer<ObservValue<Root>> {
		@Override
		public void observe(ObservValue<Root> eventArgs) {
			viewEngine.send(Actions.Library.LIBRARY_ROOT_REMOVED, new ValueAction<Root>(eventArgs.getValue()));
		}
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void removeMessages(Messages messages) {
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		this.messages = messages;
	}

	private void stopRefresher() {
		try {
			if (refresher != null) {
				LOG.info("Stopping middle panel refresher...");
				refresher.interrupt();
			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}
}
