package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.core.actions.Actions;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Root;
import com.all.shared.model.Root.ContainerType;

public abstract class CollapsedLibraryPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension BUTTON_PANEL_DEFAULT_SIZE = new Dimension(30, 48);

	private static final Dimension BUTTON_SPACER_DEFAULT_SIZE = new Dimension(20, 15);

	private static final Dimension CLOSE_BUTTON_SIZE = new Dimension(20, 18);

	private static final Dimension EXPAND_BIG_BUTTON_DEFAULT_SIZE = new Dimension(20, 20);

	private static final Dimension EXPAND_SMALL_BUTTON_SIZE = new Dimension(20, 17);

	private static final Dimension HORIZONTAL_SPACER_DEFAULT_SIZE = new Dimension(30, 2);

	private static final Dimension VERTICAL_SPACER_DEFAULT_SIZE = new Dimension(1, 30);

	private static final Insets BOTTOM_BUTTON_INSETS = new Insets(0, 4, 5, 4);

	private static final Insets TOP_BUTTON_INSETS = new Insets(5, 4, 3, 4);

	private static final String BUBBLE_PANEL_NAME = "collapsedPanelBubble";

	private static final String CLOSE_BUTTON_NAME = "closeCollapsedLibraryButton";

	private static final String EXPAND_BIG_BUTTON_NAME = "expandCollapsedLibraryBigButton";

	private static final String EXPAND_SMALL_BUTTON_NAME = "expandCollapsedLibrarySmallButton";

	private static final String NAME = "collapsedPanelBackground";

	private JButton closeButton;

	private JButton expandBigButton;

	private JButton expandSmallButton;

	private JPanel buttonPanel;

	private JPanel buttonSpacer;

	private JPanel horizontalSpacerPanel;

	private JPanel verticalSpacerPanel;

	private VerticalLabelPanel bubblePanel;

	private Messages messages;

	public CollapsedLibraryPanel(Messages messages) {
		this.messages = messages;
		this.setMaximumSize(LibraryPanel.COLLAPSED_MAX_SIZE);
		this.setMinimumSize(LibraryPanel.COLLAPSED_SIZE);
		this.setPreferredSize(LibraryPanel.COLLAPSED_SIZE);
		this.setSize(LibraryPanel.COLLAPSED_SIZE);
		this.setName(NAME);
		this.setLayout(new BorderLayout());
		this.add(getVerticalSpacerPanel(), BorderLayout.WEST);
		this.add(getVerticalSpacerPanel(), BorderLayout.WEST);
		this.add(getBubblePanel(), BorderLayout.CENTER);
		this.add(getHorizontalSpacerPanel(), BorderLayout.SOUTH);
		this.add(getButtonPanel(), BorderLayout.NORTH);
		setMessages(messages);
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		removeMessages(messages);
	}

	public void initialize(final ViewEngine viewEngine, final Root root) {
		getCloseButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.sendValueAction(Actions.Library.LIBRARY_ROOT_REMOVED, root);
			}
		});
		getExpandBigButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CollapsedLibraryPanel.this.dispatchEvent(e);
			}
		});
		getExpandSmallButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CollapsedLibraryPanel.this.dispatchEvent(e);
			}
		});
		getExpandBigButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CollapsedLibraryPanel.this.dispatchEvent(e);
			}
		});
		getExpandSmallButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CollapsedLibraryPanel.this.dispatchEvent(e);
			}
		});

		GridBagConstraints topButton = new GridBagConstraints();
		topButton.gridx = 0;
		topButton.gridy = 0;
		topButton.insets = TOP_BUTTON_INSETS;

		GridBagConstraints bottomButton = new GridBagConstraints();
		bottomButton.gridx = 0;
		bottomButton.gridy = 1;
		bottomButton.insets = BOTTOM_BUTTON_INSETS;

		if (root.getType() == ContainerType.LOCAL) {
			getButtonPanel().add(getExpandBigButton(), topButton);
			getButtonPanel().add(getButtonSpacer(), bottomButton);
		} else {
			getButtonPanel().add(getCloseButton(), topButton);
			getButtonPanel().add(getExpandSmallButton(), bottomButton);
		}
		String name = root.getName();
		if (name == null || "".equals(name.trim())) {
			getBubblePanel().setLabel(messages.getMessage("collapse.local.panel.title"));
		} else {
			getBubblePanel().setLabel(messages.getMessage("collapse.remote.panel.title", name));
		}
	}

	private JPanel getVerticalSpacerPanel() {
		if (verticalSpacerPanel == null) {
			verticalSpacerPanel = new JPanel();
			verticalSpacerPanel.setSize(VERTICAL_SPACER_DEFAULT_SIZE);
			verticalSpacerPanel.setMaximumSize(VERTICAL_SPACER_DEFAULT_SIZE);
			verticalSpacerPanel.setMinimumSize(VERTICAL_SPACER_DEFAULT_SIZE);
			verticalSpacerPanel.setPreferredSize(VERTICAL_SPACER_DEFAULT_SIZE);
		}
		return verticalSpacerPanel;
	}

	private JPanel getHorizontalSpacerPanel() {
		if (horizontalSpacerPanel == null) {
			horizontalSpacerPanel = new JPanel();
			horizontalSpacerPanel.setSize(HORIZONTAL_SPACER_DEFAULT_SIZE);
			horizontalSpacerPanel.setMaximumSize(HORIZONTAL_SPACER_DEFAULT_SIZE);
			horizontalSpacerPanel.setMinimumSize(HORIZONTAL_SPACER_DEFAULT_SIZE);
			horizontalSpacerPanel.setPreferredSize(HORIZONTAL_SPACER_DEFAULT_SIZE);
		}
		return horizontalSpacerPanel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.setSize(BUTTON_PANEL_DEFAULT_SIZE);
			buttonPanel.setMaximumSize(BUTTON_PANEL_DEFAULT_SIZE);
			buttonPanel.setMinimumSize(BUTTON_PANEL_DEFAULT_SIZE);
			buttonPanel.setPreferredSize(BUTTON_PANEL_DEFAULT_SIZE);
		}
		return buttonPanel;
	}

	private JPanel getButtonSpacer() {
		if (buttonSpacer == null) {
			buttonSpacer = new JPanel();
			buttonSpacer.setSize(BUTTON_SPACER_DEFAULT_SIZE);
			buttonSpacer.setMaximumSize(BUTTON_SPACER_DEFAULT_SIZE);
			buttonSpacer.setMinimumSize(BUTTON_SPACER_DEFAULT_SIZE);
			buttonSpacer.setPreferredSize(BUTTON_SPACER_DEFAULT_SIZE);
		}
		return buttonSpacer;
	}

	private JButton getExpandBigButton() {
		if (expandBigButton == null) {
			expandBigButton = new JButton();
			expandBigButton.setSize(EXPAND_BIG_BUTTON_DEFAULT_SIZE);
			expandBigButton.setMaximumSize(EXPAND_BIG_BUTTON_DEFAULT_SIZE);
			expandBigButton.setMinimumSize(EXPAND_BIG_BUTTON_DEFAULT_SIZE);
			expandBigButton.setPreferredSize(EXPAND_BIG_BUTTON_DEFAULT_SIZE);
			expandBigButton.setName(EXPAND_BIG_BUTTON_NAME);
		}
		return expandBigButton;
	}

	private JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setSize(CLOSE_BUTTON_SIZE);
			closeButton.setMaximumSize(CLOSE_BUTTON_SIZE);
			closeButton.setMinimumSize(CLOSE_BUTTON_SIZE);
			closeButton.setPreferredSize(CLOSE_BUTTON_SIZE);
			closeButton.setName(CLOSE_BUTTON_NAME);
		}
		return closeButton;
	}

	private JButton getExpandSmallButton() {
		if (expandSmallButton == null) {
			expandSmallButton = new JButton();
			expandSmallButton.setSize(EXPAND_SMALL_BUTTON_SIZE);
			expandSmallButton.setMaximumSize(EXPAND_SMALL_BUTTON_SIZE);
			expandSmallButton.setMinimumSize(EXPAND_SMALL_BUTTON_SIZE);
			expandSmallButton.setPreferredSize(EXPAND_SMALL_BUTTON_SIZE);
			expandSmallButton.setName(EXPAND_SMALL_BUTTON_NAME);
		}
		return expandSmallButton;
	}

	private VerticalLabelPanel getBubblePanel() {
		if (bubblePanel == null) {
			bubblePanel = new VerticalLabelPanel();
			bubblePanel.setName(BUBBLE_PANEL_NAME);
		}
		return bubblePanel;
	}

	@Override
	public void internationalize(Messages messages) {
		getExpandBigButton().setToolTipText(messages.getMessage("tooltip.expandPanel"));
		getCloseButton().setToolTipText(messages.getMessage("tooltip.closePanel"));
		getExpandSmallButton().setToolTipText(messages.getMessage("tooltip.expandPanel"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

}
