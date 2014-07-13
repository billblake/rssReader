package com.bill.rss.server;

import spark.Request;
import spark.Route;

import com.bill.rss.domain.User;

public abstract class BaseRoute extends Route {

    protected BaseRoute(String path) {
        super(path);
    }
    
    public void verifyUserLoggedIn(Request request) {
        String loggedInCookieValue = request.cookie("loggedIn");
        User user = (User) request.session().attribute("user");
        if (loggedInCookieValue == null || !loggedInCookieValue.equals("true") || user == null) {
            halt(401, "User not authenticated");    
        }
    }
}
