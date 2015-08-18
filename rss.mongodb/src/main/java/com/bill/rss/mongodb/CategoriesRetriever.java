package com.bill.rss.mongodb;

import java.util.ArrayList;
import java.util.List;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.mongodb.FeedItem.FeedItemRetriever;
import com.bill.rss.mongodb.FeedItem.FeedItemUtils;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORIES_COLLECTION;
import static com.bill.rss.mongodb.FeedConstants.CATEGORY_NAME;
import static com.bill.rss.mongodb.FeedConstants.FEED_IDS;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

public class CategoriesRetriever implements CategoryProvider {

    private static final String CATEGORY_ID = "_id";

    private final FeedProvider feedRetriever = new FeedRetriever();

    public List<Category> retrieveCategories(String username) {
        DBCollection categoriesCollection = getCategoriesCollection();
        DBCursor categoriesCursor = executeCategoriesQuery(username, categoriesCollection);
        List<Category> categories = parseCategoryResults(categoriesCursor, username);
        enrichCategoriesWithCounts(username, categories);
        return categories;
    }


    private void enrichCategoriesWithCounts(String username, List<Category> categories) {
        DBCollection feedItemCollection = FeedItemUtils.getFeedItemsCollection();
        FeedItemRetriever feedItemRetriever = new FeedItemRetriever();
        DBObject getUsersFeedItemsQuery = feedItemRetriever.buildGetUsersFeedItemsQuery(username);
        DBObject groupByCountQuery = buildGroupByCountQuery();

        AggregationOutput countOfFeedItems = feedItemCollection.aggregate(getUsersFeedItemsQuery, groupByCountQuery);
        parseCounts(categories, countOfFeedItems, true);

        addUnreadFeedsToQuery(getUsersFeedItemsQuery);
        AggregationOutput countOfUnreadFeedItems = feedItemCollection.aggregate(getUsersFeedItemsQuery, groupByCountQuery);
        parseCounts(categories, countOfUnreadFeedItems, false);
        calculateCategoryCounts(categories);
    }


    private void calculateCategoryCounts(List<Category> categories) {
        for (Category category : categories) {
            int totalCountForCategory = 0;
            int unreadCountForCategory = 0;
            for (Feed feed : category.getFeeds()) {
                totalCountForCategory += Integer.parseInt(feed.getTotalCount());
                unreadCountForCategory += Integer.parseInt(feed.getUnReadCount());
            }
            category.setTotalCount(Integer.toString(totalCountForCategory));
            category.setUnReadCount(Integer.toString(unreadCountForCategory));
        }
    }


    private DBObject buildGroupByCountQuery() {
        BasicDBObject groupByCountQuery = new BasicDBObject();
        BasicDBObject query = new BasicDBObject("_id", "$feedId");

        BasicDBObject countValue = new BasicDBObject();
        countValue.append("$sum", 1);
        query.append("count", countValue);

        groupByCountQuery.append("$group", query);
        return groupByCountQuery;
    }


    private void parseCounts(List<Category> categories, AggregationOutput countOfFeedItems, boolean totalCount) {
        for (DBObject result : countOfFeedItems.results()) {
            for (Category category : categories) {
                for (Feed feed : category.getFeeds()) {
                    if (feed.getFeedId().equals(result.get("_id").toString())) {
                        if (totalCount) {
                            feed.setTotalCount(result.get("count").toString());
                        } else {
                            feed.setUnReadCount(result.get("count").toString());
                        }
                        break;
                    }
                }
            }
        }
    }


    private void addUnreadFeedsToQuery(DBObject getUsersFeedItemsQuery) {
        ((BasicDBObject) getUsersFeedItemsQuery.get("$match")).put("read", new BasicDBObject("$ne", true));
    }


    private DBCollection getCategoriesCollection() {
        DB rssDb = MongoDBConnection.getDbConnection();
        return rssDb.getCollection(CATEGORIES_COLLECTION);
    }

    private DBCursor executeCategoriesQuery(String username, DBCollection categoriesCollection) {
        BasicDBObject query = new BasicDBObject();
        query.append(USER_NAME, username);
        return categoriesCollection.find(query);
    }

    private List<Category> parseCategoryResults(DBCursor categoriesCursor, String username) {
        List<Category> categories = new ArrayList<Category>();
        while (categoriesCursor.hasNext()) {
            DBObject next = categoriesCursor.next();
            categories.add(createCategory(next, username));
        }
        return categories;
    }

    private Category createCategory(DBObject nextCategory, String username) {
        Category category = new Category();
        category.setCategoryId(nextCategory.get(CATEGORY_ID).toString());
        category.setName(nextCategory.get(CATEGORY_NAME).toString());
        category.setUsername(nextCategory.get(USER_NAME).toString());
        category.setTotalCount("0");
        category.setUnReadCount("0");
        category.setFeeds(parseCategoryFeeds(nextCategory, username));
        return category;
    }

    private List<Feed> parseCategoryFeeds(DBObject nextCategory, String username) {
        BasicDBList dbFeedIds = (BasicDBList) nextCategory.get(FEED_IDS);
        List<String> feedIds = new ArrayList<String>();
        for (Object feedId : dbFeedIds) {
            feedIds.add(feedId.toString());
        }
        return feedRetriever.retrieveFeedsIn(feedIds, username);
    }
}
