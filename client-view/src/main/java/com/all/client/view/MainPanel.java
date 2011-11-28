package com.all.client.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.all.client.view.contacts.ContactListMainPanel;
import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.core.events.Events;
import com.all.event.EventMethod;

@org.springframework.stereotype.Component
public final class MainPanel extends JPanel {
	private static final int LAYOUT_ALIGNMENT = 0;
	private static final Dimension MAXIMUM_LAYOUT_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	private static final Dimension PREFERRED_LAYOUT_SIZE = new Dimension(100, 100);
	private static final Dimension MINIMUM_LAYOUT_SIZE = new Dimension(0, 0);
	private static final Dimension DEFAULT_SIZE = new Dimension(1020, 741);
	private static final String LOGIN_PANEL = "LOGIN_PANEL";
	private static final String MIDDLE_PANEL = "MIDDLE_PANEL";
	private static final long serialVersionUID = 1L;

	@Autowired
	private HipecotechTopPanel topPanel = null;
	@Autowired
	private MiddlePanel middlePanel = null;
	@Autowired
	private ContactListMainPanel contactPanel;
	@Autowired
	private ToolBarPanel toolbarPanel;
	private JPanel cardsPanel;

	public MainPanel() {
		super();
		this.setName("backgroundContentPane");
	}

	protected void doResize() {
		int h = this.getHeight();
		int w = this.getWidth() - 4;
		int y = 0;
		int x = 2;
		topPanel.setBounds(x, y, w, topPanel.getHeight());
		y += topPanel.getHeight();
		h -= topPanel.getHeight();

		int toolbarHeight = (int) toolbarPanel.getMinimumSize().getHeight();
		toolbarPanel.setBounds(x, y, w, toolbarHeight);
		y += toolbarHeight;
		h -= toolbarHeight;

		if (contactPanel.isVisible()) {
			w -= contactPanel.getWidth();
			contactPanel.setBounds(x + w, y, contactPanel.getWidth(), h);
		}
		cardsPanel.setBounds(2, y, w, h);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	@Autowired
	public void initialize() {
		this.setSize(DEFAULT_SIZE);
		this.setLayout(new LayoutManager2() {

			@Override
			public void addLayoutComponent(String name, Component comp) {
			}

			@Override
			public void layoutContainer(Container parent) {
				doResize();
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return MINIMUM_LAYOUT_SIZE;
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return PREFERRED_LAYOUT_SIZE;
			}

			@Override
			public void removeLayoutComponent(Component comp) {
			}

			@Override
			public void addLayoutComponent(Component comp, Object constraints) {
			}

			@Override
			public float getLayoutAlignmentX(Container target) {
				return LAYOUT_ALIGNMENT;
			}

			@Override
			public float getLayoutAlignmentY(Container target) {
				return LAYOUT_ALIGNMENT;
			}

			@Override
			public void invalidateLayout(Container target) {
				doResize();
			}

			@Override
			public Dimension maximumLayoutSize(Container target) {
				return MAXIMUM_LAYOUT_SIZE;
			}

		});
		this.add(topPanel);
		this.add(toolbarPanel);
		this.add(getCardsPanel());
		this.add(contactPanel);

		// This listener does nothing BUT makes obvious to Java MAC
		// implementation to update correct coordinates of MainFrame.layeredPane
		// components (bubblePanel) before paint them
		this.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorAdded(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}
		});

		WindowDraggerMouseListener draggerMouseListener = new WindowDraggerMouseListener();
		draggerMouseListener.setup(topPanel);
	}

	private JPanel getCardsPanel() {
		if (cardsPanel == null) {
			cardsPanel = new JPanel();
			cardsPanel.setLayout(new CardLayout());
			cardsPanel.add(middlePanel, MIDDLE_PANEL);
		}
		return cardsPanel;
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onAppStarted() {
		((CardLayout) cardsPanel.getLayout()).show(cardsPanel, MIDDLE_PANEL);
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onUserLogout() {
		((CardLayout) cardsPanel.getLayout()).show(cardsPanel, LOGIN_PANEL);
	}

}
