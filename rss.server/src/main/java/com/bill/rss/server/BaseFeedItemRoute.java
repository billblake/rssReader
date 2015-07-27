package com.bill.rss.server;

import com.bill.rss.dataProvider.FeedItemUpdater;
import com.bill.rss.mongodb.FeedItem.MongoFeedItemUpdater;


public abstract class BaseFeedItemRoute extends BaseRoute {

    protected FeedItemUpdater feedItemUpdater = new MongoFeedItemUpdater();

    protected BaseFeedItemRoute(String path) {
        super(path);
    }
}
