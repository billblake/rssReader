package rss.httpclient.feedfetcher;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Test;

import com.bill.rss.domain.FeedItem;

import static org.junit.Assert.assertEquals;

public class RssFeedParserTest {

    @Test
    public void parse() throws FileNotFoundException, URISyntaxException {
        RssFeedParser rssFeedParser = new RssFeedParser();
        InputStream inputStream = getClass().getResourceAsStream("/rss.xml");
        List<FeedItem> feedItems = rssFeedParser.parse(inputStream );
        assertEquals(80, feedItems.size());
        assertEquals("Burnley 1-3 Chelsea", feedItems.get(0).getTitle());
        assertEquals("http://www.bbc.co.uk/sport/0/football/28735901", feedItems.get(0).getLink());
    }
}
