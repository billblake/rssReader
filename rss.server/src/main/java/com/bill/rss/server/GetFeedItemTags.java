package com.bill.rss.server;

import java.util.Map;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.mongodb.FeedItem.FeedItemRetriever;

import static com.bill.rss.server.ViewConstants.JSON_RESPONSE_TYPE;

public class GetFeedItemTags extends BaseFeedItemRoute {

    private FeedItemProvider feedItemProvider;

    private final String tagCountAsJson = "{\"text\": \"%s\", \"weight\": %s}";

    protected GetFeedItemTags(String path) {
        super(path);
        feedItemProvider = new FeedItemRetriever();
    }


    @Override
    public Object handle(Request request, Response response) {
        Map<String, Integer> tags = feedItemProvider.getTags(getUsername(request));
        StringBuilder tagCountListAsJson = new StringBuilder();
        tagCountListAsJson.append("[");
        for (String key : tags.keySet()) {
            tagCountListAsJson.append(String.format(tagCountAsJson, key, tags.get(key))).append(",");
        }
        tagCountListAsJson.deleteCharAt(tagCountListAsJson.length() - 1).append("]");
        response.type(JSON_RESPONSE_TYPE);
        return tagCountListAsJson.toString();
    }


    void setFeedProvider(FeedItemProvider feedItemProvider) {
        this.feedItemProvider = feedItemProvider;
    }
}
