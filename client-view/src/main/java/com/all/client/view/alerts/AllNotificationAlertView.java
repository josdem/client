package com.all.client.view.alerts;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyEvent;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.i18n.Messages;
import com.all.shared.alert.Alert;
import com.all.shared.alert.AllNotificationAlert;

public class AllNotificationAlertView extends AlertView<AllNotificationAlert> {

	private static final String NOTIFICATION_LINK_BUTTON_NAME = "notificationLinkButton";

	private static final long serialVersionUID = 1L;

	private final Log log = LogFactory.getLog(this.getClass());

	private final ViewEngine viewEngine;

	public AllNotificationAlertView(AllNotificationAlert alert, Messages messages, ViewEngine viewEngine) {
		super(alert, IconType.NOTIFICATION, messages);
		this.viewEngine = viewEngine;
	}

	@Override
	void executeAccept() {
		throw new UnsupportedOperationException("Cannot accept an All Notification alert.");
	}

	@Override
	void executeDeny() {
		throw new UnsupportedOperationException("Cannot deny an All Notification alert.");
	}

	@Override
	void executeDetails() {
		try {
			java.awt.Desktop.getDesktop().browse(new URI(getAlert().getLink()));
		} catch (Exception e) {
			log.error(e, e);
		}
		viewEngine.send(Actions.Alerts.ALERT_ACTION_DELETE, new ValueAction<Alert>(getAlert()));
	}

	@Override
	com.all.client.view.alerts.AlertView.ButtonBar getButtonBar() {
		return ButtonBar.SINGLE;
	}

	@Override
	String getDescriptionMessage() {
		return getAlert().getDescription();
	}

	@Override
	String getFooter() {
		return null;
	}

	@Override
	String getHeader() {
		return getAlert().getHeader();
	}

	@Override
	protected Image getSenderImage() {
		return null;
	}

	@Override
	protected JPanel getDetailsPanel() {
		if (detailsPanel == null) {
			final JLabel description = new JLabel();
			description.setText(getDescriptionMessage());
			description.setHorizontalAlignment(JLabel.CENTER);

			final JButton linkButton = new JButton();
			linkButton.setName(NOTIFICATION_LINK_BUTTON_NAME);
			linkButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					executeDetails();
				}
			});
			final int descriptionHeight = 75;
			final int buttonWidth = 122;
			final int buttonHeight = 22;
			final int maxPanelHeight = 99;
			final int zero = 0;
			detailsPanel = new JPanel(null);
			detailsPanel.addHierarchyBoundsListener(new HierarchyBoundsAdapter() {
				@Override
				public void ancestorResized(HierarchyEvent e) {
					Container changedParent = e.getChangedParent();
					if(changedParent != null){
						Dimension newSize = changedParent.getSize();
						description.setBounds(zero, zero, newSize.width, descriptionHeight);
						linkButton.setBounds(calculateButtonGap(newSize.width), maxPanelHeight - buttonHeight, buttonWidth,
								buttonHeight);
					}
				}

				private int calculateButtonGap(int width) {
					return (width - buttonWidth) / 2;
				}
			});
			detailsPanel.add(description);
			detailsPanel.add(linkButton);
		}
		return detailsPanel;
	}

	@Override
	protected JPanel getButtonBarPanel() {
		if (buttonBarPanel == null) {
			buttonBarPanel = new JPanel();
			buttonBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
			createAndAddLaterButton();
		}
		return buttonBarPanel;
	}

}
