package com.bill.rss.server;

import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.FeedItem;

public class SaveFeedItemRoute extends BaseFeedItemRoute {

    private final ObjectMapper mapper = new ObjectMapper();

    protected SaveFeedItemRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        FeedItem feedItem = buildFeedItemFromRequest(request);
        feedItem = feedItemUpdater.saveFeedItem(feedItem);
        return JsonUtils.convertObjectToJson(feedItem);
    }


    private FeedItem buildFeedItemFromRequest(Request request) {
        FeedItem feedItem = null;
        try {
            feedItem = mapper.readValue(request.body(), FeedItem.class);
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }
        return feedItem;
    }

}
