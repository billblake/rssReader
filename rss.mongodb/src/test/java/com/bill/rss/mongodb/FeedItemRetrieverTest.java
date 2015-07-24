package com.bill.rss.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.FeedItem.FeedItemRetriever;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

public class FeedItemRetrieverTest {

    @Test
    public void testRetrieveFeedItems() {
        DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();

        FeedItem searchFeedItem = new FeedItem();
        searchFeedItem.setCatId(ObjectId.get().toString());
        searchFeedItem.setFeedId(ObjectId.get().toString());
        searchFeedItem.setUsername("billblake");
        List<FeedItem> feedItems = feedItemRetriever.retrieveFeedItems(searchFeedItem, 1);
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

        FeedItem searchFeedItem = new FeedItem();
        searchFeedItem.setUsername("billblake");
        List<FeedItem> feedItems = feedItemRetriever.retrieveFeedItems(searchFeedItem, 1);
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
    public void testEnrichCategoryWithFeedItemCount() {
    	DB db = MockUtils.createDbMock();
        DBCollection feedsItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
        when(feedsItemsCollection.count(Mockito.any(BasicDBObject.class))).thenReturn(10L, 8L);
        
        Category category = new Category();
        category.setUsername("bob");
        category.setCategoryId("53bf159330044594e9be9f78");
        
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();
		feedItemRetriever.enrichCategoryWithFeedItemCount(category);
		assertEquals("10", category.getTotalCount());
		assertEquals("8", category.getUnReadCount());
    }
    
    
    @Test
    public void testEnrichFeedWithFeedItemCount() {
    	DB db = MockUtils.createDbMock();
        DBCollection feedsItemsCollection = MockUtils.createFeedsItemsCollectionMock(db);
        when(feedsItemsCollection.count(Mockito.any(BasicDBObject.class))).thenReturn(20L, 16L);
        
        Feed feed = new Feed();
        feed.setUserName("bob");
        feed.setFeedId("53bf159330044594e9be9f78");
        
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();
		feedItemRetriever.enrichFeedWithFeedItemCount(feed);
		assertEquals("20", feed.getTotalCount());
		assertEquals("16", feed.getUnReadCount());
    }
    
    
    @Test
    public void testGetTags() {
    	DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();
        LinkedHashMap<String, Integer> tags = feedItemRetriever.getTags("bob");
        assertEquals(new Integer(5), tags.get("tag1"));
        assertEquals(new Integer(8), tags.get("tag2"));
    }
    
    
    @Test
    public void testGetTagsWithLimit() {
    	DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();
        LinkedHashMap<String, Integer> tags = feedItemRetriever.getTags("bob", 1);
        assertEquals(new Integer(5), tags.get("tag1"));
        assertNull(tags.get("tag2"));
    }
}
