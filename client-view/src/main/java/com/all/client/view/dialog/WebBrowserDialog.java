package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.all.browser.AllBrowser;
import com.all.client.view.MainFrame;
import com.all.commons.Environment;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.i18n.Messages;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WebBrowserDialog extends AllDialog {

	private static final long serialVersionUID = 3669968619365360526L;

	private static final Log LOG = LogFactory.getLog(WebBrowserDialog.class);

	private static final Rectangle CONTENT_PANEL_BOUNDS = new Rectangle(0, 0, 559, 491);

	private static final Rectangle BROWSER_PANEL_BOUNDS = new Rectangle(1, 0, 557, 452);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(6, 452, 547, 2);

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(238, 459, 80, 22);

	private static final String SEPARATOR_PANEL_NAME = "bottomPanelSeparator";

	private static final String CANCEL_BUTTON_NAME = "buttonCancel";

	private JPanel contentPanel;

	private JPanel separatorPanel;

	private JButton cancelButton;

	private JPanel browserPanel;

	private boolean canceled = false;

	private AllBrowser webBrowser;

	private String displayUrl;

	private String closeUrl;

	private boolean loaded = false;

	private ScheduledExecutorService executor;
	
	private OSCommander osCommander;

	@Autowired
	public WebBrowserDialog(MainFrame mainFrame, Messages messages, AllBrowser webBrowser) {
		super(mainFrame, messages);
		this.webBrowser = webBrowser;
		
		if (Environment.isLinux()) {
			osCommander = new LinuxCommander();
		} else {
			osCommander = new WinMacCommander();
		}
		osCommander.setModalityType();

		initializeContentPane();
		internationalizeDialog(messages);

		if (!Environment.isWindows()) {
			TransparencyManagerFactory.getManager().setWindowOpaque(this, false);
		}

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				// first the component needs to be visible, then we can load a
				// url
				if (displayUrl != null && !loaded) {
					doLoad();
				}
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				stopMonitor();
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				stopMonitor();
			}
		});

		onClose().add(new Observer<ObserveObject>() {
			@Override
			public void observe(ObserveObject t) {
				canceled = true;
			}
		});
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("facebook.dialog.title");
	}

	@Override
	void internationalizeDialog(Messages messages) {
		cancelButton.setText(messages.getMessage("facebook.dialog.button.cancel"));
	}

	private JComponent getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(CONTENT_PANEL_BOUNDS);
			osCommander.addToContentPanel();
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCancelButton());
		}
		return contentPanel;
	}

	private JPanel getFacebookAuthorizationPanel() {
		if (browserPanel == null) {
			JPanel panel = webBrowser.getPanel();
			panel.setBounds(BROWSER_PANEL_BOUNDS);
			panel.setPreferredSize(new Dimension(559, 452));

			browserPanel = new JPanel();
			browserPanel.setLayout(new BorderLayout());
			browserPanel.setBounds(BROWSER_PANEL_BOUNDS);
			browserPanel.setPreferredSize(new Dimension(559, 452));
			browserPanel.add(panel, BorderLayout.CENTER);
		}
		return browserPanel;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setLayout(new GridBagLayout());
			separatorPanel.setBounds(SEPARATOR_PANEL_BOUNDS);
			separatorPanel.setName(SEPARATOR_PANEL_NAME);
		}
		return separatorPanel;
	}

	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(CANCEL_BUTTON_BOUNDS);
			cancelButton.setName(CANCEL_BUTTON_NAME);
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	public void load(String displayUrl, String closeUrl) {
		this.displayUrl = displayUrl;
		this.closeUrl = closeUrl;

		if (this.isVisible()) {
			doLoad();
		}
	}

	private void doLoad() {
		LOG.info("loading url: " + displayUrl);
		executor = Executors.newScheduledThreadPool(1, new IncrementalNamedThreadFactory("WebBrowserCloseThread"));
		osCommander.addToContentPanelOnLoad();
		webBrowser.loadUrl(displayUrl);

		if (StringUtils.isNotEmpty(closeUrl)) {
			executor.scheduleWithFixedDelay(new CloseMonitor(), 1, 1, TimeUnit.SECONDS);
			LOG.info("init close monitor for url: " + closeUrl);
		}
		loaded = true;
	}

	public String getResponseUrl() {
		String url = webBrowser.getUrl();
		if (Environment.isLinux()) {
			return url.equals("about:blank") ? null : url;
		}
		return canceled ? null : url;
	}

	private void stopMonitor() {
		executor.shutdownNow();
	}

	class CloseMonitor implements Runnable {
		@Override
		public void run() {
			if (loaded && webBrowser.getUrl() != null && webBrowser.getUrl().contains(closeUrl)) {
				LOG.info("Closing dialog, found close url: " + closeUrl);
				stopMonitor();
				closeDialog();
			}
		}
	};

	/**
	 * @Understands A class who define FB GUI Auth for Linux
	 */
	class LinuxCommander extends OSCommanderAdapter{
		@Override
		public void setModalityType() {
			WebBrowserDialog.this.setModalityType(ModalityType.MODELESS);
		}

		@Override
		public void addToContentPanelOnLoad() {
			contentPanel.add(getFacebookAuthorizationPanel());
		}
	}
	
	/**
	 * @Understands A class who define FB GUI Auth for Mac And Windows
	 */
	class WinMacCommander extends OSCommanderAdapter{
		@Override
		public void addToContentPanel() {
			contentPanel.add(getFacebookAuthorizationPanel());
		}
	}

}
