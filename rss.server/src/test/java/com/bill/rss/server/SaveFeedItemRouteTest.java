package com.bill.rss.server;

import org.junit.Test;
import org.mockito.Mockito;

import spark.HaltException;
import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedItemUpdater;
import com.bill.rss.domain.FeedItem;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;

import static org.mockito.Mockito.when;

public class SaveFeedItemRouteTest {


    public void saveFeedItem() {
        Request request = createRequestMock("logged-in");
        addFeedItemToRequestMock(request);
        Response response = createResponseMock();

        FeedItemUpdater feedItemUpdater = Mockito.mock(FeedItemUpdater.class);
        SaveFeedItemRoute saveFeedItemRoute = new SaveFeedItemRoute("");
        saveFeedItemRoute.handle(request, response);
        Mockito.verify(feedItemUpdater).saveFeedItem (Mockito.any(FeedItem.class));
    }


    @Test(expected = HaltException.class)
    public void saveFeedItemInvalidRequest() {
        Request request = createRequestMock("logged-in");
        addInvalidBodyToRequestMock(request);
        Response response = createResponseMock();

        SaveFeedItemRoute saveFeedItemRoute = new SaveFeedItemRoute("");
        saveFeedItemRoute.handle(request, response);
    }


    private void addFeedItemToRequestMock(Request request) {
        String userBody = "{\"feedItemId\":\"123\",\"username\":\"bob\",\"catId\":\"News\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addInvalidBodyToRequestMock(Request request) {
        String userBody = "{\"asdasd\":\"123\",\"asd\":\"bob\",\"asd\":\"News\"}";
        when(request.body()).thenReturn(userBody);
    }
}
