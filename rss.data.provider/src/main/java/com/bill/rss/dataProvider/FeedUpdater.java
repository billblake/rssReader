package com.bill.rss.dataProvider;

public interface FeedUpdater {

    void updateWithLatestFeeds();

	void updateWithLatestFeeds(String username);
}
