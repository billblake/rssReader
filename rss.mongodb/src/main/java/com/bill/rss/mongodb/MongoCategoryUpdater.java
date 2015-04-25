package com.bill.rss.mongodb;

import com.bill.rss.dataProvider.CategoryUpdater;
import com.bill.rss.domain.Category;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class MongoCategoryUpdater implements CategoryUpdater {

    public Category addCategory(Category category) {
        DB dbConnection = MongoDBConnection.getDbConnection();
        DBCollection categoriesCollection = dbConnection.getCollection("categories");

        BasicDBObject categoryDocument = new BasicDBObject();
        categoryDocument.put("username", category.getUsername());
        categoryDocument.put("name", category.getName());
        categoryDocument.put("feedIds", new BasicDBList());

        categoriesCollection.save(categoryDocument);

        category.setCategoryId(categoryDocument.getObjectId("_id").toString());
        return category;
    }

    public Category saveCategory(Category category) {
        throw new RuntimeException("Not Implemented");
    }

}
