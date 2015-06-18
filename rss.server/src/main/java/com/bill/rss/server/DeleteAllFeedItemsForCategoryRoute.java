package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class DeleteAllFeedItemsForCategoryRoute extends BaseFeedItemRoute {

    public DeleteAllFeedItemsForCategoryRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        verifyUserLoggedIn(request, response);
        String categoryId = getCategoryId(request);
        List<FeedItem> feedItems = feedItemUpdater.deleteFeedItemsForCategory(categoryId);
        return "{\"numberUpdated\":\"" + feedItems.size() + "\",\"categoryId\":\"" + categoryId + "\"}";
    }

}
