package com.bill.rss.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import spark.Request;
import spark.Response;

public class FeedRefreshRoute extends BaseRoute {

    private final ExecutorService executor;

    protected FeedRefreshRoute(String path) {
        super(path);
        executor = Executors.newFixedThreadPool(5);
    }

    @Override
    public Object handle(Request request, Response response) {
        Runnable worker = new FeedRefresherThread("Feed Refresher");
        try {
            executor.execute(worker);
        } catch (Exception e) {
            return e;
        }
        return "ok";
    }
}