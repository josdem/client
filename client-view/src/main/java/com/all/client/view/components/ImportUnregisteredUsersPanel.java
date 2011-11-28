package com.all.client.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.all.client.model.MailList;
import com.all.core.common.view.SynthColors;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

/**
 * Understands how to show imported contacts
 */

public class ImportUnregisteredUsersPanel extends JPanel implements Internationalizable {

	private static final String SEPARATOR_NAME = "contactSeparatorBackground";
	private static final String SEPARATOR_PANEL_NAME = "bottomPanelSeparator";
	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 515, 480, 2);
	private static final String CANCEL_BUTTON_NAME = "buttonSkip";
	private static final String INVITE_BUTTON_NAME = "buttonInvite";
	private static final Rectangle INVITATION_LABEL2_BOUNDS = new Rectangle(11, 40, 450, 25);
	private static final Rectangle SCROLLPANE_BOUNDS = new Rectangle(1, 94, 488, 301);
	private static final Rectangle INVITATION_LABEL1_BOUNDS = new Rectangle(11, 25, 450, 25);
	private static final Rectangle DESCRIPTION_TABLE_BOUNDS = new Rectangle(11, 10, 450, 25);
	private static final Rectangle SELECT_ALL_CHECKBOX_BOUNDS = new Rectangle(7, 68, 19, 17);
	private static final long serialVersionUID = 1L;
	private static final Rectangle INVITE_BUTTON_BOUNDS = new Rectangle(251, 523, 80, 22);
	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(161, 523, 80, 22);
	private static final Rectangle INSTRUCTION_LABEL_BOUNDS = new Rectangle(21, 404, 450, 18);
	private static final Rectangle SEPARATOR_BOUNDS = new Rectangle(0, 395, 488, 1);
	private static final Rectangle SELECT_ALL_LABEL_BOUNDS = new Rectangle(29, 70, 110, 14);
	private static final Dimension UNREGISTERED_USERS_PANEL_SIZE = new Dimension(490, 555);
	private static final String SEND_CONTENT_DEFAULT_MESSAGES = "inviteFriends.default.message";
	private static final String TEXT_MESSAGE_NAME = "joinInvitationtextArea";
	private static final String TEXT_MESSAGE_DEFAULT_NAME = "joinInvitationtextAreaDefault";
	private static final String EMPTY_TEXT = "";
	private JLabel invitationLabel1;
	private JLabel invitationLabel2;
	private JLabel descriptionLabel;
	private JScrollPane scrollPane;
	private JTable table;
	private JPanel separatorPanel;
	private JButton inviteButton;
	private JButton cancelButton;
	private JCheckBox selectAllcheckBox;
	private boolean selectAll = false;
	private JLabel instructionLabel;
	private JLabel selectAllLabel;
	private TableColumn nickname;
	private TableColumn email;
	private ScrollableTextArea scrollableTextArea;
	private boolean isFirstKeyTyped = true;
	private final Messages messages;

	public ImportUnregisteredUsersPanel(Messages messages) {
		this.messages = messages;
		this.setLayout(null);
		this.add(getDescriptionLabel());
		this.add(getInvitationLabel1());
		this.add(getInvitationLabel2());
		this.add(getSelectAllCheckbox());
		this.add(getSelectAllLabel());
		this.add(getScrollTablePanel());
		this.add(getBottomSeparatorPixelPanel());
		this.add(getInstructionLabel());
		this.add(getScrollableTextArea());
		this.add(getSeparator());
		this.add(getCancelButton());
		this.add(getInviteButton());
		setDefaultSize();
	}

	private void setDefaultSize() {
		setSize(UNREGISTERED_USERS_PANEL_SIZE);
		setPreferredSize(UNREGISTERED_USERS_PANEL_SIZE);
		setMaximumSize(UNREGISTERED_USERS_PANEL_SIZE);
		setMinimumSize(UNREGISTERED_USERS_PANEL_SIZE);
	}

	public void fillData(final List<MailList> emails) {
		for (MailList mail : emails) {
			((DefaultTableModel) table.getModel()).addRow(new Object[] { mail, mail });
		}
	}

	public String getInvitationText() {
		return scrollableTextArea.getText();
	}

	private JLabel getSelectAllLabel() {
		if (selectAllLabel == null) {
			selectAllLabel = new JLabel();
			selectAllLabel.setBounds(SELECT_ALL_LABEL_BOUNDS);
			selectAllLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
		}
		return selectAllLabel;
	}

	private JCheckBox getSelectAllCheckbox() {
		if (selectAllcheckBox == null) {
			selectAllcheckBox = new JCheckBox();
			selectAllcheckBox.setBounds(SELECT_ALL_CHECKBOX_BOUNDS);
			selectAllcheckBox.setName(SynthFonts.BOLD_FONT10_BLACK);
			selectAllcheckBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					selectAll = !selectAll;
					for (int i = 0; i < getTable().getModel().getRowCount(); i++) {
						((MailList) getTable().getModel().getValueAt(i, 0)).setChecked(selectAll);
						((DefaultTableModel) getTable().getModel()).fireTableDataChanged();

					}
				}
			});
		}
		return selectAllcheckBox;
	}

	private JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel();
			descriptionLabel.setBounds(DESCRIPTION_TABLE_BOUNDS);
			descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
			descriptionLabel.setName(SynthFonts.BOLD_FONT12_BLACK);
		}
		return descriptionLabel;
	}

	private JLabel getInvitationLabel1() {
		if (invitationLabel1 == null) {
			invitationLabel1 = new JLabel();
			invitationLabel1.setHorizontalAlignment(JLabel.LEFT);
			invitationLabel1.setBounds(INVITATION_LABEL1_BOUNDS);
			invitationLabel1.setName(SynthFonts.PLAIN_FONT11_BLACK);
		}
		return invitationLabel1;
	}

	private JLabel getInvitationLabel2() {
		if (invitationLabel2 == null) {
			invitationLabel2 = new JLabel();
			invitationLabel2.setHorizontalAlignment(JLabel.LEFT);
			invitationLabel2.setBounds(INVITATION_LABEL2_BOUNDS);
			invitationLabel2.setName(SynthFonts.PLAIN_FONT11_BLACK);
		}
		return invitationLabel2;
	}
	
	private JScrollPane getScrollTablePanel() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(SCROLLPANE_BOUNDS);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setViewport(new InterlacedViewPort(getTable()));
		}
		return scrollPane;
	}

	private JTable getTable() {
		if (table == null) {
			table = new MailTable();
			// Setting up table properties
			JTableHeader tableHeader = new TableHeader();
			tableHeader.setDefaultRenderer(new HeaderRenderer());
			Dimension headerSize = new Dimension(100, 25);
			tableHeader.setPreferredSize(headerSize);
			tableHeader.setReorderingAllowed(false);
			tableHeader.setResizingAllowed(false);
			table.setTableHeader(tableHeader);
			table.setDragEnabled(false);
			table.setShowVerticalLines(false);
			table.setShowHorizontalLines(false);

			DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
			columnModel.addColumn(getNickNameHeader());
			columnModel.addColumn(getEmailHeader());

			DefaultTableModel tableModel = new DefaultTableModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			tableModel.addColumn(email);
			tableModel.addColumn(nickname);

			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			// First add the TableModel, then the column model!!!!!!
			table.setModel(tableModel);
			table.setColumnModel(columnModel);

			table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int row = e.getY() / table.getRowHeight();
					JTable tableComponent = (JTable) e.getComponent();
					if (e.getX() < 30 && row < tableComponent.getModel().getRowCount()) {
						MailList mail = (MailList) tableComponent.getModel().getValueAt(row, 0);
						mail.setChecked(!mail.isChecked());
						((DefaultTableModel) getTable().getModel()).fireTableDataChanged();
					}
				}
			});
		}
		return table;
	}

	private TableColumn getEmailHeader() {
		if (email == null) {
			email = new TableColumn();
			email.setCellRenderer(new DefaultRenderer());
		}
		return email;
	}

	private TableColumn getNickNameHeader() {
		if (nickname == null) {
			nickname = new TableColumn();
			nickname.setCellRenderer(new EmailCellRenderer());
		}
		return nickname;
	}

	@Override
	public void internationalize(Messages messages) {
		getInvitationLabel1().setText(messages.getMessage("inviteFriends.invitationLabel1"));
		getInvitationLabel2().setText(messages.getMessage("inviteFriends.invitationLabel2"));
		getDescriptionLabel().setText(messages.getMessage("inviteFriends.descLabel"));
		getInviteButton().setText(messages.getMessage("InviteContacts.inviteButton"));
		getCancelButton().setText(messages.getMessage("cancel"));
		getInstructionLabel().setText(messages.getMessage("inviteFriends.personalNoteLabel"));
		getSelectAllLabel().setText(messages.getMessage("InviteContacts.select"));
		getNickNameHeader().setHeaderValue(messages.getMessage("inviteFriends.contact.name"));
		getEmailHeader().setHeaderValue(messages.getMessage("inviteFriends.contact.email"));
	}

	private JPanel getBottomSeparatorPixelPanel() {
		JPanel separator = new JPanel();
		separator.setName(SEPARATOR_NAME);
		separator.setBounds(SEPARATOR_BOUNDS);
		return separator;
	}

	private JLabel getInstructionLabel() {
		if (instructionLabel == null) {
			instructionLabel = new JLabel();
			instructionLabel.setName(SynthFonts.BOLD_FONT12_BLACK);
			instructionLabel.setBounds(INSTRUCTION_LABEL_BOUNDS);
		}
		return instructionLabel;
	}

	private ScrollableTextArea getScrollableTextArea() {
		if (scrollableTextArea == null) {
			scrollableTextArea = new ScrollableTextArea(20, 430, 450, 70, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			setDefaultMessage();
			
			scrollableTextArea.getTextArea().addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if(isFirstKeyTyped) {
						isFirstKeyTyped = false;
						scrollableTextArea.getTextArea().setText(EMPTY_TEXT);
						scrollableTextArea.getTextArea().setName(TEXT_MESSAGE_NAME);
					}
				}
			});
			scrollableTextArea.getTextArea().addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if(EMPTY_TEXT.equals(getScrollableTextArea().getText())) {
						setDefaultMessage();
					}
				}
			});
		}
		return scrollableTextArea;
	}

	private void setDefaultMessage() {
		isFirstKeyTyped = true;
		scrollableTextArea.getTextArea().setText(messages.getMessage(SEND_CONTENT_DEFAULT_MESSAGES));
		scrollableTextArea.getTextArea().setName(TEXT_MESSAGE_DEFAULT_NAME);
	}

	private JPanel getSeparator() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(null);
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
		}
		return separatorPanel;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setName(CANCEL_BUTTON_NAME);
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.setVisible(true);
		}
		return cancelButton;
	}

	private JButton getInviteButton() {
		if (inviteButton == null) {
			inviteButton = new JButton();
			inviteButton.setName(INVITE_BUTTON_NAME);
			inviteButton.setBounds(INVITE_BUTTON_BOUNDS);
		}
		return inviteButton;
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public void resetSize() {
		setDefaultSize();
	}

	public void addActionListenerToCancelButton(ActionListener actionListener) {
		getCancelButton().addActionListener(actionListener);
	}

	public void addActionListenerToInviteButton(ActionListener actionListener) {
		getInviteButton().addActionListener(actionListener);
	}
}

