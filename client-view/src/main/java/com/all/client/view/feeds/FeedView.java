package com.all.client.view.feeds;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.feeds.FeedHeaderPanel.ContactLabel;
import com.all.client.view.flows.ShowContactFeedInfoFlow;
import com.all.client.view.format.TimeFeedFormatter;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.event.EventListener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.User;
import com.all.shared.newsfeed.AbstractFeed;

public abstract class FeedView<T extends AbstractFeed> extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 3565006451773036970L;

	private static final int DETAIL_HEIGHT = 5;

	private static final int HEADER_HEIGHT = 20;

	private static final Dimension AVATAR_PANEL_SIZE = new Dimension(66, 66);

	private static final int FOOTER_HEIGHT = 32;

	private static final Dimension TIMESTAMP_FOOTER_ICON_PANEL_SIZE = new Dimension(16, 16);

	private static final String AVATAR_PORTRAIT_MASK_NAME = "profilePortraitMask";

	static final int MIN_HEIGHT = 66;

	static final int MAX_HEIGHT = 131;

	private int preferredWidth = 404;

	private static final int IMAGE_ARC = 0;

	private FeedHeaderPanel headerPanel;

	protected FeedDetailsPanel detailsPanel;

	private JPanel footerPanel;

	private JPanel avatarPanel;

	protected final T feed;

	private JPanel timestampFooterIconPanel;

	private JPanel portraitMask;

	private JLabel timestampLabel;

	protected final Messages messages;

	private final FeedIconType iconType;

	private ImagePanel imageAvatarPanel;

	private static final Rectangle IMAGE_AVATAR_PANEL_DEFAULT_BOUNDS = new Rectangle(8, 8, 50, 50);

	protected final ViewEngine viewEngine;

	private final DialogFactory dialogFactory;

	private static final String SEPARATOR_NAME = "colorD5D5D5Separator";

	private JPanel separatorPanel;

	private EventListener<ValueEvent<Date>> updateTimeListener;
	private EventListener<ValueEvent<ContactInfo>> contactUpdatedListener;
	private EventListener<ValueEvent<User>> profileUpdateListener;
	private static TimeFeedFormatter timeFeedFormatter = new TimeFeedFormatter();
	private int internalWidth;

	protected FeedView(T feed, FeedIconType iconType, FeedSize feedSize, Messages messages, ViewEngine viewEngine,
			DialogFactory dialogFactory) {
		this.messages = messages;
		this.feed = feed;
		this.iconType = iconType;
		this.viewEngine = viewEngine;
		this.dialogFactory = dialogFactory;
		this.setMessages(messages);

		initialize(feedSize);

	}

	/**
	 * Enum used for the different icons for feeds<br/>
	 * <br/>
	 * EXPORT_TO_DEVICE - uses a contact icon<br/>
	 * CUSTOM - uses nothing, override {@code getActiveIcon()} and {@code getInactiveIcon()} to set custom icons<br/>
	 */
	enum FeedIconType {
		EXPORT_TO_DEVICE("feedDeviceExportIconName"), FACEBOOK("feedFacebookIconName"), QUOTE("feedQuoteIconName"), AVATAR(
				"feedAvatarIconName"), RLBROWSING("feedRLBIconName"), TWITTER("feedTwitterIconName"), PROFILE(
				"feedProfileIconName"), SEND_MEDIA("feedSendMediaIconName"), FRIENDSHIP("feedFriendshipIconName"), MEDIA_IMPORT(
				"feedImportIconName"), MEDIA_IMPORT_ITUNES("feedImportItunesIconName"), CONTACTS("feedContactsIconName"), TOP_HUNDRED(
				"topHundredIconName"), PLAYED_TRACKS("feedSendMediaIconName"), DOWNLOADED_TRACKS("feedSendMediaIconName"), TOTAL_TRACKS(
				"feedTotalTracksName");

		String iconPanelName;

		private FeedIconType(String iconPanelName) {
			this.iconPanelName = iconPanelName;
		}

		public String getIconPanelName() {
			return iconPanelName;
		}

	}

	enum FeedSize {
		SMALL(new Dimension(Integer.MAX_VALUE, 66)), MEDIUM(new Dimension(Integer.MAX_VALUE, 90)), LARGE(new Dimension(
				Integer.MAX_VALUE, 131));

		private final Dimension feedSize;

		private FeedSize(Dimension feedSize) {
			this.feedSize = feedSize;
		}

		public Dimension getFeedSize() {
			return feedSize;
		}

	}

	protected final void initialize(FeedSize feedSize) {
		this.setPreferredSize(feedSize.getFeedSize());
		this.setMaximumSize(feedSize.getFeedSize());
		this.setMinimumSize(feedSize.getFeedSize());
		this.setLayout(new GridBagLayout());

		GridBagConstraints avatarPanelConstraints = new GridBagConstraints();
		avatarPanelConstraints.gridx = 0;
		avatarPanelConstraints.gridy = 0;
		avatarPanelConstraints.gridheight = 3;
		avatarPanelConstraints.weighty = 1.0;
		avatarPanelConstraints.fill = GridBagConstraints.VERTICAL;
		GridBagConstraints headerPanelConstraints = new GridBagConstraints();
		headerPanelConstraints.gridx = 1;
		headerPanelConstraints.gridy = 0;
		headerPanelConstraints.weightx = 1.0;
		headerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints detailsPanelConstraints = new GridBagConstraints();
		detailsPanelConstraints.gridx = 1;
		detailsPanelConstraints.gridy = 1;
		detailsPanelConstraints.weightx = 1.0;
		detailsPanelConstraints.weighty = 1.0;
		detailsPanelConstraints.fill = GridBagConstraints.BOTH;
		GridBagConstraints footerPanelConstraints = new GridBagConstraints();
		footerPanelConstraints.gridx = 1;
		footerPanelConstraints.gridy = 2;
		footerPanelConstraints.weightx = 1.0;
		footerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints separatorPanelConstraints = new GridBagConstraints();
		separatorPanelConstraints.gridx = 0;
		separatorPanelConstraints.gridy = 3;
		separatorPanelConstraints.weightx = 1.0;
		separatorPanelConstraints.gridwidth = 2;
		separatorPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

		this.add(getAvatarPanel(), avatarPanelConstraints);
		this.add(getHeaderPanel(), headerPanelConstraints);
		this.add(getDetailsPanel(), detailsPanelConstraints);
		this.add(getFooterPanel(), footerPanelConstraints);
		this.add(getSeparatorPanel(), separatorPanelConstraints);

		updateTimeListener = new EventListener<ValueEvent<Date>>() {

			@Override
			public void handleEvent(ValueEvent<Date> eventArgs) {
				Date serverDate = eventArgs.getValue();
				timestampLabel.setText(timeFeedFormatter.getTimeFormatString(feed.getDate(), serverDate));
			}

		};

		contactUpdatedListener = new ContactUpdateListener();

		profileUpdateListener = new ProfileUpdateListener();

		// TODO Remove this listeners when this panel could be destroyed or deleted
		viewEngine.addListener(Events.Feeds.UPDATE_FEED_TIME_VIEW, updateTimeListener);

		viewEngine.addListener(Events.Social.CONTACT_UPDATED, contactUpdatedListener);

		viewEngine.addListener(Events.UserProfile.USER_PROFILE_UPDATED, profileUpdateListener);
	}

	protected Image getOwnerImage() {
		return ImageUtil.getImage(feed.getOwner().getAvatar());
	}

	protected FeedHeaderPanel getHeaderPanel() {
		if (headerPanel == null) {
			headerPanel = new FeedHeaderPanel(viewEngine, dialogFactory);
			headerPanel.setSize(preferredWidth, HEADER_HEIGHT);
			headerPanel.setMinimumSize(new Dimension(preferredWidth, HEADER_HEIGHT));
			headerPanel.setPreferredSize(new Dimension(preferredWidth, HEADER_HEIGHT));
		}
		return headerPanel;
	}

	protected FeedDetailsPanel getDetailsPanel() {
		if (detailsPanel == null) {
			detailsPanel = new FeedDetailsPanel(viewEngine, dialogFactory);
			detailsPanel.setMinimumSize(new Dimension(preferredWidth, DETAIL_HEIGHT));
			detailsPanel.setPreferredSize(new Dimension(preferredWidth, DETAIL_HEIGHT));
		}
		return detailsPanel;
	}

	private JPanel getFooterPanel() {
		if (footerPanel == null) {
			footerPanel = new JPanel();
			footerPanel.setLayout(new GridBagLayout());
			footerPanel.setSize(new Dimension(preferredWidth, FOOTER_HEIGHT));
			footerPanel.setMinimumSize(new Dimension(preferredWidth, FOOTER_HEIGHT));
			footerPanel.setPreferredSize(new Dimension(preferredWidth, FOOTER_HEIGHT));
			GridBagConstraints iconConst = new GridBagConstraints();
			iconConst.gridx = 0;
			iconConst.gridy = 0;
			GridBagConstraints footerConst = new GridBagConstraints();
			footerConst.gridx = 1;
			footerConst.gridy = 0;
			footerConst.weightx = 1;
			footerConst.fill = GridBagConstraints.HORIZONTAL;
			footerConst.insets = new Insets(8, 8, 8, 0);
			footerPanel.add(getTimestampFooterIconPanel(), iconConst);
			footerPanel.add(getTimestampFooterLabel(), footerConst);
		}

		return footerPanel;
	}

	private JPanel getTimestampFooterIconPanel() {
		if (timestampFooterIconPanel == null) {
			timestampFooterIconPanel = new JPanel();
			timestampFooterIconPanel.setName(iconType.getIconPanelName());
			timestampFooterIconPanel.setSize(TIMESTAMP_FOOTER_ICON_PANEL_SIZE);
			timestampFooterIconPanel.setMinimumSize(TIMESTAMP_FOOTER_ICON_PANEL_SIZE);
			timestampFooterIconPanel.setMaximumSize(TIMESTAMP_FOOTER_ICON_PANEL_SIZE);
			timestampFooterIconPanel.setPreferredSize(TIMESTAMP_FOOTER_ICON_PANEL_SIZE);
		}
		return timestampFooterIconPanel;
	}

	private JLabel getTimestampFooterLabel() {
		if (timestampLabel == null) {
			timestampLabel = new JLabel();
			timestampLabel.setName(SynthFonts.PLAIN_FONT10_GRAY120_120_120);
			timeFeedFormatter.internationalize(messages);
			timestampLabel.setText(timeFeedFormatter.getTimeFormatString(feed.getDate(), null));
		}
		return timestampLabel;
	}

	private JPanel getAvatarPanel() {
		if (avatarPanel == null) {
			avatarPanel = new JPanel();
			avatarPanel.setSize(AVATAR_PANEL_SIZE);
			avatarPanel.setMinimumSize(AVATAR_PANEL_SIZE);
			avatarPanel.setPreferredSize(AVATAR_PANEL_SIZE);
			avatarPanel.setVisible(true);
			avatarPanel.setLayout(null);
			avatarPanel.add(getImageAvatarPanel());

		}
		return avatarPanel;
	}

	private ImagePanel getImageAvatarPanel() {
		if (imageAvatarPanel == null) {
			imageAvatarPanel = new ImagePanel();
			imageAvatarPanel.setLayout(new BorderLayout());
			imageAvatarPanel.setBounds(IMAGE_AVATAR_PANEL_DEFAULT_BOUNDS);
			imageAvatarPanel.setImage(ImageUtil.getImage(feed.getOwner().getAvatar()), IMAGE_ARC, IMAGE_ARC);
			imageAvatarPanel.add(getPortraitMask(), BorderLayout.CENTER);
			imageAvatarPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						new ShowContactFeedInfoFlow(viewEngine, dialogFactory).execute(feed.getOwner(), FeedView.this);
					}
				}
			});
		}
		return imageAvatarPanel;
	}

	private JPanel getPortraitMask() {
		if (portraitMask == null) {
			portraitMask = new JPanel();
			portraitMask.setLayout(null);
			portraitMask.setName(AVATAR_PORTRAIT_MASK_NAME);
		}
		return portraitMask;
	}

	private JPanel getSeparatorPanel() {
		if (separatorPanel == null) {
			separatorPanel = new JPanel();
			separatorPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 1));
			separatorPanel.setMinimumSize(new Dimension(Integer.MAX_VALUE, 1));
			separatorPanel.setName(SEPARATOR_NAME);
		}
		return separatorPanel;
	}

	public final void setStyle(String string) {
		setName(string);
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void internationalize(Messages messages) {
		validate();
	}

	@Override
	public void validate() {
		super.validate();
		calculateInternalWidth();
	}

	protected void clear() {
		getDetailsPanel().clear();
		getHeaderPanel().clear();
	}

	private void calculateInternalWidth() {
		internalWidth = Math.max(getHeaderPanel().getInternalWidth(), getDetailsPanel().getInternalWidth());
		this.setMinimumSize(new Dimension(internalWidth, (int) getMinimumSize().getHeight()));
	}

	public int getInternalWidth() {
		return internalWidth;
	}

	class ContactUpdateListener extends EventListener<ValueEvent<ContactInfo>> {

		@Override
		public void handleEvent(ValueEvent<ContactInfo> eventArgs) {
			ContactInfo value = eventArgs.getValue();
			if (value.getEmail().equals(feed.getOwner().getEmail())) {
				getImageAvatarPanel().setImage(ImageUtil.getImage(value.getAvatar()), IMAGE_ARC, IMAGE_ARC);
			}

			HashMap<String, ContactLabel> contactLabels = getHeaderPanel().getContactLabels();
			if (contactLabels.containsKey(value.getEmail())) {
				ContactLabel contactLabel = contactLabels.get(value.getEmail());
				contactLabel.getFeedRunnableLabel().setText(value.getNickName() + contactLabel.getAditionalText());
			}

			HashMap<String, ImagePanel> imagePanels = getDetailsPanel().getImagePanels();
			if (imagePanels.containsKey(value.getEmail())) {
				imagePanels.get(value.getEmail()).setImage(ImageUtil.getImage(value.getAvatar()), IMAGE_ARC, IMAGE_ARC);
			}
		}
	}

	class ProfileUpdateListener extends EventListener<ValueEvent<User>> {

		@Override
		public void handleEvent(ValueEvent<User> eventArgs) {
			User value = eventArgs.getValue();
			if (feed.getOwner().getEmail().equals(value.getEmail())) {
				User user = eventArgs.getValue();
				getImageAvatarPanel().setImage(ImageUtil.getImage(user.getAvatar()), IMAGE_ARC, IMAGE_ARC);
			}
			HashMap<String, ContactLabel> contactLabels = getHeaderPanel().getContactLabels();
			if (contactLabels.containsKey(value.getEmail())) {
				ContactLabel contactLabel = contactLabels.get(value.getEmail());
				contactLabel.getFeedRunnableLabel().setText(value.getNickName() + contactLabel.getAditionalText());
			}

			HashMap<String, ImagePanel> imagePanels = getDetailsPanel().getImagePanels();
			if (imagePanels.containsKey(eventArgs.getValue().getEmail())) {
				User user = eventArgs.getValue();
				imagePanels.get(eventArgs.getValue().getEmail()).setImage(ImageUtil.getImage(user.getAvatar()), IMAGE_ARC,
						IMAGE_ARC);
			}
		}

	}
}
