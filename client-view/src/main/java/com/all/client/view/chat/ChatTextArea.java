package com.all.client.view.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.ChatConstants;
import com.all.core.common.view.util.CopyPasteKeyAdapterForMac;
import com.all.core.common.view.util.SelectedTextForeground;
import com.all.shared.model.ChatMessage;

public class ChatTextArea extends JPanel{
	private static final Log LOG = LogFactory.getLog(ChatTextArea.class);
	
	public static final String REMOTE_USER_COLOR = "E6E6E6";
	public static final String LOCAL_USER_COLOR = "E6D2E6";

	private static final String TABLE_OPEN = "<table width=\"100%\" border=0 cellpadding=0 cellspacing=0>";
	private static final String TABLE_CLOSE = "</table>";
	
	private static final String TR_CLOSE = "</tr>";
	private static final String TR_OPEN = "<tr>";
	private static final String TR_DEFAULT = "<tr  BGCOLOR=\"#";
	
	private static final String TD_CLOSE = "</td>";
	private static final String TD_TIME_USER_WRITER = "<td align=\"right\" width=\"46px\" height=\"15%\">";
	private static final String TD_WIDTH_RECEIVING_COINTENT = "<td valign=\"middle\" align=\"left\" width=\"46px\" height=\"15%\">";
	private static final String TD_USER_WRITER = "<td valign=\"middle\" align=\"left\">";
	private static final String TD_SPACE = "<td align=\"left\">";
	private static final String TD_TIME = "<td align=\"rigth\" width=\"46px\" height=\"8px\"   BGCOLOR=\"" ;
	private static final String TD_USER_RECEIVE_CONTENT = "<td valign=\"middle\" align=\"center\">";
	private static final String TD_MESSAGE = "<td valign=\"middle\" align=\"left\" width=\"100\" height=\"15%\">";
	
	private static final String MAJOR_TAG = "\">";
	private static final String BR = "<br/>";
	
	private static final String DIV_TAG = "<div align=\"right\">";
	private static final String DIV_CLOSE = "</div>";
	
	private static final String FONT_CLOSE = "</font>";
	private static final String FONT_OPEN_SIZE_11 = "<font style='color: #646464; font-size: 11pt '>";
	private static final String FONT_OPEN_SIZE_10 = "<font style='color: #646464; font-size: 10pt '>";
	private static final String FONT_OPEN_SIZE_13 = "<font style='color: #464646; font-size: 13pt '>";
	
	private static final long serialVersionUID = 1L;
	private ChatMessage lastMessage;
	private SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
	private Map<String, String> colors = new HashMap<String, String>();

	private JScrollPane scroll;
	private JEditorPane area;
	private boolean scrolled = false;

	
	StringBuilder text;
	private boolean wasReceivedAlert;
	private boolean wasSenderdAlert;
	
