package com.bill.rss.server;

import org.junit.Test;

public class RssControllerTest {

    @Test
    public void testSetup() {
        RssController rssController = new RssController();
        rssController.init();
    }
}
