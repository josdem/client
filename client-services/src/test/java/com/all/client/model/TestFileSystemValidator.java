package com.all.client.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.all.client.model.FileSystemValidator;
import com.all.shared.model.Folder;
import com.all.shared.model.Playlist;
import com.all.shared.model.Root;
import com.all.shared.model.Root.ContainerType;

public class TestFileSystemValidator {// extends IntegrationTestCase {

	private FileSystemValidator folderValidator;
	private FileSystemValidator playlistValidator;
	private FileSystemValidator trackValidator;
	private FileSystemValidator notFoundPathValidator;
	private FileSystemValidator tooLongHierarchyConverter;
	private FileSystemValidator playlistAndTrackValidator;
	private FileSystemValidator folderAndTrackValidator;
	private FileSystemValidator folderAndPlaylistValidator;
	private FileSystemValidator folderPlaylistAndTrackValidator;

	@Before
	public void initialize() {
		folderValidator = new FileSystemValidator(false, new File("src/test/resources/folder"));
		playlistValidator = new FileSystemValidator(false, new File("src/test/resources/playlist"));
		trackValidator = new FileSystemValidator(false, new File("src/test/resources/playlist/TestSong1.mp3"));
		notFoundPathValidator = new FileSystemValidator(false, new File("test/thispathdoesnt/exist"));

		tooLongHierarchyConverter = new FileSystemValidator(false, new File("src/test/resources/grandpa"));
		playlistAndTrackValidator = new FileSystemValidator(false, new File("src/test/resources/playlist"), new File(
				"src/test/resources/playlist/TestSong1.mp3"));

		folderAndTrackValidator = new FileSystemValidator(false, new File("src/test/resources/folder"), new File(
				"src/test/resources/playlist/TestSong1.mp3"));

		folderAndPlaylistValidator = new FileSystemValidator(false, new File("src/test/resources/folder"), new File(
				"src/test/resources/playlist"));

		folderPlaylistAndTrackValidator = new FileSystemValidator(false, new File("src/test/resources/folder"), new File(
				"src/test/resources/playlist"), new File("src/test/resources/playlist/TestSong1.mp3"));
	}

	@Test
	public void shouldIdentifyDirectoryAsPlaylists() throws Exception {
		assertTrue("playlist has playlist", playlistValidator.hasPlaylists());
		assertFalse("folder has not playlist", folderValidator.hasPlaylists());
		assertFalse("track has not playlist", trackValidator.hasPlaylists());
	}

	@Test
	public void shouldIdentifyDirectoryAsFolder() throws Exception {
		assertTrue("Folder has folder", folderValidator.hasFolders());
		assertFalse("Track has not folder", trackValidator.hasFolders());
		assertFalse("Playlist has not folder", playlistValidator.hasFolders());
	}

	@Test
	public void shouldIdentifyAFileAsTrack() throws Exception {
		assertTrue("Track has a Track", trackValidator.hasTracks());
		assertFalse("Folder has not Track", folderValidator.hasTracks());
		assertFalse("Playlist has not Track", playlistValidator.hasTracks());
	}

	@Test
	public void shouldRecognizeSimpleFile() throws Exception {
		assertTrue("File has Track", trackValidator.hasTracks());
		assertFalse("File has not playlist", trackValidator.hasPlaylists());
		assertFalse("File has not folder", trackValidator.hasFolders());
		assertFalse("There has no error", trackValidator.hasError());
	}

	@Test
	public void shouldRecognizeSingleDirectory() throws Exception {
		assertFalse("Simple directory has not Track", playlistValidator.hasTracks());
		assertTrue("Simple directory has playlist", playlistValidator.hasPlaylists());
		assertFalse("Simple directory has not folder", playlistValidator.hasFolders());
		assertFalse("There has no error", playlistValidator.hasError());
	}

	@Test
	public void shouldRecognizeMultiLayerDirectory() throws Exception {
		assertFalse("Directory has not Track", folderValidator.hasTracks());
		assertFalse("Directory has not playlist", folderValidator.hasPlaylists());
		assertTrue("Directory has folder", folderValidator.hasFolders());
		assertFalse("There has no error", folderValidator.hasError());
	}

	@Test
	public void shouldRecognizeDirectoryAndFileAsTrackAndPlaylist() throws Exception {
		assertTrue("Playlist and Track has Track", playlistAndTrackValidator.hasTracks());
		assertTrue("Playlist and Track has playlist", playlistAndTrackValidator.hasPlaylists());
		assertFalse("Playlist and Track has not folder", playlistAndTrackValidator.hasFolders());
		assertFalse("There has no error", playlistAndTrackValidator.hasError());
	}