	@SuppressWarnings("serial")
	public ChatTextArea() {
		this.setLayout(new BorderLayout());
		area = new JEditorPane("text/html", "") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Paint general background
				g2.setColor(Color.WHITE);
				g2.fillRect(0, 0, getWidth(), getHeight());
				super.paintComponent(g);
			}
		};
		area.setMargin(new Insets(0, 10, 0, 10));
		area.setEditable(false);
		area.setSelectionColor(SelectedTextForeground.SELECTED_FOREGROUND_COLOR);
		area.addKeyListener(new CopyPasteKeyAdapterForMac());
		scroll = new JScrollPane(area);
		scroll.getViewport().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				int height = (int) area.getPreferredScrollableViewportSize().getHeight();
				JViewport viewPortSource = (JViewport) e.getSource();
				JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
				if (scrolled && !verticalScrollBar.getValueIsAdjusting() && verticalScrollBar.isVisible()) {
					int endHeight = height - viewPortSource.getHeight();
					if (viewPortSource.getViewPosition().y != endHeight) {
						viewPortSource.setViewPosition(new Point(0, endHeight));
					}
				}
			}
		});
		this.add(scroll, BorderLayout.CENTER);
	}

	public void addColor(String user, String color) {
		colors.put(user, color);
	}

	public void addMessage(ChatMessage message) {
		boolean isSendingContent = message.getMessage().equals(ChatConstants.SENT_CONTENT_TAG);
		boolean isReceivingContent = message.getMessage().equals(ChatConstants.RECEIVE_CONTENT_TAG);
		if (!isSendingContent && !isReceivingContent) {
			EmoticonHandler.processMessage(message);
		}
		text = new StringBuilder();
		String formattedTime = formatter.format(message.getTime());
		String color = colors.get(message.getSender().getEmail());
		color = color == null ? REMOTE_USER_COLOR : color;
		if (lastMessage == null || !lastMessage.getSender().equals(message.getSender()) || isSendingContent
				|| isReceivingContent) {
			text.append(BR);
			text.append(TABLE_OPEN);
			text.append(TR_DEFAULT);
			text.append(color);
			text.append(MAJOR_TAG);
			if (isReceivingContent) {
				text.append(TD_WIDTH_RECEIVING_COINTENT);
			} else {
				text.append(TD_USER_WRITER);
			}
			text.append(FONT_OPEN_SIZE_11);
			text.append(filter(message));
			text.append(FONT_CLOSE);
			text.append(TD_CLOSE);
			text.append(TD_TIME_USER_WRITER);
			text.append(FONT_OPEN_SIZE_10);
			text.append(formattedTime);
			text.append(FONT_CLOSE);
			text.append(TD_CLOSE);
			text.append(TR_CLOSE);
			text.append(TABLE_CLOSE);
		}
		else{
			if (!isReceivingContent && !isSendingContent && !wasReceivedAlert && !wasSenderdAlert) {
				text.append(TABLE_OPEN);
				text.append(TR_OPEN);
				text.append(TD_SPACE);
				text.append(TD_CLOSE);
				text.append(TD_TIME);
				text.append(color);
				text.append(MAJOR_TAG);
				text.append(DIV_TAG);
				text.append(FONT_OPEN_SIZE_10);
				text.append(formattedTime);
				text.append(DIV_CLOSE);
				text.append(FONT_CLOSE);
				text.append(TD_CLOSE);
				text.append(TR_CLOSE);
				text.append(TABLE_CLOSE);
			}
		}
		text.append(TABLE_OPEN);
		text.append(TR_OPEN);
		text.append(TD_MESSAGE);
		text.append(FONT_OPEN_SIZE_13);
		text.append(message.getMessage());
		text.append(FONT_CLOSE);
		text.append(TD_CLOSE);
		text.append(TR_CLOSE);
		
		text.append(TABLE_CLOSE);
		this.lastMessage = message;
		try {
			int length = area.getDocument().getLength();
			Reader r = new StringReader(text.toString());
			EditorKit kit = area.getEditorKit();
			kit.read(r, area.getDocument(), length);
		} catch (IOException e) {
			LOG.error(e, e);
		} catch (BadLocationException e) {
			LOG.error(e, e);
		}
		scrolled = true;
        wasReceivedAlert = isReceivingContent;
        wasSenderdAlert = isSendingContent;
	}

	private String filter(ChatMessage message) {
		if (ChatConstants.SENT_CONTENT_TAG.equals(message.getMessage())) {
			return "Your music content has been sent !";
		} else if (ChatConstants.RECEIVE_CONTENT_TAG.equals(message.getMessage())) {
			String senderName = message.getSender().getNickName();
			StringBuilder cm = new StringBuilder();
			cm.append(EmoticonHandler.getResourceAsImageTag("mail.gif"));
			cm.append(TD_CLOSE);
			cm.append(TD_USER_RECEIVE_CONTENT);
			cm.append(senderName + " has sent you music!");
			return cm.toString();
		} else {
			return message.getSender().getNickName();
		}
	}

	public Component getEditorPane() {
		return area;
	}
}
