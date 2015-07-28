package com.bill.rss.mongodb;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mockito.Mockito;

import rss.feedfetcher.FeedFetcher;
import rss.httpclient.feedfetcher.HttpClientFeedFetcher;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.FEED_ITEMS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongoFeedUpdaterTest {

	@Test
	public void testUpdateWithLatestFeeds() {
		DB db = MockUtils.createDbMock();
		MongoFeedUpdater feedUpdater = new MongoFeedUpdater();
		feedUpdater.setFeedFetcher(createFeedFetcherMock());
        feedUpdater.setFeedRetriever(createFeedRetrieverMock());
        
		DBCursor feedItemsCursor = mock(DBCursor.class);
		when(feedItemsCursor.hasNext()).thenReturn(false);

		DBCollection feedItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
		when(feedItemsCollection.find(any(BasicDBObject.class))).thenReturn(feedItemsCursor);
        
        feedUpdater.updateWithLatestFeeds();
        verify(feedItemsCollection, times(1)).insert(any(BasicDBObject.class));
	}

    @Test
    public void updateWithLatestFeeds() {
        DB db = MockUtils.createDbMock();
        DBCollection feedsCollection = MockUtils.createFeedsCollectionMock(db);
        DBCollection feedItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
        DBCursor feedItemsQueryResults = mock(DBCursor.class);
        when(feedItemsQueryResults.hasNext()).thenReturn(false);
        when(feedItemsCollection.find(any(BasicDBObject.class))).thenReturn(feedItemsQueryResults);

        DBCursor feedsCursor = mock(DBCursor.class);
        when(feedsCollection.find(any(DBObject.class))).thenReturn(feedsCursor);

        MongoFeedUpdater feedUpdater = new MongoFeedUpdater();
        feedUpdater.setFeedFetcher(createFeedFetcherMock());
        feedUpdater.setFeedRetriever(createFeedRetrieverMock());

        feedUpdater.updateWithLatestFeeds("billblake");
        verify(feedItemsCollection, times(1)).insert(any(DBObject.class));
    }


    @Test
    public void updateWithLatestFeedsNothingToUpdate() {
        DB db = MockUtils.createDbMock();
        DBCollection feedsCollection = MockUtils.createFeedsCollectionMock(db);
        DBCollection feedItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
        DBCursor feedItemsQueryResults = mock(DBCursor.class);
        when(feedItemsQueryResults.hasNext()).thenReturn(true);
        when(feedItemsCollection.find(any(BasicDBObject.class))).thenReturn(feedItemsQueryResults);

        DBCursor feedsCursor = mock(DBCursor.class);
        when(feedsCollection.find(any(DBObject.class))).thenReturn(feedsCursor);

        MongoFeedUpdater feedUpdater = new MongoFeedUpdater();
        feedUpdater.setFeedFetcher(createFeedFetcherMock());
        feedUpdater.setFeedRetriever(createFeedRetrieverMock());

        feedUpdater.updateWithLatestFeeds("billblake");
        verify(feedItemsCollection, times(0)).insert(any(DBObject.class));
    }

    
    @Test
    public void testSaveFeed() {
		DB db = MockUtils.createDbMock();
		DBCollection feedsCollection = MockUtils.createFeedsCollectionMock(db);
		DBObject feedDbObject = new BasicDBObject();
		when(feedsCollection.findOne(any(BasicDBObject.class))).thenReturn(feedDbObject );
		
		DBCollection feedItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
		
		DBCollection categoriesCollection = MockUtils.createCategoriesCollectionMock(db);
		DBObject categoryDbObject = new BasicDBObject();
		BasicDBList feedIds = new BasicDBList();
		feedIds.add("123");
		feedIds.add("456");
		categoryDbObject.put("feedIds", feedIds);
		when(categoriesCollection.findOne(any(DBObject.class))).thenReturn(categoryDbObject);
		
    	Feed feed = new Feed();
    	feed.setFeedId("53bf159330044594e9be9f78");
    	feed.setCategoryId("53bf159330044594e9be9f11");
    	
    	MongoFeedUpdater feedUpdater = new MongoFeedUpdater();
		feedUpdater.saveFeed(feed);
		
		verify(feedsCollection, times(1)).save(any(DBObject.class));
		verify(feedItemsCollection, times(1)).updateMulti(any(DBObject.class), any(DBObject.class));
		verify(categoriesCollection, times(2)).save(any(DBObject.class));
    }
    

    @Test
    public void testDeleteFeed() {
    	DB db = MockUtils.createDbMock();
    	DBCollection feedsCollection = MockUtils.createFeedsCollectionMock(db);
    	DBCollection categoriesCollection = MockUtils.createCategoriesCollectionMock(db);
    	DBCollection feedItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
    	
    	DBObject categoryDbObject = new BasicDBObject();
		BasicDBList feedIds = new BasicDBList();
		feedIds.add("123");
		feedIds.add("456");
		categoryDbObject.put("feedIds", feedIds);
		when(categoriesCollection.findOne(any(DBObject.class))).thenReturn(categoryDbObject);
    	
    	Feed feed = new Feed();
    	feed.setFeedId("53bf159330044594e9be9f78");
    	feed.setCategoryId("53bf159330044594e9be9f11");
    	
    	MongoFeedUpdater feedUpdater = new MongoFeedUpdater();
		feedUpdater.deleteFeed(feed);
		verify(feedsCollection, times(1)).remove(any(DBObject.class));
		verify(feedItemsCollection, times(1)).remove(any(DBObject.class));
		verify(categoriesCollection, times(1)).save(any(DBObject.class));
    }

    private FeedFetcher createFeedFetcherMock() {
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        FeedItem feedItem = new FeedItem();
        feedItems.add(feedItem);
        FeedFetcher feedFetcher = mock(HttpClientFeedFetcher.class);
        when(feedFetcher.fetchFeed(any(String.class))).thenReturn(feedItems);
        return feedFetcher;
    }


    private FeedProvider createFeedRetrieverMock() {
        List<Feed> feeds = new ArrayList<Feed>();
        Feed feed = new Feed();
        feed.setCategoryId(ObjectId.get().toString());
        feed.setFeedId(ObjectId.get().toString());
        feeds.add(feed);
        FeedProvider feedRetriever = mock(FeedProvider.class);
        when(feedRetriever.retrieveFeeds(any(String.class))).thenReturn(feeds);
        when(feedRetriever.retrieveAllFeeds()).thenReturn(feeds);
        
        return feedRetriever;
    }
}
