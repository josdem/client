package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterEvent.Type;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.TrashTable;
import com.all.client.view.components.TrashTableRowFilter;
import com.all.client.view.util.MacUtils;
import com.all.core.actions.Actions;
import com.all.core.common.view.util.JTextFieldLimit;
import com.all.core.model.Model;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

@Component
public class TrashPanel extends JPanel implements Internationalizable {
	private static final Log LOG = LogFactory.getLog(TrashPanel.class);
	private static final long serialVersionUID = 1L;
	private static final Dimension DEFAULT_TABPANEL_SIZE = new Dimension(581, 24);
	private static final Dimension MAXIMUM_TABPANEL_SIZE = new Dimension(1005, 24);
	private static final Dimension MINIMUM_TABPANEL_SIZE = new Dimension(413, 24);
	private static final Dimension DEFAULT_SIZE = new Dimension(600, 500);
	private static final Dimension MIMIMUM_SEPARATOR_PANEL_SIZE = new Dimension(10, 2);
	private static final Dimension DEFAULT_SEPARATOR_PANEL_SIZE = new Dimension(450, 2);
	private static final Dimension DEFAULT_CLEARSEARCH_BUTTON_SIZE = new Dimension(30, 22);
	private static final Dimension DEFAULT_LUPA_BUTTON_SIZE = new Dimension(28, 22);
	private static final Dimension DEFAULT_SEARCH_TEXT_SIZE = new Dimension(153, 22);
	private static final Dimension MIMIMUM_SEARCH_TEXT_SIZE = new Dimension(45, 22);
	private static final Dimension MAXIMUM_SEARCH_PANEL_SIZE = new Dimension(200, 22);
	private static final Dimension MINIMUM_SEARCH_PANEL_SIZE = new Dimension(92, 22);
	private static final Dimension DEFAULT_SEARCH_PANEL_SIZE = new Dimension(208, 22);
	private static final Dimension MINIMUM_TOOLBAR_SIZE = new Dimension(10, 24);
	private static final Dimension DEFAULT_TOOLBAR_SIZE = new Dimension(450, 24);
	private static final int MAX_CHARACTERS = 40;

	private JPanel toolbarPanel;
	private JPanel mainDescriptionPanel;
	private JPanel searchPanel;
	private JPanel tabPanel;
	private JPanel separatorPanel;
	private JScrollPane descriptionScrollPane;
	private JTextField searchTextField;
	private JPopupMenu libraryPopupMenu;
	private JMenuItem deleteMenuItem;
	private JMenuItem restoreMenuItem;
	private JButton lupaButton;
	private JButton clearSearchButton;
	private TrashTable trashTable;

	@Autowired
	private ViewEngine viewEngine;

	public TrashPanel() {
		this(null);
	}

	TrashPanel(TrashTable trashTable) {
		this.trashTable = trashTable;
		initialize();
		setup();
	}

