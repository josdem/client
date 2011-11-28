package com.all.client.view.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import com.all.client.view.util.AudioFilter;
import com.all.i18n.Messages;

public class FileChooserMusicFileStrategy implements FileChooserStrategy {

	protected File[] files;
	protected boolean approved;

	@Override
	public void addActionListener(JButton button, final JFileChooser fileChooser, final JDialog dialog) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				files = fileChooser.getSelectedFiles();
				approved = true;
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
	}

	@Override
	public void configureFileChooserButton(JFileChooser fileChooser, final JButton button, Messages messages) {
		button.setText(messages.getMessage("importTrack.AcceptButton.Text"));
		fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getNewValue() != null && JFileChooser.SELECTED_FILES_CHANGED_PROPERTY.equals(e.getPropertyName())
						&& !e.getNewValue().equals(e.getOldValue())) {
					button.setEnabled(true);
				} else {
					button.setEnabled(false);
				}
			}
		});

	}

	@Override
	public void configureFileChooser(JFileChooser fileChooser) {
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.addChoosableFileFilter(new AudioFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
	}

	@Override
	public File getSelectedFile() {
		return null;
	}

	@Override
	public boolean isAccepted() {
		return approved;
	}

	@Override
	public File[] getSelectedFiles() {
		return files;
	}

}
