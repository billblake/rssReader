package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;
import spark.Route;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.mongodb.CategoriesRetriever;

public class CategoriesRoute extends Route {

	private CategoryProvider categoryProvider;

	protected CategoriesRoute(String path) {
		super(path);
		categoryProvider = new CategoriesRetriever();
	}

	@Override
	public Object handle(Request request, Response response) {
		response.type(ViewConstants.JSON_RESPONSE_TYPE);
		List<Category> categories = categoryProvider.retrieveCategories();
		return JsonUtils.convertObjectToJson(categories);
	}

}
