package com.all.client.view.alerts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.observs.AlertActionEvent;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.EventExecutionMode;
import com.all.event.EventMethod;
import com.all.observ.Observer;
import com.all.shared.alert.Alert;
import com.all.shared.model.ContactInfo;

@org.springframework.stereotype.Component
public final class AlertDrawerScrollPane extends JScrollPane {

	private AlertActionListener actionListener = new AlertActionListener();

	private static final long serialVersionUID = 1L;

	private static final int MAX_ALERTS_PER_PAGE = 40;

	private final String[] synthStyles = new String[] { "drawerMessageWhite", "drawerMessageGray" };
	private AlertView<?> lastExpanded;
	private int lastExpandedIndex;
	private List<AlertView<?>> allAlerts = new LinkedList<AlertView<?>>();

	private int displayedPage = 0;
	private JPanel alertsContainerPanel;
	private Color grayColor = UIManager.getDefaults().getColor("alertDrawerGrayColor");
	private Color whiteColor = UIManager.getDefaults().getColor("alertDrawerWhiteColor");

	@Autowired
	private AlertViewFactory alertViewFactory;
	@Autowired
	private ViewEngine viewEngine;

	public AlertDrawerScrollPane() {
		createGuiComponents();
	}

	@EventMethod(value = Events.Social.CONTACT_UPDATED_ID, mode = EventExecutionMode.ASYNC_IN_SWING)
	public synchronized void onContactUpdated(ContactInfo contact) {
		Collection<Alert> alerts = viewEngine.get(Model.CURRENT_ALERTS);
		if (alerts != null) {
			for (Alert alert : alerts) {
				if (contact.equals(alert.getSender())) {
					alert.getSender().setAvatar(contact.getAvatar());
				}
			}
			repaintAlerts(alerts);
		}
	}

	@EventMethod(value = Model.CURRENT_ALERTS_ID, mode = EventExecutionMode.ASYNC_IN_SWING)
	public synchronized void onCurrentAlertsChanged(Collection<Alert> alerts) {
		repaintAlerts(alerts);
	}

	private void repaintAlerts(Collection<Alert> alerts) {
		if (alerts != null) {

			removeAllAlerts();
			boolean updateLastExpanded = false;
			for (Alert alert : alerts) {
				AlertView<?> alertView = alertViewFactory.getAlertView(alert);
				if (alertView != null) {
					allAlerts.add(alertView);
					alertView.onAlertAction().add(actionListener);
					if (alertView.equals(lastExpanded)) {
						lastExpanded = alertView;
						updateLastExpanded = true;
					}
				}
			}
			showDisplayedPage();
			updateAlertsWidth(getWidth());
			if (updateLastExpanded) {
				if (!Arrays.asList(alertsContainerPanel.getComponents()).contains(lastExpanded)) {
					displayedPage++;
					showDisplayedPage();
				}
				lastExpandedIndex = Arrays.asList(alertsContainerPanel.getComponents()).indexOf(lastExpanded);
				lastExpanded.expand();
				recalculateSize();
				updateAlertBounds(AlertView.MAX_HEIGHT - AlertView.MIN_HEIGHT);
				updateAlertViewIcons(false);
				updateViewportPosition();
			} else {
				lastExpandedIndex = -1;
				lastExpanded = null;
			}
			TransparencyManagerFactory.getManager().setWindowOpaque(SwingUtilities.getWindowAncestor(this), false);
		}
	}

