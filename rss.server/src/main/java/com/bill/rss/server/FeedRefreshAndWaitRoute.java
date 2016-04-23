package com.bill.rss.server;

import spark.Request;
import spark.Response;
import spark.Route;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.mongodb.MongoFeedUpdater;

public class FeedRefreshAndWaitRoute extends Route {

    private final FeedUpdater feedUpdater;

    protected FeedRefreshAndWaitRoute(String path) {
        super(path);
        feedUpdater = new MongoFeedUpdater();
    }

    @Override
    public Object handle(Request request, Response response) {
        feedUpdater.updateWithLatestFeeds();
        return 2;
    }

}
