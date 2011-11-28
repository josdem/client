package com.all.client.view.feeds;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.client.view.flows.ShowProfileFlow;
import com.all.core.actions.Actions;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.common.util.ImageUtil;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;
import com.all.core.model.ContainerView;
import com.all.core.model.Model;
import com.all.core.model.SubViews;
import com.all.core.model.TopHundredModelContainer;
import com.all.core.model.Views;
import com.all.i18n.Messages;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.FeedTrack;
import com.all.shared.model.User;

public class FeedDetailsPanel extends CommonFeedPanel {

	private static final Log LOG = LogFactory.getLog(FeedDetailsPanel.class);
	private static final long serialVersionUID = 4753277416773804870L;
	private static final Rectangle IMAGE_AVATAR_PANEL_DEFAULT_BOUNDS = new Rectangle(0, 0, 32, 32);
	private static final String AVATAR_PORTRAIT_MASK_NAME = "profilePortraitMask";

	private static final String LEFT_PARENTHESIS = "(";
	private static final String RIGTH_PARENTHESIS = ")";
	private static final int IMAGE_ARC = 0;
	private final DialogFactory dialogFactory;
    private HashMap<String, ImagePanel> imagePanels;
	
	public FeedDetailsPanel(ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(viewEngine);
		this.dialogFactory = dialogFactory;
		this.setLayout(new MigLayout("nogrid, insets 4 0 4 0", "", "[]2[]"));
	}

	public void appendFeedTrack(ContactInfo feedOwner, FeedTrack feedTrack) {
		User currentUser = viewEngine.get(Model.CURRENT_USER);
		ContactInfo currentContactInfo = new ContactInfo(currentUser);

		if (currentContactInfo.equals(feedOwner)) {
			appendOwnFeedTrack(feedTrack);
		} else {
			appendOthersFeedTrack(feedOwner, feedTrack);
		}
	}

