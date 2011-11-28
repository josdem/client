package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.GrayBackgroundedLoaderPanel;
import com.all.client.view.dnd.DragOverListener;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.SelectTrackContainerAction;
import com.all.core.events.LibrarySyncEvent;
import com.all.core.events.LibrarySyncEventType;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;
import com.all.shared.model.Root;

public abstract class LibraryPanel extends JPanel implements Internationalizable, View {
	private static final long serialVersionUID = 1L;

	private static final int EXPAND_TIME = 1000;
	private static final Color BG_COLOR = new Color(255, 51, 153);
	private static final Insets COLLAPSED_LIBRARY_CONTAINTER_INSETS = new Insets(0, 0, 0, 2);
	private static final Dimension EXPANDED_SIZE = new Dimension(202, 630);
	private static final Dimension EXPANDED_MAX_SIZE = new Dimension(202, Integer.MAX_VALUE);

	public static final Dimension COLLAPSED_SIZE = new Dimension(30, 630);
	public static final Dimension COLLAPSED_MAX_SIZE = new Dimension(30, Integer.MAX_VALUE);

	private HeaderPanel headerPanel = null;
	private ContentPanel contentPanel = null;
	private FooterPanel footerPanel;
	private JXLayer<JPanel> libraryContainer;
	private JXLayer<JPanel> collapsedLibraryContainer;

	private CollapsedLibraryPanel collapsedLibrary;

	private Observable<ObservValue<Root>> closed = new Observable<ObservValue<Root>>();

	private boolean expanded = true;

	protected boolean newSelected;

	private final MultiLayerDropTargetListener multiLayerDropTargetListener;

	private LibraryState state = new LibraryState();

	private GrayBackgroundedLoaderPanel loaderPanel;

	private boolean isActiveMusicPanel = false;

	private GridBagConstraints loaderLibraryConstraints;

	private final boolean remote;

	private Root root;

	public LibraryPanel(ContentPanel contentPanel, HeaderPanel headerPanel, FooterPanel footerPanel,
			CollapsedLibraryPanel collapsedLibrary, MultiLayerDropTargetListener listener, boolean remote) {
		super();
		this.contentPanel = contentPanel;
		this.headerPanel = headerPanel;
		this.footerPanel = footerPanel;
		this.collapsedLibrary = collapsedLibrary;
		this.multiLayerDropTargetListener = listener;
		this.remote = remote;

		wireListener(listener);

		state.setSelected(false);
		getLoaderPanel().setVisible(false);
		
		initGui();
	}

