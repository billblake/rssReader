package com.bill.rss.server;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.mongodb.MongoFeedUpdater;

import static com.bill.rss.server.ViewConstants.CATEGORY_ID_PATH_VARIABLE;
import static com.bill.rss.server.ViewConstants.FEED_ID_PATH_VARIABLE;

public class DeleteFeedRoute extends BaseRoute {

    private final FeedUpdater feedUpdater = new MongoFeedUpdater();

    protected DeleteFeedRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        Feed feed = new Feed();
        feed.setCategoryId(request.params(CATEGORY_ID_PATH_VARIABLE));
        feed.setFeedId(request.params(FEED_ID_PATH_VARIABLE));
        feedUpdater.deleteFeed(feed);
        return JsonUtils.convertObjectToJson(feed);
    }
}
