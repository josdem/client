package com.all.client.view.chat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;

public class ChatDialogPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private static final Dimension DIALOG_PANEL_DEFAULT_SIZE = new Dimension(396, 403);

	private static final Dimension DIALOG_PANEL_MINIMUM_SIZE = new Dimension(259, 403);

	private static final Dimension FOOTER_PANEL_DEFAULT_SIZE = new Dimension(396, 17);

	private static final Dimension FOOTER_PANEL_MINIMUM_SIZE = new Dimension(330, 17);

	private static final Dimension HEADER_PANEL_DEFAULT_SIZE = new Dimension(396, 18);

	private static final Dimension HEADER_PANEL_MINIMUM_SIZE2 = new Dimension(330, 18);

	private static final Dimension SCROLLPANE_DEFAULT_SIZE = new Dimension(396, 368);

	private static final Dimension SCROLLPANE_MINIMUM_SIZE = new Dimension(330, 368);

	private static final Insets INSETS = new Insets(0, 2, 0, 2);

	private static final Insets FOOTER_PANEL_INSETS = new Insets(0, 2, 2, 2);

	private static final String FOOTER_PANEL_NAME = "footerPanelDialogChat";

	private static final String HEADER_PANEL_NAME = "headerPanelDialogChat";

	private JPanel headerPanel;

	private ChatTextArea chatTextArea;

	private JPanel footerPanel;

	public ChatDialogPanel() {
		super();
		initialize();
	}

	private void initialize() {
		chatTextArea = new ChatTextArea();
		chatTextArea.setSize(SCROLLPANE_DEFAULT_SIZE);
		chatTextArea.setPreferredSize(SCROLLPANE_DEFAULT_SIZE);
		chatTextArea.setMinimumSize(SCROLLPANE_MINIMUM_SIZE);

		this.setLayout(new GridBagLayout());
		this.setSize(DIALOG_PANEL_DEFAULT_SIZE);
		this.setPreferredSize(DIALOG_PANEL_DEFAULT_SIZE);
		this.setMinimumSize(DIALOG_PANEL_MINIMUM_SIZE);

		GridBagConstraints headerPanelConstraints = new GridBagConstraints();
		headerPanelConstraints.gridx = 0;
		headerPanelConstraints.gridy = 0;
		headerPanelConstraints.weightx = 1.0;
		headerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		headerPanelConstraints.insets = INSETS;

		GridBagConstraints scrollPaneConstraints = new GridBagConstraints();
		scrollPaneConstraints.gridx = 0;
		scrollPaneConstraints.gridy = 1;
		scrollPaneConstraints.weightx = 1.0;
		scrollPaneConstraints.weighty = 1.0;
		scrollPaneConstraints.fill = GridBagConstraints.BOTH;
		scrollPaneConstraints.insets = INSETS;

		GridBagConstraints footerPanelConstraints = new GridBagConstraints();
		footerPanelConstraints.gridx = 0;
		footerPanelConstraints.gridy = 2;
		footerPanelConstraints.weightx = 1.0;
		footerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		footerPanelConstraints.insets = FOOTER_PANEL_INSETS;

		this.add(getHeaderPanel(), headerPanelConstraints);
		this.add(chatTextArea, scrollPaneConstraints);
		this.add(getFooterPanel(), footerPanelConstraints);
	}

	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setSize(FOOTER_PANEL_DEFAULT_SIZE);
			footerPanel.setPreferredSize(FOOTER_PANEL_DEFAULT_SIZE);
			footerPanel.setMinimumSize(FOOTER_PANEL_MINIMUM_SIZE);
			footerPanel.setName(FOOTER_PANEL_NAME);
		}
		return footerPanel;
	}

	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new JPanel();
			headerPanel.setSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);
			headerPanel.setMinimumSize(HEADER_PANEL_MINIMUM_SIZE2);
			headerPanel.setName(HEADER_PANEL_NAME);
		}
		return headerPanel;
	}

	public void addMessage(ChatMessage message) {
		chatTextArea.addMessage(message);
	}


	public void setup(ContactInfo contact) {
		chatTextArea.addColor(contact.getChatId(), ChatTextArea.LOCAL_USER_COLOR);
	}

	public void setDragAndDrops(MultiLayerDropTargetListener dndListener, DropListener listener) {
		Component editorPane = chatTextArea.getEditorPane();
		editorPane.setDropTarget(null);
		dndListener.addDropListener(editorPane, listener);		
	}
}
