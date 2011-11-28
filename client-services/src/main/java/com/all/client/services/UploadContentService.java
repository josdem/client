package com.all.client.services;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.appControl.ActionMethod;
import com.all.appControl.RequestMethod;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.core.actions.Actions;
import com.all.core.events.UploadContentDoneEvent;
import com.all.core.events.UploadContentListener;
import com.all.core.events.UploadContentStartedEvent;
import com.all.core.events.UploadContentUpdateEvent;
import com.all.mc.manager.McManager;
import com.all.mc.manager.uploads.UploadStatus;
import com.all.mc.manager.uploads.UploaderListener;
import com.all.shared.model.ModelCollection;
import com.all.shared.model.Track;

@Service
public class UploadContentService {

	private static final Log LOGGER = LogFactory.getLog(UploadContentService.class);

	private static final SecureRandom RANDOM_SOURCE = new SecureRandom();

	private final Collection<UploadContentListener> listeners = Collections
			.synchronizedSet(new HashSet<UploadContentListener>());

	private final ExecutorService uploadExecutor = Executors.newSingleThreadExecutor(new IncrementalNamedThreadFactory(
			"UploadContentServiceThread"));

	private final Map<Long, UploadContentTask> uploads = new HashMap<Long, UploadContentTask>();

	@Autowired(required = false)
	private McManager mcManager;

	@PreDestroy
	public void shutdown() {
		uploadExecutor.shutdownNow();
	}

	public void addUploadContentListener(UploadContentListener listener) {
		listeners.add(listener);
	}

	public void removeUploadContentListener(UploadContentListener listener) {
		listeners.remove(listener);
	}

	public long submit(ModelCollection model) {
		UploadContentTask uploadTask = new UploadContentTask(model);
		uploadExecutor.execute(uploadTask);
		uploads.put(uploadTask.getId(), uploadTask);
		return uploadTask.getId();
	}

	@ActionMethod(Actions.Alerts.CANCEL_CONTENT_UPLOAD_ID)
	public void cancel(Long uploadId) {
		if (uploads.containsKey(uploadId)) {
			uploads.get(uploadId).cancel();
		}
	}

	public int getUploadRate() {
		return mcManager.getUploadRate();
	}

	@RequestMethod(Actions.Downloads.IS_UPLOADING_ID)
	public boolean isUploading() {
		return !uploads.isEmpty();
	}

	private Collection<UploadContentListener> getUploadContentListeners() {
		synchronized (listeners) {
			return new ArrayList<UploadContentListener>(listeners);
		}
	}

	private final class UploadContentTask implements Runnable, UploaderListener {

		private final AtomicBoolean error = new AtomicBoolean(false);

		private final AtomicBoolean skipTrack = new AtomicBoolean(false);

		private final AtomicBoolean canceled = new AtomicBoolean(false);

		private final long id;

		private final long totalSize;

		private final BlockingQueue<UploadStatus> uploadStatusQueue;

		private Collection<Track> tracksToUpload;

		private final int totalTracks;

		private final Collection<Track> uploadedTracks;

		public UploadContentTask(ModelCollection model) {
			this.id = new BigInteger(62, RANDOM_SOURCE).longValue();
			this.tracksToUpload = Collections.unmodifiableCollection(model.rawTracks());
			this.totalSize = model.size();
			this.totalTracks = tracksToUpload.size();
			this.uploadStatusQueue = new LinkedBlockingQueue<UploadStatus>();
			this.uploadedTracks = new ArrayList<Track>();
		}

		public void cancel() {
			canceled.set(true);
		}

		public long getId() {
			return id;
		}

		@Override
		public void run() {
			try {
				startUpload();
				doUpload();
			} catch (Exception e) {
				if (canceled.get()) {
					LOGGER.info("Upload " + getId() + " was canceled.");
				} else {
					LOGGER.error("Unexpected error uploading content.", e);
				}
			} finally {
				finishUpload();
			}
		}

		private void startUpload() {
			mcManager.addUploaderListener(this);
			for (UploadContentListener listener : getUploadContentListeners()) {
				try {
					listener.onContentUploadStarted(new UploadContentStartedEvent(id));
				} catch (Exception e) {
					LOGGER.error("Unexpected error during UploadContentListener execution.", e);
				}
			}
		}

		private void doUpload() throws InterruptedException {
			for (Track track : tracksToUpload) {
				upload(track);
			}
		}

		private void finishUpload() {
			mcManager.removeUploaderListener(this);
			for (UploadContentListener listener : getUploadContentListeners()) {
				try {
					listener.onContentUploadDone(new UploadContentDoneEvent(id, error.get(), canceled.get()));
				} catch (Exception e) {
					LOGGER.error("Unexpected error during UploadContentListener execution.", e);
				}
			}
			uploads.remove(id);
		}

		private void upload(Track track) throws InterruptedException {
			mcManager.upload(track.getHashcode());
			while (!uploadedTracks.contains(track) && !skipTrack.get()) {
				updateUploadStatus(track, getTrackUploadStatus());
			}
			skipTrack.set(false);
		}

		private void updateUploadStatus(Track track, UploadStatus lastStatus) throws InterruptedException {
			if (track.getHashcode().equals(lastStatus.getTrackId())) {
				switch (lastStatus.getState()) {
				case COMPLETED:
					uploadedTracks.add(track);
					break;
				case UPLOADING:
					notifyProgress(createUpdateEvent(track, lastStatus));
					break;
				case ERROR:
					error.set(true);
					skipTrack.set(true);
					LOGGER.info("Will skip upload for " + track + " since there was an error uploading it.");
					break;
				case CANCELED:
					canceled.set(true);
					throw new InterruptedException("Upload for " + track + " was canceled.");
				}
			}
		}

		private void notifyProgress(UploadContentUpdateEvent updateEvent) {
			for (UploadContentListener listener : getUploadContentListeners()) {
				try {
					listener.onContentUploadUpdated(updateEvent);
				} catch (Exception e) {
					LOGGER.error("Unexpected error during UploadContentListener execution.", e);
				}
			}
		}

		private UploadContentUpdateEvent createUpdateEvent(Track track, UploadStatus lastStatus) {
			int remainingTracks = totalTracks - uploadedTracks.size();
			int uploadedBytes = getUploadedTrackBytes() + getUploadedBytesForTrack(track, lastStatus);
			int remainingBytes = (int) (totalSize - uploadedBytes);
			int remainingSeconds = lastStatus.getUploadRate() > 0 ? remainingBytes / lastStatus.getUploadRate()
					: Integer.MAX_VALUE;
			int progress = (int) (uploadedBytes * 100.0 / totalSize);
			return new UploadContentUpdateEvent(id, totalSize, totalTracks, remainingTracks, remainingBytes,
					remainingSeconds, progress, lastStatus.getUploadRate());
		}

		private int getUploadedBytesForTrack(Track track, UploadStatus lastStatus) {
			return (int) (lastStatus.getProgress() * track.getSize() / 100.0);
		}

		private int getUploadedTrackBytes() {
			int size = 0;
			for (Track track : uploadedTracks) {
				size += track.getSize();
			}
			return size;
		}

		@Override
		public void onUploadUpdated(UploadStatus uploadStatus) {
			uploadStatusQueue.offer(uploadStatus);
		}

		private UploadStatus getTrackUploadStatus() throws InterruptedException {
			while (!canceled.get()) {
				UploadStatus status = uploadStatusQueue.poll(100, TimeUnit.MILLISECONDS);
				if (status != null) {
					return status;
				}
			}
			throw new InterruptedException("Upload was canceled by user.");
		}

	}

}
