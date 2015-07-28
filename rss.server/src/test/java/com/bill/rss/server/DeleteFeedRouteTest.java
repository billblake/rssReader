package com.bill.rss.server;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.junit.Test;
import org.mockito.Mockito;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;

import spark.Request;
import spark.Response;

public class DeleteFeedRouteTest {

	@Test
	public void testDeleteFeed() {
		Request request = createRequestMock("logged-in");
        Response response = createResponseMock();
		when(request.params(":categoryId")).thenReturn("1");
        when(request.params(":feedId")).thenReturn("2");
        
        FeedUpdater feedUpdater = Mockito.mock(FeedUpdater.class);
		
		DeleteFeedRoute deleteFeedRoute = new DeleteFeedRoute("");
		deleteFeedRoute.setFeedUpdater(feedUpdater);
		deleteFeedRoute.handle(request, response);
		
		Feed feed = new Feed();
		feed.setCategoryId("1");
		feed.setFeedId("2");
		Mockito.verify(feedUpdater, times(1)).deleteFeed(any(Feed.class));
	}
}
