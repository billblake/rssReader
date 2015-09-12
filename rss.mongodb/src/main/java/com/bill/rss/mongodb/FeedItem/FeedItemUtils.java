package com.bill.rss.mongodb.FeedItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.bill.rss.domain.FeedItem;
import com.bill.rss.mongodb.DateUtils;
import com.bill.rss.mongodb.MongoDBConnection;
import com.bill.rss.mongodb.MongoDbUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORY_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEMS;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_DESCRIPTION;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_LINK;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_OBJECT_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_PUB_DATE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_READ;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SAVED;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SOURCE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TAGS;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TITLE;
import static com.bill.rss.mongodb.FeedConstants.IMAGE_URL;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

public class FeedItemUtils {

    public static DBCollection getFeedItemsCollection() {
        DB rssDb = MongoDBConnection.getDbConnection();
        DBCollection feedItemCollection = rssDb.getCollection(FEED_ITEMS);
        return feedItemCollection;
    }


    public static DBObject retrieveFeedItemById(String feedItemId, String userName) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        BasicDBObject query = new BasicDBObject();
        query.append(FEED_ITEM_OBJECT_ID, new ObjectId(feedItemId))
            .append(USER_NAME, userName);
        DBObject feedItem = feedItemCollection.findOne(query);
        return feedItem;
    }


    public static FeedItem buildFeedItem(DBObject nextFeedItem) {
        FeedItem feedItem;
        feedItem = new FeedItem();
        feedItem.setFeedItemId(nextFeedItem.get(FEED_ITEM_OBJECT_ID).toString());
        feedItem.setCatId(nextFeedItem.get(CATEGORY_ID).toString());
        feedItem.setDescription(nextFeedItem.get(FEED_ITEM_DESCRIPTION).toString());
        feedItem.setFeedId(nextFeedItem.get(FEED_ID).toString());
        feedItem.setLink(nextFeedItem.get(FEED_ITEM_LINK).toString());
        Object imageUrl = nextFeedItem.get(IMAGE_URL);
        if (imageUrl != null) {
            feedItem.setImageUrl(imageUrl.toString());
        }
        feedItem.setSource(nextFeedItem.get(FEED_ITEM_SOURCE).toString());
        feedItem.setTitle(nextFeedItem.get(FEED_ITEM_TITLE).toString());
        feedItem.setUsername(nextFeedItem.get(USER_NAME).toString());
        feedItem.setRead(MongoDbUtils.getBooleanValue(nextFeedItem, FEED_ITEM_READ));
        feedItem.setSaved(MongoDbUtils.getBooleanValue(nextFeedItem, FEED_ITEM_SAVED));
        Date pubDate = (Date) nextFeedItem.get(FEED_ITEM_PUB_DATE);
        feedItem.setPubDate(pubDate);
        feedItem.setFormattedDate(DateUtils.formatDate(pubDate));

        Collection<String> tagsDB = (Collection<String>) nextFeedItem.get(FEED_ITEM_TAGS);
        if (tagsDB != null) {
            Set<String> tags = new HashSet(Arrays.asList(tagsDB.toArray()));
            feedItem.setTags(tags);
        }

        return feedItem;
    }


    public static List<FeedItem> parseFeadItems(DBCursor feedItemsCursor) {
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        while (feedItemsCursor.hasNext()) {
            try {
                DBObject nextFeedItem = feedItemsCursor.next();
                feedItems.add(buildFeedItem(nextFeedItem));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return feedItems;
    }
}
