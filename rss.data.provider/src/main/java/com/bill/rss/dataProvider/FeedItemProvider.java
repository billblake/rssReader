package com.bill.rss.dataProvider;

import java.util.LinkedHashMap;
import java.util.List;

import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;

public interface FeedItemProvider {

	List<FeedItem> retrieveFeedItems(FeedItem feedItem, int page);

    void enrichCategoryWithFeedItemCount(Category category);

    void enrichFeedWithFeedItemCount(Feed feed);

    LinkedHashMap<String, Integer> getTags(String username);

    LinkedHashMap<String, Integer> getTags(String username, int numberOfTags);
}
