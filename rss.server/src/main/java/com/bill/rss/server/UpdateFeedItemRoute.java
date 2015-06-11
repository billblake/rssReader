package com.bill.rss.server;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class UpdateFeedItemRoute extends BaseFeedItemRoute {


    protected UpdateFeedItemRoute(String path) {
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

    private boolean isMarkAsReadRequest(Request request) {
        return Boolean.parseBoolean(request.queryParams("markAsRead"));
    }

    private FeedItem markFeedItemAsRead(String feedItemId) {
        return feedItemUpdater.markFeedItemAsRead(feedItemId);
    }

}
