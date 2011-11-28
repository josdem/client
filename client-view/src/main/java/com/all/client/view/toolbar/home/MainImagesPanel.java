package com.all.client.view.toolbar.home;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.Timer;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class MainImagesPanel extends JScrollPane implements Internationalizable {

	private static final String MAIN_IMAGE_TOGGLE_BUTTON = "mainImageToggleButton";

	private static final long serialVersionUID = 1L;

	private static final int MAX_INDEX = 5;

	private static final int TIMER_DELAY = 5000;

	private static final Dimension MAXIMUM_SIZE = new Dimension(802, 402);

	private static final Dimension MINIMUM_SIZE = new Dimension(602, 402);

	private static final Rectangle BUTTONS_PANEL_BOUNDS = new Rectangle(408, 366, 160, 42);

	private static final Rectangle FIRST_TOGGLE_BUTTON_BOUNDS = new Rectangle(0, 6, 30, 30);

	private static final Rectangle SECOND_TOGGLE_BUTTON_BOUNDS = new Rectangle(30, 6, 30, 30);

	private static final Rectangle THIRD_TOGGLE_BUTTON_BOUNDS = new Rectangle(60, 6, 30, 30);

	private static final Rectangle FOURTH_TOGGLE_BUTTON_BOUNDS = new Rectangle(90, 6, 30, 30);

	private static final Rectangle FIFTH_TOGGLE_BUTTON_BOUNDS = new Rectangle(120, 6, 30, 30);

	private int index = 1;

	private ButtonGroup buttonGroup;

	private JPanel imagePanel;

	private JPanel buttonsPanel;

	private JToggleButton firstToggleButton;

	private JToggleButton secondToggleButton;

	private JToggleButton thirdToggleButton;

	private JToggleButton fourthToggleButton;

	private JToggleButton fifthToggleButton;

	private Timer timer;

	private Messages messages;

	public MainImagesPanel() {
		initialize();
	}

	private void initialize() {
		this.setPreferredSize(MINIMUM_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.setViewportView(getImagePanel());
	}

	private JPanel getImagePanel() {
		if (imagePanel == null) {
			imagePanel = new JPanel();
			imagePanel.setLayout(null);
			imagePanel.setPreferredSize(MAXIMUM_SIZE);
			timer = new Timer(TIMER_DELAY, new TransitionActionListener());
			timer.setRepeats(true);
			timer.start();
			imagePanel.add(getButtonsPanel());
		}
		return imagePanel;
	}

	private JPanel getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = new JPanel();
			buttonsPanel.setBounds(BUTTONS_PANEL_BOUNDS);
			buttonsPanel.setLayout(null);
			buttonGroup = new ButtonGroup();
			buttonsPanel.add(getFirstToggleButton());
			buttonsPanel.add(getSecondToggleButton());
			buttonsPanel.add(getThirdToggleButton());
			buttonsPanel.add(getFourthToggleButton());
			buttonsPanel.add(getFifthToggleButton());
			addButtonsToGroup();
		}
		return buttonsPanel;
	}

	private void addButtonsToGroup() {
		buttonGroup.add(getFirstToggleButton());
		buttonGroup.add(getSecondToggleButton());
		buttonGroup.add(getThirdToggleButton());
		buttonGroup.add(getFourthToggleButton());
		buttonGroup.add(getFifthToggleButton());
	}

	private JToggleButton getFifthToggleButton() {
		if (fifthToggleButton == null) {
			fifthToggleButton = new JToggleButton();
			fifthToggleButton.setName(MAIN_IMAGE_TOGGLE_BUTTON);
			fifthToggleButton.setBounds(FIFTH_TOGGLE_BUTTON_BOUNDS);
			fifthToggleButton.addActionListener(new ChangeIndexListener(5));
		}
		return fifthToggleButton;
	}

	private JToggleButton getFourthToggleButton() {
		if (fourthToggleButton == null) {
			fourthToggleButton = new JToggleButton();
			fourthToggleButton.setName(MAIN_IMAGE_TOGGLE_BUTTON);
			fourthToggleButton.setBounds(FOURTH_TOGGLE_BUTTON_BOUNDS);
			fourthToggleButton.addActionListener(new ChangeIndexListener(4));
		}
		return fourthToggleButton;
	}

	private JToggleButton getThirdToggleButton() {
		if (thirdToggleButton == null) {
			thirdToggleButton = new JToggleButton();
			thirdToggleButton.setName(MAIN_IMAGE_TOGGLE_BUTTON);
			thirdToggleButton.setBounds(THIRD_TOGGLE_BUTTON_BOUNDS);
			thirdToggleButton.addActionListener(new ChangeIndexListener(3));
		}
		return thirdToggleButton;
	}

	private JToggleButton getSecondToggleButton() {
		if (secondToggleButton == null) {
			secondToggleButton = new JToggleButton();
			secondToggleButton.setName(MAIN_IMAGE_TOGGLE_BUTTON);
			secondToggleButton.setBounds(SECOND_TOGGLE_BUTTON_BOUNDS);
			secondToggleButton.addActionListener(new ChangeIndexListener(2));
		}
		return secondToggleButton;
	}

	private JToggleButton getFirstToggleButton() {
		if (firstToggleButton == null) {
			firstToggleButton = new JToggleButton();
			firstToggleButton.setName(MAIN_IMAGE_TOGGLE_BUTTON);
			firstToggleButton.setBounds(FIRST_TOGGLE_BUTTON_BOUNDS);
			firstToggleButton.addActionListener(new ChangeIndexListener(1));
		}
		return firstToggleButton;
	}

	private class ChangeIndexListener implements ActionListener {
		private int buttonIndex;

		public ChangeIndexListener(int buttonIndex) {
			this.buttonIndex = buttonIndex;
		}

		@Override
		public void actionPerformed(ActionEvent paramActionEvent) {
			changeIndex(buttonIndex);
		}

	}

	private void changeIndex(int i) {
		index = i;
		if (index != MAX_INDEX) {
			index++;
		} else {
			index = 1;
		}
		if (messages != null) {
			internationalize(messages);
		}
	}

	private class TransitionActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			switch (index) {
			case 1:
				getFirstToggleButton().doClick();
				break;
			case 2:
				getSecondToggleButton().doClick();
				break;
			case 3:
				getThirdToggleButton().doClick();
				break;
			case 4:
				getFourthToggleButton().doClick();
				break;
			case 5:
				getFifthToggleButton().doClick();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void internationalize(Messages messages) {
		imagePanel.setName(messages.getMessage("home.MainImagesPanel.name", index));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);

	}

	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
		getFirstToggleButton().doClick();
	}
}