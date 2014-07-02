package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.Feed;

public interface FeedProvider {

	List<Feed> retrieveFeeds();
	
	Feed retrieveFeed(String feedId);
	
	List<Feed> retrieveFeedsIn(List<String> feedIds);
}
