package com.bill.rss.server;

import org.junit.Test;

import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Session;
import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseRouteTest {

    class TestRoute extends BaseRoute {

        protected TestRoute(String path) {
            super(path);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Object handle(Request request, Response response) {
            // TODO Auto-generated method stub
            return null;
        }
    }


    @Test
    public void testVerifyUserLoggedIn() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();

        TestRoute baseRoute = new TestRoute("/test");
        baseRoute.verifyUserLoggedIn(request, response);
    }


    @Test(expected = HaltException.class)
    public void testVerifyUserLoggedOut() {
        Request request = createRequestMock("logged-out");
        Response response = createResponseMock();

        TestRoute baseRoute = new TestRoute("/test");
        baseRoute.verifyUserLoggedIn(request, response);
    }


    @Test(expected = HaltException.class)
    public void testVerifyUserNotLoggedIn() {
        Request request = createRequestMock(null);
        Response response = createResponseMock();

        TestRoute baseRoute = new TestRoute("/test");
        baseRoute.verifyUserLoggedIn(request, response);
    }


    @Test(expected = HaltException.class)
    public void testVerifyUserNotLoggedInMissingFromSession() {
        Request request = createRequestMock("logged-in");
        Session session = mock(Session.class);
        when(session.attribute("user")).thenReturn(null);
        when(request.session()).thenReturn(session);

        Response response = createResponseMock();

        TestRoute baseRoute = new TestRoute("/test");
        baseRoute.verifyUserLoggedIn(request, response);
    }


    @Test
    public void testLogout() {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();

        TestRoute baseRoute = new TestRoute("/test");
        baseRoute.logout(request, response);
    }


    @Test
    public void testUsername() {
        Request request = createRequestMock("logged-in");
        createResponseMock();

        TestRoute baseRoute = new TestRoute("/test");
        String username = baseRoute.getUsername(request);
        assertEquals("billblake", username);
    }
}
