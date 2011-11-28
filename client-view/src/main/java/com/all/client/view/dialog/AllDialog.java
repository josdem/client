package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;

/**
 * This class decorates the dialog borders avoiding code duplication in every
 * dialog</br> </br> <b>Note:</b> Call {@code initializeContentPane()} in the
 * contructor of every subclass but before setting the dialog visible
 */
public abstract class AllDialog extends JDialog implements Internationalizable {

	private static final long serialVersionUID = -5011830128400452555L;
	
	private Log log = LogFactory.getLog(this.getClass());

	protected static final int TITLE_HEIGHT = 24;
	
	protected static final int BORDER = 5;

	private static final Dimension OK_BUTTON_SIZE = new Dimension(80, 22);

	private static final Dimension EXIT_BUTTON_SIZE = new Dimension(15, 15);
	
	private static final Dimension TITLE_PANEL_DEFAULT_SIZE = new Dimension(560, 24);

	private static final Insets EXIT_BUTTON_INSETS = new Insets(2, 0, 0, 0);
	
	private static final Insets TITLE_LABEL_INSETS = new Insets(1, 0, 0, 0);

	private static final FlowLayout TOP_BOTTOM_PANEL_LAYOUT = new FlowLayout(FlowLayout.CENTER, 0, 5);

	protected static final Insets CONTENT_PANEL_INSETS = new Insets(0, 5, 4, 5);

	protected static final Insets TITLE_PANEL_INSETS = new Insets(0, 5, 0, 5);

	private static final String OK_BUTTON_NAME = "buttonOk";
	
	private static final String EXIT_BUTTON_NAME = "closeDialogButton";

	private static final String ROOT_PANE_NAME = "RootPane";

	private JButton exitButton;

	private JButton okButton;
	
	private JLabel titleLabel;

	private JPanel buttonPanel;

	private JPanel titlePanel;

	private Messages messages;

	private Observable<ObserveObject> onCloseEvent = new Observable<ObserveObject>();

	/**
	 * Obtains the panel with the components for the dialog
	 * 
	 * @return JComponent dialog contents
	 */
	abstract JComponent getContentComponent();

	/**
	 * Set the title of the dialog
	 * 
	 * @param titleLabel
	 */
	abstract String dialogTitle(Messages messages);

	abstract void internationalizeDialog(Messages messages);

	public AllDialog(Frame frame, Messages messages) {
		super(frame, null, false);
		this.messages = messages;
		initialize();
	}

	public AllDialog(Dialog dialog, Messages messages) {
		super(dialog, null, false);
		this.messages = messages;
		initialize();
	}

	private void initialize() {
		setUndecorated(true);
		setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		getRootPane().setName(ROOT_PANE_NAME);
		addComponentListener(new ComponentResizedListener());
		// Shouldn't set visible here, set it in the classes that extends this
	}

