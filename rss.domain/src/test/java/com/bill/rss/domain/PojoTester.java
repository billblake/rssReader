package com.bill.rss.domain;

import org.agileware.test.ConstructorsTester;
import org.agileware.test.PropertiesTester;
import org.junit.Test;

public class PojoTester {

    @Test
    public void testProperties() throws Exception {
        PropertiesTester tester = new PropertiesTester();
        tester.testAll(Category.class);
        tester.testAll(Feed.class);
        tester.testAll(FeedItem.class);
        tester.testAll(User.class);

        ConstructorsTester constructerTester = new ConstructorsTester();
        constructerTester.testAll(ReaderException.class);
    }
}
