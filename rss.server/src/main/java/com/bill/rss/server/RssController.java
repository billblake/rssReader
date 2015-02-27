package com.bill.rss.server;

import spark.servlet.SparkApplication;
import static spark.Spark.get;
import static spark.Spark.post;

public class RssController implements SparkApplication {

	public void init() {
		get(new GetCategoriesRoute("/category"));
		get(new GetCategoriesRoute("/category/:categoryId"));
		get(new GetFeedsRoute("/feeds/category/feed"));
		get(new GetFeedsRoute("/feeds/category/:categoryId/feed"));
		get(new GetFeedsRoute("/feeds/category/feed/:feedId"));
        get(new LogoutRoute("/logout"));
        get(new FeedRefreshRoute("/refresh"));

		post(new UserRegistrationRoute("/user"));
		post(new LoginRoute("/login"));

		post(new SaveCategoryRoute("/category"));
		post(new SaveCategoryRoute("/category/:categoryId"));
	}
}