class DefaultRenderer extends DefaultTableCellRenderer {
	private static final Rectangle LABEL_BOUNDS = new Rectangle(8, 5, 240, 15);

	private static final long serialVersionUID = 1L;

	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();

	public DefaultRenderer() {
		label.setBounds(LABEL_BOUNDS);
		label.setOpaque(false);
		panel.setLayout(null);
		panel.setOpaque(false);
		panel.add(label);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		label.setText(((MailList) value).getEmail());
		return panel;
	}
}

class HeaderRenderer extends DefaultTableCellRenderer {
	private static final Rectangle LABEL_BOUNDS = new Rectangle(8, 5, 240, 15);

	private static final long serialVersionUID = 1L;

	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();

	public HeaderRenderer() {
		label.setBounds(LABEL_BOUNDS);
		label.setName(SynthFonts.BOLD_FONT12_BLACK);
		panel.setLayout(null);
		panel.setOpaque(false);
		panel.add(label);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		label.setText((String) value);
		return panel;
	}
}

class EmailCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

	private static final Rectangle NAME_BOUNDS = new Rectangle(33, 5, 211, 15);
	private static final Rectangle CHECKBOX_BOUNDS = new Rectangle(6, 4, 19, 17);

	private JPanel panel = new JPanel();
	private JCheckBox checkbox = new JCheckBox();
	private JLabel name = new JLabel();

	public EmailCellRenderer() {
		panel.setLayout(null);
		panel.setOpaque(false);
		checkbox.setBounds(CHECKBOX_BOUNDS);
		checkbox.setOpaque(false);
		name.setBounds(NAME_BOUNDS);
		name.setOpaque(false);
		panel.add(checkbox, BorderLayout.WEST);
		panel.add(name, BorderLayout.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		MailList mailList = (MailList) value;
		name.setText(mailList.getName());
		checkbox.setSelected(mailList.isChecked());
		return panel;
	}
}

