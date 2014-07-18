package com.bill.rss.server;

import spark.Request;
import spark.Response;

public class LogoutRoute extends BaseRoute {

    protected LogoutRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        logout(request, response);
        return "{}";
    }

}
