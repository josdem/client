package com.all.core.common.view;

import static org.junit.Assert.assertEquals;

import javax.swing.Icon;
import javax.swing.UIManager;

import org.junit.Test;


public class TestSynthIcons {
	private final static Icon FOLDER_ICON = UIManager.getDefaults().getIcon("Tree.openIcon");
	private final static Icon PLAYLIST_ICON = UIManager.getDefaults().getIcon("Tree.leafIcon");
	private final static Icon NEW_FOLDER_ICON = UIManager.getDefaults().getIcon("Tree.newOpenIcon");
	private final static Icon NEW_PLAYLIST_ICON = UIManager.getDefaults().getIcon("Tree.newLeafIcon");
	private final static Icon HIGHLIGHT_ICON = UIManager.getDefaults().getIcon("Tree.highlight");
    private final static Icon SORT_ASCENDING_ICON = UIManager.getIcon("Table.ascendingSortIcon");
    private final static Icon SORT_DESCENDING_ICON = UIManager.getIcon("Table.descendingSortIcon");
    private final static Icon SORT_NATURAL_ICON = UIManager.getIcon("Table.naturalSortIcon");
    private final static Icon SPEAKER_ICON = UIManager.getDefaults().getIcon("Table.speakerIcon");
    private final static Icon NEW_ICON = UIManager.getDefaults().getIcon("Table.newIcon");
    private final static Icon DOWNLOAD_ICON = UIManager.getDefaults().getIcon("Table.downloadingTrackIcon");
    private final static Icon DOWNLOAD_QUEUE_ICON = UIManager.getDefaults().getIcon("Table.downloadingQueueIcon");
    private final static Icon DOWNLOAD_ERROR_ICON = UIManager.getDefaults().getIcon("Table.downloadingErrorIcon");
    private final static Icon SPEAKER_INVISIBLE_ICON = UIManager.getDefaults().getIcon("Table.speakerInvisibleIcon");
    private final static Icon OFFLINE_ICON = UIManager.getDefaults().getIcon("contactTree.leafOfflineIcon");
    private final static Icon ONLLINE_ICON = UIManager.getDefaults().getIcon("contactTree.leafOnlineIcon");
    private final static Icon PENDING_ICON = UIManager.getDefaults().getIcon("contactTree.leafPendingIcon");
    private final static Icon FACEBOOK_ONLINE_ICON = UIManager.getDefaults().getIcon("contactTree.leafOnlineFacebokIcon");
    private final static Icon FACEBOOK_OFFLINE_ICON = UIManager.getDefaults().getIcon("contactTree.leafOfflineFacebokIcon");
    private final static Icon FACEBOOK_AWAY_ICON = UIManager.getDefaults().getIcon("contactTree.leafAwayFacebookIcon");
	
    @Test
	public void shouldGetSynthIcons() throws Exception {
		assertEquals(FOLDER_ICON, SynthIcons.FOLDER_ICON);
		assertEquals(PLAYLIST_ICON, SynthIcons.PLAYLIST_ICON);
		assertEquals(NEW_FOLDER_ICON, SynthIcons.NEW_FOLDER_ICON);
		assertEquals(NEW_PLAYLIST_ICON, SynthIcons.NEW_PLAYLIST_ICON);
		assertEquals(HIGHLIGHT_ICON, SynthIcons.HIGHLIGHT_ICON);
		assertEquals(SORT_ASCENDING_ICON, SynthIcons.SORT_ASCENDING_ICON);
		assertEquals(SORT_DESCENDING_ICON, SynthIcons.SORT_DESCENDING_ICON);
		assertEquals(SORT_NATURAL_ICON, SynthIcons.SORT_NATURAL_ICON);
		assertEquals(SPEAKER_ICON, SynthIcons.SPEAKER_ICON);
		assertEquals(NEW_ICON, SynthIcons.NEW_ICON);
		assertEquals(DOWNLOAD_ICON, SynthIcons.DOWNLOAD_ICON);
		assertEquals(DOWNLOAD_QUEUE_ICON, SynthIcons.DOWNLOAD_QUEUE_ICON);
		assertEquals(DOWNLOAD_ERROR_ICON, SynthIcons.DOWNLOAD_ERROR_ICON);
		assertEquals(SPEAKER_INVISIBLE_ICON, SynthIcons.SPEAKER_INVISIBLE_ICON);
		assertEquals(OFFLINE_ICON, SynthIcons.OFFLINE_ICON);
		assertEquals(ONLLINE_ICON, SynthIcons.ONLLINE_ICON);
		assertEquals(PENDING_ICON, SynthIcons.PENDING_ICON);
		assertEquals(FACEBOOK_ONLINE_ICON, SynthIcons.FACEBOOK_ONLINE_ICON);
		assertEquals(FACEBOOK_OFFLINE_ICON, SynthIcons.FACEBOOK_OFFLINE_ICON);
		assertEquals(FACEBOOK_AWAY_ICON, SynthIcons.FACEBOOK_AWAY_ICON);
	}
}
