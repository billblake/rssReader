package com.bill.rss.server;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemUpdater;
import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.FeedItemRetriever;

public class FeedItemRoute extends BaseRoute {

    private final FeedItemUpdater feedItemUpdater = new FeedItemRetriever();

    protected FeedItemRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        FeedItem feedItem = new FeedItem();
        if (isMarkAsReadRequest(request)) {
            feedItem = markFeedItemAsRead(getFeedItemId(request));
        }
        return JsonUtils.convertObjectToJson(feedItem);
    }

    private String getFeedItemId(Request request) {
        return request.params("feedItemId");
    }

    private boolean isMarkAsReadRequest(Request request) {
        return Boolean.parseBoolean(request.queryParams("markAsRead"));
    }

    private FeedItem markFeedItemAsRead(String feedItemId) {
        return feedItemUpdater.markFeedItemAsRead(feedItemId);
    }

}