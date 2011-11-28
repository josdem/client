package com.all.client.view;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.appControl.ViewEngineConfigurator;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.Actions;
import com.all.core.common.model.ApplicationModel;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerStateEvent;
import com.all.core.events.MusicPlayerState;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Track;

@Component
public class DisplayContainerPanel extends JPanel implements Internationalizable {
	private static final long serialVersionUID = 3077901577783143949L;

	private static final Dimension CENTER_PANEL_DEFAULT_SIZE = new Dimension(294, 82);

	private static final Dimension CENTER_PANEL_MAXIMUM_SIZE = new Dimension(999, 82);

	private static final Dimension CENTER_PANEL_MINIMUM_SIZE = new Dimension(150, 82);

	private static final Dimension DEFAULT_BUTTON_SIZE = new Dimension(42, 42);

	private static final Dimension LEFT_BUBBLE_SIDE_DEFAULT_SIZE = new Dimension(135, 82);

	private static final Dimension MINIMUM_SIZE = new Dimension(450, 82);

	private static final Dimension PREFERRED_SIZE = new Dimension(564, 82);

	private static final Dimension RIGHT_BUBBLE_DEFAULT_SIZE = new Dimension(135, 82);

	private static final Point PULSE_BUBBLE_BUTTON_LOCATION = new Point(7, 28);

	private static final Rectangle DATA_BUBBLE_BUTTON_BOUNDS = new Rectangle(87, 28, 42, 42);

	private static final Rectangle NOW_PLAYING_BUTTON_BOUNDS = new Rectangle(18, 40, 34, 22);
	
	private static final String NOW_PLAYING_BUTTON_NAME = "nowPlayingButton";

	private static final Rectangle BOUNDS = new Rectangle(355, 0, 564, 82);

	private static final String BUBBLE_CENTER = "BubbleCenter";

	private static final String BUBBLE_LEFT = "BubbleLeft";

	private static final String BUBBLE_RIGHT = "BubbleRight";

	// TODO: review why is the reason to use two images instead enable and disable
	// button
	private static final String ACTIVE_TWITTER_BUTTON = "twitterBubbleButton";

	private static final String GRAY_TWITTER_BUTTON = "GrayTwitterBubbleButton";

	private static final String INFO_PLAYER_PANEL = "InfoPlayerPanel";

	private static final String LOGO_PANEL = "LogoPanel";

	private CardLayout cardLayout;

	@Autowired
	private InfoPlayerPanel infoPlayerPanel;

	@Autowired
	private LogoPanel logoPanel;

	@Autowired
	private ViewEngine viewEngine;
	@Autowired
	private DialogFactory dialogFactory;

	private JButton twitterBubbleButton;

	private PulseBubbleButton pulseBubbleButton;

	private JPanel centerPanel;

	private JPanel leftBubbleSide;

	private JPanel rightBubbleSide;

	private Messages messages;
	
	private JButton nowPlayingButton;;

	public DisplayContainerPanel() {
		this.setLayout(new GridBagLayout());
	}

	@PostConstruct
	public void initialize() {
		this.setBounds(BOUNDS);

		this.setPreferredSize(PREFERRED_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);

		GridBagConstraints leftBubbleSideConstraint = new GridBagConstraints();
		leftBubbleSideConstraint.gridx = 0;
		leftBubbleSideConstraint.gridy = 0;
		this.add(getLeftBubbleSide(), leftBubbleSideConstraint);

		GridBagConstraints centerBubbleConstraint = new GridBagConstraints();
		centerBubbleConstraint.gridx = 1;
		centerBubbleConstraint.gridy = 0;
		centerBubbleConstraint.fill = GridBagConstraints.HORIZONTAL;
		centerBubbleConstraint.weightx = 1;
		centerPanel = new JPanel();
		centerPanel.setSize(CENTER_PANEL_DEFAULT_SIZE);
		centerPanel.setPreferredSize(CENTER_PANEL_DEFAULT_SIZE);
		centerPanel.setMinimumSize(CENTER_PANEL_MINIMUM_SIZE);
		centerPanel.setMaximumSize(CENTER_PANEL_MAXIMUM_SIZE);
		cardLayout = new CardLayout();
		infoPlayerPanel.setName(BUBBLE_CENTER);
		centerPanel.setLayout(cardLayout);
		centerPanel.add(logoPanel, LOGO_PANEL);
		centerPanel.add(infoPlayerPanel, INFO_PLAYER_PANEL);
		this.add(centerPanel, centerBubbleConstraint);

		GridBagConstraints rightBubbleSideConstraint = new GridBagConstraints();
		rightBubbleSideConstraint.gridx = 2;
		rightBubbleSideConstraint.gridy = 0;
		this.add(getRightBubbleSide(), rightBubbleSideConstraint);
		enableControls(false);
	}

	@Autowired
	public void setAppControlConfigurer(ViewEngineConfigurator configurer) {
		// TODO: as we setup we should eventually reset.
		configurer.setupViewEngine(this);
	}

	@EventMethod(Events.Player.STATE_CHANGED_ID)
	public void handleEvent(MediaPlayerStateEvent event) {
		MusicPlayerState state = event.getState();
		boolean isVisible = (state == MusicPlayerState.PLAYING || state == MusicPlayerState.PAUSE);
		if (isVisible) {
			cardLayout.show(centerPanel, INFO_PLAYER_PANEL);
			getTwitterBubbleButton().setName(ACTIVE_TWITTER_BUTTON);
		} else {
			cardLayout.show(centerPanel, LOGO_PANEL);
			getTwitterBubbleButton().setName(GRAY_TWITTER_BUTTON);
		}
		getTwitterBubbleButton().setEnabled(isVisible);
		getNowPlayingButton().setEnabled(isVisible);
		revalidate();
	}

