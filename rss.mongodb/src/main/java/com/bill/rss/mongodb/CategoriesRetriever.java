
package com.bill.rss.mongodb;

import static com.bill.rss.mongodb.FeedConstants.CATEGORIES_COLLECTION;
import static com.bill.rss.mongodb.FeedConstants.CATEGORY_NAME;
import static com.bill.rss.mongodb.FeedConstants.FEED_IDS;
import static com.bill.rss.mongodb.FeedConstants.USER_NAME;

import java.util.ArrayList;
import java.util.List;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class CategoriesRetriever implements CategoryProvider {

	private static final String CATEGORY_ID = "_id";

	private final FeedProvider feedRetriever = new FeedRetriever();

	public List<Category> retrieveCategories(String username) {
		DBCollection categoriesCollection = getCategoriesCollection();
	    DBCursor categoriesCursor = executeCategoriesQuery(username, categoriesCollection);
	    return parseCategoryResults(categoriesCursor, username);
	}


    private DBCollection getCategoriesCollection() {
        DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection categoriesCollection = rssDb.getCollection(CATEGORIES_COLLECTION);
        return categoriesCollection;
    }


    private DBCursor executeCategoriesQuery(String username, DBCollection categoriesCollection) {
        BasicDBObject query = new BasicDBObject();
        query.append(USER_NAME, username);
        DBCursor categoriesCursor = categoriesCollection.find(query);
        return categoriesCursor;
    }


	private List<Category> parseCategoryResults(DBCursor categoriesCursor, String username) {
		List<Category> categories = new ArrayList<Category>();
		while (categoriesCursor.hasNext()) {
	    	DBObject next = categoriesCursor.next();
	    	categories.add(createCategory(next, username));
	    }
		return categories;
	}


	private Category createCategory(DBObject nextCategory, String username) {
		Category category = new Category();
		category.setCategoryId(nextCategory.get(CATEGORY_ID).toString());
		category.setName(nextCategory.get(CATEGORY_NAME).toString());
		category.setUsername(nextCategory.get(USER_NAME).toString());
		category.setFeeds(parseCategoryFeeds(nextCategory, username));
		return category;
	}


	private List<Feed> parseCategoryFeeds(DBObject nextCategory, String username) {
		BasicDBList dbFeedIds = (BasicDBList) nextCategory.get(FEED_IDS);
		List<String> feedIds = new ArrayList<String>();
		for (Object feedId : dbFeedIds) {
			feedIds.add(feedId.toString());
		}
		return feedRetriever.retrieveFeedsIn(feedIds, username);
	}
}
