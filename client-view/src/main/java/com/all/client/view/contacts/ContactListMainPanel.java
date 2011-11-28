package com.all.client.view.contacts;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Container of many other contact panels in order to encapsulate their layout
 */
@Component
public class ContactListMainPanel extends JPanel {
	private static final Dimension SIZE = new Dimension(224, 723);
	private static final long serialVersionUID = -455499104018896508L;

	@Autowired
	private ContactListHeaderPanel contactListHeaderPanel;
	@Autowired
	private ContactListPanel contactListPanel;

	public ContactListMainPanel() {
	}

	@PostConstruct
	@SuppressWarnings("unused")
	private void initialize() {
		this.setSize(SIZE);
		this.setPreferredSize(SIZE);
		this.setMinimumSize(SIZE);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(contactListHeaderPanel, BorderLayout.NORTH);
		panel.add(contactListPanel, BorderLayout.CENTER);
		GridBagLayout gridBagLayout = new GridBagLayout();
		this.setLayout(gridBagLayout);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(0, 2, 0, 2);
		this.add(panel, gridBagConstraints);
	}

}
