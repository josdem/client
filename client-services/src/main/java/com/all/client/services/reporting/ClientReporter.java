package com.all.client.services.reporting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.commons.Environment;
import com.all.core.common.model.ApplicationActions;
import com.all.core.common.services.reporting.Reporter;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.Category;
import com.all.shared.model.Playlist;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.DownloadStat;
import com.all.shared.stats.MediaContainerStat;
import com.all.shared.stats.MediaImportStat;
import com.all.shared.stats.UpdaterStat;
import com.all.shared.stats.MediaImportStat.ImportType;
import com.all.shared.stats.TopHundredStat;
import com.all.shared.stats.usage.UserActionStat;

@Service
public class ClientReporter {
	private String currentUserEmail = null;

	@Autowired
	private Reporter reporter;

	public synchronized void login(User user) {
		this.currentUserEmail = user.getEmail();
	}

	public synchronized void logout() {
		this.currentUserEmail = null;
	}

	public void log(AllStat stat) {
		reporter.log(stat);
	}

	public void logNewFolder() {
		if (currentUserEmail == null) {
			return;
		}
		reporter.log(new MediaContainerStat(currentUserEmail, 0, 1));
	}

	public void logNewPlaylist() {
		if (currentUserEmail == null) {
			return;
		}
		reporter.log(new MediaContainerStat(currentUserEmail, 1, 0));
	}

	@ActionMethod(ApplicationActions.REPORT_USER_ACTION_ID)
	@MessageMethod(MessEngineConstants.REPORT_USER_ACTION)
	public void logUserAction(Integer userAction) {
		if (currentUserEmail == null) {
			return;
		}
		UserActionStat userActionStat = new UserActionStat();
		userActionStat.setAction(userAction);
		userActionStat.setTimes(1);
		userActionStat.setEmail(currentUserEmail);
		reporter.log(userActionStat);
	}

	public void logDownloadAction(Integer userAction, String trackId) {
		if (currentUserEmail == null) {
			return;
		}
		reporter.log(new DownloadStat(currentUserEmail, userAction, trackId));
	}

	public void logImportEvent(ImportType importType, int importedTracks, int importedPlaylists, int importedFolders) {
		if (currentUserEmail == null) {
			return;
		}
		reporter.log(new MediaImportStat(currentUserEmail, importType, importedTracks, importedPlaylists, importedFolders));
		if (importedFolders > 0 || importedPlaylists > 0) {
			reporter.log(new MediaContainerStat(currentUserEmail, importedPlaylists, importedFolders));
		}
	}
	
	public void logUpdaterEvent(String version){
		if (currentUserEmail == null) {
			return;
		}
		reporter.log(new UpdaterStat(currentUserEmail, version, Environment.getPlatform()));
	}

	public void logTopHundredDownload(Category hundredCategory, Playlist hundredPlaylist) {
		reporter.log(new TopHundredStat(currentUserEmail, hundredCategory, hundredPlaylist));
	}
}