	protected void appendOwnFeedTrack(FeedTrack feedTrack) {
		this.add(new FeedActionLabel<ValueAction<ContainerView>>(feedTrack.getTrackname(),
				SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100, Actions.View.setCurrentView, viewEngine,
				new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)), SynthIcons.FEED_MUSIC_ICON),
				getContraints());

		this.add(new FeedActionLabel<ValueAction<ContainerView>>(new StringBuilder(LEFT_PARENTHESIS).append(
				feedTrack.getArtist()).append(RIGTH_PARENTHESIS).toString(), SynthFonts.ITALIC_FONT12_GRAY100_100_100,
				Actions.View.setCurrentView, viewEngine, new ValueAction<ContainerView>(new ContainerView(
						Views.LOCAL_MUSIC))), getContraints());
	}

	private void appendOthersFeedTrack(final ContactInfo contact, FeedTrack feedTrack) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					new ShowProfileFlow(viewEngine, dialogFactory).execute(contact, FeedDetailsPanel.this);
				} catch (Exception e) {
					LOG.error(e, e);
				}
			}
		};

		this.add(new FeedRunnableLabel(feedTrack.getTrackname(), SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100,
				runnable, SynthIcons.FEED_MUSIC_ICON), getContraints());

		this.add(new FeedRunnableLabel(new StringBuilder(LEFT_PARENTHESIS).append(feedTrack.getArtist()).append(
				RIGTH_PARENTHESIS).toString(), SynthFonts.ITALIC_FONT12_GRAY100_100_100, runnable), getContraints());
	}

	public void appendAvatarFriend(final ContactInfo visited) {
		JPanel portraitMask = new JPanel();
		portraitMask.setLayout(null);
		portraitMask.setName(AVATAR_PORTRAIT_MASK_NAME);

		ImagePanel imageAvatarPanel = new ImagePanel();
		imageAvatarPanel.setLayout(new BorderLayout());
		imageAvatarPanel.setBounds(IMAGE_AVATAR_PANEL_DEFAULT_BOUNDS);
		imageAvatarPanel.setImage(ImageUtil.getImage(visited.getAvatar()), IMAGE_ARC, IMAGE_ARC);
		imageAvatarPanel.add(portraitMask, BorderLayout.CENTER);
		imageAvatarPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(visited));
				super.mouseClicked(e);
			}
		});
		getImagePanels().put(visited.getEmail(), imageAvatarPanel);
		this.add(imageAvatarPanel, "w 32!, h 32!");
	}

	public void appendCategory(String playlistName) {
		// appendOwnFolderOrPlaylist(playlistName, SynthIcons.FEED_PLAYLIST_ICON);
		this.add(new FeedActionLabel<ValueAction<SubViews>>(playlistName,
				SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100, Actions.View.setCurrentSubView, viewEngine,
				new ValueAction<SubViews>(SubViews.HUNDRED_PLAYLISTS), SynthIcons.FEED_PLAYLIST_ICON), getContraints());

	}

	public void appendFolder(ContactInfo feedOwner, String folderName) {
		User currentUser = viewEngine.get(Model.CURRENT_USER);
		ContactInfo currentContactInfo = new ContactInfo(currentUser);

		if (currentContactInfo.equals(feedOwner)) {
			appendOwnFolderOrPlaylist(folderName, SynthIcons.FEED_FOLDER_ICON);
		} else {
			appendOthersLibraryLinkLabel(feedOwner, folderName, SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100,
					SynthIcons.FEED_FOLDER_ICON);
		}
	}

	public void appendPlaylist(ContactInfo feedOwner, String playlistName) {
		User currentUser = viewEngine.get(Model.CURRENT_USER);
		ContactInfo currentContactInfo = new ContactInfo(currentUser);

		if (currentContactInfo.equals(feedOwner)) {
			appendOwnFolderOrPlaylist(playlistName, SynthIcons.FEED_PLAYLIST_ICON);
		} else {
			appendOthersLibraryLinkLabel(feedOwner, playlistName, SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100,
					SynthIcons.FEED_PLAYLIST_ICON);
		}
	}

	private void appendOwnFolderOrPlaylist(String folderName, Icon icon) {
		this.add(new FeedActionLabel<ValueAction<ContainerView>>(folderName,
				SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100, Actions.View.setCurrentView, viewEngine,
				new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC)), icon), getContraints());
	}

	public void appendTopHundredCategoryLinkLabel(String topHundredFeedText, long categoryId) {
		this.add(new FeedActionLabel<ValueAction<ContainerView>>(topHundredFeedText,
				SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1, Actions.View.setCurrentView, viewEngine,
				new ValueAction<ContainerView>(new ContainerView(Views.HUNDRED,
						new TopHundredModelContainer(categoryId)))), getContraints());
	}

	public void appendTopHundredPlaylistLinkLabel(String playlistName, String playlistHash, long categoryId) {
		this.add(new FeedActionLabel<ValueAction<ContainerView>>(playlistName,
				SynthFonts.BOLD_ITALIC_FONT12_GRAY100_100_100, Actions.View.setCurrentView, viewEngine,
				new ValueAction<ContainerView>(new ContainerView(Views.HUNDRED, new TopHundredModelContainer(
						categoryId, playlistHash)))), getContraints());
	}

	/**
	 * Add an arbitrary message link that will open own local music, friends local music or show the add as a friend
	 * dialog
	 * 
	 * @param feedOwner
	 * @param message
	 */
	public void appendLinkLibraryLabel(ContactInfo feedOwner, String message) {
		User currentUser = viewEngine.get(Model.CURRENT_USER);
		ContactInfo currentContactInfo = new ContactInfo(currentUser);

		if (currentContactInfo.equals(feedOwner)) {
			appendOwnLinkLibraryLabel(message, SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1);
		} else {
			appendOthersLinkLibraryLabel(feedOwner, message, SynthFonts.BOLD_FONT12_PURPLE8F_5B_B1);
		}
	}

	private void appendOwnLinkLibraryLabel(String message, String style) {
		this.add(new FeedActionLabel<ValueAction<ContainerView>>(message, style, Actions.View.setCurrentView,
				viewEngine, new ValueAction<ContainerView>(new ContainerView(Views.LOCAL_MUSIC))), getContraints());
	}

	private void appendOthersLinkLibraryLabel(final ContactInfo contact, String message, String style) {
		appendOthersLibraryLinkLabel(contact, message, style, null);
	}

	private void appendOthersLibraryLinkLabel(final ContactInfo contact, String message, String style, Icon icon) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					new ShowProfileFlow(viewEngine, dialogFactory).execute(contact, FeedDetailsPanel.this);
				} catch (Exception e) {
					LOG.error(e, e);
				}
			}
		};

		this.add(new FeedRunnableLabel(message, style, runnable, icon), getContraints());
	}

	public String appendMediaAmountMessage(Messages messages, int totalFolders, int totalPlaylists, int totalTracks) {
		StringBuilder messageBuilder = new StringBuilder();
		DecimalFormat formatter = new DecimalFormat("###,###,###");
		if (totalFolders > 0) {
			messageBuilder.append(formatter.format(totalFolders)).append(" folder").append(totalFolders > 1 ? "s" : "");
		}
		if (totalPlaylists > 0) {
			if (messageBuilder.length() > 0) {
				messageBuilder.append(", ");
			}
			messageBuilder.append(formatter.format(totalPlaylists)).append(" playlist").append(
					totalPlaylists > 1 ? "s" : "");
		}
		if (totalTracks != 0) {
			if (messageBuilder.length() > 0) {
				messageBuilder.append(" ").append(messages.getMessage("feed.import.and")).append(" ");
			}

			messageBuilder.append(formatter.format(totalTracks)).append(" ").append(
					messages.getMessage("feed.import.track"));
			if (totalTracks > 1) {
				messageBuilder.append(messages.getMessage("feed.import.track.plural"));
			}
		}

		messageBuilder.append(".");
		return messageBuilder.toString();
	}
	
	public HashMap<String, ImagePanel> getImagePanels() {
		if(imagePanels == null){
			imagePanels = new HashMap<String, ImagePanel>();
		}
		return imagePanels;
	}
}
