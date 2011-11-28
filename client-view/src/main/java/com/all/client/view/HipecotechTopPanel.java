package com.all.client.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.RepeatType;
import com.all.client.util.TimeUtil;
import com.all.client.util.ViewModelUtils;
import com.all.client.view.actions.BackTrackAction;
import com.all.client.view.actions.ExecutingAction;
import com.all.client.view.actions.NextTrackAction;
import com.all.client.view.i18n.Ji18nTooltip;
import com.all.core.actions.Actions;
import com.all.core.events.ContainerModifiedEvent;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerProgressEvent;
import com.all.core.events.MediaPlayerStateEvent;
import com.all.core.events.MusicPlayerState;
import com.all.core.model.Model;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;

@Component
public class HipecotechTopPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 6660199913223149483L;

	@Autowired
	private PokeballPanel pokeballPanel;
	@Autowired
	private ViewEngine viewEngine;

	private static final Dimension LEFT_VOLUME_MAX_SIZE = new Dimension(30, 61); // @jve:decl-index=0:
	private static final Dimension LEFT_VOLUME_SIZE = new Dimension(12, 61);
	private static final Dimension LEFT_VOLUME_MINIMUM_SIZE = new Dimension(1, 61);
	private static final int VOLUME_HEIGHT = 61;
	private static final String TOP_PANEL_BACKGROUND = "topPanelBackground";
	private static final int MIN_WIDTH = 792;
	private static final int MAX_WIDTH = 1020;
	private static final int HEIGHT = 63;
	private static final Dimension PREFERRED_SIZE = new Dimension(MAX_WIDTH, HEIGHT);
	private static final Dimension MINIMUM_SIZE = new Dimension(MIN_WIDTH, HEIGHT);
	private static final Dimension BACK_BUBBLE_PANEL_MAX_SIZE = new Dimension(999, HEIGHT); // @jve:decl-index=0:
	private static final Dimension BACK_BUBBLE_PANEL_MIN_SIZE = new Dimension(450, HEIGHT);
	private static final Dimension BACK_BUBBLE_PANEL_SIZE = new Dimension(564, HEIGHT);
	private static final Dimension SHORTCUT_PANEL_SIZE = new Dimension(83, HEIGHT);
	private static final Dimension RIGHT_PLAYER_SEPARATOR_SIZE = new Dimension(13, HEIGHT);
	private static final Dimension RIGHT_PLAYER_SEPARATOR_MIN_SIZE = new Dimension(2, HEIGHT);
	private static final Dimension LEFT_PLAYER_SEPARATOR_SIZE = new Dimension(14, HEIGHT);
	private static final Dimension LEFT_PLAYER_SEPARATOR_MAX_SIZE = new Dimension(40, HEIGHT);
	private static final Dimension LEFT_PLAYER_SEPARATOR_MIN_SIZE = new Dimension(3, HEIGHT);
	private static final Dimension FIXED_SEPARATOR_SIZE = new Dimension(8, HEIGHT);
	private static final Dimension VOLUME_PANEL_SIZE = new Dimension(81, VOLUME_HEIGHT);
	private static final Dimension LEFT_SHORTCUT_SEPARATOR_SIZE = new Dimension(17, HEIGHT);
	private static final Dimension LEFT_SHORTCUT_SEPARATOR_MIN_SIZE = new Dimension(2, HEIGHT);

	private static final String PLAY_BUTTON = "playButton";
	public static final String STOP_BUTTON = "stopButton";
	public static final String PAUSE_BUTTON = "pauseButton";
	private static final String BACK_BUTTON = "rewindButton";
	private static final String NEXT_BUTTON = "forwardButton";

	private static final Dimension PLAY_BUTTON_SIZE = new Dimension(34, 38);
	private static final Dimension NEXT_BUTTON_SIZE = new Dimension(29, 20);
	private static final Dimension REPEAT_BUTTON_SIZE = new Dimension(28, 22);
	private static final Dimension SHUFFLE_BUTTON_SIZE = new Dimension(29, 22);
	public static final String PLAY_BUTTON_STYLE = "playButton";
	private static final String SHUFFLE_BUTTON_STYLE = "shuffleButton";
	private static final String REPEAT_BUTTON_STYLE = "repeatButton";
	private static final Insets NEXT_BUTTON_INSETS = new Insets(20, 0, 20, 0);
	private static final Insets PLAY_BUTTON_INSETS = new Insets(12, 0, 11, 0);
	private static final Insets BACK_BUTTON_INSETS = NEXT_BUTTON_INSETS;
	private static final Insets SHUFFLE_BUTTON_INSETS = new Insets(20, 0, 19, 0);
	private static final Dimension MAXIMUM_20 = new Dimension(20, 61);
	private static final Dimension MAXIMUM_40 = new Dimension(40, 61);
	private static final Dimension MAXIMUM_15 = new Dimension(15, 61);
	private static final Dimension PREFERRED = new Dimension(12, 61);
	private static final Dimension MINIMUM = new Dimension(1, 61);
	private JButton backButton;
	private JButton playButton;
	private JButton nextButton;
	private JButton shuffleButton;
	private Ji18nTooltip<JButton> repeatButton;
	private JPanel panelSeparator1;
	private JPanel panelSeparator2;
	private JPanel panelSeparator3;
	private JPanel panelSeparator4;
	private JPanel panelSeparator5;
	private JPanel fixedSeparator;
	private JPanel fixedSeparator2;
	private JPanel leftPlayerSeparator;
	private JPanel rightPlayerSeparator;
	private JPanel leftShortcutSeparator;
	private JPanel rightShortcutSeparator;
	private JPanel shortCutPanel;
	private JPanel leftVolumeSeparator;
	private VolumePanel volumePanel;
	private DisplayBackPanel backBubblePanel;
	private Timer nextButtonTimer;
	private Timer backButtonTimer;
	private int velocity;
	private boolean timerRunning;
	private MusicPlayerState playerState = MusicPlayerState.STOP;
	private TrackContainer playingPlaylist;
	private boolean loggedIn = false;

	private NextTrackAction nextTrackAction;
	private BackTrackAction backTrackAction;
	private boolean mousePressed = false;
	@SuppressWarnings("unchecked")
	private List<Track> selectedTracks = Collections.EMPTY_LIST;

	// private Logger log = Logger.getLogger(HipecotechTopPanel.class);

	@PostConstruct
	public void initialize() {
		this.setName(TOP_PANEL_BACKGROUND);

		this.setLayout(new GridBagLayout());
		this.setMinimumSize(MINIMUM_SIZE);
		this.setSize(PREFERRED_SIZE);
		this.setPreferredSize(PREFERRED_SIZE);

		GridBagConstraints fixedConstraints = new GridBagConstraints();
		fixedConstraints.gridx = 0;
		fixedConstraints.gridy = 0;
		this.add(getFixedSeparatorPanel(), fixedConstraints);

		GridBagConstraints leftVolumeConstraints = new GridBagConstraints();
		leftVolumeConstraints.gridx = 1;
		leftVolumeConstraints.gridy = 0;
		leftVolumeConstraints.weightx = 0.05;
		leftVolumeConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getLeftVolumeSeparatorPanel(), leftVolumeConstraints);

		GridBagConstraints volConstraints = new GridBagConstraints();
		volConstraints.gridx = 2;
		volConstraints.gridy = 0;
		this.add(getVolumePanel(), volConstraints);

		GridBagConstraints volumePlayerSeparatorConstraints = new GridBagConstraints();
		volumePlayerSeparatorConstraints.gridx = 3;
		volumePlayerSeparatorConstraints.gridy = 0;
		volumePlayerSeparatorConstraints.weightx = 0.05;
		volumePlayerSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getVolumePlayerSeparatorPanel(), volumePlayerSeparatorConstraints);

		GridBagConstraints panelSeparator1Constraints = new GridBagConstraints();
		panelSeparator1Constraints.gridx = 4;
		panelSeparator1Constraints.gridy = 0;
		panelSeparator1Constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getPanelSeparator1(), panelSeparator1Constraints);

		GridBagConstraints backButtonConstraints = new GridBagConstraints();
		backButtonConstraints.gridx = 5;
		backButtonConstraints.gridy = 0;
		this.add(getBackButton(), backButtonConstraints);

		GridBagConstraints panelSeparator2Constraints = new GridBagConstraints();
		panelSeparator2Constraints.gridx = 6;
		panelSeparator2Constraints.gridy = 0;
		panelSeparator2Constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getPanelSeparator2(), panelSeparator2Constraints);

		GridBagConstraints playButtonConstraints = new GridBagConstraints();
		playButtonConstraints.gridx = 7;
		playButtonConstraints.gridy = 0;
		this.add(getPlayButton(), playButtonConstraints);

		GridBagConstraints panelSeparator3Constraints = new GridBagConstraints();
		panelSeparator3Constraints.gridx = 8;
		panelSeparator3Constraints.gridy = 0;
		panelSeparator3Constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getPanelSeparator3(), panelSeparator3Constraints);

		GridBagConstraints nextButtonConstraints = new GridBagConstraints();
		nextButtonConstraints.gridx = 9;
		nextButtonConstraints.gridy = 0;
		this.add(getNextButton(), nextButtonConstraints);

		GridBagConstraints panelSeparator4Constraints = new GridBagConstraints();
		panelSeparator4Constraints.gridx = 10;
		panelSeparator4Constraints.gridy = 0;
		panelSeparator4Constraints.weightx = 0.05;
		panelSeparator4Constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getPanelSeparator4(), panelSeparator4Constraints);

		GridBagConstraints shuffleButtonConstraints = new GridBagConstraints();
		shuffleButtonConstraints.gridx = 11;
		shuffleButtonConstraints.gridy = 0;
		this.add(getShuffleButton(), shuffleButtonConstraints);

		GridBagConstraints panelSeparator5Constraints = new GridBagConstraints();
		panelSeparator5Constraints.gridx = 12;
		panelSeparator5Constraints.gridy = 0;
		panelSeparator5Constraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getPanelSeparator5(), panelSeparator5Constraints);

		GridBagConstraints repeatButtonConstraints = new GridBagConstraints();
		repeatButtonConstraints.gridx = 13;
		repeatButtonConstraints.gridy = 0;
		this.add(getRepeatButton().getComponent(), repeatButtonConstraints);
		
		GridBagConstraints leftBubbleSeparatorConstraints = new GridBagConstraints();
		leftBubbleSeparatorConstraints.gridx = 14;
		leftBubbleSeparatorConstraints.gridy = 0;
		leftBubbleSeparatorConstraints.weightx = .05;
		leftBubbleSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getLeftBubbleSeparatorPanel(), leftBubbleSeparatorConstraints);

