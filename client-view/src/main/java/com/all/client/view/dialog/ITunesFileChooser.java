package com.all.client.view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import sun.swing.FilePane;

import com.all.client.util.FileUtil;
import com.all.commons.Environment;
import com.all.i18n.Messages;

@SuppressWarnings("restriction")
public final class ITunesFileChooser extends AllDialog {

	private static final int RIGHT = 10;

	private static final int BOTTOM = 10;

	private static final int LEFT = 10;

	private static final int TOP = 30;

	private static final Dimension FILE_FILTER_COMBOBOX_SIZE = new Dimension(298, 22);

	private static final long serialVersionUID = 4349090271106045322L;

	private JFileChooser fileChooser;
	private File iTuneslibrary;
	private boolean approved;
	private JButton importButton;

	public ITunesFileChooser(JFrame frame, Messages messages) {
		super(frame, messages);
		initializeContentPane();
		setVisible(true);
	}

	@Override
	JComponent getContentComponent() {
		return getFileChooser();
	}

	@Override
	public String dialogTitle(Messages messages) {
		return messages.getMessage("importTrack.Title");
	}

	@Override
	void internationalizeDialog(Messages messages) {
	}

	private JFileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.addChoosableFileFilter(new FileFilter(){

				@Override
				public boolean accept(File f) {
					return FileUtil.getExtension(f).equals("XML") || f.isDirectory();
				}

				@Override
				public String getDescription() {
					return "iTunes Music Libraries (*.xml)";
				}});
			fileChooser.setBorder(BorderFactory.createEmptyBorder(TOP, LEFT, BOTTOM, RIGHT));
			fileChooser.setApproveButtonText(getMessages().getMessage("importTrack.AcceptButton.Text"));
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getNewValue() != null && e.getNewValue().toString().endsWith(".xml")) {
						importButton.setEnabled(true);
					} else {
						importButton.setEnabled(false);
					}
				}
			});

			JPanel filePane = getFilePane();
			JComboBox fileFilterComboBox = getFileFilterComboBox(filePane);
			fileFilterComboBox.setName("fileFilterComboBox");
			fileFilterComboBox.setSize(FILE_FILTER_COMBOBOX_SIZE);
			fileFilterComboBox.setPreferredSize(FILE_FILTER_COMBOBOX_SIZE);

			JPanel firstPanel = getFirstPanel();
			JComboBox directoryCombo = getDirectoryComboBox(firstPanel);
			directoryCombo.setName("directoryComboBox");

			JPanel lastPanel = getLastPanel();
			importButton = getImportButton(lastPanel);
			importButton.setEnabled(false);

			final JDialog dialog = this;
			fileChooser.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (e.getActionCommand().equals("CancelSelection")) {
						dialog.setVisible(false);
						dialog.dispose();
					}
					if (e.getActionCommand().equals("ApproveSelection")) {
						iTuneslibrary = fileChooser.getSelectedFile();
						approved = true;
						dialog.setVisible(false);
						dialog.dispose();
					}
				}
			});

			importButton.setName("importFileChooserButton");
			importButton.setToolTipText(null);
			getCancelButton(lastPanel).setToolTipText(null);
			if (Environment.isMac()) {
				fileFilterComboBox.setName("fileFilterMacComboBox");
				directoryCombo.setName("directoryMacComboBox");
			} else {
				// we have to repaint the scroll pane so that it is grafically shown
				// correctly - STRANGE bug -- seems to occurr only in windows, thanks
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

	private JButton getImportButton(JPanel lastPanel) {
		return (JButton) (getActionButtonsPanel(lastPanel)).getComponent(0);
	}

	private JButton getCancelButton(JPanel lastPanel) {
		return (JButton) (getActionButtonsPanel(lastPanel)).getComponent(1);
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

	private JComboBox getFileFilterComboBox(JPanel filePane) {
		return (JComboBox) (getFileChooserInferiorPanel(filePane)).getComponent(1);
	}

	private JPanel getFileChooserInferiorPanel(JPanel filePane) {
		return (JPanel) filePane.getComponent(2);
	}

	private JPanel getFilePane() {
		return getLastPanel();
	}

	public boolean isApproved() {
		return approved;
	}

	public File getSelectedLibrary() {
		return iTuneslibrary;
	}

}
