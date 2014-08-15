package com.bill.rss.server;

import org.junit.Test;

import spark.Request;
import spark.Response;
import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.junit.Assert.assertEquals;

public class LogoutRouteTest {


    @Test
    public void testLogout() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();

        LogoutRoute logoutRoute = new LogoutRoute("/logout");
        String jsonResponse = (String) logoutRoute.handle(request, response);
        assertEquals("{}", jsonResponse);
    }
}
