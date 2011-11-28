package com.all.client.view.contacts;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.all.chat.ChatType;
import com.all.client.view.util.JTreeCoordinateHelper;
import com.all.observ.Observable;
import com.all.observ.ObservePropertyChanged;
import com.all.shared.model.City;
import com.all.shared.model.ContactInfo;
import com.all.shared.model.Gender;
import com.all.shared.model.User;

public class TestContactListPanel {

	@InjectMocks
	@SuppressWarnings("deprecation")
	private ContactListPanel contactListPanel = new ContactListPanel();

	@Mock
	private ContactTree contactTree;
	@Mock
	private JTreeCoordinateHelper jTreeHelper;
	@Mock
	private JViewportHelper viewportHelper;
	@Mock
	private JViewport viewPort;
	@Mock
	private ChatSelectionPanel chatSelectionPanel;
	@Mock
	private JScrollPane jScrollPane;

	private Collection<ContactInfo> receivedContacts = new ArrayList<ContactInfo>();

	private boolean expandNodes;
	private Point viewPoint;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		viewPoint = new Point(0, 100);
		when(contactTree.getTreeHelper()).thenReturn(jTreeHelper);
		when(jScrollPane.getViewport()).thenReturn(viewPort);
		when(viewportHelper.getViewport(isA(JScrollPane.class))).thenReturn(viewPort);
		when(viewPort.getViewPosition()).thenReturn(viewPoint);
		when(chatSelectionPanel.onChatTypeSelected()).thenReturn(
				new Observable<ObservePropertyChanged<ChatSelectionPanel, ChatType>>());
	}

	@Test
	public void shouldUpdateContactTree() throws Exception {
		contactListPanel.updateContactTree(receivedContacts, expandNodes);
		verify(viewportHelper).setViewPosition(viewPoint);
	}

	@Test
	public void shouldRefreshJTreeOnInitialState() throws Exception {
		ContactInfo contactInfo = new ContactInfo(createUser());
		List<ContactInfo> contactList = new ArrayList<ContactInfo>();
		contactList.add(contactInfo);
		try {
			contactListPanel.onContactListUpdated(contactList);
		} catch (Exception e) {
		}

		verify(viewportHelper).getViewport(jScrollPane);
		verify(viewPort).getViewPosition();
		verify(jTreeHelper).saveState();
		verify(jTreeHelper).restoreState();
		verify(viewportHelper).setViewPosition(viewPoint);
		verify(contactTree).setNodesTitles();
	}

	private User createUser() {
		User user = new User();
		user.setId(100L);
		user.setFirstName("First Name");
		user.setLastName("Last Name");
		user.setEmail("user@all.com");
		user.setPassword("password");
		user.setGender(Gender.FEMALE);
		user.setIdLocation("123456");
		user.setQuote("some quote");
		user.setZipCode("54766");
		user.setVersion(3L);
		user.setDay(24);
		user.setMonth(8);
		user.setYear(1984);
		user.setCity(new City());
		return user;
	}
}
