package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class UpdateAllFeedItemsForFeedRoute extends BaseFeedItemRoute {


    public UpdateAllFeedItemsForFeedRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        verifyUserLoggedIn(request, response);
        String feedId = getFeedId(request);
        List<FeedItem> feedItems = feedItemUpdater.markFeedItemsForFeedAsRead(feedId);
        return "{\"numberUpdated\":\"" + feedItems.size() + "\",\"feedId\":\"" + feedId + "\"}";
    }

}
