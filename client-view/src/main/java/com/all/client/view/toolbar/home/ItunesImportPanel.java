package com.all.client.view.toolbar.home;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.ImportItunesFlow;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.event.EmptyEvent;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class ItunesImportPanel extends JScrollPane implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension MAXIMUM_SIZE = new Dimension(497, 230);

	private static final Dimension MINIMUM_SIZE = new Dimension(397, 138);

	private static final Rectangle ITUNES_BUTTON_BOUNDS = new Rectangle(222, 36, 176, 68);

	private static final Rectangle ITUNES_TITLE_BOUNDS = new Rectangle(158, 0, 262, 36);

	private static final Rectangle ITUNES_INSTRUCTIONS_LABEL_BOUNDS = new Rectangle(158, 104, 262, 46);

	private static final String ITUNES_BUTTON_NAME = "importItunesButtonWizard";

	private static final String MAIN_PANEL_NAME = "itunesImportHomeBackgroundPanel";

	private JButton itunesButton;

	private JLabel itunesTitleLabel;

	private JTextPane itunesInstructionsLabel;

	private JPanel mainPanel;

	private ViewEngine viewEngine;

	private Listener<EmptyEvent> importLibraryItunesDoneListener;

	private final DialogFactory dialogFactory;

	public ItunesImportPanel(DialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
		initialize();
		createListeners();
	}

	private void createListeners() {
		importLibraryItunesDoneListener = new EventListener<EmptyEvent>() {
			@Override
			public void handleEvent(EmptyEvent eventArgs) {
				itunesButton.setEnabled(true);
			}
		};
	}

	private void initialize() {
		this.setMinimumSize(MINIMUM_SIZE);
		this.setPreferredSize(MINIMUM_SIZE);
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setViewportView(getMainPanel());
	}

	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(null);
			mainPanel.add(getItunesTitleLabel());
			mainPanel.add(getItunesButton());
			mainPanel.add(getItunesInstructionsLabel());
			mainPanel.setPreferredSize(MAXIMUM_SIZE);
			mainPanel.setName(MAIN_PANEL_NAME);
		}
		return mainPanel;
	}

	private JButton getItunesButton() {
		if (itunesButton == null) {
			itunesButton = new JButton();
			itunesButton.setBounds(ITUNES_BUTTON_BOUNDS);
			itunesButton.setName(ITUNES_BUTTON_NAME);
			itunesButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new ImportItunesFlow(viewEngine, dialogFactory).importItunesFromFile();
					itunesButton.setEnabled(false);
				}
			});
		}
		return itunesButton;
	}

	private JLabel getItunesTitleLabel() {
		if (itunesTitleLabel == null) {
			itunesTitleLabel = new JLabel();
			itunesTitleLabel.setBounds(ITUNES_TITLE_BOUNDS);
			itunesTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			itunesTitleLabel.setName(SynthFonts.BOLD_FONT16_PURPLE_70_40_90);
		}
		return itunesTitleLabel;
	}

	private JTextPane getItunesInstructionsLabel() {
		if (itunesInstructionsLabel == null) {
			itunesInstructionsLabel = new JTextPane();
			itunesInstructionsLabel.setBounds(ITUNES_INSTRUCTIONS_LABEL_BOUNDS);
			itunesInstructionsLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE70_40_90);
			itunesInstructionsLabel.setEditable(false);
			itunesInstructionsLabel.setEnabled(false);
			StyledDocument doc = itunesInstructionsLabel.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}
		return itunesInstructionsLabel;
	}

	@Override
	public void internationalize(Messages messages) {
		itunesTitleLabel.setText(messages.getMessage("wizard.itunes.label"));
		itunesInstructionsLabel.setText(messages.getMessage("home.itunes.instructions"));
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		viewEngine.addListener(Events.Library.IMPORTING_ITUNES_LIBRARY_DONE, importLibraryItunesDoneListener);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.Library.IMPORTING_ITUNES_LIBRARY_DONE, importLibraryItunesDoneListener);
	}
}