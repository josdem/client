package com.all.client.view.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.validation.Validator;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;

import com.all.action.ResponseCallback;
import com.all.action.ValueAction;
import com.all.appControl.control.ViewEngine;
import com.all.browser.AllBrowser;
import com.all.client.model.Picture;
import com.all.client.model.ResizeImageType;
import com.all.client.view.DisplayBackPanel;
import com.all.client.view.MainFrame;
import com.all.client.view.alerts.AlertDrawerScrollPane;
import com.all.client.view.alerts.DrawerDialog;
import com.all.client.view.components.AllClientFrame;
import com.all.client.view.dialog.AddContactDialog.AddContactResult;
import com.all.client.view.dialog.CodecErrorDialog.CodecErrorType;
import com.all.client.view.dialog.DeleteDownloadsDialog.DeleteDownloadsAction;
import com.all.client.view.dialog.ReceiveContentDialog.ReceiveContentResult;
import com.all.client.view.dialog.TryAgainErrorDialog.Response;
import com.all.client.view.dnd.MultiLayerDropTargetListener;
import com.all.client.view.file.FileChooserDirectoryStrategy;
import com.all.client.view.file.FileChooserMusicFileStrategy;
import com.all.client.view.flows.AddContactFlow;
import com.all.client.view.flows.ImportItunesFlow;
import com.all.client.view.music.LocalDescriptionPanel;
import com.all.client.view.wizard.WizardDialog;
import com.all.commons.Environment;
import com.all.core.actions.Actions;
import com.all.core.common.bean.UpdateUserCommand;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.ErrorMessageEvent;
import com.all.core.events.Events;
import com.all.core.events.FacebookPostContentEvent;
import com.all.core.events.NetworkActionErrorEvent;
import com.all.core.events.SendContentEvent;
import com.all.core.model.ContactCollection;
import com.all.core.model.ContainerView;
import com.all.core.model.FacebookPost;
import com.all.core.model.Model;
import com.all.core.model.Views;
import com.all.event.EventExecutionMode;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;
import com.all.observ.ObservValue;
import com.all.observ.ObserveObject;
import com.all.observ.Observer;
import com.all.shared.alert.McRequestAlert;
import com.all.shared.messages.FriendshipRequestStatus;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.FeedTrack;
import com.all.shared.model.Folder;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.ModelTypes;
import com.all.shared.model.Playlist;
import com.all.shared.model.Track;
import com.all.shared.model.TrackContainer;
import com.all.shared.model.User;
import com.all.shared.newsfeed.RecommendedMediaFacebookFeed;
import com.all.shared.stats.FeedStat;
import com.all.shared.stats.usage.UserActions;

@Controller
public class DialogFactory implements ApplicationContextAware {

	private final Log log = LogFactory.getLog(this.getClass());

	private final Collection<AllDialog> currentDisplayedDialogs = Collections
			.synchronizedList(new ArrayList<AllDialog>());

