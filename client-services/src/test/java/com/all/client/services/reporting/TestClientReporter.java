package com.all.client.services.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.core.common.services.reporting.Reporter;
import com.all.shared.model.Category;
import com.all.shared.model.Playlist;
import com.all.shared.model.User;
import com.all.shared.stats.AllStat;
import com.all.shared.stats.DownloadStat;
import com.all.shared.stats.MediaContainerStat;
import com.all.shared.stats.MediaImportStat;
import com.all.shared.stats.MediaImportStat.ImportType;
import com.all.shared.stats.TopHundredStat;
import com.all.shared.stats.TopHundredStatId;
import com.all.shared.stats.usage.UserActionStat;

public class TestClientReporter {
	@Mock
	private Reporter reporter;
	@InjectMocks
	private ClientReporter clientReporter = new ClientReporter();

	private String email = "a@a.com";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		assertTrue(true);
	}

	@After
	public void teardown() {
		verifyNoMoreInteractions(reporter);
	}

	@Test
	public void shouldRedirectCallsToReporter() throws Exception {
		AllStat stat = mock(AllStat.class);
		clientReporter.log(stat);
		verify(reporter).log(stat);
	}

	@Test
	public void shouldNotReportAnythingIfThereIsNoSession() throws Exception {
		clientReporter.logNewFolder();
		clientReporter.logNewPlaylist();
		clientReporter.logUserAction(1);
		clientReporter.logDownloadAction(1, "aa");
		clientReporter.logImportEvent(ImportType.EXTERNAL_DEVICES, 3, 2, 1);
	}

	@Test
	public void shouldNotReportAnythingIfLoggedOut() throws Exception {
		login(email);
		clientReporter.logout();
		clientReporter.logNewFolder();
		clientReporter.logNewPlaylist();
		clientReporter.logUserAction(1);
		clientReporter.logDownloadAction(1, "aa");
		clientReporter.logImportEvent(ImportType.EXTERNAL_DEVICES, 3, 2, 1);
	}

	@Test
	public void shouldLogNewFolder() throws Exception {
		login(email);
		clientReporter.logNewFolder();
		MediaContainerStat capture = capture(MediaContainerStat.class, email);
		assertEquals(1, capture.getNewFolders());
		assertEquals(0, capture.getNewPlaylists());
	}

	@Test
	public void shouldLogNewPlaylist() throws Exception {
		login(email);
		clientReporter.logNewPlaylist();
		MediaContainerStat capture = capture(MediaContainerStat.class, email);
		assertEquals(0, capture.getNewFolders());
		assertEquals(1, capture.getNewPlaylists());
	}

	@Test
	public void shouldLogAUserAction() throws Exception {
		login(email);
		clientReporter.logUserAction(5);
		UserActionStat capture = capture(UserActionStat.class, email);
		assertEquals(5, capture.getAction());
		assertEquals(1, capture.getTimes());
	}

	@Test
	public void shouldLogDownloadAction() throws Exception {
		login(email);
		String trackId = "bb";
		clientReporter.logDownloadAction(7, trackId);
		DownloadStat capture = capture(DownloadStat.class, email);
		assertEquals(7, capture.getAction());
		assertEquals(trackId, capture.getTrackId());
	}

	@Test
	public void shouldLogImportActionNoFolders() throws Exception {
		login(email);
		clientReporter.logImportEvent(ImportType.ITUNES, 10, 0, 0);
		MediaImportStat capture = capture(MediaImportStat.class, email);
		assertEquals(ImportType.ITUNES.action(), capture.getImportTypeAction());
		assertEquals(0, capture.getTotalFolders());
		assertEquals(0, capture.getTotalPlaylists());
		assertEquals(10, capture.getTotalTracks());
	}

	@Test
	public void shouldLogImportActionWithFolders() throws Exception {
		login(email);
		clientReporter.logImportEvent(ImportType.ITUNES, 10, 0, 1);

		ArgumentCaptor<AllStat> statCaptor = ArgumentCaptor.forClass(AllStat.class);
		verify(reporter, times(2)).log(statCaptor.capture());

		MediaImportStat capture = (MediaImportStat) statCaptor.getAllValues().get(0);
		assertEquals(email, capture.getEmail());
		assertEquals(ImportType.ITUNES.action(), capture.getImportTypeAction());
		assertEquals(1, capture.getTotalFolders());
		assertEquals(0, capture.getTotalPlaylists());
		assertEquals(10, capture.getTotalTracks());

		MediaContainerStat containerStat = (MediaContainerStat) statCaptor.getAllValues().get(1);
		assertEquals(email, containerStat.getEmail());
		assertEquals(1, containerStat.getNewFolders());
		assertEquals(0, containerStat.getNewPlaylists());
	}

	@Test
	public void shouldReportCategoryStat() throws Exception {
		login(email);
		long catId = 2L;
		String playHash = "AAA";
		Category hundredCategory = mock(Category.class);
		Playlist hundredPlaylist = mock(Playlist.class);
		when(hundredCategory.getId()).thenReturn(catId);
		when(hundredPlaylist.getHashcode()).thenReturn(playHash);
		clientReporter.logTopHundredDownload(hundredCategory, hundredPlaylist);
		ArgumentCaptor<AllStat> statCaptor = ArgumentCaptor.forClass(AllStat.class);
		verify(reporter).log(statCaptor.capture());
		TopHundredStat stat = (TopHundredStat) statCaptor.getValue();
		TopHundredStatId id = (TopHundredStatId) stat.getId();
		assertEquals(catId, id.getCategoryId());
		assertEquals(email, id.getEmail());
		assertEquals(playHash, id.getPlaylistHash());

	}

	@SuppressWarnings("unchecked")
	private <T extends AllStat> T capture(Class<T> clazz, String email) {
		ArgumentCaptor<T> statCaptor = ArgumentCaptor.forClass(clazz);
		verify(reporter).log(statCaptor.capture());
		AllStat value = statCaptor.getValue();
		assertNotNull(value);
		assertTrue("Stat of class " + clazz, value.getClass().equals(clazz));
		assertEquals(email, value.getEmail());
		return (T) value;
	}

	private void login(String email) {
		User user = new User();
		user.setEmail(email);
		clientReporter.login(user);
	}
}
