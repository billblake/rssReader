package com.bill.rss.mongodb;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CategoriesRetrieverTest {


    @Test
    public void testRetrieveCategories() {
        CategoriesRetriever categoriesRetriever = new CategoriesRetriever();

        DB db = createDbMock();
        DBCollection categoriesCollection = createCategoriesCollectionMock(db);
        DBCollection feedsCollection = createFeedsCollectionMock(db);

        List<Category> retrieveCategories = categoriesRetriever.retrieveCategories("billblake"); // TODO add assert
        assertEquals(1, retrieveCategories.size());

        Category category = retrieveCategories.get(0);
        assertEquals("12345", category.getCategoryId());
        assertEquals("Sport", category.getName());
        assertEquals("billblake", category.getUsername());
        Feed feed = category.getFeeds().get(0);
        assertEquals("521", feed.getCategoryId());
        assertEquals("12345", feed.getFeedId());
        assertEquals("BBC Sport", feed.getName());
        assertEquals("http://www.bbc.co.uk", feed.getUrl());
        assertEquals("billblake", feed.getUserName());
    }


    private DBObject createCategoryDbObjectMock() {
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


    private DBCursor createCategoriesCursorMock(DBObject categoryDbObject) {
        DBCursor categoriesCursor = mock(DBCursor.class);
        when(categoriesCursor.hasNext()).thenReturn(true, false);
        when(categoriesCursor.next()).thenReturn(categoryDbObject );
        return categoriesCursor;
    }


    private DBCollection createCategoriesCollectionMock(DB db) {
        DBCollection categoriesCollection = mock(DBCollection.class);
        when(db.getCollection("categories")).thenReturn(categoriesCollection );

        DBCursor categoriesCursor = createCategoriesCursorMock(createCategoryDbObjectMock());
        when(categoriesCollection.find(any(BasicDBObject.class))).thenReturn(categoriesCursor);

        return categoriesCollection;
    }


    private DBCollection createFeedsCollectionMock(DB db) {
        DBCollection feedsCollection = mock(DBCollection.class);
        when(db.getCollection("feeds")).thenReturn(feedsCollection );

        DBCursor feedsCursor = createFeedsCursorMock(createFeedsDbObjectMock());
        when(feedsCollection.find(any(BasicDBObject.class))).thenReturn(feedsCursor);

        return feedsCollection;
    }


    private DBObject createFeedsDbObjectMock() {
        DBObject feedDbObject = mock(DBObject.class);
        when(feedDbObject.get("_id")).thenReturn("12345");
        when(feedDbObject.get("feedId")).thenReturn("124124");
        when(feedDbObject.get("name")).thenReturn("BBC Sport");
        when(feedDbObject.get("url")).thenReturn("http://www.bbc.co.uk");
        when(feedDbObject.get("username")).thenReturn("billblake");
        when(feedDbObject.get("categoryId")).thenReturn("521");
        return feedDbObject;
    }


    private DBCursor createFeedsCursorMock(DBObject feedDbObject) {
        DBCursor feedsCursor = mock(DBCursor.class);
        when(feedsCursor.hasNext()).thenReturn(true, false);
        when(feedsCursor.next()).thenReturn(feedDbObject );
        return feedsCursor;
    }


    private DB createDbMock() {
        DB db = mock(DB.class);
        MongoDBConnection.setDbConnection(db);
        return db;
    }
}
