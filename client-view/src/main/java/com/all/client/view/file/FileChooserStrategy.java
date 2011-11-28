package com.all.client.view.file;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import com.all.i18n.Messages;

public interface FileChooserStrategy {
	void configureFileChooser(JFileChooser fileChooser);

	void configureFileChooserButton(final JFileChooser fileChooser, final JButton button, Messages messages);

	void addActionListener(JButton button, final JFileChooser fileChooser, final JDialog dialog);

	boolean isAccepted();

	File getSelectedFile();

	File[] getSelectedFiles();

}