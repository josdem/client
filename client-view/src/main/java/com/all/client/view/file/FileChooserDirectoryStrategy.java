package com.all.client.view.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.view.util.AudioFilter;
import com.all.i18n.Messages;

public class FileChooserDirectoryStrategy implements FileChooserStrategy {
	
	private boolean approved;
	private File files;
	
	private Log log = LogFactory.getLog(this.getClass());

	public void configureFileChooser(JFileChooser fileChooser) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.addChoosableFileFilter(new AudioFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
	}

	public void configureFileChooserButton(final JFileChooser fileChooser, final JButton button, Messages message) {
		button.setText(message.getMessage("restoreTracks.acceptButton"));
		fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getNewValue() != null
						&& JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(e.getPropertyName())
						&& !e.getNewValue().equals(e.getOldValue())) {
					button.setEnabled(fileChooser.getSelectedFile().isDirectory());
				} else {
					button.setEnabled(false);
				}
			}
		});

	}

	public void addActionListener(JButton button, final JFileChooser fileChooser, final JDialog dialog) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				files = fileChooser.getSelectedFile();
				log.debug("file: " + files);
				approved = true;
				dialog.setVisible(false);
				dialog.dispose();
			}
		});

	}

	public boolean isAccepted() {
		return approved;
	}

	public File getSelectedFile() {
		log.debug("file: " + files);
		return files;
	}

	@Override
	public File[] getSelectedFiles() {
		return null;
	}

}
