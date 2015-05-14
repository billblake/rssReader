package com.bill.rss.server;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.CategoryUpdater;
import com.bill.rss.domain.Category;
import com.bill.rss.mongodb.MongoCategoryUpdater;

public class SaveCategoryRoute extends BaseRoute {

    private final ObjectMapper mapper = new ObjectMapper();
    private final CategoryUpdater categoryUpdater;

    protected SaveCategoryRoute(String path) {
        super(path);
        categoryUpdater = new MongoCategoryUpdater();
    }

    @Override
    public Object handle(Request request, Response response) {
        Category category = extractCategoryFromRequest(request);
        if (StringUtils.isBlank(category.getCategoryId())) {
            category = categoryUpdater.addCategory(category);
        } else {
            //update existing category
            category = categoryUpdater.saveCategory(category);
        }

        return JsonUtils.convertObjectToJson(category);
    }


    private Category extractCategoryFromRequest(Request request) {
        Category category = null;
        try {
            category = mapper.readValue(request.body(), Category.class);
            category.setUsername(getUsername(request));
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }
        return category;
    }

}
