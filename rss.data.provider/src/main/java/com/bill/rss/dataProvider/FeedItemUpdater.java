package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.FeedItem;

public interface FeedItemUpdater {

    FeedItem saveFeedItem(String feedItemId, String userName);

    FeedItem markFeedItemAsRead(String feedItemId, String userName);

    List<FeedItem> markFeedItemsForCategoryAsRead(String categoryId, String userName);

    List<FeedItem> markFeedItemsForFeedAsRead(String feedId, String userName);

    List<FeedItem> markAllFeedItemsAsRead(String userName);

    FeedItem deleteFeedItem(String feedItemId, String userName);

    List<FeedItem> deleteFeedItemsForCategory(String categoryId, String userName);

    List<FeedItem> deleteFeedItemsForFeed(String feedId, String userName);

    List<FeedItem> deleteAllFeedItems(String userName);

}
