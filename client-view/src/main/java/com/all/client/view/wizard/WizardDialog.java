package com.all.client.view.wizard;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.all.browser.AllBrowser;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.commons.Environment;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.i18n.Messages;
import com.all.observ.ObserveObject;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;

public final class WizardDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Rectangle CLOSE_BUTTON_WIN_BOUNDS = new Rectangle(670, 6, 36, 36);

	private static final Rectangle TITLE_LABEL_WIN_BOUNDS = new Rectangle(22, 0, 250, 48);

	private static final Rectangle CLOSE_BUTTON_MAC_BOUNDS = new Rectangle(676, 12, 36, 36);

	private static final Rectangle TITLE_LABEL_MAC_BOUNDS = new Rectangle(28, 6, 250, 48);

	private static final String CLOSE_BUTTON_NAME = "closeButtonWizard";

	private static final String WIZARD_WIN_PANEL_NAME = "wizardWinPanel";

	private static final String WIZARD_MAC_PANEL_NAME = "wizardMacPanel";

	private WizardContentPanel contentPanel;

	private JButton closeButton;

	private JLabel titleLabel;

	private static final Dimension WIZARD_DIALOG_WIN_DIMENSION = new Dimension(720, 540);

	private static final Dimension WIZARD_DIALOG_MAC_DIMENSION = new Dimension(738, 558);

	private LocalDescriptionPanel localDescriptionPanel;

	private AllBrowser demoBrowser;

	private Observable<ObserveObject> onItunesButtonEvent = new Observable<ObserveObject>();

	private Observable<ObservValue<Boolean>> onFacebookCheckBoxEvent = new Observable<ObservValue<Boolean>>();

	private Observable<ObservValue<Boolean>> onMcEmailCheckBoxEvent = new Observable<ObservValue<Boolean>>();

	private boolean veryFristTime = true;

	private final Messages messages;

	public WizardDialog(Messages messages) {
		this.messages = messages;
	}

	public WizardDialog(JFrame frame, Messages messages, LocalDescriptionPanel localDescriptionPanel,
			AllBrowser demoBrowser) {
		super(frame, null, false, TransparencyManagerFactory.getManager().getTranslucencyCapableGC());
		this.messages = messages;
		this.localDescriptionPanel = localDescriptionPanel;
		this.demoBrowser = demoBrowser;
		initialize();
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if (!veryFristTime) {
					WizardDialog.this.demoBrowser.refresh();
				} else {
					veryFristTime = false;
				}
			}
		});
	}

	private void initialize() {
		this.setUndecorated(true);
		this.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
		this.getContentPane().setLayout(null);
		this.getContentPane().add(getContentPanel());
		this.getContentPane().add(getCloseButton());
		this.getContentPane().add(getTitleLabel());
		setListeners();
		if (Environment.isWindows()) {
			this.setSize(WIZARD_DIALOG_WIN_DIMENSION);
			this.getContentPane().setName(WIZARD_WIN_PANEL_NAME);
			getTitleLabel().setBounds(TITLE_LABEL_WIN_BOUNDS);
			getCloseButton().setBounds(CLOSE_BUTTON_WIN_BOUNDS);
		} else {
			this.setSize(WIZARD_DIALOG_MAC_DIMENSION);
			this.getContentPane().setName(WIZARD_MAC_PANEL_NAME);
			getTitleLabel().setBounds(TITLE_LABEL_MAC_BOUNDS);
			getCloseButton().setBounds(CLOSE_BUTTON_MAC_BOUNDS);
			TransparencyManagerFactory.getManager().setWindowOpaque(this, false);
		}
		this.setBounds(new Rectangle(200 + ((localDescriptionPanel.getWidth() / 2) - (this.getWidth() / 2)),
				82 + ((localDescriptionPanel.getHeight() / 2) - (this.getHeight() / 2)), this.getWidth(), this.getHeight()));
	}

	private void setListeners() {
		contentPanel.onItunesButton().add(new Observer<ObserveObject>() {
			@Override
			public void observe(ObserveObject args) {
				onItunesButtonEvent.fire(args);
			}
		});
		contentPanel.onFacebookCheckBox().add(new Observer<ObservValue<Boolean>>() {
			@Override
			public void observe(ObservValue<Boolean> args) {
				onFacebookCheckBoxEvent.fire(args);
			}
		});
		contentPanel.onMCCheckBox().add(new Observer<ObservValue<Boolean>>() {
			@Override
			public void observe(ObservValue<Boolean> args) {
				onMcEmailCheckBoxEvent.fire(args);
			}
		});
	}

	public void enableItunesButton() {
		getContentPanel().getTopPanel().getItunesButton().setEnabled(true);
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setName(SynthFonts.BOLD_FONT26_WHITE);
		}
		return titleLabel;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setName(CLOSE_BUTTON_NAME);
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					WizardDialog.this.demoBrowser.refresh();
					setVisible(false);
				}
			});
		}
		return closeButton;
	}

	private WizardContentPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new WizardContentPanel(demoBrowser, messages);
			contentPanel.getTopPanel().getFacebookCheckBox().setSelected(false);
			contentPanel.getTopPanel().getMCAlertCheckBox().setSelected(false);
		}
		return contentPanel;
	}

	public void internationalize(Messages messages) {
		titleLabel.setText(messages.getMessage("wizard.tittle"));
		contentPanel.internationalize(messages);
	}

	public ObserverCollection<ObserveObject> onItunesButton() {
		return onItunesButtonEvent;
	}

	public ObserverCollection<ObservValue<Boolean>> onFacebookCheckBox() {
		return onFacebookCheckBoxEvent;
	}

	public ObserverCollection<ObservValue<Boolean>> onMcEmailCheckBox() {
		return onMcEmailCheckBoxEvent;
	}
}
