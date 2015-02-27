package com.bill.rss.mongodb;

import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.domain.Feed;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class FeedRetriever implements FeedProvider {

	private static final String FEED_ID = "_id";

	public List<Feed> retrieveFeeds(String username) {
		QueryBuilder builder = new QueryBuilder();
		builder.is(new BasicDBObject(USER_NAME, username));
		return executeQueryAndParseResults(builder);
	}

	public Feed retrieveFeed(String feedId, String username) {
		throw new RuntimeException("Not Implemented");
	}

	public List<Feed> retrieveFeedsIn(List<String> feedIds, String username) {
		QueryBuilder builder = new QueryBuilder();
		List<ObjectId> dbFeedIds = new ArrayList<ObjectId>();
		builder.and(new BasicDBObject(USER_NAME, username));

		for (String feedId : feedIds) {
			dbFeedIds.add(new ObjectId(feedId));
		}

		builder.put(FEED_ID).in(dbFeedIds);
		return executeQueryAndParseResults(builder);
	}


    public List<Feed> retrieveAllFeeds() {
        return executeQueryAndParseResults(new QueryBuilder());
    }


	private List<Feed> executeQueryAndParseResults(QueryBuilder builder) {
		List<Feed> feeds = new ArrayList<Feed>();
		DB rssDb = MongoDBConnection.getDbConnection();
		DBCollection feedsCollection = rssDb.getCollection(FeedConstants.FEEDS_COLLECTION);
		DBCursor feedsCursor = feedsCollection.find(builder.get());
		while (feedsCursor.hasNext()) {
			DBObject nextFeed = feedsCursor.next();
			feeds.add(createFeed(nextFeed));
		}
		return feeds;
	}


	private Feed createFeed(DBObject nextFeed) {
		Feed feed = new Feed();
		feed.setFeedId(nextFeed.get(FEED_ID).toString());
		feed.setName((String) nextFeed.get(FeedConstants.FEED_NAME));
		feed.setUrl((String) nextFeed.get(FeedConstants.FEED_URL));
		feed.setUserName((String) nextFeed.get(FeedConstants.FEED_USERNAME));
		feed.setCategoryId(nextFeed.get(FeedConstants.CATEGORY_ID).toString());
		return feed;
	}
}
