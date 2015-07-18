package com.bill.rss.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.FeedItem;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FeedsRouteTest {

    Request request = mock(Request.class);
    Response response = mock(Response.class);

    @Test
    public void testGetFeedItems() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();
        GetFeedItemsRoute feedsRoute = new GetFeedItemsRoute("/feeds");
        feedsRoute.setFeedProvider(createFeedProviderMock());
        feedsRoute.setFeedUpdater(createFeedUpdaterMock());

        when(request.params(":categoryId")).thenReturn("1");
        when(request.params(":feedId")).thenReturn("2");


        String jsonResponse = (String) feedsRoute.handle(request, response);
        String expectedJson = "[{\"feedItemId\":\"3\",\"username\":\"billblake\",\"catId\":\"1\",\"feedId\":\"2\",\"source\":\"source\",\"title\":\"title\",\"description\":\"description\",\"link\":\"link\",\"pubDate\":10000000,\"formattedDate\":\"Jul 28\",\"read\":false,\"saved\":false,\"tags\":null}]";
        assertEquals(expectedJson , jsonResponse);
    }


    @Test
    public void testGetFeedItemsRefresh() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();
        GetFeedItemsRoute feedsRoute = new GetFeedItemsRoute("/feeds");
        feedsRoute.setFeedProvider(createFeedProviderMock());
        feedsRoute.setFeedUpdater(createFeedUpdaterMock());

        when(request.params(":categoryId")).thenReturn("1");
        when(request.params(":feedId")).thenReturn("2");
        when(request.queryParams("refresh")).thenReturn("false");


        String jsonResponse = (String) feedsRoute.handle(request, response);
        String expectedJson = "[{\"feedItemId\":\"3\",\"username\":\"billblake\",\"catId\":\"1\",\"feedId\":\"2\",\"source\":\"source\",\"title\":\"title\",\"description\":\"description\",\"link\":\"link\",\"pubDate\":10000000,\"formattedDate\":\"Jul 28\",\"read\":false,\"saved\":false,\"tags\":null}]";
        assertEquals(expectedJson , jsonResponse);
    }


    @Test
    public void testGetFeedItemsAndRefresh() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();
        GetFeedItemsRoute feedsRoute = new GetFeedItemsRoute("/feeds");
        feedsRoute.setFeedProvider(createFeedProviderMock());
        feedsRoute.setFeedUpdater(createFeedUpdaterMock());

        when(request.params(":categoryId")).thenReturn("1");
        when(request.params(":feedId")).thenReturn("2");
        when(request.queryParams("refresh")).thenReturn("true");

        String jsonResponse = (String) feedsRoute.handle(request, response);
        String expectedJson = "[{\"feedItemId\":\"3\",\"username\":\"billblake\",\"catId\":\"1\",\"feedId\":\"2\",\"source\":\"source\",\"title\":\"title\",\"description\":\"description\",\"link\":\"link\",\"pubDate\":10000000,\"formattedDate\":\"Jul 28\",\"read\":false,\"saved\":false,\"tags\":null}]";
        assertEquals(expectedJson , jsonResponse);
    }


    private FeedItemProvider createFeedProviderMock() {
        FeedItemProvider feedProvider = mock(FeedItemProvider.class);
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        FeedItem feedItem = MockUtils.createFeedItemMock("1", "description", "2", "3", "link", new Date(10000000), "source", "title", "billblake");
        feedItems.add(feedItem);
        when(feedProvider.retrieveFeedItems(Mockito.any(FeedItem.class), Mockito.anyInt())).thenReturn(feedItems);
        return feedProvider;
    }


    private FeedUpdater createFeedUpdaterMock() {
        FeedUpdater feedUpdater = mock(FeedUpdater.class);

        return feedUpdater;
    }
}
