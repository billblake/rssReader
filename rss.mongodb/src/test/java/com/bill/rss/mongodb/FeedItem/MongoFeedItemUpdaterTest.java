package com.bill.rss.mongodb.FeedItem;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.MockUtils;
import com.mongodb.DB;

public class MongoFeedItemUpdaterTest {

	
	@Test
	public void testMarkFeedItemAsRead() {
		DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        
        MongoFeedItemUpdater feedItemUpdater = new MongoFeedItemUpdater();
		FeedItem feedItem = feedItemUpdater.markFeedItemAsRead("53bf159330044594e9be9f78", "bob");
		assertEquals(feedItem.getFeedItemId(), "52ffe096e81f7a9bb906b6f9");
		assertEquals(feedItem.isRead(), true);
	}

	
	@Test
	public void testMarkFeedItemsForFeedAsRead() {
		DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        
        MongoFeedItemUpdater feedItemUpdater = new MongoFeedItemUpdater();
		List<FeedItem> feedItem = feedItemUpdater.markFeedItemsForFeedAsRead("53bf159330044594e9be9f78", "bob");
		assertEquals(feedItem.get(0).isRead(), true);
	}

	
	@Test
	public void testMarkAllFeedItemsAsRead() {
		DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        
        MongoFeedItemUpdater feedItemUpdater = new MongoFeedItemUpdater();
		List<FeedItem> feedItem = feedItemUpdater.markAllFeedItemsAsRead("bob");
		assertEquals(feedItem.get(0).isRead(), true);
	}

	
	@Test
	public void testMarkFeedItemsForCategoryAsRead() {
		DB db = MockUtils.createDbMock();
        MockUtils.createFeedsItemsCollectionMock(db);
        
        MongoFeedItemUpdater feedItemUpdater = new MongoFeedItemUpdater();
		List<FeedItem> feedItem = feedItemUpdater.markFeedItemsForCategoryAsRead("53bf159330044594e9be9f78", "bob");
		assertEquals(feedItem.get(0).isRead(), true);
	}
}
