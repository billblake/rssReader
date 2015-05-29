package com.bill.rss.server;

import spark.servlet.SparkApplication;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

public class RssController implements SparkApplication {

	public void init() {
		get(new GetCategoriesRoute("/category"));
		get(new GetCategoriesRoute("/category/:categoryId"));
		post(new SaveCategoryRoute("/category"));
		post(new SaveCategoryRoute("/category/:categoryId"));
		delete(new DeleteCategoryRoute("/category/:categoryId"));

		get(new GetFeedsRoute("/feeds/category/feed"));
		get(new GetFeedsRoute("/feeds/category/:categoryId/feed"));
		get(new GetFeedsRoute("/feeds/category/feed/:feedId"));
		post(new SaveFeedRoute("/feeds/category/:categoryId/feed"));
		delete(new DeleteFeedRoute("/feeds/category/:categoryId/feed/:feedId"));

		get(new LogoutRoute("/logout"));
		post(new LoginRoute("/login"));

		get(new FeedRefreshRoute("/refresh"));

		post(new UserRegistrationRoute("/user"));

		put(new UpdateFeedItemRoute("/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId"));
		delete(new DeleteFeedItemRoute("/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId"));
	}
}
