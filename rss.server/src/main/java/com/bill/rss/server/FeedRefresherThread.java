package com.bill.rss.server;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.mongodb.MongoFeedUpdater;

public class FeedRefresherThread implements Runnable {

    private final String command;
    private final FeedUpdater feedUpdater;

    public FeedRefresherThread(String s){
        this.command=s;
        feedUpdater = new MongoFeedUpdater();
    }


    public void run() {
        feedUpdater.updateWithLatestFeeds();
    }


    @Override
    public String toString(){
        return this.command;
    }


}
