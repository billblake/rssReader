package com.bill.rss.mongodb;

import java.util.List;

import org.bson.types.ObjectId;

import rss.feedfetcher.FeedFetcher;
import rss.httpclient.feedfetcher.HttpClientFeedFetcher;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class MongoFeedUpdater implements FeedUpdater {

	private FeedFetcher feedFetcher = new HttpClientFeedFetcher();
	private FeedProvider feedRetriever = new FeedRetriever();

	public void updateWithLatestFeeds() {
		List<Feed> feeds = feedRetriever.retrieveFeeds();
		for (Feed feed : feeds) {
			List<FeedItem> fetchedFeeds = feedFetcher.fetcherFeed(feed.getUrl());
			updateFeed(feed, fetchedFeeds);
		}
	}

	private void updateFeed(Feed feed, List<FeedItem> fetchedFeeds) {
		DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection feedItemsCollection = rssDb.getCollection(FeedConstants.FEED_ITEMS);

	    for (FeedItem fetchedFeed : fetchedFeeds) {
	    	BasicDBObject query = new BasicDBObject();
	    	query.append(FeedConstants.FEED_ITEM_SOURCE, feed.getName());
	    	query.append(FeedConstants.FEED_ITEM_LINK, fetchedFeed.getLink());
	    	DBCursor queryResults = feedItemsCollection.find(query);
	    	if (!queryResults.hasNext()) {
		    	BasicDBObject feedItemDocument = new BasicDBObject();
		    	feedItemDocument.put("source", feed.getName());
		    	feedItemDocument.put("feedId", new ObjectId(feed.getFeedId()));
		    	feedItemDocument.put("categoryId", new ObjectId(feed.getCategoryId()));
		    	feedItemDocument.put("userName", "billblake01@yahoo.ie");
		    	feedItemDocument.put("title", fetchedFeed.getTitle());
		    	feedItemDocument.put("description", fetchedFeed.getDescription());
		    	feedItemDocument.put("pubDate", fetchedFeed.getPubDate());
		    	feedItemDocument.put("link", fetchedFeed.getLink());

		    	feedItemsCollection.insert(feedItemDocument);
	    	}
	    }
	}
}
