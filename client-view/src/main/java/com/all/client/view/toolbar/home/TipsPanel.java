package com.all.client.view.toolbar.home;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public final class TipsPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final int MAX_INDEX = 3;

	private static final int TIMER_DELAY = 8000;

	private static final Dimension DEFAULT_SIZE = new Dimension(200, 402);

	private int index = 1;

	private Timer timer;

	private Messages messages;

	public TipsPanel() {
		initialize();
	}

	private void initialize() {
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		timer = new Timer(TIMER_DELAY, new TransitionActionListener());
		timer.setRepeats(true);
		timer.start();
	}

	private void changeIndex() {
		if (messages != null) {
			internationalize(messages);
		}
		if (index != MAX_INDEX) {
			index++;
		} else {
			index = 1;
		}
	}

	private class TransitionActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			changeIndex();
		}

	}

	@Override
	public void internationalize(Messages messages) {
		setName(messages.getMessage("home.TipsPanel.name", index));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
		changeIndex();
	}

}
