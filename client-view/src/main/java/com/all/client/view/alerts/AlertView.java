package com.all.client.view.alerts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.commons.lang.StringUtils;

import com.all.client.view.components.ImagePanel;
import com.all.client.view.observs.AlertActionEvent;
import com.all.client.view.util.LabelUtil;
import com.all.core.common.util.ImageUtil;
import com.all.i18n.Messages;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;
import com.all.shared.alert.Alert;

/**
 * <b>Note:</b> Remember to call initialize() method on the constructor<br>
 * of child classes for proper function before showing the alert
 */
public abstract class AlertView<T extends Alert> extends JPanel {
	private static final int IMAGESIZE_COTA = 105;

	private static final long serialVersionUID = 3565006451773036970L;

	static final int MAX_HEIGHT = 150;

	static final int MIN_HEIGHT = 25;

	private static final double ARC_IMAGE = .17;

	private static final Dimension BUTTON_SIZE = new Dimension(23, 23);

	private static final Dimension IMAGE_PANEL_DEFAULT_SIZE = new Dimension(70, 70);

	private static final Insets IMAGE_PANEL_INSETS = new Insets(2, 15, 7, 10);

	private static final Insets QUESTION_MESSAGE_INSETS = new Insets(0, 15, 0, 0);

	private int preferredWidth = 402;

	private JPanel headerPanel;

	private JPanel footerPanel;

	private JLabel headerLabel;
	private final T alert;

	protected JPanel buttonBarPanel;
	protected JPanel detailsPanel;
	protected final Messages messages;

	Observable<AlertActionEvent> buttonListeners = new Observable<AlertActionEvent>();
	private final IconType iconType;

	private JLabel description;

	private JLabel footerMessage;

	/**
	 * Enum for the different Button Bars used in an AlertView footer.<br/>
	 * <br/>
	 * SINGLE - uses the accept button only<br>
	 * TRIPLE - uses the accept, deny and answer later buttons<br>
	 * QUAD - uses the accept, deny, answer later and details buttons<br>
	 */
	enum ButtonBar {
		SINGLE, STANDARD, MUSIC_CONTENT;
	};

	/**
	 * Enum used for the different icons used in the alerts<br/>
	 * <br/>
	 * CONTACT - uses a contact icon<br/>
	 * MUSIC - uses a music note icon<br/>
	 * CUSTOM - uses nothing, override {@code getActiveIcon()} and {@code
	 * getInactiveIcon()} to set custom icons<br/>
	 */
	enum IconType implements Iconizable {
		CONTACT("contactAlertActiveIcon", "contactAlertInactiveIcon"), MUSIC("musicAlertActiveIcon",
				"musicAlertInactiveIcon"), CUSTOM(null, null), ALL("icons.allActive", "icons.allInactive"), NOTIFICATION(
				"icons.notificationActive", "icons.notificationInactive"), FORUM("icons.forumActive", "icons.forumInactive"), TESTIMONIALS(
				"icons.testimonialsActive", "icons.testimonialsInactive"), MC_REQUEST("mcRequestAlertActiveIcon",
				"mcRequestAlertInactiveIcon");

		String activeIcon;
		String inactiveIcon;

		private IconType(String activeIcon, String inactiveIcon) {
			this.activeIcon = activeIcon;
			this.inactiveIcon = inactiveIcon;
		}

		@Override
		public Icon getActiveIcon() {
			return UIManager.getIcon(activeIcon);
		}

		@Override
		public Icon getInactiveIcon() {
			return UIManager.getIcon(inactiveIcon);
		}
	}

	/**
	 * <b>Note:</b> Remember to call initialize() method on the constructor<br>
	 * of child classes for proper function before showing the alert
	 */
	protected AlertView(T alert, IconType iconType, Messages messages) {
		this.messages = messages;
		this.alert = alert;
		this.iconType = iconType == null ? IconType.CUSTOM : iconType;
		setHeight(MIN_HEIGHT);
		setLayout(new BorderLayout());
	}

