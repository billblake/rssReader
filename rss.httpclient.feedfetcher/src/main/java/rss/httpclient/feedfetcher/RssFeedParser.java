package rss.httpclient.feedfetcher;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bill.rss.domain.FeedItem;

public class RssFeedParser {

	private static final String RSS_LINK = "link";
	private static final String RSS_PUB_DATE = "pubDate";
	private static final String RSS_DESCRIPTION = "description";
	private static final String RSS_TITLE = "title";
	private static final String RSS_ITEM = "item";
    private static final String RSS_ENCLOSURE = "enclosure";


	public List<FeedItem> parse(InputStream inputStream) {
		List<FeedItem> feedItems = new ArrayList<FeedItem>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputStream);
			doc.getDocumentElement().normalize();

			NodeList feedItemNodes = doc.getElementsByTagName(RSS_ITEM);
			for (int i = 0; i < feedItemNodes.getLength(); i++) {
				Element feedItemNode = (Element) feedItemNodes.item(i);
				feedItems.add(parseFeedItem(feedItemNode));
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return feedItems;
	}

	private FeedItem parseFeedItem(Element feedItemNode) {
		FeedItem feedItem = new FeedItem();
		feedItem.setTitle(getTagValue(feedItemNode, RSS_TITLE));
		feedItem.setDescription(getTagValue(feedItemNode, RSS_DESCRIPTION));
		feedItem.setImageUrl(getImage(feedItemNode));
		String pubDateStr = getTagValue(feedItemNode, RSS_PUB_DATE);
		Date pubDate = parseDate(pubDateStr);
		Date now = new Date();
		if (pubDate.after(now)) {
		    feedItem.setPubDate(now);
		} else {
		    feedItem.setPubDate(pubDate);
		}
		feedItem.setLink(getTagValue(feedItemNode, RSS_LINK));
		return feedItem;
	}

	private Date parseDate(String pubDateStr) {
		Date pubDate = new Date();
		SimpleDateFormat dateFormater = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		try {
			pubDate = dateFormater.parse(pubDateStr);
		} catch (ParseException e) {
			dateFormater.applyPattern("");
		}
		return pubDate;
	}

	private String getTagValue(Element feedItemNode, String childTag) {
		if (feedItemNode.getElementsByTagName(childTag) != null && feedItemNode.getElementsByTagName(childTag).item(0) != null) {
		    return feedItemNode.getElementsByTagName(childTag).item(0).getTextContent();
		} else {
		    return "";
		}
	}


    private String getImage(Element feedItemNode) {
        if (feedItemNode.getElementsByTagName(RSS_ENCLOSURE).item(0) != null) {
            return getAttributeOfTag(feedItemNode, RSS_ENCLOSURE, "url");
        } else if (feedItemNode.getElementsByTagName("media:thumbnail").item(0) != null) {
            return getAttributeOfTag(feedItemNode, "media:thumbnail", "url");
        }
        return "";
    }

    private String getAttributeOfTag(Element feedItemNode, String tag, String attribute) {
        Node node = feedItemNode.getElementsByTagName(tag).item(0);
        if (node != null) {
            Node attributeNode = node.getAttributes().getNamedItem(attribute);
            if (attributeNode != null) {
                if (attributeNode.getTextContent() != null) {
                    return attributeNode.getTextContent();
                }
            }
        }
        return "";
    }
}
