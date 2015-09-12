package com.bill.rss.server.scripts;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bill.rss.dataProvider.FeedProvider;
import com.bill.rss.dataProvider.FeedUpdater;
import com.bill.rss.domain.Feed;
import com.bill.rss.mongodb.FeedRetriever;
import com.bill.rss.mongodb.MongoFeedUpdater;

public class AddImageToFeeds {


    private static final int OK_RESPONSE_TYPE = 200;
    private static final String ACEEPT_HEADER_NAME = "accept";
    private static final String REQUEST_TYPE_JSON = "application/json";


    public static void main(String args[]) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        FeedProvider feedProvider = new FeedRetriever();
        FeedUpdater feedUpdater = new MongoFeedUpdater();

        for (Feed feed : feedProvider.retrieveAllFeeds() ) {
            HttpGet request = new HttpGet(feed.getUrl());
            request.addHeader(ACEEPT_HEADER_NAME, REQUEST_TYPE_JSON);

            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != OK_RESPONSE_TYPE) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            InputStream inputStream = response.getEntity().getContent();


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            NodeList imageNode = doc.getElementsByTagName("image");

            Node imageItem = imageNode.item(0);

            if (imageItem != null) {
                NodeList childNodes = imageItem.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    if (childNodes.item(i).getNodeName().equals("url")) {
                        feed.setImageUrl(childNodes.item(i).getTextContent());
                        break;
                    }
                }
                feedUpdater.saveFeed(feed);
            }
        }

    }
}
