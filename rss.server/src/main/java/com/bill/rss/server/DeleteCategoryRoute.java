package com.bill.rss.server;

import spark.Request;
import spark.Response;

import com.bill.rss.domain.Category;

import static com.bill.rss.server.ViewConstants.CATEGORY_ID_PATH_VARIABLE;

public class DeleteCategoryRoute extends BaseRoute {

    public DeleteCategoryRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        Category category = new Category();
        category.setCategoryId(request.params(CATEGORY_ID_PATH_VARIABLE));
        category.setUsername(getUsername(request));
        return null;
    }

}
