package com.bill.rss.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FeedItemRetriever implements FeedItemProvider {

	

	public List<FeedItem> retrieveFeedItems(String categoryId, String feedId) {
		DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection coll = rssDb.getCollection(FeedConstants.FEED_ITEMS);
	    DBCursor feedItemsCursor;
	    
	    BasicDBObject query = buildFeedItemQuery(categoryId, feedId);
	    feedItemsCursor = coll.find(query).sort(new BasicDBObject("pubDate", -1));
	    return parseFeadItems(feedItemsCursor);
	}


	private List<FeedItem> parseFeadItems(DBCursor feedItemsCursor) {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
	    FeedItem feedItem;
	    while (feedItemsCursor.hasNext()) {
	    	try {
		    	DBObject nextFeedItem = feedItemsCursor.next();
		        feedItem = new FeedItem();
		        feedItem.setFeedItemId(nextFeedItem.get(FeedConstants.FEED_ITEM_ID).toString());
		        feedItem.setCatId(nextFeedItem.get(FeedConstants.CATEGORY_ID).toString());
		        feedItem.setDescription(nextFeedItem.get(FeedConstants.FEED_ITEM_DESCRIPTION).toString());
		        feedItem.setFeedId(nextFeedItem.get(FeedConstants.FEED_ID).toString());
		        feedItem.setLink(nextFeedItem.get(FeedConstants.FEED_ITEM_LINK).toString());
		        feedItem.setSource(nextFeedItem.get(FeedConstants.FEED_ITEM_SOURCE).toString());
		        feedItem.setTitle(nextFeedItem.get(FeedConstants.FEED_ITEM_TITLE).toString());
		        feedItem.setUsername(nextFeedItem.get(FeedConstants.USER_NAME).toString());
		        Date pubDate = (Date) nextFeedItem.get(FeedConstants.FEED_ITEM_PUB_DATE);
		        feedItem.setPubDate(pubDate);
		        feedItems.add(feedItem);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
		return feedItems;
	}


	private BasicDBObject buildFeedItemQuery(String categoryId, String feedId) {
		BasicDBObject query = new BasicDBObject();
	    if (categoryId != null) {
	    	query.append(FeedConstants.CATEGORY_ID, new ObjectId(categoryId));
	    } 
	    if (feedId != null) {
	    	query.append(FeedConstants.FEED_ID, new ObjectId(feedId));
	    }
		return query;
	}
}
