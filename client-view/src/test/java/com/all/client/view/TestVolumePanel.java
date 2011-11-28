package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.swing.JButton;
import javax.swing.JSlider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import com.all.appControl.control.TestEngine;
import com.all.appControl.control.ViewEngine;
import com.all.client.UnitTestCase;
import com.all.core.actions.Actions;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.i18n.Messages;

public class TestVolumePanel extends UnitTestCase {
	private static final String VOLUME_MUTE = "volumeMute";
	private static final String VOLUME_MID = "volumeMid";
	private static final String VOLUME_FULL = "volumeFull";
	private VolumePanel volumePanel;
	private JButton muteButton;
	private JSlider volumeSlider;

	@Mock
	private ViewEngine viewEngine;
	@Spy
	private TestEngine eventEngine = new TestEngine();
	@Mock
	private Messages messages;

	@Before
	public void createTestInstances() throws Exception {
		volumePanel = new VolumePanel();
		volumePanel.setMessages(messages);
		volumePanel.setViewEngine(viewEngine);

		// controller = mock(VolumePanelController.class);
		when(viewEngine.get(Model.UserPreference.PLAYER_VOLUME)).thenReturn(25);

		volumePanel.setup();

		muteButton = (JButton) getPrivateField(volumePanel, "muteButton");
		volumeSlider = (JSlider) getPrivateField(volumePanel, "volumeSlider");

		eventEngine.setup(volumePanel);
	}

	@Test
	public void shoudDefaultMuteButtonStateBeOffAndControllerInvokesSetInitialVolume() throws Exception {
		assertTrue(muteButton.getName().equals(VOLUME_MID));
		verify(viewEngine, times(1)).get(Model.UserPreference.PLAYER_VOLUME);
		assertEquals(25, volumeSlider.getValue());
	}

	@Test
	public void shouldInvokeChangeEventOnControllerWhenSliderChanges() throws Exception {
		volumeSlider.setValue(10);
		verify(viewEngine).sendValueAction(Actions.Player.UPDATE_VOLUME, 10);
		assertEquals(VOLUME_MID, muteButton.getName());
	}

	@Test
	public void shouldInvokeChangeEventOnControllerWhenSliderIsBeingAdjusted() throws Exception {
		volumeSlider.setValueIsAdjusting(true);
		volumeSlider.setValue(10);
		verify(viewEngine, atLeastOnce()).sendValueAction(Actions.Player.UPDATE_VOLUME, 10);
	}

	@Test
	public void shouldInvokeMuteButtonClickedOnControllerWhenButtonWasClicked() throws Exception {
		muteButton.doClick();
		verify(viewEngine, times(1)).send(Actions.Player.TOGGLE_MUTE);
	}

	@Test
	public void shouldReactToChangesWhenEventsHappenChangeButtonState() throws Exception {
		assertTrue(muteButton.getName().equals(VOLUME_MID));
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(1));
		assertTrue(muteButton.getName().equals(VOLUME_MUTE));
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(10));
		assertTrue(muteButton.getName().equals(VOLUME_MID));
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(100));
		assertTrue(muteButton.getName().equals(VOLUME_FULL));
	}

	@Test
	public void shouldReactToEventsOnTheSliderBarOnlyWhenItsNotAdjusting() throws Exception {
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(44));
		assertEquals(44, volumeSlider.getValue());
		volumeSlider.setValueIsAdjusting(true);
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(0));
		assertEquals(44, volumeSlider.getValue());
		volumeSlider.setValueIsAdjusting(false);
		eventEngine.fireEvent(Events.Player.VOLUME_CHANGED, new ValueEvent<Integer>(20));
		assertEquals(20, volumeSlider.getValue());
	}
}
