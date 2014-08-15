package com.bill.rss.server;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import spark.HaltException;
import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.when;

public class LoginRouteTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testLogin() throws Exception {
        Request request = createRequestMock("logged-in");
        addBodyToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();
        User userMock = MockUtils.createUserMock();

        LoginRoute loginRoute = new LoginRoute("/login");
        loginRoute.setUserProvider(userProvider);

        String userJsonResponse = (String) loginRoute.handle(request, response);

        User responseUser = mapper.readValue(userJsonResponse, User.class);
        assertEquals(userMock.getFirstName(), responseUser.getFirstName());
        assertEquals(userMock.getLastName(), responseUser.getLastName());
        assertEquals(userMock.getUserName(), responseUser.getUserName());
        assertEquals("", responseUser.getPassword());
    }


    @Test(expected = HaltException.class)
    public void testLoginInvalidRequest() throws Exception {
        Request request = createRequestMock("logged-in");
        addInvalidBodyToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();

        LoginRoute loginRoute = new LoginRoute("/login");
        loginRoute.setUserProvider(userProvider);
        loginRoute.handle(request, response);
    }


    @Test(expected = HaltException.class)
    public void testLoginMissingUserName() throws Exception {
        Request request = createRequestMock("logged-in");
        addBodyMissingUsernameToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();

        LoginRoute loginRoute = new LoginRoute("/login");
        loginRoute.setUserProvider(userProvider);
        loginRoute.handle(request, response);
    }


    @Test(expected = HaltException.class)
    public void testLoginMissingPassword() throws Exception {
        Request request = createRequestMock("logged-in");
        addBodyMissingPasswordToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();

        LoginRoute loginRoute = new LoginRoute("/login");
        loginRoute.setUserProvider(userProvider);
        loginRoute.handle(request, response);
    }


    @SuppressWarnings("unchecked")
    @Test(expected = HaltException.class)
    public void testLoginInvalidUser() throws Exception {
        Request request = createRequestMock("logged-in");
        addBodyToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();
        when(userProvider.validateUser(any(User.class))).thenThrow(HaltException.class);

        LoginRoute loginRoute = new LoginRoute("/login");
        loginRoute.setUserProvider(userProvider);
        loginRoute.handle(request, response);
    }


    private void addBodyToRequestMock(Request request) {
        String userBody = "{\"userName\":\"billblake01@yahoo.ie\",\"password\":\"password\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addInvalidBodyToRequestMock(Request request) {
        String userBody = "{\"userName\"{}:\"billblake01@yahoo.ie\",\"password\":\"password\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addBodyMissingUsernameToRequestMock(Request request) {
        String userBody = "{\"password\":\"password\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addBodyMissingPasswordToRequestMock(Request request) {
        String userBody = "{\"userName\":\"billblake01@yahoo.ie\"}";
        when(request.body()).thenReturn(userBody);
    }
}
