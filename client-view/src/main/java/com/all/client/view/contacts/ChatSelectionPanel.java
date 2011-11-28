package com.all.client.view.contacts;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.all.chat.ChatType;
import com.all.commons.SoundPlayer.Sound;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObservePropertyChanged;
import com.all.observ.ObservedProperty;
import com.all.observ.ObserverCollection;

public class ChatSelectionPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final String EMPTY_TITLE = "";

	private static final Dimension TITLE_CONTAINER_DEFAULT_SIZE = new Dimension(1, 25);

	public static final String COLLAPSABLE_PANEL_FLAT_STYLE = "chatSelectionPanel";

	private static final Dimension TITLE_CONTAINER_MINIMUM_SIZE = new Dimension(0, 25);

	private static final Dimension ARROW_BUTTON_DEFAULT_SIZE = new Dimension(8, 8);

	private static final Insets ARROW_INSETS = new Insets(2, 4, 2, 0);

	private static final Insets TITLE_INSETS = new Insets(1, 5, 1, 0);

	public static final String COLLAPSABLE_PANEL_COLLAPSED_BUTTON_STYLE = "collapsablePanelCollapsedButton";
	public static final String COLLAPSABLE_PANEL_EXPANDED_BUTTON_STYLE = "collapsablePanelExpandedButton";

	private String styleButtonCollapsed = COLLAPSABLE_PANEL_COLLAPSED_BUTTON_STYLE;
	private String styleButtonExpanded = COLLAPSABLE_PANEL_EXPANDED_BUTTON_STYLE;

	private JLabel titleLabel = null;

	private JPanel titleContainer = null;

	private JButton arrowButton = null;

	private JPanel contentContainer;

	ClosableChatAccountPanel panelFacebook;
	ChatAccountPanel panelALL;
	List<ChatAccountPanel> chatPanels = new ArrayList<ChatAccountPanel>();

	private boolean expanded = false;

	private ObservedProperty<ChatSelectionPanel, ChatType> selectedChat = new ObservedProperty<ChatSelectionPanel, ChatType>(this);

	public ChatSelectionPanel() {
		super();
		initialize();
		setup();
		click();
		panelALL.select();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());

		GridBagConstraints title = new GridBagConstraints();
		title.gridx = 0;
		title.gridy = 0;
		title.weightx = 1;
		title.weighty = 0;
		title.fill = GridBagConstraints.HORIZONTAL;
		title.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints content = new GridBagConstraints();
		content.gridx = 0;
		content.gridy = 1;
		content.weightx = 1;
		content.weighty = 0;
		content.fill = GridBagConstraints.HORIZONTAL;
		content.insets = new Insets(0, 0, 0, 0);

		GridBagConstraints bottom = new GridBagConstraints();
		bottom.gridx = 0;
		bottom.gridy = 2;
		bottom.weightx = 0;
		bottom.weighty = 1;
		bottom.fill = GridBagConstraints.HORIZONTAL;
		bottom.insets = new Insets(0, 0, 0, 0);

		super.add(getTitleContainer(), title);
		super.add(content(), bottom);

	}

	public JPanel content() {
		if (contentContainer == null) {
			contentContainer = new JPanel();
			contentContainer.setLayout(new BoxLayout(contentContainer, BoxLayout.Y_AXIS));
			addChatAccount();
		}
		return contentContainer;
	}

	private void addChatAccount() {
		panelALL = new ChatAccountPanel(ChatType.ALL, true, selectedChat);
		content().add(panelALL);
		chatPanels.add(panelALL);
		panelFacebook = new ClosableChatAccountPanel(ChatType.FACEBOOK, false, selectedChat);
		content().add(panelFacebook);
		chatPanels.add(panelFacebook);
		recalculate();
	}

	private void recalculate() {
		if (content().isVisible()) {
			content().invalidate();
			Window win = SwingUtilities.getWindowAncestor(content());
			if (win == null) {
				this.validate();
			} else {
				win.validate();
			}
		}
	}

	public JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setText(EMPTY_TITLE);
			titleLabel.setDisplayedMnemonic(KeyEvent.VK_UNDEFINED);
			titleLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			titleLabel.setName(SynthFonts.BOLD_FONT12_GRAY77_77_77);
			titleLabel.setText("ACCOUNTS");
		}
		return titleLabel;
	}

	public JPanel getTitleContainer() {
		if (titleContainer == null) {
			titleContainer = new JPanel();
			titleContainer.setLayout(new GridBagLayout());
			titleContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
			titleContainer.setMinimumSize(TITLE_CONTAINER_MINIMUM_SIZE);
			titleContainer.setPreferredSize(TITLE_CONTAINER_DEFAULT_SIZE);
			titleContainer.setSize(TITLE_CONTAINER_DEFAULT_SIZE);
			titleContainer.setName(COLLAPSABLE_PANEL_FLAT_STYLE);
			titleContainer.add(getTitleLabel(), getTitleContraints());
			titleContainer.add(getArrowButton(), getArrowConstraints());
		}
		return titleContainer;
	}

	public GridBagConstraints getArrowConstraints() {
		GridBagConstraints arrowConstraints = new GridBagConstraints();
		arrowConstraints.gridx = 0;
		arrowConstraints.gridy = 0;
		arrowConstraints.fill = GridBagConstraints.NONE;
		arrowConstraints.insets = ARROW_INSETS;
		return arrowConstraints;
	}

	public GridBagConstraints getTitleContraints() {
		GridBagConstraints titleConstraints = new GridBagConstraints();
		titleConstraints.gridx = 1;
		titleConstraints.gridy = 0;
		titleConstraints.ipadx = 0;
		titleConstraints.weightx = 1.0;
		titleConstraints.fill = GridBagConstraints.HORIZONTAL;
		titleConstraints.insets = TITLE_INSETS;
		return titleConstraints;
	}

	public JButton getArrowButton() {
		if (arrowButton == null) {
			arrowButton = new JButton();
			arrowButton.setMaximumSize(ARROW_BUTTON_DEFAULT_SIZE);
			arrowButton.setPreferredSize(ARROW_BUTTON_DEFAULT_SIZE);
			arrowButton.setMinimumSize(ARROW_BUTTON_DEFAULT_SIZE);
		}
		return arrowButton;
	}

	private void setup() {
		getTitleContainer().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				click();
			}
		});
		getArrowButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				click();
			}
		});
		expandOrCollapse();
	}

	private void click() {
		if (expanded) {
			collapse();
		} else {
			expand();
		}
	}

	public void collapse() {
		this.expanded = false;
		expandOrCollapse();
		Sound.CONTACT_FOLDER_CLOSE.play();
	}

	private void expandOrCollapse() {
		content().setVisible(expanded);
		arrowButton.setName(expanded ? styleButtonExpanded : styleButtonCollapsed);
		Sound.CONTACT_FOLDER_OPEN.play();
	}

	public void expand() {
		this.expanded = true;
		expandOrCollapse();
	}

	public ChatType getSelectedChatType() {
		for (Iterator<ChatAccountPanel> iterator = chatPanels.iterator(); iterator.hasNext();) {
			ChatAccountPanel chatPanel =  iterator.next();
            if(chatPanel.isSelected()){
            	return chatPanel.getChatType();
            }
		}
		return null;
	}

	@Override
	public void internationalize(Messages messages) {
		panelFacebook.internationalize(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		panelFacebook.removeMessages(messages);
	}

	@Override
	public void setMessages(Messages messages) {
		panelFacebook.setMessages(messages);
	}

	public ObserverCollection<ObservePropertyChanged<ChatSelectionPanel, ChatType>> onChatTypeSelected() {
		return selectedChat.on();
	}

	public void activateFacebook(boolean active) {
		panelFacebook.activateChatType(active);
	}

	public void selectChatType(ChatType chatType) {
		for (Iterator<ChatAccountPanel> iterator = chatPanels.iterator(); iterator.hasNext();) {
			ChatAccountPanel chatPanel = iterator.next();
			if(chatPanel.getChatType().equals(chatType)){
				chatPanel.select();
			}
		}
	}
}
