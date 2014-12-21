package rss.feedfetcher;

import java.util.List;

import com.bill.rss.domain.FeedItem;

public interface FeedFetcher {

	List<FeedItem> fetchFeed(String feedUrl);
}
