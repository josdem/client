package com.all.client.view.contacts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatStatus;
import com.all.chat.ChatType;
import com.all.client.view.chat.ChatViewManager;
import com.all.client.view.components.MenuItems;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.dnd.ScrollPaneDragOverListener;
import com.all.client.view.dnd.TracksToContactListener;
import com.all.client.view.flows.AddContactFlow;
import com.all.client.view.toolbar.social.MessageContainerPanel;
import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.client.view.util.MacUtils;
import com.all.commons.Environment;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.actions.Actions;
import com.all.core.actions.ComposeView;
import com.all.core.actions.LoadContactLibraryAction;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.core.model.SubViews;
import com.all.core.model.Views;
import com.all.event.EventMethod;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.ObservePropertyChanged;
import com.all.observ.ObservedProperty;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.ContactStatus;

@Component
public final class ContactListPanel extends JPanel implements Internationalizable {
	private static final String SEPARATOR_PANEL_NAME = "separatorPanel";
	private static final Dimension SEPARATOR_PANEL_DEFAULT_SIZE = new Dimension(198, 2);
	private static final Color TREE_PANEL_BACKGROUND_COLOR = UIManager.getDefaults().getColor("contactTree.background");
	private static final long serialVersionUID = 7319187527316505775L;
	private static final Log log = LogFactory.getLog(ContactListPanel.class);
	private static final Dimension SIZE = new Dimension(220, 700);
	private static final Dimension MINIMUM_SIZE = new Dimension(220, 700);
	private JPanel contactListContainer;
	private JLabel myContactsLabel;
	private JPanel myContactsPanel;
	private JPanel bottomContactListPanel;
	private JPanel contactListPanel;
	private JScrollPane jScrollPane;
	private JPanel treePanel;
	private ContactTree contactTree;
	private ChatSelectionPanel chatSelectionPanel;

	private JMenuItem createFolderMenuItem = null;
	private JMenuItem addContactMenuItem;
	private JMenuItem deleteFolderMenuItem;
	private JMenuItem deleteContactMenuItem;
	private JMenuItem sendInvitationMenuItem;
	private JMenuItem chatMenuItem;
	private JMenuItem facebookChatMenuItem;
	private JMenuItem profileMenuItem;
	private JMenuItem remoteLibraryMenuItem;
	private JPopupMenu popUpMenu;

	private JXLayer<JPanel> loaderPanel;
	private boolean loaded = false;
	private int rowForTree = 0;
	private int xLocal = 0;
	private int yLocal = 0;
	private TreePath pathToSelect = null;

	private JViewportHelper viewportHelper = new JViewportHelper();

	@Autowired
	private DialogFactory dialogFactory;
	@Autowired
	private ViewEngine viewEngine;

	private JPanel separatorPanel;
	private ObservedProperty<ContactListPanel, Boolean> pendingEmailSelected = new ObservedProperty<ContactListPanel, Boolean>(
			this);
	private Set<ContactInfo> currentSelection = null;

	@Deprecated
	public ContactListPanel() {
	}

