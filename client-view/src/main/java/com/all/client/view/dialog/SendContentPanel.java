package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.chat.ChatType;
import com.all.client.view.components.CellFilter;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.format.ByteFormater;
import com.all.client.view.format.TimeFormater;
import com.all.core.actions.Actions;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.core.model.ContactCollection;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public final class SendContentPanel extends JPanel implements Internationalizable {

	private static final String SEND_CONTENT_DEFAULT_MESSAGES = "sendContent.default.messages";

	private static final String EMPTY_TEXT = "";

	private static final long serialVersionUID = 1L;

	private static final Color CONTACT_TABLE_BG = new Color(243, 243, 247);

	private static final Color CONTENT_TABLE_BG = new Color(235, 240, 245);

	private static final Color CONTACT_TABLE_SELECTION_BG = new Color(212, 212, 240);

	private static final Color CONTENT_TABLE_SELECTION_BG = new Color(174, 205, 225);

	private static final Rectangle BOUNDS = new Rectangle(0, 0, 440, 521);

	private static final Rectangle BLUE_BOTTOM_PANEL_BOUNDS = new Rectangle(6, 269, 206, 20);

	private static final Rectangle BLUE_TOP_PANEL_BOUNDS = new Rectangle(6, 247, 206, 20);

	private static final Rectangle BTN_CANCEL_BOUNDS = new Rectangle(134, 486, 80, 22);

	private static final Rectangle BTN_SEND_BOUNDS = new Rectangle(224, 486, 80, 22);

	private static final Rectangle MAIL_ICON_PANEL_BOUNDS = new Rectangle(18, 12, 40, 40);

	private static final Rectangle IMAGE_PANEL_BOUNDS = new Rectangle(285, 375, 114, 86);

	private static final Rectangle CONTACT_BOTTOM_PANEL_BOUNDS = new Rectangle(260, 224, 160, 17);

	private static final Rectangle CONTACT_SCROLL_BOUNDS = new Rectangle(260, 24, 160, 200);

	private static final Rectangle CONTACT_TOP_PANEL_BOUNDS = new Rectangle(260, 6, 160, 18);

	private static final Rectangle CONTACT_LABEL_BOUNDS = new Rectangle(40, 1, 64, 18);

	private static final Rectangle CONTACT_LABEL_MAXIMUM_BOUNDS = new Rectangle(105, 1, 20, 18);

	private static final Rectangle CONTAINER_PANEL_BOUNDS = new Rectangle(6, 60, 426, 295);

	private static final Rectangle CONTENT_BOTTOM_PANEL = new Rectangle(6, 224, 250, 17);

	private static final Rectangle CONTENT_SCROLL_BOUNDS = new Rectangle(6, 24, 250, 200);

	private static final Rectangle CONTENT_TOP_PANEL_BOUNDS = new Rectangle(6, 6, 250, 18);

	private static final Rectangle GRAY_BOTTOM_PANEL_BOUNDS = new Rectangle(214, 269, 206, 20);

	private static final Rectangle GRAY_TOP_PANEL_BOUNDS = new Rectangle(214, 247, 206, 20);

	private static final Rectangle ICON_PANEL_BOUNDS = new Rectangle(0, 0, 26, 20);

	private static final Rectangle ICON_BOUNDS = new Rectangle(6, 2, 16, 16);

	private static final Rectangle BUSY_ICON_BOUNDS = new Rectangle(4, 2, 16, 16);

	private static final Rectangle INFO_LABEL_BOUNDS = new Rectangle(30, 0, 150, 20);

	private static final Rectangle INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(12, -5, 200, 30);

	private static final Rectangle SEPARATOR_BOUNDS = new Rectangle(2, 477, 446, 2);

	private static final Rectangle LBL_INSTRUCTIONS_BOUNDS = new Rectangle(68, 10, 372, 40);

	private static final Rectangle LBL_PERSONALIZE_BOUNDS = new Rectangle(10, 355, 250, 30);
	
	private static final Rectangle LBL_TWITTER_BOUNDS = new Rectangle(35, 375, 250, 30);
	
	private static final Rectangle CHECKBOX_BOUNDS = new Rectangle(10, 375, 250, 30);

	private static final Rectangle TEXT_MESSAGE_BOUNDS = new Rectangle(5, 5, 240, 50);

	private static final Rectangle TEXT_CONTAINER_BOUNDS = new Rectangle(12, 405, 250, 60);

	private static final Rectangle VERTICAL_BOTTOM_SEPARATOR_BOUNDS = new Rectangle(212, 269, 2, 20);

	private static final Rectangle VERTICAL_TOP_SEPARATOR_BOUNDS = new Rectangle(212, 247, 2, 20);

	private static final String BLUE_PANEL_NAME = "sendContentBluePanel";

	private static final String CANCEL_BUTTON_NAME = "buttonCancel";

	private static final String CLOCK_LABEL_NAME = "sendContentClockPanel";

	private static final String CONTACT_BOTTOM_PANEL_NAME = "myContactsBottomPanel";

	private static final String CONTACT_TOP_PANEL_NAME = "myContactsPanel";

	private static final String CONTAINER_PANEL_NAME = "grayRoundedBorderPanel";

	private static final String CONTENT_BOTTOM_PANEL_NAME = "myMusicBottomLibPanel";

	private static final String CONTENT_TOP_PANEL_NAME = "myMusicPanel";

	private static final String DEFAULT_TEXT = "label";

	private static final String GRAY_PANEL_NAME = "sendContentGrayPanel";

	private static final String NUMBER_OF_TRACKS_PANEL_NAME = "sendContentNumberOfTracksPanel";

	private static final String SEND_BUTTON_NAME = "buttonSend";

	private static final String SEPARATOR_NAME = "bottomPanelSeparator";

	private static final String TEXT_CONTAINER_NAME = "joinInvitationPanel";

	private static final String TEXT_MESSAGE_NAME = "sendContentTextArea";

	private static final String TEXT_MESSAGE_DEFAULT_NAME = "sendContentDefaultTextArea";

	private static final String VERTICAL_SEPARATOR_NAME = "sendContentVeticalSeparatorPanel";

	private static final String WHITE_DIALOG_PLAIN12_LABEL_NAME = "whiteDialogBold12";

	private static final int POINTS = 8;
	private static final int TRAIL_LENGTH = 7;
	private static final int FRAME = -1;
	private static final Color BASE_COLOR = new Color(187, 189, 191);
	private static final Color HIGHLIGHT_COLOR = new Color(35, 31, 32);
	private static final Dimension BUSY_LABEL_DEFAULT_SIZE = new Dimension(16, 16);

	private final ContactCollection contacts;

	private JLabel lblInstructions;

	private JScrollPane contentScroll;

	private JTable contentTable;

	private JScrollPane contactScroll;

	private JTextArea textMessage;

	private JTable contactTable;

	private JButton cancelButton;

	private JButton sendButton;

	private final ModelCollection model;

	private JPanel containerPanel;

	private JLabel lblPersonalize;
	
	private JLabel lblTwitter;

	private JLabel contactLabel;

	private JLabel contactLabelMaximum;

	private JLabel contentLabel;

	private JPanel separator;

	private JPanel contentTopPanel;

	private JPanel contactTopPanel;

	private JPanel contentBottomPanel;

	private JPanel contactBottomPanel;

	private JPanel imagePanel;

	private JPanel textContainer;

	private JPanel mailIconPanel;

	private JPanel verticalBottomSeparator;

	private JPanel verticalTopSeparator;

	private JPanel grayBottomPanel;

	private JPanel blueBottomPanel;

	private JPanel grayTopPanel;

	private JPanel blueTopPanel;

	private JLabel timeInstructionsLabel;

	private JLabel numberInstructionsLabel;

	private ByteFormater formater;

	private TimeFormater timeFormater;

	private JLabel infoNumberOfTracksLabel;

	private Messages messages;

	private JLabel infoTimeLabel;

	private JLabel clockLabel;

	private JCheckBox twitterCheckBox;

	private JXBusyLabel busyLabel;

	private JPanel clockPanel;

	private boolean isFirstKeyTyped = true;

	private final ViewEngine viewEngine;


	public SendContentPanel(Messages messages, ModelCollection model, ContactCollection contacts, ViewEngine viewEngine) {
		this.messages = messages;
		this.viewEngine = viewEngine;
		this.model = new ModelCollection();
		this.contacts = new ContactCollection();
		formater = new ByteFormater();
		timeFormater = new TimeFormater();
		initialize();
		setMessages(messages);
		addData(model, contacts);
	}

	public ModelCollection getModel() {
		return model;
	}

	@Override
	public void internationalize(Messages messages) {
		lblInstructions.setText(messages.getMessage("sendContent.instructions"));
		contactLabel.setText(messages.getMessage("sendContent.contactList"));
		contactLabelMaximum.setText(messages.getMessage("sendContent.contactListMaximum"));
		contentLabel.setText(messages.getMessage("sendContent.contentList"));
		cancelButton.setText(messages.getMessage("cancel"));
		sendButton.setText(messages.getMessage("sendContent.send"));
		timeInstructionsLabel.setText(messages.getMessage("sendContent.timeInstructions"));
		numberInstructionsLabel.setText(messages.getMessage("sendContent.sizeInstructions"));

		getLblPersonalize().setText(messages.getMessage("sendContent.personalize"));
		getLblTwitter().setText(messages.getMessage("sendContent.twitter"));
		getContentScroll().setToolTipText(messages.getMessage("tooltip.mediaSendCont"));
		getContactScroll().setToolTipText(messages.getMessage("tooltip.contactSendCont"));
		getCancelButton().setToolTipText(messages.getMessage("tooltip.cancelSendCont"));
		getSendButton().setToolTipText(messages.getMessage("tooltip.sendSendCont"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
		timeFormater.internationalize(messages);
	}

	private void addData(ModelCollection model, ContactCollection contacts) {
		if (contacts != null) {
			DefaultTableModel contactModel = (DefaultTableModel) contactTable.getModel();
			for (ContactInfo contact : contacts.getContacts()) {
				if (contact.isPending()) {
					continue;
				}
				if (!this.contacts.getContacts().contains(contact)) {
					contactModel.addRow(new Object[] { contact });
					this.contacts.getContacts().add(contact);
				}
			}
		}
		if (model != null) {
			DefaultTableModel contentModel = (DefaultTableModel) contentTable.getModel();
			for (Track track : model.getTracks()) {
				if (!this.model.getTracks().contains(track)) {
					contentModel.addRow(new Object[] { track });
					this.model.getTracks().add(track);
				}
			}
			for (Folder folder : model.getFolders()) {
				removeData(new ModelCollection(folder.getPlaylists()), null);
				model.getPlaylists().removeAll(folder.getPlaylists());
				if (!this.model.getFolders().contains(folder)) {
					contentModel.addRow(new Object[] { folder });
					this.model.getFolders().add(folder);
				}
			}
			for (Playlist playlist : model.getPlaylists()) {
				if (!this.model.getPlaylists().contains(playlist)
						&& !this.model.getFolders().contains(playlist.getParentFolder())) {
					contentModel.addRow(new Object[] { playlist });
					this.model.getPlaylists().add(playlist);
				}
			}
		}
		updateSendButton();
	}

	private void removeData(ModelCollection model, ContactCollection contacts) {
		if (contacts != null) {
			DefaultTableModel contactModel = (DefaultTableModel) contactTable.getModel();
			for (ContactInfo contact : contacts.getContacts()) {
				this.contacts.getContacts().remove(contact);
				removeRowFor(contact, contactModel);
			}
		}
		if (model != null) {
			DefaultTableModel contentModel = (DefaultTableModel) contentTable.getModel();
			for (Playlist playlist : model.getPlaylists()) {
				this.model.getPlaylists().remove(playlist);
				removeRowFor(playlist, contentModel);
			}
			for (Folder folder : model.getFolders()) {
				this.model.getFolders().remove(folder);
				removeRowFor(folder, contentModel);
			}
			for (Track track : model.getTracks()) {
				this.model.getTracks().remove(track);
				removeRowFor(track, contentModel);
			}
			updateDataUpload();
		}
		updateSendButton();
	}

	private void removeRowFor(Object value, DefaultTableModel model) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(value)) {
				model.removeRow(i);
				return;
			}
		}
	}

	public void setup(final JDialog dialog, MultiLayerDropTargetListener dndListener) {
		((ImagePanel) imagePanel).setImage(ImageUtil.getImage(ImageUtil.Images.STAMP_BG), .17, .17);
		((ImagePanel) mailIconPanel).setImage(ImageUtil.getImage(ImageUtil.Images.MAIL_ICON), .17, .17);
		dndListener.addDropListener(this, new DropListener() {
			private final Class<?>[] classes = new Class<?>[] { ContactCollection.class };

			@Override
			public void doDrop(DraggedObject draggedObject, Point location) {
				ContactCollection contacts = draggedObject.get(ContactCollection.class);
				addData(null, contacts);
				setContainerPanelName(CONTAINER_PANEL_NAME);
			}

			@Override
			public boolean validateDrop(DraggedObject draggedObject, Point location) {
				ContactCollection contacts = draggedObject.get(ContactCollection.class);
				if (contacts == null || !contacts.getPendingContacts().isEmpty()) {
					return false;
				}
				return !contacts.getContacts().isEmpty();
			}

			@Override
			public Class<?>[] handledTypes() {
				return classes;
			}
		});
		dndListener.addDropListener(this, new DropListener() {
			private final Class<?>[] classes = new Class<?>[] { ModelCollection.class };

			@Override
			public void doDrop(DraggedObject draggedObject, Point location) {
				ModelCollection model = draggedObject.get(ModelCollection.class);
				addData(model, null);
				updateDataUpload();
				setContainerPanelName(CONTAINER_PANEL_NAME);
			}

			@Override
			public boolean validateDrop(DraggedObject draggedObject, Point location) {
				return draggedObject.is(ModelCollection.class);
			}

			@Override
			public Class<?>[] handledTypes() {
				return classes;
			}

		});
		textMessage.setDropTarget(null);

		contactTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable table = (JTable) e.getComponent();
				int row = e.getY() / table.getRowHeight();
				if (row < table.getModel().getRowCount() && e.getX() > table.getWidth() - 20) {
					ContactInfo contact = (ContactInfo) table.getValueAt(row, 0);
					removeData(null, new ContactCollection(contact));
				}
			}
		});

		contentTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JTable table = (JTable) e.getComponent();
				int row = e.getY() / table.getRowHeight();
				if (row < table.getModel().getRowCount() && e.getX() > table.getWidth() - 20) {
					List<Object> content = new ArrayList<Object>();
					content.add(table.getValueAt(row, 0));
					removeData(new ModelCollection(content), null);
				}
			}
		});
		DocumentFilter filter = new DocumentFilter() {
			public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
					throws BadLocationException {
				if (string == null) {
					return;
				} else {
					replace(fb, offset, 0, string, attr);
				}
			}

			public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
				replace(fb, offset, length, EMPTY_TEXT, null);
			}

			public void replace(DocumentFilter.FilterBypass fb, int offset, int selectedLength, String text,
					AttributeSet attrs) throws BadLocationException {
				text = text.replaceAll("\\n", " ");

				Document doc = fb.getDocument();
				int currentLength = doc.getLength();
				if (currentLength + text.length() - selectedLength < 78) {
					fb.replace(offset, selectedLength, text, attrs);
				} else {
					text = text.substring(0, 78 - currentLength + selectedLength);
					fb.replace(offset, selectedLength, text, attrs);
				}
			}
		};
		((AbstractDocument) (textMessage.getDocument())).setDocumentFilter(filter);
	}

	public void updateDataUpload() {
		String totalTracksInModel = EMPTY_TEXT + model.trackCount();
		String tracksInfo = messages.getMessage("sendContent.sizeInfo", totalTracksInModel, formater
				.getFormat(model.size()));
		getInfoNumberOfTracksLabel().setText(tracksInfo);
		getInfoTimeLabel().setText(messages.getMessage("sendContent.timeInfo"));
		updateToBusyClockLabel();
		viewEngine.request(Actions.Social.REQUEST_UPLOAD_TIME, getModel(), new ResponseCallback<Integer>() {
			@Override
			public void onResponse(Integer uploadTime) {
				String timeFormat;
				try {
					timeFormat = timeFormater.getFormat(uploadTime);
					getInfoTimeLabel().setText(timeFormat);
					updateToClockLabel();
				} catch (Exception e) {
					getInfoTimeLabel().setText(EMPTY_TEXT);
				}
			}

		});
	}

	private void updateToBusyClockLabel() {
		getClockLabel().setName(EMPTY_TEXT);
		getClockPanel().add(getBusyLabel());
	}

	private void setupTable(JTable table, TableCellRenderer contentRenderer, Comparator<?> rowComparator) {
		// Setting up header
		JTableHeader jTableHeader = new JTableHeader();
		jTableHeader.setVisible(false);
		table.setTableHeader(jTableHeader);

		// Setting up table properties
		table.setShowVerticalLines(false);
		table.setShowHorizontalLines(false);
		table.setRowHeight(20);

		// Setting up model

		TableColumn mainColumn = new TableColumn();
		mainColumn.setIdentifier(DEFAULT_TEXT);
		mainColumn.setCellRenderer(contentRenderer);

		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		model.addColumn(mainColumn);

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(mainColumn);

		table.setModel(model);
		table.setColumnModel(columnModel);

		// Setting up behavior

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		MouseAdapter mouseListener = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				JTable table = (JTable) e.getComponent();
				int row = e.getY() / table.getRowHeight();
				if (row >= table.getRowCount()) {
					table.clearSelection();
				} else {
					table.changeSelection(row, 0, false, false);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				JTable table = (JTable) e.getComponent();
				table.clearSelection();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (e.getComponent() == contentTable) {
					contactTable.clearSelection();
				}
				if (e.getComponent() == contactTable) {
					contentTable.clearSelection();
				}
			}

		};
		table.addMouseMotionListener(mouseListener);
		table.addMouseListener(mouseListener);

		// Setting up sorting

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		sorter.setComparator(0, rowComparator);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);

	}

	private void initialize() {
		getLblInstructions();
		this.setLayout(null);
		this.setBounds(BOUNDS);
		this.add(getMailIconPanel());
		this.add(getLblInstructions());
		this.add(getContainerPanel());
		this.add(getLblPersonalize());
		this.add(getTwitterCheckBox());
		this.add(getLblTwitter());
		this.add(getImagePanel());
		this.add(getTextContainer());
		this.add(getSeparator());
		this.add(getSendButton());
		this.add(getCancelButton());
		setTwitterStatus();
	}
	
	private void setTwitterStatus() {
		viewEngine.request(Actions.Twitter.POST_AUTH_STATUS, new ResponseCallback<Boolean>(){
			
			@Override
			public void onResponse(Boolean status) {
				getTwitterCheckBox().setSelected(status);
			}
		});
	}

	private JLabel getLblInstructions() {
		if (lblInstructions == null) {
			lblInstructions = new JLabel();
			lblInstructions.setText(DEFAULT_TEXT);
			lblInstructions.setBounds(LBL_INSTRUCTIONS_BOUNDS);
		}
		return lblInstructions;
	}

	private JLabel getLblPersonalize() {
		if (lblPersonalize == null) {
			lblPersonalize = new JLabel();
			lblPersonalize.setHorizontalAlignment(SwingConstants.LEFT);
			lblPersonalize.setBounds(LBL_PERSONALIZE_BOUNDS);
			lblPersonalize.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			lblPersonalize.setText(DEFAULT_TEXT);
		}
		return lblPersonalize;
	}
	
	private JLabel getLblTwitter() {
		if (lblTwitter == null) {
			lblTwitter = new JLabel();
			lblTwitter.setHorizontalAlignment(SwingConstants.LEFT);
			lblTwitter.setBounds(LBL_TWITTER_BOUNDS);
			lblTwitter.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			lblTwitter.setText(DEFAULT_TEXT);
		}
		return lblTwitter;
	}
	
	private JCheckBox getTwitterCheckBox() {
		if (twitterCheckBox == null) {
			twitterCheckBox = new JCheckBox();
			twitterCheckBox.setBounds(CHECKBOX_BOUNDS);
		}
		return twitterCheckBox;
	}

	private JScrollPane getContentScroll() {
		if (contentScroll == null) {
			contentScroll = new JScrollPane();
			contentScroll.setBounds(CONTENT_SCROLL_BOUNDS);
			contentScroll.setViewportView(getContentTable());
			contentScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			// A not so known fact the JScrollPane component SUCK
			contentScroll.getViewport().setBackground(getContentTable().getBackground());
		}
		return contentScroll;
	}

	private JScrollPane getContactScroll() {
		if (contactScroll == null) {
			contactScroll = new JScrollPane();
			contactScroll.setBounds(CONTACT_SCROLL_BOUNDS);
			contactScroll.setViewportView(getContactTable());
			contactScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			// A not so known fact the JScrollPane component SUCK
			contactScroll.getViewport().setBackground(getContactTable().getBackground());
		}
		return contactScroll;
	}

	private JTable getContentTable() {
		if (contentTable == null) {
			contentTable = new SendContentTable(CONTENT_TABLE_BG, CONTENT_TABLE_SELECTION_BG);
			setupTable(contentTable, new ContentCellRenderer(), new ModelComparator());
		}
		return contentTable;
	}

	private JTable getContactTable() {
		if (contactTable == null) {
			contactTable = new SendContentTable(CONTACT_TABLE_BG, CONTACT_TABLE_SELECTION_BG);
			setupTable(contactTable, new ContactCellRenderer(), new ContactComparator());
		}
		return contactTable;
	}

	private JTextArea getTextMessage() {
		if (textMessage == null) {
			textMessage = new JTextArea();
			textMessage.setBounds(TEXT_MESSAGE_BOUNDS);
			textMessage.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
			textMessage.setLineWrap(true);
			textMessage.setWrapStyleWord(true);
			textMessage.addKeyListener(new CopyPasteKeyAdapterForMac());

			setDefaultMessage();

			textMessage.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (isFirstKeyTyped) {
						isFirstKeyTyped = false;
						getTextMessage().setText(EMPTY_TEXT);
						getTextMessage().setName(TEXT_MESSAGE_NAME);
					}
				}
			});
			textMessage.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (EMPTY_TEXT.equals(textMessage.getText())) {
						setDefaultMessage();
					}
				}
			});
		}
		return textMessage;
	}

	private void setDefaultMessage() {
		isFirstKeyTyped = true;
		getTextMessage().setText(messages.getMessage(SEND_CONTENT_DEFAULT_MESSAGES));
		getTextMessage().setName(TEXT_MESSAGE_DEFAULT_NAME);
	}

	public JButton getSendButton() {
		if (sendButton == null) {
			sendButton = new JButton();
			sendButton.setText(DEFAULT_TEXT);
			sendButton.setName(SEND_BUTTON_NAME);
			sendButton.setBounds(BTN_SEND_BOUNDS);
		}
		return sendButton;
	}

	public JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(DEFAULT_TEXT);
			cancelButton.setName(CANCEL_BUTTON_NAME);
			cancelButton.setBounds(BTN_CANCEL_BOUNDS);
		}
		return cancelButton;
	}

	private JPanel getContainerPanel() {
		if (containerPanel == null) {
			containerPanel = new JPanel();
			containerPanel.setLayout(null);
			containerPanel.setBounds(CONTAINER_PANEL_BOUNDS);
			containerPanel.setName(CONTAINER_PANEL_NAME);
			containerPanel.add(getContactScroll());
			containerPanel.add(getContentScroll());
			containerPanel.add(getContentTopPanel());
			containerPanel.add(getContactTopPanel());
			containerPanel.add(getContentBottomPanel());
			containerPanel.add(getContactBottomPanel());
			containerPanel.add(getBlueTopPanel());
			containerPanel.add(getVerticalTopSeparator());
			containerPanel.add(getGrayTopPanel());
			containerPanel.add(getBlueBottomPanel());
			containerPanel.add(getVerticalBottomSeparator());
			containerPanel.add(getGrayBottomPanel());
		}
		return containerPanel;
	}

	private JPanel getVerticalBottomSeparator() {
		if (verticalBottomSeparator == null) {
			verticalBottomSeparator = new JPanel();
			verticalBottomSeparator.setName(VERTICAL_SEPARATOR_NAME);
			verticalBottomSeparator.setBounds(VERTICAL_BOTTOM_SEPARATOR_BOUNDS);
		}
		return verticalBottomSeparator;
	}

	private JPanel getVerticalTopSeparator() {
		if (verticalTopSeparator == null) {
			verticalTopSeparator = new JPanel();
			verticalTopSeparator.setName(VERTICAL_SEPARATOR_NAME);
			verticalTopSeparator.setBounds(VERTICAL_TOP_SEPARATOR_BOUNDS);
		}

		return verticalTopSeparator;
	}

	private JPanel getGrayBottomPanel() {
		if (grayBottomPanel == null) {
			grayBottomPanel = new JPanel();
			grayBottomPanel.setLayout(null);
			grayBottomPanel.setBounds(GRAY_BOTTOM_PANEL_BOUNDS);
			grayBottomPanel.setName(GRAY_PANEL_NAME);
			grayBottomPanel.add(getClockPanel());
			grayBottomPanel.add(getInfoTimeLabel());
		}
		return grayBottomPanel;
	}

	private JLabel getClockLabel() {
		if (clockLabel == null) {
			clockLabel = new JLabel();
			clockLabel.setBounds(ICON_BOUNDS);
		}
		return clockLabel;
	}

	protected final JXBusyLabel getBusyLabel() {
		if (busyLabel == null) {
			BusyPainter painter = new BusyPainter(new Ellipse2D.Float(0, 0, 3f, 3f), new Ellipse2D.Float(3f, 3f, 12f, 12f));
			painter.setTrailLength(TRAIL_LENGTH);
			painter.setPoints(POINTS);
			painter.setFrame(FRAME);
			painter.setBaseColor(BASE_COLOR);
			painter.setHighlightColor(HIGHLIGHT_COLOR);
			busyLabel = new JXBusyLabel(BUSY_LABEL_DEFAULT_SIZE);
			busyLabel.setPreferredSize(BUSY_LABEL_DEFAULT_SIZE);
			busyLabel.setIcon(new EmptyIcon(16, 16));
			busyLabel.setBusyPainter(painter);
			busyLabel.setBusy(true);
			busyLabel.setVisible(true);
			busyLabel.setBounds(BUSY_ICON_BOUNDS);
		}
		return busyLabel;
	}

	public JPanel getClockPanel() {
		if (clockPanel == null) {
			clockPanel = new JPanel();
			clockPanel.setLayout(null);
			clockPanel.setBounds(ICON_PANEL_BOUNDS);
			clockPanel.add(getClockLabel());
		}
		return clockPanel;
	}

	private JLabel getInfoTimeLabel() {
		if (infoTimeLabel == null) {
			infoTimeLabel = new JLabel();
			infoTimeLabel.setText("10.2 Hours (at 35.0 kbps)");
			infoTimeLabel.setBounds(INFO_LABEL_BOUNDS);
		}
		return infoTimeLabel;
	}

	private JPanel getBlueBottomPanel() {
		if (blueBottomPanel == null) {
			blueBottomPanel = new JPanel();
			blueBottomPanel.setLayout(null);
			blueBottomPanel.setBounds(BLUE_BOTTOM_PANEL_BOUNDS);
			blueBottomPanel.setName(BLUE_PANEL_NAME);
			blueBottomPanel.add(getTimeInstructionsLabel());
		}
		return blueBottomPanel;
	}

	private JLabel getTimeInstructionsLabel() {
		if (timeInstructionsLabel == null) {
			timeInstructionsLabel = new JLabel();
			timeInstructionsLabel.setName(WHITE_DIALOG_PLAIN12_LABEL_NAME);
			timeInstructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
		}
		return timeInstructionsLabel;
	}

	private JPanel getGrayTopPanel() {
		if (grayTopPanel == null) {
			grayTopPanel = new JPanel();
			grayTopPanel.setLayout(null);
			grayTopPanel.setBounds(GRAY_TOP_PANEL_BOUNDS);
			grayTopPanel.setName(GRAY_PANEL_NAME);
			JPanel numberOfTracksPanel = new JPanel();
			numberOfTracksPanel.setBounds(ICON_BOUNDS);
			numberOfTracksPanel.setName(NUMBER_OF_TRACKS_PANEL_NAME);
			grayTopPanel.add(numberOfTracksPanel);
			grayTopPanel.add(getInfoNumberOfTracksLabel());
		}
		return grayTopPanel;
	}

	private JLabel getInfoNumberOfTracksLabel() {
		if (infoNumberOfTracksLabel == null) {
			infoNumberOfTracksLabel = new JLabel();
			infoNumberOfTracksLabel.setBounds(INFO_LABEL_BOUNDS);
		}
		return infoNumberOfTracksLabel;
	}

	private JPanel getBlueTopPanel() {
		if (blueTopPanel == null) {
			blueTopPanel = new JPanel();
			blueTopPanel.setLayout(null);
			blueTopPanel.setBounds(BLUE_TOP_PANEL_BOUNDS);
			blueTopPanel.setName(BLUE_PANEL_NAME);
			blueTopPanel.add(getNumberInstructionsLabel());
		}
		return blueTopPanel;
	}

	private JLabel getNumberInstructionsLabel() {
		if (numberInstructionsLabel == null) {
			numberInstructionsLabel = new JLabel();
			numberInstructionsLabel.setName(WHITE_DIALOG_PLAIN12_LABEL_NAME);
			numberInstructionsLabel.setBounds(INSTRUCTIONS_LABEL_BOUNDS);
		}
		return numberInstructionsLabel;
	}

	public void setContainerPanelName(String name) {
		containerPanel.setName(name);
	}

	private JPanel getSeparator() {
		if (separator == null) {
			separator = new JPanel();
			separator.setLayout(new GridBagLayout());
			separator.setName(SEPARATOR_NAME);
			separator.setBounds(SEPARATOR_BOUNDS);
		}
		return separator;
	}

	private JPanel getContentTopPanel() {
		if (contentTopPanel == null) {
			contentLabel = new JLabel();
			contentLabel.setText(DEFAULT_TEXT);
			contentLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			contentTopPanel = new JPanel();
			contentTopPanel.setLayout(new BorderLayout());
			contentTopPanel.setBounds(CONTENT_TOP_PANEL_BOUNDS);
			contentTopPanel.setName(CONTENT_TOP_PANEL_NAME);
			contentTopPanel.add(contentLabel, BorderLayout.CENTER);
		}
		return contentTopPanel;
	}

	private JPanel getContactTopPanel() {
		if (contactTopPanel == null) {
			contactLabel = new JLabel();
			contactLabel.setText(DEFAULT_TEXT);
			contactLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contactLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			contactLabel.setBounds(CONTACT_LABEL_BOUNDS);
			contactLabelMaximum = new JLabel();
			contactLabelMaximum.setText(DEFAULT_TEXT);
			contactLabelMaximum.setHorizontalAlignment(SwingConstants.CENTER);
			contactLabelMaximum.setName(SynthFonts.PLAIN_FONT10_GRAY77_77_77);
			contactLabelMaximum.setBounds(CONTACT_LABEL_MAXIMUM_BOUNDS);
			contactTopPanel = new JPanel();
			contactTopPanel.setLayout(null);
			contactTopPanel.setBounds(CONTACT_TOP_PANEL_BOUNDS);
			contactTopPanel.add(contactLabel);
			contactTopPanel.add(contactLabelMaximum);
			contactTopPanel.setName(CONTACT_TOP_PANEL_NAME);
		}
		return contactTopPanel;
	}

	private JPanel getContentBottomPanel() {
		if (contentBottomPanel == null) {
			contentBottomPanel = new JPanel();
			contentBottomPanel.setLayout(new GridBagLayout());
			contentBottomPanel.setBounds(CONTENT_BOTTOM_PANEL);
			contentBottomPanel.setName(CONTENT_BOTTOM_PANEL_NAME);
		}
		return contentBottomPanel;
	}

	private JPanel getContactBottomPanel() {
		if (contactBottomPanel == null) {
			contactBottomPanel = new JPanel();
			contactBottomPanel.setLayout(new GridBagLayout());
			contactBottomPanel.setBounds(CONTACT_BOTTOM_PANEL_BOUNDS);
			contactBottomPanel.setName(CONTACT_BOTTOM_PANEL_NAME);
		}
		return contactBottomPanel;
	}

	private JPanel getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new ImagePanel();
			imagePanel.setLayout(new GridBagLayout());
			imagePanel.setBounds(IMAGE_PANEL_BOUNDS);
		}
		return imagePanel;
	}

	private JPanel getMailIconPanel() {
		if (mailIconPanel == null) {
			mailIconPanel = new ImagePanel();
			mailIconPanel.setLayout(new GridBagLayout());
			mailIconPanel.setBounds(MAIL_ICON_PANEL_BOUNDS);
		}
		return mailIconPanel;
	}

	private JPanel getTextContainer() {
		if (textContainer == null) {
			textContainer = new JPanel();
			textContainer.setLayout(null);
			textContainer.setName(TEXT_CONTAINER_NAME);
			textContainer.setBounds(TEXT_CONTAINER_BOUNDS);
			textContainer.add(getTextMessage(), null);
		}
		return textContainer;
	}

	public ContactCollection getContacts() {
		return contacts;
	}

	public String getUserMessage() {
		return textMessage.getText();
	}

	public void updateSendButton() {
		getSendButton().setEnabled(getContactTable().getRowCount() != 0 && getContentTable().getRowCount() != 0);
	}

	private void updateToClockLabel() {
		getClockPanel().remove(getBusyLabel());
		getClockLabel().setName(CLOCK_LABEL_NAME);
	}

	public boolean isTwitterSelected() {
		return getTwitterCheckBox().isSelected();
	}

}

