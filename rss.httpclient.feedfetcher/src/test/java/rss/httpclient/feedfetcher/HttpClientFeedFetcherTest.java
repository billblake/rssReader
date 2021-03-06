package rss.httpclient.feedfetcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.junit.Test;

import com.bill.rss.domain.FeedItem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpClientFeedFetcherTest {

    @Test
    public void fetcherFeed() throws ClientProtocolException, IOException {
        HttpClientFeedFetcher feedFetcher = new HttpClientFeedFetcher();
        feedFetcher.setRssFeedParser(setupRssFeedParser());

        List<FeedItem> fetcherFeeds = feedFetcher.fetchFeed("http://www.bbc.co.uk/sport", setupHttpClient());
        assertEquals(1, fetcherFeeds.size());
        assertEquals("myTitle", fetcherFeeds.get(0).getTitle());
    }


    @Test(expected = RuntimeException.class)
    public void fetcherFeedErrorResponse() throws ClientProtocolException, IOException {
        HttpClientFeedFetcher feedFetcher = new HttpClientFeedFetcher();
        feedFetcher.setRssFeedParser(setupRssFeedParser());
        HttpClient mockHttpClient = setupHttpClient();
        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(400);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(response);

        List<FeedItem> fetcherFeeds = feedFetcher.fetchFeed("http://www.bbc.co.uk/sport", mockHttpClient);
        assertEquals(1, fetcherFeeds.size());
        assertEquals("myTitle", fetcherFeeds.get(0).getTitle());
    }


    private RssFeedParser setupRssFeedParser() {
        RssFeedParser rssFeedParser = mock(RssFeedParser.class);
        List<FeedItem> feedItems = new ArrayList<FeedItem>();
        FeedItem feedItem = new FeedItem();
        feedItem.setTitle("myTitle");
        feedItems.add(feedItem);
        when(rssFeedParser.parse(any(InputStream.class))).thenReturn(feedItems );
        return rssFeedParser;
    }


    private HttpClient setupHttpClient() throws IllegalStateException, IOException {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(statusLine);
        HttpEntity entity = mock(HttpEntity.class);
        InputStream value = mock(InputStream.class);
        when(entity.getContent()).thenReturn(value );
        when(response.getEntity()).thenReturn(entity);

        ClientConnectionManager clientConnectionManager = mock(ClientConnectionManager.class);
        when(httpClient.getConnectionManager()).thenReturn(clientConnectionManager);
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);
        return httpClient;
    }
}
