package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.view.contacts.EmailContactPanel;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;

public final class ContactResultsPanel extends JPanel implements Internationalizable {

	private static final Rectangle SKIP_BUTTON_BOUNDS = new Rectangle(77, 524, 80, 22);

	private static final Rectangle INVITE_BUTTON_BOUNDS = new Rectangle(167, 524, 80, 22);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 515, 314, 2);

	private static final Point SELECT_ALL_CHECKBOX_LOCATION = new Point(4, 68);

	private static final Dimension SELECT_ALL_CHECKBOX_SIZE = new Dimension(20, 17);

	private static final long serialVersionUID = 4176623792248141754L;

	private static final int BASE_WIDTH = 324;
	private static final int SEARCHPANEL_HEIGHT = 104;
	private static final int BUTTONSPANEL_HEIGHT = 40;
	private static final int BASE_HEIGHT = SEARCHPANEL_HEIGHT + BUTTONSPANEL_HEIGHT;
	private static final String ERROR_PANEL = "ERROR_PANEL";
	private static final String EMPTY_PANEL = "EMPTY_PANEL";
	private static final String MULTIPLE_RESULT_PANEL = "MULTIPLE_RESULT_PANEL";

	private JPanel resultsPanel;

	private JPanel emptyPanel;
	private JLabel errorLabel;
	private JLabel manyResultsLabel;
	private JPanel manyResultsPanel;
	private JLabel sendInvitationLabel;
	private JLabel emailLabel;
	private DefaultTableModel model = new DefaultTableModel();
	private Messages messages;
	private JLabel selectLabel;
	private JButton inviteButton;
	private JButton skipButton;
	private JPanel separatorPanel;
	private Map<Integer, CheckBoxCellEditor> checkEditorMap = new HashMap<Integer, CheckBoxCellEditor>();
	private Map<String, Boolean> checkedContactsMap = new HashMap<String, Boolean>();
	private boolean selectAll = false;
	private JScrollPane scrollPane = null;

	private EmailContactCellRenderer emailContactCellRenderer;

	private JCheckBox selectAllcheckBox;

	private final List<ContactInfo> contacts;

	private Log log = LogFactory.getLog(this.getClass());

	@Deprecated
	public ContactResultsPanel() {
		this.contacts = null;
		this.messages = null;
	}

	public ContactResultsPanel(List<ContactInfo> contacts, Messages messages) {
		this.contacts = contacts;
		this.messages = messages;
		init();
	}

	private void init() {
		this.setLayout(new BorderLayout());
		this.setSize(BASE_WIDTH, BASE_HEIGHT);
		this.setPreferredSize(new Dimension(BASE_WIDTH, BASE_HEIGHT));
		this.add(getResultsPanel(), BorderLayout.CENTER);
		this.setVisible(true);
		this.showContacts(contacts);
		this.internationalize(messages);

		// Cell renderer
		emailContactCellRenderer = new EmailContactCellRenderer();
	}

	@SuppressWarnings("unused")
	private void showNoResultsPanel() {
		String messageKey;
		messageKey = "addContact.noResultsForName";
		sendInvitationLabel.setVisible(false);
		emailLabel.setVisible(false);
		getErrorLabel().setText(messages.getMessage("addContact.errorLabel"));
		showPanel(ERROR_PANEL);
	}

	private JLabel getErrorLabel() {
		if (errorLabel == null) {
			errorLabel = new JLabel();
			errorLabel.setPreferredSize(new Dimension(BASE_WIDTH - 20, 35));
			errorLabel.setBounds(80, 4, BASE_WIDTH - 100, 40);
			errorLabel.setName(SynthFonts.PLAIN_FONT11_PURPLE50_15_50);
		}
		return errorLabel;
	}

	private JPanel getEmptyPanel() {
		if (emptyPanel == null) {
			emptyPanel = new JPanel();
			emptyPanel.setLayout(null);
			emptyPanel.setName("emptyPanel");
			emptyPanel.setPreferredSize(new Dimension(BASE_WIDTH, 0));
		}
		return emptyPanel;
	}

	private void showPanel(String panel) {
		CardLayout cardLayout = (CardLayout) getResultsPanel().getLayout();
		cardLayout.show(getResultsPanel(), panel);
	}

	private JPanel getResultsPanel() {
		if (resultsPanel == null) {
			resultsPanel = new JPanel();
			resultsPanel.setLayout(new CardLayout());
			resultsPanel.setPreferredSize(new Dimension(BASE_WIDTH, 0));
			resultsPanel.setName("resultsPanel");
			resultsPanel.add(getEmptyPanel(), EMPTY_PANEL);
			resultsPanel.add(getManyResultsPanel(), MULTIPLE_RESULT_PANEL);
			resultsPanel.setVisible(true);
		}
		return resultsPanel;
	}

	private JPanel getManyResultsPanel() {
		if (manyResultsPanel == null) {
			manyResultsLabel = new JLabel();
			manyResultsLabel.setBounds(8, 10, BASE_WIDTH - 12, 45);

			manyResultsPanel = new JPanel();
			manyResultsPanel.setLayout(null);
			manyResultsPanel.setPreferredSize(new Dimension(BASE_WIDTH, 100));
			manyResultsPanel.setName("manyResultsPanel");

			selectAllcheckBox = new JCheckBox();
			selectAllcheckBox.setLocation(SELECT_ALL_CHECKBOX_LOCATION);
			selectAllcheckBox.setSize(SELECT_ALL_CHECKBOX_SIZE);
			selectAllcheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					selectAll = !selectAll;
					for (int i = 0; i < model.getRowCount(); i++) {
						EmailContactPanel panel = (EmailContactPanel) model.getValueAt(i, 0);
						panel.getCheckBox().setSelected(selectAll);
					}
					model.fireTableDataChanged();
				}
			});

			manyResultsPanel.add(selectAllcheckBox);
			manyResultsPanel.add(getSelectLabel());
			manyResultsPanel.add(manyResultsLabel);

			manyResultsPanel.add(getSeparatorPixelPanel());
			manyResultsPanel.add(getResultsList());
			manyResultsPanel.add(getBottomSeparatorPixelPanel());

			manyResultsPanel.add(getSeparator());
			manyResultsPanel.add(getInviteButton());
			manyResultsPanel.add(getSkipButton());
		}
		return manyResultsPanel;
	}

	private JPanel getSeparatorPixelPanel() {
		JPanel separator = new JPanel();
		separator.setName("contactSeparatorBackground");
		separator.setBounds(0, 95, BASE_WIDTH, 1);
		return separator;
	}

	private JPanel getBottomSeparatorPixelPanel() {
		JPanel separator = new JPanel();
		separator.setName("contactSeparatorBackground");
		separator.setBounds(0, 500, BASE_WIDTH, 1);
		return separator;
	}

	public void fillResultsList(List<ContactInfo> contacts) {
		for (ContactInfo contactInfo : contacts) {
			model.addRow(new Object[] { new EmailContactPanel(contactInfo, true) });
			checkedContactsMap.put(contactInfo.getEmail(), false);
		}
	}

	public void selectAllContacts() {
		for (int i = 0; i < model.getRowCount(); i++) {
			model.getValueAt(i, 0);
		}
	}

	public List<ContactInfo> getCheckedContacts() {
		List<ContactInfo> selectedContacts = new ArrayList<ContactInfo>();
		for (int i = 0; i < model.getRowCount(); i++) {
			EmailContactPanel emailPanel = (EmailContactPanel) model.getValueAt(i, 0);
			if (emailPanel.isChecked()) {
				selectedContacts.add(emailPanel.getContactInfo());
			}
		}
		return selectedContacts;
	}

	private JComponent getResultsList() {
		JTable resultTable = new JTable() {
			private static final long serialVersionUID = -2174045751991670056L;

			@Override
			protected void paintComponent(Graphics g) {
				g.setColor(new Color(224, 224, 224));
				g.fillRect(0, 0, getWidth(), getHeight());
				this.setOpaque(false);
				super.paintComponent(g);
				this.setOpaque(true);
			}

			@Override
			public TableCellEditor getCellEditor(int row, int column) {
				if (!checkEditorMap.containsKey(row)) {
					checkEditorMap.put(row, new CheckBoxCellEditor());
				}
				return (TableCellEditor) checkEditorMap.get(row);
			}

			@Override
			public TableCellRenderer getCellRenderer(int row, int column) {
				return emailContactCellRenderer;
			}

		};
		resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane = new JScrollPane(resultTable);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension scrollPaneDimension = new Dimension(BASE_WIDTH, 404);
		scrollPane.setPreferredSize(scrollPaneDimension);
		scrollPane.setMinimumSize(scrollPaneDimension);
		scrollPane.setMaximumSize(scrollPaneDimension);

		scrollPane.setBounds(1, 96, scrollPaneDimension.width - 3, scrollPaneDimension.height);
		scrollPane.setBorder(null);
		// Setting up header
		JTableHeader jTableHeader = new JTableHeader();
		jTableHeader.setVisible(false);
		resultTable.setTableHeader(jTableHeader);

		// Setting up table properties
		resultTable.setShowVerticalLines(false);
		resultTable.setShowHorizontalLines(false);
		resultTable.setRowHeight(81);
		resultTable.setRowMargin(0);

		TableColumn mainColumn = new TableColumn();

		model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};
		model.addColumn(mainColumn);

		DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(mainColumn);

		resultTable.setModel(model);
		resultTable.setColumnModel(columnModel);
		resultTable.setName("ContactList");
		return scrollPane;
	}

	public void showContacts(List<ContactInfo> contacts) {
		fillResultsList(contacts);
		showPanel(MULTIPLE_RESULT_PANEL);
	}

	@Override
	public void internationalize(Messages messages) {
		manyResultsLabel.setText(messages.getMessage("InviteContacts.subtitle"));
		getSelectLabel().setText(messages.getMessage("InviteContacts.select"));
		inviteButton.setText(messages.getMessage("InviteContacts.inviteButton"));
		skipButton.setText(messages.getMessage("InviteContacts.skipButton"));
		getErrorLabel().setText(messages.getMessage("addContact.errorLabel"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	private JPanel getSeparator() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(null);
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName("bottomPanelSeparator");
		}
		return separatorPanel;
	}

	public JButton getInviteButton() {
		if (inviteButton == null) {
			inviteButton = new JButton();
			inviteButton.setName("buttonInvite");
			inviteButton.setBounds(INVITE_BUTTON_BOUNDS);
			inviteButton.setVisible(true);
		}
		return inviteButton;
	}

	JButton getSkipButton() {
		if (skipButton == null) {
			skipButton = new JButton();
			skipButton.setName("buttonSkip");
			skipButton.setBounds(SKIP_BUTTON_BOUNDS);
			skipButton.setVisible(true);
		}
		return skipButton;
	}

	public JLabel getSelectLabel() {
		if (selectLabel == null) {
			selectLabel = new JLabel();
			selectLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			selectLabel.setBounds(30, 69, BASE_WIDTH - 12, 14);
		}
		return selectLabel;
	}

	private class EmailContactCellRenderer implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			EmailContactPanel emailContactPanel = (EmailContactPanel) value;
			return new EmailContactPanelCell(emailContactPanel);
		}
	}

	private class CheckBoxCellEditor extends AbstractCellEditor implements TableCellEditor {
		private static final long serialVersionUID = 1L;
		private EmailContactPanel emailContactPanelEditor;

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			emailContactPanelEditor = (EmailContactPanel) value;
			boolean checked = emailContactPanelEditor.isChecked();
			emailContactPanelEditor.getCheckBox().setSelected(checked);
			EmailContactPanelCell panel = new EmailContactPanelCell(emailContactPanelEditor);
			log.debug(emailContactPanelEditor.getContactInfo().getEmail() + " is checked: " + checked);
			checkedContactsMap.put(emailContactPanelEditor.getContactInfo().getEmail(), checked);
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return emailContactPanelEditor;
		}

	}

	private class EmailContactPanelCell extends JPanel {
		private static final long serialVersionUID = 1L;
		private final EmailContactPanel emailContactPanelCell;

		public EmailContactPanelCell(EmailContactPanel emailContactPanel) {
			super();
			this.emailContactPanelCell = emailContactPanel;
			init();
		}

		private void init() {
			this.setName("searchResultsPanel");
			this.setLayout(null);
			emailContactPanelCell.setBackgroundPanelSize(new Dimension(316, 72));
			if (contacts.size() < 5) {
				emailContactPanelCell.setBackgroundPanelSize(new Dimension(316, 72));
			} else {
				emailContactPanelCell.setBackgroundPanelSize(new Dimension(303, 72));
			}
			this.add(emailContactPanelCell);
			JPanel bottomSeparator = new JPanel();
			bottomSeparator.setBounds(6, emailContactPanelCell.getHeight(), BASE_WIDTH - 13, 1);
			bottomSeparator.setName("contactSeparatorBackground");
			this.add(bottomSeparator);

			Dimension resultPaneDimension = new Dimension(BASE_WIDTH, emailContactPanelCell.getHeight()
					+ bottomSeparator.getHeight());
			this.setPreferredSize(resultPaneDimension);
			this.setSize(resultPaneDimension);
			this.setMaximumSize(resultPaneDimension);
			this.setMinimumSize(resultPaneDimension);
		}
	}

}
