package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class UpdateAllFeedItemsRoute extends BaseFeedItemRoute {

    public UpdateAllFeedItemsRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        verifyUserLoggedIn(request, response);
        List<FeedItem> feedItems = feedItemUpdater.markAllFeedItemsAsRead();
        return "{\"numberUpdated\":\"" + feedItems.size() + "\"}";
    }

}