	@Autowired
	public ContactListPanel(ContactTree contactsTree) {
		this.contactTree = contactsTree;
		this.setSize(SIZE);
		this.setPreferredSize(SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setLayout(new GridBagLayout());
		GridBagConstraints mainConstraints = new GridBagConstraints();
		mainConstraints.gridx = 0;
		mainConstraints.gridy = 1;
		mainConstraints.ipadx = 0;
		mainConstraints.ipady = 0;
		mainConstraints.weightx = 1.0;
		mainConstraints.weighty = 1.0;
		mainConstraints.insets = new Insets(2, 0, 0, 0);
		mainConstraints.fill = GridBagConstraints.BOTH;
		this.add(getContactListContainer(), mainConstraints);
		setupTrees();
	}

	@Autowired
	public void setChatViewManager(final ChatViewManager chatViewManager) {
		contactTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					try {
						Object value = contactTree.getTreeHelper().getValueAt(e.getPoint());
						if (value instanceof String || value instanceof FolderWrapper) {
							e.consume();
							return;
						}
						ContactInfo contact = (ContactInfo) value;
						if (contact.getChatStatus() != ChatStatus.OFFLINE && contact.getChatStatus() != ChatStatus.PENDING) {
							chatViewManager.showChat(contact);
						}
					} catch (Exception ex) {
						log.error(ex, ex);
					}
				}
			}
		});

		chatSelectionPanel.onChatTypeSelected().add(new Observer<ObservePropertyChanged<ChatSelectionPanel, ChatType>>() {
			@Override
			public void observe(ObservePropertyChanged<ChatSelectionPanel, ChatType> eventArgs) {
				final ChatType newSelection = eventArgs.getValue();
				final ChatType oldSelection = eventArgs.getOldValue();

				viewEngine.request(Actions.Chat.REQUEST_SELECT_CHAT_TYPE, newSelection, new ResponseCallback<Boolean>() {

					@Override
					public void onResponse(Boolean response) {
						if (response) {
							chatSelectionPanel.selectChatType(newSelection);
						} else {
							chatSelectionPanel.selectChatType(oldSelection);
						}
					}
				});

			}
		});

		chatSelectionPanel.panelFacebook.onCloseEvent().add(new Observer<ObservValue<ChatType>>() {
			public void observe(ObservValue<ChatType> eventArgs) {
				ChatType type = eventArgs.getValue();
				// I NEED TO DO THIS BEFORE THE LOGOUT BECAUSE IF ALL NOT SELECTED
				// PENDING EMAILS ARE NOT SHOWING
				chatSelectionPanel.selectChatType(ChatType.ALL);
				chatViewManager.logout(type);
				chatSelectionPanel.activateFacebook(false);
			}
		});

		getChatMenuItem().setEnabled(true);
		getChatMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TreePath[] selectionPaths = contactTree.getSelectionPaths();
					for (TreePath treePath : selectionPaths) {
						DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						Object userObject = lastPathComponent.getUserObject();
						if (userObject instanceof ContactInfo) {
							ContactInfo contact = (ContactInfo) userObject;
							if (contact.getChatStatus() != ChatStatus.PENDING) {
								chatViewManager.showChat(contact);
							}
						}
					}
				} catch (Exception ex) {
					log.debug(ex, ex);
				}
			}
		});
		getFacebookChatMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TreePath[] selectionPaths = contactTree.getSelectionPaths();
					for (TreePath treePath : selectionPaths) {
						DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) treePath.getLastPathComponent();
						Object userObject = lastPathComponent.getUserObject();
						if (userObject instanceof ContactInfo) {
							ContactInfo contact = (ContactInfo) userObject;
							if (contact.getChatType() == ChatType.FACEBOOK && contact.getChatStatus() != ChatStatus.OFFLINE) {
								chatViewManager.showChat(contact);
							}
						}
					}
				} catch (Exception ex) {
					log.debug(ex, ex);
				}
			}
		});
	}

	@PostConstruct
	public void initialize() {
		getProfileMenuItem().setEnabled(true);
		getProfileMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TreePath[] selectionPaths = contactTree.getSelectionPaths();
					if (selectionPaths != null && selectionPaths.length > 0) {
						DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPaths[0]
								.getLastPathComponent();
						Object userObject = lastPathComponent.getUserObject();
						if (userObject instanceof ContactInfo) {
							final ContactInfo contact = (ContactInfo) userObject;
							if (!contact.isPending()) {
								viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(contact, new ComposeView(Views.PROFILE, SubViews.ALL)));
							}
						}

					}
				} catch (Exception ex) {
					log.debug(ex, ex);
				}
			}
		});
		// TODO CHANGE THIS BECAUSE THIS ONLY APPLIES FOR ALL CONTACTS
		contactTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				Set<ContactInfo> selected = new HashSet<ContactInfo>();
				TreePath[] selectionPaths = contactTree.getSelectionPaths();
				if (selectionPaths != null && selectionPaths.length > 0) {
					for (TreePath treePath : selectionPaths) {
						Object userObject = ((DefaultMutableTreeNode) treePath.getLastPathComponent()).getUserObject();
						if (userObject instanceof ContactInfo) {
							selected.add((ContactInfo) userObject);
						}
					}
				}
				if (!selected.isEmpty()) {
					currentSelection = selected;
				}
			}
		});

		getAddContactMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new AddContactFlow(viewEngine, dialogFactory).executeAdd();
			}
		});

		getSendInvitationMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendInvitation();
			}
		});

		contactTree.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (KeyEvent.VK_DELETE == e.getKeyCode()) {
					deleteSelectedContacts();
				}
			}
		});
		getDeleteContactMenuItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteSelectedContacts();
			}
		});

	}

	@Autowired
	public void setRemoteLibrary() {
		getRemoteLibraryItem().setEnabled(true);
		getRemoteLibraryItem().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					TreePath[] selectionPaths = contactTree.getSelectionPaths();
					if (selectionPaths != null && selectionPaths.length > 0) {
						DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) selectionPaths[0]
								.getLastPathComponent();
						Object userObject = lastPathComponent.getUserObject();
						if (userObject instanceof ContactInfo) {
							final ContactInfo contact = (ContactInfo) userObject;
							if (!contact.isPending()) {
								viewEngine.send(Actions.Library.LOAD_CONTACT_LIBRARY, LoadContactLibraryAction.load(contact.getEmail()));
							}
						}

					}
				} catch (Exception ex) {
					log.debug(ex, ex);
				}
			}
		});
	}

	@EventMethod(Model.SELECTED_CHAT_TYPE_ID)
	public void onChangeChatView(ChatType chatType) {
		chatSelectionPanel.selectChatType(chatType);
		chatSelectionPanel.activateFacebook(viewEngine.get(Model.UserPreference.FACEBOOK_CHAT_STATUS));
	}

	private void deleteContacts(final Set<ContactInfo> contacts) {
		if (contacts == null || contacts.isEmpty()) {
			return;
		}
		boolean confirmationResult = dialogFactory.showDeleteContactConfirmationDialog(false);
		if (confirmationResult) {
			viewEngine.sendValueAction(Actions.Social.DELETE_CONTACTS, contacts);
		}
	}

	@EventMethod(Events.Social.CONTACT_UPDATED_ID)
	public void onContactUpdated(ContactInfo contact) {
		contactTree.updateNode(contact);
	}

	@EventMethod(Events.Social.CONTACT_ADDED_ID)
	public void onContactAdded(ContactInfo contact) {
		contactTree.addNode(contact);
	}

	@EventMethod(Events.Social.CONTACT_DELETED_ID)
	public void onContactDeleted(ContactInfo contact) {
		contactTree.deleteNode(contact);
	}

	@EventMethod(Model.DISPLAYED_CONTACT_LIST_ID)
	public void onContactListUpdated(List<ContactInfo> receivedContacts) {
		List<ContactInfo> contacts = new ArrayList<ContactInfo>();
		for (ContactInfo contactInfo : receivedContacts) {
			contacts.add(contactInfo);
		}
		updateContactTree(contacts, true);
		loaded = true;
		repaintLoader();
	}

	private void repaintLoader() {
		contactListContainer.invalidate();
		getJScrollPane().invalidate();
		getJScrollPane().getViewport().invalidate();
		if (SwingUtilities.getWindowAncestor(loaderPanel) != null) {
			SwingUtilities.getWindowAncestor(loaderPanel).invalidate();
			SwingUtilities.getWindowAncestor(loaderPanel).validate();
			SwingUtilities.getWindowAncestor(loaderPanel).repaint();
		}
	}

	@Autowired
	public void setDragAndDrops(MultiLayerDropTargetListener dropListener, ViewEngine viewEngine) {
		TracksToContactListener tracksListener = new TracksToContactListener(this.contactTree, viewEngine);

		dropListener.addDragListener(this.contactTree, tracksListener);
		dropListener.addDropListener(this.contactTree, tracksListener);
		dropListener.addDragListener(getJScrollPane(), new ScrollPaneDragOverListener(getJScrollPane()));
	}

	private JXLayer<JPanel> getContactListContainer() {
		if (contactListContainer == null) {

			GridBagConstraints myContactsConstraints = new GridBagConstraints();
			myContactsConstraints.gridx = 0;
			myContactsConstraints.gridy = 0;
			myContactsConstraints.insets = new Insets(0, 1, 0, 1);
			myContactsConstraints.fill = GridBagConstraints.HORIZONTAL;

			GridBagConstraints contactListConstraints = new GridBagConstraints();
			contactListConstraints.gridx = 0;
			contactListConstraints.gridy = 2;
			contactListConstraints.fill = GridBagConstraints.BOTH;
			contactListConstraints.weightx = 1.0;
			contactListConstraints.weighty = 1.0;
			contactListConstraints.insets = new Insets(0, 1, 0, 1);

			GridBagConstraints bottomContactListConstraints = new GridBagConstraints();
			bottomContactListConstraints.gridx = 0;
			bottomContactListConstraints.gridy = 3;
			bottomContactListConstraints.fill = GridBagConstraints.HORIZONTAL;
			bottomContactListConstraints.insets = new Insets(0, 1, 0, 1);

			contactListContainer = new JPanel();
			contactListContainer.setLayout(new GridBagLayout());
			contactListContainer.add(getMyContactsPanel(), myContactsConstraints);
			contactListContainer.add(getContactListPanel(), contactListConstraints);
			contactListContainer.add(getBottomContactListPanel(), bottomContactListConstraints);

			loaderPanel = new JXLayer<JPanel>(contactListContainer);
			loaderPanel.setUI(new AbstractLayerUI<JPanel>() {
				@Override
				protected void paintLayer(Graphics2D g2, JXLayer<JPanel> panel) {
					super.paintLayer(g2, panel);
					if (!loaded) {
						g2.setColor(new Color(0, 0, 0, 75));
						g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
					}
				}
			});
		}
		return loaderPanel;
	}

	private JPanel getBottomContactListPanel() {
		if (bottomContactListPanel == null) {
			bottomContactListPanel = new JPanel();
			bottomContactListPanel.setLayout(null);
			bottomContactListPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			bottomContactListPanel.setName("myContactsBottomPanel");
			bottomContactListPanel.setPreferredSize(new Dimension(198, 18));
			bottomContactListPanel.setMaximumSize(new Dimension(198, 18));
			bottomContactListPanel.setMinimumSize(new Dimension(198, 18));
			bottomContactListPanel.setSize(new Dimension(198, 18));
		}
		return bottomContactListPanel;
	}

	private JPanel getContactListPanel() {
		if (contactListPanel == null) {
			contactListPanel = new JPanel();
			contactListPanel.setLayout(new GridBagLayout());

			GridBagConstraints contactFolderTreeConstraints = new GridBagConstraints();
			contactFolderTreeConstraints.gridx = 0;
			contactFolderTreeConstraints.gridy = 0;
			contactFolderTreeConstraints.fill = GridBagConstraints.HORIZONTAL;
			contactFolderTreeConstraints.weightx = 1.0;

			GridBagConstraints separatorConstraints = new GridBagConstraints();
			separatorConstraints.gridx = 0;
			separatorConstraints.gridy = 1;
			separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
			contactFolderTreeConstraints.weightx = 1.0;

			GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
			scrollPaneConstraints.gridx = 0;
			scrollPaneConstraints.gridy = 2;
			scrollPaneConstraints.fill = GridBagConstraints.BOTH;
			scrollPaneConstraints.weightx = 1.0;
			scrollPaneConstraints.weighty = 1.0;

			contactListPanel.add(getChatSelectionPanel(), contactFolderTreeConstraints);
			contactListPanel.add(getSeparatorPanel(), separatorConstraints);
			contactListPanel.add(getJScrollPane(), scrollPaneConstraints);
		}
		return contactListPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
			separatorPanel.setPreferredSize(SEPARATOR_PANEL_DEFAULT_SIZE);
		}
		return separatorPanel;
	}

	private ChatSelectionPanel getChatSelectionPanel() {
		if (chatSelectionPanel == null) {
			chatSelectionPanel = new ChatSelectionPanel();
		}
		return chatSelectionPanel;
	}

	private JPanel getMyContactsPanel() {
		if (myContactsPanel == null) {
			myContactsPanel = new JPanel();
			myContactsPanel.setLayout(new GridBagLayout());
			myContactsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
			myContactsPanel.setName("myContactsPanel");
			myContactsPanel.setPreferredSize(new Dimension(198, 18));
			myContactsPanel.setMaximumSize(new Dimension(198, 18));
			myContactsPanel.setMinimumSize(new Dimension(198, 18));
			myContactsPanel.setSize(new Dimension(198, 18));
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0;
			myContactsPanel.add(getMyContactsLabel(), constraints);
		}
		return myContactsPanel;
	}

	public JLabel getMyContactsLabel() {
		if (myContactsLabel == null) {
			myContactsLabel = new JLabel("My Contacts", JLabel.CENTER);
			myContactsLabel.setForeground(new Color(77, 77, 77));
			myContactsLabel.setVerticalAlignment(JLabel.CENTER);
			myContactsLabel.setHorizontalAlignment(JLabel.CENTER);
			myContactsLabel.setBounds(30, 0, 138, 18);
			myContactsLabel.setName("myContactsLabel");
		}
		return myContactsLabel;
	}

	JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getTreePanel());
			jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			// Forces the viewPort to start showing at Point 0, 0 of the
			// TreePanel
			jScrollPane.getViewport().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JViewport viewPort = (JViewport) e.getSource();
					if (viewPort.getViewPosition().x != 0) {
						viewPort.setViewPosition(new Point(0, viewPort.getViewPosition().y));
					}
				}
			});
			jScrollPane.getViewport().setBackground(TREE_PANEL_BACKGROUND_COLOR);
		}
		return jScrollPane;
	}

	private JPanel getTreePanel() {
		if (treePanel == null) {
			treePanel = new MessageContainerPanel();
			treePanel.setLayout(new BorderLayout());
			treePanel.add(contactTree, BorderLayout.CENTER);
		}
		return treePanel;
	}

	private void setupTrees() {
		MouseListener rmcMenuListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
					log.debug("Showing menu ...");
					getContactPopupMenu((JTree) e.getSource(), e.getPoint()).show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
		contactTree.addMouseListener(rmcMenuListener);
		chatSelectionPanel.addMouseListener(rmcMenuListener);

		contactTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					TreePath pathForLocation = contactTree.getPathForLocation(e.getX(), e.getY());
					if (pathForLocation != null) {
						boolean editablePathForLocation = contactTree.isEditablePathForLocation(pathForLocation
								.getLastPathComponent());
						if (!isMultiSelectionEnabled(e) && editablePathForLocation) {
							contactTree.setEditable(true);
							contactTree.startEditingAtPath(pathForLocation);

						} else {
							contactTree.setEditable(false);
						}
						pendingEmailSelected.setValue(!editablePathForLocation);
					}
				}
			}
		});

		contactTree.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				if (!contactTree.getTreeHelper().isRestoring()) {
					Sound.CONTACT_FOLDER_OPEN.play();
				}
			}

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				if (!contactTree.getTreeHelper().isRestoring()) {
					Sound.CONTACT_FOLDER_CLOSE.play();
				}
			}
		});

	}

	@SuppressWarnings("static-access")
	private boolean isMultiSelectionEnabled(MouseEvent e) {
		if (Environment.isWindows() && (e.getModifiersEx() == e.CTRL_DOWN_MASK || e.getModifiersEx() == e.SHIFT_DOWN_MASK)) {
			return true;
		} else if (Environment.isMac()
				&& (e.getModifiersEx() == e.META_DOWN_MASK || e.getModifiersEx() == e.SHIFT_DOWN_MASK)) {
			return true;
		}
		return false;
	}

	public TreePath visitAllNodes(JTree tree, int x, int y) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		TreePath visitAllNodes = visitAllNodes(root, x, y);
		return visitAllNodes;
	}

	@SuppressWarnings("unchecked")
	public TreePath visitAllNodes(TreeNode node, int x, int y) {
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = new TreePath(new Object[] { n });
				Rectangle rowBounds = contactTree.getRowBounds(rowForTree++);
				xLocal += rowBounds.width;
				yLocal += rowBounds.height;
				if (yLocal >= y && pathToSelect == null) {
					pathToSelect = path;
					return path;
				} else {
					visitAllNodes(n, x, y);
				}
			}
		}
		return null;
	}

	public void resetPointLocal() {
		xLocal = 0;
		yLocal = 0;
		rowForTree = 0;
		pathToSelect = null;
	}

	private JMenuItem getCreateFolderMenuItem() {
		if (createFolderMenuItem == null) {
			createFolderMenuItem = MenuItems.CREATE_FOLDER.getItem();
			createFolderMenuItem.setVisible(false);
		}
		return createFolderMenuItem;
	}

	private JMenuItem getAddContactMenuItem() {
		if (addContactMenuItem == null) {
			addContactMenuItem = MenuItems.ADD_CONTACT.getItem();
		}
		return addContactMenuItem;
	}

	private JMenuItem getProfileMenuItem() {
		if (profileMenuItem == null) {
			profileMenuItem = MenuItems.PROFILE.getItem();
		}
		return profileMenuItem;
	}

	private JMenuItem getDeleteFolderMenuItem() {
		if (deleteFolderMenuItem == null) {
			deleteFolderMenuItem = MenuItems.DELETE_CONTACT_FOLDER.getItem();
			deleteFolderMenuItem.setVisible(false);
		}
		return deleteFolderMenuItem;
	}

	private JMenuItem getDeleteContactMenuItem() {
		if (deleteContactMenuItem == null) {
			deleteContactMenuItem = MenuItems.DELETE_CONTACT.getItem();
		}
		return deleteContactMenuItem;
	}

	private JMenuItem getSendInvitationMenuItem() {
		if (sendInvitationMenuItem == null) {
			sendInvitationMenuItem = MenuItems.SEND_INVITATION.getItem();
		}
		return sendInvitationMenuItem;
	}

	private JMenuItem getChatMenuItem() {
		if (chatMenuItem == null) {
			chatMenuItem = MenuItems.CHAT.getItem();
			chatMenuItem.setEnabled(false);
		}
		return chatMenuItem;
	}

	private JMenuItem getRemoteLibraryItem() {
		if (remoteLibraryMenuItem == null) {
			remoteLibraryMenuItem = MenuItems.REMOTE_LIBRARY.getItem();
			remoteLibraryMenuItem.setEnabled(false);
		}
		return remoteLibraryMenuItem;
	}

	private JMenuItem getFacebookChatMenuItem() {
		if (facebookChatMenuItem == null) {
			facebookChatMenuItem = MenuItems.FACEBOOK_CHAT.getItem();
			facebookChatMenuItem.setEnabled(true);
		}
		return facebookChatMenuItem;
	}

	private JPopupMenu getContactPopupMenu(JTree component, Point point) {
		popUpMenu = new JPopupMenu();

		Object o = new JTreeCoordinateHelper(component).getValueAt(point);
		if (o != null) {
			if (o instanceof ContactInfo) {
				ContactInfo contactInfo = getContactInfo((ContactTree) component);
				if (contactInfo != null) {
					if (contactInfo.getChatType().equals(ChatType.ALL)) {
						if (!contactInfo.isPending()) {
							popUpMenu.add(getAddContactMenuItem());
							popUpMenu.addSeparator();
							popUpMenu.add(getRemoteLibraryItem());
							popUpMenu.add(getProfileMenuItem());
							if (contactInfo.isOnline()) {
								popUpMenu.add(getChatMenuItem());
							}
							popUpMenu.addSeparator();
							popUpMenu.add(getCreateFolderMenuItem());
							popUpMenu.addSeparator();
							popUpMenu.add(getDeleteContactMenuItem());
						} else if (contactInfo.isPending()) {
							getSendInvitationMenuItem().setActionCommand(contactInfo.getEmail());
							popUpMenu.add(sendInvitationMenuItem);
							popUpMenu.addSeparator();
							popUpMenu.add(getDeleteContactMenuItem());
							popUpMenu.addSeparator();
							popUpMenu.add(getAddContactMenuItem());
						}
					} else {
						if (contactInfo.isOnline() || contactInfo.isAway()) {
							popUpMenu.add(getFacebookChatMenuItem());
						}
					}
				}
			} else {
				popUpMenu.add(getAddContactMenuItem());
				popUpMenu.add(getCreateFolderMenuItem());
			}
		}

		return popUpMenu;
	}

	private ContactInfo getContactInfo(ContactTree tree) {
		DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
		return (lastPathComponent.getUserObject() instanceof ContactInfo) ? (ContactInfo) lastPathComponent.getUserObject()
				: null;
	}

	@Override
	public void internationalize(Messages messages) {
		MenuItems.CREATE_FOLDER.internationalize(getCreateFolderMenuItem(), messages);
		MenuItems.ADD_CONTACT.internationalize(getAddContactMenuItem(), messages);
		MenuItems.DELETE_CONTACT_FOLDER.internationalize(getDeleteFolderMenuItem(), messages);
		MenuItems.DELETE_CONTACT.internationalize(getDeleteContactMenuItem(), messages);
		MenuItems.SEND_INVITATION.internationalize(getSendInvitationMenuItem(), messages);
		MenuItems.CHAT.internationalize(getChatMenuItem(), messages);
		MenuItems.PROFILE.internationalize(getProfileMenuItem(), messages);
		MenuItems.REMOTE_LIBRARY.internationalize(getRemoteLibraryItem(), messages);
		getMyContactsLabel().setText(messages.getMessage("contactTree.header"));
		chatSelectionPanel.internationalize(messages);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
		chatSelectionPanel.setMessages(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		chatSelectionPanel.setMessages(messages);
	}

	private void deleteSelectedContacts() {
		int[] selectionRows = contactTree.getSelectionRows();
		deleteContacts(currentSelection);
		contactTree.setSelectionRow(selectionRows[0]);
	}

	public void sendInvitation() {
		List<String> emails = new ArrayList<String>();
		for (TreePath selection : contactTree.getSelectionPaths()) {
			Object o = ((DefaultMutableTreeNode) selection.getLastPathComponent()).getUserObject();
			if (o instanceof ContactInfo) {
				ContactInfo contact = (ContactInfo) o;
				if (contact.getStatus() == ContactStatus.pending) {
					emails.add(contact.getEmail());
				}
			}
		}
		dialogFactory.showSendMultipleInvitationDialog(emails);
	}

	void updateContactTree(Collection<ContactInfo> contacts, boolean expandNodes) {
		JViewport viewPort = viewportHelper.getViewport(getJScrollPane());
		Point viewPosition = viewPort.getViewPosition();
		contactTree.getTreeHelper().saveState();
		contactTree.setModel(new ArrayList<ContactInfo>(contacts),
				chatSelectionPanel.getSelectedChatType() == ChatType.ALL, expandNodes);
		contactTree.getTreeHelper().restoreState();
		viewportHelper.setViewPosition(viewPosition);
		contactTree.setNodesTitles();
	}

	public ObserverCollection<ObservePropertyChanged<ContactListPanel, Boolean>> onPendingEmailSelected() {
		return pendingEmailSelected.on();
	}

}
