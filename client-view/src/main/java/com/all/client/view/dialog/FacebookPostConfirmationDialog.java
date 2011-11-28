package com.all.client.view.dialog;

import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.view.components.ScrollableTextPane;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;

public class FacebookPostConfirmationDialog extends AllDialog {

	private static final Rectangle CHAR_COUNTER_LABEL_BOUNDS = new Rectangle(304, 165, 60, 30);

	private static final long serialVersionUID = 1L;

	private static final Log LOG = LogFactory.getLog(FacebookPostConfirmationDialog.class);

	private static final int MAX_NUMBER_OF_CHARS = 420;

	private static final Rectangle POST_CONFIRMATION_PANEL_BOUNDS = new Rectangle(0, 0, 388, 196);

	private static final Rectangle CONTENT_PANEL_BOUNDS = new Rectangle(0, 0, 388, 237);

	private static final Rectangle POST_BUTTON_BOUNDS = new Rectangle(200, 204, 80, 22);

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(109, 204, 80, 22);

	private static final Rectangle WARNING_LABEL_BOUNDS = new Rectangle(24, 50, 370, 28);

	private static final Rectangle SCROLLPANE_BOUNDS = new Rectangle(24, 78, 340, 87);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 196, 379, 2);

	private static final String BACKGROUND_PANEL_NAME = "facebookLoginBackgroundPanel";

	private static final String SEPARATOR_PANEL_NAME = "bottomPanelSeparator";

	private static final String BUTTON_NAME = "buttonOk";

	private ScrollableTextPane messageScrollPane;

	private JPanel postConfirmationPanel;

	private JPanel contentPanel;

	private JPanel separatorPanel;

	private JButton cancelButton;

	private JButton postButton;

	private JLabel warningLabel;

	private String more;

	private String textAreaMiddle;

	private String textAreaTitle;

	private String textAreaLast;

	private String textAreaLink;

	private AbstractDocument doc;

	private JLabel charCounterLabel;

	private boolean posted = false;

	public FacebookPostConfirmationDialog(Frame frame, Messages messages, ModelCollection model) {
		super(frame, messages);
		initializeContentPane();
		internationalizeDialog(messages);
		addData(model);
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(CONTENT_PANEL_BOUNDS);
			contentPanel.add(getBackgroundPanel());
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCancelButton());
			contentPanel.add(getPostButton());
		}
		return contentPanel;
	}

	private JLabel getCharCounterLabel() {
		if (charCounterLabel == null) {
			charCounterLabel = new JLabel();
			charCounterLabel.setBounds(CHAR_COUNTER_LABEL_BOUNDS);
			charCounterLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			charCounterLabel.setName(SynthFonts.BOLD_FONT20_GRAY51_51_51);
		}
		return charCounterLabel;
	}

	private JPanel getBackgroundPanel() {
		if (postConfirmationPanel == null) {
			postConfirmationPanel = new JPanel();
			postConfirmationPanel.setLayout(null);
			postConfirmationPanel.setName(BACKGROUND_PANEL_NAME);
			postConfirmationPanel.setBounds(POST_CONFIRMATION_PANEL_BOUNDS);
			postConfirmationPanel.add(getWarningLabel());
			postConfirmationPanel.add(getMessagePane());
			postConfirmationPanel.add(getCharCounterLabel());
		}
		return postConfirmationPanel;
	}

	private JLabel getWarningLabel() {
		if (warningLabel == null) {
			warningLabel = new JLabel();
			warningLabel.setBounds(WARNING_LABEL_BOUNDS);
		}
		return warningLabel;
	}

	private ScrollableTextPane getMessagePane() {
		if (messageScrollPane == null) {
			messageScrollPane = new ScrollableTextPane(SCROLLPANE_BOUNDS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			messageScrollPane.getTextPane().getDocument().addDocumentListener(new FacebookDocumentListener());
		}
		return messageScrollPane;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
		}
		return separatorPanel;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.setName(BUTTON_NAME);
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	private JButton getPostButton() {
		if (postButton == null) {
			postButton = new JButton();
			postButton.setBounds(POST_BUTTON_BOUNDS);
			postButton.setName(BUTTON_NAME);
			postButton.setEnabled(false);
			postButton.addActionListener(new CloseListener());
			postButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					posted = true;
				}
			});
		}
		return postButton;
	}

	private void addData(ModelCollection model) {
		try {
			if (model != null) {
				String content = new String();
				String newline = "\n";
				SimpleAttributeSet fontStyle = new SimpleAttributeSet();
				StyleConstants.setFontFamily(fontStyle, "Dialog");
				StyleConstants.setFontSize(fontStyle, 13);
				StyledDocument styledDoc = getMessagePane().getTextPane().getStyledDocument();
				doc = (AbstractDocument) styledDoc;
				doc.insertString(doc.getLength(), textAreaTitle + newline, fontStyle);
				StyleConstants.setBold(fontStyle, true);

				int numberMedia = 0;

				for (Track track : model.getTracks()) {
					if (numberMedia < 3) {
						content = '"' + track.getName() + '"';
						doc.insertString(doc.getLength(), content + newline, fontStyle);
						numberMedia++;
					} else {
						break;
					}
				}
				for (Folder folder : model.getFolders()) {
					if (numberMedia < 3) {
						content = '"' + folder.getName() + '"';
						doc.insertString(doc.getLength(), content + newline, fontStyle);
						numberMedia++;
					} else {
						break;
					}
				}
				for (Playlist playlist : model.getPlaylists()) {
					if (numberMedia < 3) {
						content = '"' + playlist.getName() + '"';
						doc.insertString(doc.getLength(), content + newline, fontStyle);
						numberMedia++;
					} else {
						break;
					}
				}

				StyleConstants.setBold(fontStyle, false);

				int totalMedia = model.getTracks().size() + model.getPlaylists().size() + model.getFolders().size();
				if (totalMedia > 3) {
					doc.insertString(doc.getLength(), more + newline, fontStyle);
				}

				doc.insertString(doc.getLength(), newline + textAreaMiddle, fontStyle);

				HTMLEditorKit kit = (HTMLEditorKit) getMessagePane().getTextPane().getEditorKit();
				StringReader reader = new StringReader(textAreaLink);
				kit.read(reader, doc, doc.getLength());

				doc.insertString(doc.getLength(), newline + textAreaLast + newline, fontStyle);
			}

		} catch (BadLocationException e) {
			LOG.error("Couldn't insert initial text.", e);
		} catch (IOException e) {
			LOG.error("Couldn't insert initial text.", e);
		}

	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("facebook.confirmationpost.dialog.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
		cancelButton.setText(messages.getMessage("facebook.dialog.button.cancel"));
		postButton.setText(messages.getMessage("facebook.confirmationpost.post"));
		warningLabel.setText(messages.getMessage("facebook.confirmationpost.warningLabel"));
		textAreaTitle = messages.getMessage("facebook.confirmationpost.textArea.title");
		more = messages.getMessage("facebook.confirmationpost.textArea.more");
		textAreaMiddle = messages.getMessage("facebook.confirmationpost.textArea.middle");
		textAreaLink = messages.getMessage("facebook.confirmationpost.textArea.link");
		textAreaLast = messages.getMessage("facebook.confirmationpost.textArea.last");
	}

	private final class FacebookDocumentListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			int length = e.getDocument().getLength();
			validateMessageLength(length);
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			int length = e.getDocument().getLength();
			validateMessageLength(length);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			int length = e.getDocument().getLength();
			validateMessageLength(length);
		}
	}

	private void validateMessageLength(int length) {
		int remainingChars = getRemainingChars(length);
		charCounterLabel.setText(Integer.toString(remainingChars));
		if (remainingChars >= 0 && remainingChars != MAX_NUMBER_OF_CHARS) {
			postButton.setEnabled(true);
			messageScrollPane.setError(false);
			charCounterLabel.setName(SynthFonts.BOLD_FONT20_GRAY51_51_51);
		} else {
			postButton.setEnabled(false);
			messageScrollPane.setError(true);
			charCounterLabel.setName(SynthFonts.BOLD_FONT20_RED);
		}
	}

	private int getRemainingChars(int length) {
		int value = MAX_NUMBER_OF_CHARS - length;
		return value;
	}

	public String getResult() {
		String resut = null;
		try {
			if (posted) {
				resut = doc.getText(0, doc.getLength());
			}
		} catch (BadLocationException e) {
			LOG.error("Unable to obtain the the post message", e);
		}
		return resut;
	}
}
