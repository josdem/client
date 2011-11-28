package com.all.client.view.dialog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.all.i18n.Messages;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.testing.MockInyectRunner;
import com.all.testing.UnderTest;

@RunWith(MockInyectRunner.class)
public class TestAddAsAFriendProfileDialog {
	private static final String CONTAINER_NAME_EXPECTED = "addAsAFriendPanelProfile";
	private static final String TITTLE_NAME_EXPECTED = "boldFont12Purple90_74_103";
	private static final Rectangle TITTLE_BOUNDS_EXPECTED = new Rectangle(10, 4, 140, 16);
	private static final String CLOSE_BUTTON_NAME_EXPECTED = "addAsAFriendCloseButtonProfile";
	private static final Rectangle CLOSE_BUTTON_BOUNDS_EXPECTED = new Rectangle(157, 3, 20, 20);
	private static final Rectangle PHOTO_PANEL_BOUNDS_EXPECTED = new Rectangle(41, 32, 98, 98);
	private static final Rectangle NAME_LABEL_BOUNDS_EXPECTED = new Rectangle(5, 137, 170, 36);
	private static final String NAME_LABEL_NAME_EXPECTED = "boldFont12Purple90_74_103";
	private static final Rectangle CITY_LABEL_BOUNDS_EXPECTED = new Rectangle(5, 178, 170, 14);
	private static final String CITY_LABEL_NAME_EXPECTED = "plainFont11Purple90_74_103";
	private static final Rectangle INVITATION_BUTTON_BOUNDS_EXPECTED = new Rectangle(19, 227, 142, 22);
	private static final Rectangle COUNTRY_LABEL_BOUNDS_EXPECTED = new Rectangle(5, 199, 170, 14);
	@UnderTest
	private AddAsAFriendProfileDialog addAsAFriendProfileDialog;
	private JPanel contentPane;
	@Mock
	private ContactInfo contactInfo;
	@Mock
	private Messages messages;
	@Mock
	private City city;

	@Before
	public void init() {
		when(contactInfo.getName()).thenReturn("Hilda Chablé Jesusa Canuta de los Santos Olvidados");
		when(contactInfo.getCity()).thenReturn(city);
		when(city.getCityName()).thenReturn("Mexico City");
		when(city.getCountryName()).thenReturn("Mexico");
		addAsAFriendProfileDialog = new AddAsAFriendProfileDialog(contactInfo, messages);
		contentPane = (JPanel) addAsAFriendProfileDialog.getContentPane();
	}

	@Test
	public void shouldCreateAddFriendDialog() throws Exception {
		assertNotNull(addAsAFriendProfileDialog);
		assertEquals(CONTAINER_NAME_EXPECTED, contentPane.getName());
		assertTrue(contentPane instanceof JPanel);
		assertNull(contentPane.getLayout());
	}

	@Test
	public void shouldHaveTittleLabel() throws Exception {
		Component tittleLabel = contentPane.getComponent(0);
		assertEquals(TITTLE_NAME_EXPECTED, tittleLabel.getName());
		assertEquals(TITTLE_BOUNDS_EXPECTED, tittleLabel.getBounds());
	}

	@Test
	public void shouldHaveCloseButton() throws Exception {
		JButton closeButton = (JButton) contentPane.getComponent(1);
		assertEquals(CLOSE_BUTTON_NAME_EXPECTED, closeButton.getName());
		assertEquals(CLOSE_BUTTON_BOUNDS_EXPECTED, closeButton.getBounds());
		closeButton.doClick();
		assertFalse(addAsAFriendProfileDialog.isShowing());
	}

	@Test
	public void shouldHavePhotoPanel() throws Exception {
		Component photoPanel = contentPane.getComponent(2);
		assertEquals(PHOTO_PANEL_BOUNDS_EXPECTED, photoPanel.getBounds());

	}

	@Test
	public void shouldHaveNameLabel() throws Exception {
		Component nameLabel = contentPane.getComponent(3);
		assertEquals(NAME_LABEL_BOUNDS_EXPECTED, nameLabel.getBounds());
		assertEquals(NAME_LABEL_NAME_EXPECTED, nameLabel.getName());
	}

	@Test
	public void shouldHaveCityLabel() throws Exception {
		Component cityLabel = contentPane.getComponent(4);
		assertEquals(CITY_LABEL_BOUNDS_EXPECTED, cityLabel.getBounds());
		assertEquals(CITY_LABEL_NAME_EXPECTED, cityLabel.getName());
	}

	@Test
	public void shouldHaveCountryLabel() throws Exception {
		Component countryLabel = contentPane.getComponent(5);
		assertEquals(COUNTRY_LABEL_BOUNDS_EXPECTED, countryLabel.getBounds());
	}

	@Test
	public void shouldHaveInvitationButton() throws Exception {
		Component invitationButton = contentPane.getComponent(6);
		assertEquals(INVITATION_BUTTON_BOUNDS_EXPECTED, invitationButton.getBounds());

	}

}
