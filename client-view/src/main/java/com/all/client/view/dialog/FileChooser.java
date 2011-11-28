package com.all.client.view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sun.swing.FilePane;

import com.all.client.view.file.FileChooserStrategy;
import com.all.commons.Environment;
import com.all.i18n.Messages;

@SuppressWarnings("restriction")
public class FileChooser extends AllDialog {

	private static final long serialVersionUID = 4349090271106045322L;

	private static final int BOTTOM = 10;

	private static final int LEFT = 10;

	private static final int RIGHT = 10;
	
	private static final int TOP = 30;

	private static final Dimension BUTTON_DEFAULT_SIZE = new Dimension(80, 22);

	private static final String DIRECTORY_COMBO_BOX_NAME = "directoryComboBox";

	private static final String DIRECTORY_MAC_COMBO_BOX_NAME = "directoryMacComboBox";

	private static final String IMPORT_FILE_CHOOSER_BUTTON_NAME = "importFileChooserButton";

	private JButton findButton;
	
	private JFileChooser fileChooser;

	private final FileChooserStrategy fileChooserStrategy;

	private final String titleMessage;

	public FileChooser(JFrame frame, Messages messages, FileChooserStrategy fileChooserStrategy, String titleMessage) {
		super(frame, messages);
		this.fileChooserStrategy = fileChooserStrategy;
		this.titleMessage = titleMessage;
		initializeContentPane();
		setVisible(true);
	}

	@Override
	JComponent getContentComponent() {
		return getFileChooser();
	}

	@Override
	public String dialogTitle(Messages messages) {
		return messages.getMessage(titleMessage);
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setBorder(BorderFactory.createEmptyBorder(TOP, LEFT, BOTTOM, RIGHT));
			fileChooserStrategy.configureFileChooser(fileChooser);

			JPanel filePane = getFilePane();
			JPanel firstPanel = getFirstPanel();
			JComboBox directoryCombo = getDirectoryComboBox(firstPanel);
			directoryCombo.setName(DIRECTORY_COMBO_BOX_NAME);

			JPanel lastPanel = getLastPanel();
			findButton = getFindButton(lastPanel);
			findButton.setEnabled(false);
			fileChooserStrategy.configureFileChooserButton(fileChooser, findButton, getMessages());

			fileChooserStrategy.addActionListener(findButton, fileChooser, this);

			configureFileChooserButton(findButton);
			JButton cancelButton = getCancelButton(lastPanel);
			cancelButton.setText(getMessages().getMessage("cancel"));
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
			
			configureFileChooserButton(cancelButton);
			
			disablePanelTypeOfFile(filePane);
			
			if (Environment.isMac()) {
				directoryCombo.setName(DIRECTORY_MAC_COMBO_BOX_NAME);
			} else {
				// we have to repaint the scroll pane so that it is grafically
				// shown
				// correctly - STRANGE bug -- seems to occurr only in windows,
				// thanks
				// Bill
				final JScrollPane scrollPane = getScrollPane();
				scrollPane.getViewport().addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						scrollPane.repaint();
					}

				});
			}
		}
		
		return fileChooser;
	}

	private void configureFileChooserButton(JButton button) {
		button.setName(IMPORT_FILE_CHOOSER_BUTTON_NAME);
		button.setToolTipText(null);
		button.setPreferredSize(BUTTON_DEFAULT_SIZE);
	}

	private JScrollPane getScrollPane() {
		for (Component comp : fileChooser.getComponents()) {
			if (comp instanceof FilePane) {
				for (Component compSon : ((FilePane) comp).getComponents()) {
					if (compSon instanceof JPanel) {
						for (Component compGrandSon : ((JPanel) compSon).getComponents()) {
							if (compGrandSon instanceof JScrollPane) {
								return (JScrollPane) compGrandSon;
							}
						}
					}
				}
			}
		}

		return null;
	}

	private JButton getFindButton(JPanel lastPanel) {
		return (JButton) (getActionButtonsPanel(lastPanel)).getComponent(1);
	}

	private JButton getCancelButton(JPanel lastPanel) {
		return (JButton) (getActionButtonsPanel(lastPanel)).getComponent(0);
	}

	private JPanel getActionButtonsPanel(JPanel lastPanel) {
		return (JPanel) lastPanel.getComponent(3);
	}

	private JPanel getLastPanel() {
		return ((JPanel) fileChooser.getComponent(3));
	}

	private JPanel getFirstPanel() {
		return ((JPanel) fileChooser.getComponent(0));
	}

	private JComboBox getDirectoryComboBox(JPanel firstPanel) {
		return ((JComboBox) firstPanel.getComponent(2));
	}
	
	private void disablePanelTypeOfFile(JPanel filePanel) {
		((JPanel) filePanel.getComponent(2)).setVisible(false);
	}
	
	private JPanel getFilePane() {
		return getLastPanel();
	}

	public boolean isApproved() {
		return fileChooserStrategy.isAccepted();
	}

	public File[] getSelectedTracks() {
		return fileChooserStrategy.getSelectedFiles();
	}
	
	public File getSelectedFile() {
		return fileChooserStrategy.getSelectedFile();
	}

}
