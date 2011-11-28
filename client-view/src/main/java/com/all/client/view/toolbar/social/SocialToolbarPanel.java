package com.all.client.view.toolbar.social;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.core.actions.Actions;
import com.all.core.model.SubViews;
import com.all.observ.Observable;
import com.all.observ.ObserveObject;
import com.all.observ.ObserverCollection;

public class SocialToolbarPanel extends JPanel implements View {

	private static final long serialVersionUID = 1L;

	private static final int MINIMUM_REASONABLE_SIZE = 580;

	private static final Dimension DEFAULT_SIZE_BUTTON = new Dimension(100, 24);

	private static final Dimension HEADER_PANEL_DEFAULT_SIZE = new Dimension(822, 24);

	private static final Dimension MINIMUM_SIZE_BUTTON = new Dimension(40, 24);

	private static final Dimension SEPARATOR_DEFAULT_SIZE = new Dimension(2, 24);

	private static final String PROFILE_ALL_TOGGLE_BUTTON_NAME = "profileAllToggleButton";

	private static final String PROFILE_ALL_TOGGLE_BUTTON_NAME_MINI = "profileAllToggleButtonMini";

	private static final String PROFILE_FACEBOOK_TOGGLE_BUTTON_NAME = "profileFacebookToggleButton";

	private static final String PROFILE_FACEBOOK_TOGGLE_BUTTON_NAME_MINI = "profileFacebookToggleMiniButton";

	private static final String PROFILE_TWITTER_TOGGLE_BUTTON_NAME = "profileTwitterToggleButton";

	private static final String PROFILE_TWITTER_TOGGLE_BUTTON_NAME_MINI = "profileTwitterToggleMiniButton";

	private static final String PROFILE_YOUTUBE_TOGGLE_BUTTON_NAME = "profileYouTubeToggleButton";

	private static final String PROFILE_YOUTUBE_TOGGLE_BUTTON_NAME_MINI = "profileYouTubeToggleButtonMini";

	private static final String TABS_PANEL_NAME = "profileTabPanelBackground";

	private static final String VERTICAL_SEPARATOR_NAME = "verticalSocialSeparator";

	private ButtonGroup buttonGroup;

	private Observable<ObserveObject> onAllEvent = new Observable<ObserveObject>();

	private Observable<ObserveObject> onFacebookEvent = new Observable<ObserveObject>();

	private Observable<ObserveObject> onTwitterEvent = new Observable<ObserveObject>();

	private Observable<ObserveObject> onYoutubeEvent = new Observable<ObserveObject>();

	private JToggleButton allToggleButton;

	private JToggleButton facebookToggleButton;

	private JToggleButton twitterToggleButton;

	private JToggleButton youtubeToggleButton;

	public SocialToolbarPanel() {
		initialize();
	}

