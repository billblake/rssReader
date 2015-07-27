package com.bill.rss.server;

import java.util.LinkedHashMap;

import org.junit.Test;
import org.mockito.Mockito;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemProvider;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetFeedItemTagsTest {

    Request request = mock(Request.class);
    Response response = mock(Response.class);

    @Test
    public void testGetFeedItems() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();
        GetFeedItemTags getfeedItemTagsRoute = new GetFeedItemTags("/feeds");

        getfeedItemTagsRoute.setFeedProvider(createFeedProviderMock());

        when(request.params(":categoryId")).thenReturn("1");
        when(request.params(":feedId")).thenReturn("2");


        String jsonResponse = (String) getfeedItemTagsRoute.handle(request, response);
        String expectedJson = "[{\"text\": \"tag1\", \"weight\": 10},{\"text\": \"tag2\", \"weight\": 4},{\"text\": \"tag3\", \"weight\": 3}]";
        assertEquals(expectedJson , jsonResponse);
    }


    private FeedItemProvider createFeedProviderMock() {
        LinkedHashMap<String, Integer> tags = new LinkedHashMap<String, Integer>();
        tags.put("tag1", 10);
        tags.put("tag2", 4);
        tags.put("tag3", 3);

        FeedItemProvider feedProvider = mock(FeedItemProvider.class);
        when(feedProvider.getTags(Mockito.any(String.class))).thenReturn(tags);
        return feedProvider;
    }

}
