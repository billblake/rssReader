package com.bill.rss.mongodb;

import java.util.Date;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static org.mockito.Matchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockUtils {

    public static DBObject createCategoryDbObjectMock() {
        BasicDBList dbFeedIds = mock(BasicDBList.class);

        Iterator<Object> mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(mockIterator.next()).thenReturn("53bf159330044594e9be9f78");
        when(dbFeedIds.iterator()).thenReturn(mockIterator);

        DBObject categoryDbObject = mock(DBObject.class);
        when(categoryDbObject.get("_id")).thenReturn("12345");
        when(categoryDbObject.get("name")).thenReturn("Sport");
        when(categoryDbObject.get("userName")).thenReturn("billblake");
        when(categoryDbObject.get("feedIds")).thenReturn(dbFeedIds);
        return categoryDbObject;
    }


    public static DBCollection createCategoriesCollectionMock(DB db) {
        DBCollection categoriesCollection = mock(DBCollection.class);
        when(db.getCollection("categories")).thenReturn(categoriesCollection );

        DBCursor categoriesCursor = createCursorMock(createCategoryDbObjectMock());
        when(categoriesCollection.find(any(BasicDBObject.class))).thenReturn(categoriesCursor);

        return categoriesCollection;
    }


    public static DBCollection createFeedsCollectionMock(DB db) {
        DBCollection feedsCollection = mock(DBCollection.class);
        when(db.getCollection("feeds")).thenReturn(feedsCollection );

        DBCursor feedsCursor = createCursorMock(createFeedsDbObjectMock());
        when(feedsCollection.find(any(BasicDBObject.class))).thenReturn(feedsCursor);

        return feedsCollection;
    }


    public static DBCollection createFeedsItemsCollectionMock(DB db) {
        DBCollection feedsItemsCollection = mock(DBCollection.class);
        when(db.getCollection("feedItems")).thenReturn(feedsItemsCollection );

        DBCursor feedsItemsCursor = createCursorMock(createFeedsItemDbObjectMock());
        when(feedsItemsCursor.sort(any(DBObject.class))).thenReturn(feedsItemsCursor);
        when(feedsItemsCollection.find(any(BasicDBObject.class))).thenReturn(feedsItemsCursor);

        return feedsItemsCollection;
    }


    public static DBObject createFeedsItemDbObjectMock() {
        DBObject feedDbObject = mock(DBObject.class);
        when(feedDbObject.get("_id")).thenReturn("654");
        when(feedDbObject.get("categoryId")).thenReturn("235");
        when(feedDbObject.get("description")).thenReturn("The Description");
        when(feedDbObject.get("feedId")).thenReturn("467");
        when(feedDbObject.get("link")).thenReturn("http://www.bbc.co.uk/sport/1234");
        when(feedDbObject.get("source")).thenReturn("BBC Sport");
        when(feedDbObject.get("title")).thenReturn("My Title");
        when(feedDbObject.get("userName")).thenReturn("billblake");
        Date date = new Date(1408277117000L);
        when(feedDbObject.get("pubDate")).thenReturn(date);
        return feedDbObject;
    }



    public static DBObject createFeedsDbObjectMock() {
        DBObject feedDbObject = mock(DBObject.class);
        when(feedDbObject.get("_id")).thenReturn("12345");
        when(feedDbObject.get("feedId")).thenReturn("124124");
        when(feedDbObject.get("name")).thenReturn("BBC Sport");
        when(feedDbObject.get("url")).thenReturn("http://www.bbc.co.uk");
        when(feedDbObject.get("username")).thenReturn("billblake");
        when(feedDbObject.get("categoryId")).thenReturn("521");
        return feedDbObject;
    }


    public static DBCursor createCursorMock(DBObject feedDbObject) {
        DBCursor feedsCursor = mock(DBCursor.class);
        when(feedsCursor.hasNext()).thenReturn(true, false);
        when(feedsCursor.next()).thenReturn(feedDbObject );
        return feedsCursor;
    }


    public static DB createDbMock() {
        DB db = mock(DB.class);
        MongoDBConnection.setDbConnection(db);
        return db;
    }
}
