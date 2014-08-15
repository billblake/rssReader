package com.bill.rss.server;

import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import com.bill.rss.domain.User;

import static com.bill.rss.server.ViewConstants.LOGGED_IN_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.LOGGED_IN_COOKIE_VALUE;
import static com.bill.rss.server.ViewConstants.USER_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.USER_SESSION_KEY;

public abstract class BaseRoute extends Route {

    protected BaseRoute(String path) {
        super(path);
    }

    protected void verifyUserLoggedIn(Request request, Response response) {
        Map<String, String> cookies =  request.cookies();
        String loggedInCookieValue = cookies.get(LOGGED_IN_COOKIE_NAME);

        User user = (User) request.session().attribute(USER_SESSION_KEY);
        if (loggedInCookieValue == null || !loggedInCookieValue.equals(LOGGED_IN_COOKIE_VALUE) || user == null) {
            logout(request, response);
            halt(401, "User not authenticated");
        }
    }

    protected void logout(Request request, Response response) {
        response.removeCookie(LOGGED_IN_COOKIE_NAME);
        response.removeCookie(USER_COOKIE_NAME);
        request.session().removeAttribute(USER_SESSION_KEY);
    }


    protected String getUsername(Request request) {
        User user = (User) request.session().attribute(USER_SESSION_KEY);
        return user.getUserName();
    }
}
