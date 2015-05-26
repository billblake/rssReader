package com.bill.rss.dataProvider;

import com.bill.rss.domain.FeedItem;

public interface FeedItemUpdater {

    FeedItem markFeedItemAsRead(String feedItemId);
}
