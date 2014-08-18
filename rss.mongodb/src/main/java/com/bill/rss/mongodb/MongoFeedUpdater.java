package com.bill.rss.mongodb;

import java.util.List;

import org.bson.types.ObjectId;

import rss.feedfetcher.FeedFetcher;
import rss.httpclient.feedfetcher.HttpClientFeedFetcherTest;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import static com.bill.rss.mongodb.FeedConstants.CATEGORY_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEMS;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_DESCRIPTION;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_LINK;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_PUB_DATE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SOURCE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TITLE;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

public class MongoFeedUpdater implements FeedUpdater {

	private FeedFetcher feedFetcher = new HttpClientFeedFetcherTest();

	private FeedProvider feedRetriever = new FeedRetriever();

    public void updateWithLatestFeeds(String username) {
		List<Feed> feeds = feedRetriever.retrieveFeeds(username);
		for (Feed feed : feeds) {
			List<FeedItem> fetchedFeeds = feedFetcher.fetcherFeed(feed.getUrl());
			updateFeed(feed, fetchedFeeds, username);
		}
	}

	private void updateFeed(Feed feed, List<FeedItem> fetchedFeeds, String username) {
		DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection feedItemsCollection = rssDb.getCollection(FEED_ITEMS);

	    for (FeedItem fetchedFeed : fetchedFeeds) {
	    	BasicDBObject query = new BasicDBObject();
	    	query.append(FEED_ITEM_SOURCE, feed.getName());
	    	query.append(FEED_ITEM_LINK, fetchedFeed.getLink());
	    	query.append(USER_NAME, username);
	    	DBCursor queryResults = feedItemsCollection.find(query);
	    	if (!queryResults.hasNext()) {
		    	BasicDBObject feedItemDocument = new BasicDBObject();
		    	feedItemDocument.put(FEED_ITEM_SOURCE, feed.getName());
		    	feedItemDocument.put(FEED_ID, new ObjectId(feed.getFeedId()));
		    	feedItemDocument.put(CATEGORY_ID, new ObjectId(feed.getCategoryId()));
		    	feedItemDocument.put(USER_NAME, username);
		    	feedItemDocument.put(FEED_ITEM_TITLE, fetchedFeed.getTitle());
		    	feedItemDocument.put(FEED_ITEM_DESCRIPTION, fetchedFeed.getDescription());
		    	feedItemDocument.put(FEED_ITEM_PUB_DATE, fetchedFeed.getPubDate());
		    	feedItemDocument.put(FEED_ITEM_LINK, fetchedFeed.getLink());

		    	feedItemsCollection.insert(feedItemDocument);
	    	}
	    }
	}

	public void setFeedFetcher(FeedFetcher feedFetcher) {
	    this.feedFetcher = feedFetcher;
	}

	public void setFeedRetriever(FeedProvider feedRetriever) {
        this.feedRetriever = feedRetriever;
    }
}
