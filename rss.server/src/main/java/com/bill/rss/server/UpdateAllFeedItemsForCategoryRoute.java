package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class UpdateAllFeedItemsForCategoryRoute extends BaseFeedItemRoute {

    public UpdateAllFeedItemsForCategoryRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        verifyUserLoggedIn(request, response);
        String categoryId = getCategoryId(request);
        List<FeedItem> feedItems = feedItemUpdater.markFeedItemsForCategoryAsRead(categoryId);
        return "{\"numberUpdated\":\"" + feedItems.size() + "\",\"categoryId\":\"" + categoryId + "\"}";
    }

}
