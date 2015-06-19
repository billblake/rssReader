package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class DeleteAllFeedItemsRoute extends BaseFeedItemRoute {

    protected DeleteAllFeedItemsRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        verifyUserLoggedIn(request, response);
        List<FeedItem> feedItems = feedItemUpdater.deleteAllFeedItems(getUsername(request));
        return "{\"numberUpdated\":\"" + feedItems.size() + "\"}";
    }

}