	@Test
	public void shouldRecognizeDirectoryAndFileAsTrackAndFolder() throws Exception {
		assertTrue("Folder and Track has Track", folderAndTrackValidator.hasTracks());
		assertFalse("Folder and Track has not playlist", folderAndTrackValidator.hasPlaylists());
		assertTrue("Folder and Track has folder", folderAndTrackValidator.hasFolders());
		assertFalse("There has no error", folderAndTrackValidator.hasError());
	}

	@Test
	public void shouldRecognizeTwoDirectoriesAsPlaylistAndFolder() throws Exception {
		assertFalse("Folder and Playlist has not Track", folderAndPlaylistValidator.hasTracks());
		assertTrue("Folder and Playlist has playlist", folderAndPlaylistValidator.hasPlaylists());
		assertTrue("Folder and Playlist has folder", folderAndPlaylistValidator.hasFolders());
		assertFalse("There has no error", folderAndPlaylistValidator.hasError());
	}

	@Test
	public void shouldRecognizeTwoDirectoriesAndAFileAsPlaylistFolderAndTracks() throws Exception {
		assertTrue("Folder Playlist and Track has Track", folderPlaylistAndTrackValidator.hasTracks());
		assertTrue("Folder Playlist and Track has playlist", folderPlaylistAndTrackValidator.hasPlaylists());
		assertTrue("Folder Playlist and Track has folder", folderPlaylistAndTrackValidator.hasFolders());
		assertFalse("There has no error", folderPlaylistAndTrackValidator.hasError());
	}

	@Test
	public void shouldCheckIfTheValidatorContentCanBeDropedInsideAFolder() throws Exception {
		assertFalse(folderValidator.canBeInside(Folder.class));
		assertTrue(playlistValidator.canBeInside(Folder.class));
		assertTrue(trackValidator.canBeInside(Folder.class));
		assertFalse(tooLongHierarchyConverter.canBeInside(Folder.class));
	}

	@Test
	public void shouldCheckIfTheValidatorContentCanBeDropedInsideAPlaylist() throws Exception {
		assertFalse(folderValidator.canBeInside(Playlist.class));
		assertFalse(playlistValidator.canBeInside(Playlist.class));
		assertTrue(trackValidator.canBeInside(Playlist.class));
		assertFalse(tooLongHierarchyConverter.canBeInside(Playlist.class));
	}

	@Test
	public void shouldCheckIfTheValidatorContentCanBeDropedInsideRoot() throws Exception {
		assertTrue(folderValidator.canBeInside(null));
		assertTrue(playlistValidator.canBeInside(null));
		assertTrue(trackValidator.canBeInside(null));
		assertTrue(tooLongHierarchyConverter.canBeInside(null));
	}

	@Test
	public void shouldCheckIfTheValidatorContentCanBeDropedInsideSomethingElseAsItWorksAsRoot() throws Exception {
		assertTrue(folderValidator.canBeInside(Root.class));
		assertTrue(playlistValidator.canBeInside(Root.class));
		assertTrue(trackValidator.canBeInside(Root.class));
		assertTrue(tooLongHierarchyConverter.canBeInside(Root.class));
	}

	@Test
	public void shouldHaveErrorIfPathGivenDoesntExist() throws Exception {
		assertTrue(notFoundPathValidator.hasError());
	}

	@Test
	public void shouldNotHaveErrorIfGivenPathIsValid() throws Exception {
		assertFalse(playlistValidator.hasError());
	}

	@Test
	public void shouldNotHaveErrorIfPathHasTooLongHierarchy() throws Exception {
		assertFalse(tooLongHierarchyConverter.hasError());
	}

	@Test
	public void shouldGetTheFilesForFolderPlaylistTrackAndError() throws Exception {
		File folderFile = new File("src/test/resources/folder");
		File playlistFile = new File("src/test/resources/playlist");
		File trackFile = new File("src/test/resources/playlist/TestSong1.mp3");
		File errorFile = new File("src/test/resources/grandpa");
		FileSystemValidator mostCompleteValidator = new FileSystemValidator(false, folderFile, playlistFile, trackFile,
				errorFile);
		assertTrue("complete has Track", mostCompleteValidator.hasTracks());
		assertTrue("complete has playlist", mostCompleteValidator.hasPlaylists());
		assertTrue("complete has folder", mostCompleteValidator.hasFolders());
		List<File> trackFiles = mostCompleteValidator.getTracks();
		assertTrue(trackFiles.contains(trackFile));
	}

