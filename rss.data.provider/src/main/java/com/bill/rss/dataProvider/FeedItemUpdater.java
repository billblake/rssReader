package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.FeedItem;

public interface FeedItemUpdater {

    FeedItem markFeedItemAsRead(String feedItemId);

    FeedItem deleteFeedItem(String feedItemId);

    List<FeedItem> markFeedItemsForCategoryAsRead(String categoryId);

    List<FeedItem> markFeedItemsForFeedAsRead(String feedId);

    List<FeedItem> markAllFeedItemsAsRead();

    FeedItem saveFeedItem(String feedItemId);

}
