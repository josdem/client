package com.all.client.view.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.all.browser.AllBrowser;
import com.all.commons.Environment;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.observ.ObserverCollection;

public class WizardContentPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Dimension BOTTOM_PANEL_DEFAULT_SIZE = new Dimension(692, 34);

	private static final Dimension CENTER_PANEL_DEFAULT_SIZE = new Dimension(692, 290);

	private static final Rectangle MAC_BOUNDS = new Rectangle(20, 54, 692, 486);

	private static final Rectangle WIN_BOUNDS = new Rectangle(14, 48, 692, 486);

	private JPanel bottomPanel;

	private WizardTopPanel topPanel;

	private WizardBrowserPanel wizardBrowserPanel;

	private Observable<ObserveObject> onItunesButtonEvent = new Observable<ObserveObject>();

	private Observable<ObservValue<Boolean>> onFacebookCheckBoxEvent = new Observable<ObservValue<Boolean>>();

	private Observable<ObservValue<Boolean>> onMCCheckBoxEvent = new Observable<ObservValue<Boolean>>();

	private AllBrowser demoBrowser;

	public WizardContentPanel(AllBrowser demoBrowser, Messages messages) {
		this.demoBrowser = demoBrowser;
		initialize();
		demoBrowser.loadUrl(messages.getMessage("home.wizard.url"));
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		if (Environment.isWindows()) {
			this.setBounds(WIN_BOUNDS);
		} else {
			this.setBounds(MAC_BOUNDS);
		}
		this.add(getTopPanel(), BorderLayout.NORTH);
		this.add(getWizardBrowserPanel(), BorderLayout.CENTER);
		this.add(getBottomPanel(), BorderLayout.SOUTH);
		setListeners();
	}

	private void setListeners() {
		topPanel.onItunesButton().add(new Observer<ObserveObject>() {
			@Override
			public void observe(ObserveObject args) {
				onItunesButtonEvent.fire(args);
			}
		});
		topPanel.onFacebookCheckBox().add(new Observer<ObservValue<Boolean>>() {
			@Override
			public void observe(ObservValue<Boolean> args) {
				onFacebookCheckBoxEvent.fire(args);
			}
		});
		topPanel.onMCCheckBox().add(new Observer<ObservValue<Boolean>>() {
			@Override
			public void observe(ObservValue<Boolean> args) {
				onMCCheckBoxEvent.fire(args);
			}
		});
	}

	private JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setLayout(null);
			bottomPanel.setPreferredSize(BOTTOM_PANEL_DEFAULT_SIZE);
		}
		return bottomPanel;
	}

	private WizardBrowserPanel getWizardBrowserPanel() {
		if (wizardBrowserPanel == null) {
			wizardBrowserPanel = new WizardBrowserPanel(demoBrowser);
			wizardBrowserPanel.setPreferredSize(CENTER_PANEL_DEFAULT_SIZE);
		}
		return wizardBrowserPanel;
	}

	WizardTopPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new WizardTopPanel();
		}
		return topPanel;
	}

	public void internationalize(Messages messages) {
		topPanel.internationalize(messages);
	}

	public ObserverCollection<ObserveObject> onItunesButton() {
		return onItunesButtonEvent;
	}

	public ObserverCollection<ObservValue<Boolean>> onFacebookCheckBox() {
		return onFacebookCheckBoxEvent;
	}

	public ObserverCollection<ObservValue<Boolean>> onMCCheckBox() {
		return onMCCheckBoxEvent;
	}

	public void setBrowser(AllBrowser demoBrowser) {
		this.demoBrowser = demoBrowser;
	}
}
