package com.all.client.view;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.VolumeSlider;
import com.all.core.actions.Actions;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

@Component
public class VolumePanel extends JPanel implements Internationalizable {

	private static final int VALUE = 50;

	private static final int MINIMUM = 0;

	private static final int MAXIMUM = 100;

	private static final Rectangle VOLUME_SLIDER_BOUNDS = new Rectangle(0, 10, 81, 41);

	private static final Dimension MUTE_BUTTON_DEFAULT_SIZE = new Dimension(12, 10);

	private static final Rectangle MUTE_BUTTON_BOUNDS = new Rectangle(31, 34, 18, 18);

	private static final Dimension DEFAULT_SIZE = new Dimension(81, 61);

	private static final long serialVersionUID = -675369135301499539L;

	private static final Dimension MINIMUM_SIZE = new Dimension(81, 41);
	public static final String VOLUME_MUTE = "volumeMute"; // @jve:decl-index=0:
	public static final String VOLUME_MID = "volumeMid"; // @jve:decl-index=0:
	public static final String VOLUME_FULL = "volumeFull"; // @jve:decl-index=0:
	private JButton muteButton;
	private JSlider volumeSlider;

	private ViewEngine viewEngine;

	public VolumePanel() {
		super();
		initialize();
	}

	private void initialize() {
		this.setLayout(null);
		this.setSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(DEFAULT_SIZE);
		this.add(getMuteButton());
		this.add(getVolumeSlider());
		muteButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				viewEngine.send(Actions.Player.TOGGLE_MUTE);
			}
		});
		volumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				viewEngine.sendValueAction(Actions.Player.UPDATE_VOLUME, new Integer(volumeSlider.getValue()));
			}
		});
	}

	private JButton getMuteButton() {
		if (muteButton == null) {
			muteButton = new JButton();
			muteButton.setBounds(MUTE_BUTTON_BOUNDS);
			muteButton.setName(VOLUME_MID);
			muteButton.setMinimumSize(MUTE_BUTTON_DEFAULT_SIZE);
			muteButton.setPreferredSize(MUTE_BUTTON_DEFAULT_SIZE);
			muteButton.setMaximumSize(MUTE_BUTTON_DEFAULT_SIZE);
		}
		return muteButton;
	}

	/**
	 * This method initializes volumeSlider
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getVolumeSlider() {
		if (volumeSlider == null) {
			volumeSlider = new VolumeSlider();
			volumeSlider.setMinimumSize(MINIMUM_SIZE);
			volumeSlider.setPreferredSize(MINIMUM_SIZE);
			volumeSlider.setMaximumSize(MINIMUM_SIZE);
			volumeSlider.setMaximum(MAXIMUM);
			volumeSlider.setMinimum(MINIMUM);
			volumeSlider.setValue(VALUE);
			volumeSlider.setBounds(VOLUME_SLIDER_BOUNDS);
		}
		return volumeSlider;
	}

	@EventMethod(Events.Player.VOLUME_CHANGED_ID)
	public void onPlayerVolumeChanged(ValueEvent<Integer> eventArgs) {
		if (eventArgs.getValue() < 3) {
			muteButton.setName(VOLUME_MUTE);
		} else if (eventArgs.getValue() < 51) {
			muteButton.setName(VOLUME_MID);
		} else {
			muteButton.setName(VOLUME_FULL);
		}
		if (!volumeSlider.getValueIsAdjusting()) {
			volumeSlider.setValue(eventArgs.getValue());
		}
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void setup() {
		getVolumeSlider().setValue(viewEngine.get(Model.UserPreference.PLAYER_VOLUME));
	}

	public void enableControls(boolean enabled) {
		this.volumeSlider.setEnabled(enabled);
		this.muteButton.setEnabled(enabled);
	}

	public void volumeDown() {
		if (this.volumeSlider.getValue() >= (this.volumeSlider.getMinimum() + 5)) {
			this.volumeSlider.grabFocus();
			int newValue = this.volumeSlider.getValue() - 5;
			this.volumeSlider.setValue(newValue);
			this.volumeSlider.validate();
			// value = newValue;
		}
	}

	public void volumeUp() {
		if (this.volumeSlider.getValue() <= (this.volumeSlider.getMaximum() - 5)) {
			this.volumeSlider.repaint();
			int newValue = this.volumeSlider.getValue() + 5;
			this.volumeSlider.setValue(newValue);
			this.volumeSlider.validate();
			// value = newValue;
		}
	}

	public void volumeMute() {
		// if(value!=this.volumeSlider.getMinimum()){
		this.volumeSlider.setValue(this.volumeSlider.getMinimum());
		// }else{
		// this.volumeSlider.setValue(value);
		// }
	}

	@Override
	public void internationalize(Messages messages) {
		getMuteButton().setToolTipText("tooltip.mute");
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Autowired
	public void setViewEngine(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}
}