final class SendContentTable extends JTable {
	private static final long serialVersionUID = 1L;
	private final Color bg;
	private final Color selectionBg;

	public SendContentTable(Color bg, Color selectionBg) {
		this.bg = bg;
		this.selectionBg = selectionBg;
		setBackground(bg);
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(bg);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (getSelectedRowCount() > 0) {
			g.setColor(selectionBg);
			g.fillRect(0, getSelectedRow() * getRowHeight(), getWidth(), getRowHeight() - 1);
		}

		this.setOpaque(false);
		super.paintComponent(g);
		this.setOpaque(true);
	}

}

final class ContentCellRenderer extends DeleteIconRenderer<Object> {
	private static final long serialVersionUID = 1L;
	private final Icon trackIcon;
	private final Icon playlistIcon;
	private final Icon folderIcon;

	public ContentCellRenderer() {
		super(null);
		folderIcon = UIManager.getDefaults().getIcon("icons.folderBlue");
		playlistIcon = UIManager.getDefaults().getIcon("icons.playlistBlue");
		trackIcon = UIManager.getDefaults().getIcon("icons.trackBlue");
	}

	@Override
	public Icon getIcon(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value instanceof Track) {
			return trackIcon;
		}
		if (value instanceof Playlist) {
			return playlistIcon;
		}
		if (value instanceof Folder) {
			return folderIcon;
		}
		return null;
	}
}

