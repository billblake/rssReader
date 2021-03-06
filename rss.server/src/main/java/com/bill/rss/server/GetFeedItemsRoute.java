package com.bill.rss.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.FeedRetriever;
import com.bill.rss.mongodb.MongoFeedUpdater;
import com.bill.rss.mongodb.FeedItem.FeedItemRetriever;

import static com.bill.rss.server.ViewConstants.CATEGORY_ID_PATH_VARIABLE;
import static com.bill.rss.server.ViewConstants.FEED_ID_PATH_VARIABLE;
import static com.bill.rss.server.ViewConstants.JSON_RESPONSE_TYPE;
import static com.bill.rss.server.ViewConstants.REFRESH_QUERY_PARAM;
import static com.bill.rss.server.ViewConstants.SAVED_QUERY_PARAM;
import static com.bill.rss.server.ViewConstants.TAG_QUERY_PARAM;

public class GetFeedItemsRoute extends BaseRoute {

    private FeedItemProvider feedItemProvider;
    private FeedUpdater feedUpdater;
    private FeedProvider feedProvider;

    protected GetFeedItemsRoute(String path) {
        super(path);
        feedItemProvider = new FeedItemRetriever();
        feedUpdater = new MongoFeedUpdater();
        feedProvider = new FeedRetriever();
    }


    @Override
    public Object handle(Request request, Response response) {
        verifyUserLoggedIn(request, response);
        response.type(JSON_RESPONSE_TYPE);

        refreshFeedsIfSpecified(request);

        FeedItem searchFeedItem = new FeedItem();
        searchFeedItem.setCatId(request.params(CATEGORY_ID_PATH_VARIABLE));
        searchFeedItem.setFeedId(request.params(FEED_ID_PATH_VARIABLE));
        searchFeedItem.setSaved(Boolean.parseBoolean(request.queryParams(SAVED_QUERY_PARAM)));
        searchFeedItem.setUsername(getUsername(request));
        searchFeedItem.setTags(getTags(request));
        List<FeedItem> feedItems = feedItemProvider.retrieveFeedItems(searchFeedItem, getPage(request));
        enrichFeedItemsWithFeedImage(feedItems);
        return JsonUtils.convertObjectToJson(feedItems);
    }


    private HashSet<String> getTags(Request request) {
        HashSet<String> tags = new HashSet<String>();
        if (request.queryParams(TAG_QUERY_PARAM) != null) {
            tags.add(request.queryParams(TAG_QUERY_PARAM));
        }
        return tags;
    }


    private int getPage(Request request) {
        try {
            return Integer.parseInt(request.queryParams("page"));
        } catch(Exception ex) {
            return 1;
        }
    }


    private void refreshFeedsIfSpecified(Request request) {
        String refresh = request.queryParams(REFRESH_QUERY_PARAM);
        if (refresh != null && Boolean.parseBoolean(refresh)) {
            feedUpdater.updateWithLatestFeeds(getUsername(request));
        }
    }


    public void setFeedItemProvider(FeedItemProvider feedItemProvider) {
        this.feedItemProvider = feedItemProvider;
    }

    public void setFeedUpdater(FeedUpdater feedUpdater) {
        this.feedUpdater = feedUpdater;
    }

    public void setFeedProvider(FeedProvider feedProvider) {
        this.feedProvider = feedProvider;
    }


    private void enrichFeedItemsWithFeedImage(List<FeedItem> feedItems) {
        List<Feed> feeds = feedProvider.retrieveAllFeeds();
        Map<String, String> feedIdFeedImages = new HashMap<String, String>();
        for (Feed feed : feeds) {
            if (feed.getImageUrl() != null && !feed.getImageUrl().equals("")) {
                feedIdFeedImages.put(feed.getFeedId(), feed.getImageUrl());
            }
        }

        for (FeedItem feedItem : feedItems) {
            if (feedItem.getImageUrl() == null || feedItem.getImageUrl().equals("")) {
                feedItem.setImageUrl(feedIdFeedImages.get(feedItem.getFeedId()));
            }
        }

    }

}
