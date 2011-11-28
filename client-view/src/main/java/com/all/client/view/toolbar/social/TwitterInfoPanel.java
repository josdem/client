package com.all.client.view.toolbar.social;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.TwitterProfile;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.listeners.TwitterActionListener;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class TwitterInfoPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final int IMAGE_ARC = 0;

	private static final Dimension DEFAULT_SIZE = new Dimension(170, 466);

	private static final Rectangle NAME_LABEL_BOUNDS = new Rectangle(15, 92, 150, 50);

	private static final Rectangle TITLE_LABEL_BOUNDS = new Rectangle(15, 0, 150, 30);

	private static final Rectangle SCREEN_NAME_LABEL_BOUNDS = new Rectangle(15, 118, 150, 30);

	private static final Rectangle BOTTOM_SEPARATOR_BOUNDS = new Rectangle(0, 307, 170, 2);

	private static final Rectangle FOLLOWERS_CONTENT_LABEL_BOUNDS = new Rectangle(15, 332, 150, 18);

	private static final Rectangle FOLLOWERS_TITLE_LABEL_BOUNDS = new Rectangle(15, 315, 150, 18);

	private static final Rectangle FOLLOWING_CONTENT_LABEL_BOUNDS = new Rectangle(15, 373, 150, 18);

	private static final Rectangle FOLLOWING_TITLE_LABEL = new Rectangle(15, 354, 150, 18);

	private static final Rectangle LOCATION_CONTENT_LABEL_BOUNDS = new Rectangle(15, 242, 150, 18);

	private static final Rectangle LOCATION_TITLE_LABEL_BOUNDS = new Rectangle(15, 225, 150, 18);

	private static final Rectangle PROFILE_PHOTO_PANEL_BOUNDS = new Rectangle(15, 30, 73, 73);

	private static final Rectangle QUOTE_LABEL_BOUNDS = new Rectangle(15, 148, 150, 62);

	private static final Rectangle TOP_SEPARATOR_BOUNDS = new Rectangle(0, 217, 170, 2);

	private static final Rectangle TWEET_CONTENT_LABEL_BOUNDS = new Rectangle(15, 413, 150, 18);

	private static final Rectangle TWEET_TITLE_LABEL = new Rectangle(15, 396, 150, 18);

	private static final Rectangle WEB_CONTENT_LABEL_BOUNDS = new Rectangle(15, 279, 150, 18);

	private static final Rectangle WEB_TITLE_LABEL_BOUNDS = new Rectangle(15, 262, 150, 18);

	private static final String NAME = "profileInfoBackgroundPanel";

	private static final String PROFILE_PORTRAIT_MASK_NAME = "profileTwitterPortraitMask";

	private static final String FOLLOW_BUTTON_NAME = "followButton";

	private static final String UNFOLLOW_BUTTON_NAME = "unfollowButton";

	private static final Rectangle FOLLOW_BUTTON_BOUNDS = new Rectangle(90, 82, 24, 22);

	private static final Rectangle FOLLOW_LABEL_BOUNDS  = new Rectangle(116, 82, 50, 22);

	private JLabel nameLabel;

	private ImagePanel profilePhotoPanel;

	private JPanel portraitMask;

	private JSeparator topSeparator;

	private JSeparator bottomSeparator;

	private JLabel followersContentLabel;

	private JLabel followersTitleLabel;

	private JLabel followingContentLabel;

	private JLabel followingTitleLabel;

	private JLabel locationContentLabel;

	private JLabel locationTitleLabel;

	private JLabel titleLabel;

	private JLabel screenNameLabel;

	private JLabel tweetContentLabel;

	private JLabel tweetTitleLabel;

	private JLabel webContentLabel;

	private JLabel webTitleLabel;

	private JTextArea quoteLabel;

	private JButton followButton;

	private JButton unFollowButton;

	private Log log = LogFactory.getLog(this.getClass());

	private final TwitterActionListener twitterListener;

	private MouseAdapter mouseAction;

	private TwitterProfile currentProfile;

	private Messages messages;

	private JLabel followLabel;

	public TwitterInfoPanel(TwitterActionListener twitterListener, Messages messages) {
		this.twitterListener = twitterListener;
		initialize();
		setMessages(messages);
		internationalize(messages);
	}

	private void initialize() {
		this.setLayout(null);
		this.setName(NAME);
		this.setMinimumSize(DEFAULT_SIZE);
		this.setSize(DEFAULT_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.setMaximumSize(DEFAULT_SIZE);
		this.add(getTitleLabel());
		this.add(getProfilePhotoPanel());
		this.add(getNameLabel());
		this.add(getScreenNameLabel());
		this.add(getTopSeparator());
		this.add(getBottomSeparator());
		this.add(getLocationTitleLabel());
		this.add(getLocationContentLabel());
		this.add(getQuoteLabel());
		this.add(getWebTitleLabel());
		this.add(getWebContentLabel());
		this.add(getFollowersTitleLabel());
		this.add(getFollowersContentLabel());
		this.add(getFollowingTitleLabel());
		this.add(getFollowingContentLabel());
		this.add(getTweetTitleLabel());
		this.add(getTweetContentLabel());
		this.add(getFollowButton());
		this.add(getUnFollowButton());
		this.add(getFollowLabel());
	}

	

	private MouseAdapter createMouseAction(final JLabel webLabel, final String sourceUrl) {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (webLabel != null) {
					try {
						Desktop.getDesktop().browse(new URI(sourceUrl));
					} catch (Exception e) {
						log.error("Unable to open source in web browser", e);
					}
				}
			}
		};
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setBounds(TITLE_LABEL_BOUNDS);
			titleLabel.setName(SynthFonts.BOLD_FONT14_PURPLE87_63_106);
		}
		return titleLabel;
	}

	private JLabel getScreenNameLabel() {
		if (screenNameLabel == null) {
			screenNameLabel = new JLabel();
			screenNameLabel.setBounds(SCREEN_NAME_LABEL_BOUNDS);
			screenNameLabel.setName(SynthFonts.PLAIN_FONT14_PURPLE8102_45_145);
		}
		return screenNameLabel;
	}

	private ImagePanel getProfilePhotoPanel() {
		if (profilePhotoPanel == null) {
			profilePhotoPanel = new ImagePanel();
			profilePhotoPanel.setLayout(new BorderLayout());
			profilePhotoPanel.setBounds(PROFILE_PHOTO_PANEL_BOUNDS);
			profilePhotoPanel.add(getPortraitMask(), BorderLayout.CENTER);
		}
		return profilePhotoPanel;
	}

	private JLabel getNameLabel() {
		if (nameLabel == null) {
			nameLabel = new JLabel();
			nameLabel.setBounds(NAME_LABEL_BOUNDS);
			nameLabel.setName(SynthFonts.BOLD_FONT15_PURPLE87_63_106);
		}
		return nameLabel;
	}

	private JPanel getPortraitMask() {
		if (portraitMask == null) {
			portraitMask = new JPanel();
			portraitMask.setLayout(null);
			portraitMask.setName(PROFILE_PORTRAIT_MASK_NAME);
		}
		return portraitMask;
	}

	private JSeparator getTopSeparator() {
		if (topSeparator == null) {
			topSeparator = new JSeparator();
			topSeparator.setBounds(TOP_SEPARATOR_BOUNDS);
		}
		return topSeparator;
	}

	private JSeparator getBottomSeparator() {
		if (bottomSeparator == null) {
			bottomSeparator = new JSeparator();
			bottomSeparator.setBounds(BOTTOM_SEPARATOR_BOUNDS);
		}
		return bottomSeparator;
	}

	private JLabel getLocationTitleLabel() {
		if (locationTitleLabel == null) {
			locationTitleLabel = new JLabel();
			locationTitleLabel.setBounds(LOCATION_TITLE_LABEL_BOUNDS);
			locationTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
			locationTitleLabel.setName(SynthFonts.PLAIN_FONT12_GRAY123_119_130);
		}
		return locationTitleLabel;
	}

	private JLabel getLocationContentLabel() {
		if (locationContentLabel == null) {
			locationContentLabel = new JLabel();
			locationContentLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE87_63_106);
			locationContentLabel.setBounds(LOCATION_CONTENT_LABEL_BOUNDS);
		}
		return locationContentLabel;
	}

	private JTextArea getQuoteLabel() {
		if (quoteLabel == null) {
			quoteLabel = new JTextArea();
			quoteLabel.setEditable(false);
			quoteLabel.setLineWrap(true);
			quoteLabel.setWrapStyleWord(true);
			quoteLabel.setBounds(QUOTE_LABEL_BOUNDS);
		}
		return quoteLabel;
	}

	private JLabel getWebTitleLabel() {
		if (webTitleLabel == null) {
			webTitleLabel = new JLabel();
			webTitleLabel.setName(SynthFonts.PLAIN_FONT12_GRAY123_119_130);
			webTitleLabel.setBounds(WEB_TITLE_LABEL_BOUNDS);
		}
		return webTitleLabel;
	}

	private JLabel getWebContentLabel() {
		if (webContentLabel == null) {
			webContentLabel = new JLabel();
			webContentLabel.setName(SynthFonts.PLAIN_FONT12_PURPLE87_63_106);
			webContentLabel.setBounds(WEB_CONTENT_LABEL_BOUNDS);
		}
		return webContentLabel;
	}

	private JLabel getFollowersTitleLabel() {
		if (followersTitleLabel == null) {
			followersTitleLabel = new JLabel();
			followersTitleLabel.setName(SynthFonts.PLAIN_FONT12_GRAY123_119_130);
			followersTitleLabel.setBounds(FOLLOWERS_TITLE_LABEL_BOUNDS);
		}
		return followersTitleLabel;
	}

	private JLabel getFollowersContentLabel() {
		if (followersContentLabel == null) {
			followersContentLabel = new JLabel();
			followersContentLabel.setBounds(FOLLOWERS_CONTENT_LABEL_BOUNDS);
			followersContentLabel.setName(SynthFonts.PLAIN_FONT18_PURPLE87_63_106);
		}
		return followersContentLabel;
	}

	private JLabel getFollowingTitleLabel() {
		if (followingTitleLabel == null) {
			followingTitleLabel = new JLabel();
			followingTitleLabel.setName(SynthFonts.PLAIN_FONT12_GRAY123_119_130);
			followingTitleLabel.setBounds(FOLLOWING_TITLE_LABEL);
		}
		return followingTitleLabel;
	}

	private JLabel getFollowingContentLabel() {
		if (followingContentLabel == null) {
			followingContentLabel = new JLabel();
			followingContentLabel.setBounds(FOLLOWING_CONTENT_LABEL_BOUNDS);
			followingContentLabel.setName(SynthFonts.PLAIN_FONT18_PURPLE87_63_106);
		}
		return followingContentLabel;
	}

	private JLabel getTweetTitleLabel() {
		if (tweetTitleLabel == null) {
			tweetTitleLabel = new JLabel();
			tweetTitleLabel.setName(SynthFonts.PLAIN_FONT12_GRAY123_119_130);
			tweetTitleLabel.setBounds(TWEET_TITLE_LABEL);
		}
		return tweetTitleLabel;
	}

	private JLabel getTweetContentLabel() {
		if (tweetContentLabel == null) {
			tweetContentLabel = new JLabel();
			tweetContentLabel.setBounds(TWEET_CONTENT_LABEL_BOUNDS);
			tweetContentLabel.setName(SynthFonts.PLAIN_FONT18_PURPLE87_63_106);
		}
		return tweetContentLabel;
	}

	private JButton getFollowButton() {
		if (followButton == null) {
			followButton = new JButton();
			followButton.setBounds(FOLLOW_BUTTON_BOUNDS);
			followButton.setName(FOLLOW_BUTTON_NAME);
			followButton.setVisible(false);
			followButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			followButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (currentProfile != null) {
						twitterListener.onFollowUser(currentProfile.getScreenName());
						followButton.setVisible(false);
						unFollowButton.setVisible(true);
						getFollowLabel().setText(messages.getMessage("twitter.info.unfollowButtonLabel"));
					}
				}
			});
		}
		return followButton;
	}

	private JButton getUnFollowButton() {
		if (unFollowButton == null) {
			unFollowButton = new JButton();
			unFollowButton.setBounds(FOLLOW_BUTTON_BOUNDS);
			unFollowButton.setName(UNFOLLOW_BUTTON_NAME);
			unFollowButton.setVisible(false);
			unFollowButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			unFollowButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (currentProfile != null) {
						twitterListener.onUnfollowUser(currentProfile.getScreenName());
						unFollowButton.setVisible(false);
						followButton.setVisible(true);
						getFollowLabel().setText(messages.getMessage("twitter.info.followButtonLabel"));
					}
				}
			});
		}
		return unFollowButton;
	}
	
	private JLabel getFollowLabel() {
		if (followLabel == null) {
			followLabel = new JLabel();
			followLabel.setBounds(FOLLOW_LABEL_BOUNDS);
		}
		return followLabel;
	}

	@Override
	public void internationalize(Messages messages) {
		getTitleLabel().setText(messages.getMessage("twitter.info.title"));
		locationTitleLabel.setText(messages.getMessage("twitter.info.locationTitle"));
		webTitleLabel.setText(messages.getMessage("twitter.info.web"));
		followersTitleLabel.setText(messages.getMessage("twitter.info.followers"));
		tweetTitleLabel.setText(messages.getMessage("twitter.info.tweets"));
		followingTitleLabel.setText(messages.getMessage("twitter.info.followging"));
		if (currentProfile != null && !currentProfile.isLoggedInUser()) {
			getTitleLabel().setText(messages.getMessage("twitter.info.friend.title", currentProfile.getScreenName()));
		}
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		this.messages = messages;
		messages.add(this);
	}

	public void showProfile(TwitterProfile userProfile) {
		if (userProfile != null) {
			currentProfile = userProfile;
			getNameLabel().setText(userProfile.getName());
			getScreenNameLabel().setText("@" + userProfile.getScreenName());
			getQuoteLabel().setText(userProfile.getDescription());
			getLocationContentLabel().setText(userProfile.getLocation());

			setWebPreferences(userProfile.getURL());
			getFollowersContentLabel().setText(String.valueOf(userProfile.getFollowersCount()));
			getFollowingContentLabel().setText(String.valueOf(userProfile.getFriendsCount()));
			getTweetContentLabel().setText(String.valueOf(userProfile.getTweetsCount()));
			getProfilePhotoPanel().setImage(userProfile.getImageProfile(), IMAGE_ARC, IMAGE_ARC);
			if (userProfile.isLoggedInUser()) {
				getFollowButton().setVisible(false);
				getUnFollowButton().setVisible(false);
				getFollowLabel().setVisible(false);
				getTitleLabel().setText(messages.getMessage("twitter.info.title"));
			} else {
				getFollowButton().setVisible(!userProfile.isFollowing());
				getUnFollowButton().setVisible(userProfile.isFollowing());
				getFollowLabel().setVisible(true);
				getFollowLabel().setText(userProfile.isFollowing()? messages.getMessage("twitter.info.unfollowButtonLabel"):  messages.getMessage("twitter.info.followButtonLabel"));
				getTitleLabel().setText(messages.getMessage("twitter.info.friend.title", userProfile.getScreenName()));
			}
		}
	}

	private void setWebPreferences(String sourceUrl) {
		final JLabel webLabel = getWebContentLabel();
		if (!StringUtils.isEmpty(sourceUrl)) {
			webLabel.setText(sourceUrl);
			webLabel.removeMouseListener(mouseAction);
			mouseAction = createMouseAction(webLabel, sourceUrl);
			webLabel.addMouseListener(mouseAction);
			webLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			webLabel.setToolTipText(sourceUrl);
		} else {
			webLabel.setText("");
			webLabel.removeMouseListener(mouseAction);
			webLabel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			webLabel.setToolTipText(null);
		}
	}

}
