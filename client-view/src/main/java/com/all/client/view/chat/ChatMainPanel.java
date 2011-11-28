package com.all.client.view.chat;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.core.actions.Actions;
import com.all.core.actions.ContactMessage;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ChatMessage;
import com.all.shared.model.ContactInfo;

public final class ChatMainPanel extends JPanel implements Internationalizable {
	private static final long serialVersionUID = 1L;

	private static final Dimension LEFT_MESSAGE_PANEL_DEFAULT_SIZE = new Dimension(347, 88);

	private static final Dimension LEFT_MESSAGE_PANEL_MINIMUM_SIZE = new Dimension(250, 88);

	private static final Dimension MESSAGE_PANEL_DEFAULT_SIZE = new Dimension(316, 88);

	private static final Dimension MESSAGE_PANEL_MINIMUM_SIZE = LEFT_MESSAGE_PANEL_MINIMUM_SIZE;

	private static final Dimension RIGHT_MESSAGE_PANEL_DEFAULT_SIZE = new Dimension(49, 88);

	private static final Dimension SEND_BUTTON_DEFAULT_SIZE = new Dimension(28, 82);
	
	private static final Dimension SEND_SCROLL_DEFAULT_SIZE = new Dimension(310, 80);

	private static final Insets MESSAGE_PANEL_INSETS = new Insets(0, 2, 0, 2);

	private static final Insets SEND_BUTTON_INSETS = new Insets(0, 0, 0, 3);
	
	private static final Insets TEXT_AREA_INSETS = new Insets(15, 10, 15, 10);

	private static final Rectangle EMOTICON_BUTTON_BOUNDS = new Rectangle(7, 46, 34, 34);

	private static final String EMOTICON_BUTTON_NAME = "emoticonChatButton";

	private static final String LEFT_MESSAGE_PANEL_NAME = "leftMessageChatPanel";

	private static final String MESSAGE_PANEL_NAME = "messagePanelChat";

	private static final String RIGHT_MESSAGE_PANEL_NAME = "rightMessageChatPanel";

	private static final String SEND_BUTTON_NAME = "sendMessageChatButton";

	private static final String CHAT_TEXT_AREA_NAME = "chatTextArea";

	private ChatDialogPanel dialogPanel;

	private JPanel messagePanel;

	private JButton sendButton;

	private JTextArea sendTextArea;

	private JPanel leftMessagePanel;

	private JPanel rightMessagePanel;

	private JButton emotIconButton;

	JFrame parent;

	private JScrollPane textAreaScrollPane;

	ActionListener emoticonAction;
	private ViewEngine viewEngine;
	
	private final Log log = LogFactory.getLog(this.getClass());

	public ChatMainPanel() {

	}

