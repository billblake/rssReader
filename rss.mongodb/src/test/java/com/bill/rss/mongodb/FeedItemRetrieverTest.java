package com.bill.rss.mongodb;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.bill.rss.domain.FeedItem;
import com.mongodb.DB;

import static org.junit.Assert.assertEquals;

public class FeedItemRetrieverTest {

    @Test
    public void testRetrieveFeedItems() {
        DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();

        List<FeedItem> feedItems = feedItemRetriever.retrieveFeedItems(ObjectId.get().toString(), ObjectId.get().toString(), "billblake", 1);
        FeedItem feedItem = feedItems.get(0);
        assertEquals("235", feedItem.getCatId());
        assertEquals("467", feedItem.getFeedId());
        assertEquals("654", feedItem.getFeedItemId());
        assertEquals("The Description", feedItem.getDescription());
        assertEquals("http://www.bbc.co.uk/sport/1234", feedItem.getLink());
        assertEquals("BBC Sport", feedItem.getSource());
        assertEquals("My Title", feedItem.getTitle());
        assertEquals("billblake", feedItem.getUsername());
        assertEquals(new Date(1408277117000L), feedItem.getPubDate());
    }


    @Test
    public void testRetrieveFeedItemsWithoutCatIdFeedId() {
        DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();

        List<FeedItem> feedItems = feedItemRetriever.retrieveFeedItems(null, null, "billblake", 1);
        FeedItem feedItem = feedItems.get(0);
        assertEquals("235", feedItem.getCatId());
        assertEquals("467", feedItem.getFeedId());
        assertEquals("654", feedItem.getFeedItemId());
        assertEquals("The Description", feedItem.getDescription());
        assertEquals("http://www.bbc.co.uk/sport/1234", feedItem.getLink());
        assertEquals("BBC Sport", feedItem.getSource());
        assertEquals("My Title", feedItem.getTitle());
        assertEquals("billblake", feedItem.getUsername());
        assertEquals(new Date(1408277117000L), feedItem.getPubDate());
    }
}
