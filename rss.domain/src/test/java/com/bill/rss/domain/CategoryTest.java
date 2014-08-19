package com.bill.rss.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CategoryTest {

    @Test
    public void addFeed() {
        Category category = new Category();
        assertNull(category.getFeeds());
        category.addFeed(new Feed());
        category.addFeed(null);
        category.addFeed(new Feed());
        assertEquals(2, category.getFeeds().size());
    }
}
