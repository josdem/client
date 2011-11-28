package com.all.client.view.chat;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.core.common.view.transparency.TransparencyManagerFactory;

public abstract class EmoticonsDialog extends JDialog {

	private static final long serialVersionUID = 7384389529334811369L;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	protected static final String EXTENSION_34X34 = "_34x34";
	protected static final int MARGIN_GRID_HORIZONTAL = 16;
	protected static final int MARGIN_GRID_VERTICAL = 16;
	protected static final int EMOTICON_LABEL_SIZE = 34;
	protected static final int SIZE_GRAY_LINE = 1;
	
	private JPanel emoticonPanel;

	private final JTextArea sendArea;

	public EmoticonsDialog(JFrame parent, JTextArea sendArea) {
		super(parent, null, false, TransparencyManagerFactory.getManager().getTranslucencyCapableGC());
		this.sendArea = sendArea;
		
		this.setModal(false);
		this.setUndecorated(true);
		this.setVisible(true);
		this.requestFocus();
		this.getContentPane().add(getEmoticonPanel());
		this.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				Component oppositeComponent = e.getOppositeComponent();
				if (oppositeComponent instanceof EmoticonButton) {
					oppositeComponent.addNotify();
					((EmoticonButton) oppositeComponent).doClick();
				}
				close();
			}
		});
	}

	final protected void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		this.dispose();
	}
	
	protected JPanel getEmoticonPanel() {
		if (emoticonPanel == null) {
			emoticonPanel = new JPanel();
			emoticonPanel.setLayout(null);
			emoticonPanel.setFocusable(true);
			addEmoticonToGrid();
		}
		return emoticonPanel;
	}

	protected EmoticonButton createEmoticonButton(final String emoticonKey, final String strEmoticonPng, final String strEmoticonGif) {
		final EmoticonButton emoticonButton = new EmoticonButton();
		emoticonButton.setText("<html>" + strEmoticonPng + "</html>");
		emoticonButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder textToInsert = new StringBuilder();
				int caretPosition = sendArea.getCaretPosition();
				try {
					if (!sendArea.getText(caretPosition > 0 ? caretPosition - 1 : 0, 1).equals(" ")) {
						textToInsert.append(" ");
					}
				} catch (BadLocationException e1) {
					log.error("Getting error inserting emoticon", e1);
				}
				textToInsert.append(emoticonKey);
				textToInsert.append(" ");
				sendArea.insert(textToInsert.toString(), caretPosition);
				sendArea.requestFocus();
				close();
			}
		});

		emoticonButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				emoticonButton.setText("<html>" + strEmoticonPng + "</html>");
				emoticonButton.setToolTipText(emoticonKey);
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				emoticonButton.setText("<html>" + strEmoticonGif + "</html>");
				emoticonButton.setToolTipText(emoticonKey);
			}
		});

		return emoticonButton;
	}
	
	protected abstract void addEmoticonToGrid();

	public class EmoticonButton extends JButton {
		private static final long serialVersionUID = 1L;
	}
	
}