	@Test
	public void shouldConstructTheSameUsingAListInsteadOfAnArray() throws Exception {
		List<File> files = new ArrayList<File>();
		files.add(new File("src/test/resources/folder"));
		files.add(new File("src/test/resources/playlist"));
		files.add(new File("src/test/resources/playlist/TestSong1.mp3"));
		files.add(new File("src/test/resources/grandpa"));
		FileSystemValidator listValidator = new FileSystemValidator(false, files);
		assertTrue("complete has Track", listValidator.hasTracks());
		assertTrue("complete has playlist", listValidator.hasPlaylists());
		assertTrue("complete has folder", listValidator.hasFolders());
	}

	@Test
	public void shouldVerifyNotNullList() throws Exception {
		assertNotNull(folderValidator.getFolders());
		assertNotNull(folderValidator.getPlaylists());
		assertNotNull(folderValidator.getErrors());
		assertNotNull(folderValidator.getTracks());
	}

	@Test
	public void shouldVerifyEmptyFolder() throws Exception {
		File emptyFolder = new File("src/test/resources/emptyFolder/anotherEmptyFolder");
		List<File> files = new ArrayList<File>();
		files.add(emptyFolder);
		FileSystemValidator validator = new FileSystemValidator(false, files);
		assertFalse(validator.getErrorMessages().isEmpty());

		emptyFolder = new File("src/test/resources/emptyFolder");
		files = new ArrayList<File>();
		files.add(emptyFolder);
		validator = new FileSystemValidator(false, files);
		assertFalse(validator.getErrorMessages().isEmpty());
	}

	@Test
	public void shouldImportAplaylistWithATrackAndAHiddenFile() throws Exception {
		File playlist = new File("src/test/resources/grandpa/parent/child");
		List<File> files = new ArrayList<File>();
		files.add(playlist);
		FileSystemValidator validator = new FileSystemValidator(false, files);
		assertFalse(validator.hasTracks());
		assertFalse(validator.hasFolders());
		assertTrue(validator.hasPlaylists());
		assertEquals(1, validator.getPlaylists().size());

		assertEquals(0, validator.getErrorMessages().size());
	}

	@Test
	public void shouldReportAErrorBecausePlaylistIsEmpty() throws Exception {
		File playlist = new File("src/test/resources/emptyFolder/anotherEmptyFolder");
		List<File> files = new ArrayList<File>();
		files.add(playlist);
		FileSystemValidator validator = new FileSystemValidator(false, files);
		assertFalse(validator.hasPlaylists());
		assertEquals(1, validator.getErrorMessages().size());
	}

	@Test
	public void shouldKnowValidImageFile() throws Exception {
		FileSystemValidator validator = new FileSystemValidator(false, new File("src/test/resources/02715.jpg"));
		assertTrue(validator.hasValidImageFile());

		validator = new FileSystemValidator(false, new File("src/test/resources/playlist/TestSong1.mp3"));
		assertFalse(validator.hasValidImageFile());
	}

	@Test
	public void shouldGetValidImageFile() throws Exception {
		File expected = new File("src/test/resources/02715.jpg");
		FileSystemValidator validator = new FileSystemValidator(false, expected);
		assertEquals(expected, validator.getValidImageFile());
	}

	@Test
	public void shouldCheckThatFoldersCannotGetInsideAPlaylist() throws Exception {
		FileSystemValidator validator = new FileSystemValidator(false, new File("src/test/resources/grandpa/parent"));
		assertFalse(validator.getFolders().isEmpty());
		assertTrue(validator.getPlaylists().isEmpty());
		assertTrue(validator.getTracks().isEmpty());
		assertTrue(validator.getErrors().isEmpty());
		assertFalse(validator.canBeInside(new MockFolder()));
		assertFalse(validator.canBeInside(new MockPlaylist()));
		assertTrue(validator.canBeInside(new MockRoot(ContainerType.CONTACT)));
	}

	@Test
	public void shouldGetTotalTrackCount() throws Exception {
		assertEquals(1, trackValidator.getTrackCount());
		assertEquals(6, playlistValidator.getTrackCount());
		assertEquals(1, folderValidator.getTrackCount());
		assertEquals(7, playlistAndTrackValidator.getTrackCount());
	}

}
