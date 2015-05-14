package com.bill.rss.mongodb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.domain.FeedItem;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORY_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEMS;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_DESCRIPTION;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_OBJECT_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_LINK;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_PUB_DATE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_SOURCE;
import static com.bill.rss.mongodb.FeedConstants.FEED_ITEM_TITLE;
import static com.bill.rss.mongodb.FeedConstants.MAX_PAGE_SIZE;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class FeedItemRetriever implements FeedItemProvider {

    public List<FeedItem> retrieveFeedItems(String categoryId, String feedId, String username, int page) {
        DB rssDb = MongoDBConnection.getDbConnection();
        DBCollection coll = rssDb.getCollection(FEED_ITEMS);
        DBCursor feedItemsCursor;

        BasicDBObject query = buildFeedItemQuery(categoryId, feedId, username);
        feedItemsCursor = coll.find(query).sort(new BasicDBObject(FEED_ITEM_PUB_DATE, -1)).limit(MAX_PAGE_SIZE).skip((page - 1) * MAX_PAGE_SIZE);
        return parseFeadItems(feedItemsCursor);
    }

    private List<FeedItem> parseFeadItems(DBCursor feedItemsCursor) {
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        FeedItem feedItem;
        while (feedItemsCursor.hasNext()) {
            try {
                DBObject nextFeedItem = feedItemsCursor.next();
                feedItem = new FeedItem();
                feedItem.setFeedItemId(nextFeedItem.get(FEED_ITEM_OBJECT_ID).toString());
                feedItem.setCatId(nextFeedItem.get(CATEGORY_ID).toString());
                feedItem.setDescription(nextFeedItem.get(FEED_ITEM_DESCRIPTION).toString());
                feedItem.setFeedId(nextFeedItem.get(FEED_ID).toString());
                feedItem.setLink(nextFeedItem.get(FEED_ITEM_LINK).toString());
                feedItem.setSource(nextFeedItem.get(FEED_ITEM_SOURCE).toString());
                feedItem.setTitle(nextFeedItem.get(FEED_ITEM_TITLE).toString());
                feedItem.setUsername(nextFeedItem.get(USER_NAME).toString());
                Date pubDate = (Date) nextFeedItem.get(FEED_ITEM_PUB_DATE);
                feedItem.setPubDate(pubDate);
                feedItem.setFormattedDate(formatDate(pubDate));
                feedItems.add(feedItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return feedItems;
    }


    private BasicDBObject buildFeedItemQuery(String categoryId, String feedId, String username) {
        BasicDBObject query = new BasicDBObject();
        query.append(USER_NAME, username);
        if (categoryId != null) {
            query.append(CATEGORY_ID, new ObjectId(categoryId));
        }
        if (feedId != null) {
            query.append(FEED_ID, new ObjectId(feedId));
        }
        return query;
    }


    private String formatDate(Date pubDate) {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
        Calendar pubDateCalender = Calendar.getInstance();
        pubDateCalender.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
        pubDateCalender.setTime(pubDate);

        if (nowCalendar.get(DATE) == pubDateCalender.get(DATE) &&
            nowCalendar.get(MONTH) == pubDateCalender.get(MONTH) &&
            nowCalendar.get(YEAR) == pubDateCalender.get(YEAR)) {

            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
            timeFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Dublin"));
            return timeFormatter.format(pubDate);
        } else {
            SimpleDateFormat time = new SimpleDateFormat("MMM dd");
            return time.format(pubDate);
        }
    }
}
