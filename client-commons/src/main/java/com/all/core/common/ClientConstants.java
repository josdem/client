package com.all.core.common;

public interface ClientConstants {
	
	long PRESENCE_ANNOUNCEMENT_DELAY = 1 * 60 * 1000; // 1 min.
	
	long CONTACT_LIST_RETRIVAL_TIMEOUT = 1 * 60 * 1000; // 1 min.
	
	long ONLINE_USERS_REQUEST_TIMEOUT = 1 * 60 * 1000; // 1 min.
	
	long ALERTS_REQUEST_DELAY = 10 * 60 * 1000; // 10 min.
	
	long PRESENCE_TIMEOUT = 10 * 60 * 1000; // 10 min.

	long PUBLISH_USAGE_STATS_DELAY = 10 * 60 * 1000; // 10 min.

	
	String CONNECTION_ERROR_KEY = "error.connection";

	String SELF_INVITATION_ERROR_KEY = "addContact.myself";
}
