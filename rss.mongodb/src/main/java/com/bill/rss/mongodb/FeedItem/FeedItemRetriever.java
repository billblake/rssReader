package com.bill.rss.mongodb.FeedItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORY_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_DELETE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_PUB_DATE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_READ;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SAVED;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TAGS;
import static com.bill.rss.mongodb.FeedConstants.MAX_PAGE_SIZE;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

public class FeedItemRetriever implements FeedItemProvider {


    public List<FeedItem> retrieveFeedItems(FeedItem feedItem, int page) {
        DBCollection coll = FeedItemUtils.getFeedItemsCollection();
        BasicDBObject query = buildFeedItemQuery(feedItem);
        DBCursor feedItemsCursor = coll.find(query).sort(new BasicDBObject(FEED_ITEM_PUB_DATE, -1)).limit(MAX_PAGE_SIZE).skip((page - 1) * MAX_PAGE_SIZE);
        return FeedItemUtils.parseFeadItems(feedItemsCursor);
    }


    public void enrichCategoryWithFeedItemCount(Category category) {
        category.setTotalCount(getTotalCount(CATEGORY_ID, category.getCategoryId(), category.getUsername()));
        category.setUnReadCount(getUnRead(CATEGORY_ID, category.getCategoryId(), category.getUsername()));
    }


    public void enrichFeedWithFeedItemCount(Feed feed) {
        feed.setTotalCount(getTotalCount(FEED_ID, feed.getFeedId(), feed.getUserName()));
        feed.setUnReadCount(getUnRead(FEED_ID, feed.getFeedId(), feed.getUserName()));
    }


    public LinkedHashMap<String, Integer> getTags(String username) {
        return getTags(username, 0);
    }


    public LinkedHashMap<String, Integer> getTags(String username, int numberOfTags) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        AggregationOutput aggregate = feedItemCollection.aggregate(buildGetUsersFeedItemsQuery(username),
                buildUnwindTagsOperation(), buildCountTagsOperation(), buildSortByCountOperation());

        LinkedHashMap<String, Integer> tagCounts = new LinkedHashMap<String, Integer>();
        int numberOfTagsProcessed = 0;
        for (DBObject t : aggregate.results()) {
            String tag = (String) t.get("_id");
            Integer tagCount = (Integer) t.get("count");
            tagCounts.put(tag, tagCount);
            numberOfTagsProcessed++;
            if (numberOfTagsProcessed == numberOfTags) {
                break;
            }
        }
        return tagCounts;
    }


    private BasicDBObject buildGetUsersFeedItemsQuery(String username) {
        BasicDBObject getUsersFeedItems = new BasicDBObject();
        BasicDBObject query = new BasicDBObject(USER_NAME, username);

        BasicDBObject deleteValue = new BasicDBObject();
        deleteValue.append("$ne", true);
        query.append(FEED_ITEM_DELETE, deleteValue);

        getUsersFeedItems.append("$match", query);
        return getUsersFeedItems;
    }


    private BasicDBObject buildUnwindTagsOperation() {
        BasicDBObject unwindTags = new BasicDBObject();
        unwindTags.put("$unwind", "$tags");
        return unwindTags;
    }


    private BasicDBObject buildCountTagsOperation() {
        BasicDBObject countTags = new BasicDBObject();
        BasicDBObject groupQuery = new BasicDBObject();
        groupQuery.append("_id", "$tags").append("count", new BasicDBObject("$sum", 1));
        countTags.append("$group", groupQuery);
        return countTags;
    }


    private BasicDBObject buildSortByCountOperation() {
        BasicDBObject sortTagCounts = new BasicDBObject();
        sortTagCounts.append("$sort", new BasicDBObject("count", -1));
        return sortTagCounts;
    }


    private BasicDBObject buildFeedItemQuery(FeedItem feedItem) {
        BasicDBObject query = new BasicDBObject();
        query.append(USER_NAME, feedItem.getUsername());
        if (feedItem.getCatId() != null) {
            query.append(CATEGORY_ID, new ObjectId(feedItem.getCatId()));
        }
        if (feedItem.getFeedId() != null) {
            query.append(FEED_ID, new ObjectId(feedItem.getFeedId()));
        }
        if (feedItem.isSaved()) {
            query.append(FEED_ITEM_SAVED, true);
        }
        Set<String> tags = feedItem.getTags();
        if (tags != null && !tags.isEmpty()) {
            query.append(FEED_ITEM_TAGS, tags.toArray()[0]);
        }


        BasicDBObject deleteValue = new BasicDBObject();
        deleteValue.append("$ne", true);
        query.append(FEED_ITEM_DELETE, deleteValue);
        return query;
    }


    private String getTotalCount(String field, String fieldValue, String userName) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        BasicDBObject query = buildQueryForFeeds(field, fieldValue, userName);
        Long countOfFeedItems = feedItemCollection.count(query);
        return countOfFeedItems.toString();
    }


    private String getUnRead(String field, String fieldValue, String userName) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        BasicDBObject query = buildQueryForFeeds(field, fieldValue, userName);
        query.append(FEED_ITEM_READ, false);
        Long countOfUnReadFeedItems = feedItemCollection.count(query);
        return countOfUnReadFeedItems.toString();
    }


    private BasicDBObject buildQueryForFeeds(String field, String fieldValue, String userName) {
        BasicDBObject query = new BasicDBObject();
        query.append(field, new ObjectId(fieldValue));
        BasicDBObject deleteValue = new BasicDBObject();
        deleteValue.append("$ne", true);
        query.append(FEED_ITEM_DELETE, deleteValue).append(USER_NAME, userName);
        return query;
    }
}
