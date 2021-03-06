package com.bill.rss.server;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.mongodb.MongoFeedUpdater;

public class SaveFeedRoute extends BaseRoute {

    private final ObjectMapper mapper = new ObjectMapper();
    private FeedUpdater feedUpdater = new MongoFeedUpdater();

    protected SaveFeedRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        Feed feed = extractFeedFromRequest(request);
        if (StringUtils.isBlank(feed.getFeedId())) {
            //create new Feed
            feed = feedUpdater.addFeed(feed);
        } else {
            //update existing feed
            feed = feedUpdater.saveFeed(feed);
        }
        return JsonUtils.convertObjectToJson(feed);
    }


    private Feed extractFeedFromRequest(Request request) {
        Feed feed = null;
        try {
            feed = mapper.readValue(request.body(), Feed.class);
            feed.setUserName(getUsername(request));
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }
        return feed;
    }


    void setFeedUpdater(FeedUpdater feedUpdater) {
        this.feedUpdater = feedUpdater;
    }
}
