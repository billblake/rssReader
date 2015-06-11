package com.bill.rss.server;

import com.bill.rss.dataProvider.FeedItemUpdater;
import com.bill.rss.mongodb.FeedItemRetriever;


public abstract class BaseFeedItemRoute extends BaseRoute {

    protected final FeedItemUpdater feedItemUpdater = new FeedItemRetriever();

    protected BaseFeedItemRoute(String path) {
        super(path);
    }
}
