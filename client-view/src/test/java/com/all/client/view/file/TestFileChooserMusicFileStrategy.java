package com.all.client.view.file;

import static org.junit.Assert.assertArrayEquals;
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

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.view.util.AudioFilter;
import com.all.i18n.Messages;


public class TestFileChooserMusicFileStrategy extends UnitTestCase {
	
	private FileChooserStrategy fileStrat = new FileChooserMusicFileStrategy();
	@Mock
	private JFileChooser fileChooser;
	@Mock
	private JButton button;
	
	@Test
	public void shouldConfigureFileChooser() throws Exception {
		fileStrat.configureFileChooser(fileChooser);
		
		verify(fileChooser).addChoosableFileFilter(any(AudioFilter.class));
		verify(fileChooser).setAcceptAllFileFilterUsed(false);
		verify(fileChooser).setMultiSelectionEnabled(true);
	}
	
	@Test
	public void shouldAddEnableButtonListener() throws Exception {
		Messages messages = mock(Messages.class);
		fileStrat.configureFileChooserButton(fileChooser, button, messages);
		
		ArgumentCaptor<PropertyChangeListener> argCaptor = ArgumentCaptor.forClass(PropertyChangeListener.class);
		verify(fileChooser).addPropertyChangeListener(argCaptor.capture());
		
		PropertyChangeListener newPropertyChangeListener = argCaptor.getValue();
		PropertyChangeEvent evt = new PropertyChangeEvent(fileChooser, JFileChooser.SELECTED_FILES_CHANGED_PROPERTY, 23, 34);
		newPropertyChangeListener.propertyChange(evt);
		verify(button).setEnabled(true);
		
		evt = new PropertyChangeEvent(fileChooser, "otherProperty", 23, 34);
		newPropertyChangeListener.propertyChange(evt);
		verify(button).setEnabled(false);
		
		verify(messages).getMessage("importTrack.AcceptButton.Text");
	}
	
	@Test
	public void shouldAddActionListener() throws Exception {
		File file = mock(File.class);
		File[] selectedFiles = new File[]{file};
		JDialog dialog = mock(JDialog.class);
		when(fileChooser.getSelectedFiles()).thenReturn(selectedFiles);
		fileStrat.addActionListener(button, fileChooser, dialog);
		ArgumentCaptor<ActionListener> argCaptor = ArgumentCaptor.forClass(ActionListener.class);
		verify(button).addActionListener(argCaptor.capture());
		
		ActionListener actionListener = argCaptor.getValue();
		ActionEvent ae = new ActionEvent(fileChooser, 2, "CancelSelection");
		actionListener.actionPerformed(ae);
		verify(dialog).setVisible(false);
		verify(dialog).dispose();
		assertTrue(fileStrat.isAccepted());
		assertArrayEquals(selectedFiles, fileStrat.getSelectedFiles());
	}
}
