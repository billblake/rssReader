package com.bill.rss.mongodb;

import com.mongodb.DBObject;

public class MongoDbUtils {

    public static Boolean getBooleanValue(DBObject nextFeedItem, String fieldName) {
        Object fieldValue = nextFeedItem.get(fieldName);
        if (fieldValue == null) {
            return false;
        }
        return (Boolean) fieldValue;
    }
}
