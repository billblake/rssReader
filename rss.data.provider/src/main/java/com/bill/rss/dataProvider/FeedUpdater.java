package com.bill.rss.dataProvider;

import com.bill.rss.domain.Feed;

public interface FeedUpdater {

    void updateWithLatestFeeds();

	void updateWithLatestFeeds(String username);

	Feed addFeed(Feed feed);

	Feed saveFeed(Feed feed);
}
