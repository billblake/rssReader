package com.bill.rss.mongodb;

import java.util.List;

import org.junit.Test;

import com.bill.rss.domain.Feed;
import com.mongodb.DB;

import static org.junit.Assert.assertEquals;

public class FeedRetrieverTest {


    @Test
    public void retrieveFeeds() {
        DB db = MockUtils.createDbMock();
        MockUtils.createFeedsCollectionMock(db);

        FeedRetriever feedRetriever = new FeedRetriever();
        List<Feed> retrieveFeeds = feedRetriever.retrieveFeeds("billblake");
        Feed feed = retrieveFeeds.get(0);
        assertEquals("521", feed.getCategoryId());
        assertEquals("12345", feed.getFeedId());
        assertEquals("BBC Sport", feed.getName());
        assertEquals("http://www.bbc.co.uk", feed.getUrl());
        assertEquals("billblake", feed.getUserName());
    }


    @Test(expected = RuntimeException.class)
    public void retrieveFeed() {
        FeedRetriever feedRetriever = new FeedRetriever();
        feedRetriever.retrieveFeed("1", "2");
    }

}
