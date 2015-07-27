package com.bill.rss.server;

import org.junit.Test;
import org.mockito.Mockito;

import spark.HaltException;
import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;

import static org.mockito.Mockito.when;

public class SaveFeedRouteTest {

    @Test
    public void testAddFeed() {
        Request request = createRequestMock("logged-in");
        addAddFeedBodyToRequestMock(request);
        Response response = createResponseMock();
        FeedUpdater feedUpdater = Mockito.mock(FeedUpdater.class);

        SaveFeedRoute saveFeedRoute = new SaveFeedRoute("");
        saveFeedRoute.setFeedUpdater(feedUpdater);
        saveFeedRoute.handle(request, response);
        Mockito.verify(feedUpdater, Mockito.times(1)).addFeed(Mockito.any(Feed.class));
    }


    @Test
    public void testSaveFeed() {
        Request request = createRequestMock("logged-in");
        addSaveFeedBodyToRequestMock(request);
        Response response = createResponseMock();
        FeedUpdater feedUpdater = Mockito.mock(FeedUpdater.class);

        SaveFeedRoute saveFeedRoute = new SaveFeedRoute("");
        saveFeedRoute.setFeedUpdater(feedUpdater);
        saveFeedRoute.handle(request, response);
        Mockito.verify(feedUpdater, Mockito.times(1)).saveFeed(Mockito.any(Feed.class));
    }


    @Test(expected = HaltException.class)
    public void testSaveFeedInvalidInput() {
        Request request = createRequestMock("logged-in");
        addInvalidBodyToRequestMock(request);
        Response response = createResponseMock();
        FeedUpdater feedUpdater = Mockito.mock(FeedUpdater.class);

        SaveFeedRoute saveFeedRoute = new SaveFeedRoute("");
        saveFeedRoute.setFeedUpdater(feedUpdater);
        saveFeedRoute.handle(request, response);
    }



    private void addSaveFeedBodyToRequestMock(Request request) {
        String userBody = "{\"categoryId\":\"123\",\"userName\":\"bob\",\"name\":\"News\", \"feedId\": \"12323\", \"url\":\"http://something\" }";
        when(request.body()).thenReturn(userBody);
    }


    private void addAddFeedBodyToRequestMock(Request request) {
        String userBody = "{\"userName\":\"bob\",\"name\":\"News\",\"url\":\"http://something\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addInvalidBodyToRequestMock(Request request) {
        String userBody = "{\"usersdname\":\"bob\",\"nasdme\":\"News\"}";
        when(request.body()).thenReturn(userBody);
    }
}
