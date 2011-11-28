package com.all.client.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;


@Entity
public class UserPreference {

	private static final long serialVersionUID = 1L;
	private static final int INITIAL_VOLUME = 70;
	@Id
	private int id;
	private int repeat;
	private boolean shuffle;
	private int volume = INITIAL_VOLUME;
	@Transient
	private int oldVolume = INITIAL_VOLUME;
	private boolean crappySmartPlaylistDeleted;
	private boolean trackInTitleSmartPlaylistDeleted;
	private int screenXPosition;
	private int screenYPosition;
	private int screenWidth;
	private int screenHeight;
	private boolean skipDeleteContactConfirmation = false;
	private boolean skipDragTracksToContactsConfirmation = false;
	private int snapshotId;
	private int deltaId;
	private double currentDeltaSizeBySnapshot;
	private boolean skipPortForwardingWizard = false;

	private String twitterUser;
	private String twitterPassword;
	private Boolean twitterRememberMe;
	private Boolean twitterPostAutorization = false;
	
	private Boolean facebookChatStatus;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "VCOL_ID")
	private ViewColumnOptions viewColumnOptions;

	private int downloadTableSortColumn = 0;
	private boolean downloadTableSortAscending = true;
	private boolean copyGrayReferenceToUsbHidden = false;

	public final int getScreenXPosition() {
		return screenXPosition;
	}

	public final void setScreenXPosition(int screenXPosition) {
		this.screenXPosition = screenXPosition;
	}

	public final int getScreenYPosition() {
		return screenYPosition;
	}

	public final void setScreenYPosition(int screenYPosition) {
		this.screenYPosition = screenYPosition;
	}

	public final int getScreenWidth() {
		return screenWidth;
	}

	public final void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public final int getScreenHeight() {
		return screenHeight;
	}

	public final void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public final void setRepeat(RepeatType repeatType) {
		repeat = repeatType.value();
	}

	public final int id() {
		return id;
	}

	public RepeatType getRepeat() {
		return RepeatType.valueOf(repeat);
	}

	public final void toggleRepeat() {
		repeat++;
		if (repeat > RepeatType.ONE.value()) {
			repeat = RepeatType.OFF.value();
		}
	}

	public final void setShuffle(boolean shuffleState) {
		shuffle = shuffleState;
	}

	public boolean getShuffle() {
		return shuffle;
	}

	public final void toggleShuffle() {
		shuffle = !shuffle;
	}

	public int getVolume() {
		return volume;
	}

	public final void setVolume(int volume) {
		this.volume = volume;
	}

	public int toggleMute() {
		if (this.volume > 0) {
			this.oldVolume = this.volume;
			this.volume = 0;
		} else {
			this.volume = oldVolume;
			this.oldVolume = 70;
		}

		return volume;
	}

	@Override
	public final boolean equals(Object o) {
		boolean result = false;
		if (o instanceof UserPreference) {
			UserPreference up = (UserPreference) o;
			result = up.id == this.id;
		}
		return result;
	}

	@Override
	public final int hashCode() {
		return id == 0 ? super.hashCode() : Integer.valueOf(id).hashCode();
	}

	public final void smartPlaylistDeleted(LocalDefaultSmartPlaylist smartPlaylist) {
		if (smartPlaylist == LocalDefaultSmartPlaylist.CRAPPY_KBPS) {
			crappySmartPlaylistDeleted = true;
			return;
		}
		if (smartPlaylist == LocalDefaultSmartPlaylist.TRACK_IN_TITLE) {
			trackInTitleSmartPlaylistDeleted = true;
			return;
		}
		throw new IllegalArgumentException("The smart playlist can not be deleted: " + smartPlaylist);
	}

	public final boolean isSmartPlaylistDeleted(LocalDefaultSmartPlaylist smartPlaylist) {
		if (smartPlaylist == LocalDefaultSmartPlaylist.CRAPPY_KBPS) {
			return crappySmartPlaylistDeleted;
		}
		if (smartPlaylist == LocalDefaultSmartPlaylist.TRACK_IN_TITLE) {
			return trackInTitleSmartPlaylistDeleted;
		}
		return false;
	}

	public final void setSkipDeleteContactConfirmation(boolean skipDeleteContactConfirmation) {
		this.skipDeleteContactConfirmation = skipDeleteContactConfirmation;
	}

	public boolean isSkipDeleteContactConfirmation() {
		return skipDeleteContactConfirmation;
	}

	public final void setSkipDragTracksToContactsConfirmation(boolean skipDragTracksToContactsConfirmation) {
		this.skipDragTracksToContactsConfirmation = skipDragTracksToContactsConfirmation;
	}

	public boolean isSkipDragTracksToContactsConfirmation() {
		return this.skipDragTracksToContactsConfirmation;
	}

	public int getDownloadTableSortColumn() {
		return downloadTableSortColumn;
	}

	public final void setDownloadTableSortColumn(int downloadTableSortColumn) {
		this.downloadTableSortColumn = downloadTableSortColumn;
	}

	public boolean isDownloadTableSortAscending() {
		return downloadTableSortAscending;
	}

	public final void setDownloadTableSortAscending(boolean downloadTableSortAscending) {
		this.downloadTableSortAscending = downloadTableSortAscending;
	}

	public ViewColumnOptions getViewColumnOptions() {
		if (viewColumnOptions == null) {
			this.viewColumnOptions = new ViewColumnOptions();
		}
		return viewColumnOptions;
	}

	public int getSnapshotId() {
		return snapshotId;
	}

	public void setSnapshotId(int snapshot) {
		this.snapshotId = snapshot;
	}

	public int getDeltaId() {
		return deltaId;
	}

	public void setDeltaId(int delta) {
		this.deltaId = delta;
	}

	public void setCurrentDeltaSizeBySnapshot(double deltaSizeCurrentSnapshot) {
		this.currentDeltaSizeBySnapshot = deltaSizeCurrentSnapshot;
	}

	public double getCurrentDeltaSizeBySnapshot() {
		return currentDeltaSizeBySnapshot;
	}

	public final void setSkipPortForwardingWizard(boolean skipPortForwardingWizard) {
		this.skipPortForwardingWizard = skipPortForwardingWizard;
	}

	public final boolean isSkipPortForwardingWizard() {
		return skipPortForwardingWizard;
	}

	public void setCopyGrayReferenceToUsbHidden(boolean copyGrayReferenceToUsbHidden) {
		this.copyGrayReferenceToUsbHidden = copyGrayReferenceToUsbHidden;
	}

	public boolean isCopyGrayReferenceToUsbHidden() {
		return copyGrayReferenceToUsbHidden;
	}

	public void setViewColumnOptions(ViewColumnOptions viewColumnOptions) {
		this.viewColumnOptions = viewColumnOptions;
	}

	public String getTwitterUser() {
		return twitterUser;
	}

	public void setTwitterUser(String twitterUser) {
		this.twitterUser = twitterUser;
	}

	public String getTwitterPassword() {
		return twitterPassword;
	}

	public void setTwitterPassword(String twitterPassword) {
		this.twitterPassword = twitterPassword;
	}

	public boolean isTwitterRememberMe() {
		return twitterRememberMe == null ? false : twitterRememberMe.booleanValue();
	}

	public void setTwitterRememberMe(boolean twitterRememberMe) {
		this.twitterRememberMe = twitterRememberMe;
	}

	public void setFacebookChatStatus(Boolean facebookChatStatus) {
		this.facebookChatStatus = facebookChatStatus;
	}

	public boolean isFacebookChatLogin() {
		return facebookChatStatus == null ? false : facebookChatStatus.booleanValue();
	}

	public void setTwitterPostAuthorization(boolean auth) {
		this.twitterPostAutorization = auth;
	}
	
	public boolean isTwitterPostAuthorized() {
		return twitterPostAutorization;
	}
}
