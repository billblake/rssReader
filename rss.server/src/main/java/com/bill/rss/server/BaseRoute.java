package com.bill.rss.server;

import static com.bill.rss.server.ViewConstants.LOGGED_IN_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.USER_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.USER_SESSION_KEY;

import java.util.Map;

import spark.Request;
import spark.Response;
import spark.Route;

import com.bill.rss.domain.User;

public abstract class BaseRoute extends Route {

    protected BaseRoute(String path) {
        super(path);
    }

    public void verifyUserLoggedIn(Request request, Response response) {
        Map<String, String> cookies =  request.cookies();
        String loggedInCookieValue = cookies.get(LOGGED_IN_COOKIE_NAME);

        User user = (User) request.session().attribute(USER_SESSION_KEY);
        if (loggedInCookieValue == null || !loggedInCookieValue.equals(ViewConstants.LOGGED_IN_COOKIE_VALUE) || user == null) {
            logout(request, response);
            halt(401, "User not authenticated");
        }
    }

    protected void logout(Request request, Response response) {
        response.removeCookie(LOGGED_IN_COOKIE_NAME);
        response.removeCookie(USER_COOKIE_NAME);
        request.session().removeAttribute(USER_SESSION_KEY);
    }
}