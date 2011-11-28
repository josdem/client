package com.all.client.services;

import java.awt.Rectangle;

import javax.annotation.PostConstruct;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.control.ControlEngine;
import com.all.client.model.LocalDefaultSmartPlaylist;
import com.all.client.model.LocalModelDao;
import com.all.client.model.RepeatType;
import com.all.client.model.UserPreference;
import com.all.client.model.ViewColumnOptions;
import com.all.core.model.DisplayableMetadataFields;
import com.all.core.model.Model;
import com.all.shared.model.SyncValueObject;

@Service
public class UserPreferenceService {

	private final Log log = LogFactory.getLog(this.getClass());

	private final LocalModelDao dao;

	private UserPreference userPreference;

	@Autowired
	private ControlEngine controlEngine;

	@Autowired
	public UserPreferenceService(LocalModelDao dao) {
		this.dao = dao;
	}

	@PostConstruct
	public void initialize() {
		try {
			this.userPreference = dao.findById(UserPreference.class, 0);
			if (userPreference == null) {
				userPreference = new UserPreference();
				dao.save(userPreference);
			}
		} catch (Exception e) {
			log.error("Could not load user preferences.", e);
		}
		if (controlEngine != null) {
			controlEngine.set(Model.UserPreference.PLAYER_VOLUME, userPreference.getVolume(), null);
			controlEngine.set(Model.UserPreference.PLAYER_REPEAT_MODE, userPreference.getRepeat(), null);
			controlEngine.set(Model.UserPreference.PLAYER_SHUFFLE_MODE, isPlayerShuffleOption(), null);
			controlEngine.set(Model.UserPreference.SKIP_CONTACT_DELETION_CONFIRMATION, userPreference
					.isSkipDeleteContactConfirmation(), null);
			controlEngine.set(Model.UserPreference.SKIP_DRAG_CONTENT_TO_CONTACT_CONFIRMATION, userPreference
					.isSkipDragTracksToContactsConfirmation(), null);
			controlEngine.set(Model.UserPreference.DOWNLOAD_TABLE_SORT_COLUMN, getDownloadTablePrefferedSortKey(), null);
			controlEngine.set(Model.UserPreference.APPLICATION_BOUNDS, getScreenBounds(), null);
			controlEngine.set(Model.UserPreference.DISPLAYABLE_METADATA_FIELDS, userPreference.getViewColumnOptions(), null);
			controlEngine.set(Model.UserPreference.SKIP_COPY_REFERENCES_TO_USB_WARN, userPreference.isCopyGrayReferenceToUsbHidden(), null);
			controlEngine.set(Model.UserPreference.FACEBOOK_CHAT_STATUS, userPreference.isFacebookChatLogin(), null);
		}
	}

	private SortKey getDownloadTablePrefferedSortKey() {
		return new SortKey(userPreference.getDownloadTableSortColumn(),
				userPreference.isDownloadTableSortAscending() ? SortOrder.ASCENDING : SortOrder.DESCENDING);
	}

