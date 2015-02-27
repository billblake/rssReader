package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.Feed;

public interface FeedProvider {

    List<Feed> retrieveAllFeeds();

    List<Feed> retrieveFeeds(String username);

	Feed retrieveFeed(String feedId, String username);

	List<Feed> retrieveFeedsIn(List<String> feedIds, String username);
}
