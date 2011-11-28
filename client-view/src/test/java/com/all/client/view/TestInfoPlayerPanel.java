package com.all.client.view;

import static org.junit.Assert.assertEquals;

import javax.swing.JLabel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.all.client.UnitTestCase;
import com.all.client.model.LocalTrack;
import com.all.core.events.MediaPlayerTrackPlayedEvent;
import com.all.i18n.Messages;

public class TestInfoPlayerPanel extends UnitTestCase {

	@Mock
	private Messages messages;

	InfoPlayerPanel infoPlayerPanel;

	@Before
	public void prepare() {
		infoPlayerPanel = new InfoPlayerPanel();
		infoPlayerPanel.setMessages(messages);
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldUpdateInfoPlayerInPlayState() throws Exception {
		assertEquals("-9:99:99", ((JLabel) getPrivateField(infoPlayerPanel, "remainingLabel")).getText());
		assertEquals("0:00:00", ((JLabel) getPrivateField(infoPlayerPanel, "durationLabel")).getText());

		LocalTrack track = LocalTrack.createEmptyTrack("testing");
		track.setDuration(60 * 5);
		MediaPlayerTrackPlayedEvent event = new MediaPlayerTrackPlayedEvent(track, 0);

		invokePrivateMethod(infoPlayerPanel, "updateTrackInfo", event);

		assertEquals("-5:00", ((JLabel) getPrivateField(infoPlayerPanel, "remainingLabel")).getText());
		assertEquals("0:00", ((JLabel) getPrivateField(infoPlayerPanel, "durationLabel")).getText());
	}
}
