package com.all.client.view.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.TransparentLoaderPanel;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.core.model.TwitterUser;
import com.all.i18n.Messages;

public final class TwitterRegistrationDialog extends AllDialog {

	private static final long serialVersionUID = 1L;

	private static final Rectangle CANCEL_BUTTON_BOUNDS = new Rectangle(109, 204, 80, 22);

	private static final Rectangle CARD_PANEL_BOUNDS = new Rectangle(0, 0, 388, 197);

	private static final Rectangle BACKGROUND_PANEL_BOUNDS = new Rectangle(0, 0, 388, 197);

	private static final Rectangle CONTENT_PANEL_BOUNDS = new Rectangle(0, 0, 388, 237);

	private static final Rectangle ERROR_ICON_LABEL_BOUNDS = new Rectangle(30, 130, 39, 35);

	private static final Dimension PREFERRED_SIZE_LOADER_SPACER = new Dimension(390, 51);

	private static final Rectangle MESSAGE_LABEL_BOUNDS = new Rectangle(85, 95, 300, 100);

	private static final Rectangle PASSWORD_FIELD_BOUNDS = new Rectangle(23, 134, 230, 22);

	private static final Rectangle REMEMBER_ME_CHECKBOX_BOUNDS = new Rectangle(34, 163, 226, 16);

	private static final Rectangle PASSWORD_LABEL_BOUNDS = new Rectangle(24, 110, 226, 18);

	private static final Rectangle SEND_BUTTON_BOUNDS = new Rectangle(200, 204, 80, 22);

	private static final Rectangle SEPARATOR_PANEL_BOUNDS = new Rectangle(5, 196, 379, 2);

	private static final Rectangle USER_LABEL_BOUNDS = new Rectangle(24, 50, 226, 18);

	private static final Rectangle USER_TEXT_FIELD_BOUNDS = new Rectangle(23, 80, 230, 22);

	private static final String BUTTON_NAME = "buttonOk";

	private static final String ERROR_BACKGROUND_PANEL_NAME = "twitterErrorBackgroundPanel";

	private static final String ERROR_ICON_LABEL_NAME = "icons.warningBigRed";

	private static final String ERROR_PANEL_NAME = "errorPanel";

	private static final String LOADER_PANEL_NAME = "loaderPanel";

	private static final String LOGIN_BACKGROUND_PANEL_NAME = "twitterLoginBackgroundPanel";

	private static final String LOADING_PANEL_NAME = "twitterUpdateStatusPanel";

	private static final String LOGIN_PANEL_NAME = "loginPanel";

	private static final String SEPARATOR_PANEL_NAME = "bottomPanelSeparator";

	private final Log log = LogFactory.getLog(this.getClass());

	private final ExecutorService loginExecutor = Executors
			.newSingleThreadScheduledExecutor(new IncrementalNamedThreadFactory("twitterLogginTask"));

	private JButton cancelButton;

	private JButton sendButton;

	private JLabel errorIconLabel;

	private JLabel messageLabel;

	private JLabel passwordLabel;

	private JLabel userLabel;

	private JPanel contentPanel;

	private JPanel loginPanel;

	private JPanel separatorPanel;

	private JPasswordField passwordField;

	private JTextField userTextField;

	private JPanel cardPanel;

	private JPanel errorPanel;

	private JPanel loginBackgroundPanel;

	private JPanel loginContainer;

	private JPanel errorContainer;

	private JPanel errorBackgroundPanel;

	private JButton retryButton;

	private TransparentLoaderPanel loaderPanel;

	private JCheckBox remeberMeCheckBox;

	private ViewEngine viewEngine;

	public TwitterRegistrationDialog(Frame frame, Messages messages, ViewEngine viewEngine) {
		super(frame, messages);
		this.viewEngine = viewEngine;
		init();
		initializeContentPane();
		internationalizeDialog(messages);
	}

