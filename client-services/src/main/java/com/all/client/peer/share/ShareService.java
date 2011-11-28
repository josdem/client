package com.all.client.peer.share;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.client.services.MusicEntityService;
import com.all.commons.IncrementalNamedThreadFactory;
import com.all.downloader.alllink.AllLink;
import com.all.downloader.share.FileSharedEvent;
import com.all.downloader.share.ShareException;
import com.all.downloader.share.Sharer;
import com.all.downloader.share.SharerListener;
import com.all.messengine.MessageMethod;
import com.all.shared.messages.MessEngineConstants;
import com.all.shared.model.Track;

@Service
public class ShareService implements SharerListener {

	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired(required = false)
	private Sharer sharerManager;
	@Autowired
	private MusicEntityService musicEntityService;

	private final AtomicBoolean interrupted = new AtomicBoolean(false);
	private final ExecutorService sharingExecutor = Executors.newCachedThreadPool(new IncrementalNamedThreadFactory(
			"ShareServiceThread"));

	@PostConstruct
	public void initialize() {
		sharerManager.addSharerListener(this);
	}

	@PreDestroy
	@MessageMethod(MessEngineConstants.USER_SESSION_CLOSED_TYPE)
	public void shutdown() {
		sharingExecutor.shutdownNow();
	}

	public void run() {
		if (interrupted.get()) {
			throw new IllegalStateException("ShareService was previously interrupted.");
		}
		sharingExecutor.execute(new ShareTask());
	}

	private void share() {
		shareTracks(musicEntityService.getAllTracks());
	}

	private void shareTracks(List<Track> tracks) {
		log.info("Files to be shared: " + tracks.size());
		for (Track track : tracks) {
			if (interrupted.get()) {
				log.info("Interrupted while making share");
				return;
			}
			if (musicEntityService.isFileAvailable(track)) {
				share(track);
			}
		}
	}

	private void share(Track track) {
		try {
			AllLink allLink = AllLink.parse(track.getDownloadString());
			sharerManager.share(allLink);
		} catch (IllegalArgumentException iae) {
			log.error("Unable to share track" + track, iae);
		} catch (ShareException e) {
			log.error("Unable to share track" + track, e);
		}

	}

	public void interrupt() {
		interrupted.set(true);
	}

	@Override
	public void onFileShared(FileSharedEvent fileSharedEvent) {
		log.debug("Received FileSharedEvent with allLink " + fileSharedEvent.getAllLink());
		// this implementations only works for phex sharer events, change to
		// adapt to new share implementations
		AllLink updatedAllLink = fileSharedEvent.getAllLink();
		String downloadId = updatedAllLink.getHashCode();
		musicEntityService.updateDownloadString(downloadId, updatedAllLink.toString());
	}

	class ShareTask implements Runnable {
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			try {
				share();
			} catch (Exception e) {
				log.error("Unexpected error received while sharing", e);
			} finally {
				log.info(String.format("Done sharing tracks, time spent %d ms", System.currentTimeMillis() - start));
			}
		}
	}

}
