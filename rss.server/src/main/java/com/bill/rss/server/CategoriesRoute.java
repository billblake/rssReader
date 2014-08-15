package com.bill.rss.server;

import java.util.List;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.mongodb.CategoriesRetriever;

import static com.bill.rss.server.ViewConstants.JSON_RESPONSE_TYPE;

public class CategoriesRoute extends BaseRoute {

	private CategoryProvider categoryProvider;

	protected CategoriesRoute(String path) {
		super(path);
		categoryProvider = new CategoriesRetriever();
	}

	@Override
	public Object handle(Request request, Response response) {
		verifyUserLoggedIn(request, response);
		response.type(JSON_RESPONSE_TYPE);
		List<Category> categories = categoryProvider.retrieveCategories(getUsername(request));
		return JsonUtils.convertObjectToJson(categories);
	}


	void setCategoryProvider(CategoryProvider categoryProvider) {
	    this.categoryProvider = categoryProvider;
	}
}