	protected final void initialize() {
		add(getHeaderPanel(), BorderLayout.NORTH);
		add(getDetailsPanel(), BorderLayout.CENTER);
		add(getFooterPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Gets the title of the alert
	 * 
	 * @return title to be shown in the alert
	 */
	abstract String getHeader();

	/**
	 * Gets the message sent with the alert<br>
	 * <b>Note:</b> Use the html tag to do word wrapping when needed
	 * 
	 * @return message to be shown in the alert
	 */
	abstract String getDescriptionMessage();

	/**
	 * Gets the question of the alert
	 * 
	 * @return question to be shown in the alert
	 */
	abstract String getFooter();

	/**
	 * see {@link ButtonBar}
	 * 
	 * @return ButtonBar
	 */
	abstract ButtonBar getButtonBar();

	/**
	 * Called when the accept button is clicked<br>
	 * <br>
	 * <b>Note:</b> This is called within the EDT<br>
	 * Use a swing worker for heavy tasks
	 */
	abstract void executeAccept();

	/**
	 * Called when the details button is clicked<br>
	 * <br>
	 * <b>Note:</b> This is called within the EDT<br>
	 * Use a swing worker for heavy tasks
	 */
	abstract void executeDetails();

	/**
	 * Called when the deny button is clicked<br>
	 * <br>
	 * <b>Note:</b> This is called within the EDT<br>
	 * Use a swing worker for heavy tasks
	 */
	abstract void executeDeny();

	protected Image getSenderImage() {
		return ImageUtil.getImage(getAlert().getSender().getAvatar());
	}

	protected final Icon getActiveIcon() {
		return iconType.getActiveIcon();
	}

	protected final Icon getInactiveIcon() {
		return iconType.getInactiveIcon();
	}

	protected final Date getAlertDate() {
		return alert.getDate();
	}

	protected final T getAlert() {
		return alert;
	}

	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new JPanel();
			headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 2));
			headerPanel.add(getHeaderLabel());
		}
		return headerPanel;
	}

	private JLabel getHeaderLabel() {
		if (headerLabel == null) {
			headerLabel = new JLabel(getHeader());
			headerLabel.setIcon(getActiveIcon());
			headerLabel.setHorizontalAlignment(JLabel.LEFT);
		}
		return headerLabel;
	}

	/**
	 * Override if a different layout is needed<br>
	 * Return a panel that has height of 100 px
	 * 
	 * @return JPanel with height of 100 px
	 */
	protected JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			ImagePanel imagePanel = new ImagePanel();
			imagePanel.setImage(getSenderImage(), ARC_IMAGE, ARC_IMAGE);
			imagePanel.setPreferredSize(IMAGE_PANEL_DEFAULT_SIZE);
			imagePanel.setMinimumSize(IMAGE_PANEL_DEFAULT_SIZE);
			imagePanel.setMaximumSize(IMAGE_PANEL_DEFAULT_SIZE);

			JLabel footerMessage = getFooterMessage();

			GridBagConstraints imagePanelConstraints = new GridBagConstraints();
			imagePanelConstraints.gridx = 0;
			imagePanelConstraints.gridy = 0;
			imagePanelConstraints.insets = IMAGE_PANEL_INSETS;

			GridBagConstraints descriptionConstraints = new GridBagConstraints();
			descriptionConstraints.gridx = 1;
			descriptionConstraints.gridy = 0;
			descriptionConstraints.fill = GridBagConstraints.HORIZONTAL;
			descriptionConstraints.weightx = 1.0;

			GridBagConstraints questionMessageConstraints = new GridBagConstraints();
			questionMessageConstraints.gridx = 0;
			questionMessageConstraints.gridy = 1;
			questionMessageConstraints.gridwidth = 0;
			questionMessageConstraints.fill = GridBagConstraints.HORIZONTAL;
			questionMessageConstraints.weightx = 1.0;
			questionMessageConstraints.insets = QUESTION_MESSAGE_INSETS;

			detailsPanel = new JPanel(new GridBagLayout());
			detailsPanel.add(imagePanel, imagePanelConstraints);
			detailsPanel.add(getDescription(), descriptionConstraints);
			detailsPanel.add(footerMessage, questionMessageConstraints);
			detailsPanel.setVisible(false);
		}
		return detailsPanel;
	}

	private JLabel getFooterMessage() {
		if (footerMessage == null) {
			footerMessage = new JLabel();
			footerMessage.setHorizontalAlignment(JLabel.CENTER);
			footerMessage.setText(getFooter());
		}
		return footerMessage;
	}

	private JLabel getDescription() {
		if (description == null) {
			description = new JLabel();
			description.setText(getDescriptionMessage());
		}
		return description;
	}

	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			DateFormat df = new SimpleDateFormat("MMMMM / d / yyyy");
			String date = getAlertDate() != null ? df.format(getAlertDate()) : "";
			date = StringUtils.capitalize(date);
			JLabel dateLabel = new JLabel(date);

			getButtonBarPanel();

			JPanel spacer = new JPanel();
			spacer.setSize(new Dimension(5, 2));
			footerPanel = new JPanel();
			footerPanel.setLayout(new BorderLayout());
			footerPanel.add(spacer, BorderLayout.WEST);
			footerPanel.add(dateLabel, BorderLayout.CENTER);
			footerPanel.add(getButtonBarPanel(), BorderLayout.EAST);
			footerPanel.setVisible(false);
		}

		return footerPanel;
	}

	protected JPanel getButtonBarPanel() {
		if (buttonBarPanel == null) {
			buttonBarPanel = new JPanel();
			buttonBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
			switch (getButtonBar()) {
			case SINGLE:
				createAndAddAcceptButton();
				break;
			case STANDARD:
				createAndAddAcceptButton();
				createAndAddDenyButton();
				createAndAddLaterButton();
				break;

			case MUSIC_CONTENT:
				createAndAddAcceptButton();
				createAndAddDenyButton();
				createAndAddDetailsButton();
				createAndAddLaterButton();
			}
		}
		return buttonBarPanel;
	}

	private void createAndAddDetailsButton() {
		JButton detailsButton = createBarButton("alertDetailsButton");
		detailsButton.addActionListener(new DetailsButtonActionListener());
		detailsButton.setToolTipText(messages.getMessage("alertView.tooltip.explore"));
		buttonBarPanel.add(detailsButton);
	}

	protected final void createAndAddLaterButton() {
		JButton laterButton = createBarButton("alertLaterButton");
		laterButton.addActionListener(new LaterButtonActionListener());
		laterButton.setToolTipText(messages.getMessage("alertView.tooltip.later"));
		buttonBarPanel.add(laterButton);
	}

	protected final void createAndAddDenyButton() {
		JButton denyButton = createBarButton("alertDenyButton");
		denyButton.addActionListener(new DenyButtonActionListener());
		denyButton.setToolTipText(messages.getMessage("alertView.tooltip.deny"));
		buttonBarPanel.add(denyButton);
	}

	protected final void createAndAddAcceptButton() {
		JButton acceptButton = createBarButton("alertAcceptButton");
		acceptButton.addActionListener(new AcceptButtonActionListener());
		acceptButton.setToolTipText(messages.getMessage("alertView.tooltip.accept"));
		buttonBarPanel.add(acceptButton);
	}

	private JButton createBarButton(String synthStyle) {
		JButton button = new JButton();
		button.setSize(BUTTON_SIZE);
		button.setPreferredSize(BUTTON_SIZE);
		button.setMinimumSize(BUTTON_SIZE);
		button.setName(synthStyle);
		return button;
	}

	final void setHeight(int height) {
		setSize(preferredWidth, height);
		setMinimumSize(new Dimension(preferredWidth, height));
		setPreferredSize(new Dimension(preferredWidth, height));
		setMaximumSize(new Dimension(preferredWidth, height));
		this.repaint();
	}

	final void collapse() {
		setHeight(MIN_HEIGHT);
		getDetailsPanel().setVisible(false);
		getFooterPanel().setVisible(false);
	}

	final void expand() {
		setHeight(MAX_HEIGHT);
		getDetailsPanel().setVisible(true);
		getFooterPanel().setVisible(true);
	}

	public final void setPreferredWidth(int width) {
		preferredWidth = width;
		setHeight(getHeight());
		getDescription().setSize(new Dimension(width - IMAGESIZE_COTA, 0));
		getDescription().setText(
				LabelUtil.splitTextInTwoRows(description, getDescriptionMessage(), Color.black, width - 105));
		adjustHeaderTextWidth();

	}

	private void adjustHeaderTextWidth() {
		int fontWidth = 4;
		int labelMaxWidth = preferredWidth - 43;
		if (getHeaderLabel().getText().length() * fontWidth >= labelMaxWidth) {
			int newLength = labelMaxWidth / fontWidth - 3;
			String header = (newLength >= getHeader().length() - 3 || newLength < 0) ? getHeader() : getHeader().substring(0,
					newLength)
					+ "...";
			getHeaderLabel().setText(header);
		} else {
			getHeaderLabel().setText(getHeader());
		}
	}

	private final class DetailsButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			executeDetails();
			buttonListeners.fire(new AlertActionEvent(AlertActionEvent.AlertAction.DETAILS));
		}
	}

	private final class LaterButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			buttonListeners.fire(new AlertActionEvent(AlertActionEvent.AlertAction.LATER));
		}
	}

	private final class DenyButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			executeDeny();
			buttonListeners.fire(new AlertActionEvent(AlertActionEvent.AlertAction.DENY));
		}
	}

	private final class AcceptButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			executeAccept();
			buttonListeners.fire(new AlertActionEvent(AlertActionEvent.AlertAction.ACCEPT));
		}
	}

	public final void setStyle(String string) {
		setName(string);
	}

	final ObserverCollection<AlertActionEvent> onAlertAction() {
		return buttonListeners;
	}

	public final void setActiveIcon(boolean active) {
		getHeaderLabel().setIcon(active ? getActiveIcon() : getInactiveIcon());
	}

	@Override
	public final boolean equals(Object alertView) {
		return alertView == null || !(alertView instanceof AlertView<?>) ? false : this.getAlert().equals(
				((AlertView<?>) alertView).getAlert());
	}

	@Override
	public final int hashCode() {
		return getAlert().hashCode();
	}
}

interface Iconizable {
	Icon getActiveIcon();

	Icon getInactiveIcon();
}
