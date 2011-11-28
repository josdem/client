package com.all.client.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JPanel;

import org.springframework.stereotype.Component;

@Component
public class LogoPanel extends JPanel {

	private static final long serialVersionUID = 3672771087622824207L;

	private static final Dimension DEFAULT_SIZE = new Dimension(294, 82);
	private static final Dimension CENTER_PANEL_PREFERRED_SIZE = new Dimension(180, 82);
	private static final Dimension DEFAULT_LEFT_PANEL_SIZE = new Dimension(2, 82);
	private static final String LOGO_SIDE_PANEL = "logoSidePanel";
	private static final String LOGO_PANEL = "logoPanel";

	public LogoPanel() {
		this.setLayout(new GridBagLayout());
		initialize();
	}

	public void initialize() {
		GridBagConstraints leftConstraint = new GridBagConstraints();
		leftConstraint.gridx = 0;
		leftConstraint.gridy = 0;
		leftConstraint.weightx = 0.5;
		leftConstraint.fill = GridBagConstraints.HORIZONTAL;
		JPanel leftPanel = new JPanel();
		leftPanel.setName(LOGO_SIDE_PANEL);
		leftPanel.setSize(DEFAULT_LEFT_PANEL_SIZE);
		leftPanel.setPreferredSize(DEFAULT_LEFT_PANEL_SIZE);
		leftPanel.setMinimumSize(DEFAULT_LEFT_PANEL_SIZE);

		GridBagConstraints centerConstraint = new GridBagConstraints();
		centerConstraint.gridx = 1;
		centerConstraint.gridy = 0;
		centerConstraint.weightx = 0.0;
		JPanel centerPanel = new JPanel();
		centerPanel.setName(LOGO_PANEL);
		centerPanel.setSize(CENTER_PANEL_PREFERRED_SIZE);
		centerPanel.setPreferredSize(CENTER_PANEL_PREFERRED_SIZE);
		centerPanel.setMinimumSize(CENTER_PANEL_PREFERRED_SIZE);

		GridBagConstraints rightConstraint = new GridBagConstraints();
		rightConstraint.gridx = 2;
		rightConstraint.gridy = 0;
		rightConstraint.weightx = 0.5;
		rightConstraint.fill = GridBagConstraints.HORIZONTAL;
		JPanel rightPanel = new JPanel();
		rightPanel.setName(LOGO_SIDE_PANEL);
		rightPanel.setSize(DEFAULT_LEFT_PANEL_SIZE);
		rightPanel.setPreferredSize(DEFAULT_LEFT_PANEL_SIZE);
		rightPanel.setMinimumSize(DEFAULT_LEFT_PANEL_SIZE);

		this.setLayout(new GridBagLayout());
		this.setSize(DEFAULT_SIZE);
		this.setMaximumSize(new Dimension(729, 82));
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(CENTER_PANEL_PREFERRED_SIZE);
		this.add(leftPanel, leftConstraint);
		this.add(centerPanel, centerConstraint);
		this.add(rightPanel, rightConstraint);
		this.addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				if (getParent() != null) {
					if (isParentPanelWidthOdd()) {
						int width = (int) (getParent().getSize().getWidth() + 1);
						getParent().setSize(width, getParent().getHeight());
						if (isVisible()) {
							revalidate();
						}
					}
				}
			}
		});

	}

	private boolean isParentPanelWidthOdd() {
		return getParent().getSize().getWidth() % 2 != 0;
	}

}
