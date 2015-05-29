package com.bill.rss.server;

import spark.Request;


public abstract class BaseFeedItemRoute extends BaseRoute {

    protected BaseFeedItemRoute(String path) {
        super(path);
    }


    protected String getFeedItemId(Request request) {
        return request.params("feedItemId");
    }

}
