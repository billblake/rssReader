package com.bill.rss.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import spark.Request;
import spark.Response;
import spark.Session;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.domain.Feed;
import com.bill.rss.domain.FeedItem;
import com.bill.rss.domain.User;
import com.bill.rss.mongodb.UserRetriever;
import com.bill.rss.mongodb.FeedItem.FeedItemRetriever;

import static com.bill.rss.server.ViewConstants.LOGGED_IN_COOKIE_NAME;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockUtils {

    public static Request createRequestMock(String loggedInCookieValue) {
        Request request = mock(Request.class);
        Map<String, String> cookies = new HashMap<String, String>();
        cookies.put(LOGGED_IN_COOKIE_NAME, loggedInCookieValue);
        when(request.cookies()).thenReturn(cookies);

        Session session = mock(Session.class);
        User user = createUserMock();
        when(session.attribute("user")).thenReturn(user);
        when(request.session()).thenReturn(session);
        return request;
    }

    public static Response createResponseMock() {
        Response response = mock(Response.class);
        HttpServletResponse mockHttpRequest = mock(HttpServletResponse.class);
        when(response.raw()).thenReturn(mockHttpRequest);
        return response;
    }

    public static CategoryProvider createCategoryProviderMock() {
        CategoryProvider categoryProvider = mock(CategoryProvider.class);
        List<Category> categories = new ArrayList<Category>();
        Category category = createCategoryMock();
        category.addFeed(createFeedMock());
        categories.add(category);
        when(categoryProvider.retrieveCategories(any(String.class))).thenReturn(categories);
        return categoryProvider;
    }

    public static Category createCategoryMock() {
        Category category = new Category();
        category.setCategoryId("1");
        category.setName("Sport");
        category.setUsername("myusername");
        category.setTotalCount("5");
        category.setUnReadCount("2");
        return category;
    }

    public static Feed createFeedMock() {
        Feed feed = new Feed();
        feed.setCategoryId("1");
        feed.setFeedId("2");
        feed.setName("BBC");
        feed.setUrl("http://www.bbc.co.uk");
        feed.setUserName("myusername");
        feed.setTotalCount("5");
        feed.setUnReadCount("2");
        return feed;
    }

    public static FeedItem createFeedItemMock(String categoryId, String description, String feedId, String feedItemId, String link,
            Date pubDate, String source, String title, String username) {
        FeedItem feedItem = new FeedItem();
        feedItem.setCatId(categoryId);
        feedItem.setDescription(description);
        feedItem.setFeedId(feedId);
        feedItem.setFeedItemId(feedItemId);
        feedItem.setLink(link);
        feedItem.setPubDate(pubDate);
        feedItem.setSource(source);
        feedItem.setTitle(title);
        feedItem.setUsername(username);
        feedItem.setFormattedDate("Jul 28");
        return feedItem;
    }

    public static User createUserMock() {
        User user = new User();
        user.setFirstName("Bill");
        user.setLastName("Blake");
        user.setPassword("sdgh4e34wtgdfh");
        user.setUserName("billblake");
        return user;
    }


    public static UserProvider createUserProviderMock() {
        UserProvider userProvider = mock(UserRetriever.class);
        User user = createUserMock();
        when(userProvider.validateUser(any(User.class))).thenReturn(user);
        when(userProvider.checkIfUserNameExists(any(String.class))).thenReturn(false);
        return userProvider;
    }

    public static FeedItemRetriever createFeedItemRetrieverMock() {
        FeedItemRetriever feedItemRetriever = mock(FeedItemRetriever.class);


        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Category category = (Category) args[0];
                category.setUnReadCount("2");
                category.setTotalCount("5");
                return null;
            }})
        .when(feedItemRetriever).enrichCategoryWithFeedItemCount(any(Category.class));

        Mockito.doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                Feed feed = (Feed) args[0];
                feed.setUnReadCount("2");
                feed.setTotalCount("5");
                return null;
            }})
        .when(feedItemRetriever).enrichFeedWithFeedItemCount((any(Feed.class)));



        return feedItemRetriever;
    }
}
