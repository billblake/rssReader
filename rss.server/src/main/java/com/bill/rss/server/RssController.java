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

		get(new LogoutRoute("/logout"));
		post(new LoginRoute("/login"));

		get(new FeedRefreshRoute("/refresh"));

		post(new UserRegistrationRoute("/user"));

		put(new UpdateFeedItemRoute("/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId"));
		put(new UpdateAllFeedItemsForCategoryRoute("/feeds/category/:categoryId/feed/feedItem"));
		put(new UpdateAllFeedItemsForFeedRoute("/feeds/category/feed/:feedId/feedItem"));
		put(new UpdateAllFeedItemsRoute("/feeds/category/feed/feedItem"));

		delete(new DeleteFeedItemRoute("/feeds/category/:categoryId/feed/:feedId/feedItem/:feedItemId"));
		delete(new DeleteAllFeedItemsForCategoryRoute("/feeds/category/:categoryId/feed/feedItem"));
		delete(new DeleteAllFeedItemsForFeedRoute("/feeds/category/feed/:feedId/feedItem"));
		delete(new DeleteAllFeedItemsRoute("/feeds/category/feed/feedItem"));

        delete(new DeleteFeedRoute("/feeds/category/:categoryId/feed/:feedId"));
	}
}
