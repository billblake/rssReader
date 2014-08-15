package com.bill.rss.server;

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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class UserRegistrationRouteTest {

    @Test
    public void testSignUp() {
        Request request = createRequestMock("logged-in");
        addBodyToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();

        UserRegistrationRoute userRegistrationRoute = new UserRegistrationRoute("/signUP");
        userRegistrationRoute.setUserProvider(userProvider);
        String jsonResponse = (String) userRegistrationRoute.handle(request, response);
        assertEquals("{\"userName\":\"billblake01@yahoo.ie\",\"firstName\":\"Bill\",\"lastName\":\"Blake\",\"password\":\"\"}", jsonResponse);
    }


    @Test(expected = HaltException.class)
    public void testSignUpInvalidRequest() {
        Request request = createRequestMock("logged-in");
        addInvalidBodyToRequestMock(request);
        Response response = createResponseMock();

        UserRegistrationRoute userRegistrationRoute = new UserRegistrationRoute("/signUp");
        userRegistrationRoute.handle(request, response);
    }


    @Test(expected = HaltException.class)
    public void testSignUpUserExists() {
        Request request = createRequestMock("logged-in");
        addBodyToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();
        when(userProvider.checkIfUserNameExists(any(String.class))).thenReturn(true);

        UserRegistrationRoute userRegistrationRoute = new UserRegistrationRoute("/signUP");
        userRegistrationRoute.setUserProvider(userProvider);
        userRegistrationRoute.handle(request, response);
    }


    @Test(expected = HaltException.class)
    public void testSignUpCreateUserError() {
        Request request = createRequestMock("logged-in");
        addBodyToRequestMock(request);
        Response response = createResponseMock();
        UserProvider userProvider = MockUtils.createUserProviderMock();
        doThrow(new RuntimeException()).when(userProvider).createNewUser(any(User.class));


        UserRegistrationRoute userRegistrationRoute = new UserRegistrationRoute("/signUP");
        userRegistrationRoute.setUserProvider(userProvider);
        userRegistrationRoute.handle(request, response);
    }


    private void addBodyToRequestMock(Request request) {
        String userBody = "{\"userName\":\"billblake01@yahoo.ie\",\"password\":\"password\",\"firstName\": \"Bill\",\"lastName\": \"Blake\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addInvalidBodyToRequestMock(Request request) {
        String userBody = "{\"userName\":\"billblake01@yahoo.ie\",\"password\":\"password\",\"firstName\": \"Bill\",\"lastName\":: \"Blake\"}";
        when(request.body()).thenReturn(userBody);
    }
}
