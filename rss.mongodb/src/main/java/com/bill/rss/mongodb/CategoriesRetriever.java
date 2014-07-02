
package com.bill.rss.mongodb;

import java.util.ArrayList;
import java.util.List;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class CategoriesRetriever implements CategoryProvider {

	private static final String CATEGORY_ID = "_id";
	
	private FeedProvider feedRetriever = new FeedRetriever();

	public List<Category> retrieveCategories() {
		DB rssDb = MongoDBConnection.getDbConnection();
	    DBCollection categoriesCollection = rssDb.getCollection(FeedConstants.CATEGORIES_COLLECTION);
	    
	    DBCursor categoriesCursor = categoriesCollection.find();
	    
	    List<Category> categories = parseCategoryResults(categoriesCursor);
	    return categories;
	}

	private List<Category> parseCategoryResults(DBCursor categoriesCursor) {
		List<Category> categories = new ArrayList<Category>();
		while (categoriesCursor.hasNext()) {
	    	DBObject next = categoriesCursor.next();
	    	categories.add(createCategory(next));
	    }
		return categories;
	}

	private Category createCategory(DBObject nextCategory) {
		Category category = new Category();
		category.setCategoryId(nextCategory.get(CATEGORY_ID).toString());
		category.setName(nextCategory.get(FeedConstants.CATEGORY_NAME).toString());
		category.setUsername(nextCategory.get(FeedConstants.USER_NAME).toString());
		category.setFeeds(parseCategoryFeeds(nextCategory));
		return category;
	}

	private List<Feed> parseCategoryFeeds(DBObject nextCategory) {
		BasicDBList dbFeedIds = (BasicDBList) nextCategory.get(FeedConstants.FEED_IDS);
		List<String> feedIds = new ArrayList<String>();
		for (Object feedId : dbFeedIds) {
			feedIds.add(feedId.toString());
		}
		return feedRetriever.retrieveFeedsIn(feedIds);
	}
}
