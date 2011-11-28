package com.all.client.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ViewEngine;
import com.all.client.model.Download;
import com.all.client.util.Formatters;
import com.all.client.view.format.RateFormater;
import com.all.client.view.i18n.Ji18nLabel;
import com.all.core.common.model.ApplicationModel;
import com.all.core.common.spring.InitializeService;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.events.LibrarySyncEvent;
import com.all.core.events.LibrarySyncEventType;
import com.all.core.events.LibrarySyncProgressEvent;
import com.all.core.events.UploadContentProgressEvent;
import com.all.core.model.Model;
import com.all.core.model.SearchState;
import com.all.downloader.bean.DownloadState;
import com.all.event.EventExecutionMode;
import com.all.event.EventMethod;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

@Component
public class BottomPanel extends JPanel implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private static final int HEIGHT = 26;

	private static final Dimension DEFAULT_SIZE = new Dimension(1016, HEIGHT);

	private static final Dimension INFO_PANEL_DEFAULT_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);

	private static final Dimension MINIMUM_SIZE = new Dimension(792, HEIGHT);

	private static final Dimension SEPARATOR_PANEL_PREFERRED_SIZE = new Dimension(2, 28);

	private static final Dimension STATUS_PANEL_DEFAULT_SIZE = new Dimension(202, HEIGHT);

	private static final Rectangle ICON_LABEL_BOUNDS = new Rectangle(0, 0, 30, 26);

	private static final Rectangle RATE_LABEL_BOUNDS = new Rectangle(40, -1, 162, 26);

	private static final Rectangle SEPARATOR_INNER_PANEL_BOUNDS = new Rectangle(200, -3, 2, 30);

	private static final Rectangle STATUS_LABEL_BOUNDS = new Rectangle(40, -1, 92, 28);

	private static final String ICON_LABEL_OFFLINE = "iconLabelOffline";

	private static final String SEPARATOR_PANEL_NAME = "verticalSeparator";

	private Log log = LogFactory.getLog(getClass());

	private JPanel statusPanel;
	private JPanel infoPanel;
	private JPanel separatorInnerPanel;
	private JPanel infoLabelPanel;

	private JLabel iconLabel;
	private Ji18nLabel infoLabel;
	private Ji18nLabel rateLabel;
	private Ji18nLabel statusLabel;

	private Map<String, SyncParams> syncParams = new HashMap<String, SyncParams>();
	private String currentSyncLib = "";

	// This lock and the Swing workers are used to show the library progress bar
	// 1 second at least.
	private final Object libraryProgressBarLock = new Object();

	HashSet<Download> downloadsInProcess = new HashSet<Download>();

	private RateFormater formatter = new RateFormater();

	private JPanel activityPanel;

	private final Object importITunesLock = new Object();

	private SyncProgressBottomPanel syncProgressBottomPanel = new SyncProgressBottomPanel();

	private SyncProgressBottomPanel appUpdateProgressBottomPanel = new SyncProgressBottomPanel();

	@Autowired
	private ITunesImportProgressPanel iTunesImportProgressPanel;
	@Autowired
	private ProgressBarPanel progressBarPanel;
	@Autowired
	private SearchProgressBottomPanel searchProgressBottomPanel;
	@Autowired
	private SharingProgressPanel sharingProgressPanel;
	@Autowired
	private SyncAfterImportProgressBottomPanel syncAfterImportProgressBottomPanel;
	@Autowired
	private UploadProgressBottomPanel uploadProgressBottomPanel;
	@Autowired
	private AlertProgressBottomPanel alertProgressBottomPanel;
	@Autowired
	private MessageActivityPanel messageActivityPanel;
	@Autowired
	private ViewEngine viewEngine;

	public BottomPanel() {
		initialize();
	}

	@InitializeService
	public void setup() {
		appUpdateProgressBottomPanel.setIcon("updateIcon");

		getActivityPanel().add(syncProgressBottomPanel);
		getActivityPanel().add(iTunesImportProgressPanel);
		getActivityPanel().add(progressBarPanel);
		getActivityPanel().add(searchProgressBottomPanel);
		getActivityPanel().add(sharingProgressPanel);
		getActivityPanel().add(syncAfterImportProgressBottomPanel);
		getActivityPanel().add(uploadProgressBottomPanel);
		getActivityPanel().add(appUpdateProgressBottomPanel);

		// this needs to go last
		getActivityPanel().add(messageActivityPanel);

	}

	private void showAlertProgress() {
		alertProgressBottomPanel.setVisible(true);
	}

	private void stopAlertProgress() {
		alertProgressBottomPanel.setVisible(false);
	}

	private void showSearchProgress() {
		searchProgressBottomPanel.setVisible(true);
	}

	private void stopSearchProgress() {
		searchProgressBottomPanel.setVisible(false);
	}

	@EventMethod(Events.Alerts.UPLOAD_CONTENT_STARTED_ID)
	public void onContentUploadStarted(Long uploadId) {
		uploadProgressBottomPanel.setVisible(true);
		uploadProgressBottomPanel.setUploadId(uploadId);
		uploadProgressBottomPanel.getCancelUploadButton().setVisible(true);
	}

	@EventMethod(Events.Alerts.UPLOAD_CONTENT_PROGRESS_ID)
	public void onContentUploadUpdated(UploadContentProgressEvent uploadEvent) {
		uploadProgressBottomPanel.updateProgress(uploadEvent.getProgress());
		uploadProgressBottomPanel.setText("-" + Formatters.formatTimeString(uploadEvent.getRemainingSeconds()) + " @ "
				+ Formatters.formatSpeed(uploadEvent.getUploadRate()));
	}

	@EventMethod(Events.Alerts.UPLOAD_CONTENT_DONE_ID)
	public void onContentUploadDone(Long uploadId) {
		uploadProgressBottomPanel.setVisible(false);
		uploadProgressBottomPanel.setText("");
		uploadProgressBottomPanel.updateProgress(0);
		uploadProgressBottomPanel.setUploadId(0L);
		uploadProgressBottomPanel.getCancelUploadButton().setVisible(false);
	}

	@EventMethod(Events.AutoUpdate.UPDATE_FOUND_ID)
	public void onUpdateFound() {
		appUpdateProgressBottomPanel.setMessage("bottomPanel.updateApp");
		appUpdateProgressBottomPanel.setVisible(true);
		appUpdateProgressBottomPanel.updateProgress(0);
	}

	@EventMethod(Events.AutoUpdate.UPDATE_DOWNLOAD_PROGRESS_ID)
	public void onDownloadUpdateProgress(Integer progress) {
		appUpdateProgressBottomPanel.updateProgress(progress);
	}

	@EventMethod(Events.AutoUpdate.UPDATE_DOWNLOAD_COMPLETED_ID)
	public void onUpdateDownloadCompleted() {
		appUpdateProgressBottomPanel.setVisible(false);
	}

	@EventMethod(Events.AutoUpdate.UPDATE_DOWNLOAD_ERROR_ID)
	public void onUpdateDownloadError() {
		appUpdateProgressBottomPanel.setVisible(false);
	}

	@Override
	public final void internationalize(Messages messages) {
	}

	@EventMethod(Model.DISPLAYED_ITEM_COUNT_ID)
	public void onNumberOfTracksChanged(ValueEvent<Integer> eventArgs) {
		Integer value = eventArgs.getValue();
		setNumberOfTracks(value);
	}

	@EventMethod(value = Events.Library.IMPORTING_ITUNES_LIBRARY_ID, mode = EventExecutionMode.ASYNC)
	public void onItunesImportStarted() {
		synchronized (importITunesLock) {
			showITunesProgressPanel();
			try {
				Thread.sleep(1900);
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}
	}

	@EventMethod(value = Events.Library.IMPORTING_ITUNES_LIBRARY_DONE_ID, mode = EventExecutionMode.ASYNC)
	public void onItunesImportDone() {
		synchronized (importITunesLock) {
			hideITunesProgressPanel();
		}
	}

	@EventMethod(Events.Application.SEARCH_TRACKS_ID)
	public void onSearchTracks(ValueEvent<SearchState> event) {
		if (SearchState.Started == event.getValue()) {
			log.debug("Search started");
			updateSearchProgress();
		} else {
			log.debug("Search finished");
			stopSearchProgress();
		}
	}

	@EventMethod(Events.Downloads.UPDATED_ID)
	public void onDownloadUpdated(ValueEvent<Download> valueEvent) {
		Download download = valueEvent.getValue();
		downloadsInProcess.add(download);
		if (download.getStatus() != DownloadState.Downloading) {
			cleanDownloadsInProcess();
		}
		updateRateLabel();
	}

	@EventMethod(Events.Downloads.ALL_MODIFIED_ID)
	public void onDownloadAllModified() {
		cleanDownloadsInProcess();
		updateRateLabel();
	}

	@EventMethod(Events.Downloads.COMPLETED_ID)
	public void onDownloadCompleted(ValueEvent<Download> valueEvent) {
		downloadsInProcess.remove(valueEvent.getValue());
		updateRateLabel();
	}

	private void cleanDownloadsInProcess() {
		HashSet<Download> downloads = new HashSet<Download>();
		for (Download download : downloadsInProcess) {
			if (download.getStatus() != DownloadState.Downloading) {
				downloads.add(download);
			}
		}
		downloadsInProcess.removeAll(downloads);
	}

	long calculateRateOfCurrentDownloads() {
		long rate = 0;
		for (Download download : downloadsInProcess) {
			long currentRate = download.getRate();
			rate += currentRate;
		}
		return rate;
	}

	void updateRateLabel() {
		long rate = calculateRateOfCurrentDownloads();
		List<String> currentDownloads = viewEngine.get(Model.CURRENT_DOWNLOAD_IDS);
		int numberOfCurrentdownloads = currentDownloads == null ? 0 : currentDownloads.size();
		if (rate > 0 && numberOfCurrentdownloads > 0) {
			getRateLabel().setMessage("bottomPanel.rate", String.valueOf(numberOfCurrentdownloads),
					formatter.getFormatNoDigits(rate));
			getRateLabel().setVisible(true);
		} else {
			getRateLabel().setVisible(false);
		}
	}

	protected final void hideITunesProgressPanel() {
		iTunesImportProgressPanel.setVisible(false);
	}

	private void showITunesProgressPanel() {
		iTunesImportProgressPanel.setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(DEFAULT_SIZE);
		this.setMinimumSize(MINIMUM_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.add(getStatusPanel(), BorderLayout.WEST);
		this.add(getInfoPanel(), BorderLayout.CENTER);
		infoLabel.setVisible(false);
	}

	final JPanel getInfoPanel() {
		if (infoPanel == null) {
			infoPanel = new JPanel();
			infoPanel.setSize(INFO_PANEL_DEFAULT_SIZE);
			infoPanel.setMaximumSize(INFO_PANEL_DEFAULT_SIZE);
			infoPanel.setMinimumSize(INFO_PANEL_DEFAULT_SIZE);
			infoPanel.setPreferredSize(INFO_PANEL_DEFAULT_SIZE);
			infoPanel.setLayout(new BorderLayout());
			infoPanel.add(getActivityPanel(), BorderLayout.EAST);
			infoPanel.add(getInfoLabelPanel(), BorderLayout.CENTER);
		}
		return infoPanel;
	}

	private Ji18nLabel getInfoLabel() {
		if (infoLabel == null) {
			infoLabel = new Ji18nLabel();
			infoLabel.setName(SynthFonts.PLAIN_FONT12_WHITE);
		}
		return infoLabel;
	}

	@SuppressWarnings("serial")
	public Ji18nLabel getRateLabel() {
		if (rateLabel == null) {
			rateLabel = new Ji18nLabel() {
				@Override
				public void setVisible(boolean aFlag) {
					super.setVisible(aFlag);
					getStatusLabel().setVisible(!aFlag);
				}
			};
			rateLabel.setBounds(RATE_LABEL_BOUNDS);
			rateLabel.setName(SynthFonts.PLAIN_FONT11_GRAY100_100_100);
			rateLabel.setVisible(false);
		}
		return rateLabel;
	}

	private JPanel getInfoLabelPanel() {
		if (infoLabelPanel == null) {
			infoLabelPanel = new JPanel();
			infoLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			infoLabelPanel.add(getInfoLabel());
		}
		return infoLabelPanel;
	}

	final JPanel getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new JPanel();
			statusPanel.setLayout(null);
			statusPanel.setName("bottomProgressPanel");
			statusPanel.setPreferredSize(STATUS_PANEL_DEFAULT_SIZE);
			statusPanel.setSize(STATUS_PANEL_DEFAULT_SIZE);
			statusPanel.setMinimumSize(STATUS_PANEL_DEFAULT_SIZE);
			statusPanel.setMaximumSize(STATUS_PANEL_DEFAULT_SIZE);
			statusPanel.add(getIconLabel());
			statusPanel.add(getStatusLabel());
			statusPanel.add(getRateLabel());
			statusPanel.add(getSeparatorInnerPanel());
		}
		return statusPanel;
	}

	private JLabel getIconLabel() {
		if (iconLabel == null) {
			iconLabel = new JLabel();
			iconLabel.setBounds(ICON_LABEL_BOUNDS);
			iconLabel.setName(ICON_LABEL_OFFLINE);
		}
		return iconLabel;
	}

	private Ji18nLabel getStatusLabel() {
		if (statusLabel == null) {
			statusLabel = new Ji18nLabel();
			statusLabel.setBounds(STATUS_LABEL_BOUNDS);
			statusLabel.setName(SynthFonts.BOLD_FONT12_GRAY100_100_100);
			statusLabel.setMessage("BottomPanel.offline");
		}
		return statusLabel;
	}

	private JPanel getSeparatorInnerPanel() {
		if (separatorInnerPanel == null) {
			separatorInnerPanel = new JPanel();
			separatorInnerPanel.setPreferredSize(SEPARATOR_PANEL_PREFERRED_SIZE);
			separatorInnerPanel.setBounds(SEPARATOR_INNER_PANEL_BOUNDS);
			separatorInnerPanel.setName("verticalSeparator");
		}
		return separatorInnerPanel;
	}

	@EventMethod(Events.Application.STARTED_ID)
	public void onAppStarted() {
		login(viewEngine.get(ApplicationModel.HAS_INTERNET_CONNECTION));
	}

	@EventMethod(Events.Application.STOPED_ID)
	public void onUserLogout() {
		offline();
	}

	@EventMethod(ApplicationModel.HAS_INTERNET_CONNECTION_ID)
	public void onInternetConnectionStatusChanged(ValueEvent<Boolean> event) {
		login(event.getValue());
	}

	@Override
	public void removeMessages(Messages messages) {
		statusLabel.removeMessages(messages);
		infoLabel.removeMessages(messages);
		rateLabel.removeMessages(messages);
	}

	@Override
	@Autowired
	public final void setMessages(Messages messages) {
		appUpdateProgressBottomPanel.setMessages(messages);
		syncProgressBottomPanel.setMessages(messages);
		appUpdateProgressBottomPanel.setMessages(messages);
		getStatusLabel().setMessages(messages);
		getInfoLabel().setMessages(messages);
		getRateLabel().setMessages(messages);
	}

	public final void loggingIn() {
		statusLabel.setMessage("BottomPanel.logging");
		iconLabel.setName(ICON_LABEL_OFFLINE);
	}

	public final void offline() {
		infoLabel.setVisible(false);
		statusLabel.setMessage("BottomPanel.offline");
		iconLabel.setName(ICON_LABEL_OFFLINE);
	}

	private void login(boolean online) {
		infoLabel.setVisible(true);
		if (online) {
			statusLabel.setMessage("BottomPanel.online");
			iconLabel.setName("iconLabelOnline");
		} else {
			statusLabel.setMessage("BottomPanel.offline");
			iconLabel.setName(ICON_LABEL_OFFLINE);
		}
	}

	@EventMethod(Model.CURRENT_ALERTS_ID)
	public final void onCurrentAlertsChanged() {
		showAlertProgress();
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(e, e);
				}
				return null;
			}

			@Override
			protected void done() {
				stopAlertProgress();
			}
		}.execute();
	}

	private void updateSearchProgress() {
		synchronized (libraryProgressBarLock) {
			showSearchProgress();
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				log.error(e, e);
			}
		}
	}

	private void showSyncProgress(String id, String rootName) {
		currentSyncLib = id;
		syncParams.put(id, new SyncParams(rootName));
		syncProgressBottomPanel.setMessage("bottomPanel.syncingProgress", rootName);
		syncProgressBottomPanel.updateProgress(0);
		syncProgressBottomPanel.setVisible(true);
	}

	private void stopSyncProgress(String id) {
		syncParams.remove(id);
		if (syncParams.isEmpty()) {
			syncProgressBottomPanel.setVisible(false);
			currentSyncLib = "";
		} else if (currentSyncLib.equals(id)) {
			Entry<String, SyncParams> syncParam = this.syncParams.entrySet().iterator().next();
			showSyncProgress(syncParam.getKey(), syncParam.getValue().getName());
			syncProgressBottomPanel.updateProgress(syncParam.getValue().getProgress());
		}

	}

	@EventMethod(Events.Library.SYNC_DOWNLOAD_PROGRESS_EVENT_ID)
	public void onSyncProgress(LibrarySyncProgressEvent syncEvent) {
		synchronized (libraryProgressBarLock) {
			String id = syncEvent.getOwner();
			SyncParams syncParams = this.syncParams.get(id);
			if (syncParams != null) {
				syncParams.setProgress(syncEvent.getProgress());
			}
			if (currentSyncLib.equals(id)) {
				syncProgressBottomPanel.updateProgress(syncEvent.getProgress());
			}
		}
	}

	@EventMethod(Events.Library.SYNC_DOWNLOAD_EVENT_ID)
	public void onSync(LibrarySyncEvent syncEvent) {
		synchronized (libraryProgressBarLock) {
			LibrarySyncEventType type = syncEvent.getType();
			String id = syncEvent.getOwner();
			switch (type) {
			case SYNC_FINISHED:
				stopSyncProgress(id);
				break;
			case SYNC_STARTED:
				showSyncProgress(id, syncEvent.getRootName());
			default:
			}
		}
	}

	public static JPanel getSeparatorPanel() {
		JPanel separatorPanel = new JPanel();
		separatorPanel = new JPanel();
		separatorPanel.setPreferredSize(SEPARATOR_PANEL_PREFERRED_SIZE);
		separatorPanel.setName(SEPARATOR_PANEL_NAME);
		return separatorPanel;
	}

	private JPanel getActivityPanel() {
		if (activityPanel == null) {
			activityPanel = new JPanel();
			activityPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		}
		return activityPanel;
	}

	private void setNumberOfTracks(Integer value) {
		infoLabel.setMessage("bottomPanel.label", Formatters.formatInteger(value));
		if (value.equals(9000)) {
			infoLabel.setText("Whaat?!!, 9000!!");
		}
	}

}

class SyncParams {
	private final String name;
	private int progress;

	public SyncParams(String name) {
		this.name = name;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getName() {
		return name;
	}

}
