package com.all.client.view.toolbar.social;

import java.util.List;

import com.all.shared.model.ContactInfo;

public final class PaginationFriendsPanelManager {
	private static final int SIZE_PAGINATION = 60;
	private List<ContactInfo> friends;
	private int currentPage = 1;
	private int numberOfPages;
	private int module;
	private final int sizePagination;

	public PaginationFriendsPanelManager(List<ContactInfo> friends) {
		this.friends = friends;
		this.sizePagination = SIZE_PAGINATION;
		setNumberPages(friends);

	}

	public PaginationFriendsPanelManager(List<ContactInfo> friends, int sizePagination) {
		this.friends = friends;
		this.sizePagination = sizePagination;
		setNumberPages(friends);
	}

	private void setNumberPages(List<ContactInfo> friends) {
		numberOfPages = (friends.size()) / sizePagination;
		module = (friends.size()) % sizePagination;
		if (module != 0) {
			numberOfPages++;
		}
	}

	public boolean isTherePagination() {
		return friends.size() >= sizePagination;
	}

	public int getPage() {
		return currentPage;
	}

	public List<ContactInfo> getNextFriendsPage() {
		int fromIndex = (currentPage) * sizePagination;
		int toIndex = ((currentPage + 1) * sizePagination);
		currentPage++;
		if (currentPage == numberOfPages) {
			toIndex = (fromIndex + module);
		}
		return friends.subList(fromIndex, toIndex);
	}

	public List<ContactInfo> getFirstFriendsPage() {
		int toIndex = (module >= 0 && module < sizePagination && numberOfPages <= 1) ? module : sizePagination;
		return friends.subList(0, toIndex);
	}

	public List<ContactInfo> getPrevFriendsPage() {
		currentPage--;
		int fromIndex = (currentPage - 1) * sizePagination;
		int toIndex = ((currentPage) * sizePagination);
		return friends.subList(fromIndex, toIndex);
	}

	public boolean isNextPage() {
		return currentPage < numberOfPages;
	}

	public boolean isPrevPage() {
		return currentPage > 1;
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}

}