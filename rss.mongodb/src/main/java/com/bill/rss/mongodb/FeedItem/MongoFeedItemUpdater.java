package com.bill.rss.mongodb.FeedItem;

import java.util.List;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.FeedItemUpdater;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORY_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_DELETE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_READ;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SAVED;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TAGS;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MongoFeedItemUpdater implements FeedItemUpdater {

    public FeedItem saveFeedItem(String feedItemId, String userName) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        DBObject feedItem = FeedItemUtils.retrieveFeedItemById(feedItemId, userName);
        feedItem.put(FEED_ITEM_SAVED, true);
        feedItemCollection.save(feedItem);
        return FeedItemUtils.buildFeedItem(feedItem);
    }


    public FeedItem markFeedItemAsRead(String feedItemId, String userName) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        DBObject feedItem = FeedItemUtils.retrieveFeedItemById(feedItemId, userName);
        feedItem.put(FEED_ITEM_READ, true);
        feedItemCollection.save(feedItem);
        return FeedItemUtils.buildFeedItem(feedItem);
    }


    public List<FeedItem> markFeedItemsForCategoryAsRead(String categoryId, String userName) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append(CATEGORY_ID, new ObjectId(categoryId))
            .append(FEED_ITEM_READ, FALSE)
            .append(USER_NAME, userName);
        List<FeedItem> feedItems = getFeedItems(searchQuery);
        markFeedsAsRead(searchQuery);
        return feedItems;
    }


    public List<FeedItem> markFeedItemsForFeedAsRead(String feedId, String userName) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append(FEED_ID, new ObjectId(feedId))
            .append(FEED_ITEM_READ, FALSE)
            .append(USER_NAME, userName);
        List<FeedItem> feedItems = getFeedItems(searchQuery);
        markFeedsAsRead(searchQuery);
        return feedItems;
    }


    public List<FeedItem> markAllFeedItemsAsRead(String userName) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append(FEED_ITEM_READ, FALSE)
            .append(USER_NAME, userName);
        List<FeedItem> feedItems = getFeedItems(searchQuery);
        markFeedsAsRead(searchQuery);
        return feedItems;
    }


    public FeedItem deleteFeedItem(String feedItemId, String userName) {
        DBObject feedItem = FeedItemUtils.retrieveFeedItemById(feedItemId, userName);
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        feedItem.put(FEED_ITEM_DELETE, true);
        feedItemCollection.save(feedItem);
        return FeedItemUtils.buildFeedItem(feedItem);
    }


    public List<FeedItem> deleteFeedItemsForCategory(String categoryId, String userName) {
        BasicDBObject searchQuery = buildBasicSearchQueryForDeleteFeedItems(userName);
        searchQuery.append(CATEGORY_ID, new ObjectId(categoryId));
        List<FeedItem> feedItems = getFeedItems(searchQuery);
        deleteFeedItems(searchQuery);
        return feedItems;
    }


    public List<FeedItem> deleteFeedItemsForFeed(String feedId, String userName) {
        BasicDBObject searchQuery = buildBasicSearchQueryForDeleteFeedItems(userName);
        searchQuery.append(FEED_ID, new ObjectId(feedId));
        List<FeedItem> feedItems = getFeedItems(searchQuery);
        deleteFeedItems(searchQuery);
        return feedItems;
    }


    public List<FeedItem> deleteAllFeedItems(String userName) {
        BasicDBObject searchQuery = buildBasicSearchQueryForDeleteFeedItems(userName);
        List<FeedItem> feedItems = getFeedItems(searchQuery);
        deleteFeedItems(searchQuery);
        return feedItems;
    }

    private BasicDBObject buildBasicSearchQueryForDeleteFeedItems(String userName) {
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.append(FEED_ITEM_SAVED, new BasicDBObject("$ne", true))
            .append(FEED_ITEM_DELETE, new BasicDBObject("$ne", true))
            .append(USER_NAME, userName);
        return searchQuery;
    }

    private List<FeedItem> getFeedItems(BasicDBObject searchQuery) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        DBCursor feedItemsCursor = feedItemCollection.find(searchQuery);
        return FeedItemUtils.parseFeadItems(feedItemsCursor);
    }

    private void markFeedsAsRead(BasicDBObject searchQuery) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$set", new BasicDBObject().append(FEED_ITEM_READ, TRUE));
        feedItemCollection.updateMulti(searchQuery, updateQuery);
    }



    private void deleteFeedItems(BasicDBObject searchQuery) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$set", new BasicDBObject().append(FEED_ITEM_DELETE, TRUE));
        feedItemCollection.updateMulti(searchQuery, updateQuery);
    }


    public FeedItem saveFeedItemNew(FeedItem feedItem) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        DBObject feedItemDB = FeedItemUtils.retrieveFeedItemById(feedItem.getFeedItemId(), feedItem.getUsername());
        BasicDBList tags = new BasicDBList();
        tags.addAll(feedItem.getTags());
        feedItemDB.put(FEED_ITEM_READ, feedItem.isRead());
        feedItemDB.put(FEED_ITEM_SAVED, feedItem.isSaved());
        feedItemDB.put(FEED_ITEM_TAGS, feedItem.getTags());
        feedItemCollection.save(feedItemDB);
        return FeedItemUtils.buildFeedItem(feedItemDB);
    }
}
