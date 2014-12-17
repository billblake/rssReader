package com.bill.rss.server;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;
import spark.Route;

import com.bill.rss.domain.Category;

public class SaveCategoryRoute extends Route {

    private final ObjectMapper mapper = new ObjectMapper();

    protected SaveCategoryRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        Category category = extractCategoryFromRequest(request);
        if (StringUtils.isBlank(category.getCategoryId())) {
            //create new category
        } else {
            //update existing category
        }

        return null;
    }


    private Category extractCategoryFromRequest(Request request) {
        Category category = null;
        try {
            category = mapper.readValue(request.body(), Category.class);
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }
        return category;
    }

}
