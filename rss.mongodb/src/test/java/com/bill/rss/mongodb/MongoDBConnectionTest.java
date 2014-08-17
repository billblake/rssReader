package com.bill.rss.mongodb;

import org.junit.Test;

import com.mongodb.DB;

import static org.junit.Assert.assertNotNull;

public class MongoDBConnectionTest {

    @Test
    public void createNewDbConnection() {
        DB db = MongoDBConnection.getDbConnection();
        assertNotNull(db);
    }
}
