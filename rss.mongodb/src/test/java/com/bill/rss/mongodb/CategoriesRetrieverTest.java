package com.bill.rss.mongodb;

import java.util.List;

import org.junit.Test;

import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.mongodb.DB;

import static org.junit.Assert.assertEquals;

public class CategoriesRetrieverTest {


    @Test
    public void testRetrieveCategories() {
        CategoriesRetriever categoriesRetriever = new CategoriesRetriever();

        DB db = MockUtils.createDbMock();
        MockUtils.createCategoriesCollectionMock(db);
        MockUtils.createFeedsCollectionMock(db);

        List<Category> retrieveCategories = categoriesRetriever.retrieveCategories("billblake");
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

}
