package com.bill.rss.server;

import static spark.Spark.setPort;

public class HerokuBootStrapper {

	public static final void main(String[] args) {
		System.out.println("Rock and Roll");
		setPort(Integer.parseInt(System.getenv("PORT")));
		RssController rssController = new RssController();
		rssController.init();
	} 
}
