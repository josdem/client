package com.all.client.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.annotation.IfProfileValue;

import com.all.client.SimpleGUITest;

public class TestHipecotechTopPanel extends SimpleGUITest {

	@InjectMocks
	private HipecotechTopPanel topPanel = new HipecotechTopPanel();
	private PokeballPanel pokeballPanel = new PokeballPanel();

	@Before
	public void setUp() throws Exception {
		topPanel.setMessages(messages);
		topPanel.setVolumePanel(new VolumePanel());
		setValueToPrivateField(topPanel, "pokeballPanel", pokeballPanel);
		topPanel.initialize();
	}

	public void setValueToPrivateField(Object object, String fieldName, Object value) throws Exception {
		Field privateField = object.getClass().getDeclaredField(fieldName);
		privateField.setAccessible(true);
		privateField.set(object, value);
	}

	@Test
	public void shouldHaveGridBagLayoutAndDimensions() throws Exception {
		assertTrue(topPanel.getLayout() instanceof GridBagLayout);
		assertEquals("topPanelBackground", topPanel.getName());
	}

	@Test
	@IfProfileValue(name = "os.name", values = { "Windows 7", "Windows Vista", "Windows NT", "Windows XP" })
	// FIXME to run on mac
	public void shouldHaveCorrectSizes() throws Exception {
		Dimension preferredSize = new Dimension(1020, 63);
		Dimension minSize = new Dimension(792, 63);
		assertEquals(minSize, topPanel.getMinimumSize());
		assertEquals(preferredSize, topPanel.getPreferredSize());
	}