	private void createGuiComponents() {
		alertsContainerPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				// fix background when less than 3 alerts and one is expanded
				Color firstColor = (alertsContainerPanel.getComponentCount() <= 2 && lastExpanded != null) ? grayColor
						: whiteColor;
				Color secondColor = (alertsContainerPanel.getComponentCount() <= 2 && lastExpanded != null) ? whiteColor
						: grayColor;
				// end fix
				for (int i = 0; i < getHeight() / AlertView.MIN_HEIGHT; i++) {
					g.setColor(i % 2 == 0 ? firstColor : secondColor);
					g.fillRect(0, i * AlertView.MIN_HEIGHT, getWidth(), AlertView.MIN_HEIGHT);
				}
				super.paint(g);
			}
		};
		alertsContainerPanel.setLayout(null);
		alertsContainerPanel.addMouseListener(new ExpanderListener());
		setViewportView(alertsContainerPanel);
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.getVerticalScrollBar().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				updateAlertsWidth(getWidth());
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				updateAlertsWidth(getWidth());
			}
		});
	}

	private void pullAlertIntoPanel(AlertView<?> alertView) {
		int displayedAlerts = alertsContainerPanel.getComponentCount();
		if (displayedAlerts < MAX_ALERTS_PER_PAGE) {
			alertView.setStyle(synthStyles[displayedAlerts & 1]);
			alertsContainerPanel.add(alertView);
			recalculateSize();
			setBoundsTo(alertView);
		}
	}

	private void setBoundsTo(AlertView<?> alertView) {
		int width = alertView.getWidth();
		if (getVerticalScrollBar().isVisible()) {
			width -= getVerticalScrollBar().getWidth();
		}
		int y = alertsContainerPanel.getHeight() - alertView.getHeight();
		alertView.setBounds(0, y, width, alertView.getHeight());
	}

	void recalculateSize() {
		int height = 0;
		for (Component component : alertsContainerPanel.getComponents()) {
			if (component.isVisible()) {
				height += component.getHeight();
			}
		}
		alertsContainerPanel.setSize(alertsContainerPanel.getWidth(), height);
		alertsContainerPanel.setMinimumSize(new Dimension((int) alertsContainerPanel.getMinimumSize().getWidth(), height));
		alertsContainerPanel.setMaximumSize(new Dimension((int) alertsContainerPanel.getMaximumSize().getWidth(), height));
		alertsContainerPanel.setPreferredSize(new Dimension((int) alertsContainerPanel.getPreferredSize().getWidth(),
				height));
		alertsContainerPanel.validate();
	};

	private final class ExpanderListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			Component clickedComponent = alertsContainerPanel.getComponentAt(e.getX(), e.getY());
			if (clickedComponent instanceof AlertView<?> && !clickedComponent.equals(lastExpanded)) {
				if (lastExpanded != null) {
					lastExpanded.collapse();
					updateAlertBounds(AlertView.MIN_HEIGHT - AlertView.MAX_HEIGHT);
				}

				lastExpanded = (AlertView<?>) clickedComponent;
				lastExpanded.expand();
				lastExpandedIndex = Arrays.asList(alertsContainerPanel.getComponents()).indexOf(lastExpanded);
				recalculateSize();
				updateAlertBounds(AlertView.MAX_HEIGHT - AlertView.MIN_HEIGHT);
				updateAlertViewIcons(false);
				updateViewportPosition();
			}
		}
	}

	public void resetAlerts() {
		collapseAlerts();
		displayedPage = 0;
		showDisplayedPage();
	}

	private void collapseAlerts() {
		if (lastExpanded != null) {
			lastExpanded.collapse();
			recalculateSize();
			resetLastExpanded();
			updateAlertViewIcons(true);
		}
	}

	private void showDisplayedPage() {
		alertsContainerPanel.removeAll();
		recalculateSize();
		int limit = Math.min(allAlerts.size(), ((displayedPage + 1) * MAX_ALERTS_PER_PAGE));
		for (int i = displayedPage * MAX_ALERTS_PER_PAGE; i < limit; i++) {
			pullAlertIntoPanel(allAlerts.get(i));
		}
	}

	public void updateAlertViewIcons(boolean activeIcon) {
		for (Component component : alertsContainerPanel.getComponents()) {
			AlertView<?> alertView = (AlertView<?>) component;
			alertView.setActiveIcon(activeIcon);
		}
		if (lastExpanded != null) {
			lastExpanded.setActiveIcon(true);
		}
	}

	private void updateAlertBounds(int delta) {
		Component[] alerts = alertsContainerPanel.getComponents();
		for (int i = lastExpandedIndex + 1; i < alerts.length; i++) {
			Component alertView = alerts[i];
			int oldY = alertView.getLocation().y;
			alertView.setBounds(0, oldY + delta, alertView.getWidth(), alertView.getHeight());
		}
	}

	public synchronized void updateAlertsWidth(int width) {
		if (getVerticalScrollBar().isVisible()) {
			width -= getVerticalScrollBar().getWidth();
		}
		for (AlertView<?> alertView : allAlerts) {
			alertView.setPreferredWidth(width);
		}
		revalidate();
	}

	public Integer getAlertCount() {
		return viewEngine.get(Model.CURRENT_ALERTS).size();
	}

	private void updateViewportPosition() {
		int y = AlertView.MIN_HEIGHT * (lastExpandedIndex - 1);
		getViewport().setViewPosition(new Point(0, y < 0 ? 0 : y));
	}

	private void resetLastExpanded() {
		lastExpanded = null;
		lastExpandedIndex = -1;
	}

	private final class AlertActionListener implements Observer<AlertActionEvent> {
		public void observe(AlertActionEvent alertActionEvent) {
			switch (alertActionEvent.getAlertAction()) {
			case LATER:
				lastExpanded.collapse();
				updateAlertBounds(AlertView.MIN_HEIGHT - AlertView.MAX_HEIGHT);
				resetLastExpanded();
				updateAlertViewIcons(true);
				recalculateSize();
			}
		}
	}

	public void removeAllAlerts() {
		allAlerts.clear();
		alertsContainerPanel.removeAll();
		recalculateSize();
	}

	public boolean hasPrevious() {
		return displayedPage > 0;
	}

	public boolean hasNext() {
		if (!allAlerts.isEmpty()) {
			int requiredPages = (allAlerts.size() - 1) / MAX_ALERTS_PER_PAGE;
			return displayedPage < requiredPages;
		}
		return false;
	}

	public void nextPage() {
		collapseAlerts();
		displayedPage++;
		showDisplayedPage();
	}

	public void previousPage() {
		collapseAlerts();
		displayedPage--;
		showDisplayedPage();
	}
}
