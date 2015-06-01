package com.bill.rss.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.mongodb.CategoriesRetriever;
import com.bill.rss.mongodb.FeedItemRetriever;

import static com.bill.rss.server.ViewConstants.JSON_RESPONSE_TYPE;

public class GetCategoriesRoute extends BaseRoute {

	private CategoryProvider categoryProvider;
	private FeedItemProvider feedItemRetriever = new FeedItemRetriever();

	protected GetCategoriesRoute(String path) {
		super(path);
		categoryProvider = new CategoriesRetriever();
	}

	@Override
	public Object handle(Request request, Response response) {
		verifyUserLoggedIn(request, response);

		response.type(JSON_RESPONSE_TYPE);
		String categoryId = request.params(":categoryId");
		List<Category> categories = categoryProvider.retrieveCategories(getUsername(request));
		enrichCounts(categories);
		if (StringUtils.isBlank(categoryId)) {
		    return JsonUtils.convertObjectToJson(categories);
		} else {
		    return JsonUtils.convertObjectToJson(categories.get(0));
		}
	}


	private void enrichCounts(List<Category> categories) {
        for (Category category : categories) {
            feedItemRetriever.enrichCategoryWithFeedItemCount(category);
            for (Feed feed : category.getFeeds()) {
                feedItemRetriever.enrichFeedWithFeedItemCount(feed);
            }
        }

    }

    void setCategoryProvider(CategoryProvider categoryProvider) {
	    this.categoryProvider = categoryProvider;
	}

    void setFeedItemRetriever(FeedItemRetriever feedItemRetriever) {
        this.feedItemRetriever = feedItemRetriever;
    }
}
