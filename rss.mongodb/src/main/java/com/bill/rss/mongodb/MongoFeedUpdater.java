package com.bill.rss.mongodb;

import java.util.List;

import org.bson.types.ObjectId;

import rss.feedfetcher.FeedFetcher;
import rss.httpclient.feedfetcher.HttpClientFeedFetcher;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORY_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_IDS;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEMS;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_DESCRIPTION;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_LINK;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_PUB_DATE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SOURCE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TITLE;
import static com.bill.rss.mongodb.FeedConstants.FEED_NAME;
import static com.bill.rss.mongodb.FeedConstants.FEED_URL;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

public class MongoFeedUpdater implements FeedUpdater {

	private FeedFetcher feedFetcher = new HttpClientFeedFetcher();

	private FeedProvider feedRetriever = new FeedRetriever();


	public void updateWithLatestFeeds() {
        List<Feed> feeds = feedRetriever.retrieveAllFeeds();
        for (Feed feed : feeds) {
            List<FeedItem> fetchedFeeds = feedFetcher.fetchFeed(feed.getUrl());
            updateFeed(feed, fetchedFeeds);
        }
    }


	public void updateWithLatestFeeds(String username) {
		List<Feed> feeds = feedRetriever.retrieveFeeds(username);
		for (Feed feed : feeds) {
			List<FeedItem> fetchedFeeds = feedFetcher.fetchFeed(feed.getUrl());
			updateFeed(feed, fetchedFeeds);
		}
	}

	private void updateFeed(Feed feed, List<FeedItem> fetchedFeeds) {
		DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection feedItemsCollection = rssDb.getCollection(FEED_ITEMS);

	    for (FeedItem fetchedFeed : fetchedFeeds) {
	    	BasicDBObject query = new BasicDBObject();
	    	query.append(FEED_ITEM_SOURCE, feed.getName());
	    	query.append(FEED_ITEM_LINK, fetchedFeed.getLink());
    	    query.append(USER_NAME, feed.getUserName());

	    	DBCursor queryResults = feedItemsCollection.find(query);
	    	if (!queryResults.hasNext()) {
		    	BasicDBObject feedItemDocument = new BasicDBObject();
		    	feedItemDocument.put(FEED_ITEM_SOURCE, feed.getName());
		    	feedItemDocument.put(FEED_ID, new ObjectId(feed.getFeedId()));
		    	feedItemDocument.put(CATEGORY_ID, new ObjectId(feed.getCategoryId()));
		    	feedItemDocument.put(USER_NAME, feed.getUserName());
		    	feedItemDocument.put(FEED_ITEM_TITLE, fetchedFeed.getTitle());
		    	feedItemDocument.put(FEED_ITEM_DESCRIPTION, fetchedFeed.getDescription());
		    	feedItemDocument.put(FEED_ITEM_PUB_DATE, fetchedFeed.getPubDate());
		    	feedItemDocument.put(FEED_ITEM_LINK, fetchedFeed.getLink());

		    	feedItemsCollection.insert(feedItemDocument);
	    	}
	    }
	}


    public Feed saveFeed(Feed feed) {
        // TODO Auto-generated method stub
        throw new RuntimeException("not implemented");
    }


    public Feed addFeed(Feed feed) {
        DB dbConnection = MongoDBConnection.getDbConnection();
        DBCollection feedCollection = dbConnection.getCollection("feeds");
        DBCollection categoriesCollection = dbConnection.getCollection("categories");

        BasicDBObject feedDocument = new BasicDBObject();
        feedDocument.put(USER_NAME, feed.getUserName());
        feedDocument.put(FEED_NAME, feed.getName());
        feedDocument.put(FEED_URL, feed.getUrl());
        feedDocument.put(CATEGORY_ID, new ObjectId(feed.getCategoryId()));

        feedCollection.save(feedDocument);
        ObjectId feedId = feedDocument.getObjectId("_id");

        DBObject categoryQuery = new BasicDBObject();
        categoryQuery.put("_id", new ObjectId(feed.getCategoryId()));

        DBObject category = categoriesCollection.findOne(categoryQuery);
        BasicDBList feedIds = (BasicDBList) category.get(FEED_IDS);
        feedIds.add(feedId);
        category.put(FEED_IDS, feedIds);
        categoriesCollection.save(category);

        feed.setFeedId(feedId.toString());
        return feed;
    }


	public void setFeedFetcher(FeedFetcher feedFetcher) {
	    this.feedFetcher = feedFetcher;
	}


	public void setFeedRetriever(FeedProvider feedRetriever) {
        this.feedRetriever = feedRetriever;
    }
}
