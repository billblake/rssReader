package com.bill.rss.server;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.mongodb.MongoFeedUpdater;

public class FeedRefreshRoute  extends BaseRoute {

    private FeedUpdater feedUpdater;

    protected FeedRefreshRoute(String path) {
        super(path);
        feedUpdater = new MongoFeedUpdater();
    }

    @Override
    public Object handle(Request request, Response response) {
        feedUpdater.updateWithLatestFeeds();
        return "ok";
    }
}