final class ContactCellRenderer extends DeleteIconRenderer<ContactInfo> {
	private static final long serialVersionUID = 1L;
	private final Icon contactIcon = SynthIcons.ONLLINE_ICON;
	private final Icon facebookContactIcon = SynthIcons.FACEBOOK_ONLINE_ICON;

	public ContactCellRenderer() {
		super(new CellFilter<ContactInfo>() {
			@Override
			public Object filter(ContactInfo value, int row, int column) {
				return value.getNickName();
			}
		});
	}

	@Override
	public Icon getIcon(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		ContactInfo contact = (ContactInfo)value;
		return contact.getChatType().equals(ChatType.ALL) ? contactIcon : facebookContactIcon;
	}
}

abstract class DeleteIconRenderer<T> implements TableCellRenderer {
	private static final Dimension LEFT_SPACE_SIZE = new Dimension(15, 15);
	private static final Dimension DELETE_LABEL_SIZE = new Dimension(17, 14);
	private static final long serialVersionUID = 1L;
	private final Icon deleteIcon;
	private final JLabel deleteLabel;
	private final JPanel panel;
	private final JLabel label;
	private final CellFilter<T> filter;

	public DeleteIconRenderer(CellFilter<T> filter) {
		this.filter = filter;
		deleteIcon = UIManager.getDefaults().getIcon("icons.deleteRed");
		deleteLabel = new JLabel();
		deleteLabel.setText("");
		deleteLabel.setOpaque(false);
		deleteLabel.setSize(DELETE_LABEL_SIZE);
		label = new JLabel();
		label.setOpaque(false);
		JPanel leftSpace = new JPanel();
		leftSpace.setSize(LEFT_SPACE_SIZE);
		panel = new JPanel(new BorderLayout());
		panel.add(leftSpace, BorderLayout.WEST);
		panel.add(label, BorderLayout.CENTER);
		panel.add(deleteLabel, BorderLayout.EAST);

	}

