package com.bill.rss.mongodb;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;

import rss.feedfetcher.FeedFetcher;
import rss.httpclient.feedfetcher.HttpClientFeedFetcher;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MongoFeedUpdaterTest {


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
        return feedRetriever;
    }
}
