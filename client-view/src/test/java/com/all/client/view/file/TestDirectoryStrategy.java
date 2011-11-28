package com.all.client.view.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.view.util.AudioFilter;
import com.all.i18n.Messages;


public class TestDirectoryStrategy extends UnitTestCase {
	
	private FileChooserStrategy dirStrat = new FileChooserDirectoryStrategy();
	@Mock
	private JFileChooser fileChooser;
	@Mock
	private File selectedFile;
	@Mock
	private JButton button;
	
	@Before
	public void init() {
		when(fileChooser.getSelectedFile()).thenReturn(selectedFile);
	}
	
	@Test
	public void shouldConfigureFileChooser() throws Exception {
		dirStrat.configureFileChooser(fileChooser);
		
		verify(fileChooser).setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		verify(fileChooser).addChoosableFileFilter(any(AudioFilter.class));
		verify(fileChooser).setAcceptAllFileFilterUsed(false);
	}
	
	@Test
	public void shouldAddPropertyChangeListener() throws Exception {
		Messages messages = mock(Messages.class);
		when(selectedFile.isDirectory()).thenReturn(true);
		dirStrat.configureFileChooserButton(fileChooser, button, messages);
		
		ArgumentCaptor<PropertyChangeListener> argCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
		verify(fileChooser).addPropertyChangeListener(argCaptor.capture());
		
		PropertyChangeListener newPropertyChangeListener = argCaptor.getValue();
		PropertyChangeEvent evt = new PropertyChangeEvent(fileChooser, JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, 23, 34);
		newPropertyChangeListener.propertyChange(evt);
		
		verify(button).setEnabled(true);
		
		evt = new PropertyChangeEvent(fileChooser, "otherProperty", 23, 34);
		newPropertyChangeListener.propertyChange(evt);
		verify(button).setEnabled(false);
		verify(messages).getMessage("restoreTracks.acceptButton");
	}
	
	@Test
	public void shouldAddActionListener() throws Exception {
		JDialog dialog = mock(JDialog.class);
		dirStrat.addActionListener(button, fileChooser, dialog);
		ArgumentCaptor<ActionListener> argCaptor = ArgumentCaptor.forClass(ActionListener.class);
		verify(button).addActionListener(argCaptor.capture());
		
		ActionListener actionListener = argCaptor.getValue();
		ActionEvent ae = new ActionEvent(fileChooser, 2, "CancelSelection");
		actionListener.actionPerformed(ae);
		verify(dialog).setVisible(false);
		verify(dialog).dispose();
		assertTrue(dirStrat.isAccepted());
		assertEquals(selectedFile, dirStrat.getSelectedFile());
	}
}