	public void updatePlayerVolume(int volume) {
		userPreference.setVolume(volume);
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.PLAYER_VOLUME, userPreference.getVolume(), null);
	}

	public int getPlayerVolume() {
		return userPreference.getVolume();
	}

	public boolean isPlayerShuffleOption() {
		return userPreference.getShuffle();
	}

	public RepeatType getPlayerRepeatMode() {
		return userPreference.getRepeat();
	}

	public RepeatType togglePlayerRepeatMode() {
		userPreference.toggleRepeat();
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.PLAYER_REPEAT_MODE, userPreference.getRepeat(), null);
		return getPlayerRepeatMode();
	}

	public boolean togglePlayerShuffle() {
		userPreference.toggleShuffle();
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.PLAYER_SHUFFLE_MODE, isPlayerShuffleOption(), null);
		return isPlayerShuffleOption();
	}

	public int togglePlayerMute() {
		int newVolume = userPreference.toggleMute();
		dao.update(userPreference);
		return newVolume;
	}

	public boolean isSmartPlaylistDeleted(LocalDefaultSmartPlaylist smartPlaylist) {
		return userPreference.isSmartPlaylistDeleted(smartPlaylist);
	}

	@ActionMethod(Model.UserPreference.SKIP_DRAG_CONTENT_TO_CONTACT_CONFIRMATION_ID)
	public void setSkipConfirmationForDraggingContentToContacts(Boolean skipped) {
		userPreference.setSkipDragTracksToContactsConfirmation(skipped);
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.SKIP_DRAG_CONTENT_TO_CONTACT_CONFIRMATION, userPreference
				.isSkipDragTracksToContactsConfirmation(), null);
	}

	@ActionMethod(Model.UserPreference.SKIP_CONTACT_DELETION_CONFIRMATION_ID)
	public void setSkipDeleteContactConfirmation(Boolean skipped) {
		userPreference.setSkipDeleteContactConfirmation(skipped);
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.SKIP_CONTACT_DELETION_CONFIRMATION, userPreference
				.isSkipDeleteContactConfirmation(), null);
	}

	@ActionMethod(Model.UserPreference.DOWNLOAD_TABLE_SORT_COLUMN_ID)
	public void setDownloadTablePreferredSort(SortKey sortKey) {
		userPreference.setDownloadTableSortColumn(sortKey.getColumn());
		userPreference.setDownloadTableSortAscending(sortKey.getSortOrder().equals(SortOrder.ASCENDING));
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.DOWNLOAD_TABLE_SORT_COLUMN, sortKey, null);
	}

	public Rectangle getScreenBounds() {
		return new Rectangle(userPreference.getScreenXPosition(), userPreference.getScreenYPosition(), userPreference
				.getScreenWidth(), userPreference.getScreenHeight());
	}

	@ActionMethod(Model.UserPreference.APPLICATION_BOUNDS_ID)
	public void setCurrentApplicationBounds(Rectangle bounds) {
		userPreference.setScreenXPosition(bounds.x);
		userPreference.setScreenYPosition(bounds.y);
		userPreference.setScreenWidth(bounds.width);
		userPreference.setScreenHeight(bounds.height);
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.APPLICATION_BOUNDS, bounds, null);
	}

	@ActionMethod(Model.UserPreference.DISPLAYABLE_METADATA_FIELDS_ID)
	public void updateViewColumnOptions(DisplayableMetadataFields fields) {
		userPreference.setViewColumnOptions((ViewColumnOptions) fields);
		dao.update(userPreference);
	}

	public int getCurrentSnapshot() {
		return userPreference.getSnapshotId();
	}

	public int getCurrentDelta() {
		return userPreference.getDeltaId();
	}

	public double getDeltasTotalSize() {
		return userPreference.getCurrentDeltaSizeBySnapshot();
	}

	public int getNextSnapshot() {
		return userPreference.getSnapshotId() + 1;
	}

	public void updateSyncStatus(SyncValueObject previousStatus, SyncValueObject newStatus) {
		if (isNewSnapshot(newStatus)) {
			log.info("Got new snapshot, saving it in user preferences... ");
			userPreference.setSnapshotId(newStatus.getSnapshot());
			userPreference.setCurrentDeltaSizeBySnapshot(0);
		} else if (!previousStatus.getEvents().isEmpty()) {
			userPreference.setCurrentDeltaSizeBySnapshot(userPreference.getCurrentDeltaSizeBySnapshot()
					+ (previousStatus.getEvents().get(0).getBytes().length / 1024.0));
		}

		userPreference.setDeltaId(newStatus.getDelta());
		dao.update(userPreference);
	}

	public boolean isNewSnapshot(SyncValueObject mergeResponse) {
		int currentSnapshot = userPreference.getSnapshotId();
		int newSnapshot = mergeResponse.getSnapshot();
		return newSnapshot > currentSnapshot;
	}

	@ActionMethod(Model.UserPreference.SKIP_COPY_REFERENCES_TO_USB_WARN_ID)
	public void setSkipCopyReferenceToUsbWarning(Boolean skip) {
		userPreference.setCopyGrayReferenceToUsbHidden(skip);
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.SKIP_COPY_REFERENCES_TO_USB_WARN, skip, null);		
	}

	public String getTwitterUser() {
		return userPreference.getTwitterUser();
	}

	public String getTwitterPassword() {
		return userPreference.getTwitterPassword();
	}

	public boolean isTwitterRememberMe() {
		return userPreference.isTwitterRememberMe();
	}

	public void setTwitterUser(String encrypt) {
		userPreference.setTwitterUser(encrypt);
		dao.update(userPreference);
	}

	public void setTwitterPassword(String encrypt) {
		userPreference.setTwitterPassword(encrypt);
		dao.update(userPreference);
	}

	public void setTwitterRememberMe(boolean rememberMe) {
		userPreference.setTwitterRememberMe(rememberMe);
		dao.update(userPreference);
	}

	public void setFacebookChatStatus(Boolean facebookChatStatus) {
		userPreference.setFacebookChatStatus(facebookChatStatus);
		dao.update(userPreference);
		controlEngine.set(Model.UserPreference.FACEBOOK_CHAT_STATUS, facebookChatStatus, null);
	}

	public boolean isFacebookChatLogin() {
		return userPreference.isFacebookChatLogin();
	}

	public void setPostAuthorization(boolean auth) {
		userPreference.setTwitterPostAuthorization(auth);
	}
	
	public boolean isPostTwitterAuthorized(){
		return userPreference.isTwitterPostAuthorized();
	}
}
