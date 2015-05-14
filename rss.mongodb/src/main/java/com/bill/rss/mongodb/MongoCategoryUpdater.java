package com.bill.rss.mongodb;

import org.bson.types.ObjectId;

import com.bill.rss.dataProvider.CategoryUpdater;
import com.bill.rss.domain.Category;
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
        DB dbConnection = MongoDBConnection.getDbConnection();
        DBCollection categoriesCollection = dbConnection.getCollection(CATEGORIES_COLLECTION);

        BasicDBObject categoryQuery = new BasicDBObject();
        categoryQuery.put(USER_NAME, category.getUsername());
        categoryQuery.put(CATEGORY_OBJECT_ID, new ObjectId(category.getCategoryId()));

        DBObject categoryDocument = categoriesCollection.findOne(categoryQuery);
        categoryDocument.put(CATEGORY_NAME, category.getName());
        categoriesCollection.save(categoryDocument);
        return category;
    }

}
