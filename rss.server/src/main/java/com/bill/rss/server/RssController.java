package com.bill.rss.server;

import static spark.Spark.get;
import static spark.Spark.post;
import spark.servlet.SparkApplication;

public class RssController implements SparkApplication {

	public void init() {
		
		get(new CategoriesRoute("/categories"));
		
		get(new FeedsRoute("/category/feeds"));
		get(new FeedsRoute("/category/:categoryId/feeds"));
		get(new FeedsRoute("/category/feeds/:feedId"));
		
		post(new UserRegistrationRoute("/users"));
	}
}
