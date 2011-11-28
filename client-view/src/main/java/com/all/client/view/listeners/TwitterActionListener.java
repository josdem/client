package com.all.client.view.listeners;

import com.all.twitter.TwitterStatus;

public interface TwitterActionListener {

	void onTwitterStatustReplied(TwitterStatus twitterStatus);

	void onRetweetedStatus(TwitterStatus twitterStatus);

	void onTwitterStatustDirect(TwitterStatus twitterStatus);

	void onTwitterStatusUpdated(String status);

	void onUserProfileRequested(String screenName);

	void onFollowUser(String screenName);

	void onUnfollowUser(String screenName);

	void onHome();

	void onMentions();

	void onDirectMessages();

}