	/**
	 * Call this method from the classes which extends this, after initialize
	 * all their components and before setting it visible.
	 */
	protected void initializeContentPane() {
		// setting the layout of the dialog
		GridBagConstraints titlePanelConstraints = new GridBagConstraints();
		titlePanelConstraints.gridx = 0;
		titlePanelConstraints.gridy = 0;
		titlePanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		titlePanelConstraints.weightx = 1;
		titlePanelConstraints.insets = TITLE_PANEL_INSETS;
		GridBagConstraints contentPanelConstraints = new GridBagConstraints();
		contentPanelConstraints.gridx = 0;
		contentPanelConstraints.gridy = 1;
		contentPanelConstraints.fill = GridBagConstraints.BOTH;
		contentPanelConstraints.weightx = 1;
		contentPanelConstraints.weighty = 1;
		contentPanelConstraints.insets = CONTENT_PANEL_INSETS;

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(getTitlePanel(), titlePanelConstraints);
		getContentPane().add(getContentComponent(), contentPanelConstraints);

		// setting the size according to the content component
		Dimension contentSize = getContentComponent().getPreferredSize();
		int w = (int) contentSize.getWidth() + BORDER * 2;
		int h = (int) contentSize.getHeight() + BORDER + TITLE_HEIGHT;
		changeSize(w, h);
		setLocationRelativeTo(getParent());
		pack();
		setMessages(getMessages());
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				removeMessages(getMessages());
			}
		});
	}

	protected void changeSize(int width, int height) {
		Dimension preferredSize = new Dimension(width, height);
		this.setPreferredSize(preferredSize);
		this.setMinimumSize(preferredSize);
		this.setMaximumSize(preferredSize);
		this.setSize(preferredSize);
		this.validate();
	}

	protected JPanel getTitlePanel() {
		if (titlePanel == null) {
			GridBagConstraints titleLabelConstraints = new GridBagConstraints();
			titleLabelConstraints.gridx = 0;
			titleLabelConstraints.gridy = 0;
			titleLabelConstraints.insets = TITLE_LABEL_INSETS;
			titleLabelConstraints.weightx = 1;
			titleLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints exitButtonConstraints = new GridBagConstraints();
			exitButtonConstraints.gridx = 1;
			exitButtonConstraints.gridy = 0;
			exitButtonConstraints.insets = EXIT_BUTTON_INSETS;
			exitButtonConstraints.anchor = GridBagConstraints.CENTER;

			titlePanel = new JPanel();
			titlePanel.setLayout(new GridBagLayout());
			titlePanel.setPreferredSize(TITLE_PANEL_DEFAULT_SIZE);
			titlePanel.setMinimumSize(TITLE_PANEL_DEFAULT_SIZE);
			titlePanel.setMaximumSize(TITLE_PANEL_DEFAULT_SIZE);
			titlePanel.add(getTitleLabel(), titleLabelConstraints);
			titlePanel.add(getExitButton(), exitButtonConstraints);
			WindowDraggerMouseListener draggerMouseListener = new WindowDraggerMouseListener();
			draggerMouseListener.setup(titlePanel);
		}
		return titlePanel;
	}

	protected JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setName(SynthFonts.BOLD_FONT11_GRAY77_77_77);
		}
		return titleLabel;
	}

	protected JButton getExitButton() {
		if (exitButton == null) {
			exitButton = new JButton();
			exitButton.setName(EXIT_BUTTON_NAME);
			exitButton.setPreferredSize(EXIT_BUTTON_SIZE);
			exitButton.setMinimumSize(EXIT_BUTTON_SIZE);
			exitButton.setMaximumSize(EXIT_BUTTON_SIZE);
			exitButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			exitButton.addActionListener(new CloseListener());
		}
		return exitButton;
	}

	protected JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(getSeparator(), BorderLayout.NORTH);
		buttonPanel.add(getOkButton(), BorderLayout.CENTER);
		buttonPanel.add(getSpacer(), BorderLayout.SOUTH);
		return buttonPanel;
	}

	private Component getSpacer() {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(getContentComponent().getWidth(), 4));
		return spacer;
	}

	private JPanel getOkButton() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.add(getOkButtonComp());
		}
		return buttonPanel;
	}

	private JButton getOkButtonComp() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setName(OK_BUTTON_NAME);
			okButton.setPreferredSize(OK_BUTTON_SIZE);
			okButton.addActionListener(new CloseListener());
		}
		return okButton;
	}

	private JPanel getSeparator() {
		JPanel topBottomPanel = new JPanel(TOP_BOTTOM_PANEL_LAYOUT);
		JPanel separatorPanel = new JPanel();

		int width = getContentComponent().getPreferredSize().width - 20;
		log.debug("W: " + width);
		separatorPanel.setPreferredSize(new Dimension(width, 2));
		separatorPanel.setName("bottomPanelSeparator");
		topBottomPanel.add(separatorPanel);
		return topBottomPanel;
	}

	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new CloseListener(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
		return rootPane;
	}

	protected void closeDialog() {
		Window window = AllDialog.this;
		window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	}

	class CloseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			onCloseEvent.fire(ObserveObject.EMPTY);
			closeDialog();
		}

	}

	static class ComponentResizedListener extends ComponentAdapter {
		@Override
		public void componentResized(ComponentEvent e) {
			Dialog dialog = (Dialog) e.getComponent();
			Area shape = new Area(new RoundRectangle2D.Double(0, 0, dialog.getWidth(), dialog.getHeight(), 15, 15));
			shape.add(new Area(new Rectangle2D.Float(0, 0, dialog.getWidth(), TITLE_HEIGHT)));
			TransparencyManagerFactory.getManager().setWindowShape(dialog, shape);
		}
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(dialogTitle(messages));
		getOkButtonComp().setText(messages.getMessage("ok"));
		internationalizeDialog(messages);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	public ObserverCollection<ObserveObject> onClose() {
		return onCloseEvent;
	}

	public Messages getMessages() {
		return messages;
	}

}
