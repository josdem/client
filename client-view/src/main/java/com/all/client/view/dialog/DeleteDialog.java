package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;

import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.i18n.Messages;

public class DeleteDialog extends AllDialog {

	public enum TYPE {
		Folder(
				"deleteFolderButton",
				"deleteFolderFilesButton",
				"popup.deleteFolder.delete",
				"popup.deleteFolder.deleteFiles",
				"popup.tooltip.deleteFolder.delete",
				"popup.tooltip.deleteFolder.deleteFiles"),
		Playlist(
				"deletePlaylistButton",
				"deletePlaylistFilesButton",
				"popup.deletePlaylist.delete",
				"popup.deletePlaylist.deleteFiles",
				"popup.tooltip.deletePlaylist.delete",
				"popup.tooltip.deletePlaylist.deleteFiles"),
		Both(
				"deleteFolderPlaylistButton",
				"deleteFolderPlaylistFilesButton",
				"popup.deleteMixed.delete",
				"popup.deleteMixed.deleteFiles",
				"popup.tooltip.deleteMixed.delete",
				"popup.tooltip.deleteMixed.deleteFiles"),
		Tracks(
				"deleteOnlyReference",
				"deleteFile",
				"deleteTrackOption.reference",
				"deleteTrackOption.all",
				"deleteTrackOption.reference",
				"deleteTrackOption.all");

				private String getDeleteSynthStyle() {
					return deleteSynthStyle;
				}

				private String getDeleteFilesSynthStyle() {
					return deleteFilesSynthStyle;
				}

				private String getDeleteButtonText() {
					return deleteButtonText;
				}

				private String getDeleteButtonTooltip() {
					return deleteButtonTooltip;
				}

				private String getDeleteFilesButtonText() {
					return deleteFilesButtonText;
				}

				private String getDeleteFilesButtonTooltip() {
					return deleteFilesButtonTooltip;
				}

		private final String deleteSynthStyle;
		private final String deleteFilesSynthStyle;
		private final String deleteButtonText;
		private final String deleteButtonTooltip;
		private final String deleteFilesButtonText;
		private final String deleteFilesButtonTooltip;

		private TYPE(String deleteSynthStyle, String deleteFilesSynthStyle, String deleteButtonText,
				String deleteFilesButtonText, String deleteButtonTooltip, String deleteFilesButtonTooltip) {
			this.deleteSynthStyle = deleteSynthStyle;
			this.deleteFilesSynthStyle = deleteFilesSynthStyle;
			this.deleteButtonText = deleteButtonText;
			this.deleteButtonTooltip = deleteButtonTooltip;
			this.deleteFilesButtonText = deleteFilesButtonText;
			this.deleteFilesButtonTooltip = deleteFilesButtonTooltip;
		}
	}

	private static final long serialVersionUID = 1L;
	public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
	public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;
	public static final int DELETE = 101;
	public static final int DELETE_AND_FILES = 102;
	private static final Dimension CONTENT_PANEL_SIZE = new Dimension(265, 185);
	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(95, 150, 80, 22);
	private static final Rectangle SEPARATOR_BOUNDS = new Rectangle(5, 135, 255, 2);
	private static final Dimension DELETE_PLAYLIST_BUTTON_SIZE = new Dimension(244, 40);
	private static final Point DELETE_PLAYLIST_BUTTON_LOCATION = new Point(12, 34);
	private static final Point ERROR_MESSAGE_LABEL_LOCATION = new Point(10, 5);
	private static final Dimension ERROR_MESSAGE_LABEL_SIZE = new Dimension(265, 18);
	private static final Point ZERO_LOCATION = new Point(0, 0);
	private static final Dimension DIALOG_PANEL_SIZE = new Dimension(265, 180);
	private static final Dimension DEFAULT_SIZE = new Dimension(275, 215);
	private JPanel dialogPanel;
	private JButton cancelButton;
	private JPanel contentPanel;
	private JLabel errorMessageLabel;
	private JButton deletePlaylistButton;
	private JButton deletePlaylistFilesButton;
	private TYPE type;

	private String title;
	private int returnValue;

