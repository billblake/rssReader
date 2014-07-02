package rss.httpclient.feedfetcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import rss.feedfetcher.FeedFetcher;

import com.bill.rss.domain.FeedItem;

public class HttpClientFeedFetcher implements FeedFetcher {
	
	private static final int OK_RESPONSE_TYPE = 200;
	private String ACEEPT_HEADER_NAME = "accept";
	private String REQUEST_TYPE_JSON = "application/json";
	
	private RssFeedParser rssFeedParser = new RssFeedParser();

	public List<FeedItem> fetcherFeed(String feedUrl) {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(feedUrl);
			request.addHeader(ACEEPT_HEADER_NAME, REQUEST_TYPE_JSON);

			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() != OK_RESPONSE_TYPE) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			InputStream inputStream = response.getEntity().getContent();
			feedItems = rssFeedParser.parse(inputStream);

			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return feedItems;
	}

}