	private void initialize() {
		this.setLayout(new GridBagLayout());
		this.setName(TABS_PANEL_NAME);
		this.setPreferredSize(HEADER_PANEL_DEFAULT_SIZE);

		GridBagConstraints toggleButtonContraints = new GridBagConstraints();
		toggleButtonContraints.gridx = 0;
		toggleButtonContraints.fill = 1;
		toggleButtonContraints.weightx = 1;

		this.add(getAllToggleButton(), toggleButtonContraints);

		GridBagConstraints separatorConstraints = new GridBagConstraints();
		separatorConstraints.gridx = 1;

		this.add(getSeparator(), separatorConstraints);

		toggleButtonContraints.gridx = 2;
		this.add(getTwitterToggleButton(), toggleButtonContraints);

		separatorConstraints.gridx = 3;
		this.add(getSeparator(), separatorConstraints);

		toggleButtonContraints.gridx = 4;
		this.add(getYoutubeToggleButton(), toggleButtonContraints);

		separatorConstraints.gridx = 5;
		this.add(getSeparator(), separatorConstraints);

		toggleButtonContraints.gridx = 6;
		this.add(getFacebookToggleButton(), toggleButtonContraints);

		buttonGroup = new ButtonGroup();
		buttonGroup.add(getAllToggleButton());
		buttonGroup.add(getTwitterToggleButton());
		buttonGroup.add(getYoutubeToggleButton());
		buttonGroup.add(getFacebookToggleButton());

		buttonGroup.setSelected(getAllToggleButton().getModel(), true);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				applyResize();
			}
		});
	}

	private JPanel getSeparator() {
		JPanel separator = new JPanel();
		separator.setSize(SEPARATOR_DEFAULT_SIZE);
		separator.setMinimumSize(SEPARATOR_DEFAULT_SIZE);
		separator.setMaximumSize(SEPARATOR_DEFAULT_SIZE);
		separator.setPreferredSize(SEPARATOR_DEFAULT_SIZE);
		separator.setName(VERTICAL_SEPARATOR_NAME);
		return separator;
	}

	private JToggleButton getTwitterToggleButton() {
		if (twitterToggleButton == null) {
			twitterToggleButton = new JToggleButton();
			twitterToggleButton.setMinimumSize(MINIMUM_SIZE_BUTTON);
			twitterToggleButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			twitterToggleButton.setName(PROFILE_TWITTER_TOGGLE_BUTTON_NAME);
		}
		return twitterToggleButton;
	}

	private JToggleButton getYoutubeToggleButton() {
		if (youtubeToggleButton == null) {
			youtubeToggleButton = new JToggleButton();
			youtubeToggleButton.setMinimumSize(MINIMUM_SIZE_BUTTON);
			youtubeToggleButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			youtubeToggleButton.setName(PROFILE_YOUTUBE_TOGGLE_BUTTON_NAME);
		}
		return youtubeToggleButton;
	}

	private JToggleButton getFacebookToggleButton() {
		if (facebookToggleButton == null) {
			facebookToggleButton = new JToggleButton();
			facebookToggleButton.setMinimumSize(MINIMUM_SIZE_BUTTON);
			facebookToggleButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			facebookToggleButton.setName(PROFILE_FACEBOOK_TOGGLE_BUTTON_NAME);
		}
		return facebookToggleButton;
	}

	JToggleButton getAllToggleButton() {
		if (allToggleButton == null) {
			allToggleButton = new JToggleButton();
			allToggleButton.setMinimumSize(MINIMUM_SIZE_BUTTON);
			allToggleButton.setPreferredSize(DEFAULT_SIZE_BUTTON);
			allToggleButton.setName(PROFILE_ALL_TOGGLE_BUTTON_NAME);
		}
		return allToggleButton;
	}

	private void applyResize() {
		if (getWidth() <= MINIMUM_REASONABLE_SIZE) {
			allToggleButton.setName(PROFILE_ALL_TOGGLE_BUTTON_NAME_MINI);
			youtubeToggleButton.setName(PROFILE_YOUTUBE_TOGGLE_BUTTON_NAME_MINI);
			facebookToggleButton.setName(PROFILE_FACEBOOK_TOGGLE_BUTTON_NAME_MINI);
			twitterToggleButton.setName(PROFILE_TWITTER_TOGGLE_BUTTON_NAME_MINI);
		} else {
			allToggleButton.setName(PROFILE_ALL_TOGGLE_BUTTON_NAME);
			youtubeToggleButton.setName(PROFILE_YOUTUBE_TOGGLE_BUTTON_NAME);
			facebookToggleButton.setName(PROFILE_FACEBOOK_TOGGLE_BUTTON_NAME);
			twitterToggleButton.setName(PROFILE_TWITTER_TOGGLE_BUTTON_NAME);
		}
	}

	public ObserverCollection<ObserveObject> onAll() {
		return onAllEvent;
	}

	public ObserverCollection<ObserveObject> onFacebook() {
		return onFacebookEvent;
	}

	public ObserverCollection<ObserveObject> onTwitter() {
		return onTwitterEvent;
	}

	public ObserverCollection<ObserveObject> onYoutube() {
		return onYoutubeEvent;
	}

	//TODO change this integer for constants or one enum
	public void selectedButton(int selectedButton) {
		switch (selectedButton) {
		case 0:
			buttonGroup.setSelected(allToggleButton.getModel(), true);
			break;
		case 1:
			buttonGroup.setSelected(youtubeToggleButton.getModel(), true);
			break;
		case 2:
			buttonGroup.setSelected(facebookToggleButton.getModel(), true);
			break;
		case 3:
			buttonGroup.setSelected(twitterToggleButton.getModel(), true);
		}
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize(final ViewEngine viewEngine) {
		getTwitterToggleButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onTwitterEvent.fire(ObserveObject.EMPTY);
				viewEngine.send(Actions.View.setCurrentSubView, new ValueAction<SubViews>(SubViews.TWITTER));
			}
		});
		
		getAllToggleButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onAllEvent.fire(ObserveObject.EMPTY);
				viewEngine.send(Actions.View.setCurrentSubView, new ValueAction<SubViews>(SubViews.ALL));
			}
		});
		
		getYoutubeToggleButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onYoutubeEvent.fire(ObserveObject.EMPTY);
				viewEngine.send(Actions.View.setCurrentSubView, new ValueAction<SubViews>(SubViews.NONE));
			}
		});
		
		getFacebookToggleButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onFacebookEvent.fire(ObserveObject.EMPTY);
				viewEngine.send(Actions.View.setCurrentSubView, new ValueAction<SubViews>(SubViews.NONE));
			}
		});
	}

}