	public ChatMainPanel(JFrame parent, ViewEngine viewEngine) {
		this.parent = parent;
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints dialogPanelConstraints = new GridBagConstraints();
		dialogPanelConstraints.gridx = 0;
		dialogPanelConstraints.gridy = 1;
		dialogPanelConstraints.weightx = 1.0;
		dialogPanelConstraints.weighty = 1.0;
		dialogPanelConstraints.fill = GridBagConstraints.BOTH;
		this.add(getDialogPanel(), dialogPanelConstraints);

		GridBagConstraints messagePanelConstraints = new GridBagConstraints();
		messagePanelConstraints.gridx = 0;
		messagePanelConstraints.gridy = 2;
		messagePanelConstraints.weightx = 1;
		messagePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		messagePanelConstraints.insets = MESSAGE_PANEL_INSETS;
		this.add(getMessagePanel(), messagePanelConstraints);
	}

	
	public void setup(final ContactInfo contact) {
		getSendButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(contact);
			}
		});

		getTextArea().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage(contact);
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					Window windowAncestor = SwingUtilities.getWindowAncestor(e.getComponent());
					windowAncestor.dispatchEvent(new WindowEvent(windowAncestor, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		getDialogPanel().setup(contact);
	}

	public void requestFocusOnTextArea() {
		getTextArea().requestFocus();
	}

	private void sendMessage(ContactInfo contact) {
		String msg = getTextArea().getText().trim();
		getTextArea().setText("");
		if ("".equals(msg)) {
			return;
		}
		ContactMessage contactMessage = new ContactMessage(contact,msg);
		
		viewEngine.request(Actions.Chat.SEND_MESSAGE_TO_CONTACT, contactMessage, new ResponseCallback<ChatMessage>() {
			@Override
			public void onResponse(ChatMessage response) {
				log.debug("ON RESPONSE");
				addMessage(response);
			}
		});
	}

	private JPanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new JPanel();
			messagePanel.setLayout(new GridBagLayout());
			messagePanel.setSize(MESSAGE_PANEL_DEFAULT_SIZE);
			messagePanel.setPreferredSize(MESSAGE_PANEL_DEFAULT_SIZE);
			messagePanel.setMinimumSize(MESSAGE_PANEL_MINIMUM_SIZE);
			messagePanel.setName(MESSAGE_PANEL_NAME);

			GridBagConstraints leftMessagePanelConstraints = new GridBagConstraints();
			leftMessagePanelConstraints.gridx = 0;
			leftMessagePanelConstraints.gridy = 0;
			leftMessagePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
			leftMessagePanelConstraints.weightx = 1;
			messagePanel.add(getLeftMessagePanel(), leftMessagePanelConstraints);

			GridBagConstraints rightMessagePanelConstraints = new GridBagConstraints();
			rightMessagePanelConstraints.gridx = 1;
			rightMessagePanelConstraints.gridy = 0;
			rightMessagePanelConstraints.fill = GridBagConstraints.NONE;
			messagePanel.add(getRightMessagePanel(), rightMessagePanelConstraints);

		}
		return messagePanel;
	}

	private JPanel getRightMessagePanel() {
		if (rightMessagePanel == null) {
			rightMessagePanel = new JPanel();
			rightMessagePanel.setLayout(null);
			rightMessagePanel.setName(RIGHT_MESSAGE_PANEL_NAME);
			rightMessagePanel.setSize(RIGHT_MESSAGE_PANEL_DEFAULT_SIZE);
			rightMessagePanel.setPreferredSize(RIGHT_MESSAGE_PANEL_DEFAULT_SIZE);
			rightMessagePanel.setMinimumSize(RIGHT_MESSAGE_PANEL_DEFAULT_SIZE);
			rightMessagePanel.add(getEmotIconButton());
		}
		return rightMessagePanel;
	}

	JButton getEmotIconButton() {
		if (emotIconButton == null) {
			emotIconButton = new JButton();
			emotIconButton.setBounds(EMOTICON_BUTTON_BOUNDS);
			emotIconButton.setName(EMOTICON_BUTTON_NAME);
			emoticonAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new EmoticonsAllDialog(parent, getTextArea());
				}
			};
			emotIconButton.addActionListener(emoticonAction);
		}
		return emotIconButton;
	}

	private JPanel getLeftMessagePanel() {
		if (leftMessagePanel == null) {
			leftMessagePanel = new JPanel();
			leftMessagePanel.setName(LEFT_MESSAGE_PANEL_NAME);
			leftMessagePanel.setSize(LEFT_MESSAGE_PANEL_DEFAULT_SIZE);
			leftMessagePanel.setPreferredSize(LEFT_MESSAGE_PANEL_DEFAULT_SIZE);
			leftMessagePanel.setMinimumSize(LEFT_MESSAGE_PANEL_MINIMUM_SIZE);
			leftMessagePanel.setLayout(new GridBagLayout());
			
			GridBagConstraints textAreaConstraints = new GridBagConstraints();
			textAreaConstraints.gridx = 0;
			textAreaConstraints.gridy = 0;
			textAreaConstraints.weightx = 1.0;
			textAreaConstraints.fill = GridBagConstraints.BOTH;
			textAreaConstraints.insets = TEXT_AREA_INSETS;

			leftMessagePanel.add(getTextAreaScrollPane(), textAreaConstraints);

			GridBagConstraints sendButtonConstraints = new GridBagConstraints();
			sendButtonConstraints.gridx = 1;
			sendButtonConstraints.gridy = 0;
			sendButtonConstraints.insets = SEND_BUTTON_INSETS;
			leftMessagePanel.add(getSendButton(), sendButtonConstraints);
		}
		return leftMessagePanel;
	}
	
	private JScrollPane getTextAreaScrollPane() {
		if (textAreaScrollPane == null) {
			textAreaScrollPane = new JScrollPane();
			textAreaScrollPane.setPreferredSize(SEND_SCROLL_DEFAULT_SIZE);
			textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			textAreaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			textAreaScrollPane.setName(CHAT_TEXT_AREA_NAME);
			textAreaScrollPane.setViewportView(getTextArea());
		}
		return textAreaScrollPane;
	}

	JTextArea getTextArea() {
		if (sendTextArea == null) {
			sendTextArea = new JTextArea();
			sendTextArea.setLineWrap(true);
			sendTextArea.setWrapStyleWord(true);
			sendTextArea.setName(CHAT_TEXT_AREA_NAME);
			sendTextArea.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);

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
					replace(fb, offset, length, "", null);
				}

				public void replace(DocumentFilter.FilterBypass fb, int offset, int selectedLength, String text,
						AttributeSet attrs) throws BadLocationException {
					text = text.replaceAll("\\n", " ");

					Document doc = fb.getDocument();
					int currentLength = doc.getLength();
					if (currentLength + text.length() - selectedLength < 300) {
						fb.replace(offset, selectedLength, text, attrs);
					} else {
						text = text.substring(0, 300 - currentLength + selectedLength);
						fb.replace(offset, selectedLength, text, attrs);
					}
				}
			};
			((AbstractDocument) (sendTextArea.getDocument())).setDocumentFilter(filter);
			sendTextArea.addKeyListener(new CopyPasteKeyAdapterForMac());
		}
		return sendTextArea;
	}

	private JButton getSendButton() {
		if (sendButton == null) {
			sendButton = new JButton();
			sendButton.setName(SEND_BUTTON_NAME);
			sendButton.setPreferredSize(SEND_BUTTON_DEFAULT_SIZE);
			sendButton.setMaximumSize(SEND_BUTTON_DEFAULT_SIZE);
			sendButton.setMinimumSize(SEND_BUTTON_DEFAULT_SIZE);
			sendButton.setSize(SEND_BUTTON_DEFAULT_SIZE);
		}
		return sendButton;
	}

	private ChatDialogPanel getDialogPanel() {
		if (dialogPanel == null) {
			dialogPanel = new ChatDialogPanel();
		}
		return dialogPanel;
	}

	public void addMessage(ChatMessage message) {
		dialogPanel.addMessage(message);
	}

	@Override
	public void internationalize(Messages messages) {
		emotIconButton.setToolTipText(messages.getMessage("chatMainPanel.showEmoticon.tooltip"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public void setDragAndDrops(MultiLayerDropTargetListener dndListener, DropListener listener) {
		getTextArea().setDropTarget(null);
		dndListener.addDropListener(getMessagePanel(), new DropListener() {
			@Override
			public void doDrop(DraggedObject draggedObject, Point location) {
			}

			@Override
			public boolean validateDrop(DraggedObject draggedObject, Point location) {
				return false;
			}

			@Override
			public Class<?>[] handledTypes() {
				return null;
			}
		});
		dialogPanel.setDragAndDrops(dndListener, listener);
	}

}