	private void wireListener(MultiLayerDropTargetListener listener) {
		headerPanel.getCollapseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				collapse();
			}
		});

		DragOverListener dragOverListener = new DragOverListener() {
			boolean wasCollapsed = false;
			private long timeToExpand;

			@Override
			public void dragEnter(DraggedObject dragObject) {
				wasCollapsed = !isExpanded();
				timeToExpand = new Date().getTime() + EXPAND_TIME;
				that().revalidate();
				that().repaint();
			}

			@Override
			public void dragExit(boolean dropped) {
				if (wasCollapsed && !dropped) {
					collapse();
				}
				that().revalidate();
				that().repaint();
			}

			@Override
			public void dropOcurred(boolean success) {
				if (wasCollapsed) {
					collapse();
				}
				that().revalidate();
				that().repaint();
			}

			@Override
			public void updateLocation(Point location) {
				if (!isExpanded() && timeToExpand < new Date().getTime()) {
					expand();
				}
			}

			@Override
			public void dragAllowedChanged(boolean newStatus) {
			}

			@Override
			public Class<?>[] handledTypes() {
				return null;
			}
		};
		listener.addDragListener(this, dragOverListener);
	}

	private JComponent that() {
		return this;
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		multiLayerDropTargetListener.removeListeners(this);
		contentPanel.destroy(viewEngine);
		headerPanel.destroy(viewEngine);
		footerPanel.destroy(viewEngine);
		collapsedLibrary.destroy(viewEngine);
	}

	public void onSelectedRootChanged(Root root) {
		state.setSelected((root.equals(root)));
		refreshView();
	}

	public void onSyncDownload(LibrarySyncEvent syncEvent) {
		if (root != null && syncEvent.getOwner().equals(root.getOwnerMail())) {
			boolean started = syncEvent.getType() == LibrarySyncEventType.SYNC_STARTED;
			getLoaderPanel().setVisible(started);
			state.setSynching(started);
			refreshView();
		}
	}
	
	@Override
	public void internationalize(Messages messages){
		headerPanel.internationalize(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
		headerPanel.setMessages(messages);
		contentPanel.setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		footerPanel.removeMessages(messages);
		headerPanel.removeMessages(messages);
		contentPanel.removeMessages(messages);
	}

	private void initGui() {
		GridBagConstraints collapsedLibraryContainerConstraints = new GridBagConstraints();
		collapsedLibraryContainerConstraints.insets = COLLAPSED_LIBRARY_CONTAINTER_INSETS;
		collapsedLibraryContainerConstraints.gridy = 0;
		collapsedLibraryContainerConstraints.fill = GridBagConstraints.BOTH;
		collapsedLibraryContainerConstraints.weightx = 1.0;
		collapsedLibraryContainerConstraints.weighty = 1.0;
		collapsedLibraryContainerConstraints.gridx = 0;
		
		loaderLibraryConstraints = new GridBagConstraints();
		loaderLibraryConstraints.gridy = 0;
		loaderLibraryConstraints.fill = GridBagConstraints.BOTH;
		loaderLibraryConstraints.weightx = 1.0;
		loaderLibraryConstraints.weighty = 1.0;
		loaderLibraryConstraints.gridx = 0;
		
		this.setLayout(new GridBagLayout());
		this.setBackground(BG_COLOR);
		
		this.setName("backgroundContentPane");
		this.setOpaque(false);
		refreshLibraryContainer();
		this.add(getLibraryContainer(), collapsedLibraryContainerConstraints);
		refreshView();
		this.add(getCollapsedLibraryContainer(), collapsedLibraryContainerConstraints);
		
		if (remote) {
			loaderLibraryConstraints.insets = new Insets(20, 0, 0, 0);
		} else {
			loaderLibraryConstraints.insets = COLLAPSED_LIBRARY_CONTAINTER_INSETS;
		}
		this.add(getLoaderPanel(), loaderLibraryConstraints);
		
		setComponentZOrder(getLibraryContainer(), 1);
		setComponentZOrder(getCollapsedLibraryContainer(), 1);
		setComponentZOrder(getLoaderPanel(), 0);
	}

	private void refreshLibraryContainer() {
		getLibraryContainer().invalidate();
		getLibraryContainer().repaint();
	}
	
	public void initialize(final ViewEngine viewEngine, final Root root) {
		this.root = root;

		collapsedLibrary.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				expand();
				viewEngine.send(Actions.View.SELECT_TRACKCONTAINER, new SelectTrackContainerAction(root, root));
			}
		});

		contentPanel.initialize(viewEngine);
		headerPanel.initialize(viewEngine);
		footerPanel.initialize(viewEngine);
		collapsedLibrary.initialize(viewEngine);

		headerPanel.getCloseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closed.fire(new ObservValue<Root>(root));
			}
		});

		viewEngine.request(Actions.Library.IS_SYNCH_DOWNLOAD, root.getOwnerMail(), new ResponseCallback<Boolean>() {
			@Override
			public void onResponse(Boolean t) {
				state.setSynching(t);
			}
		});
		state.setSelected(true);
		refreshView();
	}

	private GrayBackgroundedLoaderPanel getLoaderPanel() {
		if (loaderPanel == null) {
			loaderPanel = new GrayBackgroundedLoaderPanel();
		}
		return loaderPanel;
	}

	protected JXLayer<JPanel> getCollapsedLibraryContainer() {
		if (collapsedLibraryContainer == null) {
			JPanel collapsedLibraryPanel = new JPanel();
			collapsedLibraryPanel = new JPanel();
			collapsedLibraryPanel.setBackground(Color.gray);
			collapsedLibraryPanel.setLayout(new BorderLayout());
			collapsedLibraryPanel.add(collapsedLibrary, BorderLayout.CENTER);
			collapsedLibraryContainer = new JXLayer<JPanel>(collapsedLibraryPanel);
			collapsedLibraryContainer.setUI(new AbstractLayerUI<JPanel>() {
				@Override
				protected void paintLayer(Graphics2D g2, JXLayer<JPanel> panel) {
					super.paintLayer(g2, panel);
					switch (state.getStatus()) {
					case INACTIVE:
						g2.setColor(new Color(0, 0, 0, 75));
						g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
						break;
					case SYNCHING:
						g2.setColor(new Color(77, 77, 77, 100));
						g2.fillRect(0, 0, panel.getWidth(), 45);
						break;
					default:
						break;
					}
				}
			});
		}
		return collapsedLibraryContainer;
	}

	private JXLayer<JPanel> getLibraryContainer() {
		if (libraryContainer == null) {
			JPanel libraryPanel = new JPanel();
			libraryPanel = new JPanel();
			libraryPanel.setBackground(Color.gray);
			libraryPanel.setLayout(new BorderLayout());
			libraryPanel.add(headerPanel, BorderLayout.NORTH);
			libraryPanel.add(contentPanel, BorderLayout.CENTER);
			libraryPanel.add(footerPanel, BorderLayout.SOUTH);
			libraryContainer = new JXLayer<JPanel>(libraryPanel);
			libraryContainer.setUI(new AbstractLayerUI<JPanel>() {
				@Override
				protected void paintLayer(Graphics2D g2, JXLayer<JPanel> panel) {
					super.paintLayer(g2, panel);
					switch (state.getStatus()) {
					case INACTIVE:
						if (!isActiveMusicPanel) {
							g2.setColor(new Color(0, 0, 0, 75));
							g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
						}
						break;
					case SYNCHING:
						if (remote && !isActiveMusicPanel) {
							g2.setColor(new Color(77, 77, 77, 100));
							g2.fillRect(0, 0, panel.getWidth(), 20);
						}
						break;
					default:
						break;
					}
				}
			});
		}
		return libraryContainer;
	}

	public void expand() {
		Sound.LIBRARY_EXPAND.play();
		collapsedLibrary.setVisible(false);
		getLibraryContainer().setVisible(true);
		refreshLibraryContainer();
			this.setSize(EXPANDED_SIZE);
			this.setPreferredSize(EXPANDED_SIZE);
			this.setMinimumSize(EXPANDED_SIZE);
			this.setMaximumSize(EXPANDED_MAX_SIZE);

		loaderLibraryConstraints.insets = new Insets(20, 0, 0, 0);
		getLoaderPanel().addLoader();
		this.add(getLoaderPanel(), loaderLibraryConstraints);
		setComponentZOrder(getLoaderPanel(), 0);

		expanded = true;
	}

	public void collapse() {
		Sound.LIBRARY_COLLAPSE.play();
		collapsedLibrary.setVisible(true);

		loaderLibraryConstraints.insets = new Insets(45, 0, 0, 0);
		getLoaderPanel().removeLoader();
		this.add(getLoaderPanel(), loaderLibraryConstraints);
		setComponentZOrder(getLoaderPanel(), 0);

		getLibraryContainer().setVisible(false);
		this.setSize(COLLAPSED_SIZE);
		this.setPreferredSize(COLLAPSED_SIZE);
		this.setMinimumSize(COLLAPSED_SIZE);
		this.setMaximumSize(COLLAPSED_MAX_SIZE);
		expanded = false;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public ObserverCollection<ObservValue<Root>> onClose() {
		return closed;
	}

	private void refreshView() {
		refreshLibraryContainer();
		getCollapsedLibraryContainer().invalidate();
		getCollapsedLibraryContainer().repaint();
	}

	public void setActiveMusicPanel(boolean isActive) {
		this.isActiveMusicPanel = isActive;
		refreshLibraryContainer();
	}

	enum LibraryDisplayStatus {
		SELECTED, INACTIVE, SYNCHING;
	}

	class LibraryState {
		private boolean selected = false;
		private boolean synching = false;

		public LibraryDisplayStatus getStatus() {
			if (synching) {
				return LibraryDisplayStatus.SYNCHING;
			}
			if (selected) {
				return LibraryDisplayStatus.SELECTED;
			}
			return LibraryDisplayStatus.INACTIVE;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public void setSynching(boolean synching) {
			// this.synching = synching;
		}
	}
}
