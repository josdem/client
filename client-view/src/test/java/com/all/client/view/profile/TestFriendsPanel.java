package com.all.client.view.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.all.client.view.toolbar.social.PaginationFriendsPanelManager;
import com.all.shared.model.ContactInfo;

public class TestFriendsPanel {

	private List<ContactInfo> friends;

	PaginationFriendsPanelManager friendsPanelManager;

	@SuppressWarnings("deprecation")
	@Test
	public void shouldCreateFriendsPanelManager() throws Exception {
		friends = new ArrayList<ContactInfo>();
		friends.add(new ContactInfo());
		friends.add(new ContactInfo());

		friendsPanelManager = new PaginationFriendsPanelManager(friends);
		assertNotNull(friendsPanelManager);
		assertEquals(1, friendsPanelManager.getPage());
		assertFalse(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());
	}

	@Test
	public void shouldCreateTwoPages() throws Exception {
		friends = new ArrayList<ContactInfo>();
		addMoreContacts(65);
		friendsPanelManager = new PaginationFriendsPanelManager(friends);
		assertNotNull(friendsPanelManager);
		assertEquals(1, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());
	}

	@Test
	public void shouldGoNextPage() throws Exception {
		friends = new ArrayList<ContactInfo>();
		addMoreContacts(65);
		friendsPanelManager = new PaginationFriendsPanelManager(friends);
		assertNotNull(friendsPanelManager);
		assertEquals(1, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());
		assertNotNull(friendsPanelManager.getNextFriendsPage());
		assertFalse(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());
	}

	@Test
	public void shouldGoPrevPage() throws Exception {
		friends = new ArrayList<ContactInfo>();
		addMoreContacts(65);
		friendsPanelManager = new PaginationFriendsPanelManager(friends);
		assertNotNull(friendsPanelManager);
		assertEquals(1, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());
		assertNotNull(friendsPanelManager.getNextFriendsPage());
		assertFalse(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());
		assertEquals(2, friendsPanelManager.getPage());
		assertNotNull(friendsPanelManager.getPrevFriendsPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());

	}

	@Test
	public void shouldGoNextAndGoBack() throws Exception {
		friends = new ArrayList<ContactInfo>();
		addMoreContacts(185);
		friendsPanelManager = new PaginationFriendsPanelManager(friends);
		assertNotNull(friendsPanelManager);
		assertEquals(1, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());

		List<ContactInfo> nextFriendsPage = friendsPanelManager.getNextFriendsPage();
		assertNotNull(nextFriendsPage);
		assertEquals(60, nextFriendsPage.size());
		assertEquals(2, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());

		nextFriendsPage = friendsPanelManager.getNextFriendsPage();
		assertNotNull(nextFriendsPage);
		assertEquals(60, nextFriendsPage.size());
		assertEquals(3, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());

		nextFriendsPage = friendsPanelManager.getNextFriendsPage();
		assertNotNull(nextFriendsPage);
		assertEquals(5, nextFriendsPage.size());
		assertEquals(4, friendsPanelManager.getPage());
		assertFalse(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());

		assertNotNull(friendsPanelManager.getPrevFriendsPage());
		assertEquals(3, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());

		assertNotNull(friendsPanelManager.getPrevFriendsPage());
		assertEquals(2, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertTrue(friendsPanelManager.isPrevPage());

		assertNotNull(friendsPanelManager.getPrevFriendsPage());
		assertEquals(1, friendsPanelManager.getPage());
		assertTrue(friendsPanelManager.isNextPage());
		assertFalse(friendsPanelManager.isPrevPage());
	}

	@SuppressWarnings("deprecation")
	private void addMoreContacts(int n) {
		for (int i = 0; i < n; i++) {
			friends.add(new ContactInfo());
		}
	}
}
