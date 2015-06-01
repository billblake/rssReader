package com.bill.rss.dataProvider;

import java.util.List;

import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;

public interface FeedItemProvider {

	List<FeedItem> retrieveFeedItems(String categoryId, String feedId, String username, int page);

    void enrichCategoryWithFeedItemCount(Category category);

    void enrichFeedWithFeedItemCount(Feed feed);
}