	@Override
	@SuppressWarnings("unchecked")
	public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		label.setIcon(getIcon(table, value, isSelected, hasFocus, row, column));
		label.setText(filter == null ? value.toString() : filter.filter((T) value, row, column).toString());
		if (isSelected) {
			deleteLabel.setIcon(deleteIcon);
			deleteLabel.setText(" ");
		} else {
			deleteLabel.setIcon(null);
			deleteLabel.setText("");
		}
		return panel;
	}

	public abstract Icon getIcon(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column);

}

final class ContactComparator implements Comparator<ContactInfo> {
	@Override
	public int compare(ContactInfo o1, ContactInfo o2) {
		if(o1.getChatType().equals(ChatType.FACEBOOK)){
			if(o2.getChatType().equals(ChatType.FACEBOOK)){
				return o1.getNickName().compareTo(o2.getNickName());
			}
			if(o2.getChatType().equals(ChatType.ALL)){
				return 1;
			}
		}
		if(o1.getChatType().equals(ChatType.ALL)){
			if(o2.getChatType().equals(ChatType.ALL)){
				return o1.getNickName().compareTo(o2.getNickName());
			}
			if(o2.getChatType().equals(ChatType.FACEBOOK)){
				return -1;
			}
		}
		return o1.getNickName().compareTo(o2.getNickName());
	}

}

final class ModelComparator implements Comparator<Object> {

	@Override
	public int compare(Object obj1, Object obj2) {
		if (obj1 instanceof Track) {
			if (obj2 instanceof Track) {
				return ((Track) obj1).getName().compareTo(((Track) obj2).getName());
			}
			if (obj2 instanceof Playlist) {
				return 1;
			}
			if (obj2 instanceof Folder) {
				return 1;
			}
		}
		if (obj1 instanceof Playlist) {
			if (obj2 instanceof Track) {
				return -1;
			}
			if (obj2 instanceof Playlist) {
				return ((Playlist) obj1).getName().compareTo(((Playlist) obj2).getName());
			}
			if (obj2 instanceof Folder) {
				return 1;
			}
		}
		if (obj1 instanceof Folder) {
			if (obj2 instanceof Track) {
				return -1;
			}
			if (obj2 instanceof Playlist) {
				return -1;
			}
			if (obj2 instanceof Folder) {
				return ((Folder) obj1).getName().compareTo(((Folder) obj2).getName());
			}
		}
		throw new RuntimeException("WTF");
	}

}