	public JPanel getCenterPanel() {
		return centerPanel;
	}

	@EventMethod(Model.DRAWER_DISPLAYED_ID)
	public void onDrawerDisplayedChanged(ValueEvent<Boolean> eventArgs) {
		if (eventArgs.getValue()) {
			getPulseBubbleButton().stopAnimation();
		}
	}

	@EventMethod(Events.Alerts.NEW_ALERT_ID)
	public void onNewAlerts() {
		getPulseBubbleButton().animate();
	}
	
	@EventMethod(Model.CURRENT_ALERTS_ID)
	public void onCurrentAlertsChanged() {
		getPulseBubbleButton().updateStatus();
	}



	public JPanel getLeftBubbleSide() {
		if (leftBubbleSide == null) {
			leftBubbleSide = new JPanel();
			leftBubbleSide.setLayout(null);
			leftBubbleSide.setMinimumSize(LEFT_BUBBLE_SIDE_DEFAULT_SIZE);
			leftBubbleSide.setSize(LEFT_BUBBLE_SIDE_DEFAULT_SIZE);
			leftBubbleSide.setPreferredSize(LEFT_BUBBLE_SIDE_DEFAULT_SIZE);
			leftBubbleSide.setMaximumSize(LEFT_BUBBLE_SIDE_DEFAULT_SIZE);
			leftBubbleSide.setName(BUBBLE_LEFT);
			leftBubbleSide.add(getNowPlayingButton());
			leftBubbleSide.add(getTwitterBubbleButton());
		}
		return leftBubbleSide;
	}

	private final JButton getNowPlayingButton(){
		if(nowPlayingButton == null){
		    nowPlayingButton = new JButton();	
		    nowPlayingButton.setBounds(NOW_PLAYING_BUTTON_BOUNDS);
		    nowPlayingButton.setName(NOW_PLAYING_BUTTON_NAME);
		    nowPlayingButton.setEnabled(false);
		    nowPlayingButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					viewEngine.send(Actions.View.SELECT_PLAYING_TRACKCONTAINER);
				}
			});
		}
		return nowPlayingButton;
	}
	
	private JButton getTwitterBubbleButton() {
		if (twitterBubbleButton == null) {
			twitterBubbleButton = new JButton();
			twitterBubbleButton.setName(GRAY_TWITTER_BUTTON);
			twitterBubbleButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
			twitterBubbleButton.setMaximumSize(DEFAULT_BUTTON_SIZE);
			twitterBubbleButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
			twitterBubbleButton.setBounds(DATA_BUBBLE_BUTTON_BOUNDS);
			twitterBubbleButton.setVisible(true);
			twitterBubbleButton.setEnabled(false);
			twitterBubbleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (dialogFactory.isTwitterLoggedIn()) {
						viewEngine.request(Actions.Player.REQUEST_CURRENT_TRACK, new ResponseCallback<Track>() {
							public void onResponse(Track currentTrack) {
								dialogFactory.showTwitterNowPlayingDialog(currentTrack);
							}
						});
					}
				}
			});
		}
		return twitterBubbleButton;
	}

	public InfoPlayerPanel getInfoPlayerPanel() {
		return infoPlayerPanel;
	}

	public JPanel getRightBubbleSide() {
		if (rightBubbleSide == null) {

			rightBubbleSide = new JPanel();
			rightBubbleSide.setLayout(null);
			rightBubbleSide.setMinimumSize(RIGHT_BUBBLE_DEFAULT_SIZE);
			rightBubbleSide.setSize(RIGHT_BUBBLE_DEFAULT_SIZE);
			rightBubbleSide.setPreferredSize(RIGHT_BUBBLE_DEFAULT_SIZE);
			rightBubbleSide.setMaximumSize(RIGHT_BUBBLE_DEFAULT_SIZE);
			rightBubbleSide.setName(BUBBLE_RIGHT);
			rightBubbleSide.add(getPulseBubbleButton());
		}
		return rightBubbleSide;
	}

	private PulseBubbleButton getPulseBubbleButton() {
		if (pulseBubbleButton == null) {
			pulseBubbleButton = new PulseBubbleButton(viewEngine);
			pulseBubbleButton.setBounds(new Rectangle(PULSE_BUBBLE_BUTTON_LOCATION, DEFAULT_BUTTON_SIZE));
			pulseBubbleButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					showAlerts();
				}
			});
		}
		return pulseBubbleButton;
	}

	private void enableControls(boolean enabled) {
		getPulseBubbleButton().setEnabled(enabled);
		if (enabled) {
			getPulseBubbleButton().setToolTipText(messages.getMessage("tooltip.pulseOff"));
		} else {
			getPulseBubbleButton().setToolTipText(messages.getMessage("tooltip.pulseDisabled"));
		}
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onAppStarted() {
		if (viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION)) {
			enableControls(true);
		}
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onUserLogout() {
		getPulseBubbleButton().stopAnimation();
		enableControls(false);
	}

	@EventMethod(ApplicationModel.HAS_INTERNET_CONNECTION_ID)
	public void onInternetConnectionStatusChanged(ValueEvent<Boolean> event) {
		if (!event.getValue()) {
			viewEngine.send(Actions.View.HIDE_DRAWER);
			twitterBubbleButton.setName(GRAY_TWITTER_BUTTON);
			twitterBubbleButton.setEnabled(false);
		}
	}

	private void showAlerts() {
		viewEngine.send(Actions.View.TOGGLE_DRAWER);
	}

	@Override
	public void internationalize(Messages messages) {
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Autowired
	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

}
