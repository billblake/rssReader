package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.FeedConstants;
import com.bill.rss.mongodb.FeedItemRetriever;
import com.bill.rss.mongodb.MongoDBConnection;
import com.bill.rss.mongodb.MongoFeedUpdater;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class FeedsRoute extends Route {
	
	private static final String CATEGORY_ID_PATH_VARIABLE = ":categoryId";
	private static final String FEED_ID_PATH_VARIABLE = ":feedId";

	private FeedItemProvider feedProvider;
	FeedUpdater feedUpdater;

	protected FeedsRoute(String path) {
		super(path);
		feedProvider = new FeedItemRetriever();
		feedUpdater = new MongoFeedUpdater();
	}

	@Override
	public Object handle(Request request, Response response) {
		response.type(ViewConstants.JSON_RESPONSE_TYPE);
		String categoryId = request.params(CATEGORY_ID_PATH_VARIABLE);
		String feedId = request.params(FEED_ID_PATH_VARIABLE);
		String refresh = request.queryParams("refresh");
		
		if (refresh != null && Boolean.parseBoolean(refresh)) {
			refreshFeeds();
		}
		
		List<FeedItem> feedItems = feedProvider.retrieveFeedItems(categoryId, feedId);
		return JsonUtils.convertObjectToJson(feedItems);
	}

	private void refreshFeeds() {
		feedUpdater.updateWithLatestFeeds();
		

		DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection coll = rssDb.getCollection(FeedConstants.FEED_ITEMS);
	}

}
