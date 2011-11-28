package com.all.client.controller.beans;

public class FeedsRequest {

	private final Long lastId;
	private final Long userId;

	public FeedsRequest(Long lastId, Long userId) {
		this.lastId = lastId;
		this.userId = userId;
	}

	public Long getLastId() {
		return lastId;
	}

	public Long getUserId() {
		return userId;
	}

}