	private void init() {
		setPasswordIfAutoLogin();
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				loginExecutor.shutdownNow();
			}
		});
	}

	@Override
	String dialogTitle(Messages messages) {
		return messages.getMessage("twitter.dialog.title");
	}

	@Override
	JComponent getContentComponent() {
		return getContentPanel();
	}

	@Override
	void internationalizeDialog(Messages messages) {
		sendButton.setText(messages.getMessage("twitter.dialog.button.login"));
		cancelButton.setText(messages.getMessage("twitter.dialog.button.cancel"));
		messageLabel.setText(messages.getMessage("twitter.dialog.error.message"));
		userLabel.setText(messages.getMessage("twitter.dialog.user"));
		passwordLabel.setText(messages.getMessage("twitter.dialog.password"));
		remeberMeCheckBox.setText(messages.getMessage("twitter.dialog.remember.me"));
	}

	private JPanel getLoginPanel() {
		if (loginPanel == null) {
			loginPanel = new JPanel();
			loginPanel.setLayout(null);
			loginPanel.setBounds(CONTENT_PANEL_BOUNDS);
			loginPanel.add(getLoginBackgroundPanel());
		}
		return loginPanel;
	}

	private JPanel getLoginBackgroundPanel() {
		if (loginBackgroundPanel == null) {
			loginBackgroundPanel = new JPanel();
			loginBackgroundPanel.setLayout(null);
			loginBackgroundPanel.setName(LOGIN_BACKGROUND_PANEL_NAME);
			loginBackgroundPanel.setBounds(BACKGROUND_PANEL_BOUNDS);
			loginBackgroundPanel.add(getUserLabel());
			loginBackgroundPanel.add(getUserTextField());
			loginBackgroundPanel.add(getPasswordLabel());
			loginBackgroundPanel.add(getPasswordField());
			loginBackgroundPanel.add(getRemeberMeCheckBox());
		}
		return loginBackgroundPanel;
	}

	private JCheckBox getRemeberMeCheckBox() {
		if (remeberMeCheckBox == null) {
			remeberMeCheckBox = new JCheckBox();
			remeberMeCheckBox.setBounds(REMEMBER_ME_CHECKBOX_BOUNDS);
		}
		return remeberMeCheckBox;
	}

	private JPanel getContentPanel() {
		if (contentPanel == null) {
			contentPanel = new JPanel();
			contentPanel.setLayout(null);
			contentPanel.setBounds(CONTENT_PANEL_BOUNDS);
			contentPanel.add(getCardPanel());
			contentPanel.add(getSeparatorPanel());
			contentPanel.add(getCancelButton());
			contentPanel.add(getSendButton());
		}
		return contentPanel;
	}

	private JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel();
			cardPanel.setLayout(new CardLayout());
			cardPanel.setBounds(CARD_PANEL_BOUNDS);
			cardPanel.add(getLoginContainer(), LOGIN_PANEL_NAME);
			cardPanel.add(getErrorContainer(), ERROR_PANEL_NAME);
			cardPanel.add(getLoaderPanel(), LOADER_PANEL_NAME);
		}
		return cardPanel;
	}

	private TransparentLoaderPanel getLoaderPanel() {
		if (loaderPanel == null) {
			loaderPanel = new TransparentLoaderPanel();
			loaderPanel.setName(LOADING_PANEL_NAME);
			JPanel spacer = new JPanel();
			spacer.setPreferredSize(PREFERRED_SIZE_LOADER_SPACER);
			loaderPanel.add(spacer, BorderLayout.NORTH);
		}
		return loaderPanel;
	}

	private JPanel getLoginContainer() {
		if (loginContainer == null) {
			loginContainer = new JPanel();
			loginContainer.setLayout(null);
			loginContainer.setBounds(CONTENT_PANEL_BOUNDS);
			loginContainer.add(getLoginPanel());
		}
		return loginContainer;
	}

	private JPanel getErrorContainer() {
		if (errorContainer == null) {
			errorContainer = new JPanel();
			errorContainer.setLayout(null);
			errorContainer.setBounds(CONTENT_PANEL_BOUNDS);
			errorContainer.add(getErrorPanel());
		}
		return errorContainer;
	}

	private JPanel getErrorPanel() {
		if (errorPanel == null) {
			errorPanel = new JPanel();
			errorPanel.setLayout(null);
			errorPanel.setBounds(CONTENT_PANEL_BOUNDS);
			errorPanel.add(getErrorBackgroundPanel());
		}
		return errorPanel;
	}

	private JPanel getErrorBackgroundPanel() {
		if (errorBackgroundPanel == null) {
			errorBackgroundPanel = new JPanel();
			errorBackgroundPanel.setLayout(null);
			errorBackgroundPanel.setName(ERROR_BACKGROUND_PANEL_NAME);
			errorBackgroundPanel.setBounds(BACKGROUND_PANEL_BOUNDS);
			errorBackgroundPanel.add(getErrorIconLabel());
			errorBackgroundPanel.add(getMessageLabel());
		}
		return errorBackgroundPanel;
	}

	private JLabel getErrorIconLabel() {
		if (errorIconLabel == null) {
			errorIconLabel = new JLabel();
			errorIconLabel.setBounds(ERROR_ICON_LABEL_BOUNDS);
			Icon icon = UIManager.getDefaults().getIcon(ERROR_ICON_LABEL_NAME);
			errorIconLabel.setIcon(icon);
		}
		return errorIconLabel;
	}

	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setBounds(PASSWORD_FIELD_BOUNDS);
			passwordField.setName("textFieldPassword");
			passwordField.addFocusListener(new FocusListener());
			passwordField.addKeyListener(new KeyListener());
		}
		return passwordField;
	}

	private JLabel getPasswordLabel() {
		if (passwordLabel == null) {
			passwordLabel = new JLabel();
			passwordLabel.setBounds(PASSWORD_LABEL_BOUNDS);
			passwordLabel.setName("twitterLabel");
		}
		return passwordLabel;
	}

	private JTextField getUserTextField() {
		if (userTextField == null) {
			userTextField = new JTextField();
			userTextField.setBounds(USER_TEXT_FIELD_BOUNDS);
			userTextField.setName("twitterInput");
			userTextField.addFocusListener(new FocusListener());
			userTextField.addKeyListener(new KeyListener());
		}
		return userTextField;
	}

	private JLabel getUserLabel() {
		if (userLabel == null) {
			userLabel = new JLabel();
			userLabel.setBounds(USER_LABEL_BOUNDS);
			userLabel.setName("twitterLabel");
			userLabel.requestFocus();
		}
		return userLabel;
	}

	private JButton getSendButton() {
		if (sendButton == null) {
			sendButton = new JButton();
			sendButton.setBounds(SEND_BUTTON_BOUNDS);
			sendButton.setName(BUTTON_NAME);
			sendButton.setEnabled(true);
			getRootPane().setDefaultButton(sendButton);
			sendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sendButton.setEnabled(false);
					showPanel(LOADER_PANEL_NAME);
					log.info("Signing in to Twitter...");
					loginExecutor.execute(new LoginTwitter());
				}
			});
		}
		return sendButton;
	}

	private JButton getRetryButton() {
		if (retryButton == null) {
			retryButton = new JButton();
			retryButton.setBounds(SEND_BUTTON_BOUNDS);
			retryButton.setName(BUTTON_NAME);
			retryButton.setText(getMessages().getMessage("twitter.dialog.button.retry"));
			retryButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					changePanel(getSendButton(), getRetryButton(), LOGIN_PANEL_NAME,
							getMessages().getMessage("twitter.dialog.title"));
				}
			});
		}
		return retryButton;
	}

	private void changePanel(JButton buttonToAdd, JButton buttonToRemove, String panelName, String title) {
		getContentPanel().remove(buttonToRemove);
		getContentPanel().add(buttonToAdd);
		showPanel(panelName);
		getTitleLabel().setText(title);
		getContentPanel().repaint();
	}

	private void showPanel(String name) {
		CardLayout layout = (CardLayout) cardPanel.getLayout();
		layout.show(getCardPanel(), name);
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
			cancelButton.setName(BUTTON_NAME);
			cancelButton.addActionListener(new CloseListener());
		}
		return cancelButton;
	}

	private JLabel getMessageLabel() {
		if (messageLabel == null) {
			messageLabel = new JLabel();
			messageLabel.setBounds(MESSAGE_LABEL_BOUNDS);
		}
		return messageLabel;
	}

	private void setPasswordIfAutoLogin() {
		viewEngine.request(Actions.Twitter.REQUEST_CREDENTIALS, null, new ResponseCallback<TwitterUser>() {
			@Override
			public void onResponse(TwitterUser twitterUser) {
				if (twitterUser != null && twitterUser.isRememberMe()) {
					getUserTextField().setText(twitterUser.getUsername());
					getPasswordField().setText(twitterUser.getPassword());
					getRemeberMeCheckBox().setSelected(true);
				}
			}
		});
	}

	private final class KeyListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getSource().equals(userTextField) || e.getSource().equals(passwordField)) {
				if (userTextField.getText().length() > 0 && passwordField.getPassword().length > 5) {
					getSendButton().setEnabled(true);
				}
			}
			if (getSendButton().isEnabled() && e.getSource().equals(passwordField) && e.getKeyCode() == KeyEvent.VK_ENTER) {
				getSendButton().doClick();
			}
		}
	}

	private final class FocusListener extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e) {
			if (e.getSource().equals(userTextField) || e.getSource().equals(passwordField)) {
				if (!userTextField.getText().equals("") && passwordField.getPassword().length > 5) {
					getSendButton().setEnabled(true);
				}
			}
		}
	}

	private final class LoginTwitter implements Runnable {
		@Override
		public void run() {
			TwitterUser twitterUser = new TwitterUser(getUserTextField().getText(), new String(getPasswordField()
					.getPassword()), getRemeberMeCheckBox().isSelected());

			final CountDownLatch countDownLatch = new CountDownLatch(1);
			viewEngine.request(Actions.Twitter.LOGIN, twitterUser, new ResponseCallback<Void>() {
				@Override
				public void onResponse(Void v) {
					countDownLatch.countDown();
				}
			});

			try {
				countDownLatch.await(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.error("Timeout while waiting for twitter login response", e);
			}

			if (BooleanUtils.isTrue(viewEngine.get(Model.TWITTER_LOGGED_IN))) {
				closeDialog();
			} else {
				getSendButton().setEnabled(true);
				changePanel(getRetryButton(), sendButton, ERROR_PANEL_NAME,
						getMessages().getMessage("twitter.dialog.error.title"));
			}
		}

	}

}