	private final WindowListener currentDisplayedDialogsListener = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			currentDisplayedDialogs.remove(e.getWindow());
		}
	};

	private DrawerDialog drawerDialog;

	private WizardDialog wizardDialog;

	private ViewEngine viewEngine;

	private Messages messages;

	private MainFrame mainFrame;

	private AllBrowser demoBrowser;

	private LocalDescriptionPanel localDescriptionPanel;

	private Validator validator;

	private MultiLayerDropTargetListener multiLayerDropTargetListener;

	private WebBrowserDialog facebookBrowserDialog;

	private DisplayBackPanel displayBackPanel;

	private AlertDrawerScrollPane alertDrawerScrollPane;

	// END OF TODO

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		mainFrame = applicationContext.getBean(MainFrame.class);
		demoBrowser = applicationContext.getBean(AllBrowser.class);
		localDescriptionPanel = applicationContext.getBean(LocalDescriptionPanel.class);
		viewEngine = applicationContext.getBean(ViewEngine.class);
		messages = applicationContext.getBean(Messages.class);
		validator = applicationContext.getBean(Validator.class);
		multiLayerDropTargetListener = applicationContext.getBean(MultiLayerDropTargetListener.class);
		facebookBrowserDialog = applicationContext.getBean(WebBrowserDialog.class);
		displayBackPanel = applicationContext.getBean(DisplayBackPanel.class);
		alertDrawerScrollPane = applicationContext.getBean(AlertDrawerScrollPane.class);
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void closeCurrentDisplayedDialogs() {
		for (AllDialog dialog : new ArrayList<AllDialog>(currentDisplayedDialogs)) {
			dialog.closeDialog();
		}
		if (drawerDialog != null) {
			drawerDialog.destroy(viewEngine);
		}
	}

	private AddContactDialog getAddContactDialog(String keyword) {
		AddContactDialog addContactDialog;
		if (keyword == null) {
			addContactDialog = new AddContactDialog(mainFrame, messages, viewEngine);
		} else {
			addContactDialog = new AddContactDialog(mainFrame, messages, viewEngine, keyword);
		}
		addContactDialog.setVisible(true);
		return addContactDialog;
	}

	// TODO: REMOVE LOGIC FROM DIALOG FACTORY
	public final WizardDialog getWizardDialog() {
		if (wizardDialog == null) {
			if (!demoBrowser.isInitialized()) {
				viewEngine.sendValueAction(ApplicationActions.REPORT_USER_ACTION, UserActions.Errors.BROWSER_INITIZATION_FAIL);
			}
			wizardDialog = new WizardDialog(mainFrame, messages, localDescriptionPanel, demoBrowser);
			wizardDialog.internationalize(messages);
			wizardDialog.onItunesButton().add(new Observer<ObserveObject>() {
				@Override
				public void observe(ObserveObject eventArgs) {
					// viewEngine.send(Actions.Library.IMPORT_FROM_ITUNES, null);
					File itunesFile = new ImportItunesFlow(viewEngine).getiTunesLibraryFile();
					if (itunesFile == null) {
						itunesFile = showITunesFileChooserDialog();
						new ImportItunesFlow(viewEngine).importItunesFromFile();
					}
				}
			});
			wizardDialog.onFacebookCheckBox().add(new Observer<ObservValue<Boolean>>() {
				@Override
				public void observe(ObservValue<Boolean> eventArgs) {
					// boolean facebook = eventArgs.getValue();
					// DARKNESS::
					// userPreference.setFacebook(facebook);
					// modelDao.update(userPreference);
				}
			});
			wizardDialog.onMcEmailCheckBox().add(new Observer<ObservValue<Boolean>>() {
				@Override
				public void observe(ObservValue<Boolean> eventArgs) {
					// boolean mcEmail = eventArgs.getValue();
					// DARKNESS
					// userPreference.setMcEmail(mcEmail);
					// modelDao.update(userPreference);
				}
			});
		}
		wizardDialog.setLocationRelativeTo(mainFrame);
		return wizardDialog;
	}

	@EventMethod(Events.Library.IMPORTING_ITUNES_LIBRARY_DONE_ID)
	public void onImportItunesLibraryDone() {
		getWizardDialog().enableItunesButton();

	}

	public UpdateUserCommand showEditProfiledialog(boolean mainFrame) {
		Frame frame = this.mainFrame;
		EditProfileDialog editProfileDialog = new EditProfileDialog(frame, messages, validator,
				multiLayerDropTargetListener, this, viewEngine);
		UpdateUserCommand userCommand = editProfileDialog.getUserCommand();
		return userCommand;
	}

	public void showSendInvitationDialog(String toEmail) {
		SendJoinInvitationDialog dialog = new SendJoinInvitationDialog(mainFrame, messages, toEmail, viewEngine);
		dialog.setVisible(true);
	}

	@EventMethod(Events.Social.EMAIL_INVITATION_SENT_ID)
	public void showConfirmationMessage(List<String> emails) {
		String message = emails.size() == 1 ? messages.getMessage("sendInvitation.single.confirmedMessage", emails.get(0))
				: messages.getMessage("sendInvitation.confirmedMessages", emails.size() + " e-mails addresses");
		new MessageDialog(mainFrame, messages, message, messages.getMessage("sendInvitation.sentMailTitle"), SynthFonts.PLAIN_FONT12_PURPLE50_15_50);
	}

	@EventMethod(Events.Social.REQUEST_FRIENDSHIP_RESPONSE_ID)
	public void showInvitationMessageDialog(FriendshipRequestStatus addFriendResult) {
		AllClientFrame parent = mainFrame;
		InvitationMessageDialog invitationMessageDialog = new InvitationMessageDialog(parent, messages, addFriendResult);
		invitationMessageDialog.setVisible(true);
	}

	public boolean showTracksToContactsConfirmation(String name) {
		return new ConfirmTracksToContactDialog(mainFrame, messages, name, viewEngine).getAnswer();
	}

	public boolean showDeleteContactConfirmationDialog(boolean allowSkip) {
		return new ConfirmationDeleteContactFromFolderDialog(mainFrame, messages, allowSkip, viewEngine).showDialog() == ConfirmationDeleteContactFromFolderDialog.APPROVE_OPTION;
	}

	@EventMethod(Events.Application.GENERIC_ERROR_MESSAGE_ID)
	public void onGenericErrorMessage(String messageKey) {
		new MessageDialog(null, messages, messages.getMessage(messageKey));
	}

	@EventMethod(Events.Downloads.TRACK_ALREADY_AVAILABLE_ID)
	public void onTrackAlreadyAvailable(){
		new MessageDialog(null, messages, messages.getMessage("dialog.available.track.message"),  messages.getMessage("import"), SynthFonts.PLAIN_FONT10_107_83_125);
	}
	@EventMethod(Events.AutoUpdate.UPDATE_DOWNLOAD_ERROR_ID)
	public void onAutoUpdateDownloadError(String error) {
		onGenericErrorMessage("dialog.autoupdate.error");
	}

	public void showMessageDialog(String message) {
		new MessageDialog(mainFrame, messages, message);
	}

	public final void showMessageDialog(List<File> files) {
		if (mainFrame.isVisible()) {
			new MessageDialog(mainFrame, messages, files);
		}
	}

	public void showExceptionDialog(Exception e) {
		new MessageDialog(mainFrame, messages, messages.getMessage("error.unexpected", e.getMessage()));
	}

	public boolean showConfirmationDeleteDialog(ModelCollection model) {
		ConfirmationDeleteDialog confirmationDeleteDialog = new ConfirmationDeleteDialog(mainFrame, messages,
				model.has(ModelTypes.tracks));
		int dialogResult = confirmationDeleteDialog.showDialog();
		return dialogResult == ConfirmationDeleteDialog.APPROVE_OPTION;
	}

	public boolean showConfirmationDialog(String messageKey, String titleKey, String yesButton) {
		ConfirmationGenericDialog confirmationDeleteDialog = new ConfirmationGenericDialog(mainFrame, messages, messageKey,
				titleKey, yesButton);
		return confirmationDeleteDialog.showDialog() == ConfirmationDeleteDialog.APPROVE_OPTION;
	}

	public int showDeleteDialog(ModelCollection model) {
		DeleteDialog deleteDialog = DeleteDialog.getDialog(mainFrame, model.has(ModelTypes.playlists),
				model.has(ModelTypes.folders), model.has(ModelTypes.tracks), messages);
		return deleteDialog.showDialog();
	}

	public final void showInfoDialog(String messageKey, String titleKey) {
		new InfoDialog(mainFrame, messages, messageKey, titleKey).setVisible(true);
	}

	public void showLongInfoDialog(String messageKey, String titleKey, int width, int height) {
		new LongInfoDialog(null, messages, messageKey, titleKey, width, height).setVisible(true);
	}

	public void showErrorDialog(String key, String... parameters) {
		new ErrorDialog(null, messages, key, parameters).setVisible(true);
	}

	@EventMethod(Events.Errors.ERROR_MESSAGE_ID)
	public void onErrorMessage(ErrorMessageEvent event) {
		if (event.hasParameters()) {
			new ErrorDialog(null, messages, event.getMessageKey(), event.getStringParameters()).setVisible(true);
		} else {
			new ErrorDialog(null, messages, event.getMessageKey()).setVisible(true);
		}
	}

	public void onErrorMessage(ValueEvent<String> error) {
		showErrorDialog(error.getValue());
	}

	public void showErrorDialog(String key) {
		new ErrorDialog(mainFrame, messages, key).setVisible(true);
	}

	public final Response showTryAgainError(String errorKey) {
		TryAgainErrorDialog tryAgainErrorDialog = new TryAgainErrorDialog(mainFrame, messages, errorKey);
		tryAgainErrorDialog.setVisible(true);
		return tryAgainErrorDialog.getResult();
	}

	public File showDirChooserDialog() {
		FileChooser chooser = new FileChooser(mainFrame, messages, new FileChooserDirectoryStrategy(),
				"menu.file.findLocalTracks.title");
		log.debug(chooser.isApproved());
		log.debug("Selected file: " + chooser.getSelectedFile());
		if (chooser.isApproved()) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	public File showITunesFileChooserDialog() {
		ITunesFileChooser chooser = new ITunesFileChooser(mainFrame, messages);
		if (chooser.isApproved()) {
			return chooser.getSelectedLibrary();
		}
		return null;
	}

	public final void showPrivacyDialog() {
		new NewAccountPrivacyDialog(mainFrame, messages);
	}

	@EventMethod(Model.DRAWER_DISPLAYED_ID)
	public void onDrawerDialogDisplayChange(ValueEvent<Boolean> event) {
		getDrawerDialog().setVisible(event.getValue());
		displayBackPanel.recalculateDrawerBounds();
	}

	@EventMethod(Events.Application.APP_CLOSE_ID)
	public void onApplicationClose() {
		getDrawerDialog().setVisible(false);
	}

	public DrawerDialog getDrawerDialog() {
		if (drawerDialog == null && mainFrame != null && displayBackPanel != null) {
			drawerDialog = new DrawerDialog(mainFrame, alertDrawerScrollPane, viewEngine, messages);
			drawerDialog.initialize(viewEngine);
		}
		return drawerDialog;
	}

	@EventMethod(Events.Errors.NETWORK_REQUIRED_FOR_ACTION_ID)
	public void onActionNetworkError(NetworkActionErrorEvent event) {
		switch (event.getAction()) {
		case DISPLAY_DRAWER:
			showAlertsUnavailableDialog();
			break;
		}
	}

	public final Image showEditPhotoDialog(Picture picture, ResizeImageType resizeImageType) {
		return new EditPhotoDialog(mainFrame, messages, picture).croppedImage(resizeImageType);
	}

	public void showImportContactsDialog(List<ContactInfo> contacts) {
		ImportContactsDialog importContactsDialog = new ImportContactsDialog(mainFrame, messages, contacts,
				localDescriptionPanel, viewEngine, this);
		importContactsDialog.onClose().add(new Observer<ObserveObject>() {
			@Override
			public void observe(ObserveObject eventArgs) {
				viewEngine.send(Actions.View.setCurrentView, new ValueAction<ContainerView>(
						new ContainerView(Views.LOCAL_MUSIC)));
			}
		});
		importContactsDialog.setVisible(true);
	}

	@EventMethod(Events.Social.SHOW_SEND_CONTENT_DIALOG_ID)
	public void showSendContentDialog(SendContentEvent event) {
		List<ContactInfo> contacts = event.getContacts();
		ContactCollection contactCollection = contacts == null ? new ContactCollection() : new ContactCollection(contacts);
		new SendContentDialog(mainFrame, event.getModel(), contactCollection, messages, viewEngine, this).setVisible(true);
	}

	public void showReceiveContentDialog(ModelCollection model, Messages messages,
			Observer<ObservValue<ReceiveContentResult>> listener) {
		DrawerDialog dialog = getDrawerDialog();
		int x = dialog.getLocation().x + ((dialog.getWidth() - 360) / 2);
		Point location = new Point(x, mainFrame.getLocation().y + 200);
		ReceiveContentDialog receiveContentDialog = new ReceiveContentDialog(model, messages, location,
				viewEngine.get(Model.TRACK_REPOSITORY));
		receiveContentDialog.setDialogListener(listener);
		receiveContentDialog.addWindowListener(currentDisplayedDialogsListener);
		currentDisplayedDialogs.add(receiveContentDialog);
		receiveContentDialog.setVisible(true);
	}

	public void showSendMultipleInvitationDialog(List<String> emails) {
		new SendMultipleInvitationDialog(mainFrame, messages, emails, viewEngine);
	}

	public DeleteDownloadsAction showDeleteDownloadsDialog() {
		DeleteDownloadsDialog dialog = new DeleteDownloadsDialog(mainFrame, messages);
		dialog.setVisible(true);
		return dialog.getAction();
	}

	@EventMethod(value = Events.Errors.IMPORT_FROM_ITUNES_MISSING_FILES_ID, mode = EventExecutionMode.ASYNC)
	public void onUnimportedItunesFiles(ValueEvent<ModelCollection> event) {
		ModelCollection collection = event.getValue();
		UnimportedItunesDialog dialog = new UnimportedItunesDialog(mainFrame, collection, messages, viewEngine);
		dialog.setVisible(true);

	}

	public final void showContactListUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.contact.subtitle", "networkOff.contact.message");
	}

	public void showProfileUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.profile.subtitle", "networkoff.profile.message");
	}

	private void showNetworkMessageDialog(SendContentDialog dialog, String subtitle, String message) {
		if (dialog == null) {
			new NetworkConnectionMessageDialog(mainFrame, messages, "networkOff.title", subtitle, message).setVisible(true);
		} else {
			new NetworkConnectionMessageDialog(dialog, messages, "networkOff.title", subtitle, message).setVisible(true);
		}
	}

	public void showSearchUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.search.title", "networkOff.search.message");
	}

	public void showAlertsUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.alert.subtitle", "networkOff.alert.message");
	}

	public void showNetworkUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.content.subtitle", "networkOff.content.message");
	}

	public void showGenericNetworkUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.generic.subtitle", "networkOff.generic.message");
	}

	public void showP2PNetworkUnavailableDialog() {
		showNetworkMessageDialog(null, "networkOff.p2p.title", "networkOff.p2p.message");
	}

	public void showAddAsAFriendProfileDialog(ContactInfo contactInfo, Component parent) {
		final int distanceParentModifier = 6;
		final int minHeight = 24;

		AddContactFlow addContactFlow = new AddContactFlow(viewEngine, this);
		Dialog dialog = new AddAsAFriendProfileDialog(mainFrame, contactInfo, messages, addContactFlow);
		Point locationOnScreen = parent.getLocationOnScreen();
		int height = (int) locationOnScreen.getY() + parent.getHeight() - dialog.getHeight() + distanceParentModifier;
		int y = height < minHeight ? minHeight : height;
		int x = (int) locationOnScreen.getX() + parent.getWidth() - dialog.getWidth() + distanceParentModifier;
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}

	public void showNotEnoughSpaceDialog(Dialog parent) {
		NotEnoughSpaceDialog notEnoughSpaceDialog = new NotEnoughSpaceDialog(parent, messages);
		notEnoughSpaceDialog.setVisible(true);
	}

	public DeviceCopyDialog getCopyProgressDialog() {
		return new DeviceCopyDialog(mainFrame, messages, viewEngine);
	}

	public void showUnableToCopyGrayReferencesDialog(Dialog parent) {
		if (!viewEngine.get(Model.UserPreference.SKIP_COPY_REFERENCES_TO_USB_WARN)) {
			CannotCopyGrayReferenceDialog notEnoughSpaceDialog = new CannotCopyGrayReferenceDialog(parent, messages,
					viewEngine);
			notEnoughSpaceDialog.setVisible(true);
		}
	}

	public boolean showDeleteUsbContentDialog() {
		DeleteUsbContentDialog dialog = new DeleteUsbContentDialog(mainFrame, messages);
		dialog.setVisible(true);
		return dialog.getUserSelection();
	}

	public void showCodecDialog(String extension) {
		CodecErrorType type = null;
		if (Environment.isMac()) {
			if (extension != null && extension.toLowerCase().contains("wma")) {
				type = CodecErrorType.FLIP4MAC;
			} else {
				type = CodecErrorType.PERIAN;
			}
		}
		if (Environment.isWindows()) {
			type = CodecErrorType.KLITE;
		}
		if (type != null) {
			new CodecErrorDialog(mainFrame, messages, type).setVisible(true);
		}
	}

	public boolean isTwitterLoggedIn() {
		if (BooleanUtils.isTrue(viewEngine.get(Model.TWITTER_LOGGED_IN))) {
			return true;
		}

		TwitterRegistrationDialog dialog = new TwitterRegistrationDialog(mainFrame, messages, viewEngine);
		dialog.setVisible(true);

		return BooleanUtils.isTrue(viewEngine.get(Model.TWITTER_LOGGED_IN));
	}

	public void showFacebookAuthenticationDialog() {
		FacebookAuthenticationDialog dialog = new FacebookAuthenticationDialog(mainFrame, messages,
				viewEngine.get(Model.CURRENT_USER), viewEngine);
		dialog.setVisible(true);
	}

	public void showTwitterNowPlayingDialog(Track track) {
		showTwitterUpdateStatusDialog(
				messages.getMessage("twitter.hashtag.nowplaying", track.getArtist(), track.getName()),
				UserActions.SocialNetworks.TWITTER_TRACK, track.getHashcode());
	}

	public void showTwitterRecommendationDialog(Track track) {
		showTwitterUpdateStatusDialog(messages.getMessage("twitter.hashtag.goodmusic", track.getArtist(), track.getName()),
				UserActions.SocialNetworks.TWITTER_RECOMMENDATION, track.getHashcode());
	}

	public void showTwitterUpdateStatusDialog(String status, Integer actionType, String hashcode) {
		TwitterUpdateStatusDialog dialog = new TwitterUpdateStatusDialog(mainFrame, messages, status, actionType,
				viewEngine, hashcode);
		dialog.setVisible(true);
	}

	@EventMethod(Events.Social.TWITTER_ERROR_ID)
	public void showTwitterErrorDialog(Integer errorCode) {
		TwitterErrorDialog dialog = new TwitterErrorDialog(mainFrame, messages, errorCode);
		dialog.setVisible(true);
	}

	@EventMethod(Events.Alerts.CONFIRM_REQUEST_ALERT_ID)
	public void showAskForContentDialog(McRequestAlert alert) {
		AskForContentDialog askForContentDialog = new AskForContentDialog(mainFrame, messages);
		askForContentDialog.setVisible(true);
		if (askForContentDialog.shouldRequest()) {
			viewEngine.sendValueAction(Actions.Alerts.SEND_REQUEST_ALERT, alert);
		}
	}

	public boolean showSomeReferencesErrorDialog(Integer referenceTrackCount) {
		CancelUploadDialog cancelUploadDialog = new CancelUploadDialog(mainFrame, messages, messages.getMessage(
				"sendContent.some.references.error", referenceTrackCount.toString()),
				messages.getMessage("sendContent.some.references.error.detail"),
				messages.getMessage("sendContent.error.title"), messages.getMessage("sendContent.error.send.anyway.button"),
				messages.getMessage("sendContent.error.cancel.button"));
		cancelUploadDialog.setVisible(true);
		return cancelUploadDialog.getResponse();
	}

	public void showReferencesOnlyErrorDialog() {
		new McSendErrorDialog(mainFrame, messages, messages.getMessage("sendContent.references.only.error"),
				messages.getMessage("sendContent.references.only.error.detail"),
				messages.getMessage("sendContent.error.title"), messages.getMessage("sendContent.error.back.button"))
				.setVisible(true);
	}

	public boolean showCloseApplicationDialog() {
		CloseApplicationDialog dialog = new CloseApplicationDialog(mainFrame, messages);
		dialog.setVisible(true);
		return dialog.isClosing();
	}

	public boolean showCancelUploadDialog() {
		CancelUploadDialog cancelUploadDialog = new CancelUploadDialog(mainFrame, messages,
				messages.getMessage("sendContent.cancel.confirm.title"),
				messages.getMessage("sendContent.cancel.confirm.warning"),
				messages.getMessage("sendContent.cancel.confirm.dialogTitle"),
				messages.getMessage("sendContent.cancel.confirm.yesButtonMsg"),
				messages.getMessage("sendContent.cancel.confirm.noButtonMsg"));
		cancelUploadDialog.setVisible(true);
		return cancelUploadDialog.getResponse();
	}

	public void showEmailErrorDialog() {
		new McSendErrorDialog(mainFrame, messages, messages.getMessage("crawler.email.error.dialog.only.error"),
				messages.getMessage("crawler.email.error.dialog.errorDetail"),
				messages.getMessage("crawler.email..error.dialog.title"), messages.getMessage("ok")).setVisible(true);
	}

	@EventMethod(Events.AutoUpdate.UPDATE_DOWNLOAD_COMPLETED_ID)
	public void showAutoUpdateDialog() {
		new AutoUpdateDialog(mainFrame, messages).setVisible(true);
	}

	@EventMethod(Events.Errors.SYNC_DOWNLOAD_FAILED_ID)
	public void onSyncError() {
		showErrorDialog("sync.error.noresponse");
	}

	@EventMethod(Events.Errors.DEVICE_FULL_ID)
	public void onDeviceFull() {
		// WHAT 9000!?
	}

	@EventMethod(Events.Errors.EXCEPTION_ID)
	public void onException(Exception e) {
		showExceptionDialog(e);
	}

	@EventMethod(Events.Errors.MODEL_IMPORT_INVALID_FILES_ID)
	public void onModelImportFiles(List<File> files) {
		showMessageDialog(files);
	}

	public AddContactResult showAddContactDialog(String keyword) {
		return getAddContactDialog(keyword).getResult();
	}

	@EventMethod(Events.Social.SHOW_POST_CONTENT_ON_FACEBOOK_DIALOG_ID)
	public void showFacebookContentDialog(FacebookPostContentEvent postContent) {
		if (!BooleanUtils.isTrue(viewEngine.get(Model.FACEBOOK_AUTHORIZED))) {
			showFacebookLoginDialog();
		}
		ModelCollection model = postContent.getModel();
		String post = showFacebookPostConfirmationDialog(model);
		User user = viewEngine.get(Model.CURRENT_USER);
		ContactInfo owner = new ContactInfo(user);

		viewEngine.send(Actions.Facebook.POST_TO_FACEBOOK,
				new ValueAction<FacebookPost>(new FacebookPost(postContent.getContact(), post)));

		RecommendedMediaFacebookFeed recommendedMediaFacebookFeed = createRecommendedMediaFacebookFeed(postContent, model,
				owner);

		viewEngine.sendValueAction(ApplicationActions.REPORT_USER_STAT, new FeedStat(recommendedMediaFacebookFeed));

	}

	private RecommendedMediaFacebookFeed createRecommendedMediaFacebookFeed(FacebookPostContentEvent postContent,
			ModelCollection model, ContactInfo owner) {

		List<String> folders = new ArrayList<String>();
		List<Folder> modelFolders = model.getFolders();
		List<String> playlists = new ArrayList<String>();
		List<Playlist> modelPlaylists = model.getPlaylists();
		List<FeedTrack> feedTracks = new ArrayList<FeedTrack>();
		List<Track> modelTracks = model.getTracks();

		int i = 0;

		for (int j = 0; i < RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES && j < modelFolders.size(); i++, j++) {
			folders.add(modelFolders.get(j).getName());
		}

		for (int j = 0; i < RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES && j < modelPlaylists.size(); i++, j++) {
			playlists.add(modelPlaylists.get(j).getName());
		}

		for (int j = 0; i < RecommendedMediaFacebookFeed.MAXIMUM_ENTITIES && j < modelTracks.size(); i++, j++) {
			Track track = modelTracks.get(j);
			feedTracks.add(new FeedTrack(track.getHashcode(), track.getName(), track.getArtist()));
		}

		RecommendedMediaFacebookFeed recommendedMediaFacebookFeed = new RecommendedMediaFacebookFeed(owner, postContent
				.getContact().getChatName(), model.trackCount(), folders, playlists, feedTracks);
		return recommendedMediaFacebookFeed;
	}

	@EventMethod(Events.Chat.DISPLAY_FACEBOOK_AUTHORIZATION_DIALOG_ID)
	public void showFacebookServiceAuthorized() {
		if (!BooleanUtils.isTrue(viewEngine.get(Model.FACEBOOK_AUTHORIZED))) {
			showFacebookLoginDialog();
		} else {
			viewEngine.send(Actions.Facebook.CHAT_LOGIN);
		}
	}

	public boolean showFacebookLoginDialog() {
		// Event Display Auhorization Dialog Window
		viewEngine.request(Actions.Facebook.GET_AUTH_AND_REDIRECT_URLS, new ResponseCallback<String[]>() {
			@Override
			public void onResponse(String[] urls) {
				String authorizationUrl = urls[0];
				String redirectUrl = urls[1];
				facebookBrowserDialog.load(authorizationUrl, redirectUrl);
			}
		});

		final CountDownLatch latch = new CountDownLatch(1);
		facebookBrowserDialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				latch.countDown();
			}

		});

		facebookBrowserDialog.setVisible(true);
		String responseUrl = facebookBrowserDialog.getResponseUrl();
		log.info("response: " + responseUrl);
		if (responseUrl == null) {
			return false;
		}

		final CountDownLatch countDownLatch = new CountDownLatch(1);

		viewEngine.request(Actions.Facebook.AUTHORIZE, responseUrl, new ResponseCallback<Void>() {
			@Override
			public void onResponse(Void v) {
				countDownLatch.countDown();
			}
		});
		try {
			countDownLatch.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}

		return true;
	}

	public String showFacebookPostConfirmationDialog(ModelCollection model) {
		FacebookPostConfirmationDialog facebookPostConfirmationDialog = new FacebookPostConfirmationDialog(mainFrame,
				messages, model);
		facebookPostConfirmationDialog.setVisible(true);
		return facebookPostConfirmationDialog.getResult();
	}

	public final File[] showFileChooserDialog() {
		FileChooser chooser = new FileChooser(mainFrame, messages, new FileChooserMusicFileStrategy(), "importTrack.Title");
		if (chooser.isApproved()) {
			return chooser.getSelectedTracks();
		}
		return null;
	}

	public FileDialogResult showImportFolderDialog(TrackContainer currentlySelectedItem) {
		FileChooser chooser = new FileChooser(mainFrame, messages, new FileChooserDirectoryStrategy(), "importTrack.Title");
		log.debug("chooser:" + chooser.getName());
		if (chooser.isApproved()) {
			FileDialogResult fileDialogResult = new FileDialogResult(JFileChooser.APPROVE_OPTION, chooser.getSelectedFile());
			return fileDialogResult;
		}
		return null;
	}

}