	@Test
	public void shouldInitializeLeftShortCutSeparatorPanel() throws Exception {
		JPanel rightBubbleSeparatorPanel = topPanel.getRightBubbleSeparatorPanel();
		assertNotNull(rightBubbleSeparatorPanel);
		assertEquals(2, rightBubbleSeparatorPanel.getMinimumSize().getWidth(), 0);
		assertEquals(63, rightBubbleSeparatorPanel.getMinimumSize().getHeight(), 0);
		assertEquals(17, rightBubbleSeparatorPanel.getSize().getWidth(), 0);
		assertEquals(63, rightBubbleSeparatorPanel.getSize().getHeight(), 0);
		assertEquals(17, rightBubbleSeparatorPanel.getPreferredSize().getWidth(), 0);
		assertEquals(63, rightBubbleSeparatorPanel.getPreferredSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeRightShortCutSeparatorPanel() throws Exception {
		JPanel rightShortcutSeparatorPanel = topPanel.getRightShortcutSeparatorPanel();
		assertNotNull(rightShortcutSeparatorPanel);
		assertEquals(2, rightShortcutSeparatorPanel.getMinimumSize().getWidth(), 0);
		assertEquals(61, rightShortcutSeparatorPanel.getMinimumSize().getHeight(), 0);
		assertEquals(12, rightShortcutSeparatorPanel.getSize().getWidth(), 0);
		assertEquals(61, rightShortcutSeparatorPanel.getSize().getHeight(), 0);
		assertEquals(12, rightShortcutSeparatorPanel.getPreferredSize().getWidth(), 0);
		assertEquals(61, rightShortcutSeparatorPanel.getPreferredSize().getHeight(), 0);
		assertEquals(30, rightShortcutSeparatorPanel.getMaximumSize().getWidth(), 0);
		assertEquals(61, rightShortcutSeparatorPanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeLeftBubbleSeparatorPanel() {
		JPanel leftBubbleSeparatorPanel = topPanel.getLeftBubbleSeparatorPanel();
		assertNotNull(leftBubbleSeparatorPanel);
		assertEquals(2, leftBubbleSeparatorPanel.getMinimumSize().getWidth(), 0);
		assertEquals(63, leftBubbleSeparatorPanel.getMinimumSize().getHeight(), 0);
		assertEquals(13, leftBubbleSeparatorPanel.getSize().getWidth(), 0);
		assertEquals(63, leftBubbleSeparatorPanel.getSize().getHeight(), 0);
		assertEquals(13, leftBubbleSeparatorPanel.getPreferredSize().getWidth(), 0);
		assertEquals(63, leftBubbleSeparatorPanel.getPreferredSize().getHeight(), 0);
	}

	@Test
	public void shouldIntializeLeftPlayerSeparator() {
		JPanel volumePlayerSeparatorPanel = topPanel.getVolumePlayerSeparatorPanel();
		assertNotNull(volumePlayerSeparatorPanel);
		assertEquals(14, volumePlayerSeparatorPanel.getSize().getWidth(), 0);
		assertEquals(63, volumePlayerSeparatorPanel.getSize().getHeight(), 0);
		assertEquals(14, volumePlayerSeparatorPanel.getPreferredSize().getWidth(), 0);
		assertEquals(63, volumePlayerSeparatorPanel.getPreferredSize().getHeight(), 0);
		assertEquals(3, volumePlayerSeparatorPanel.getMinimumSize().getWidth(), 0);
		assertEquals(63, volumePlayerSeparatorPanel.getMinimumSize().getHeight(), 0);
		assertEquals(40, volumePlayerSeparatorPanel.getMaximumSize().getWidth(), 0);
		assertEquals(63, volumePlayerSeparatorPanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeFixedSeparatorPanel() {
		JPanel fixedSeparatorPanel = topPanel.getFixedSeparatorPanel();
		assertNotNull(fixedSeparatorPanel);
		assertEquals(8, fixedSeparatorPanel.getMinimumSize().getWidth(), 0);
		assertEquals(63, fixedSeparatorPanel.getMinimumSize().getHeight(), 0);
		assertEquals(8, fixedSeparatorPanel.getSize().getWidth(), 0);
		assertEquals(63, fixedSeparatorPanel.getSize().getHeight(), 0);
		assertEquals(8, fixedSeparatorPanel.getPreferredSize().getWidth(), 0);
		assertEquals(63, fixedSeparatorPanel.getPreferredSize().getHeight(), 0);
		assertEquals(8, fixedSeparatorPanel.getMaximumSize().getWidth(), 0);
		assertEquals(63, fixedSeparatorPanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeBackBubblePanel() {
		JPanel backBubblePanel = topPanel.getBackBubblePanel();
		assertNotNull(backBubblePanel);
		assertEquals(450, backBubblePanel.getMinimumSize().getWidth(), 0);
		assertEquals(63, backBubblePanel.getMinimumSize().getHeight(), 0);
		assertEquals(564, backBubblePanel.getSize().getWidth(), 0);
		assertEquals(63, backBubblePanel.getSize().getHeight(), 0);
		assertEquals(564, backBubblePanel.getPreferredSize().getWidth(), 0);
		assertEquals(63, backBubblePanel.getPreferredSize().getHeight(), 0);
		assertEquals(999, backBubblePanel.getMaximumSize().getWidth(), 0);
		assertEquals(63, backBubblePanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeShorCutPanel() {
		JPanel shorcutPanel = topPanel.getShorcutPanel();
		assertNotNull(shorcutPanel);
		assertEquals(83, shorcutPanel.getMinimumSize().getWidth(), 0);
		assertEquals(63, shorcutPanel.getMinimumSize().getHeight(), 0);
		assertEquals(83, shorcutPanel.getSize().getWidth(), 0);
		assertEquals(63, shorcutPanel.getSize().getHeight(), 0);
		assertEquals(83, shorcutPanel.getPreferredSize().getWidth(), 0);
		assertEquals(63, shorcutPanel.getPreferredSize().getHeight(), 0);
		assertEquals(83, shorcutPanel.getMaximumSize().getWidth(), 0);
		assertEquals(63, shorcutPanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeVolumePanel() throws Exception {
		VolumePanel volumePanel = topPanel.getVolumePanel();
		assertNotNull(volumePanel);
		assertEquals(81, volumePanel.getMinimumSize().getWidth(), 0);
		assertEquals(61, volumePanel.getMinimumSize().getHeight(), 0);
		assertEquals(81, volumePanel.getSize().getWidth(), 0);
		assertEquals(61, volumePanel.getSize().getHeight(), 0);
		assertEquals(81, volumePanel.getPreferredSize().getWidth(), 0);
		assertEquals(61, volumePanel.getPreferredSize().getHeight(), 0);
		assertEquals(81, volumePanel.getMaximumSize().getWidth(), 0);
		assertEquals(61, volumePanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldInitializeLeftVolume() throws Exception {
		JPanel leftVolumeSeparatorPanel = topPanel.getLeftVolumeSeparatorPanel();
		assertNotNull(leftVolumeSeparatorPanel);
		assertEquals(1, leftVolumeSeparatorPanel.getMinimumSize().getWidth(), 0);
		assertEquals(61, leftVolumeSeparatorPanel.getMinimumSize().getHeight(), 0);
		assertEquals(12, leftVolumeSeparatorPanel.getSize().getWidth(), 0);
		assertEquals(61, leftVolumeSeparatorPanel.getSize().getHeight(), 0);
		assertEquals(12, leftVolumeSeparatorPanel.getPreferredSize().getWidth(), 0);
		assertEquals(61, leftVolumeSeparatorPanel.getPreferredSize().getHeight(), 0);
		assertEquals(30, leftVolumeSeparatorPanel.getMaximumSize().getWidth(), 0);
		assertEquals(61, leftVolumeSeparatorPanel.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldSeparator1HaveAllSizes() throws Exception {
		JPanel panelSeparator1 = topPanel.getPanelSeparator1();
		assertEquals(1, panelSeparator1.getMinimumSize().getWidth(), 0);
		assertEquals(61, panelSeparator1.getMinimumSize().getHeight(), 0);
		assertEquals(12, panelSeparator1.getSize().getWidth(), 0);
		assertEquals(61, panelSeparator1.getSize().getHeight(), 0);
		assertEquals(12, panelSeparator1.getPreferredSize().getWidth(), 0);
		assertEquals(61, panelSeparator1.getPreferredSize().getHeight(), 0);
		assertEquals(20, panelSeparator1.getMaximumSize().getWidth(), 0);
		assertEquals(61, panelSeparator1.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldSeparator2HaveAllSizes() throws Exception {
		JPanel panelSeparator2 = topPanel.getPanelSeparator2();
		assertEquals(1, panelSeparator2.getMinimumSize().getWidth(), 0);
		assertEquals(61, panelSeparator2.getMinimumSize().getHeight(), 0);
		assertEquals(12, panelSeparator2.getSize().getWidth(), 0);
		assertEquals(61, panelSeparator2.getSize().getHeight(), 0);
		assertEquals(12, panelSeparator2.getPreferredSize().getWidth(), 0);
		assertEquals(61, panelSeparator2.getPreferredSize().getHeight(), 0);
		assertEquals(15, panelSeparator2.getMaximumSize().getWidth(), 0);
		assertEquals(61, panelSeparator2.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldSeparator3HaveAllSizes() throws Exception {
		JPanel panelSeparator3 = topPanel.getPanelSeparator3();
		assertEquals(1, panelSeparator3.getMinimumSize().getWidth(), 0);
		assertEquals(61, panelSeparator3.getMinimumSize().getHeight(), 0);
		assertEquals(12, panelSeparator3.getSize().getWidth(), 0);
		assertEquals(61, panelSeparator3.getSize().getHeight(), 0);
		assertEquals(12, panelSeparator3.getPreferredSize().getWidth(), 0);
		assertEquals(61, panelSeparator3.getPreferredSize().getHeight(), 0);
		assertEquals(15, panelSeparator3.getMaximumSize().getWidth(), 0);
		assertEquals(61, panelSeparator3.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldSeparator4HaveAllSizes() throws Exception {
		JPanel panelSeparator4 = topPanel.getPanelSeparator4();
		assertEquals(1, panelSeparator4.getMinimumSize().getWidth(), 0);
		assertEquals(61, panelSeparator4.getMinimumSize().getHeight(), 0);
		assertEquals(12, panelSeparator4.getSize().getWidth(), 0);
		assertEquals(61, panelSeparator4.getSize().getHeight(), 0);
		assertEquals(12, panelSeparator4.getPreferredSize().getWidth(), 0);
		assertEquals(61, panelSeparator4.getPreferredSize().getHeight(), 0);
		assertEquals(40, panelSeparator4.getMaximumSize().getWidth(), 0);
		assertEquals(61, panelSeparator4.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldSeparator5HaveAllSizes() throws Exception {
		JPanel panelSeparator5 = topPanel.getPanelSeparator5();
		assertEquals(1, panelSeparator5.getMinimumSize().getWidth(), 0);
		assertEquals(61, panelSeparator5.getMinimumSize().getHeight(), 0);
		assertEquals(12, panelSeparator5.getSize().getWidth(), 0);
		assertEquals(61, panelSeparator5.getSize().getHeight(), 0);
		assertEquals(12, panelSeparator5.getPreferredSize().getWidth(), 0);
		assertEquals(61, panelSeparator5.getPreferredSize().getHeight(), 0);
		assertEquals(15, panelSeparator5.getMaximumSize().getWidth(), 0);
		assertEquals(61, panelSeparator5.getMaximumSize().getHeight(), 0);
	}


	@Test
	public void shouldHavePreviousButton() throws Exception {
		JButton backButton = topPanel.getBackButton();
		assertEquals("rewindButton", backButton.getName());
		assertEquals(20, backButton.getMargin().top);
		assertEquals(20, backButton.getMargin().bottom);
		assertEquals(29, backButton.getMinimumSize().getWidth(), 0);
		assertEquals(20, backButton.getMinimumSize().getHeight(), 0);
		assertEquals(29, backButton.getSize().getWidth(), 0);
		assertEquals(20, backButton.getSize().getHeight(), 0);
		assertEquals(29, backButton.getPreferredSize().getWidth(), 0);
		assertEquals(20, backButton.getPreferredSize().getHeight(), 0);
		assertEquals(29, backButton.getMaximumSize().getWidth(), 0);
		assertEquals(20, backButton.getMaximumSize().getHeight(), 0);

	}

	@Test
	public void shouldHavePlayButton() throws Exception {
		JButton playButton = topPanel.getPlayButton();
		assertEquals("playButton", playButton.getName());
		assertEquals(12, playButton.getMargin().top);
		assertEquals(11, playButton.getMargin().bottom);
		assertEquals(34, playButton.getMinimumSize().getWidth(), 0);
		assertEquals(38, playButton.getMinimumSize().getHeight(), 0);
		assertEquals(34, playButton.getSize().getWidth(), 0);
		assertEquals(38, playButton.getSize().getHeight(), 0);
		assertEquals(34, playButton.getPreferredSize().getWidth(), 0);
		assertEquals(38, playButton.getPreferredSize().getHeight(), 0);
		assertEquals(34, playButton.getMaximumSize().getWidth(), 0);
		assertEquals(38, playButton.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldHaveNextButton() throws Exception {
		JButton nextButton = topPanel.getNextButton();
		assertEquals("forwardButton", nextButton.getName());
		assertEquals(20, nextButton.getMargin().top);
		assertEquals(20, nextButton.getMargin().bottom);
		assertEquals(29, nextButton.getMinimumSize().getWidth(), 0);
		assertEquals(20, nextButton.getMinimumSize().getHeight(), 0);
		assertEquals(29, nextButton.getSize().getWidth(), 0);
		assertEquals(20, nextButton.getSize().getHeight(), 0);
		assertEquals(29, nextButton.getPreferredSize().getWidth(), 0);
		assertEquals(20, nextButton.getPreferredSize().getHeight(), 0);
		assertEquals(29, nextButton.getMaximumSize().getWidth(), 0);
		assertEquals(20, nextButton.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldHaveShuffleButton() throws Exception {
		assertEquals("shuffleButton", topPanel.getShuffleButton().getName());
		assertEquals(20, topPanel.getShuffleButton().getMargin().top);
		assertEquals(19, topPanel.getShuffleButton().getMargin().bottom);
		assertEquals(29, topPanel.getShuffleButton().getMinimumSize().getWidth(), 0);
		assertEquals(22, topPanel.getShuffleButton().getMinimumSize().getHeight(), 0);
		assertEquals(29, topPanel.getShuffleButton().getSize().getWidth(), 0);
		assertEquals(22, topPanel.getShuffleButton().getSize().getHeight(), 0);
		assertEquals(29, topPanel.getShuffleButton().getPreferredSize().getWidth(), 0);
		assertEquals(22, topPanel.getShuffleButton().getPreferredSize().getHeight(), 0);
		assertEquals(29, topPanel.getShuffleButton().getMaximumSize().getWidth(), 0);
		assertEquals(22, topPanel.getShuffleButton().getMaximumSize().getHeight(), 0);

	}

	@Test
	public void shouldHaveRepeatButton() throws Exception {
		JButton repeatButton = topPanel.getRepeatButton().getComponent();
		assertEquals("repeatButton", repeatButton.getName());
		assertEquals(20, repeatButton.getMargin().top);
		assertEquals(19, repeatButton.getMargin().bottom);
		assertEquals(28, repeatButton.getMinimumSize().getWidth(), 0);
		assertEquals(22, repeatButton.getMinimumSize().getHeight(), 0);
		assertEquals(28, repeatButton.getSize().getWidth(), 0);
		assertEquals(22, repeatButton.getSize().getHeight(), 0);
		assertEquals(28, repeatButton.getPreferredSize().getWidth(), 0);
		assertEquals(22, repeatButton.getPreferredSize().getHeight(), 0);
		assertEquals(28, repeatButton.getMaximumSize().getWidth(), 0);
		assertEquals(22, repeatButton.getMaximumSize().getHeight(), 0);
	}

	@Test
	public void shouldHavePanelsDistribution() throws Exception {
		GridBagLayout layout = (GridBagLayout) topPanel.getLayout();

		JPanel fixedSeparatorPanel = topPanel.getFixedSeparatorPanel();
		assertEquals(fixedSeparatorPanel, topPanel.getComponent(0));
		assertEquals(0, layout.getConstraints(fixedSeparatorPanel).gridx);
		assertEquals(0, layout.getConstraints(fixedSeparatorPanel).gridy);

		JPanel leftVolumeSeparatorPanel = topPanel.getLeftVolumeSeparatorPanel();
		assertEquals(leftVolumeSeparatorPanel, topPanel.getComponent(1));
		assertEquals(1, layout.getConstraints(leftVolumeSeparatorPanel).gridx);
		assertEquals(0, layout.getConstraints(leftVolumeSeparatorPanel).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(leftVolumeSeparatorPanel).fill);
		assertEquals(.05, layout.getConstraints(leftVolumeSeparatorPanel).weightx, 0);

		VolumePanel volumePanel = topPanel.getVolumePanel();
		assertTrue(volumePanel instanceof VolumePanel);
		assertEquals(volumePanel, topPanel.getComponent(2));
		assertEquals(2, layout.getConstraints(volumePanel).gridx);
		assertEquals(0, layout.getConstraints(volumePanel).gridy);

		JPanel volumePlayerSeparatorPanel = topPanel.getVolumePlayerSeparatorPanel();
		assertEquals(volumePlayerSeparatorPanel, topPanel.getComponent(3));
		assertEquals(3, layout.getConstraints(volumePlayerSeparatorPanel).gridx);
		assertEquals(0, layout.getConstraints(volumePlayerSeparatorPanel).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(volumePlayerSeparatorPanel).fill);
		assertEquals(.05, layout.getConstraints(volumePlayerSeparatorPanel).weightx, 0);

		JPanel panelSeparator1 = topPanel.getPanelSeparator1();
		assertEquals(panelSeparator1, topPanel.getComponent(4));
		assertEquals(4, layout.getConstraints(panelSeparator1).gridx);
		assertEquals(0, layout.getConstraints(panelSeparator1).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(panelSeparator1).fill);

		JButton backButton = topPanel.getBackButton();
		assertEquals(backButton, topPanel.getComponent(5));
		assertEquals(5, layout.getConstraints(backButton).gridx);
		assertEquals(0, layout.getConstraints(backButton).gridy);

		JPanel panelSeparator2 = topPanel.getPanelSeparator2();
		assertEquals(panelSeparator2, topPanel.getComponent(6));
		assertEquals(6, layout.getConstraints(panelSeparator2).gridx);
		assertEquals(0, layout.getConstraints(panelSeparator2).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(panelSeparator2).fill);

		JButton playButton = topPanel.getPlayButton();
		assertEquals(playButton, topPanel.getComponent(7));
		assertEquals(7, layout.getConstraints(playButton).gridx);
		assertEquals(0, layout.getConstraints(playButton).gridy);

		JPanel panelSeparator3 = topPanel.getPanelSeparator3();
		assertEquals(panelSeparator3, topPanel.getComponent(8));
		assertEquals(8, layout.getConstraints(panelSeparator3).gridx);
		assertEquals(0, layout.getConstraints(panelSeparator3).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(panelSeparator3).fill);

		JButton nextButton = topPanel.getNextButton();
		assertEquals(nextButton, topPanel.getComponent(9));
		assertEquals(9, layout.getConstraints(nextButton).gridx);
		assertEquals(0, layout.getConstraints(nextButton).gridy);

		JPanel panelSeparator4 = topPanel.getPanelSeparator4();
		assertEquals(panelSeparator4, topPanel.getComponent(10));
		assertEquals(10, layout.getConstraints(panelSeparator4).gridx);
		assertEquals(0, layout.getConstraints(panelSeparator4).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(panelSeparator4).fill);
		assertEquals(.05, layout.getConstraints(panelSeparator4).weightx, 0);

		JButton shuffleButton = topPanel.getShuffleButton();
		assertEquals(shuffleButton, topPanel.getComponent(11));
		assertEquals(11, layout.getConstraints(shuffleButton).gridx);
		assertEquals(0, layout.getConstraints(shuffleButton).gridy);

		JPanel panelSeparator5 = topPanel.getPanelSeparator5();
		assertEquals(panelSeparator5, topPanel.getComponent(12));
		assertEquals(12, layout.getConstraints(panelSeparator5).gridx);
		assertEquals(0, layout.getConstraints(panelSeparator5).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(panelSeparator5).fill);

		JButton repeatButton = topPanel.getRepeatButton().getComponent();
		assertEquals(repeatButton, topPanel.getComponent(13));
		assertEquals(13, layout.getConstraints(repeatButton).gridx);
		assertEquals(0, layout.getConstraints(repeatButton).gridy);

		JPanel leftBubbleSeparatorPanel = topPanel.getLeftBubbleSeparatorPanel();
		assertEquals(leftBubbleSeparatorPanel, topPanel.getComponent(14));
		assertEquals(14, layout.getConstraints(leftBubbleSeparatorPanel).gridx);
		assertEquals(0, layout.getConstraints(leftBubbleSeparatorPanel).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(leftBubbleSeparatorPanel).fill);
		assertEquals(.05, layout.getConstraints(leftBubbleSeparatorPanel).weightx, 0);

		JPanel backBubblePanel = topPanel.getBackBubblePanel();
		assertEquals(backBubblePanel, topPanel.getComponent(15));
		assertEquals(15, layout.getConstraints(backBubblePanel).gridx);
		assertEquals(0, layout.getConstraints(backBubblePanel).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(backBubblePanel).fill);
		assertEquals(.5, layout.getConstraints(backBubblePanel).weightx, 0);

		JPanel rightBubbleSeparatorPanel = topPanel.getRightBubbleSeparatorPanel();
		assertEquals(rightBubbleSeparatorPanel, topPanel.getComponent(16));
		assertEquals(16, layout.getConstraints(rightBubbleSeparatorPanel).gridx);
		assertEquals(0, layout.getConstraints(rightBubbleSeparatorPanel).gridy);
		assertEquals(GridBagConstraints.HORIZONTAL, layout.getConstraints(rightBubbleSeparatorPanel).fill);
		assertEquals(.1, layout.getConstraints(rightBubbleSeparatorPanel).weightx, 0);

		JPanel shorcutPanel = topPanel.getShorcutPanel();
		assertEquals(shorcutPanel, topPanel.getComponent(17));
		assertEquals(17, layout.getConstraints(shorcutPanel).gridx);
		assertEquals(0, layout.getConstraints(shorcutPanel).gridy);

		JPanel fixedSeparator2Panel = topPanel.getFixedSeparator2Panel();
		assertEquals(fixedSeparator2Panel, topPanel.getComponent(18));
		assertEquals(18, layout.getConstraints(fixedSeparator2Panel).gridx);
		assertEquals(0, layout.getConstraints(fixedSeparator2Panel).gridy);
	}

}
