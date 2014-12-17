package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.FeedItem;

public interface FeedItemProvider {

	List<FeedItem> retrieveFeedItems(String categoryId, String feedId, String username, int page);
}
