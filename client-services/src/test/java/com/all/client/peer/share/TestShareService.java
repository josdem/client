package com.all.client.peer.share;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.all.client.model.LocalTrack;
import com.all.client.services.MusicEntityService;
import com.all.downloader.alllink.AllLink;
import com.all.downloader.share.FileSharedEvent;
import com.all.downloader.share.ShareException;
import com.all.downloader.share.Sharer;
import com.all.shared.model.Track;
import com.all.testing.MockInyectRunner;
import com.all.testing.Stub;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestShareService {
	@UnderTest
	ShareService shareService;
	@Mock
	Sharer sharer;
	@Mock
	MusicEntityService musicEntityService;
	@Stub
	ExecutorService executor = Executors.newFixedThreadPool(1);

	@Mock
	LocalTrack track;

	@Test
	public void shouldAddListener() throws Exception {
		shareService.initialize();

		verify(sharer).addSharerListener(shareService);
	}


	@Test
	public void shouldShareTracks() throws Exception {
		List<Track> trackList = new ArrayList<Track>();
		trackList.add(track);
		when(musicEntityService.getAllTracks()).thenReturn(trackList);
		
		when(musicEntityService.isFileAvailable(track)).thenReturn(true);
		when(track.getDownloadString()).thenReturn("allLink:hashcode=00a9ae41a50cfece357f26e786db6fa014af765b");

		shareService.run();
		

		executor.awaitTermination(1, TimeUnit.SECONDS);
		
		verify(sharer).share(any(AllLink.class));
	}

	@Test
	public void shouldUpdateTrackAfterSharing() throws Exception {
		String hashcode = "00a9ae41a50cfece357f26e786db6fa014af765b";
		AllLink allLink = AllLink.parse("allLink:hashcode=" + hashcode);
		FileSharedEvent fileSharedEvent = new FileSharedEvent(this, allLink);

		shareService.onFileShared(fileSharedEvent);

		verify(musicEntityService).updateDownloadString(hashcode, allLink.toString());
	}

	@Test
	public void shouldInterruptShareTracks() throws Exception {
		List<Track> trackList = new ArrayList<Track>();
		trackList.add(track);
		trackList.add(track);
		when(musicEntityService.getAllTracks()).thenReturn(trackList);

		when(musicEntityService.isFileAvailable(track)).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				shareService.interrupt();
				return true;
			}
		});
		when(track.getDownloadString()).thenReturn("allLink:hashcode=00a9ae41a50cfece357f26e786db6fa014af765b");

		shareService.run();
		executor.awaitTermination(1, TimeUnit.SECONDS);

		verify(sharer).share(any(AllLink.class));
	}

	@Test
	public void shouldContinueIfInvalidDownloadString() throws Exception {
		List<Track> trackList = new ArrayList<Track>();
		trackList.add(track);
		trackList.add(track);
		when(musicEntityService.getAllTracks()).thenReturn(trackList);

		when(musicEntityService.isFileAvailable(track)).thenReturn(true);
		when(track.getDownloadString()).thenReturn("illegalDownloadString",
				"allLink:hashcode=00a9ae41a50cfece357f26e786db6fa014af765b");

		shareService.run();
		executor.awaitTermination(1, TimeUnit.SECONDS);


		verify(sharer).share(any(AllLink.class));
	}
	
	@Test
	public void shouldIgnoreSharerExceptionsAndContinueSharing() throws Exception {
		List<Track> trackList = new ArrayList<Track>();
		trackList.add(track);
		trackList.add(track);
		when(musicEntityService.getAllTracks()).thenReturn(trackList);

		when(musicEntityService.isFileAvailable(track)).thenReturn(true);
		when(track.getDownloadString()).thenReturn("allLink:hashcode=00a9ae41a50cfece357f26e786db6fa014af765b");
		doThrow(new ShareException("Some exception")).when(sharer).share(any(AllLink.class));
		
		shareService.run();
		executor.awaitTermination(1, TimeUnit.SECONDS);

		verify(sharer, times(2)).share(any(AllLink.class));
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldNotRunIfInterrupted() throws Exception {
		shareService.interrupt();
		shareService.run();
	}

}
