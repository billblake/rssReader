package com.bill.rss.server;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemUpdater;
import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.FeedItem.MongoFeedItemUpdater;

public class DeleteFeedItemRoute extends BaseFeedItemRoute {

    private final FeedItemUpdater feedItemUpdater = new MongoFeedItemUpdater();

    protected DeleteFeedItemRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        FeedItem feedItem = feedItemUpdater.deleteFeedItem(getFeedItemId(request), getUsername(request));
        return JsonUtils.convertObjectToJson(feedItem);
    }

}
