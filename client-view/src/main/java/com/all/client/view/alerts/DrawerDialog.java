package com.all.client.view.alerts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.MainFrame;
import com.all.client.view.View;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.transparency.TransparencyManagerFactory;
import com.all.core.events.Events;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.alert.Alert;

public class DrawerDialog extends JDialog implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	protected static final int ALERT_HEIGHT = 25;

	private JPanel leftEmptyPanel = null;
	private JPanel headerPanel = null;
	private JPanel rightEmptyPanel = null;
	private JLayeredPane centerPanel = null;
	private JPanel bottomEmptyPanel = null;
	private JPanel rightShadowEmptyPanel = null;
	private JPanel bottomShadowEmptyPanel = null;
	private JPanel rigthTexturePanel = null;
	private JPanel leftTexturePanel = null;
	private JButton closeButton = null;
	private AlertDrawerScrollPane alertScrollPane = null;
	private JPanel contentPanel = null; // @jve:decl-index=0:visual-constraint="584,82"
	private JButton drawerPreviousButton = null;
	private JButton drawerNextButton = null;
	private JLabel drawerAlertLabel = null;
	private ViewEngine viewEngine;

	Messages messages;

	private Rectangle boundsToSet = new Rectangle();

	private EventListener<ValueEvent<Collection<Alert>>> updateDrawerListener;

	public DrawerDialog(MainFrame mainFrame, AlertDrawerScrollPane alertScrollPane, ViewEngine viewEngine,
			Messages messages) {
		super(mainFrame, null, false, TransparencyManagerFactory.getManager().getTranslucencyCapableGC());
		this.alertScrollPane = alertScrollPane;
		this.messages = messages;
		this.viewEngine = viewEngine;
		initialize();
	}

	private void initialize() {
		this.setPreferredSize(new Dimension((414 + 12), (261 + 12)));
		this.setMinimumSize(new Dimension(300 + 12, (261 + 12)));
		this.setUndecorated(true);
		this.setVisible(false);
		this.setModal(false);
		this.setLayout(new BorderLayout());
		this.add(getContentPanel(), BorderLayout.CENTER);
		this.managePanelsSizes();
		TransparencyManagerFactory.getManager().setWindowOpaque(this, false);
	}

	private JPanel getLeftEmptyPanel() {
		if (leftEmptyPanel == null) {
			leftEmptyPanel = new JPanel();
			leftEmptyPanel.setLayout(null);
			leftEmptyPanel.setPreferredSize(new Dimension(51, 40));
			leftEmptyPanel.setMinimumSize(new Dimension(51, 40));
		}
		return leftEmptyPanel;
	}

	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			drawerAlertLabel = new JLabel();
			drawerAlertLabel.setText(messages.getMessage("drawer.alerts", "0"));
			drawerAlertLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			drawerAlertLabel.setHorizontalAlignment(SwingConstants.CENTER);
			drawerAlertLabel.setName(SynthFonts.BOLD_FONT11_GRAY77_77_77);
			headerPanel = new JPanel();
			headerPanel.setLayout(new BorderLayout());
			headerPanel.setPreferredSize(new Dimension(312, 26));
			headerPanel.setMinimumSize(new Dimension(198, 26));
			headerPanel.setName("headerDrawer");
			headerPanel.add(getDrawerPreviousButton(), BorderLayout.WEST);
			headerPanel.add(getDrawerNextButton(), BorderLayout.EAST);
			headerPanel.add(drawerAlertLabel, BorderLayout.CENTER);
		}
		return headerPanel;
	}

	private JPanel getRightEmptyPanel() {
		if (rightEmptyPanel == null) {
			rightEmptyPanel = new JPanel();
			rightEmptyPanel.setLayout(null);
			rightEmptyPanel.setPreferredSize(new Dimension(51, 40));
			rightEmptyPanel.setMinimumSize(new Dimension(51, 40));
			rightEmptyPanel.add(getCloseButton(), null);
		}
		return rightEmptyPanel;
	}

	private JLayeredPane getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JLayeredPane();
			centerPanel.setPreferredSize(new Dimension(402, 200));
			centerPanel.setMinimumSize(new Dimension(288, 200));
			initAlertScrollPane();
			centerPanel.add(alertScrollPane, 1);
			centerPanel.add(getLeftTexturePanel(), 2);
			centerPanel.add(getRigthTexturePanel(), 2);
		}
		return centerPanel;
	}

	public void managePanelsSizes() {
		this.invalidate();
		int centerPanelWidth = centerPanel.getWidth();
		// when centerPanel size <= 402 only the messagesPanel is shown
		if (centerPanelWidth < 402) {
			alertScrollPane.setBounds(0, 0, centerPanelWidth, centerPanel.getPreferredSize().height);
			alertScrollPane.updateAlertsWidth(centerPanelWidth);
		} else {
			// when centerPanel size >402 the three panels are shown according
			// to the messagesPanel width constraints
			int messagesPanelWidth = (int) ((((double) centerPanelWidth - 402.) * 0.0758620689655172) + 402.);
			int textureWidth = (centerPanelWidth - messagesPanelWidth) / 2;
			leftTexturePanel.setLocation(0, 0);
			alertScrollPane.setBounds(textureWidth, 0, messagesPanelWidth, 200);
			alertScrollPane.updateAlertsWidth(messagesPanelWidth);
			rigthTexturePanel.setLocation(centerPanelWidth - 200, 0);
		}
		this.validate();
	}

	private JPanel getBottomEmptyPanel() {
		if (bottomEmptyPanel == null) {
			bottomEmptyPanel = new JPanel();
			bottomEmptyPanel.setLayout(null);
			bottomEmptyPanel.setPreferredSize(new Dimension(414, 21));
			bottomEmptyPanel.setMinimumSize(new Dimension(300, 21));
		}
		return bottomEmptyPanel;
	}

	private JPanel getRightShadowEmptyPanel() {
		if (rightShadowEmptyPanel == null) {
			rightShadowEmptyPanel = new JPanel();
			rightShadowEmptyPanel.setLayout(null);
			rightShadowEmptyPanel.setPreferredSize(new Dimension(12, (261 + 12)));
			rightShadowEmptyPanel.setMinimumSize(new Dimension(12, (261 + 12)));
		}
		return rightShadowEmptyPanel;
	}

	private JPanel getBottomShadowEmptyPanel() {
		if (bottomShadowEmptyPanel == null) {
			bottomShadowEmptyPanel = new JPanel();
			bottomShadowEmptyPanel.setLayout(null);
			bottomShadowEmptyPanel.setPreferredSize(new Dimension(414 + 12, 12));
			bottomShadowEmptyPanel.setMinimumSize(new Dimension(300 + 12, 12));
		}
		return bottomShadowEmptyPanel;
	}

	private JPanel getRigthTexturePanel() {
		if (rigthTexturePanel == null) {
			rigthTexturePanel = new JPanel();
			rigthTexturePanel.setLayout(null);
			rigthTexturePanel.setName("drawerTexturePanel");
			rigthTexturePanel.setBounds(0, 0, 200, 200);
			rigthTexturePanel.setMinimumSize(new Dimension(200, 200));
			rigthTexturePanel.setPreferredSize(new Dimension(200, 200));
		}
		return rigthTexturePanel;
	}

	private JPanel getLeftTexturePanel() {
		if (leftTexturePanel == null) {
			leftTexturePanel = new JPanel();
			leftTexturePanel.setLayout(null);
			leftTexturePanel.setName("drawerTexturePanel");
			leftTexturePanel.setBounds(0, 0, 200, 200);
			leftTexturePanel.setMinimumSize(new Dimension(200, 200));
			leftTexturePanel.setPreferredSize(new Dimension(200, 200));
		}
		return leftTexturePanel;
	}

	/**
	 * This method initializes closeButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setName("closeDrawerButton");
			closeButton.setBounds(new Rectangle(19, 15, 21, 21));
			closeButton.setToolTipText(messages.getMessage("tooltip.close"));
			closeButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					viewEngine.send(Actions.View.HIDE_DRAWER);
				}
			});
		}
		return closeButton;
	}

	private void initAlertScrollPane() {
		alertScrollPane.addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				managePanelsSizes();
			}
		});

		updateDrawerListener = new EventListener<ValueEvent<Collection<Alert>>>() {

			public void handleEvent(com.all.event.ValueEvent<java.util.Collection<Alert>> eventArgs) {
				enableNavigationButtons();
				drawerAlertLabel.setText(messages.getMessage("drawer.alerts", alertScrollPane.getAlertCount().toString()));
				managePanelsSizes();
			}
		};

	}

	/**
	 * This method initializes contentPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getContentPanel() {
		if (contentPanel == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 2;
			gridBagConstraints4.gridwidth = 3;
			gridBagConstraints4.weightx = 1;
			gridBagConstraints4.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridwidth = 3;
			gridBagConstraints2.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.insets = new Insets(0, 6, 0, 6);
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = GridBagConstraints.NORTH;
			gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.weightx = 1.0D;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridy = 0;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.gridwidth = 3;
			gridBagConstraints7.weightx = 1.0D;
			gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 3;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.gridheight = 0;
			gridBagConstraints8.weighty = 1;

			contentPanel = new JPanel();
			contentPanel.setLayout(new GridBagLayout());
			contentPanel.setName("drawerPanel");
			contentPanel.add(getLeftEmptyPanel(), gridBagConstraints);
			contentPanel.add(getHeaderPanel(), gridBagConstraints1);
			contentPanel.add(getRightEmptyPanel(), gridBagConstraints11);
			contentPanel.add(getCenterPanel(), gridBagConstraints2);
			contentPanel.add(getBottomEmptyPanel(), gridBagConstraints4);
			contentPanel.add(getBottomShadowEmptyPanel(), gridBagConstraints7);
			contentPanel.add(getRightShadowEmptyPanel(), gridBagConstraints8);
		}
		return contentPanel;
	}

	/**
	 * This method initializes drawerPreviousButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDrawerPreviousButton() {
		if (drawerPreviousButton == null) {
			drawerPreviousButton = new JButton();
			drawerPreviousButton.setName("previousDrawerButton");
			drawerPreviousButton.setToolTipText(messages.getMessage("tooltip.prevAlert"));
			drawerPreviousButton.setPreferredSize(new Dimension(15, 26));
			drawerPreviousButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					alertScrollPane.previousPage();
					enableNavigationButtons();

				}
			});
		}
		return drawerPreviousButton;
	}

	/**
	 * This method initializes drawerNextButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getDrawerNextButton() {
		if (drawerNextButton == null) {
			drawerNextButton = new JButton();
			drawerNextButton.setName("nextDrawerButton");
			drawerNextButton.setToolTipText(messages.getMessage("tooltip.nextAlert"));
			drawerNextButton.setPreferredSize(new Dimension(15, 26));
			drawerNextButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					alertScrollPane.nextPage();
					enableNavigationButtons();

				}
			});
		}
		return drawerNextButton;
	}

	@Override
	public void setVisible(boolean v) {
		reinitializeAlerts(v);
		super.setVisible(v);
		alertScrollPane.invalidate();
		this.validate();
		this.repaint();
	}

	private void reinitializeAlerts(boolean visible) {
		if (alertScrollPane != null) {
			alertScrollPane.resetAlerts();
			alertScrollPane.getViewport().setViewPosition(new Point(0, 0));
			if (visible) {
				enableNavigationButtons();
			}
		}
	}

	private void enableNavigationButtons() {
		drawerPreviousButton.setEnabled(alertScrollPane.hasPrevious());
		drawerNextButton.setEnabled(alertScrollPane.hasNext());
	}

	@Override
	public void internationalize(Messages messages) {
		getCloseButton().setToolTipText(messages.getMessage("tooltip.closeAlert"));
		getDrawerNextButton().setToolTipText(messages.getMessage("tooltip.nextAlert"));
		getDrawerPreviousButton().setToolTipText(messages.getMessage("tooltip.prevAlert"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		internationalize(messages);
	}

	@Override
	public void setBounds(Rectangle r) {
		if (boundsWillTakeAction(r)) {
			this.boundsToSet = r;
			super.setBounds(r);
		}
	}

	public boolean boundsWillTakeAction(Rectangle r) {
		return !boundsToSet.equals(r);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.Application.CURRENT_ALERTS_CHANGED, updateDrawerListener);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		viewEngine.addListener(Events.Application.CURRENT_ALERTS_CHANGED, updateDrawerListener);
	}

}
