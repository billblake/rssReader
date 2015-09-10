package com.bill.rss.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.mockito.Mockito;

import com.mongodb.AggregationOutput;
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
        when(categoryDbObject.get("username")).thenReturn("billblake");
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
        DBCollection feedItemsCollection = mock(DBCollection.class);
        when(db.getCollection("feedItems")).thenReturn(feedItemsCollection );

        DBCursor feedsItemsCursor = createCursorMock(createFeedsItemDbObjectMock());
        when(feedsItemsCursor.sort(any(DBObject.class))).thenReturn(feedsItemsCursor);
        when(feedsItemsCursor.limit(any(Integer.class))).thenReturn(feedsItemsCursor);
        when(feedsItemsCursor.skip(any(Integer.class))).thenReturn(feedsItemsCursor);
        when(feedItemsCollection.find(any(BasicDBObject.class))).thenReturn(feedsItemsCursor);


        AggregationOutput aggregationOutput = Mockito.mock(AggregationOutput.class);
        List<DBObject> results = new ArrayList<DBObject>();
        DBObject result1 = new BasicDBObject();
        result1.put("_id", "tag1");
        result1.put("count", 5);
        results.add(result1);
        DBObject result2 = new BasicDBObject();
        result2.put("_id", "tag2");
        result2.put("count", 8);
        results.add(result2);

		when(aggregationOutput.results()).thenReturn(results );
		when(feedItemsCollection.aggregate(any(DBObject.class), any(DBObject.class), any(DBObject.class), any(DBObject.class)))
        	.thenReturn(aggregationOutput);



		AggregationOutput countAggregate = Mockito.mock(AggregationOutput.class);
        List<DBObject> countResults = new ArrayList<DBObject>();
        DBObject countResult1 = new BasicDBObject();
        countResult1.put("_id", "12345");
        countResult1.put("count", 5);
        countResults.add(countResult1);
        when(countAggregate.results()).thenReturn(countResults);

        when(feedItemsCollection.aggregate(any(DBObject.class), any(DBObject.class))).thenReturn(countAggregate);


		DBObject feedItem = new BasicDBObject();
		feedItem.put("_id", "52ffe096e81f7a9bb906b6f9");
		feedItem.put("categoryId", "5581e900d4c6cda2d04d0a98");
		feedItem.put("feedId", "55893938e4b0c822dc99fa2f");
		feedItem.put("delete", true);
		feedItem.put("description", "description");
		feedItem.put("link", "http://thedailyedge.thejournal.ie/irish-supermarket-fails-2176781-Jun2015/");
		feedItem.put("imageLink", "http://thedailyedge.thejournal.ie/image.jpg");
		feedItem.put("read", true);
		feedItem.put("source", "The Daily Edge");
		feedItem.put("title", "title");
		feedItem.put("username", "bob");
		feedItem.put("pubDate", new Date());


		when(feedItemsCollection.findOne(any(DBObject.class))).thenReturn(feedItem );

        return feedItemsCollection;
    }


    public static DBObject createFeedsItemDbObjectMock() {
        DBObject feedDbObject = mock(DBObject.class);
        when(feedDbObject.get("_id")).thenReturn("654");
        when(feedDbObject.get("categoryId")).thenReturn("235");
        when(feedDbObject.get("description")).thenReturn("The Description");
        when(feedDbObject.get("feedId")).thenReturn("467");
        when(feedDbObject.get("link")).thenReturn("http://www.bbc.co.uk/sport/1234");
        when(feedDbObject.get("imageLink")).thenReturn("http://www.bbc.co.uk/sport/1234.jpg");
        when(feedDbObject.get("source")).thenReturn("BBC Sport");
        when(feedDbObject.get("title")).thenReturn("My Title");
        when(feedDbObject.get("username")).thenReturn("billblake");
        when(feedDbObject.get("read")).thenReturn(true);
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


    public static DBCollection createUsersCollectionMock(DB db) {
        DBCollection usersCollection = mock(DBCollection.class);
        when(db.getCollection("user")).thenReturn(usersCollection );

        DBObject userDbObjectMock = createUserDbObjectMock();
        DBCursor usersCursor = createCursorMock(userDbObjectMock);
        when(usersCollection.find(any(BasicDBObject.class))).thenReturn(usersCursor);
        when(usersCursor.hasNext()).thenReturn(true);
        when(usersCollection.findOne(any(BasicDBObject.class))).thenReturn(userDbObjectMock);
        return usersCollection;
    }

    public static DBObject createUserDbObjectMock() {
        DBObject userDbObject = mock(DBObject.class);
        when(userDbObject.get("firstName")).thenReturn("Bill");
        when(userDbObject.get("lastName")).thenReturn("Blake");
        return userDbObject;
    }


    public static DBObject createUserDbObjectListMock() {
        BasicDBList users = mock(BasicDBList.class);

        Iterator<Object> mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(mockIterator.next()).thenReturn(ObjectId.get().toString());
        when(users.iterator()).thenReturn(mockIterator);
        return users;
    }
}