	private void setup() {
		getTrashTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3 || MacUtils.isRMCOnMac(e)) {
					int row = getTrashTable().rowAtPoint(e.getPoint());
					getTrashTable().selectedRow(row);
					getLibraryPopupMenu().show(getTrashTable(), e.getX(), e.getY());
				}
			}
		});
		ArrayList<SortKey> keys = new ArrayList<SortKey>();
		keys.add(new SortKey(0, SortOrder.ASCENDING));
		getTrashTable().getRowSorter().setSortKeys(keys);
	}

	@PostConstruct
	public void setupSearch() {
		getSearchTextField().getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				resetFilter(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				resetFilter(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				resetFilter(e);
			}

			private void resetFilter(DocumentEvent event) {
				String text = "";
				try {
					text = event.getDocument().getText(0, event.getDocument().getLength());
				} catch (BadLocationException e) {
					LOG.error(e, e);
				}
				doSearch(text);
			}
		});
		getClearSearchButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getSearchTextField().setText("");
			}
		});
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				getTrashTable().setModel(viewEngine.get(Model.USER_TRASH));
			}
		});
		this.getTrashTable().getRowSorter().addRowSorterListener(new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				if (e.getType() == Type.SORTED) {
					viewEngine.send(Actions.Application.SET_CURRENT_DISPLAYED_ITEM_COUNT, new ValueAction<Integer>(getTrashTable().getRowCount()));
				}
			}
		});

	}

	@Override
	public void internationalize(Messages messages) {
		getDeleteMenuItem().setText(messages.getMessage("delete"));
		getRestoreMenuItem().setText(messages.getMessage("restore"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Autowired
	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@SuppressWarnings("unchecked")
	private void doSearch(String text) {
		TableRowSorter sorter = (TableRowSorter) getTrashTable().getRowSorter();
		text = text.trim();
		if (text.length() == 0) {
			sorter.setRowFilter(null);
			getClearSearchButton().setName("clearSearchButtonInvisible");
		} else {
			getClearSearchButton().setName("clearSearchButtonVisible");
			sorter.setRowFilter(new TrashTableRowFilter());
		}
	}

	private void initialize() {
		GridBagConstraints mainDescriptionConstraints = new GridBagConstraints();
		mainDescriptionConstraints.gridx = 0;
		mainDescriptionConstraints.fill = GridBagConstraints.BOTH;
		mainDescriptionConstraints.weightx = 1.0D;
		mainDescriptionConstraints.weighty = 1.0D;
		mainDescriptionConstraints.gridwidth = 2;
		mainDescriptionConstraints.gridy = 2;
		GridBagConstraints toolBarConstraints = new GridBagConstraints();
		toolBarConstraints.gridx = 0;
		toolBarConstraints.fill = GridBagConstraints.HORIZONTAL;
		toolBarConstraints.weightx = 1.0D;
		toolBarConstraints.gridy = 0;
		GridBagConstraints separatorConstraints = new GridBagConstraints();
		separatorConstraints.gridx = 0;
		separatorConstraints.fill = GridBagConstraints.HORIZONTAL;
		separatorConstraints.weightx = 1.0D;
		separatorConstraints.gridy = 1;
		this.setName("descriptionPanel");
		this.setLayout(new GridBagLayout());
		this.setSize(DEFAULT_SIZE);
		this.add(getToolbarPanel(), toolBarConstraints);
		this.add(getMainDescriptionPanel(), mainDescriptionConstraints);
		this.add(getSeparatorPanel(), separatorConstraints);
	}

	private JPanel getToolbarPanel() {
		if (toolbarPanel == null) {
			GridBagConstraints tabPanelConstraints = new GridBagConstraints();
			tabPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			tabPanelConstraints.gridx = 0;
			tabPanelConstraints.gridy = 0;
			tabPanelConstraints.gridwidth = 1;
			tabPanelConstraints.weightx = 1.0D;

			GridBagConstraints searchPanelConstraints = new GridBagConstraints();
			searchPanelConstraints.gridx = 1;
			searchPanelConstraints.gridy = 0;

			toolbarPanel = new JPanel();
			toolbarPanel.setLayout(new GridBagLayout());
			toolbarPanel.setPreferredSize(DEFAULT_TOOLBAR_SIZE);
			toolbarPanel.setMinimumSize(MINIMUM_TOOLBAR_SIZE);
			toolbarPanel.setName("toolbarPanel");
			toolbarPanel.setMaximumSize(MINIMUM_TOOLBAR_SIZE);
			toolbarPanel.add(getSearchPanel(), searchPanelConstraints);
			toolbarPanel.add(getTabPanel(), tabPanelConstraints);
		}
		return toolbarPanel;
	}

	private JPanel getMainDescriptionPanel() {
		if (mainDescriptionPanel == null) {
			GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
			scrollPaneConstraints.fill = GridBagConstraints.BOTH;
			scrollPaneConstraints.gridy = 0;
			scrollPaneConstraints.weightx = 1.0;
			scrollPaneConstraints.weighty = 1.0;
			scrollPaneConstraints.gridx = 0;
			mainDescriptionPanel = new JPanel();
			mainDescriptionPanel.setLayout(new GridBagLayout());
			mainDescriptionPanel.add(getDescriptionScrollPane(), scrollPaneConstraints);
		}
		return mainDescriptionPanel;
	}

	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.gridx = 0;
			searchPanel = new JPanel();
			searchPanel.setLayout(new BorderLayout());
			searchPanel.setPreferredSize(DEFAULT_SEARCH_PANEL_SIZE);
			searchPanel.setMinimumSize(MINIMUM_SEARCH_PANEL_SIZE);
			searchPanel.setMaximumSize(MAXIMUM_SEARCH_PANEL_SIZE);
			searchPanel.add(getLupaBoton(), BorderLayout.WEST);
			searchPanel.add(getSearchTextField(), BorderLayout.CENTER);
			searchPanel.add(getClearSearchButton(), BorderLayout.EAST);
		}
		return searchPanel;
	}

	private JScrollPane getDescriptionScrollPane() {
		if (descriptionScrollPane == null) {
			descriptionScrollPane = new JScrollPane();
			descriptionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			descriptionScrollPane.setViewportView(getTrashTable());
			descriptionScrollPane.setName("descriptionScrollPane");
		}
		return descriptionScrollPane;
	}

	private JTextField getSearchTextField() {
		if (searchTextField == null) {
			searchTextField = new JTextField();
			searchTextField.setDocument(new JTextFieldLimit(MAX_CHARACTERS));
			searchTextField.setName("searchTextField");
			searchTextField.setMinimumSize(MIMIMUM_SEARCH_TEXT_SIZE);
			searchTextField.setMaximumSize(DEFAULT_SEARCH_TEXT_SIZE);
			searchTextField.setPreferredSize(DEFAULT_SEARCH_TEXT_SIZE);
		}
		return searchTextField;
	}

	private JPopupMenu getLibraryPopupMenu() {
		if (libraryPopupMenu == null) {
			libraryPopupMenu = new JPopupMenu();
			libraryPopupMenu.add(getDeleteMenuItem());
			libraryPopupMenu.add(getRestoreMenuItem());
		}
		return libraryPopupMenu;
	}

	private JMenuItem getDeleteMenuItem() {
		if (deleteMenuItem == null) {
			deleteMenuItem = new JMenuItem();
		}
		return deleteMenuItem;
	}

	private JMenuItem getRestoreMenuItem() {
		if (restoreMenuItem == null) {
			restoreMenuItem = new JMenuItem();
		}
		return restoreMenuItem;
	}

	private JButton getLupaBoton() {
		if (lupaButton == null) {
			lupaButton = new JButton();
			lupaButton.setPreferredSize(DEFAULT_LUPA_BUTTON_SIZE);
			lupaButton.setName("lupaSearchButton");
		}
		return lupaButton;
	}

	private JButton getClearSearchButton() {
		if (clearSearchButton == null) {
			clearSearchButton = new JButton();
			clearSearchButton.setName("clearSearchButtonInvisible");
			clearSearchButton.setPreferredSize(DEFAULT_CLEARSEARCH_BUTTON_SIZE);
		}
		return clearSearchButton;
	}

	private JPanel getTabPanel() {
		if (tabPanel == null) {
			tabPanel = new JPanel();
			tabPanel.setLayout(new GridBagLayout());
			tabPanel.setMinimumSize(MINIMUM_TABPANEL_SIZE);
			tabPanel.setMaximumSize(MAXIMUM_TABPANEL_SIZE);
			tabPanel.setPreferredSize(DEFAULT_TABPANEL_SIZE);
		}
		return tabPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setName("backgroundPanel");
			separatorPanel.setPreferredSize(DEFAULT_SEPARATOR_PANEL_SIZE);
			separatorPanel.setMinimumSize(MIMIMUM_SEPARATOR_PANEL_SIZE);
			separatorPanel.setMaximumSize(MIMIMUM_SEPARATOR_PANEL_SIZE);
		}
		return separatorPanel;
	}

	private TrashTable getTrashTable() {
		if (trashTable == null) {
			trashTable = new TrashTable();
		}
		return trashTable;
	}
}
