package com.all.client.view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.action.ResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.DeviceBase;
import com.all.client.view.components.DevicesPanel;
import com.all.client.view.components.ExternalDevicePanel;
import com.all.core.actions.Actions;
import com.all.core.model.Model;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;

@Component
public class MediaPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private static final Dimension ICON_SIZE = new Dimension(26, 29);

	private static final Dimension MEDIA_PANEL_SIZE = new Dimension(200, 31);

	private static final Point AUDIOBOOK_TOGGLE_BUTTON_LOCATION = new Point(140, 1);

	private static final Point GAME_TOGGLE_BUTTON_LOCATION = new Point(168, 1);

	private static final Point FILE_TOGGLE_BUTTON_LOCATION = new Point(56, 1);

	private static final Point MUSIC_TOGGLE_BUTTON_LOCATION = new Point(3, 1);

	private static final Point PHOTO_TOGGLE_BUTTON_LOCATION = new Point(112, 1);

	private static final Point PODCAST_TOGGLE_BUTTON_LOCATION = new Point(84, 1);

	private static final Point VIDEO_TOGGLE_BUTTON_LOCATION = new Point(28, 1);

	private static final Rectangle COMBO_BOX_BOUNDS = new Rectangle(4, 8, 192, 16);

	private static final String AUDIOBOOK_TOGGLE_BUTTON_NAME = "audiobookButton";

	private static final String FILE_TOGGLE_BUTTON_NAME = "fileButton";

	private static final String GAME_TOGGLE_BUTTON = "gameButton";

	private static final String MUSIC_TOGGLE_BUTTON_NAME = "musicButton";

	private static final String NAME = "libraryBackgroundPanel";

	private static final String PHOTO_TOGGLE_BUTTON_NAME = "photoButton";

	private static final String PODCAST_TOGGLE_BUTTON_NAME = "podcastButton";

	private static final String TOOLTIP_AUDIOBOOK_LIB = "tooltip.audiobookLib";

	private static final String TOOLTIP_FILES_LIB = "tooltip.filesLib";

	private static final String TOOLTIP_MUSIC_LIB = "tooltip.musicLib";

	private static final String TOOLTIP_GAMES_LIB = "tooltip.gamesLib";

	private static final String TOOLTIP_PHOTO_LIB = "tooltip.photoLib";

	private static final String TOOLTIP_PODCAST_LIB = "tooltip.podcastLib";

	private static final String TOOLTIP_VIDEO_LIB = "tooltip.videoLib";

	private static final String VIDEO_TOGGLE_BUTTON_NAME = "videoButton";

	private JToggleButton audiobookToggleButton = null;

	private JToggleButton fileToggleButton = null;

	private JToggleButton gameToggleButton = null;

	private JToggleButton musicToggleButton = null;

	private JToggleButton photoToggleButton = null;

	private JToggleButton podcastToggleButton = null;

	private JToggleButton videoToggleButton = null;

	private boolean showMedia = true;

	private DeviceComboBox deviceComboBox;

	private Messages messages;

	private DevicesPanel devicesPanel;

	private ViewEngine viewEngine;

	/**
	 * This is the default constructor
	 */
	public MediaPanel() {
		super();
	}

	public MediaPanel(boolean showMedia, Messages messages, DevicesPanel devicesPanel, ViewEngine viewEngine) {
		super();
		this.showMedia = showMedia;
		this.messages = messages;
		this.devicesPanel = devicesPanel;
		this.viewEngine = viewEngine;
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
		getVideoToggleButton().setToolTipText(messages.getMessage(TOOLTIP_VIDEO_LIB));
		getPhotoToggleButton().setToolTipText(messages.getMessage(TOOLTIP_PHOTO_LIB));
		getPodcastToggleButton().setToolTipText(messages.getMessage(TOOLTIP_PODCAST_LIB));
		getAudiobookToggleButton().setToolTipText(messages.getMessage(TOOLTIP_AUDIOBOOK_LIB));
		getFileToggleButton().setToolTipText(messages.getMessage(TOOLTIP_FILES_LIB));
		getGameToggleButton().setToolTipText(messages.getMessage(TOOLTIP_GAMES_LIB));
		getMusicToggleButton().setToolTipText(messages.getMessage(TOOLTIP_MUSIC_LIB));
	};

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	@PostConstruct
	public void initialize() {
		this.setLayout(null);
		this.setName(NAME);
		this.setOpaque(true);
		this.setSize(MEDIA_PANEL_SIZE);
		this.setPreferredSize(MEDIA_PANEL_SIZE);
		this.setMaximumSize(MEDIA_PANEL_SIZE);
		this.setMinimumSize(MEDIA_PANEL_SIZE);
		if (showMedia) {
			this.add(getMusicToggleButton());
			this.add(getVideoToggleButton());
			this.add(getPhotoToggleButton());
			this.add(getPodcastToggleButton());
			this.add(getAudiobookToggleButton());
			this.add(getFileToggleButton());
			this.add(getGameToggleButton());
		} else {
			this.add(getDeviceComboBox());
		}
	}

	private DeviceComboBox getDeviceComboBox() {
		if (deviceComboBox == null) {
			deviceComboBox = new DeviceComboBox(viewEngine, messages);
			deviceComboBox.setBounds(COMBO_BOX_BOUNDS);
			deviceComboBox.onSelectDevice().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					viewEngine.request(Actions.Devices.GET_DEVICES, null, new ResponseCallback<List<DeviceBase>>() {
						@Override
						public void onResponse(List<DeviceBase> devices) {
							for (DeviceBase deviceBase : devices) {
								if (deviceBase.getDeviceRoot().equals(viewEngine.get(Model.SELECTED_ROOT))) {
									ExternalDevicePanel externalDevicePanel = devicesPanel.searchDevicePanel(deviceBase);
									externalDevicePanel.selectDevice();
									break;
								}
							}
						}
					});
				}
			});
		}
		return deviceComboBox;
	}

	/**
	 * This method initializes musicToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getMusicToggleButton() {
		if (musicToggleButton == null) {
			musicToggleButton = new JToggleButton();
			musicToggleButton.setSize(ICON_SIZE);
			musicToggleButton.setSelected(true);
			musicToggleButton.setName(MUSIC_TOGGLE_BUTTON_NAME);
			musicToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			musicToggleButton.setLocation(MUSIC_TOGGLE_BUTTON_LOCATION);
		}
		return musicToggleButton;
	}

	/**
	 * This method initializes videoToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getVideoToggleButton() {
		if (videoToggleButton == null) {
			videoToggleButton = new JToggleButton();
			videoToggleButton.setSize(ICON_SIZE);
			videoToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			videoToggleButton.setEnabled(false);
			videoToggleButton.setName(VIDEO_TOGGLE_BUTTON_NAME);
			videoToggleButton.setLocation(VIDEO_TOGGLE_BUTTON_LOCATION);
		}
		return videoToggleButton;
	}

	/**
	 * This method initializes fileToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getFileToggleButton() {
		if (fileToggleButton == null) {
			fileToggleButton = new JToggleButton();
			fileToggleButton.setSize(ICON_SIZE);
			fileToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			fileToggleButton.setEnabled(false);
			fileToggleButton.setName(FILE_TOGGLE_BUTTON_NAME);
			fileToggleButton.setLocation(FILE_TOGGLE_BUTTON_LOCATION);
		}
		return fileToggleButton;
	}

	/**
	 * This method initializes podcastToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getPodcastToggleButton() {
		if (podcastToggleButton == null) {
			podcastToggleButton = new JToggleButton();
			podcastToggleButton.setSize(ICON_SIZE);
			podcastToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			podcastToggleButton.setEnabled(false);
			podcastToggleButton.setName(PODCAST_TOGGLE_BUTTON_NAME);
			podcastToggleButton.setLocation(PODCAST_TOGGLE_BUTTON_LOCATION);
		}
		return podcastToggleButton;
	}

	/**
	 * This method initializes photoToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getPhotoToggleButton() {
		if (photoToggleButton == null) {
			photoToggleButton = new JToggleButton();
			photoToggleButton.setSize(ICON_SIZE);
			photoToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			photoToggleButton.setEnabled(false);
			photoToggleButton.setName(PHOTO_TOGGLE_BUTTON_NAME);
			photoToggleButton.setLocation(PHOTO_TOGGLE_BUTTON_LOCATION);
		}
		return photoToggleButton;
	}

	/**
	 * This method initializes audiobookToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getAudiobookToggleButton() {
		if (audiobookToggleButton == null) {
			audiobookToggleButton = new JToggleButton();
			audiobookToggleButton.setSize(ICON_SIZE);
			audiobookToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			audiobookToggleButton.setEnabled(false);
			audiobookToggleButton.setName(AUDIOBOOK_TOGGLE_BUTTON_NAME);
			audiobookToggleButton.setLocation(AUDIOBOOK_TOGGLE_BUTTON_LOCATION);
		}
		return audiobookToggleButton;
	}

	/**
	 * This method initializes gameToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getGameToggleButton() {
		if (gameToggleButton == null) {
			gameToggleButton = new JToggleButton();
			gameToggleButton.setSize(ICON_SIZE);
			gameToggleButton.setMnemonic(KeyEvent.VK_UNDEFINED);
			gameToggleButton.setEnabled(false);
			gameToggleButton.setName(GAME_TOGGLE_BUTTON);
			gameToggleButton.setLocation(GAME_TOGGLE_BUTTON_LOCATION);
		}
		return gameToggleButton;
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
	}
}
