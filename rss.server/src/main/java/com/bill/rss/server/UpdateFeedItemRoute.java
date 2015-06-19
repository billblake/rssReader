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
        String userName = getUsername(request);
        if (isMarkAsReadRequest(request)) {
            feedItem = markFeedItemAsRead(getFeedItemId(request), userName);
        } else {
            feedItem = saveFeedItem(getFeedItemId(request), userName);
        }
        return JsonUtils.convertObjectToJson(feedItem);
    }


    private boolean isMarkAsReadRequest(Request request) {
        return Boolean.parseBoolean(request.queryParams("markAsRead"));
    }


    private FeedItem markFeedItemAsRead(String feedItemId, String userName) {
        return feedItemUpdater.markFeedItemAsRead(feedItemId, userName);
    }


    private FeedItem saveFeedItem(String feedItemId, String userName) {
        return feedItemUpdater.saveFeedItem(feedItemId, userName);
    }

}