	/**
	 * @param owner
	 */
	public DeleteDialog(JFrame frame, TYPE type, Messages messages) {
		super(frame, messages);
		this.type = type;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setSize(DEFAULT_SIZE);
		getContentPanel().add(getDialogPanel(), BorderLayout.CENTER);
		WindowDraggerMouseListener draggerMouseListener = new WindowDraggerMouseListener();
		draggerMouseListener.setup(getTitlePanel());
		initializeContentPane();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage(type.getDeleteButtonText());
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JPanel getDialogPanel() {
		if (dialogPanel == null) {
			dialogPanel = new JPanel();
			dialogPanel.setLayout(null);
			dialogPanel.setSize(DIALOG_PANEL_SIZE);
			dialogPanel.setLocation(ZERO_LOCATION);

			errorMessageLabel = new JLabel();
			errorMessageLabel.setText(getMessages().getMessage("deleteTrackOption.label"));
			errorMessageLabel.setName(SynthFonts.PLAIN_FONT11_DARK_GRAY);
			errorMessageLabel.setSize(ERROR_MESSAGE_LABEL_SIZE);
			errorMessageLabel.setLocation(ERROR_MESSAGE_LABEL_LOCATION);
			dialogPanel.add(errorMessageLabel);

			dialogPanel.add(getDeletePlaylistButton());
			dialogPanel.add(getDeletePlaylistFilesButton());
			dialogPanel.add(getSeparator());
			dialogPanel.add(getCancelButton());
		}
		return dialogPanel;
	}

	private JButton getDeletePlaylistButton() {
		if (deletePlaylistButton == null) {
			deletePlaylistButton = new JButton();
			deletePlaylistButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			deletePlaylistButton.setPreferredSize(DELETE_PLAYLIST_BUTTON_SIZE);
			deletePlaylistButton.setLocation(DELETE_PLAYLIST_BUTTON_LOCATION);
			deletePlaylistButton.setSize(DELETE_PLAYLIST_BUTTON_SIZE);
			deletePlaylistButton.setName(type.getDeleteSynthStyle());
			deletePlaylistButton.setText(getMessages().getMessage(type.deleteButtonText));
			deletePlaylistButton.setToolTipText(getMessages().getMessage(type.getDeleteButtonTooltip()));
		}
		return deletePlaylistButton;
	}

	private JButton getDeletePlaylistFilesButton() {
		if (deletePlaylistFilesButton == null) {
			deletePlaylistFilesButton = new JButton();
			deletePlaylistFilesButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			deletePlaylistFilesButton.setText(getMessages().getMessage(type.getDeleteFilesButtonText()));
			deletePlaylistFilesButton.setName(type.getDeleteFilesSynthStyle());
			deletePlaylistFilesButton.setLocation(new Point(12, 84));
			deletePlaylistFilesButton.setSize(DELETE_PLAYLIST_BUTTON_SIZE);
			deletePlaylistFilesButton.setPreferredSize(DELETE_PLAYLIST_BUTTON_SIZE);
			deletePlaylistFilesButton.setToolTipText(getMessages().getMessage(type.getDeleteFilesButtonTooltip()));
		}
		return deletePlaylistFilesButton;
	}

	private JSeparator getSeparator() {
		JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
		separator.setBounds(SEPARATOR_BOUNDS);
		return separator;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.setName("gray");
			cancelButton.setText(getMessages().getMessage("cancel"));
			getCancelButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					returnValue = CANCEL_OPTION;
					setVisible(false);
				}
			});
		}
		return cancelButton;
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setSize(CONTENT_PANEL_SIZE);
			contentPanel.setLocation(ZERO_LOCATION);
		}
		return contentPanel;
	}

	/**
	 * Pops a custom directory chooser dialog Example
	 * 
	 * <pre>
	 * JFolderChooser chooser = new JFolderChooser(null);
	 * chooser.showDialog(parentFrame, null);
	 * </pre>
	 * 
	 * <p>
	 * 
	 * The <code>parent</code> argument determines two things: the frame on which
	 * the open dialog depends and the component whose position the look and feel
	 * should consider when placing the dialog. If the parent is a
	 * <code>Frame</code> object (such as a <code>JFrame</code>) then the dialog
	 * depends on the frame and the look and feel positions the dialog relative to
	 * the frame (for example, centered over the frame). If the parent is a
	 * component, then the dialog depends on the frame containing the component,
	 * and is positioned relative to the component (for example, centered over the
	 * component). If the parent is <code>null</code>, then the dialog depends on
	 * no visible window, and it's placed in a look-and-feel-dependent position
	 * such as the center of the screen.
	 * 
	 * @param parent
	 *          the parent component of the dialog; can be <code>null</code>
	 * @param approveButtonText
	 *          the text of the <code>ApproveButton</code>
	 * @return the return state of the file chooser on popdown:
	 *         <ul>
	 *         <li>CANCEL_OPTION <li>APPROVE_OPTION <li>ERROR_OPTION if an error
	 *         occurs or the dialog is dismissed
	 *         </ul>
	 * @exception HeadlessException
	 *              if GraphicsEnvironment.isHeadless() returns true.
	 * @see java.awt.GraphicsEnvironment#isHeadless
	 */
	public int showDialog() {
		i18n();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				returnValue = CANCEL_OPTION;
			}
		});
		deletePlaylistButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = DELETE;
				setVisible(false);
			}
		});
		deletePlaylistFilesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				returnValue = DELETE_AND_FILES;
				setVisible(false);
			}
		});
		returnValue = ERROR_OPTION;
		deletePlaylistButton.requestFocus();
		deletePlaylistButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					returnValue = CANCEL_OPTION;
					setVisible(false);
				}
			}
		});
		setVisible(true);
		dispose();
		return returnValue;
	}

	private void i18n() {
		Locale loc = Locale.getDefault();
		if (title == null) {
			title = UIManager.getString("FileChooser.openDialogTitleText", loc);
		}
		cancelButton.setText(getMessages().getMessage("cancel"));
	}

	// ***************************
	// Not generated by VE end
	// ****************************

	public static DeleteDialog getDialog(JFrame frame, boolean hasPlaylist, boolean hasFolder, boolean hasTracks,
			Messages messages) {
		if (hasTracks) {
			return new DeleteDialog(frame, TYPE.Tracks, messages);
		} else {
			if (hasPlaylist && hasFolder) {
				return new DeleteDialog(frame, TYPE.Both, messages);
			} else {
				if (hasPlaylist) {
					return new DeleteDialog(frame, TYPE.Playlist, messages);
				}
				if (hasFolder) {
					return new DeleteDialog(frame, TYPE.Folder, messages);
				}
			}
		}
		return null;
	}
}