class TableHeader extends JTableHeader {
	private final Color headerColor = UIManager.getDefaults().getColor("mailTable.headerColor");
	private final Color borderColor = UIManager.getDefaults().getColor("mailTable.borderColor");
	private static final long serialVersionUID = 1L;

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(headerColor);
		g.fillRect(0, 0, 488, getHeight());

		int x = getColumnModel().getColumn(0).getWidth() - 1;
		g.setColor(borderColor);
		g.drawLine(0, 0, getWidth(), 0);
		g.drawLine(x, 0, x, getHeight());

		this.setOpaque(false);
		super.paintComponent(g);
		this.setOpaque(true);
	}
}

class MailTable extends JTable {
	private static final long serialVersionUID = 1L;
	private final Color selectedColor = UIManager.getDefaults().getColor("mailTable.selectedColor");
	private final Color borderColor = UIManager.getDefaults().getColor("mailTable.borderColor");
	private final Color backgroundColor = SynthColors.WHITE255_255_255;
	private final Color alternateColor = UIManager.getDefaults().getColor("mailTable.alternateColor");
	private Color[] rowColors = new Color[] { backgroundColor, alternateColor };

	public MailTable() {
		setRowHeight(25);
		setRowMargin(0);
	}

	@Override
	protected void paintComponent(Graphics g) {
		int maxRows = (this.getHeight() / this.getRowHeight()) + 1;
		for (int i = 0; i < maxRows; i++) {
			MailList mailList = null;
			if (i < getModel().getRowCount()) {
				mailList = (MailList) getValueAt(i, 0);
				if (mailList.isChecked()) {
					g.setColor(selectedColor);
				} else {
					g.setColor(rowColors[i & 1]);
					getRowHeight();
				}
			} else {
				g.setColor(rowColors[i & 1]);
			}
			g.fillRect(0, i * getRowHeight(), getWidth(), getRowHeight());
			if (mailList != null && mailList.isChecked()) {
				g.setColor(backgroundColor);
				g.drawLine(0, (i + 1) * getRowHeight() - 1, getWidth(), (i + 1) * getRowHeight() - 1);
			}
		}

		int x = getColumnModel().getColumn(0).getWidth() - 1;
		g.setColor(borderColor);
		g.drawLine(x, 0, x, getHeight());

		this.setOpaque(false);
		super.paintComponent(g);
		this.setOpaque(true);
	}
}

class InterlacedViewPort extends JViewport {
	private static final long serialVersionUID = 1L;
	private final Color borderColor = UIManager.getDefaults().getColor("mailTable.borderColor");
	private final Color backgroundColor = SynthColors.WHITE255_255_255;
	private final Color alternateColor = UIManager.getDefaults().getColor("mailTable.alternateColor");
	private Color[] rowColors = new Color[] { backgroundColor, alternateColor };
	private final JTable table;

	public InterlacedViewPort(JTable table) {
		this.table = table;
		setView(table);
	}

	@Override
	protected void paintComponent(Graphics g) {
		int maxRows = (getHeight() / table.getRowHeight()) + 1;
		for (int i = 0; i < maxRows; i++) {
			g.setColor(rowColors[i & 1]);
			g.fillRect(0, i * table.getRowHeight(), getWidth(), table.getRowHeight());
		}
		int x = table.getColumnModel().getColumn(0).getWidth() - 1;
		g.setColor(borderColor);
		g.drawLine(x, 0, x, getHeight());

	}

}
