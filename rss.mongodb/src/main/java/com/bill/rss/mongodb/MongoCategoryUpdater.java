package com.bill.rss.mongodb;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.CategoryUpdater;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import static com.bill.rss.mongodb.FeedConstants.CATEGORIES_COLLECTION;
import static com.bill.rss.mongodb.FeedConstants.CATEGORY_NAME;
import static com.bill.rss.mongodb.FeedConstants.CATEGORY_OBJECT_ID;
import static com.bill.rss.mongodb.FeedConstants.FEED_IDS;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

public class MongoCategoryUpdater implements CategoryUpdater {

    MongoFeedUpdater feedUpdater = new MongoFeedUpdater();

    public Category addCategory(Category category) {
        DB dbConnection = MongoDBConnection.getDbConnection();
        DBCollection categoriesCollection = dbConnection.getCollection("categories");

        BasicDBObject categoryDocument = new BasicDBObject();
        categoryDocument.put(USER_NAME, category.getUsername());
        categoryDocument.put(CATEGORY_NAME, category.getName());
        categoryDocument.put(FEED_IDS, new BasicDBList());

        categoriesCollection.save(categoryDocument);

        category.setCategoryId(categoryDocument.getObjectId(CATEGORY_OBJECT_ID).toString());
        return category;
    }

    public Category saveCategory(Category category) {
        DBObject categoryDocument = getCategoryDocument(category);
        categoryDocument.put(CATEGORY_NAME, category.getName());
        getCategoriesCollection().save(categoryDocument);
        return category;
    }

    public Category deleteCategory(Category category) {
        DBCollection categoriesCollection = getCategoriesCollection();
        BasicDBObject categoryQuery = new BasicDBObject();
        categoryQuery.put(USER_NAME, category.getUsername());
        categoryQuery.put(CATEGORY_OBJECT_ID, new ObjectId(category.getCategoryId()));
        DBObject categoryDocument = categoriesCollection.findOne(categoryQuery);
        deleteAssociatedFeeds(category, categoryDocument);
        categoriesCollection.remove(categoryQuery);
        return category;
    }

    private void deleteAssociatedFeeds(Category category, DBObject categoryDocument) {
        BasicDBList feedIds = (BasicDBList) categoryDocument.get(FEED_IDS);
        for (Object feedId : feedIds) {
            Feed feed = new Feed();
            feed.setFeedId(((ObjectId) feedId).toString());
            feed.setCategoryId(category.getCategoryId());
            feed.setUserName(category.getUsername());
            feedUpdater.deleteFeed(feed);
        }
    }



    private DBObject getCategoryDocument(Category category) {
        BasicDBObject categoryQuery = new BasicDBObject();
        categoryQuery.put(USER_NAME, category.getUsername());
        categoryQuery.put(CATEGORY_OBJECT_ID, new ObjectId(category.getCategoryId()));

        DBCollection categoriesCollection = getCategoriesCollection();
        return categoriesCollection.findOne(categoryQuery);
    }

    private DBCollection getCategoriesCollection() {
        DB dbConnection = MongoDBConnection.getDbConnection();
        DBCollection categoriesCollection = dbConnection.getCollection(CATEGORIES_COLLECTION);
        return categoriesCollection;
    }

}
