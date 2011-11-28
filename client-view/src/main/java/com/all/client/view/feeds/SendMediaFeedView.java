package com.all.client.view.feeds;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.springframework.context.support.ResourceBundleMessageSource;

import com.all.action.ActionObject;
import com.all.action.ActionType;
import com.all.action.EmptyAction;
import com.all.action.RequestAction;
import com.all.action.ResponseCallback;
import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.common.util.ImageUtil;
import com.all.event.EventObject;
import com.all.event.EventType;
import com.all.event.Listener;
import com.all.i18n.DefaultMessages;
import com.all.i18n.Messages;
import com.all.model.ModelType;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.FeedTrack;
import com.all.shared.model.Folder;
import com.all.shared.model.Gender;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Playlist;
import com.all.shared.model.User;
import com.all.shared.newsfeed.SendMediaFeed;

public class SendMediaFeedView extends FeedView<SendMediaFeed> {

	private static final long serialVersionUID = 1L;
	public static final int MAXIMUM_ENTITIES = 3;
	private static final String SPACER = "    ";

	protected SendMediaFeedView(SendMediaFeed feed, Messages messages, ViewEngine viewEngine, DialogFactory dialogFactory) {
		super(feed, FeedIconType.SEND_MEDIA, FeedSize.LARGE, messages, viewEngine, dialogFactory);
	}

	@Override
	public void internationalize(Messages arg0) {
		ContactInfo owner = feed.getOwner();

		clear();

		this.getHeaderPanel().appendContactInfo(owner);
		this.getHeaderPanel().appendText(messages.getMessage("feed.sendMedia.header"));
		this.getHeaderPanel().appendContactInfo(feed.getRecipient(), ":");

		int i = 0;
		List<String> folders = feed.getFolderNames();
		for (int j = 0; i < MAXIMUM_ENTITIES && j < folders.size(); j++, i++) {
			this.getDetailsPanel().appendFolder(owner, folders.get(j));
			this.getDetailsPanel().newLine();
		}

		List<String> playlists = feed.getPlaylistNames();
		for (int j = 0; i < MAXIMUM_ENTITIES && j < playlists.size(); j++, i++) {
			this.getDetailsPanel().appendPlaylist(owner, playlists.get(j));
			this.getDetailsPanel().newLine();
		}

		List<FeedTrack> tracks = feed.getTracks();
		int tracksAdded;
		for (tracksAdded = 0; i < MAXIMUM_ENTITIES && tracksAdded < tracks.size(); tracksAdded++, i++) {
			this.getDetailsPanel().appendFeedTrack(owner, tracks.get(tracksAdded));
			this.getDetailsPanel().newLine();
		}

		int countTotalTracks = feed.getCountTotalTracks();
		if (tracksAdded < countTotalTracks) {
			this.getDetailsPanel().appendText(SPACER); // spacer to align the text outside icon margins
			this.getDetailsPanel().appendText(messages.getMessage("feed.sendMedia.and"));

			String numberOfTracksMessage = countTotalTracks > 1 ? "feed.sendMedia.numberOfTracks" : "feed.sendMedia.oneTrack";
			Integer numberOfTracks = countTotalTracks - tracksAdded;

			this.getDetailsPanel().appendLinkLibraryLabel(feed.getOwner(),
					messages.getMessage(numberOfTracksMessage, numberOfTracks.toString()));
			this.getDetailsPanel().appendText(messages.getMessage("feed.sendMedia.more"));
		}

		super.internationalize(messages);

	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("i18n.messages");
		Messages messages = new DefaultMessages(messageSource);
		// messages.setLocale(new java.util.Locale("es", "MX"));

		UIManager.setLookAndFeel("com.all.plaf.hipecotech.HipecotechLookAndFeel");

		ContactInfo owner = new ContactInfo();
		ContactInfo visited = new ContactInfo();
		owner.setGender(Gender.FEMALE);
		owner.setNickName("Escobula");
		visited.setGender(Gender.FEMALE);
		visited.setNickName("Escobucha");
		visited.setAvatar(ImageUtil.getDefaultAvatar());

		// String song1 = "song1";
		// String filename = "TestSong3";
		// String pathname = "src/test/resources/playlist/" + filename + ".mp3";
		// Track track = LocalTrack.createEmptyTrack(song1);
		Folder folder = new LocalFolder("folder");
		Playlist playlist = new LocalPlaylist("playlist");

		ModelCollection modelCollection = new ModelCollection();
		modelCollection.setFolders(Arrays.asList(folder));
		modelCollection.setPlaylists(Arrays.asList(playlist));

		SendMediaFeed sendContentFeed = new SendMediaFeed(owner, visited, modelCollection);
		sendContentFeed.setDate(new Date());
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SendMediaFeedView(sendContentFeed, messages, new ViewEngine() {

			@Override
			public <T> void sendValueAction(ActionType<ValueAction<T>> action, T argument) {
				// TODO Auto-generated method stub

			}

			@Override
			public void send(ActionType<EmptyAction> action) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T extends ActionObject> void send(ActionType<T> actionType, T parameter) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T> void request(ActionType<RequestAction<Void, T>> type, ResponseCallback<T> callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public <V, T> void request(ActionType<RequestAction<V, T>> type, V requestParameter, ResponseCallback<T> callback) {
				// TODO Auto-generated method stub

			}

			@Override
			public <T extends EventObject> void removeListener(EventType<T> currentviewchanged, Listener<T> listener) {
				// TODO Auto-generated method stub

			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> T get(ModelType<T> type) {
				return (T) new User();
			}

			@Override
			public <T extends EventObject> void addListener(EventType<T> currentviewchanged, Listener<T> listener) {
				// TODO Auto-generated method stub

			}
		}, null));
		frame.setSize(600, 130);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}

}
