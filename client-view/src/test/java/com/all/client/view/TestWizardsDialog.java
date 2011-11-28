package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.all.browser.AllBrowser;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.client.view.wizard.WizardDialog;
import com.all.commons.Environment;
import com.all.i18n.Messages;

public class TestWizardsDialog {
	@InjectMocks
	private WizardDialog wizardDialog;

	@Spy
	private LocalDescriptionPanel descriptionPanel = Mockito.mock(LocalDescriptionPanel.class);

	@Spy
	private AllBrowser demoBrowser = Mockito.mock(AllBrowser.class);

	@Spy
	private Messages messages = Mockito.mock(Messages.class);

	private JPanel contentPanel;

	private JFrame frame = new JFrame();

	private JPanel browserPanel = new JPanel();

	private static final Rectangle CLOSE_BUTTON_WIN_BOUNDS = new Rectangle(670, 6, 36, 36);

	private static final Rectangle CLOSE_BUTTON_MAC_BOUNDS = new Rectangle(676, 12, 36, 36);

	private static final Rectangle CONTENT_PANEL_WIN_BOUNDS_EXPECTED = new Rectangle(14, 48, 692, 486);

	private static final Rectangle CONTENT_PANEL_MAC_BOUNDS_EXPECTED = new Rectangle(20, 54, 692, 486);

	private static final Dimension WIZARD_DIALOG_WIN_DIMENSION = new Dimension(720, 540);

	private static final Dimension WIZARD_DIALOG_MAC_DIMENSION = new Dimension(738, 558);

	private static final String CLOSE_BUTTON_NAME_EXPECTED = "closeButtonWizard";

	private static final String WIZARD_WIN_PANEL_NAME = "wizardWinPanel";

	private static final String WIZARD_MAC_PANEL_NAME = "wizardMacPanel";

	@Before
	public void initialize() {
		when(demoBrowser.getPanel()).thenReturn(browserPanel);
		wizardDialog = new WizardDialog(frame, messages, descriptionPanel, demoBrowser);
		MockitoAnnotations.initMocks(this);
		contentPanel = (JPanel) wizardDialog.getContentPane();
	}

	@Test
	public void shouldVerifyComponentListeners() throws Exception {
		ComponentListener[] componentListeners = wizardDialog.getComponentListeners();
		assertEquals(1, componentListeners.length);
	}

	@Test
	public void shouldCreateWizardPanel() throws Exception {
		assertNotNull(contentPanel);
		assertTrue(contentPanel instanceof JPanel);
		assertNull(contentPanel.getLayout());
		if (Environment.isWindows()) {
			assertEquals(WIZARD_WIN_PANEL_NAME, contentPanel.getName());
			assertEquals(WIZARD_DIALOG_WIN_DIMENSION, wizardDialog.getSize());
		} else {
			assertEquals(WIZARD_MAC_PANEL_NAME, contentPanel.getName());
			assertEquals(WIZARD_DIALOG_MAC_DIMENSION, wizardDialog.getSize());
		}
	}

	@Test
	public void shouldHaveContentPanel() throws Exception {
		JPanel conteinerPanel = (JPanel) contentPanel.getComponent(0);
		assertNotNull(conteinerPanel);
		assertTrue(conteinerPanel instanceof JPanel);
		if (Environment.isWindows()) {
			assertEquals(CONTENT_PANEL_WIN_BOUNDS_EXPECTED, conteinerPanel.getBounds());
		} else {
			assertEquals(CONTENT_PANEL_MAC_BOUNDS_EXPECTED, conteinerPanel.getBounds());
		}
	}

	@Test
	public void shouldHaveCloseButton() throws Exception {
		JButton closeButton = (JButton) contentPanel.getComponent(1);
		assertNotNull(closeButton);
		assertTrue(closeButton instanceof JButton);
		if (Environment.isWindows()) {
			assertEquals(CLOSE_BUTTON_WIN_BOUNDS, closeButton.getBounds());
		} else {
			assertEquals(CLOSE_BUTTON_MAC_BOUNDS, closeButton.getBounds());
		}
		assertEquals(CLOSE_BUTTON_NAME_EXPECTED, closeButton.getName());
	}
}
