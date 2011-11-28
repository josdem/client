package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.annotation.PostConstruct;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.util.Formatters;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.events.MediaPlayerProgressEvent;
import com.all.core.events.MediaPlayerTrackPlayedEvent;
import com.all.event.EventMethod;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Track;

@Component
public class InfoPlayerPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final int DELAY = 20;

	private static final int LABEL_DEFAULT_WIDTH = 229;

	private static final int PROGRESS_SLIDER_INITIAL_VALUE = 0;

	private static final int PROGRESS_SLIDER_MAXIMUM_VALUE = 1000;

	private static final Dimension ANIMATED_LABEL_MAXIMUM_SIZE = new Dimension(476, 14);

	private static final Dimension ANIMATED_LABEL_MINIMUM_SIZE = new Dimension(156, 14);

	private static final Dimension ANIMATED_LABEL_PREFERRED_SIZE = new Dimension(229, 14);

	private static final Dimension ARTIST_ALBUM_PANEL_MAXIMUM_SIZE = new Dimension(729, 14);

	private static final Dimension DEFAULT_SIZE = new Dimension(294, 82);

	private static final Dimension ELLAPSED_TIME_PANEL_DEFAULT_SIZE = new Dimension(44, 14);

	private static final Dimension MAXIMUM_SIZE = new Dimension(729, 82);

	private static final Dimension MINIMUM_SIZE = new Dimension(180, 82);

	private static final Dimension PANEL_DEFAULT_SIZE = new Dimension(229, 12);

	private static final Dimension PROGRESS_SLIDER_DEFAULT_SIZE = new Dimension(206, 6);

	private static final Dimension PROGRESS_SLIDER_MAXIMUM_SIZE = new Dimension(641, 6);

	private static final Dimension PROGRESS_SLIDER_MINIMUM_SIZE = new Dimension(50, 6);

	private static final Dimension REMAINING_TIME_PANEL_DEFAULT_SIZE = new Dimension(47, 14);

	private static final Dimension SPACE_PANEL_MIDDLE_DEFAULT_SIZE = new Dimension(294, 2);

	private static final Dimension SPACE_PANEL_MIDDLE_MAXIMUM_SIZE = new Dimension(729, 2);

	private static final Dimension SPACE_PANEL_MIDDLE_MINIMUM_SIZE = new Dimension(180, 2);

	private static final Dimension TRACK_LABEL_MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, 14);

	private static final Dimension TRACK_PANEL_DEFAULT_SIZE = new Dimension(294, 14);

	private static final Dimension TRACK_PANEL_MINIMUM_SIZE = new Dimension(180, 14);

	private static final String ANIMATED_LABEL_DEFAULT_TEXT = "JLabel";

	private static final String ARTIST_ALBUM_LABEL_NAME = "artistPlaying";

	private static final String DURATION_LABEL_DEFAULT_TEXT = "0:00:00";

	private static final String NAME = "infoPlayerPanel";

	private static final String REMAINING_LABEL_DEFAULT_TEXT = "-9:99:99";

	private static final Insets TRACK_PANEL_INSETS = new Insets(9, 0, 0, 0);

	private static final Insets PROGRESS_PANEL_INSETS = new Insets(4, 0, 0, 0);

	private static final Insets REMAINING_TIME_PANEL_INSETS = new Insets(0, 3, 0, 0);

	private static final Insets ELAPSED_TIME_PANEL_INSETS = new Insets(0, 0, 0, 6);

	private static final String PROGRES_SLIDER_NAME = "progresSlider";

	private static final String TRACK_NAME = "Track";

	private boolean mouseDragged;

	private int startPosXTrackLabel;

	private long currentTime;

	private long totalTime;

	private long totalTimeMillis;

	private AnimatedLabel artistAlbumAnimationLabel = null;

	private AnimatedLabel artistAlbumLabel = null;

	private AnimatedLabel trackLabel = null;

	private AnimatedLabel trackAnimationLabel = null;

	private JLabel durationLabel = null;

	private JLabel remainingLabel = null;

	private JPanel spacePanelMiddle = null;

	private JPanel trackPanel = null;

	private JPanel progressBarPanel = null;

	private JPanel remainingTimePanel = null;

	private JPanel ellapsedTimePanel = null;

	private JPanel artistAlbumPanel = null;

	private JSlider progresSlider = null;

	private String defaultArtistName = "Artist";

	private String defaultTrackName = TRACK_NAME;

	private Timer albumArtistAnimationTimer;

	private Timer trackAnimationTimer;

	private Track track;

	@Autowired
	private ViewEngine viewEngine;

	public InfoPlayerPanel() {
		super();
		initialize();
		trackAnimationTimer = new Timer(DELAY, new LabelAnimationListener(trackLabel, trackAnimationLabel));
		albumArtistAnimationTimer = new Timer(DELAY,
				new LabelAnimationListener(artistAlbumLabel, artistAlbumAnimationLabel));

		getProgresSlider().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (track == null) {
					return;
				}
				viewEngine.sendValueAction(Actions.Player.UPDATE_TIME, new Long(currentTime * 1000));
			}
		});
	}

	@PostConstruct
	public void setup() {
		trackLabel.setWidth(LABEL_DEFAULT_WIDTH);
		artistAlbumLabel.setWidth(LABEL_DEFAULT_WIDTH);
		trackPanel.setSize(PANEL_DEFAULT_SIZE);
		artistAlbumPanel.setSize(PANEL_DEFAULT_SIZE);
		startPosXTrackLabel = trackLabel.getX();

		progresSlider.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (track == null) {
					return;
				}
				mouseDragged = true;
				currentTime = Math.round((double) (progresSlider.getValue()) * (double) totalTime
						/ (double) progresSlider.getMaximum());

				String currentTimeStr = Formatters.formatTimeString(currentTime);
				String remainingtimeStr = "-" + Formatters.formatTimeString(totalTime - currentTime);

				updateTrackTimeLabels(currentTimeStr, remainingtimeStr);
			}
		});
		progresSlider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (track == null) {
					return;
				}
				mouseDragged = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (track == null) {
					return;
				}
				mouseDragged = true;
				double w = progresSlider.getWidth();
				double x = e.getX();
				x = x < 0 ? 0 : x;
				x = x > w ? w : x;
				currentTime = (long) (x * (double) totalTime / w);
				String currentTimeStr = Formatters.formatTimeString(currentTime);
				String remainingtimeStr = "-" + Formatters.formatTimeString(totalTime - currentTime);
				updateProgressSlider(currentTime);
				updateTrackTimeLabels(currentTimeStr, remainingtimeStr);
			}
		});
		this.addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				String trackName = TRACK_NAME;
				if (track != null) {
					trackName = track.getName();
				}
				trackLabel.setWidth(trackPanel.getWidth());
				trackAnimationLabel.setWidth(trackPanel.getWidth());
				artistAlbumLabel.setWidth(artistAlbumPanel.getWidth());
				artistAlbumAnimationLabel.setWidth(artistAlbumPanel.getWidth());
				validateAnimation(trackLabel, trackName, startPosXTrackLabel, trackAnimationLabel, trackAnimationTimer,
						trackPanel);
				validateAnimation(artistAlbumLabel, artistAlbumLabel.getText(), startPosXTrackLabel, artistAlbumAnimationLabel,
						albumArtistAnimationTimer, artistAlbumPanel);
			}
		});

		MouseAdapter ad = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (track == null) {
					return;
				}
				String text;
				if (artistAlbumLabel.getText().equalsIgnoreCase(track.getArtist())) {
					text = track.getAlbumArtist();
				} else {
					text = track.getArtistAlbum();
				}
				validateAnimation(artistAlbumLabel, text, startPosXTrackLabel, artistAlbumAnimationLabel,
						albumArtistAnimationTimer, artistAlbumPanel);
			}
		};

		artistAlbumAnimationLabel.addMouseListener(ad);
		artistAlbumLabel.addMouseListener(ad);
		progresSlider.setEnabled(false);
	}

	@Override
	@Autowired
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void internationalize(Messages messages) {
		defaultArtistName = messages.getMessage("infoplayer.label.artist");
		defaultTrackName = messages.getMessage("infoplayer.label.track");
	}

	@EventMethod(Events.Player.TRACK_PLAYED_ID)
	public void onPlayerTrackPlayed(MediaPlayerTrackPlayedEvent event) {
		updateTrackInfo(event);
	}

	@EventMethod(Events.Player.PROGRESS_CHANGED_ID)
	public void onPlayerProgressChanged(MediaPlayerProgressEvent event) {
		updateProgress(event);
	}

	private void updateTrackInfo(MediaPlayerTrackPlayedEvent event) {
		Track newTrack = event.getTrack();
		if (newTrack != null) {
			// TODO: review why the if(newTrack != track)
			// fix the defect but not sure if this validation fix another ones
			this.track = newTrack;
			mouseDragged = false;
			// TODO review track, perhaps validate with TrackFactory
			// or when a music player event is generated
			// throws null pointer if the file presented on the
			// detail view is different from an audio file
			// TODO validate drag & drop to filter files accordingly
			progresSlider.setEnabled(true);
			validateAnimation(trackLabel, track.getName(), startPosXTrackLabel, trackAnimationLabel, trackAnimationTimer,
					trackPanel);
			validateAnimation(artistAlbumLabel, track.getArtistAlbum(), startPosXTrackLabel, artistAlbumAnimationLabel,
					albumArtistAnimationTimer, artistAlbumPanel);
			totalTime = track.getDuration();
			String totalTimeStr = "-" + Formatters.formatTimeString(totalTime);
			updateTrackTimeLabels("0:00", totalTimeStr);
			progresSlider.setValue(0);
		} else {
			mouseDragged = true;
			durationLabel.setText(DURATION_LABEL_DEFAULT_TEXT);
			remainingLabel.setText(REMAINING_LABEL_DEFAULT_TEXT);
			artistAlbumLabel.setText(defaultArtistName);
			trackLabel.setText(defaultTrackName);
			progresSlider.setValue(0);
		}
	}

	private void updateProgress(MediaPlayerProgressEvent event) {
		totalTimeMillis = event.getTotalTime();
		// TODO If compares if the progress event refers to the same track.
		// Incorrect validation if
		// different tracks of same duration.
		if (mouseDragged) {// || this.track == null || totalTimeMillis !=
			// this.track.getDuration()) {
			return;
		}
		// rounding avoids loosing decimals after division
		// this allows to have a more precise timing
		currentTime = Math.round((double) event.getCurrentTime() / 1000D);
		totalTime = Math.round((double) totalTimeMillis / 1000D);
		long remainingTime = totalTime - currentTime;
		if (remainingTime < 0) {
			remainingTime = track.getDuration();
		}
		// if (totalTimeMillis != this.track.getDuration()) {
		String strCurrentTime = Formatters.formatTimeString(currentTime);
		String strRemainTime = "-" + Formatters.formatTimeString(remainingTime);
		updateProgressSlider(currentTime);
		updateTrackTimeLabels(strCurrentTime, strRemainTime);
		// }
	}

	// private String formatTimeString(long currentTime) {
	// long seconds = currentTime % 60;
	// long minutes = (currentTime % 3600) / 60;
	// long hour = currentTime / 3600;
	//
	// StringBuilder formatedStr = new StringBuilder();
	//
	// String fmt = hour > 0 ? "%02d:%02d" : "%d:%02d";
	// formatedStr.append(hour > 0 ? hour + ":" : "");
	// formatedStr.append(String.format(fmt, minutes, seconds));
	//
	// return formatedStr.toString();
	// }

	private void updateProgressSlider(long currentTime) {
		int progress = (int) (((double) currentTime / (double) totalTime) * this.getProgresSlider().getMaximum());
		progresSlider.setValue(progress);
	}

	private void updateTrackTimeLabels(String duration, String remaining) {
		durationLabel.setText(duration);
		remainingLabel.setText(remaining);
	}

	private void validateAnimation(AnimatedLabel animatedLabel, String text, int startPos, AnimatedLabel follower,
			Timer timer, JPanel aniPanel) {
		animatedLabel.setText(text);
		FontMetrics fontMetrics = animatedLabel.getFontMetrics(animatedLabel.getFont());
		int trackLabelAbsoluteWidth = fontMetrics.stringWidth(text);
		int trackLabelVisibleWidth = animatedLabel.getWidth();
		animatedLabel.setWidth(trackLabelAbsoluteWidth);
		animatedLabel.setX(startPos);
		if (trackLabelAbsoluteWidth >= trackLabelVisibleWidth) {
			startAnimation(animatedLabel, follower, trackLabelAbsoluteWidth, text, timer);
		} else {
			stopAnimation(aniPanel, animatedLabel, follower, timer);
		}
	}

	private void stopAnimation(JPanel animationPanel, AnimatedLabel animatedLabel, AnimatedLabel follower, Timer timer) {
		follower.setVisible(false);
		if (animationPanel.getWidth() > 0) {
			animatedLabel.setWidth(animationPanel.getWidth());
		}
		timer.stop();
	}

	private void startAnimation(AnimatedLabel animatedLabel, AnimatedLabel follower, int trackLabelAbsoluteWidth,
			String text, Timer timer) {
		follower.setWidth(trackLabelAbsoluteWidth);
		follower.setText(text);
		follower.setVisible(true);
		follower.setX(animatedLabel.getX() + animatedLabel.getWidth() + 5);
		timer.start();
	}

	private void animateLabel(AnimatedLabel label, AnimatedLabel follower) {
		jumpWhenPassed(label, follower);
		jumpWhenPassed(follower, label);

		moveOneLeft(label);
		moveOneLeft(follower);

		follower.repaint();
		label.repaint();
	}

	private void jumpWhenPassed(AnimatedLabel label, AnimatedLabel toJumpLabel) {
		if (label.getX() + label.getWidth() <= 0) {
			label.setX(toJumpLabel.getX() + toJumpLabel.getWidth() + 5);
		}
	}

	private void moveOneLeft(AnimatedLabel label) {
		label.setX(label.getX() - 1);
	}

	private void initialize() {
		GridBagConstraints ellapsedTimePanelConstraints = new GridBagConstraints();
		ellapsedTimePanelConstraints.gridx = 0;
		ellapsedTimePanelConstraints.gridy = 5;
		ellapsedTimePanelConstraints.anchor = GridBagConstraints.NORTHEAST;
		ellapsedTimePanelConstraints.insets = ELAPSED_TIME_PANEL_INSETS;
		GridBagConstraints remainingTimeConstraints = new GridBagConstraints();
		remainingTimeConstraints.gridx = 2;
		remainingTimeConstraints.gridy = 5;
		remainingTimeConstraints.anchor = GridBagConstraints.NORTHWEST;
		remainingTimeConstraints.insets = REMAINING_TIME_PANEL_INSETS;
		GridBagConstraints progressBarPanelConstraints = new GridBagConstraints();
		progressBarPanelConstraints.gridx = 1;
		progressBarPanelConstraints.gridy = 5;
		progressBarPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		progressBarPanelConstraints.weightx = 1;
		progressBarPanelConstraints.insets = PROGRESS_PANEL_INSETS;
		GridBagConstraints artistAlbumPanelConstraints = new GridBagConstraints();
		artistAlbumPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		artistAlbumPanelConstraints.gridy = 3;
		artistAlbumPanelConstraints.weightx = 1.0D;
		artistAlbumPanelConstraints.gridwidth = 0;
		artistAlbumPanelConstraints.gridheight = 1;
		artistAlbumPanelConstraints.gridx = 0;
		GridBagConstraints spacePanelMiddleConstraints = new GridBagConstraints();
		spacePanelMiddleConstraints.gridx = 0;
		spacePanelMiddleConstraints.gridy = 2;
		spacePanelMiddleConstraints.fill = GridBagConstraints.HORIZONTAL;
		spacePanelMiddleConstraints.weightx = 1;
		spacePanelMiddleConstraints.gridwidth = 0;
		GridBagConstraints trackPanelConstraints = new GridBagConstraints();
		trackPanelConstraints.gridx = 0;
		trackPanelConstraints.gridy = 1;
		trackPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		trackPanelConstraints.gridwidth = 0;
		trackPanelConstraints.weightx = 1;
		trackPanelConstraints.insets = TRACK_PANEL_INSETS;

		this.setLayout(new GridBagLayout());
		this.setSize(DEFAULT_SIZE);
		this.setMaximumSize(MAXIMUM_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setName(NAME);

		this.add(getTrackPanel(), trackPanelConstraints);
		this.add(getSpacePanelMiddle(), spacePanelMiddleConstraints);
		this.add(getArtistAlbumPanel(), artistAlbumPanelConstraints);
		this.add(getEllapsedTimePanel(), ellapsedTimePanelConstraints);
		this.add(getProgressBarPanel(), progressBarPanelConstraints);
		this.add(getRemainingTimePanel(), remainingTimeConstraints);
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
	}

	/**
	 * This method initializes progresSlider
	 * 
	 * @return javax.swing.JSlider
	 */
	private JSlider getProgresSlider() {
		if (progresSlider == null) {
			progresSlider = new JSlider();
			progresSlider.setSize(PROGRESS_SLIDER_DEFAULT_SIZE);
			progresSlider.setPreferredSize(PROGRESS_SLIDER_DEFAULT_SIZE);
			progresSlider.setMinimumSize(PROGRESS_SLIDER_MINIMUM_SIZE);
			progresSlider.setMaximumSize(PROGRESS_SLIDER_MAXIMUM_SIZE);
			progresSlider.setMaximum(PROGRESS_SLIDER_MAXIMUM_VALUE);
			progresSlider.setName(PROGRES_SLIDER_NAME);
			progresSlider.setRequestFocusEnabled(false);
			progresSlider.setValue(PROGRESS_SLIDER_INITIAL_VALUE);
			progresSlider.setOpaque(false);
			progresSlider.setFocusable(false);
		}
		return progresSlider;
	}

	/**
	 * This method initializes spacePanelMiddle
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getSpacePanelMiddle() {
		if (spacePanelMiddle == null) {
			spacePanelMiddle = new JPanel();
			spacePanelMiddle.setLayout(new GridBagLayout());
			spacePanelMiddle.setPreferredSize(SPACE_PANEL_MIDDLE_DEFAULT_SIZE);
			spacePanelMiddle.setSize(SPACE_PANEL_MIDDLE_DEFAULT_SIZE);
			spacePanelMiddle.setMinimumSize(SPACE_PANEL_MIDDLE_MINIMUM_SIZE);
			spacePanelMiddle.setMaximumSize(SPACE_PANEL_MIDDLE_MAXIMUM_SIZE);
		}
		return spacePanelMiddle;
	}

	private JPanel getTrackPanel() {
		if (trackPanel == null) {
			GridBagConstraints trackLabelConstraints = new GridBagConstraints();
			trackLabelConstraints.gridx = -1;
			trackLabelConstraints.gridy = -1;
			trackPanel = new JPanel();
			trackPanel.setLayout(new GridBagLayout());
			trackPanel.setPreferredSize(TRACK_PANEL_DEFAULT_SIZE);
			trackPanel.setSize(TRACK_PANEL_DEFAULT_SIZE);
			trackPanel.setMinimumSize(TRACK_PANEL_MINIMUM_SIZE);
			trackPanel.add(getTrackLabel(), trackLabelConstraints);
			trackPanel.add(getTrackAnimationLabel(), new GridBagConstraints());
		}
		return trackPanel;
	}

	private AnimatedLabel getTrackLabel() {
		if (trackLabel == null) {
			trackLabel = new AnimatedLabel();
			trackLabel.setText(defaultTrackName);
			trackLabel.setMinimumSize(TRACK_PANEL_MINIMUM_SIZE);
			trackLabel.setMaximumSize(TRACK_LABEL_MAXIMUM_SIZE);
			trackLabel.setPreferredSize(TRACK_PANEL_DEFAULT_SIZE);
			trackLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			trackLabel.setHorizontalAlignment(SwingConstants.CENTER);
			trackLabel.setName(SynthFonts.PLAIN_FONT11_BLACK);
			trackLabel.setPreferredSize(ANIMATED_LABEL_PREFERRED_SIZE);
		}
		return trackLabel;
	}

	private AnimatedLabel getTrackAnimationLabel() {
		if (trackAnimationLabel == null) {
			trackAnimationLabel = new AnimatedLabel();
			trackAnimationLabel.setText(ANIMATED_LABEL_DEFAULT_TEXT);
			trackAnimationLabel.setPreferredSize(TRACK_PANEL_DEFAULT_SIZE);
			trackAnimationLabel.setMaximumSize(ANIMATED_LABEL_MAXIMUM_SIZE);
			trackAnimationLabel.setMinimumSize(TRACK_PANEL_MINIMUM_SIZE);
			trackAnimationLabel.setName(SynthFonts.PLAIN_FONT11_BLACK);
			trackAnimationLabel.setHorizontalAlignment(SwingConstants.CENTER);
			trackAnimationLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			trackAnimationLabel.setVisible(false);
		}
		return trackAnimationLabel;
	}

	/**
	 * This method initializes progressBarPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getProgressBarPanel() {
		if (progressBarPanel == null) {
			progressBarPanel = new JPanel();
			progressBarPanel.setLayout(new BorderLayout());
			progressBarPanel.setSize(PROGRESS_SLIDER_DEFAULT_SIZE);
			progressBarPanel.setPreferredSize(PROGRESS_SLIDER_DEFAULT_SIZE);
			progressBarPanel.setMinimumSize(PROGRESS_SLIDER_MINIMUM_SIZE);
			progressBarPanel.setMaximumSize(PROGRESS_SLIDER_MAXIMUM_SIZE);
			progressBarPanel.add(getProgresSlider(), BorderLayout.NORTH);
		}
		return progressBarPanel;
	}

	/**
	 * This method initializes remainingTimePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getRemainingTimePanel() {
		if (remainingTimePanel == null) {
			remainingTimePanel = new JPanel();
			remainingTimePanel.setLayout(new BorderLayout());
			remainingTimePanel.setMaximumSize(REMAINING_TIME_PANEL_DEFAULT_SIZE);
			remainingTimePanel.setPreferredSize(REMAINING_TIME_PANEL_DEFAULT_SIZE);
			remainingTimePanel.setMinimumSize(REMAINING_TIME_PANEL_DEFAULT_SIZE);
			remainingTimePanel.add(getRemainingLabel(), BorderLayout.NORTH);
		}
		return remainingTimePanel;
	}

	private JLabel getRemainingLabel() {
		if (remainingLabel == null) {
			remainingLabel = new JLabel();
			remainingLabel.setText(REMAINING_LABEL_DEFAULT_TEXT);
			remainingLabel.setMaximumSize(REMAINING_TIME_PANEL_DEFAULT_SIZE);
			remainingLabel.setMinimumSize(REMAINING_TIME_PANEL_DEFAULT_SIZE);
			remainingLabel.setPreferredSize(REMAINING_TIME_PANEL_DEFAULT_SIZE);
			remainingLabel.setName(SynthFonts.PLAIN_FONT11_BLACK);
			remainingLabel.setHorizontalAlignment(SwingConstants.LEFT);
			remainingLabel.setHorizontalTextPosition(SwingConstants.LEFT);
			remainingLabel.setVerticalAlignment(SwingConstants.TOP);
			remainingLabel.setVerticalTextPosition(SwingConstants.TOP);
		}
		return remainingLabel;
	}

	/**
	 * This method initializes ellapsedTimePanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getEllapsedTimePanel() {
		if (ellapsedTimePanel == null) {
			ellapsedTimePanel = new JPanel();
			ellapsedTimePanel.setLayout(new BorderLayout());
			ellapsedTimePanel.setMaximumSize(ELLAPSED_TIME_PANEL_DEFAULT_SIZE);
			ellapsedTimePanel.setPreferredSize(ELLAPSED_TIME_PANEL_DEFAULT_SIZE);
			ellapsedTimePanel.setMinimumSize(ELLAPSED_TIME_PANEL_DEFAULT_SIZE);
			ellapsedTimePanel.add(getDurationLabel(), BorderLayout.NORTH);
		}
		return ellapsedTimePanel;
	}

	private JLabel getDurationLabel() {
		if (durationLabel == null) {
			durationLabel = new JLabel();
			durationLabel.setText(DURATION_LABEL_DEFAULT_TEXT);
			durationLabel.setPreferredSize(ELLAPSED_TIME_PANEL_DEFAULT_SIZE);
			durationLabel.setMaximumSize(ELLAPSED_TIME_PANEL_DEFAULT_SIZE);
			durationLabel.setMinimumSize(ELLAPSED_TIME_PANEL_DEFAULT_SIZE);
			durationLabel.setName(SynthFonts.PLAIN_FONT11_BLACK);
			durationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			durationLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
			durationLabel.setVerticalAlignment(SwingConstants.TOP);
			durationLabel.setVerticalTextPosition(SwingConstants.TOP);
		}
		return durationLabel;
	}

	/**
	 * This method initializes artistAlbumPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getArtistAlbumPanel() {
		if (artistAlbumPanel == null) {
			artistAlbumPanel = new JPanel();
			GridBagConstraints artistAlbumPanelConstraints = new GridBagConstraints();
			artistAlbumPanelConstraints.gridx = -1;
			artistAlbumPanelConstraints.gridy = -1;
			artistAlbumPanel.setLayout(new GridBagLayout());
			artistAlbumPanel.setMaximumSize(ARTIST_ALBUM_PANEL_MAXIMUM_SIZE);
			artistAlbumPanel.setMinimumSize(TRACK_PANEL_MINIMUM_SIZE);
			artistAlbumPanel.setPreferredSize(TRACK_PANEL_DEFAULT_SIZE);
			artistAlbumPanel.setSize(TRACK_PANEL_DEFAULT_SIZE);
			artistAlbumPanel.add(getArtistAlbumLabel(), artistAlbumPanelConstraints);
			artistAlbumPanel.add(getArtistAlbumAnimationLabel(), new GridBagConstraints());
			artistAlbumLabel.setName(ARTIST_ALBUM_LABEL_NAME);
		}
		return artistAlbumPanel;
	}

	private AnimatedLabel getArtistAlbumLabel() {
		if (artistAlbumLabel == null) {
			artistAlbumLabel = new AnimatedLabel();
			artistAlbumLabel.setText(defaultArtistName);
			artistAlbumLabel.setMaximumSize(ANIMATED_LABEL_MAXIMUM_SIZE);
			artistAlbumLabel.setMinimumSize(ANIMATED_LABEL_MINIMUM_SIZE);
			artistAlbumLabel.setPreferredSize(ANIMATED_LABEL_PREFERRED_SIZE);
			artistAlbumLabel.setName(SynthFonts.PLAIN_FONT11_BLACK);
			artistAlbumLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			artistAlbumLabel.setHorizontalAlignment(SwingConstants.CENTER);
		}
		return artistAlbumLabel;
	}

	private AnimatedLabel getArtistAlbumAnimationLabel() {
		if (artistAlbumAnimationLabel == null) {
			artistAlbumAnimationLabel = new AnimatedLabel();
			artistAlbumAnimationLabel.setText(ANIMATED_LABEL_DEFAULT_TEXT);
			artistAlbumAnimationLabel.setPreferredSize(ANIMATED_LABEL_PREFERRED_SIZE);
			artistAlbumAnimationLabel.setMaximumSize(ANIMATED_LABEL_MAXIMUM_SIZE);
			artistAlbumAnimationLabel.setMinimumSize(ANIMATED_LABEL_MINIMUM_SIZE);
			artistAlbumAnimationLabel.setName(SynthFonts.PLAIN_FONT11_BLACK);
			artistAlbumAnimationLabel.setHorizontalAlignment(SwingConstants.CENTER);
			artistAlbumAnimationLabel.setHorizontalTextPosition(SwingConstants.CENTER);
			artistAlbumAnimationLabel.setVisible(false);
		}
		return artistAlbumAnimationLabel;
	}

	private final class LabelAnimationListener implements ActionListener {

		AnimatedLabel animated;
		AnimatedLabel follower;

		public LabelAnimationListener(AnimatedLabel animated, AnimatedLabel follower) {
			this.animated = animated;
			this.follower = follower;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			animateLabel(animated, follower);
		}
	}

}

// understands not to allow resize from Swing for preventing layout manager
class AnimatedLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	public void setWidth(int width) {
		super.setBounds(this.getX(), this.getY(), width, this.getHeight());
	}

	public void setX(int x) {
		super.setBounds(x, this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(this.getX(), y, this.getWidth(), height);
	}

	@Override
	public void setBounds(Rectangle r) {
		r = new Rectangle((int) this.getX(), (int) r.getY(), this.getWidth(), (int) r.getHeight());
		super.setBounds(r);
	}

}