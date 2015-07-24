package com.bill.rss.server;

import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.UserProvider;
import com.bill.rss.domain.User;
import com.bill.rss.mongodb.UserRetriever;

import static com.bill.rss.server.SecurityUtils.clearPassword;
import static com.bill.rss.server.ViewConstants.LOGGED_IN_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.LOGGED_IN_COOKIE_VALUE;
import static com.bill.rss.server.ViewConstants.USERNAME_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.USER_COOKIE_NAME;
import static com.bill.rss.server.ViewConstants.USER_SESSION_KEY;

public class LoginRoute extends BaseRoute {

    private final ObjectMapper mapper = new ObjectMapper();

    private UserProvider userProvider = new UserRetriever();

    protected LoginRoute(String path) {
        super(path);
    }

    @Override
    public Object handle(Request request, Response response) {
        User user = extractUserDetailsFromRequest(request);
        validateUserInput(user);
        encryptPassword(user);
        user = validateUser(user);
        clearPassword(user);
        createCookies(response, user);
        addUserDetailsToSession(request, user);
        return JsonUtils.convertObjectToJson(user);
    }


    private User extractUserDetailsFromRequest(Request request) {
        User user = null;
        try {
            user = mapper.readValue(request.body(), User.class);
        } catch (Exception e) {
            halt(401, "Invalid Input");
        }
        return user;
    }


    private void validateUserInput(User user) {
        if (StringUtils.isBlank(user.getUserName()) || StringUtils.isBlank(user.getPassword())) {
            halt(401, "Missing username/password");
        }
    }


    private void encryptPassword(User user) {
        user.setPassword(SecurityUtils.encrypt(user.getPassword()));
    }


    private User validateUser(User user) {
        try {
            user = userProvider.validateUser(user);
        } catch (Exception e) {
            halt(401, "Invalid username/password " + e.getMessage());
        }
        return user;
    }


    private void createCookies(Response response, User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        addCookie(response, USER_COOKIE_NAME, fullName);
        addCookie(response, LOGGED_IN_COOKIE_NAME, LOGGED_IN_COOKIE_VALUE);
        addCookie(response, USERNAME_COOKIE_NAME, user.getUserName());
    }

    private void addCookie(Response response, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(10800);
        response.raw().addCookie(cookie);
    }


    private void addUserDetailsToSession(Request request, User user) {
        request.session().attribute(USER_SESSION_KEY, user);
    }


    public void setUserProvider(UserProvider userProvider) {
        this.userProvider = userProvider;
    }
}