//		GridBagConstraints nowPlayingButtonConstraints = new GridBagConstraints();
//		nowPlayingButtonConstraints.gridx = 15;
//		nowPlayingButtonConstraints.gridy = 0;
//		this.add(getNowPlayingButton(), nowPlayingButtonConstraints);

		GridBagConstraints backBubbleConstraints = new GridBagConstraints();
		backBubbleConstraints.gridx = 15;
		backBubbleConstraints.gridy = 0;
		backBubbleConstraints.fill = GridBagConstraints.HORIZONTAL;
		backBubbleConstraints.weightx = 0.5;
		this.add(getBackBubblePanel(), backBubbleConstraints);

		GridBagConstraints rightBubbleSeparatorConstraints = new GridBagConstraints();
		rightBubbleSeparatorConstraints.gridx = 16;
		rightBubbleSeparatorConstraints.gridy = 0;
		rightBubbleSeparatorConstraints.weightx = 0.1;
		rightBubbleSeparatorConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(getRightBubbleSeparatorPanel(), rightBubbleSeparatorConstraints);

		GridBagConstraints shorcutPanelConstraints = new GridBagConstraints();
		shorcutPanelConstraints.gridx = 17;
		shorcutPanelConstraints.gridy = 0;
		this.add(getShorcutPanel(), shorcutPanelConstraints);

		GridBagConstraints fixedConstraints2 = new GridBagConstraints();
		fixedConstraints2.gridx = 18;
		fixedConstraints2.gridy = 0;
		this.add(getFixedSeparator2Panel(), fixedConstraints2);
		// recalculateButtons();
	}

	@Autowired
	public final void setVolumePanel(VolumePanel volumePanel) {
		this.volumePanel = volumePanel;

		this.volumePanel.setMinimumSize(VOLUME_PANEL_SIZE);
		this.volumePanel.setSize(VOLUME_PANEL_SIZE);
		this.volumePanel.setPreferredSize(VOLUME_PANEL_SIZE);
		this.volumePanel.setMaximumSize(VOLUME_PANEL_SIZE);
	}

	@Autowired
	public final void setNextTrackAction(NextTrackAction nextTrackAction) {
		this.nextTrackAction = nextTrackAction;
	}

	@Autowired
	public final void setBackTrackAction(BackTrackAction backTrackAction) {
		this.backTrackAction = backTrackAction;
	}

	public final VolumePanel getVolumePanel() {
		if (volumePanel == null) {
			volumePanel = new VolumePanel();
			volumePanel.setMinimumSize(VOLUME_PANEL_SIZE);
			volumePanel.setSize(VOLUME_PANEL_SIZE);
			volumePanel.setPreferredSize(VOLUME_PANEL_SIZE);
			volumePanel.setMaximumSize(VOLUME_PANEL_SIZE);
		}
		return volumePanel;
	}

	public final JPanel getFixedSeparatorPanel() {
		if (fixedSeparator == null) {
			fixedSeparator = new JPanel();
			fixedSeparator.setSize(FIXED_SEPARATOR_SIZE);
			fixedSeparator.setPreferredSize(FIXED_SEPARATOR_SIZE);
			fixedSeparator.setMinimumSize(FIXED_SEPARATOR_SIZE);
			fixedSeparator.setMaximumSize(FIXED_SEPARATOR_SIZE);
		}
		return fixedSeparator;
	}

	public final JPanel getLeftVolumeSeparatorPanel() {
		if (leftVolumeSeparator == null) {
			leftVolumeSeparator = new JPanel();
			leftVolumeSeparator.setMinimumSize(LEFT_VOLUME_MINIMUM_SIZE);
			leftVolumeSeparator.setSize(LEFT_VOLUME_SIZE);
			leftVolumeSeparator.setPreferredSize(LEFT_VOLUME_SIZE);
			leftVolumeSeparator.setMaximumSize(LEFT_VOLUME_MAX_SIZE);
		}
		return leftVolumeSeparator;
	}

	public final JPanel getVolumePlayerSeparatorPanel() {
		if (leftPlayerSeparator == null) {
			leftPlayerSeparator = new JPanel();
			leftPlayerSeparator.setSize(LEFT_PLAYER_SEPARATOR_SIZE);
			leftPlayerSeparator.setPreferredSize(LEFT_PLAYER_SEPARATOR_SIZE);
			leftPlayerSeparator.setMinimumSize(LEFT_PLAYER_SEPARATOR_MIN_SIZE);
			leftPlayerSeparator.setMaximumSize(LEFT_PLAYER_SEPARATOR_MAX_SIZE);
		}
		return leftPlayerSeparator;
	}

	public final JPanel getLeftBubbleSeparatorPanel() {
		if (rightPlayerSeparator == null) {
			rightPlayerSeparator = new JPanel();
			rightPlayerSeparator.setMinimumSize(RIGHT_PLAYER_SEPARATOR_MIN_SIZE);
			rightPlayerSeparator.setSize(RIGHT_PLAYER_SEPARATOR_SIZE);
			rightPlayerSeparator.setPreferredSize(RIGHT_PLAYER_SEPARATOR_SIZE);
		}
		return rightPlayerSeparator;
	}

	public final JPanel getShorcutPanel() {
		if (shortCutPanel == null) {
			shortCutPanel = new JPanel();
			shortCutPanel.setLayout(null);
			shortCutPanel.setMinimumSize(SHORTCUT_PANEL_SIZE);
			shortCutPanel.setSize(SHORTCUT_PANEL_SIZE);
			shortCutPanel.setPreferredSize(SHORTCUT_PANEL_SIZE);
			shortCutPanel.setMaximumSize(SHORTCUT_PANEL_SIZE);
			shortCutPanel.add(pokeballPanel);
		}
		return shortCutPanel;
	}

	@Autowired
	public final void setBackBubblePanel(DisplayBackPanel backBubblePanel) {
		this.backBubblePanel = backBubblePanel;

		backBubblePanel.setMinimumSize(BACK_BUBBLE_PANEL_MIN_SIZE);
		backBubblePanel.setSize(BACK_BUBBLE_PANEL_SIZE);
		backBubblePanel.setPreferredSize(BACK_BUBBLE_PANEL_SIZE);
		backBubblePanel.setMaximumSize(BACK_BUBBLE_PANEL_MAX_SIZE);
		backBubblePanel.setName(TOP_PANEL_BACKGROUND);
	}

	public final JPanel getBackBubblePanel() {
		if (backBubblePanel == null) {
			backBubblePanel = new DisplayBackPanel();
			backBubblePanel.setMinimumSize(BACK_BUBBLE_PANEL_MIN_SIZE);
			backBubblePanel.setSize(BACK_BUBBLE_PANEL_SIZE);
			backBubblePanel.setPreferredSize(BACK_BUBBLE_PANEL_SIZE);
			backBubblePanel.setMaximumSize(BACK_BUBBLE_PANEL_MAX_SIZE);
			backBubblePanel.setName(TOP_PANEL_BACKGROUND);
		}
		return backBubblePanel;
	}

	public final JPanel getRightBubbleSeparatorPanel() {
		if (leftShortcutSeparator == null) {
			leftShortcutSeparator = new JPanel();
			leftShortcutSeparator.setMinimumSize(LEFT_SHORTCUT_SEPARATOR_MIN_SIZE);
			leftShortcutSeparator.setSize(LEFT_SHORTCUT_SEPARATOR_SIZE);
			leftShortcutSeparator.setPreferredSize(LEFT_SHORTCUT_SEPARATOR_SIZE);
		}
		return leftShortcutSeparator;
	}

	public final JPanel getRightShortcutSeparatorPanel() {
		if (rightShortcutSeparator == null) {
			rightShortcutSeparator = new JPanel();
			rightShortcutSeparator.setMinimumSize(new Dimension(2, 61));
			rightShortcutSeparator.setSize(new Dimension(12, 61));
			rightShortcutSeparator.setPreferredSize(new Dimension(12, 61));
			rightShortcutSeparator.setMaximumSize(new Dimension(30, 61));
		}
		return rightShortcutSeparator;
	}

	public final JPanel getFixedSeparator2Panel() {
		if (fixedSeparator2 == null) {
			fixedSeparator2 = new JPanel();
			fixedSeparator2.setSize(FIXED_SEPARATOR_SIZE);
			fixedSeparator2.setPreferredSize(FIXED_SEPARATOR_SIZE);
			fixedSeparator2.setMinimumSize(FIXED_SEPARATOR_SIZE);
			fixedSeparator2.setMaximumSize(FIXED_SEPARATOR_SIZE);
		}
		return fixedSeparator2;
	}

	public final JButton getBackButton() {
		if (backButton == null) {
			backButton = new JButton();
			backButton.setName(BACK_BUTTON);
			backButton.setMargin(BACK_BUTTON_INSETS);
			backButton.setMinimumSize(NEXT_BUTTON_SIZE);
			backButton.setSize(NEXT_BUTTON_SIZE);
			backButton.setPreferredSize(NEXT_BUTTON_SIZE);
			backButton.setMaximumSize(NEXT_BUTTON_SIZE);
		}
		return backButton;
	}

	public final JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton();
			playButton.setName(PLAY_BUTTON_STYLE);
			playButton.setMargin(PLAY_BUTTON_INSETS);
			playButton.setMinimumSize(PLAY_BUTTON_SIZE);
			playButton.setSize(PLAY_BUTTON_SIZE);
			playButton.setPreferredSize(PLAY_BUTTON_SIZE);
			playButton.setMaximumSize(PLAY_BUTTON_SIZE);
		}
		return playButton;
	}

	public final JButton getNextButton() {
		if (nextButton == null) {
			nextButton = new JButton();
			nextButton.setName(NEXT_BUTTON);
			nextButton.setMargin(NEXT_BUTTON_INSETS);
			nextButton.setMinimumSize(NEXT_BUTTON_SIZE);
			nextButton.setSize(NEXT_BUTTON_SIZE);
			nextButton.setPreferredSize(NEXT_BUTTON_SIZE);
			nextButton.setMaximumSize(NEXT_BUTTON_SIZE);
		}
		return nextButton;
	}

	public final JButton getShuffleButton() {
		if (shuffleButton == null) {
			shuffleButton = new JButton();
			shuffleButton.setName(SHUFFLE_BUTTON_STYLE);
			shuffleButton.setMargin(SHUFFLE_BUTTON_INSETS);
			shuffleButton.setMinimumSize(SHUFFLE_BUTTON_SIZE);
			shuffleButton.setSize(SHUFFLE_BUTTON_SIZE);
			shuffleButton.setPreferredSize(SHUFFLE_BUTTON_SIZE);
			shuffleButton.setMaximumSize(SHUFFLE_BUTTON_SIZE);
		}
		return shuffleButton;
	}

	public final Ji18nTooltip<JButton> getRepeatButton() {
		if (repeatButton == null) {
			JButton button = new JButton();
			button.setName(REPEAT_BUTTON_STYLE);
			button.setMargin(SHUFFLE_BUTTON_INSETS);
			button.setMinimumSize(REPEAT_BUTTON_SIZE);
			button.setSize(REPEAT_BUTTON_SIZE);
			button.setPreferredSize(REPEAT_BUTTON_SIZE);
			button.setMaximumSize(REPEAT_BUTTON_SIZE);
			repeatButton = new Ji18nTooltip<JButton>(button);
		}
		return repeatButton;
	}
	
	public final JPanel getPanelSeparator1() {
		if (panelSeparator1 == null) {
			panelSeparator1 = new JPanel();
			panelSeparator1.setMinimumSize(MINIMUM);
			panelSeparator1.setSize(PREFERRED);
			panelSeparator1.setPreferredSize(PREFERRED);
			panelSeparator1.setMaximumSize(MAXIMUM_20);
		}
		return panelSeparator1;
	}

	public final JPanel getPanelSeparator2() {
		if (panelSeparator2 == null) {
			panelSeparator2 = new JPanel();
			panelSeparator2.setMinimumSize(MINIMUM);
			panelSeparator2.setSize(PREFERRED);
			panelSeparator2.setPreferredSize(PREFERRED);
			panelSeparator2.setMaximumSize(MAXIMUM_15);
		}
		return panelSeparator2;
	}

	public final JPanel getPanelSeparator3() {
		if (panelSeparator3 == null) {
			panelSeparator3 = new JPanel();
			panelSeparator3.setMinimumSize(MINIMUM);
			panelSeparator3.setSize(PREFERRED);
			panelSeparator3.setPreferredSize(PREFERRED);
			panelSeparator3.setMaximumSize(MAXIMUM_15);
		}
		return panelSeparator3;
	}

	public final JPanel getPanelSeparator4() {
		if (panelSeparator4 == null) {
			panelSeparator4 = new JPanel();
			panelSeparator4.setMinimumSize(MINIMUM);
			panelSeparator4.setSize(PREFERRED);
			panelSeparator4.setPreferredSize(PREFERRED);
			panelSeparator4.setMaximumSize(MAXIMUM_40);
		}
		return panelSeparator4;
	}

	public final JPanel getPanelSeparator5() {
		if (panelSeparator5 == null) {
			panelSeparator5 = new JPanel();
			panelSeparator5.setMinimumSize(MINIMUM);
			panelSeparator5.setSize(PREFERRED);
			panelSeparator5.setPreferredSize(PREFERRED);
			panelSeparator5.setMaximumSize(MAXIMUM_15);
		}
		return panelSeparator5;
	}

	@PostConstruct
	public final void setup() {
		nextButtonTimer = new Timer(TimeUtil.ONE_SECOND, new TimerVelocityListener(true));
		backButtonTimer = new Timer(TimeUtil.ONE_SECOND, new TimerVelocityListener(false));
		getBackButton().addMouseListener(new PlayerActionMouseAdapter(backTrackAction, backButton, backButtonTimer));
		getNextButton().addMouseListener(new PlayerActionMouseAdapter(nextTrackAction, nextButton, nextButtonTimer));
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onApplicationStarted() {
		updateRepeatButton(viewEngine.get(Model.UserPreference.PLAYER_REPEAT_MODE));
		updateShuffleButton(viewEngine.get(Model.UserPreference.PLAYER_SHUFFLE_MODE));
	}

	@EventMethod(Events.View.SELECTED_TRACKCONTAINER_CHANGED_ID)
	public void onSelectedItemChanged() {
		recalculateButtons();
	}

	@EventMethod(Events.Library.CONTAINER_MODIFIED_ID)
	public void onContainerModified(ContainerModifiedEvent eventArgs) {
		recalculateButtons();
	}

	@EventMethod(Events.Player.STATE_CHANGED_ID)
	public void onPlayerStateChanged(MediaPlayerStateEvent eventArgs) {
		playerState = eventArgs.getState();
		initializeButton();
		recalculateButtons();
	}

	@EventMethod(Events.Player.PROGRESS_CHANGED_ID)
	public void onPlayerProgressChanged(MediaPlayerProgressEvent event) {
		if (event.getCurrentTime() == 0 && mousePressed) {
			backButton.setEnabled(false);
			backButton.setEnabled(true);
			initializeButton();
		}
	}

	@EventMethod(Events.Player.PLAYING_PLAYLIST_CHANGED_ID)
	public void onPlayerPlaycountChanged(ValueEvent<TrackContainer> eventArgs) {
		playingPlaylist = eventArgs.getValue();
		recalculateButtons();
	}

	private void initializeButton() {
		nextButtonTimer.stop();
		backButtonTimer.stop();
		velocity = 1;
		if (mousePressed) {
			// Next button needs to be disabled and enabled again in order to
			// set its initial graphical state. This applies when fast
			// forwarding.
			nextButton.setEnabled(false);
			nextButton.setEnabled(true);
		}
	}

	private void recalculateButtons() {
		if (!loggedIn) {
			getShuffleButton().setEnabled(false);
			getRepeatButton().getComponent().setEnabled(false);
			getVolumePanel().enableControls(false);
			getBackButton().setEnabled(false);
			getPlayButton().setEnabled(false);
			getNextButton().setEnabled(false);
			return;
		}
		getShuffleButton().setEnabled(true);
		getRepeatButton().getComponent().setEnabled(true);
		getVolumePanel().enableControls(true);
		getPlayButton().setEnabled(true);
		TrackContainer selectedContainer = viewEngine.get(Model.SELECTED_CONTAINER);
		switch (playerState) {
		case PLAYING:
			getNextButton().setEnabled(true);
			getBackButton().setEnabled(true);
			if (selectedContainer.equals(playingPlaylist)) {
				getPlayButton().setName(PAUSE_BUTTON);
			} else {
				getPlayButton().setName(STOP_BUTTON);
			}
			break;
		case PAUSE:
			getNextButton().setEnabled(true);
			getBackButton().setEnabled(true);
			if (selectedContainer.equals(playingPlaylist)) {
				getPlayButton().setName(PLAY_BUTTON);
			} else {
				getPlayButton().setName(STOP_BUTTON);
			}
			break;
		case STOP:
			getPlayButton().setName(PLAY_BUTTON);
			if (selectedContainer != null && !selectedContainer.getTracks().iterator().hasNext()) {
				getPlayButton().setEnabled(false);
			}
			getNextButton().setEnabled(false);
			getBackButton().setEnabled(false);
			break;
		}
		if (PLAY_BUTTON.equals(getPlayButton().getName())) {
			getPlayButton().setEnabled(isPlayEnabled());
		}
	}

	private boolean isPlayEnabled() {
		if (selectedTracks == null || selectedTracks.isEmpty()) {
			return false;
		}
		for (Track track : selectedTracks) {
			if (track.isEnabled() && viewEngine.get(Model.TRACK_REPOSITORY).isLocallyAvailable(track.getHashcode())
					&& ViewModelUtils.isBrowsingLocalLibrary(viewEngine)) {
				return true;
			}
		}
		return false;
	}

	private void changeVelocity() {
		viewEngine.sendValueAction(Actions.Player.CHANGE_VELOCITY, velocity);
	}

	private void startTimer(Timer timer) {
		timer.start();
	}

	private void stopTimerOrExecuteAction(Timer timer, ExecutingAction action) {
		timer.stop();
		if (mousePressed) {
			if (this.timerRunning) {
				velocity = 1;
				changeVelocity();
			} else {
				action.execute();
			}
		}
		this.timerRunning = false;
	}

	private class TimerVelocityListener implements ActionListener {
		boolean forward;

		public TimerVelocityListener(boolean forward) {
			this.forward = forward;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (forward) {
				++velocity;
			} else {
				if (velocity > 0) {
					velocity *= -1;
				} else {
					--velocity;
				}
			}
			timerRunning = true;
			changeVelocity();
		}
	}

	private final class PlayerActionMouseAdapter extends MouseAdapter {
		private boolean mouseExited;

		private Timer timer;

		private ExecutingAction action;

		private JButton button;

		public PlayerActionMouseAdapter(ExecutingAction action, JButton button, Timer timer) {
			super();
			this.action = action;
			this.button = button;
			this.timer = timer;
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (mousePressed && !mouseExited) {
				doStop();
			}

			mousePressed = false;
			mouseExited = false;
		}

		private void doStop() {
			if (button.isEnabled()) {
				stopTimerOrExecuteAction(timer, action);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mousePressed = true;
			mouseExited = false;
			doStart(e);
		}

		private void doStart(MouseEvent e) {
			if (button.isEnabled() && button.contains(e.getPoint())) {
				startTimer(timer);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (mousePressed) {
				doStop();
				mouseExited = true;
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (mouseExited && mousePressed) {
				doStart(e);
			}
			mouseExited = false;
		}
	}

	@Autowired
	public final void setMusicPlayerController() {
		getRepeatButton().getComponent().addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.request(Actions.Player.REQUEST_TOGGLE_REPEAT, null, new ResponseCallback<RepeatType>() {

					@Override
					public void onResponse(RepeatType type) {
						updateRepeatButton(type);
					}
				});
			}
		});

		getShuffleButton().addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEngine.request(Actions.Player.REQUEST_TOGGLE_SHUFFLE, new ResponseCallback<Boolean>() {

					@Override
					public void onResponse(Boolean type) {
						updateShuffleButton(type);
					}
				});
			}
		});
		getPlayButton().addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (playButton.getName().equals(PLAY_BUTTON_STYLE)) {
					// musicPlayerController.play();
					viewEngine.send(Actions.Player.PLAY);
				} else if (playButton.getName().equals(STOP_BUTTON)) {
					// musicPlayerController.stop();
					viewEngine.send(Actions.Player.STOP);
				} else if (playButton.getName().equals(PAUSE_BUTTON)) {
					// musicPlayerController.pause();
					viewEngine.send(Actions.Player.PAUSE);
					playButton.setName(PLAY_BUTTON_STYLE);
				}
			}
		});
		// musicPlayerController.onSelectedTracksChanged().add(
		// new Observer<ObservePropertyChanged<MusicPlayerController,
		// List<Track>>>() {
		// @Override
		// public void observe(ObservePropertyChanged<MusicPlayerController,
		// List<Track>> eventArgs) {
		// HipecotechTopPanel.this.selectedTracks = eventArgs.getValue();
		// recalculateButtons();
		// }
		// });
	}

	@SuppressWarnings("unused")
	@EventMethod(Events.Player.SELECTED_TRACKS_CHANGED_ID)
	private void onSelectedTracksChanged(List<Track> trackList) {
		HipecotechTopPanel.this.selectedTracks = trackList;
		recalculateButtons();
	}

	private void updateRepeatButton(RepeatType repeat) {
		getRepeatButton().getComponent().setName(repeat.synth());
		repeatButton.setTooltipMessage(repeat.getTooltipSynth());
	}

	private void updateShuffleButton(boolean shuffle) {
		if (shuffle) {
			getShuffleButton().setName("shuffleButtonOn");
		} else {
			getShuffleButton().setName("shuffleButton");
		}
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onAppStarted() {
		loggedIn = true;
		recalculateButtons();
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onUserLogout() {
		loggedIn = false;
		recalculateButtons();
	}

	@Override
	public void internationalize(Messages messages) {
		getShuffleButton().setToolTipText(messages.getMessage("tooltip.shuffle"));
		getBackButton().setToolTipText(messages.getMessage("tooltip.previous"));
		getNextButton().setToolTipText(messages.getMessage("tooltip.next"));
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
		getVolumePanel().removeMessages(messages);
		getRepeatButton().removeMessages(messages);
	}

	@Override
	@Autowired
	public final void setMessages(Messages messages) {
		messages.add(this);
		getVolumePanel().setMessages(messages);
		getRepeatButton().setMessages(messages);
	}

	public final void playOnShorCut() {
		if (getPlayButton().getName().equals(HipecotechTopPanel.PLAY_BUTTON_STYLE)) {
			viewEngine.send(Actions.Player.PLAY);
		} else if (getPlayButton().getName().equals(HipecotechTopPanel.STOP_BUTTON)) {
			viewEngine.send(Actions.Player.STOP);
		} else if (getPlayButton().getName().equals(HipecotechTopPanel.PAUSE_BUTTON)) {
			viewEngine.send(Actions.Player.PAUSE);
			getPlayButton().setName(HipecotechTopPanel.PLAY_BUTTON_STYLE);
		}
	}

	public final void nextTrack() {
		getNextButton().doClick();
		nextTrackAction.execute();
	}

	public final void prevTrack() {
		getBackButton().doClick();
		backTrackAction.execute();
	}

}
